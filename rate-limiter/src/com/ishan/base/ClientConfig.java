package com.ishan.base;

/**
 * This holds a client's config for throttling rate limits
 *
 * @author ishanjain
 * @since 21/03/18
 */
public class ClientConfig {

    /**
     * The id of the client, assumed to be unique
     */
    private String clientId;

    /**
     * Holder of the rate limits configured for the client
     */
    private RateLimits rateLimits;

    private Map<HTTPMetho>

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public RateLimits getRateLimits() {
        return rateLimits;
    }

    public void setRateLimits(RateLimits rateLimits) {
        this.rateLimits = rateLimits;
    }

    /**
     * This holds the number of requests a client can make within certain time frames.
     * <b>Note</b> that the parameters are optional. So there might not exist a monthly limit.
     */
    public static class RateLimits {
        /**
         * Number of requests in a second
         */
        private Integer secondLimit;
        /**
         * Number of requests in a minute
         */
        private Integer minuteLimit;
        /**
         * Number of requests in an hour
         */
        private Integer hourLimit;
        /**
         * Number of requests in a week
         */
        private Integer weekLimit;
        /**
         * Number of requests in a month
         */
        private Integer monthLimit;

        public Integer getSecondLimit() {
            return secondLimit;
        }

        public void setSecondLimit(Integer secondLimit) {
            this.secondLimit = secondLimit;
        }

        public Integer getMinuteLimit() {
            return minuteLimit;
        }

        public void setMinuteLimit(Integer minuteLimit) {
            this.minuteLimit = minuteLimit;
        }

        public Integer getHourLimit() {
            return hourLimit;
        }

        public void setHourLimit(Integer hourLimit) {
            this.hourLimit = hourLimit;
        }

        public Integer getWeekLimit() {
            return weekLimit;
        }

        public void setWeekLimit(Integer weekLimit) {
            this.weekLimit = weekLimit;
        }

        public Integer getMonthLimit() {
            return monthLimit;
        }

        public void setMonthLimit(Integer monthLimit) {
            this.monthLimit = monthLimit;
        }
    }

    @Override
    public String toString() {
        return "ClientConfig{" + "clientId='" + clientId + '\'' + ", rateLimits=" + rateLimits + '}';
    }
}