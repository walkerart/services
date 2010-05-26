/**
 * This document is a part of the source code and related artifacts
 * for CollectionSpace, an open source collections management system
 * for museums and related institutions:
 *
 * http://www.collectionspace.org
 * http://wiki.collectionspace.org
 *
 * Copyright (c) 2009 Regents of the University of California
 *
 * Licensed under the Educational Community License (ECL), Version 2.0.
 * You may not use this file except in compliance with this License.
 *
 * You may obtain a copy of the ECL 2.0 License at
 *
 * https://source.collectionspace.org/collection-space/LICENSE.txt
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.collectionspace.services.client.test;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.Response;

import org.collectionspace.services.jaxb.AbstractCommonList;
import org.collectionspace.services.client.CollectionSpaceClient;
import org.jboss.resteasy.client.ClientResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;

/**
 * AbstractServiceTestImpl
 *
 * Abstract base class for client tests of entity and relation services.
 * Abstract methods are provided for a set of CRUD + List tests to be invoked.
 *
 * For Javadoc descriptions of this class's methods, see the ServiceTest interface.
 *
 * $LastChangedRevision$
 * $LastChangedDate$
 */

// FIXME: http://issues.collectionspace.org/browse/CSPACE-1685

public abstract class AbstractServiceTestImpl extends BaseServiceTest implements ServiceTest {

    /** The logger. */
    private final Logger logger = LoggerFactory.getLogger(AbstractServiceTestImpl.class);

    // A non-existent logger.
    static private final Logger LOGGER_NULL = null;

    /** The Constant DEFAULT_LIST_SIZE. */
    static protected final int DEFAULT_LIST_SIZE = 10;
    static protected final int DEFAULT_PAGINATEDLIST_SIZE = 10;

    /* Use this to keep track of resources to delete */
    protected List<String> allResourceIdsCreated = new ArrayList<String>();

    /**
     * Gets the logger.
     *
     * @return the logger
     */
    private Logger getLogger() {
    	return this.logger;
    }

    // ---------------------------------------------------------------
    // CRUD tests : CREATE tests
    //
    // (See below for utility methods in support of create list tests.)
    // ---------------------------------------------------------------

    // Success outcomes

    /* (non-Javadoc)
     * @see org.collectionspace.services.client.test.ServiceTest#create(java.lang.String)
     */
    @Override
    public abstract void create(String testName) throws Exception;

    /**
     * Sets up create tests.
     *
     * @param testName The name of the test being run.
     */
    protected void setupCreate(String testName) {
        setupCreate(testName, LOGGER_NULL);
    }

    /**
     * Sets up create tests.
     *
     * @param testName The name of the test being run.
     *
     * @param testLogger The logger to use within the current base class,
     *     when generating log statements related to that test.
     */
    protected void setupCreate(String testName, Logger testLogger) {
        EXPECTED_STATUS_CODE = Response.Status.CREATED.getStatusCode();
        REQUEST_TYPE = ServiceRequestType.CREATE;
        testSetup(EXPECTED_STATUS_CODE, REQUEST_TYPE, testName, testLogger);
    }

    /* (non-Javadoc)
     * @see org.collectionspace.services.client.test.ServiceTest#createList(java.lang.String)
     */
    @Override
    public abstract void createList(String testName) throws Exception;

    // Note: No setup is required for createList(), as it currently
    // just invokes create() multiple times.

    // Failure outcomes

    /* (non-Javadoc)
     * @see org.collectionspace.services.client.test.ServiceTest#createWithEmptyEntityBody(java.lang.String)
     */
    @Override
    public abstract void createWithEmptyEntityBody(String testName)
            throws Exception;

    /**
     * Sets up create tests with empty entity body.
     *
     * @param testName The name of the test being run.
     */
    protected void setupCreateWithEmptyEntityBody(String testName) {
        setupCreateWithEmptyEntityBody(testName, LOGGER_NULL);
    }

