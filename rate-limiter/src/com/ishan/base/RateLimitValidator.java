package com.ishan.base;

import com.google.common.collect.Sets;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

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
        Set<RedisKeyWithTTL> redisKeys = constructRedisKeys(clientConfig, requestDetails);
        return null;
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
    private static Set<RedisKeyWithTTL> constructRedisKeys(ClientConfig clientConfig, RequestDetails requestDetails) {
        Set<RedisKeyWithTTL> keys = Sets.newHashSet();
        if (clientConfig.getRateLimits() != null) {
            //This means that this client has been configured with these limits
            ClientConfig.RateLimits rateLimits = clientConfig.getRateLimits();
            keys.addAll(constructRedisKeys(null, rateLimits, clientConfig, requestDetails));
        }
        if (MapUtils.isNotEmpty(clientConfig.getEndpointVsLimits())) {
            String endpoint = requestDetails.getEndpoint();
            ClientConfig.RateLimits endpointLimits = clientConfig.getEndpointVsLimits().get(endpoint);
            if (endpointLimits != null) {
                // There is a limit for these endpoints
                keys.addAll(constructRedisKeys(endpoint, endpointLimits, clientConfig, requestDetails));
            }
        }
        if (MapUtils.isNotEmpty(clientConfig.getMethodVsLimits())) {
            HttpMethod httpMethod = requestDetails.getHttpMethod();
            ClientConfig.RateLimits rateLimits = clientConfig.getMethodVsLimits().get(httpMethod);
            if (rateLimits != null) {
                // There is a limit for these endpoints
                keys.addAll(constructRedisKeys(httpMethod.name(), rateLimits, clientConfig, requestDetails));
            }
        }
        return keys;
    }

    private static Set<RedisKeyWithTTL> constructRedisKeys(String prefix, ClientConfig.RateLimits rateLimits,
                                                           ClientConfig clientConfig, RequestDetails requestDetails) {
        Set<RedisKeyWithTTL> rv = Sets.newHashSet();
        for (RateLimitPeriod rateLimitPeriod : rateLimits.getPeriodLimits().keySet()) {
            StringBuilder keyBuilder = new StringBuilder(clientConfig.getClientId());
            if (StringUtils.isNotBlank(prefix)) {
                keyBuilder.append(prefix);
            }
            keyBuilder.append(rateLimitPeriod.wrap(requestDetails.getRequestTime()));
            long nextSlot = rateLimitPeriod.wrapNext(requestDetails.getRequestTime());
            long ttl = nextSlot - requestDetails.getRequestTime();
            keyBuilder.append(rateLimitPeriod);
            rv.add(new RedisKeyWithTTL(keyBuilder.toString(), ttl));
        }
        return rv;
    }

    /**
     * Stores Redis Keys with ttl
     */
    private static class RedisKeyWithTTL {

        private final String key;

        private final long ttl;

        private RedisKeyWithTTL(String key, long ttl) {
            this.key = key;
            this.ttl = ttl;
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