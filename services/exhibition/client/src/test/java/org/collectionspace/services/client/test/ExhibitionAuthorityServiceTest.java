/**
 * This document is a part of the source code and related artifacts
 * for CollectionSpace, an open source collections management system
 * for museums and related institutions:
 *
 * http://www.collectionspace.org
 * http://wiki.collectionspace.org
 *
 * Copyright (c)) 2009 Regents of the University of California
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
import org.collectionspace.services.ExhibitionJAXBSchema;
import org.collectionspace.services.client.AbstractCommonListUtils;
import org.collectionspace.services.client.AuthorityClient;
import org.collectionspace.services.client.CollectionSpaceClient;
import org.collectionspace.services.client.PayloadOutputPart;
import org.collectionspace.services.client.PoxPayloadIn;
import org.collectionspace.services.client.PoxPayloadOut;
import org.collectionspace.services.client.ExhibitionAuthorityClient;
import org.collectionspace.services.client.ExhibitionAuthorityClientUtils;
import org.collectionspace.services.jaxb.AbstractCommonList;
import org.collectionspace.services.exhibition.ExhibitionTermGroup;
import org.collectionspace.services.exhibition.ExhibitionTermGroupList;
import org.collectionspace.services.exhibition.ExhibitionauthoritiesCommon;
import org.collectionspace.services.exhibition.ExhibitionsCommon;
import org.jboss.resteasy.client.ClientResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;

/**
 * ExhibitionAuthorityServiceTest, carries out tests against a
 * deployed and running ExhibitionAuthority Service.
 *
 */
public class ExhibitionAuthorityServiceTest extends AbstractAuthorityServiceTest<ExhibitionauthoritiesCommon, ExhibitionsCommon> {

    /** The logger. */
    private final String CLASS_NAME = ExhibitionAuthorityServiceTest.class.getName();
    private final Logger logger = LoggerFactory.getLogger(ExhibitionAuthorityServiceTest.class);

    @Override
    public String getServicePathComponent() {
        return ExhibitionAuthorityClient.SERVICE_PATH_COMPONENT;
    }

    @Override
    protected String getServiceName() {
        return ExhibitionAuthorityClient.SERVICE_NAME;
    }
    
    public String getItemServicePathComponent() {
        return AuthorityClient.ITEMS;
    }   
    
    // Instance variables specific to this test.
    
//    /** The SERVICE path component. */
//    final String SERVICE_PATH_COMPONENT = "exhibitionauthorities";
//    
//    /** The ITEM service path component. */
//    final String ITEM_SERVICE_PATH_COMPONENT = "items";
//    

    final String TEST_EXHIBITION_TERM_DISPLAY_NAME = "Yves Klien: With the Void, Full Powers";
    final String TEST_EXHIBITION_TERM_FORMATTED_DISPLAY_NAME = "";
    final String TEST_EXHIBITION_TERM_NAME = "Yves Klien: With the Void, Full Powers";
    final String TEST_EXHIBITION_TERM_TYPE = "preferred";
    final String TEST_EXHIBITION_TERM_STATUS = "accepted";
    final String TEST_EXHIBITION_TERM_QUALIFIER = "term qualifier";
    final String TEST_EXHIBITION_TERM_LANGUAGE = "english";
    final Boolean TEST_EXHIBITION_TERM_PREFFORLANG = true;
    final String TEST_EXHIBITION_TERM_SOURCE = "internal";
    final String TEST_EXHIBITION_TERM_SOURCE_DETAIL = "internal";
    final String TEST_EXHIBITION_TERM_SOURCE_ID = "12345";    
    final String TEST_EXHIBITION_TERM_SOURCE_NOTE = "source note goes here";