    /**
     * Sets up create tests with empty entity body.
     *
     * @param testName The name of the test being run.
     *
     * @param testLogger The logger to use within the current base class,
     *     when generating log statements related to that test.
     */
    protected void setupCreateWithEmptyEntityBody(String testName, Logger testLogger) {
        EXPECTED_STATUS_CODE = Response.Status.BAD_REQUEST.getStatusCode();
        REQUEST_TYPE = ServiceRequestType.CREATE;
        testSetup(EXPECTED_STATUS_CODE, REQUEST_TYPE, testName, testLogger);
    }

    /* (non-Javadoc)
     * @see org.collectionspace.services.client.test.ServiceTest#createWithMalformedXml(java.lang.String)
     */
    @Override
    public abstract void createWithMalformedXml(String testName) throws Exception;

    /**
     * Sets up create tests with malformed xml.
     *
     * @param testName The name of the test being run.
     */
    protected void setupCreateWithMalformedXml(String testName) {
        setupCreateWithMalformedXml(testName, LOGGER_NULL);
    }

    /**
     * Sets up create tests with malformed xml.
     *
     * @param testName The name of the test being run.
     *
     * @param testLogger The logger to use within the current base class,
     *     when generating log statements related to that test.
     */
    protected void setupCreateWithMalformedXml(String testName, Logger testLogger) {
        EXPECTED_STATUS_CODE = Response.Status.BAD_REQUEST.getStatusCode();
        REQUEST_TYPE = ServiceRequestType.CREATE;
        testSetup(EXPECTED_STATUS_CODE, REQUEST_TYPE, testName, testLogger);
    }

    /* (non-Javadoc)
     * @see org.collectionspace.services.client.test.ServiceTest#createWithWrongXmlSchema(java.lang.String)
     */
    @Override
    public abstract void createWithWrongXmlSchema(String testName) throws Exception;

    /**
     * Sets up create tests with wrong xml schema.
     *
     * @param testName The name of the test being run.
     */
    protected void setupCreateWithWrongXmlSchema(String testName) {
        setupCreateWithWrongXmlSchema(testName, LOGGER_NULL);
    }

    /**
     * Sets up create tests with wrong xml schema.
     *
     * @param testName The name of the test being run.
     *
     * @param testLogger The logger to use within the current base class,
     *     when generating log statements related to that test.
     */
    protected void setupCreateWithWrongXmlSchema(String testName, Logger testLogger) {
        EXPECTED_STATUS_CODE = Response.Status.BAD_REQUEST.getStatusCode();
        REQUEST_TYPE = ServiceRequestType.CREATE;
        testSetup(EXPECTED_STATUS_CODE, REQUEST_TYPE, testName, testLogger);
    }

    // ---------------------------------------------------------------
    // CRUD tests : READ tests
    // ---------------------------------------------------------------

    // Success outcomes

    /* (non-Javadoc)
     * @see org.collectionspace.services.client.test.ServiceTest#read(java.lang.String)
     */
    @Override
    public abstract void read(String testName) throws Exception;

    /**
     * Sets up read tests.
     *
     * @param testName The name of the test being run.
     */
    protected void setupRead(String testName) {
        setupRead(testName, LOGGER_NULL);
    }

    /**
     * Sets up read tests.
     *
     * @param testName The name of the test being run.
     *
     * @param testLogger The logger to use within the current base class,
     *     when generating log statements related to that test.
     */
    protected void setupRead(String testName, Logger testLogger) {
        EXPECTED_STATUS_CODE = Response.Status.OK.getStatusCode();
        REQUEST_TYPE = ServiceRequestType.READ;
        testSetup(EXPECTED_STATUS_CODE, REQUEST_TYPE, testName, testLogger);
    }

    // Failure outcomes

    /* (non-Javadoc)
     * @see org.collectionspace.services.client.test.ServiceTest#readNonExistent(java.lang.String)
     */
    @Override
    public abstract void readNonExistent(String testName) throws Exception;

