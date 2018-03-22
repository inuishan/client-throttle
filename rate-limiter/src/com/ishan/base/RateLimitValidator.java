package com.ishan.base;

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
     * Holder class for the response
     */
    public static class RateLimitResponse {

        private boolean rateLimitReached;


    }
}