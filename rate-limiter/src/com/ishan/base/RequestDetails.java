package com.ishan.base;

/**
 * A holder class which encapsulates the request details
 *
 * @author ishanjain
 * @since 22/03/18
 */
public class RequestDetails {

    private long requestTime;

    private HttpMethod httpMethod;

    private String endpoint;

    private String clientId;

    public RequestDetails(long requestTime, HttpMethod httpMethod, String endpoint, String clientId) {
        this.requestTime = requestTime;
        this.httpMethod = httpMethod;
        this.endpoint = endpoint;
        this.clientId = clientId;
    }

    public long getRequestTime() {
        return requestTime;
    }

    public void setRequestTime(long requestTime) {
        this.requestTime = requestTime;
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public void setHttpMethod(HttpMethod httpMethod) {
        this.httpMethod = httpMethod;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public RequestDetails requestTime(final long requestTime) {
        this.requestTime = requestTime;
        return this;
    }

    public RequestDetails httpMethod(final HttpMethod httpMethod) {
        this.httpMethod = httpMethod;
        return this;
    }

    public RequestDetails endpoint(final String endpoint) {
        this.endpoint = endpoint;
        return this;
    }

    public RequestDetails clientId(final String clientId) {
        this.clientId = clientId;
        return this;
    }
}