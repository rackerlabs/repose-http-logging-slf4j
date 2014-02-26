package com.rackspace.papi.components.logging;

import com.rackspace.papi.commons.util.servlet.http.ReadableHttpServletResponse;
import com.rackspace.papi.filter.logic.FilterAction;
import com.rackspace.papi.filter.logic.FilterDirector;
import com.rackspace.papi.filter.logic.common.AbstractFilterLogicHandler;
import com.rackspace.papi.filter.logic.impl.FilterDirectorImpl;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Dan Daley
 */
public class HttpLoggingSlf4jHandler extends AbstractFilterLogicHandler {
    private HttpSlf4jLoggerWrapper loggerWrapper;

    public HttpLoggingSlf4jHandler(HttpSlf4jLoggerWrapper loggerWrapper) {
        this.loggerWrapper = loggerWrapper;
    }

    @Override
    public FilterDirector handleResponse(HttpServletRequest request, ReadableHttpServletResponse response) {
        FilterDirector filterDirector = new FilterDirectorImpl();
        filterDirector.setResponseStatusCode(response.getStatus());
        filterDirector.setFilterAction(FilterAction.PASS);

        loggerWrapper.handle(request, response);

        return filterDirector;
    }
    
    
    @Override
    public FilterDirector handleRequest(HttpServletRequest request, ReadableHttpServletResponse response){
        FilterDirector filterDirector = new FilterDirectorImpl();
        filterDirector.setFilterAction(FilterAction.PROCESS_RESPONSE);
        return filterDirector;
        
    }
}
