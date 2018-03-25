package com.ishan.base;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.ishan.redis.RedisService;
import org.apache.commons.collections4.MapUtils;

import java.util.List;
import java.util.Map;
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
        List<RedisKeyDetails> redisKeys = constructRedisKeys(clientConfig, requestDetails);
        List<Object> pipeline = RedisService.pipeline(redisKeys);
        return validateRateLimited(pipeline, clientConfig, redisKeys);
    }

    /**
     * This actually checks if the corresponding values received from redis.
     *
     * This firstly checks if the limit is exceeded for a client.
     * It then checks for endpoint.
     * It then checks for method.
     *
     * @param pipeline     The response from Redis pipeline
     * @param clientConfig The {@link ClientConfig} config for the client
     * @return The {@link RateLimitResponse} response for rate limits
     */
    private static RateLimitResponse validateRateLimited(List<Object> pipeline, ClientConfig clientConfig,
                                                         List<RedisKeyDetails> redisKeyWithTTLs) {
        int index = 0;
        for (Object currentLimitObj : pipeline) {
            long currentLimit = (long) currentLimitObj;
            RedisKeyDetails redisKeyDetails = redisKeyWithTTLs.get(index++);
            RateLimitPeriod period = redisKeyDetails.getPeriod();
            if (redisKeyDetails.getEndpoint() == null && redisKeyDetails.getHttpMethod() == null) {
                //This is client specific limits
                ClientConfig.RateLimits rateLimits = clientConfig.getRateLimits();
                Map<RateLimitPeriod, Integer> periodLimits = rateLimits.getPeriodLimits();
                Integer limit = periodLimits.get(period);
                if (currentLimit > limit) {
                    return RateLimitResponse.withRateLimitReached(period, RateLimitViolationCause.CLIENT);
                }
            } else if (redisKeyDetails.getHttpMethod() != null) {
                //This is http method specific stuff
                ClientConfig.RateLimits rateLimits = clientConfig.getMethodVsLimits()
                        .get(HttpMethod.valueOf(redisKeyDetails.getHttpMethod()));
                Integer limit = rateLimits.getPeriodLimits().get(period);
                if (currentLimit > limit) {
                    return RateLimitResponse.withRateLimitReached(period, RateLimitViolationCause.METHOD);
                }
            } else if (redisKeyDetails.getEndpoint() != null) {
                //This is endpoint specific stuff
                ClientConfig.RateLimits rateLimits = clientConfig.getEndpointVsLimits()
                        .get(redisKeyDetails.getEndpoint());
                Integer limit = rateLimits.getPeriodLimits().get(period);
                if (currentLimit > limit) {
                    return RateLimitResponse.withRateLimitReached(period, RateLimitViolationCause.ENDPOINT);
                }
            }
        }
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
    private static List<RedisKeyDetails> constructRedisKeys(ClientConfig clientConfig, RequestDetails requestDetails) {
        List<RedisKeyDetails> keys = Lists.newArrayList();
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

    private static Set<RedisKeyDetails> constructRedisKeys(String endpoint, HttpMethod method,
                                                           ClientConfig.RateLimits rateLimits,
                                                           ClientConfig clientConfig, RequestDetails requestDetails) {
        Set<RedisKeyDetails> rv = Sets.newHashSet();
        for (RateLimitPeriod rateLimitPeriod : rateLimits.getPeriodLimits().keySet()) {
            long nextSlot = rateLimitPeriod.wrapNext(requestDetails.getRequestTime());
            long ttl = nextSlot - requestDetails.getRequestTime();
            RedisKeyDetails redisKeyWithTTL = new RedisKeyDetails(ttl);
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
}