package com.ishan.base;

/**
 * Stores Redis Keys details with ttl
 *
 * @author ishanjain
 * @since 25/03/18
 */
public class RedisKeyDetails {

    private String key;

    private final long ttl;

    private String endpoint;

    private String httpMethod;

    private String clientId;

    private RateLimitPeriod period;

    private RedisKeyDetails(long ttl) {
        this.ttl = ttl;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getHttpMethod() {
        return httpMethod;
    }

    public void setHttpMethod(String httpMethod) {
        this.httpMethod = httpMethod;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getKey() {
        return key;
    }

    public long getTtl() {
        return ttl;
    }

    public RateLimitPeriod getPeriod() {
        return period;
    }

    public void setPeriod(RateLimitPeriod period) {
        this.period = period;
    }

    public void generateRedisKey(long requestTime) {
        StringBuilder keybuilder = new StringBuilder(clientId);
        if (endpoint != null) {
            keybuilder.append("_").append(endpoint);
        }
        if (httpMethod != null) {
            keybuilder.append("_").append(httpMethod);
        }
        keybuilder.append("_").append(period);
        keybuilder.append("_").append(requestTime);
        this.key = keybuilder.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RedisKeyDetails that = (RedisKeyDetails) o;

        if (endpoint != null ? !endpoint.equals(that.endpoint) : that.endpoint != null) return false;
        if (httpMethod != null ? !httpMethod.equals(that.httpMethod) : that.httpMethod != null) return false;
        if (clientId != null ? !clientId.equals(that.clientId) : that.clientId != null) return false;
        return period == that.period;
    }

    @Override
    public int hashCode() {
        int result = endpoint != null ? endpoint.hashCode() : 0;
        result = 31 * result + (httpMethod != null ? httpMethod.hashCode() : 0);
        result = 31 * result + (clientId != null ? clientId.hashCode() : 0);
        result = 31 * result + (period != null ? period.hashCode() : 0);
        return result;
    }
}