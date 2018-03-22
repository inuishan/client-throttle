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

        private Map<RateLimitPeriod, Integer> periodLimits;

        public Map<RateLimitPeriod, Integer> getPeriodLimits() {
            return periodLimits;
        }

        public void setPeriodLimits(Map<RateLimitPeriod, Integer> periodLimits) {
            this.periodLimits = periodLimits;
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