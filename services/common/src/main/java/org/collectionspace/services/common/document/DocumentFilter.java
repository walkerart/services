/**
 *  This document is a part of the source code and related artifacts
 *  for CollectionSpace, an open source collections management system
 *  for museums and related institutions:

 *  http://www.collectionspace.org
 *  http://wiki.collectionspace.org

 *  Copyright 2009 University of California at Berkeley

 *  Licensed under the Educational Community License (ECL), Version 2.0.
 *  You may not use this file except in compliance with this License.

 *  You may obtain a copy of the ECL 2.0 License at

 *  https://source.collectionspace.org/collection-space/LICENSE.txt
 */
package org.collectionspace.services.common.document;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ws.rs.core.MultivaluedMap;
import org.collectionspace.services.common.query.IQueryManager;

/**
 * DocumentFilter bundles simple query filtering parameters. 
 * It is designed to be used with filtered get and search calls to RepositoryClient.
 * The values are set up and stored on a DocumentHandler, and
 * fetched by a RepositoryClient when calling filtered get methods.
 */
public class DocumentFilter {

    public static final String PAGE_SIZE_PARAM = "pgSz";
    public static final String START_PAGE_PARAM = "pgNum";
    public static final int DEFAULT_PAGE_SIZE_INIT = 40;
    public static int defaultPageSize = DEFAULT_PAGE_SIZE_INIT;
    protected String whereClause;	// Filtering clause. Omit the "WHERE".
    protected int startPage;		// Pagination offset for list results
    protected int pageSize;			// Pagination limit for list results
    private MultivaluedMap<String, String> queryParams;

    public DocumentFilter() {
        this("", 0, defaultPageSize);			// Use empty string for easy concatenation
    }

    public DocumentFilter(String whereClause, int startPage, int pageSize) {
        this.whereClause = whereClause;
        this.startPage = (startPage > 0) ? startPage : 0;
        this.pageSize = (pageSize > 0) ? pageSize : defaultPageSize;
    }

    public void setPagination(MultivaluedMap<String, String> queryParams) {

        String startPageStr = null;
        String pageSizeStr = null;
        List<String> list = queryParams.remove(PAGE_SIZE_PARAM);
        if (list != null) {
            pageSizeStr = list.get(0);
        }
        list = queryParams.remove(START_PAGE_PARAM);
        if (list != null) {
            startPageStr = list.get(0);
        }
        if (pageSizeStr != null) {
            try {
                pageSize = Integer.valueOf(pageSizeStr);
            } catch (NumberFormatException e) {
                throw new NumberFormatException("Bad value for: " + PAGE_SIZE_PARAM);
            }
        }
        if (startPageStr != null) {
            try {
                startPage = Integer.valueOf(startPageStr);
            } catch (NumberFormatException e) {
                throw new NumberFormatException("Bad value for: " + START_PAGE_PARAM);
            }
        }
    }

    /**
     * @return the current default page size for new DocumentFilter instances
     */
    public static int getDefaultPageSize() {
        return defaultPageSize;
    }

    /**
     * @param defaultPageSize the working default page size for new DocumentFilter instances
     */
    public static void setDefaultPageSize(int defaultPageSize) {
        DocumentFilter.defaultPageSize = defaultPageSize;
    }

    /**
     * @return the WHERE filtering clause
     */
    public String getWhereClause() {
        return whereClause;
    }

    /**
     * @param whereClause the filtering clause (do not include "WHERE")
     */
    public void setWhereClause(String whereClause) {
        this.whereClause = whereClause;
    }

    public void appendWhereClause(String whereClause) {
        String currentClause = getWhereClause();
        if (currentClause != null) {
            String newClause = currentClause.concat(IQueryManager.SEARCH_TERM_SEPARATOR + whereClause);
            this.setWhereClause(newClause);
        }
    }

    /**
     * @return the specified (0-based) page offset
     */
    public int getStartPage() {
        return startPage;
    }

    /**
     * @param startPage the (0-based) page offset to use
     */
    public void setStartPage(int startPage) {
        this.startPage = startPage;
    }

    /**
     * @return the max number of items to return for list requests
     */
    public int getPageSize() {
        return pageSize;
    }

    /**
     * @param pageSize the max number of items to return for list requests
     */
    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    /**
     * @return the offset computed from the startPage and the pageSize
     */
    public int getOffset() {
        return pageSize * startPage;
    }

    public void addQueryParam(String key, String value) {
        queryParams.add(key, value);
    }

    public List<String> getQueryParam(String key) {
        return queryParams.get(key);
    }

    public MultivaluedMap<String, String> getQueryParams() {
        return queryParams;
    }

    public void setQueryParams(MultivaluedMap<String, String> queryParams) {
        this.queryParams = queryParams;
    }
}
