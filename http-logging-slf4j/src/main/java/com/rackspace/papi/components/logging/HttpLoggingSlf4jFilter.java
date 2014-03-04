/*
 * Copyright 2014 Rackspace
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

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
