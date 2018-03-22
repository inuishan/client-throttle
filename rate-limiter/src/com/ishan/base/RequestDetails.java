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
}