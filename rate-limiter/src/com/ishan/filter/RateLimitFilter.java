package com.ishan.filter;

import com.ishan.base.*;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author ishanjain
 * @since 21/03/18
 */
public class RateLimitFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        String requestURI = httpServletRequest.getRequestURI();
        String method = httpServletRequest.getMethod();
        HttpMethod httpMethod = HttpMethod.valueOf(method);
        String clientId = extractClientId(requestURI);
        ClientConfig clientConfig = ClientConfigProvider.getClientConfig(clientId);

        long currentTime = System.currentTimeMillis();

        RateLimitValidator.RateLimitResponse rateLimitResponse = RateLimitValidator.validateRateLimited(clientConfig,
                new RequestDetails(currentTime, httpMethod, extractEndPoint(requestURI), extractClientId(requestURI)));

        boolean rateLimitReached = rateLimitResponse.getRateLimitReached();

        if (rateLimitReached) {
            HttpServletResponse httpServletResponse = (HttpServletResponse) response;
            httpServletResponse
                    .sendError(429, "Rate limit exceeded for period " + rateLimitResponse.getRateLimitPeriod());
        }

        chain.doFilter(request, response);
    }

    private String extractClientId(String requestURI) {

        return null;
    }

    private String extractEndPoint(String requestUri) {

    }
}