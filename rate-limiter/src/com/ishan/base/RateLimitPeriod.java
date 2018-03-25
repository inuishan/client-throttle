package com.ishan.base;

import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;

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
            default:
                throw new IllegalArgumentException("This period is not yet supported");
        }
    }

    /**
     * This wraps to the next slot. This is used to calculate the ttl of the keys
     *
     * @param time The time to wrap
     * @return The next slot wrapped milliseconds
     */
    public long wrapNext(long time) {
        DateTime dateTime = new DateTime(time);
        switch (this) {
            case SECOND:
                return dateTime.plus(DateTimeConstants.MILLIS_PER_SECOND).withMillisOfSecond(0).getMillis();
            case MINUTE:
                return dateTime.plus(DateTimeConstants.MILLIS_PER_MINUTE).withSecondOfMinute(0).withMillisOfSecond(0)
                        .getMillis();
            case HOUR:
                return dateTime.plus(DateTimeConstants.MILLIS_PER_HOUR).withMinuteOfHour(0).withSecondOfMinute(0)
                        .withMillisOfSecond(0).getMillis();
            case DAY:
                return dateTime.plus(DateTimeConstants.MILLIS_PER_DAY).millisOfDay().withMinimumValue().getMillis();
            case WEEK:
                return dateTime.plus(DateTimeConstants.MILLIS_PER_WEEK).dayOfWeek().withMinimumValue().millisOfDay()
                        .withMinimumValue().getMillis();
            case MONTH:
                return dateTime.plus(30L * DateTimeConstants.MILLIS_PER_DAY).dayOfMonth().withMinimumValue()
                        .millisOfDay().withMinimumValue().getMillis();
            default:
                throw new IllegalArgumentException("This period is not yet supported");
        }
    }
}