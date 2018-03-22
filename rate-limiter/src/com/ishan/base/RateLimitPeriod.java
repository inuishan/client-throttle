package com.ishan.base;

import org.joda.time.DateTime;

/**
 * @author ishanjain
 * @since 22/03/18
 */
public enum RateLimitPeriod {

    SECOND, MINUTE, HOUR, DAY, WEEK, MONTH;

    /**
     * This wraps the time to the nearest floored value, 1.2 seconds will be 1
     *
     * @param time The time to wrap
     * @return The wrapped time
     */
    public long wrap(long time) {
        DateTime dateTime = new DateTime(time);
        switch (this) {
            case SECOND:
                return dateTime.withMillisOfSecond(0).getMillis();
            case MINUTE:
                return dateTime.withSecondOfMinute(0).withMillisOfSecond(0).getMillis();
            case HOUR:
                return dateTime.withMinuteOfHour(0).withSecondOfMinute(0).withMillisOfSecond(0).getMillis();
            case DAY:
                return dateTime.millisOfDay().withMinimumValue().getMillis();
            case WEEK:
                return dateTime.dayOfWeek().withMinimumValue().millisOfDay().withMinimumValue().getMillis();
            case MONTH:
                return dateTime.dayOfMonth().withMinimumValue().millisOfDay().withMinimumValue().getMillis();
        }
    }
}