    /**
     * Sets up read non existent tests.
     *
     * @param testName The name of the test being run.
     */
    protected void setupReadNonExistent(String testName) {
        setupReadNonExistent(testName, LOGGER_NULL);
    }

    /**
     * Sets up read non existent tests.
     *
     * @param testName The name of the test being run.
     *
     * @param testLogger The logger to use within the current base class,
     *     when generating log statements related to that test.
     */
    protected void setupReadNonExistent(String testName, Logger testLogger) {
        EXPECTED_STATUS_CODE = Response.Status.NOT_FOUND.getStatusCode();
        REQUEST_TYPE = ServiceRequestType.READ;
        testSetup(EXPECTED_STATUS_CODE, REQUEST_TYPE, testName, testLogger);
    }

    // ---------------------------------------------------------------
    // CRUD tests : READ (list, or multiple) tests
    //
    // (See below for utility methods in support of list tests.)
    // ---------------------------------------------------------------

    // Success outcomes

    /* (non-Javadoc)
	 * @see org.collectionspace.services.client.test.ServiceTest#readList(java.lang.String)
	 */
    @Override
    public abstract void readList(String testName) throws Exception;

    /**
     * Sets up read list tests.
     *
     * @param testName The name of the test being run.
     */
    protected void setupReadList(String testName) {
        setupReadList(testName, LOGGER_NULL);
    }

    /**
     * Sets up read list tests.
     *
     * @param testName The name of the test being run.
     *
     * @param testLogger The logger to use within the current base class,
     *     when generating log statements related to that test.
     */
    protected void setupReadList(String testName, Logger testLogger) {
        EXPECTED_STATUS_CODE = Response.Status.OK.getStatusCode();
        REQUEST_TYPE = ServiceRequestType.READ_LIST;
        testSetup(EXPECTED_STATUS_CODE, REQUEST_TYPE, testName, testLogger);
    }

    // Failure outcomes

    // None tested at present.

    // ---------------------------------------------------------------
    // CRUD tests : UPDATE tests
    // ---------------------------------------------------------------

    // Success outcomes

    /* (non-Javadoc)
     * @see org.collectionspace.services.client.test.ServiceTest#update(java.lang.String)
     */
    @Override
    public abstract void update(String testName) throws Exception;

    /**
     * Sets up update tests.
     *
     * @param testName The name of the test being run.
     */
    protected void setupUpdate(String testName) {
        setupUpdate(testName, LOGGER_NULL);
    }

    /**
     * Sets up update tests.
     *
     * @param testName The name of the test being run.
     *
     * @param testLogger The logger to use within the current base class,
     *     when generating log statements related to that test.
     */
    protected void setupUpdate(String testName, Logger testLogger) {
        EXPECTED_STATUS_CODE = Response.Status.OK.getStatusCode();
        REQUEST_TYPE = ServiceRequestType.UPDATE;
        testSetup(EXPECTED_STATUS_CODE, REQUEST_TYPE, testName, testLogger);
    }

    // Failure outcomes

    /* (non-Javadoc)
     * @see org.collectionspace.services.client.test.ServiceTest#updateWithEmptyEntityBody(java.lang.String)
     */
    @Override
    public abstract void updateWithEmptyEntityBody(String testName) throws Exception;

    /**
     * Sets up update tests with an empty entity body.
     *
     * @param testName The name of the test being run.
     */
    protected void setupUpdateWithEmptyEntityBody(String testName) {
        setupUpdateWithEmptyEntityBody(testName, LOGGER_NULL);
    }

    /**
     * Sets up update tests with an empty entity body.
     *
     * @param testName The name of the test being run.
     *
     * @param testLogger The logger to use within the current base class,
     *     when generating log statements related to that test.
     */
    protected void setupUpdateWithEmptyEntityBody(String testName, Logger testLogger) {
        EXPECTED_STATUS_CODE = Response.Status.BAD_REQUEST.getStatusCode();
        REQUEST_TYPE = ServiceRequestType.UPDATE;
        testSetup(EXPECTED_STATUS_CODE, REQUEST_TYPE, testName, testLogger);
    }