    final String TEST_EXHIBITION_ORGANIZER = "Hirshhorn Museum and Sculpture Garden";
    final String TEST_EXHIBITION_VENUE_NAME = "Hirshhorn Museum and Sculpture Garden";
    final String TEST_EXHIBITION_VENUE_START_DATE = "2010-02-10";
    final String TEST_EXHIBITION_VENUE_END_DATE = "2010-05-31";
    final String TEST_EXHIBITION_VENUE_ATTENDANCE = "123456";
    final String TEST_EXHIBITION_LOCATION_INTERNAL = "Gallery 4";
    final String TEST_EXHIBITION_CURATOR = "Kerry Brougher";
    final String TEST_EXHIBITION_PUBLICATION = "yes";
    final String TEST_EXHIBITION_CATALOGUE = "catalogue goes here";
    final String TEST_EXHIBITION_LINK = "link goes here";
    final String TEST_EXHIBITION_REMARKS = "remark goes here";

    final String TEST_EXHIBITION_SHORT_IDENTIFIER = "YvesKlienWiththeVoidFullPowers";
    final String TEST_EXHIBITION_REFNAME = "refname";
    
    /** The known resource id. */
    private String knownResourceShortIdentifer = null;
    private String knownResourceRefName = null;
    private String knownExhibitionTypeRefName = null;
    
    /* (non-Javadoc)
     * @see org.collectionspace.services.client.test.BaseServiceTest#getClientInstance()
     */
    @Override
    protected CollectionSpaceClient getClientInstance() {
        return new ExhibitionAuthorityClient();
    }

    /**
     * Creates the item in authority.
     *
     * @param vcsid the vcsid
     * @param authRefName the auth ref name
     * @return the string
     */
    private String createItemInAuthority(String vcsid, String authRefName) {
        final String testName = "createItemInAuthority("+vcsid+","+authRefName+")"; 
    
        // Submit the request to the service and store the response.
        ExhibitionAuthorityClient client = new ExhibitionAuthorityClient();
        Map<String, String> exhibitionMap = new HashMap<String,String>();
        // TODO Make exhibition type and status be controlled vocabs.
        exhibitionMap.put(ExhibitionJAXBSchema.SHORT_IDENTIFIER, TEST_EXHIBITION_SHORT_IDENTIFIER);
        exhibitionMap.put(ExhibitionJAXBSchema.EXHIBITION_REMARKS, TEST_EXHIBITION_REMARKS);
        
        List<ExhibitionTermGroup> terms = new ArrayList<ExhibitionTermGroup>();
        ExhibitionTermGroup term = new ExhibitionTermGroup();
        term.setTermDisplayName(TEST_EXHIBITION_TERM_DISPLAY_NAME);
        term.setTermName(TEST_EXHIBITION_TERM_NAME);
        term.setTermSource(TEST_EXHIBITION_TERM_SOURCE);
        term.setTermSourceDetail(TEST_EXHIBITION_TERM_SOURCE_DETAIL);
        term.setTermStatus(TEST_EXHIBITION_TERM_STATUS);
        terms.add(term);
        
        String newID = ExhibitionAuthorityClientUtils.createItemInAuthority(vcsid,
                authRefName, exhibitionMap, terms, client );    

        // Store the ID returned from the first item resource created
        // for additional tests below.
        if (knownItemResourceId == null){
            setKnownItemResource(newID, TEST_EXHIBITION_SHORT_IDENTIFIER);
            if (logger.isDebugEnabled()) {
                logger.debug(testName + ": knownItemResourceId=" + newID);
            }
        }
        
        // Store the IDs from any item resources created
        // by tests, along with the IDs of their parents, so these items
        // can be deleted after all tests have been run.
        allResourceItemIdsCreated.put(newID, vcsid);

        return newID;
    }

