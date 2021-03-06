/**
 * This document is a part of the source code and related artifacts
 * for CollectionSpace, an open source collections management system
 * for museums and related institutions:
 *
 * http://www.collectionspace.org
 * http://wiki.collectionspace.org
 *
 * Copyright © 2009 {Contributing Institution}
 *
 * Licensed under the Educational Community License (ECL), Version 2.0.
 * You may not use this file except in compliance with this License.
 *
 * You may obtain a copy of the ECL 2.0 License at
 * https://source.collectionspace.org/collection-space/LICENSE.txt
 */
package org.collectionspace.services.common.profile;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.collectionspace.services.client.Profiler;
import org.collectionspace.services.common.ServletTools;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * CSpaceFilter.java
 *
 * A filter that performs specified actions at the time
 * each new request is received by the servlet container.
 *
 * This filter is currently used for recording performance
 * metrics for requests to the CollectionSpace services.
 *
 * $LastChangedRevision: $
 * $LastChangedDate: $
 *
 */
public class CSpaceFilter implements Filter {
    final Logger logger = LoggerFactory.getLogger(CSpaceFilter.class);

    /** The filter config. */
    FilterConfig filterConfig = null;
    private final String CLASS_NAME = this.getClass().getSimpleName();

    /* (non-Javadoc)
     * @see javax.servlet.Filter#destroy()
     */
    @Override
    public void destroy() {
        // Empty method.
    }

    /* (non-Javadoc)
     * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest, javax.servlet.ServletResponse, javax.servlet.FilterChain)
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
            FilterChain chain) throws IOException, ServletException {
        if (request != null) {
            HttpServletRequest httpRequest = (HttpServletRequest) request;

            // Instantiate the CollectionSpace services profiler.
            StringBuffer uri = new StringBuffer(httpRequest.getRequestURI());
            uri.append(':');
            uri.append(httpRequest.getMethod());
            Profiler profiler = new Profiler(uri.toString(), 0);
            Profiler csvProfiler = new Profiler("org.collectionspace.perf.csv");

            // Start timing.
            profiler.start();

            // Write a CSV-delimited message to the performance log,
            // in a format intended to be interoperable with those
            // generated by other system layers.
            String csvMsg =
                    profiler.getStartTime()
                    + "," + profiler.getElapsedTime()
                    + "," + "request"
                    + "," + "app"
                    + "," + "svc"
                    + "," + httpRequest.getMethod()
                    + "," + ServletTools.getURL(httpRequest)
                    + "," + CLASS_NAME
                    + "," + Thread.currentThread().getName();
           final boolean FORMAT_LOG_MESSAGE = false;
           profiler.log(csvMsg, FORMAT_LOG_MESSAGE);

            // Process the request.
            chain.doFilter(request, response);

            // Stop timing and log performance-related metrics.
            profiler.stop();

            csvMsg =
                    profiler.getStopTime()
                    + "," + profiler.getElapsedTime()
                    + "," + "response"
                    + "," + "svc"
                    + "," + "app"
                    + "," + httpRequest.getMethod()
                    + "," + ServletTools.getURL(httpRequest)
                    + "," + CLASS_NAME
                    + "," + Thread.currentThread().getName();
            profiler.log(csvMsg, FORMAT_LOG_MESSAGE);

            profiler.reset();
        }
    }
    
    /* (non-Javadoc)
     * @see javax.servlet.Filter#init(javax.servlet.FilterConfig)
     */
    @Override
    public void init(FilterConfig theFilterConfig) throws ServletException {
        filterConfig = theFilterConfig;
        if (filterConfig != null) {
            // We can initialize using the init-params here which we defined in
            // web.xml)
        }
    }
}