    /* (non-Javadoc)
     * @see org.collectionspace.services.client.test.ServiceTest#updateWithMalformedXml(java.lang.String)
     */
    @Override
    public abstract void updateWithMalformedXml(String testName) throws Exception;

    /**
     * Sets up update tests with malformed xml.
     *
     * @param testName The name of the test being run.
     */
    protected void setupUpdateWithMalformedXml(String testName) {
        setupUpdateWithMalformedXml(testName, LOGGER_NULL);
    }

    /**
     * Sets up update tests with malformed xml.
     *
     * @param testName The name of the test being run.
     *
     * @param testLogger The logger to use within the current base class,
     *     when generating log statements related to that test.
     */
    protected void setupUpdateWithMalformedXml(String testName, Logger testLogger) {
        EXPECTED_STATUS_CODE = Response.Status.BAD_REQUEST.getStatusCode();
        REQUEST_TYPE = ServiceRequestType.UPDATE;
        testSetup(EXPECTED_STATUS_CODE, REQUEST_TYPE, testName, testLogger);
    }

    /* (non-Javadoc)
     * @see org.collectionspace.services.client.test.ServiceTest#updateWithWrongXmlSchema(java.lang.String)
     */
    @Override
    public abstract void updateWithWrongXmlSchema(String testName) throws Exception;

    /**
     * Sets up update tests with wrong xml schema.
     *
     * @param testName The name of the test being run.
     */
    protected void setupUpdateWithWrongXmlSchema(String testName) {
        setupUpdateWithWrongXmlSchema(testName, LOGGER_NULL);
    }

    /**
     * Sets up update tests with wrong xml schema.
     *
     * @param testName The name of the test being run.
     *
     * @param testLogger The logger to use within the current base class,
     *     when generating log statements related to that test.
     */
    protected void setupUpdateWithWrongXmlSchema(String testName, Logger testLogger) {
        EXPECTED_STATUS_CODE = Response.Status.BAD_REQUEST.getStatusCode();
        REQUEST_TYPE = ServiceRequestType.UPDATE;
        testSetup(EXPECTED_STATUS_CODE, REQUEST_TYPE, testName, testLogger);
    }
    
    /* (non-Javadoc)
     * @see org.collectionspace.services.client.test.ServiceTest#updateNonExistent(java.lang.String)
     */
    @Override
    public abstract void updateNonExistent(String testName) throws Exception;

    /**
     * Sets up update non existent tests.
     *
     * @param testName The name of the test being run.
     */
    protected void setupUpdateNonExistent(String testName) {
        setupUpdateNonExistent(testName, LOGGER_NULL);
    }

    /**
     * Sets up update non existent tests
     *
     * @param testName The name of the test being run.
     *
     * @param testLogger The logger to use within the current base class,
     *     when generating log statements related to that test.
     */
    protected void setupUpdateNonExistent(String testName, Logger testLogger) {
        EXPECTED_STATUS_CODE = Response.Status.NOT_FOUND.getStatusCode();
        REQUEST_TYPE = ServiceRequestType.UPDATE;
        testSetup(EXPECTED_STATUS_CODE, REQUEST_TYPE, testName, testLogger);
    }

    // ---------------------------------------------------------------
    // CRUD tests : DELETE tests
    // ---------------------------------------------------------------

    // Success outcomes

    /* (non-Javadoc)
     * @see org.collectionspace.services.client.test.ServiceTest#delete(java.lang.String)
     */
    @Override
    public abstract void delete(String testName) throws Exception;

    /**
     * Sets up delete tests.
     *
     * @param testName The name of the test being run.
     */
    protected void setupDelete(String testName) {
        setupDelete(testName, LOGGER_NULL);
    }