    /**
     * Verify illegal item display name.
     *
     * @param testName the test name
     * @throws Exception the exception
     */
    @Test(dataProvider="testName")
    public void verifyIllegalItemDisplayName(String testName) throws Exception {
        // Perform setup for read.
        setupRead();

        // Submit the request to the service and store the response.
        ExhibitionAuthorityClient client = new ExhibitionAuthorityClient();
        ClientResponse<String> res = client.readItem(knownResourceId, knownItemResourceId);
        ExhibitionsCommon exhibition = null;
        try {
            assertStatusCode(res, testName);        
            PoxPayloadIn input = new PoxPayloadIn(res.getEntity());
            exhibition = (ExhibitionsCommon) extractPart(input,
                    client.getItemCommonPartName(), ExhibitionsCommon.class);
            Assert.assertNotNull(exhibition);
        } finally {
                if (res != null) {
                res.releaseConnection();
        }
        }

        //
        // Make an invalid UPDATE request, without a display name
        //
        ExhibitionTermGroupList termList = exhibition.getExhibitionTermGroupList();
        Assert.assertNotNull(termList);
        List<ExhibitionTermGroup> terms = termList.getExhibitionTermGroup();
        Assert.assertNotNull(terms);
        Assert.assertTrue(terms.size() > 0);
        terms.get(0).setTermDisplayName(null);
        terms.get(0).setTermName(null);
        
        setupUpdateWithInvalidBody(); // we expect a failure
        
        // Submit the updated resource to the service and store the response.
        PoxPayloadOut output = new PoxPayloadOut(ExhibitionAuthorityClient.SERVICE_ITEM_PAYLOAD_NAME);
        PayloadOutputPart commonPart = output.addPart(client.getItemCommonPartName(), exhibition);
        setupUpdateWithInvalidBody(); // we expected a failure here.
        res = client.updateItem(knownResourceId, knownItemResourceId, output);
        try {
            assertStatusCode(res, testName);
        } finally {
            if (res != null) {
                res.releaseConnection();
            }
        }
    }

    /**
     * Read item list.
     */
    @Test(dataProvider = "testName", groups = {"readList"},
               dependsOnMethods = {"readList"})
    public void readItemList(String testName) {
        readItemList(knownAuthorityWithItems, null);
    }

    /**
     * Read item list by authority name.
     */
    @Test(dataProvider = "testName", groups = {"readList"},
            dependsOnMethods = {"readItemList"})
    public void readItemListByAuthorityName(String testName) {
        readItemList(null, READITEMS_SHORT_IDENTIFIER);
    }
    
    /**
     * Read item list.
     * 
     * @param vcsid
     *            the vcsid
     * @param name
     *            the name
     */
    private void readItemList(String vcsid, String shortId) {
        String testName = "readItemList";

        // Perform setup.
        setupReadList();

        // Submit the request to the service and store the response.
        ExhibitionAuthorityClient client = new ExhibitionAuthorityClient();
        ClientResponse<AbstractCommonList> res = null;
        if (vcsid != null) {
            res = client.readItemList(vcsid, null, null);
        } else if (shortId != null) {
            res = client.readItemListForNamedAuthority(shortId, null, null);
        } else {
            Assert.fail("readItemList passed null csid and name!");
        }
        
        AbstractCommonList list = null;
        try {
            assertStatusCode(res, testName);
            list = res.getEntity();
        } finally {
            if (res != null) {
                res.releaseConnection();
            }
        }
        
        List<AbstractCommonList.ListItem> items = list.getListItem();
        int nItemsReturned = items.size();
        // There will be 'nItemsToCreateInList'
        // items created by the createItemList test,
        // all associated with the same parent resource.
        int nExpectedItems = nItemsToCreateInList;
        if (logger.isDebugEnabled()) {
            logger.debug(testName + ": Expected " + nExpectedItems
                    + " items; got: " + nItemsReturned);
        }
        Assert.assertEquals(nItemsReturned, nExpectedItems);

        for (AbstractCommonList.ListItem item : items) {
            String value = AbstractCommonListUtils.ListItemGetElementValue(
                    item, ExhibitionJAXBSchema.REF_NAME);
            Assert.assertTrue((null != value), "Item refName is null!");
            value = AbstractCommonListUtils.ListItemGetElementValue(item,
                    ExhibitionJAXBSchema.EXHIBITION_TERM_DISPLAY_NAME);
            Assert.assertTrue((null != value), "Item termDisplayName is null!");
        }
        if (logger.isTraceEnabled()) {
            AbstractCommonListUtils.ListItemsInAbstractCommonList(list, logger,
                    testName);
        }
    }

    @Override
    public void delete(String testName) throws Exception {
        // Do nothing.  See localDelete().  This ensure proper test order.
    }
    
