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

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.common.collect.Lists;
import com.rackspace.papi.commons.util.logging.apache.format.stock.RequestLineHandler;
import com.rackspace.papi.commons.util.logging.apache.format.stock.ResponseMessageHandler;
import com.rackspace.papi.commons.util.servlet.http.MutableHttpServletResponse;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.slf4j.MDC;

import java.util.*;

public class HttpSlf4jLoggerWrapper {
    private static final Logger logger = LoggerFactory.getLogger("repose-http-logger");

    private static final String START_TIME_ATTRIBUTE = "com.rackspace.repose.logging.start.time";
    private static final double DEFAULT_MULTIPLIER = 1000;
    private double multiplier = DEFAULT_MULTIPLIER;
    private static RequestLineHandler requestLineHandler = new RequestLineHandler();
    private static ResponseMessageHandler responseMessageHandler = new ResponseMessageHandler();

    public HttpSlf4jLoggerWrapper() {
    }

    public synchronized void handle(HttpServletRequest request, HttpServletResponse response) {
        final HashMap<String,Object> httpMDC = new HashMap<String,Object>();
        final HashMap<String,Object> requestHeaders = new HashMap<String,Object>();
        final HashMap<String,Object> responseHeaders = new HashMap<String,Object>();
        httpMDC.put("requestHeaders", requestHeaders);
        httpMDC.put("responseHeaders", responseHeaders);

        final Enumeration<String> requestHeaderNames = request.getHeaderNames();
        while (requestHeaderNames.hasMoreElements()) {
            final String nextHeaderName = requestHeaderNames.nextElement();
            final Enumeration<String> headerValues = request.getHeaders(nextHeaderName);

            String value = null;
            List<String> values = null;
            while (headerValues.hasMoreElements()) {
                if (values != null) {
                    values.add(headerValues.nextElement());
                } else if (value != null) {
                    values = Lists.newArrayList(value, headerValues.nextElement());
                } else {
                    value = headerValues.nextElement();
                }
            }
            if (value != null) {
                requestHeaders.put(nextHeaderName, values == null ? value : values);
            }
        }

        for (String headerName : response.getHeaderNames()) {
            final Collection<String> headerValues = response.getHeaders(headerName);
            if (headerValues.isEmpty()) {
                continue;
            } else if (headerValues.size() > 1) {
                responseHeaders.put(headerName, headerValues.toArray());
            } else {
                responseHeaders.put(headerName, headerValues.iterator().next());
            }
        }


        Object startTime = request.getAttribute(START_TIME_ATTRIBUTE);
        if (startTime != null) {
            httpMDC.put("requestReceived", startTime);
            httpMDC.put("processingTime", (System.currentTimeMillis() - (Long) startTime) * multiplier);
        }

        int responseStatus = response.getStatus();
        httpMDC.put("status", responseStatus);
        httpMDC.put("method", request.getMethod());
        httpMDC.put("protocol", request.getProtocol());
        httpMDC.put("requestURI", request.getRequestURI());
        httpMDC.put("requestURL", request.getRequestURL().toString());
        httpMDC.put("scheme", request.getScheme());
        httpMDC.put("serverName", request.getServerName());
        httpMDC.put("serverPort", request.getServerPort());
        httpMDC.put("localName", request.getLocalName());
        httpMDC.put("localPort", request.getLocalPort());
        httpMDC.put("remoteHost", request.getRemoteHost());
        httpMDC.put("remotePort", request.getRemotePort());

        httpMDC.put("requestContentLength", request.getContentLength());

        long responseContentLength = MutableHttpServletResponse.wrap(request, response).getResponseSize();
        httpMDC.put("responseContentLength", responseContentLength);


        String contextPath = request.getContextPath();
        if (contextPath != null && !contextPath.isEmpty()) {
            httpMDC.put("contextPath", contextPath);
        }
        String servletPath = request.getServletPath();
        if (servletPath != null && !servletPath.isEmpty()) {
            httpMDC.put("servletPath", servletPath);
        }
        Map<String, String[]> parameterMap = request.getParameterMap();
        if (parameterMap != null && !parameterMap.isEmpty()) {
            httpMDC.put("parameterMap", parameterMap);
        }
        Cookie[] cookies = request.getCookies();
        if (cookies != null && cookies.length != 0) {
            httpMDC.put("cookies", cookies);
        }
        String pathInfo = request.getPathInfo();
        if (pathInfo != null && !pathInfo.isEmpty()) {
            httpMDC.put("pathInfo", pathInfo);
        }
        String pathTranslated = request.getPathTranslated();
        if (pathTranslated != null && !pathTranslated.isEmpty()) {
            httpMDC.put("pathTranslated", pathTranslated);
        }
        String queryString = request.getQueryString();
        if (queryString != null && !queryString.isEmpty()) {
            httpMDC.put("queryString", queryString);
        }

        MDC.setContextMap(httpMDC);
        try {
            String message = requestLineHandler.handle(request, response);
            if (responseStatus >= 400) {
                MDC.put("errorMessage", responseMessageHandler.handle(request, response));
                if (responseStatus >= 500) {
                    logger.error(message);
                } else {
                    logger.warn(message);
                }
            } else {
                logger.info(message);
            }
        } finally {
            MDC.clear();
        }
    }
}