    /**
     * Sets up delete tests.
     *
     * @param testName The name of the test being run.
     *
     * @param testLogger The logger to use within the current base class,
     *     when generating log statements related to that test.
     */
    protected void setupDelete(String testName, Logger testLogger) {
        EXPECTED_STATUS_CODE = Response.Status.OK.getStatusCode();
        REQUEST_TYPE = ServiceRequestType.DELETE;
        testSetup(EXPECTED_STATUS_CODE, REQUEST_TYPE, testName, testLogger);
    }

    // Failure outcomes

    /* (non-Javadoc)
     * @see org.collectionspace.services.client.test.ServiceTest#deleteNonExistent(java.lang.String)
     */
    @Override
    public abstract void deleteNonExistent(String testName) throws Exception;

    /**
     * Sets up delete non existent tests.
     *
     * @param testName The name of the test being run.
     */
    protected void setupDeleteNonExistent(String testName) {
        setupDeleteNonExistent(testName, LOGGER_NULL);
    }

    /**
     * Sets up delete non existent tests.
     *
     * @param testName The name of the test being run.
     *
     * @param testLogger The logger to use within the current base class,
     *     when generating log statements related to that test.
     */
    protected void setupDeleteNonExistent(String testName, Logger testLogger) {
        EXPECTED_STATUS_CODE = Response.Status.NOT_FOUND.getStatusCode();
        REQUEST_TYPE = ServiceRequestType.DELETE;
        testSetup(EXPECTED_STATUS_CODE, REQUEST_TYPE, testName, testLogger);
    }

    // ---------------------------------------------------------------
    // Utility methods to clean up resources created during tests.
    // ---------------------------------------------------------------

    /**
     * Deletes all resources created by tests, after all tests have been run.
     *
     * This cleanup method will always be run, even if one or more tests fail.
     * For this reason, it attempts to remove all resources created
     * at any point during testing, even if some of those resources
     * may be expected to be deleted by certain tests.
     */
    @AfterClass(alwaysRun=true)
    public void cleanUp() {
        String noTest = System.getProperty("noTestCleanup");
    	if(Boolean.TRUE.toString().equalsIgnoreCase(noTest)) {
            if (logger.isDebugEnabled()) {
                logger.debug("Skipping Cleanup phase ...");
            }
            return;
    	}
        if (logger.isDebugEnabled()) {
            logger.debug("Cleaning up temporary resources created for testing ...");
        }
        CollectionSpaceClient client = this.getClientInstance();
        for (String resourceId : allResourceIdsCreated) {
            // Note: Any non-success responses are ignored and not reported.
            client.delete(resourceId).releaseConnection();
        }
    }

    // ---------------------------------------------------------------
    // Utility methods in support of list tests.
    // ---------------------------------------------------------------

    /**
     * Read list.
     *
     * @param testName the test name
     * @param client the client
     * @param pageSize the page size
     * @param pageNumber the page number
     * @return the abstract common list
     * @throws Exception the exception
     */
    private AbstractCommonList readList(String testName,
                    CollectionSpaceClient client,
                    long pageSize, long pageNumber) throws Exception {
        ClientResponse<AbstractCommonList> response =
                client.readList(Long.toString(pageSize), Long.toString(pageNumber));
        AbstractCommonList result = null;
        try {
            int statusCode = response.getStatus();

            // Check the status code of the response: does it match
            // the expected response(s)?
            if (getLogger().isDebugEnabled()) {
                    getLogger().debug(testName + ": status = " + statusCode);
            }
            Assert.assertTrue(this.REQUEST_TYPE.isValidStatusCode(statusCode),
                            invalidStatusCodeMessage(this.REQUEST_TYPE, statusCode));
            Assert.assertEquals(statusCode, this.EXPECTED_STATUS_CODE);

            result = this.getAbstractCommonList(response);
        } finally {
            response.releaseConnection();
        }

        return result;
    }

