package com.ishan.filter;

import com.ishan.base.*;
import com.sun.jndi.toolkit.url.Uri;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.MalformedURLException;

/**
 * @author ishanjain
 * @since 21/03/18
 */
public class RateLimitFilter implements Filter {

    private static final Logger LOGGER = LoggerFactory.getLogger(RateLimitFilter.class);

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;

        String requestURI = httpServletRequest.getRequestURI();

        String clientId = extractClientId(httpServletRequest);

        if (StringUtils.isBlank(clientId)) {
            httpServletResponse.sendError(403, "Unauthorized: Client id expected in header");
        } else {

            String method = httpServletRequest.getMethod();
            HttpMethod httpMethod = HttpMethod.valueOf(method);

            ClientConfig clientConfig = ClientConfigProvider.getClientConfig(clientId);

            long currentTime = System.currentTimeMillis();

            String endpoint = extractEndPoint(requestURI);

            RateLimitResponse rateLimitResponse = RateLimitValidator
                    .validateRateLimited(clientConfig, new RequestDetails(currentTime, httpMethod, endpoint, clientId));

            boolean rateLimitReached = rateLimitResponse.getRateLimitReached();

            if (rateLimitReached) {
                httpServletResponse
                        .sendError(429, "Rate limit exceeded for period " + rateLimitResponse.getRateLimitPeriod());
            } else {
                chain.doFilter(request, response);
            }
        }
    }

    private String extractClientId(HttpServletRequest httpServletRequest) {
        return httpServletRequest.getHeader("clientId");
    }

    private String extractEndPoint(String requestUri) {
        try {
            Uri uri = new Uri(requestUri);
            return uri.getPath();
        } catch (MalformedURLException e) {
            LOGGER.error("Malformed URL, should not have happened " + requestUri, e);
        }
        return null;
    }
}