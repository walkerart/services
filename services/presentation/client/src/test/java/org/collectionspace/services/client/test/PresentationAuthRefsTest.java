/**
 * This document is a part of the source code and related artifacts
 * for CollectionSpace, an open source collections management system
 * for museums and related institutions:
 *
 * http://www.collectionspace.org
 * http://wiki.collectionspace.org
 *
 * Copyright © 2009 Regents of the University of California
 *
 * Licensed under the Educational Community License (ECL), Version 2.0.
 * You may not use this file except in compliance with this License.
 *
 * You may obtain a copy of the ECL 2.0 License at
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.Response;

import org.collectionspace.services.PersonJAXBSchema;
import org.collectionspace.services.client.CollectionSpaceClient;
import org.collectionspace.services.client.PresentationClient;
import org.collectionspace.services.client.PersonAuthorityClient;
import org.collectionspace.services.client.PersonAuthorityClientUtils;
import org.collectionspace.services.client.PayloadOutputPart;
import org.collectionspace.services.client.PoxPayloadIn;
import org.collectionspace.services.client.PoxPayloadOut;
import org.collectionspace.services.common.authorityref.AuthorityRefList;
import org.collectionspace.services.jaxb.AbstractCommonList;
import org.collectionspace.services.presentation.PresentationsCommon;
import org.collectionspace.services.person.PersonTermGroup;

import org.jboss.resteasy.client.ClientResponse;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * PresentationAuthRefsTest, carries out Authority References tests against a
 * deployed and running Presentation Service.
 */
public class PresentationAuthRefsTest extends BaseServiceTest<AbstractCommonList> {

    private final String CLASS_NAME = PresentationAuthRefsTest.class.getName();
    private final Logger logger = LoggerFactory.getLogger(CLASS_NAME);
    
    // Instance variables specific to this test.
    final String SERVICE_NAME = "presentations";
    final String SERVICE_PATH_COMPONENT = "presentations";
    final String PERSON_AUTHORITY_NAME = "TestPersonAuth";
    private String knownResourceId = null;
    private List<String> presentationIdsCreated = new ArrayList<String>();
    private List<String> personIdsCreated = new ArrayList<String>();
    private String personAuthCSID = null;
    private String presentationPresenterRefName = null;

    private final int NUM_AUTH_REFS_EXPECTED = 1;


    /* (non-Javadoc)
     * @see org.collectionspace.services.client.test.BaseServiceTest#getClientInstance()
     */
    @Override
    protected CollectionSpaceClient getClientInstance() {
        throw new UnsupportedOperationException(); //method not supported (or needed) in this test class
    }
    
    /* (non-Javadoc)
     * @see org.collectionspace.services.client.test.BaseServiceTest#getAbstractCommonList(org.jboss.resteasy.client.ClientResponse)
     */
    @Override
    protected AbstractCommonList getCommonList(
            ClientResponse<AbstractCommonList> response) {
        throw new UnsupportedOperationException(); //method not supported (or needed) in this test class
    }

    // ---------------------------------------------------------------
    // CRUD tests : CREATE tests
    // ---------------------------------------------------------------
    // Success outcomes
    @Test(dataProvider="testName", dataProviderClass=AbstractServiceTestImpl.class)
    public void createWithAuthRefs(String testName) throws Exception {
        testSetup(STATUS_CREATED, ServiceRequestType.CREATE);

        // Submit the request to the service and store the response.
        String identifier = createIdentifier();
        
        // Create all the person refs and entities
        createPersonRefs();

        // Create a new Presentation resource.
        //
        // One or more fields in this resource will be PersonAuthority
        // references, and will refer to Person resources by their refNames.
        PresentationClient presentationClient = new PresentationClient();
        PoxPayloadOut multipart = createPresentationInstance(
                "presentationRefNumber-" + identifier,
                presentationPresenterRefName);
        ClientResponse<Response> response = presentationClient.create(multipart);
        int statusCode = response.getStatus();
        try {
            // Check the status code of the response: does it match
            // the expected response(s)?
            //
            // Specifically:
            // Does it fall within the set of valid status codes?
            // Does it exactly match the expected status code?
            if(logger.isDebugEnabled()){
                logger.debug(testName + ": status = " + statusCode);
            }
            Assert.assertTrue(testRequestType.isValidStatusCode(statusCode),
                    invalidStatusCodeMessage(testRequestType, statusCode));
            Assert.assertEquals(statusCode, testExpectedStatusCode);
    
            // Store the ID returned from the first resource created
            // for additional tests below.
            if (knownResourceId == null){
                knownResourceId = extractId(response);
                if (logger.isDebugEnabled()) {
                    logger.debug(testName + ": knownResourceId=" + knownResourceId);
                }
            }
            
            // Store the IDs from every resource created by tests,
            // so they can be deleted after tests have been run.
            presentationIdsCreated.add(extractId(response));
        } finally {
            response.releaseConnection();
        }
    }
    
