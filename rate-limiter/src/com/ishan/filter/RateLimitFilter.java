package com.ishan.filter;

import javax.servlet.*;
import java.io.IOException;

/**
 * @author ishanjain
 * @since 21/03/18
 */
public class RateLimitFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

    }
}