    /**
     * Creates the list.
     *
     * @param testName the test name
     * @param listSize the list size
     * @throws Exception the exception
     */
    protected void createPaginatedList(String testName, int listSize) throws Exception {
        for (int i = 0; i < listSize; i++) {
            create(testName);
        }
    }

    /*@Test(dataProvider = "testName", dataProviderClass = AbstractServiceTestImpl.class)
    public void leafCreate(String testName) throws Exception {
    	this.create(testName);
    }*/

    private void assertPaginationInfo(String testName,
    		AbstractCommonList list,
    		long expectedPageNum,
    		long expectedPageSize,
    		long expectedListSize,
    		long expectedTotalItems) {
    	Assert.assertNotNull(list);

    	long pageNum = list.getPageNum();
    	Assert.assertEquals(pageNum, expectedPageNum);
    	if (getLogger().isDebugEnabled() == true) {
    		getLogger().debug(testName + ":" + "page number is " + pageNum);
    	}

    	long pageSizeReturned = list.getPageSize();
    	Assert.assertEquals(pageSizeReturned, expectedPageSize);
    	if (getLogger().isDebugEnabled() == true) {
    		getLogger().debug(testName + ":" + "page size is " + list.getPageSize());
    	}

    	long itemsInPage = list.getItemsInPage();
    	Assert.assertEquals(itemsInPage, expectedListSize);
    	if (getLogger().isDebugEnabled() == true) {
    		getLogger().debug(testName + ":" + "actual items in page was/were " + itemsInPage);
    	}

    	long totalItemsReturned = list.getTotalItems();
    	Assert.assertEquals(totalItemsReturned, expectedTotalItems);
    	if (getLogger().isDebugEnabled() == true) {
    		getLogger().debug(testName + ":" + "total number of items is " + list.getTotalItems());
    	}
    }

    /**
     * Read paginated list.
     *
     * @param testName the test name
     * @throws Exception the exception
     */
    @Test(dataProvider = "testName") /*, dataProviderClass = AbstractServiceTestImpl.class,
    	    dependsOnMethods = {"leafCreate"}) */
    public void readPaginatedList(String testName) throws Exception {
        // Perform setup.
        setupReadList(testName);
        CollectionSpaceClient client = this.getClientInstance();

        // Get the current total number of items.
        // If there are no items then create some
        AbstractCommonList list = (AbstractCommonList) this.readList(testName, client, 1 /*pgSz*/, 0 /*pgNum*/);
        if (list == null || list.getTotalItems() == 0) {
        	this.createPaginatedList(testName, DEFAULT_PAGINATEDLIST_SIZE);
        	list = (AbstractCommonList) this.readList(testName, client, 1 /*pgSz*/, 0 /*pgNum*/);
        }

        // Print out the current list size to be paginated
        Assert.assertNotNull(list);
        long totalItems = list.getTotalItems();
        Assert.assertFalse(totalItems == 0);
        if (getLogger().isDebugEnabled() == true) {
        	getLogger().debug(testName + ":" + "created list of " +
        			totalItems + " to be paginated.");
        }

        long pageSize = totalItems / 3; //create up to 3 pages to iterate over
        long pagesTotal = pageSize > 0 ? (totalItems / pageSize) : 0;
        for (int i = 0; i < pagesTotal; i++) {
        	list = (AbstractCommonList) this.readList(testName, client, pageSize, i);
        	assertPaginationInfo(testName,
        			list,
        			i,			//expected page number
        			pageSize,	//expected page size
        			pageSize,	//expected num of items in page
        			totalItems);//expected total num of items
        }

        // if there are any remainders be sure to paginate them as well
        long mod = pageSize != 0 ? totalItems % pageSize : totalItems;
        if (mod != 0) {
        	list = (AbstractCommonList) this.readList(testName, client, pageSize, pagesTotal);
        	assertPaginationInfo(testName,
        			list,
        			pagesTotal, //expected page number
        			pageSize, 	//expected page size
        			mod, 		//expected num of items in page
        			totalItems);//expected total num of items
        }
    }

}


