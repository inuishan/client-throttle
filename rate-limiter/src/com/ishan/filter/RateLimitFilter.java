package com.ishan.filter;

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

        chain.doFilter(request, response);
    }
}