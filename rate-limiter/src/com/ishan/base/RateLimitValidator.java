package com.ishan.base;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.ishan.redis.RedisService;
import org.apache.commons.collections4.MapUtils;

import java.util.List;
import java.util.Set;

/**
 * The main class which contains the logic of rate limiting. The basic idea is:
 * <p>
 * There's a circular buffer for each type of limits. So there will be 60 such slots for seconds limit, 60 for
 * minutes and 12 for months.
 * <p>
 * Using redis pipeline to save RTT, we increment the usage of that slot by 1 and get back the result. We also do a
 * expire command to that key, with the time remaining for that slot.
 * <p>
 * If the limits have exceeded, then we say that limits have exceeded.
 *
 * @author ishanjain
 * @since 21/03/18
 */
public class RateLimitValidator {


    /**
     * This validates whether the client is within rate limits or not
     *
     * @return The {@link RateLimitResponse} containing the status of rate limit
     */
    public static RateLimitResponse validateRateLimited(ClientConfig clientConfig, RequestDetails requestDetails) {
        List<RedisKeyWithTTL> redisKeys = constructRedisKeys(clientConfig, requestDetails);
        List<Object> pipeline = RedisService.pipeline(redisKeys);
        return validateRateLimited(pipeline, clientConfig, redisKeys);
    }

    /**
     * This actually checks if the corresponding values received from
     *
     * @param pipeline     The response from Redis pipeline
     * @param clientConfig The {@link ClientConfig} config for the client
     * @return The {@link RateLimitResponse} response for rate limits
     */
    private static RateLimitResponse validateRateLimited(List<Object> pipeline, ClientConfig clientConfig,
                                                         List<RedisKeyWithTTL> redisKeyWithTTLs) {
        return RateLimitResponse.withRateLimitNotReached();
    }

    /**
     * This constructs the redis keys for this request. If the client is configured to have the endpoint requests,
     * it adds the keys for that endpoint as well. Similary for HTTPMethod.
     * Example:
     * keys would be (not exactly)
     * ecom_1sec
     * ecom_4min
     * ecom_getPrice_10sec
     * ecom_get_5sec
     *
     * @param clientConfig   The {@link ClientConfig} config of the client
     * @param requestDetails The {@link RequestDetails} containing details of the request
     * @return A set of constructed keys for redis
     */
    private static List<RedisKeyWithTTL> constructRedisKeys(ClientConfig clientConfig, RequestDetails requestDetails) {
        List<RedisKeyWithTTL> keys = Lists.newArrayList();
        if (clientConfig.getRateLimits() != null) {
            //This means that this client has been configured with these limits
            ClientConfig.RateLimits rateLimits = clientConfig.getRateLimits();
            keys.addAll(constructRedisKeys(null, null, rateLimits, clientConfig, requestDetails));
        }
        if (MapUtils.isNotEmpty(clientConfig.getEndpointVsLimits())) {
            String endpoint = requestDetails.getEndpoint();
            ClientConfig.RateLimits endpointLimits = clientConfig.getEndpointVsLimits().get(endpoint);
            if (endpointLimits != null) {
                // There is a limit for these endpoints
                keys.addAll(constructRedisKeys(endpoint, null, endpointLimits, clientConfig, requestDetails));
            }
        }
        if (MapUtils.isNotEmpty(clientConfig.getMethodVsLimits())) {
            HttpMethod httpMethod = requestDetails.getHttpMethod();
            ClientConfig.RateLimits rateLimits = clientConfig.getMethodVsLimits().get(httpMethod);
            if (rateLimits != null) {
                // There is a limit for these endpoints
                keys.addAll(constructRedisKeys(null, httpMethod, rateLimits, clientConfig, requestDetails));
            }
        }
        return keys;
    }

    private static Set<RedisKeyWithTTL> constructRedisKeys(String endpoint, HttpMethod method,
                                                           ClientConfig.RateLimits rateLimits,
                                                           ClientConfig clientConfig, RequestDetails requestDetails) {
        Set<RedisKeyWithTTL> rv = Sets.newHashSet();
        for (RateLimitPeriod rateLimitPeriod : rateLimits.getPeriodLimits().keySet()) {
            long nextSlot = rateLimitPeriod.wrapNext(requestDetails.getRequestTime());
            long ttl = nextSlot - requestDetails.getRequestTime();
            RedisKeyWithTTL redisKeyWithTTL = new RedisKeyWithTTL(ttl);
            if (endpoint != null) {
                redisKeyWithTTL.setEndpoint(endpoint);
            }
            if (method != null) {
                redisKeyWithTTL.setHttpMethod(method.name());
            }
            redisKeyWithTTL.setClientId(clientConfig.getClientId());
            redisKeyWithTTL.setPeriod(rateLimitPeriod);
            redisKeyWithTTL.generateRedisKey(requestDetails.getRequestTime());
            rv.add(redisKeyWithTTL);
        }
        return rv;
    }