    protected void createPersonRefs(){

        PersonAuthorityClient personAuthClient = new PersonAuthorityClient();
        // Create a temporary PersonAuthority resource, and its corresponding
        // refName by which it can be identified.
        PoxPayloadOut multipart = PersonAuthorityClientUtils.createPersonAuthorityInstance(
            PERSON_AUTHORITY_NAME, PERSON_AUTHORITY_NAME, personAuthClient.getCommonPartName());
        ClientResponse<Response> res = personAuthClient.create(multipart);
        int statusCode = res.getStatus();

        Assert.assertTrue(testRequestType.isValidStatusCode(statusCode),
            invalidStatusCodeMessage(testRequestType, statusCode));
        Assert.assertEquals(statusCode, STATUS_CREATED);
        personAuthCSID = extractId(res);

        String authRefName = PersonAuthorityClientUtils.getAuthorityRefName(personAuthCSID, null);
        
        // Create temporary Person resources, and their corresponding refNames
        // by which they can be identified.
        String csid = createPerson("Carrie", "Presenter", "CarriePresenter", authRefName);
        personIdsCreated.add(csid);
        presentationPresenterRefName = PersonAuthorityClientUtils.getPersonRefName(personAuthCSID, csid, null);
    }
    
    protected String createPerson(String firstName, String surName, String shortId, String authRefName ) {
        PersonAuthorityClient personAuthClient = new PersonAuthorityClient();
        Map<String, String> personInfo = new HashMap<String,String>();
        personInfo.put(PersonJAXBSchema.FORE_NAME, firstName);
        personInfo.put(PersonJAXBSchema.SUR_NAME, surName);
        personInfo.put(PersonJAXBSchema.SHORT_IDENTIFIER, shortId);
        List<PersonTermGroup> personTerms = new ArrayList<PersonTermGroup>();
        PersonTermGroup term = new PersonTermGroup();
        String termName = firstName + " " + surName;
        term.setTermDisplayName(termName);
        term.setTermName(termName);
        personTerms.add(term);
        PoxPayloadOut multipart =
            PersonAuthorityClientUtils.createPersonInstance(personAuthCSID, 
                    authRefName, personInfo, personTerms, personAuthClient.getItemCommonPartName());
        ClientResponse<Response> res = personAuthClient.createItem(personAuthCSID, multipart);
        int statusCode = res.getStatus();

        Assert.assertTrue(testRequestType.isValidStatusCode(statusCode),
                invalidStatusCodeMessage(testRequestType, statusCode));
        Assert.assertEquals(statusCode, STATUS_CREATED);
        return extractId(res);
    }

