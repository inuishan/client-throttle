package com.ishan.base;

/**
 * @author ishanjain
 * @since 22/03/18
 */
public enum RateLimitPeriod {

    SECOND, MINUTE, HOUR, WEEK, MONTH;

    /**
     * This wraps the time to the nearest floored value, 1.2 seconds will be 1
     *
     * @param time The time to wrap
     * @return The wrapped time
     */
    public long wrap(long time) {
        new DateTime()
    }
}