    @Test(dataProvider = "testName", dependsOnMethods = {"localDeleteItem"})    
    public void localDelete(String testName) throws Exception {
        super.delete(testName);
    }

    @Override
    public void deleteItem(String testName) throws Exception {
        // Do nothing.  We need to wait until after the test "localDelete" gets run.  When it does,
        // its dependencies will get run first and then we can call the base class' delete method.
    }
    
    @Test(dataProvider = "testName", groups = {"delete"},
        dependsOnMethods = {"verifyIllegalItemDisplayName"})
    public void localDeleteItem(String testName) throws Exception {
        super.deleteItem(testName);
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
        String parentResourceId;
        String itemResourceId;
        // Clean up contact resources.
        ExhibitionAuthorityClient client = new ExhibitionAuthorityClient();
        parentResourceId = knownResourceId;
        // Clean up item resources.
        for (Map.Entry<String, String> entry : allResourceItemIdsCreated.entrySet()) {
            itemResourceId = entry.getKey();
            parentResourceId = entry.getValue();
            // Note: Any non-success responses from the delete operation
            // below are ignored and not reported.
            client.deleteItem(parentResourceId, itemResourceId).releaseConnection();
        }
        // Clean up parent resources.
        for (String resourceId : allResourceIdsCreated) {
            // Note: Any non-success responses from the delete operation
            // below are ignored and not reported.
        client.delete(resourceId).releaseConnection();
        }
    }

    // ---------------------------------------------------------------
    // Utility methods used by tests above
    // ---------------------------------------------------------------
    /* (non-Javadoc)
     * @see org.collectionspace.services.client.test.BaseServiceTest#getServicePathComponent()
     */

    /**
     * Returns the root URL for the item service.
     *
     * This URL consists of a base URL for all services, followed by
     * a path component for the owning parent, followed by the
     * path component for the items.
     *
     * @param  parentResourceIdentifier  An identifier (such as a UUID) for the
     * parent authority resource of the relevant item resource.
     *
     * @return The root URL for the item service.
     */
    protected String getItemServiceRootURL(String parentResourceIdentifier) {
        return getResourceURL(parentResourceIdentifier) + "/" + getItemServicePathComponent();
    }

    /**
     * Returns the URL of a specific item resource managed by a service, and
     * designated by an identifier (such as a universally unique ID, or UUID).
     *
     * @param  parentResourceIdentifier  An identifier (such as a UUID) for the
     * parent authority resource of the relevant item resource.
     *
     * @param  itemResourceIdentifier  An identifier (such as a UUID) for an
     * item resource.
     *
     * @return The URL of a specific item resource managed by a service.
     */
    protected String getItemResourceURL(String parentResourceIdentifier, String itemResourceIdentifier) {
        return getItemServiceRootURL(parentResourceIdentifier) + "/" + itemResourceIdentifier;
    }

        @Override
    public void authorityTests(String testName) {
        // TODO Auto-generated method stub
        
    }

    //
    // Exhibition specific overrides
    //
    
    @Override
    protected PoxPayloadOut createInstance(String commonPartName,
            String identifier) {
        // Submit the request to the service and store the response.
        String shortId = identifier;
        String displayName = "displayName-" + shortId;
        // String baseRefName = ExhibitionAuthorityClientUtils.createExhibitionAuthRefName(shortId, null);      
        PoxPayloadOut result = 
            ExhibitionAuthorityClientUtils.createExhibitionAuthorityInstance(
            displayName, shortId, commonPartName);
        return result;
    }
    
    @Override
    protected PoxPayloadOut createNonExistenceInstance(String commonPartName, String identifier) {
        String displayName = "displayName-NON_EXISTENT_ID";
        PoxPayloadOut result = ExhibitionAuthorityClientUtils.createExhibitionAuthorityInstance(
                    displayName, "nonEx", commonPartName);
        return result;
    }

