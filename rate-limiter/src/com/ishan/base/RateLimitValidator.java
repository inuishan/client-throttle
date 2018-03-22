package com.ishan.base;

import com.google.common.collect.Sets;

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
        Set<String> redisKeys = constructRedisKeys(clientConfig, requestDetails);
        return null;
    }

    private static Set<String> constructRedisKeys(ClientConfig clientConfig, RequestDetails requestDetails) {
        Set<String> keys = Sets.newHashSet();
        if (clientConfig.getRateLimits() != null) {
            //This means that this client has been configured with these limits
            ClientConfig.RateLimits rateLimits = clientConfig.getRateLimits();

        }
        return null;
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