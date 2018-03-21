package com.ishan.filter;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.ishan.base.ClientConfig;
import com.ishan.base.ClientConfigProvider;
import com.ishan.base.HttpMethod;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
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


        chain.doFilter(request, response);
    }

    private String extractClientId(String requestURI) {


    }
}