    @Override
    protected ExhibitionauthoritiesCommon updateInstance(ExhibitionauthoritiesCommon exhibitionauthoritiesCommon) {
        ExhibitionauthoritiesCommon result = new ExhibitionauthoritiesCommon();
        
        result.setDisplayName("updated-" + exhibitionauthoritiesCommon.getDisplayName());
        result.setVocabType("updated-" + exhibitionauthoritiesCommon.getVocabType());
        
        return result;
    }

    @Override
    protected void compareUpdatedInstances(ExhibitionauthoritiesCommon original,
            ExhibitionauthoritiesCommon updated) throws Exception {
        Assert.assertEquals(updated.getDisplayName(),
                original.getDisplayName(),
                "Display name in updated object did not match submitted data.");
    }

    protected void compareReadInstances(ExhibitionauthoritiesCommon original,
            ExhibitionauthoritiesCommon fromRead) throws Exception {
        Assert.assertNotNull(fromRead.getDisplayName());
        Assert.assertNotNull(fromRead.getShortIdentifier());
        Assert.assertNotNull(fromRead.getRefName());
    }
    
    //
    // Authority item specific overrides
    //
    
    @Override
    protected String createItemInAuthority(String authorityId) {
        return createItemInAuthority(authorityId, null /*refname*/);
    }

    @Override
    protected ExhibitionsCommon updateItemInstance(ExhibitionsCommon exhibitionsCommon) {
                            
            ExhibitionTermGroupList termList = exhibitionsCommon.getExhibitionTermGroupList();
            Assert.assertNotNull(termList);
            List<ExhibitionTermGroup> terms = termList.getExhibitionTermGroup();
            Assert.assertNotNull(terms);
            Assert.assertTrue(terms.size() > 0);
            terms.get(0).setTermDisplayName("updated-" + terms.get(0).getTermDisplayName());
            terms.get(0).setTermName("updated-" + terms.get(0).getTermName());
            exhibitionsCommon.setExhibitionTermGroupList(termList);

            return exhibitionsCommon;
    }

    @Override
    protected void compareUpdatedItemInstances(ExhibitionsCommon original,
            ExhibitionsCommon updated) throws Exception {
            
            ExhibitionTermGroupList originalTermList = original.getExhibitionTermGroupList();
            Assert.assertNotNull(originalTermList);
            List<ExhibitionTermGroup> originalTerms = originalTermList.getExhibitionTermGroup();
            Assert.assertNotNull(originalTerms);
            Assert.assertTrue(originalTerms.size() > 0);
            
            ExhibitionTermGroupList updatedTermList = updated.getExhibitionTermGroupList();
            Assert.assertNotNull(updatedTermList);
            List<ExhibitionTermGroup> updatedTerms = updatedTermList.getExhibitionTermGroup();
            Assert.assertNotNull(updatedTerms);
            Assert.assertTrue(updatedTerms.size() > 0);
            
            Assert.assertEquals(updatedTerms.get(0).getTermDisplayName(),
                originalTerms.get(0).getTermDisplayName(),
                "Value in updated record did not match submitted data.");
    }

    @Override
    protected void verifyReadItemInstance(ExhibitionsCommon item)
            throws Exception {
        // TODO Auto-generated method stub
        
    }

    @Override
    protected PoxPayloadOut createNonExistenceItemInstance(
            String commonPartName, String identifier) {
        Map<String, String> nonexMap = new HashMap<String,String>();
        nonexMap.put(ExhibitionJAXBSchema.EXHIBITION_TERM_DISPLAY_NAME, TEST_EXHIBITION_TERM_DISPLAY_NAME);
        nonexMap.put(ExhibitionJAXBSchema.SHORT_IDENTIFIER, "nonEx");
        nonexMap.put(ExhibitionJAXBSchema.EXHIBITION_TERM_STATUS, TEST_EXHIBITION_TERM_STATUS);
        final String EMPTY_REFNAME = "";
        PoxPayloadOut result = 
                ExhibitionAuthorityClientUtils.createExhibitionInstance(EMPTY_REFNAME, nonexMap,
                ExhibitionAuthorityClientUtils.getTermGroupInstance(TEST_EXHIBITION_TERM_DISPLAY_NAME), commonPartName);
        return result;
    }
}