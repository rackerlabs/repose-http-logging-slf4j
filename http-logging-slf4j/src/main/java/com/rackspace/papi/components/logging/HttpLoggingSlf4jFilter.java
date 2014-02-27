package com.rackspace.papi.components.logging;

import com.rackspace.papi.filter.logic.impl.FilterLogicHandlerDelegate;
import java.io.IOException;
import javax.servlet.*;

/**
 *
 * @author jhopper
 */
public class HttpLoggingSlf4jFilter implements Filter {

    private HttpLoggingSlf4jHandlerFactory handlerFactory;

    @Override
    public void destroy() {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        new FilterLogicHandlerDelegate(request, response, chain).doFilter(handlerFactory.newHandler());
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        handlerFactory = new HttpLoggingSlf4jHandlerFactory();
    }
}