    /**
     * Stores Redis Keys with ttl
     */
    public static class RedisKeyWithTTL {

        private String key;

        private final long ttl;

        private String endpoint;

        private String httpMethod;

        private String clientId;

        private RateLimitPeriod period;

        private RedisKeyWithTTL(long ttl) {
            this.ttl = ttl;
        }

        public String getEndpoint() {
            return endpoint;
        }

        public void setEndpoint(String endpoint) {
            this.endpoint = endpoint;
        }

        public String getHttpMethod() {
            return httpMethod;
        }

        public void setHttpMethod(String httpMethod) {
            this.httpMethod = httpMethod;
        }

        public String getClientId() {
            return clientId;
        }

        public void setClientId(String clientId) {
            this.clientId = clientId;
        }

        public String getKey() {
            return key;
        }

        public long getTtl() {
            return ttl;
        }

        public RateLimitPeriod getPeriod() {
            return period;
        }

        public void setPeriod(RateLimitPeriod period) {
            this.period = period;
        }

        public void generateRedisKey(long requestTime) {
            StringBuilder keybuilder = new StringBuilder(clientId);
            if (endpoint != null) {
                keybuilder.append("_").append(endpoint);
            }
            if (httpMethod != null) {
                keybuilder.append("_").append(httpMethod);
            }
            keybuilder.append("_").append(period);
            keybuilder.append("_").append(requestTime);
            this.key = keybuilder.toString();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            RedisKeyWithTTL that = (RedisKeyWithTTL) o;

            if (endpoint != null ? !endpoint.equals(that.endpoint) : that.endpoint != null) return false;
            if (httpMethod != null ? !httpMethod.equals(that.httpMethod) : that.httpMethod != null) return false;
            if (clientId != null ? !clientId.equals(that.clientId) : that.clientId != null) return false;
            return period == that.period;
        }

        @Override
        public int hashCode() {
            int result = endpoint != null ? endpoint.hashCode() : 0;
            result = 31 * result + (httpMethod != null ? httpMethod.hashCode() : 0);
            result = 31 * result + (clientId != null ? clientId.hashCode() : 0);
            result = 31 * result + (period != null ? period.hashCode() : 0);
            return result;
        }
    }


    /**
     * Holder class for the response
     */
    public static class RateLimitResponse {
        /**
         * Whether the rate limit was reached or not
         */
        private boolean rateLimitReached;

        /**
         * If the rate limit was reached, then which period's limit that it violated
         */
        private RateLimitPeriod rateLimitPeriod;

        /**
         * If the rate limit was reached, then which limit did the request break
         */
        private RateLimitViolationCause rateLimitViolationCause;

        //Enforcing use of static constructor
        private RateLimitResponse() {

        }

        public static RateLimitResponse withRateLimitReached(RateLimitPeriod period, RateLimitViolationCause cause) {
            RateLimitResponse rateLimitResponse = new RateLimitResponse();
            rateLimitResponse.setRateLimitReached(true);
            rateLimitResponse.setRateLimitPeriod(period);
            rateLimitResponse.setRateLimitViolationCause(cause);
            return rateLimitResponse;
        }

        public static RateLimitResponse withRateLimitNotReached() {
            RateLimitResponse rateLimitResponse = new RateLimitResponse();
            rateLimitResponse.setRateLimitReached(false);
            return rateLimitResponse;
        }

        public boolean getRateLimitReached() {
            return rateLimitReached;
        }

        public void setRateLimitReached(boolean rateLimitReached) {
            this.rateLimitReached = rateLimitReached;
        }

        public RateLimitPeriod getRateLimitPeriod() {
            return rateLimitPeriod;
        }

        public void setRateLimitPeriod(RateLimitPeriod rateLimitPeriod) {
            this.rateLimitPeriod = rateLimitPeriod;
        }

        public RateLimitViolationCause getRateLimitViolationCause() {
            return rateLimitViolationCause;
        }

        public void setRateLimitViolationCause(RateLimitViolationCause rateLimitViolationCause) {
            this.rateLimitViolationCause = rateLimitViolationCause;
        }
    }
}