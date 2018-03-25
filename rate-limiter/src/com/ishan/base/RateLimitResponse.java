package com.ishan.base;

/**
 * Holder class for the response
 *
 * @author ishanjain
 * @since 25/03/18
 */
public class RateLimitResponse {
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