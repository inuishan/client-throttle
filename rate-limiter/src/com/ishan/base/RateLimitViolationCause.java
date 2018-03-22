package com.ishan.base;

/**
 * @author ishanjain
 * @since 22/03/18
 */
public enum RateLimitViolationCause {
    /**
     * Did it break the client's limits
     */
    CLIENT,
    /**
     * Did it break the endpoint's limits
     */
    ENDPOINT,
    /**
     * Did it break a particular method's limits
     */
    METHOD
}