package com.ishan.base;

import java.util.Map;

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

    /**
     * Http Method vs limits
     */
    private Map<HttpMethod, RateLimits> methodVsLimits;

    /**
     * Particular endpoints limits
     */
    private Map<String, RateLimits> endpointVsLimits;

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

    public Map<HttpMethod, RateLimits> getMethodVsLimits() {
        return methodVsLimits;
    }

    public void setMethodVsLimits(Map<HttpMethod, RateLimits> methodVsLimits) {
        this.methodVsLimits = methodVsLimits;
    }

    public Map<String, RateLimits> getEndpointVsLimits() {
        return endpointVsLimits;
    }

    public void setEndpointVsLimits(Map<String, RateLimits> endpointVsLimits) {
        this.endpointVsLimits = endpointVsLimits;
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
        final StringBuilder sb = new StringBuilder("ClientConfig{");
        sb.append("clientId='").append(clientId).append('\'');
        sb.append(", rateLimits=").append(rateLimits);
        sb.append(", methodVsLimits=").append(methodVsLimits);
        sb.append(", endpointVsLimits=").append(endpointVsLimits);
        sb.append('}');
        return sb.toString();
    }
}