    // Success outcomes
    @Test(dataProvider="testName", dataProviderClass=AbstractServiceTestImpl.class,
        dependsOnMethods = {"createWithAuthRefs"})
    public void readAndCheckAuthRefs(String testName) throws Exception {
        // Perform setup.
        testSetup(STATUS_OK, ServiceRequestType.READ);

        // Submit the request to the service and store the response.
        PresentationClient presentationClient = new PresentationClient();
        ClientResponse<String> res = presentationClient.read(knownResourceId);
        PresentationsCommon presentationCommon = null;
        try {
            assertStatusCode(res, testName);
            // Extract the common part from the response.
            PoxPayloadIn input = new PoxPayloadIn(res.getEntity());
            presentationCommon = (PresentationsCommon) extractPart(input,
                presentationClient.getCommonPartName(), PresentationsCommon.class);
            Assert.assertNotNull(presentationCommon);
            if(logger.isDebugEnabled()){
                logger.debug(objectAsXmlString(presentationCommon, PresentationsCommon.class));
            }
        } finally {
            if (res != null) {
                res.releaseConnection();
            }
        }
        //
        // Check a couple of fields
        Assert.assertEquals(presentationCommon.getPresentationPresenter(), presentationPresenterRefName);
        
        // Get the auth refs and check them
        ClientResponse<AuthorityRefList> res2 = presentationClient.getAuthorityRefs(knownResourceId);
        AuthorityRefList list = null;
        try {
            assertStatusCode(res2, testName);
            list = res2.getEntity();
            Assert.assertNotNull(list);
        } finally {
            if (res2 != null) {
                res2.releaseConnection();
            }
        }
        
        List<AuthorityRefList.AuthorityRefItem> items = list.getAuthorityRefItem();
        int numAuthRefsFound = items.size();
        if(logger.isDebugEnabled()){
            logger.debug("Expected " + NUM_AUTH_REFS_EXPECTED +
                " authority references, found " + numAuthRefsFound);
        }

        // Optionally output additional data about list members for debugging.
        boolean iterateThroughList = true;
        if(iterateThroughList && logger.isDebugEnabled()){
            int i = 0;
            for(AuthorityRefList.AuthorityRefItem item : items){
                logger.debug(testName + ": list-item[" + i + "] Field:" +
                        item.getSourceField() + "= " +
                        item.getAuthDisplayName() +
                        item.getItemDisplayName());
                logger.debug(testName + ": list-item[" + i + "] refName=" +
                        item.getRefName());
                logger.debug(testName + ": list-item[" + i + "] URI=" +
                        item.getUri());
                i++;
            }
        }

        Assert.assertEquals(numAuthRefsFound, NUM_AUTH_REFS_EXPECTED,
            "Did not find all expected authority references! " +
            "Expected " + NUM_AUTH_REFS_EXPECTED + ", found " + numAuthRefsFound);

    }


    // ---------------------------------------------------------------
    // Cleanup of resources created during testing
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
        PersonAuthorityClient personAuthClient = new PersonAuthorityClient();
        // Delete Person resource(s) (before PersonAuthority resources).
        
        for (String resourceId : personIdsCreated) {
            // Note: Any non-success responses are ignored and not reported.
            ClientResponse<Response> response = 
                personAuthClient.deleteItem(personAuthCSID, resourceId); // alternative to personAuthClient.deleteItem().releaseConnection();
            response.releaseConnection();
        }
        
        // Delete PersonAuthority resource(s).
        // Note: Any non-success response is ignored and not reported.
        if (personAuthCSID != null) {
            personAuthClient.delete(personAuthCSID);
            // Delete Presentation resource(s).
            PresentationClient presentationClient = new PresentationClient();
            ClientResponse<Response> response = null;
            for (String resourceId : presentationIdsCreated) {
                // Note: Any non-success responses are ignored and not reported.
                response = presentationClient.delete(resourceId); // alternative to presentationClient.delete(resourceId).releaseConnection();
                response.releaseConnection();
            }
        }
    }

    // ---------------------------------------------------------------
    // Utility methods used by tests above
    // ---------------------------------------------------------------
    public String getServiceName() {
        return SERVICE_NAME;
    }

    @Override
    public String getServicePathComponent() {
        return SERVICE_PATH_COMPONENT;
    }

    private PoxPayloadOut createPresentationInstance(String presentationRefNumber,
            String presentationPresenter) {
        PresentationsCommon presentationCommon = new PresentationsCommon();

        presentationCommon.setPresentationRefNumber(presentationRefNumber);
        presentationCommon.setPresentationPresenter(presentationPresenter);

        PoxPayloadOut multipart = new PoxPayloadOut(this.getServicePathComponent());
        PayloadOutputPart commonPart =
                multipart.addPart(new PresentationClient().getCommonPartName(), presentationCommon);

        if(logger.isDebugEnabled()){
            logger.debug("to be created, presentation common");
            logger.debug(objectAsXmlString(presentationCommon, PresentationsCommon.class));
        }

        return multipart;
    }

    @Override
    protected Class<AbstractCommonList> getCommonListType() {
        return AbstractCommonList.class;
    }
}