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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.collectionspace.services.PersonJAXBSchema;
import org.collectionspace.services.client.CollectionSpaceClient;
import org.collectionspace.services.client.ContactClient;
import org.collectionspace.services.client.ContactClientUtils;
import org.collectionspace.services.contact.ContactsCommon;
import org.collectionspace.services.contact.ContactsCommonList;
import org.collectionspace.services.client.PersonAuthorityClient;
import org.collectionspace.services.client.PersonAuthorityClientUtils;
import org.collectionspace.services.jaxb.AbstractCommonList;
import org.collectionspace.services.person.PersonauthoritiesCommon;
import org.collectionspace.services.person.PersonauthoritiesCommonList;
import org.collectionspace.services.person.PersonsCommon;
import org.collectionspace.services.person.PersonsCommonList;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.plugins.providers.multipart.MultipartInput;
import org.jboss.resteasy.plugins.providers.multipart.MultipartOutput;
import org.jboss.resteasy.plugins.providers.multipart.OutputPart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;

/**
 * PersonAuthorityServiceTest, carries out tests against a
 * deployed and running PersonAuthority Service.
 *
 * $LastChangedRevision: 753 $
 * $LastChangedDate: 2009-09-23 11:03:36 -0700 (Wed, 23 Sep 2009) $
 */
public class PersonAuthorityServiceTest extends AbstractServiceTestImpl {

    /** The logger. */
    private final String CLASS_NAME = PersonAuthorityServiceTest.class.getName();
    private final Logger logger = LoggerFactory.getLogger(CLASS_NAME);

    // Instance variables specific to this test.
    /** The service path component. */
    final String SERVICE_PATH_COMPONENT = "personauthorities";
    
    /** The item service path component. */
    final String ITEM_SERVICE_PATH_COMPONENT = "items";
    
    /** The contact service path component. */
    final String CONTACT_SERVICE_PATH_COMPONENT = "contacts";
    
    /** The test forename. */
    final String TEST_FORE_NAME = "John";
    
    /** The test middle name. */
    final String TEST_MIDDLE_NAME = null;
    
    /** The test surname. */
    final String TEST_SUR_NAME = "Wayne";
    
    /** The test birthdate. */
    final String TEST_BIRTH_DATE = "May 26, 1907";
    
    /** The test death date. */
    final String TEST_DEATH_DATE = "June 11, 1979";

    // Hold some values for a recently created item to verify upon read.
    private String knownResourceId = null;
    private String knownResourceShortIdentifer = null;
    private String knownResourceRefName = null;
    private String knownItemResourceId = null;
    private String knownItemResourceShortIdentifer = null;

    // The resource ID of an item resource used for partial term matching tests.
    private String knownItemPartialTermResourceId = null;
    
    /** The known contact resource id. */
    private String knownContactResourceId = null;
    
    /** The n items to create in list. */
    private int nItemsToCreateInList = 3;
    
    /** The all item resource ids created. */
    private Map<String, String> allItemResourceIdsCreated =
        new HashMap<String, String>();
    
    /** The all contact resource ids created. */
    private Map<String, String> allContactResourceIdsCreated =
        new HashMap<String, String>();

    protected void setKnownResource( String id, String shortIdentifer,
    		String refName ) {
    	knownResourceId = id;
    	knownResourceShortIdentifer = shortIdentifer;
    	knownResourceRefName = refName;
    }

    protected void setKnownItemResource( String id, String shortIdentifer ) {
    	knownItemResourceId = id;
    	knownItemResourceShortIdentifer = shortIdentifer;
    }

    
    /* (non-Javadoc)
     * @see org.collectionspace.services.client.test.BaseServiceTest#getClientInstance()
     */
    @Override
    protected CollectionSpaceClient getClientInstance() {
    	return new PersonAuthorityClient();
    }
    
    /* (non-Javadoc)
     * @see org.collectionspace.services.client.test.BaseServiceTest#getAbstractCommonList(org.jboss.resteasy.client.ClientResponse)
     */
    @Override
	protected AbstractCommonList getAbstractCommonList(
			ClientResponse<AbstractCommonList> response) {
        return response.getEntity(PersonsCommonList.class);
    }
 
    // ---------------------------------------------------------------
    // CRUD tests : CREATE tests
    // ---------------------------------------------------------------
    // Success outcomes
    /* (non-Javadoc)
     * @see org.collectionspace.services.client.test.ServiceTest#create(java.lang.String)
     */
    @Override
    @Test(dataProvider="testName", dataProviderClass=AbstractServiceTestImpl.class,
        groups = {"create"})
    public void create(String testName) throws Exception {
        
        if (logger.isDebugEnabled()) {
            logger.debug(testBanner(testName, CLASS_NAME));
        }
        // Perform setup, such as initializing the type of service request
        // (e.g. CREATE, DELETE), its valid and expected status codes, and
        // its associated HTTP method name (e.g. POST, DELETE).
        setupCreate();

        // Submit the request to the service and store the response.
        PersonAuthorityClient client = new PersonAuthorityClient();
        String shortId = createIdentifier();
    	String displayName = "displayName-" + shortId;
    	String baseRefName = PersonAuthorityClientUtils.createPersonAuthRefName(shortId, null);
    	MultipartOutput multipart = 
            PersonAuthorityClientUtils.createPersonAuthorityInstance(
    	    displayName, shortId, client.getCommonPartName());
        
    	String newID = null;
    	ClientResponse<Response> res = client.create(multipart);
    	try {
	        int statusCode = res.getStatus();
	
	        // Check the status code of the response: does it match
	        // the expected response(s)?
	        //
	        // Specifically:
	        // Does it fall within the set of valid status codes?
	        // Does it exactly match the expected status code?
	        if(logger.isDebugEnabled()){
	            logger.debug(testName + ": status = " + statusCode);
	        }
	        Assert.assertTrue(this.REQUEST_TYPE.isValidStatusCode(statusCode),
	                invalidStatusCodeMessage(this.REQUEST_TYPE, statusCode));
	        Assert.assertEquals(statusCode, this.EXPECTED_STATUS_CODE);
	
	        newID = PersonAuthorityClientUtils.extractId(res);
    	} finally {
    		res.releaseConnection();
    	}
        // Save values for additional tests
        if (knownResourceId == null){
        	setKnownResource( newID, shortId, baseRefName ); 
        	if (logger.isDebugEnabled()) {
                logger.debug(testName + ": knownResourceId=" + knownResourceId);
            }
        }
        // Store the IDs from every resource created by tests,
        // so they can be deleted after tests have been run.
        allResourceIdsCreated.add(newID);
    }

    /**
     * Creates the item.
     *
     * @param testName the test name
     */
    @Test(dataProvider="testName", dataProviderClass=AbstractServiceTestImpl.class,
        groups = {"create"}, dependsOnMethods = {"create"})
    public void createItem(String testName) {
        if (logger.isDebugEnabled()) {
            logger.debug(testBanner(testName, CLASS_NAME));
        }
        setupCreate();
        String newID = createItemInAuthority(knownResourceId, knownResourceRefName);
    }

    /**
     * Creates the item in authority.
     *
     * @param vcsid the vcsid
     * @param authRefName the auth ref name
     * @return the string
     */
    private String createItemInAuthority(String vcsid, String authRefName) {

        final String testName = "createItemInAuthority";
        if(logger.isDebugEnabled()){
            logger.debug(testName + ":...");
        }

        // Submit the request to the service and store the response.
        PersonAuthorityClient client = new PersonAuthorityClient();
        
        Map<String, String> johnWayneMap = new HashMap<String,String>();
        //
        // Fill the property map
        //
        String shortId = "johnWayneActor";
        johnWayneMap.put(PersonJAXBSchema.DISPLAY_NAME_COMPUTED, "false");
        johnWayneMap.put(PersonJAXBSchema.DISPLAY_NAME, "John Wayne");
        johnWayneMap.put(PersonJAXBSchema.SHORT_IDENTIFIER, shortId);
        
        johnWayneMap.put(PersonJAXBSchema.FORE_NAME, TEST_FORE_NAME);
        johnWayneMap.put(PersonJAXBSchema.SUR_NAME, TEST_SUR_NAME);
        johnWayneMap.put(PersonJAXBSchema.GENDER, "male");
        johnWayneMap.put(PersonJAXBSchema.BIRTH_DATE, TEST_BIRTH_DATE);
        johnWayneMap.put(PersonJAXBSchema.BIRTH_PLACE, "Winterset, Iowa");
        johnWayneMap.put(PersonJAXBSchema.DEATH_DATE, TEST_DEATH_DATE);
        johnWayneMap.put(PersonJAXBSchema.BIO_NOTE, "born Marion Robert Morrison and better" +
            "known by his stage name John Wayne, was an American film actor, director " +
            "and producer. He epitomized rugged masculinity and has become an enduring " +
            "American icon. He is famous for his distinctive voice, walk and height. " +
            "He was also known for his conservative political views and his support in " +
            "the 1950s for anti-communist positions.");
        MultipartOutput multipart = 
            PersonAuthorityClientUtils.createPersonInstance(vcsid, authRefName, johnWayneMap,
                client.getItemCommonPartName() );

        String newID = null;
        ClientResponse<Response> res = client.createItem(vcsid, multipart);
        try {
	        int statusCode = res.getStatus();
	        // Check the status code of the response: does it match
	        // the expected response(s)?
	        if(logger.isDebugEnabled()){
	            logger.debug(testName + ": status = " + statusCode);
	        }
	        Assert.assertTrue(REQUEST_TYPE.isValidStatusCode(statusCode),
	                invalidStatusCodeMessage(REQUEST_TYPE, statusCode));
	        Assert.assertEquals(statusCode, EXPECTED_STATUS_CODE);

	        newID = PersonAuthorityClientUtils.extractId(res);
        } finally {
        	res.releaseConnection();
        }

        // Store the ID returned from the first item resource created
        // for additional tests below.
        if (knownItemResourceId == null){
        	setKnownItemResource(newID, shortId);
            if (logger.isDebugEnabled()) {
                logger.debug(testName + ": knownItemResourceId=" + knownItemResourceId);                
            }
        }

        // Store the IDs from any item resources created
        // by tests, along with the IDs of their parents, so these items
        // can be deleted after all tests have been run.
        allItemResourceIdsCreated.put(newID, vcsid);

        return newID;
    }

    /**
     * Creates the contact.
     *
     * @param testName the test name
     */
    @Test(dataProvider="testName", dataProviderClass=AbstractServiceTestImpl.class,
        groups = {"create"}, dependsOnMethods = {"createItem"})
    public void createContact(String testName) {
        if (logger.isDebugEnabled()) {
            logger.debug(testBanner(testName, CLASS_NAME));
        }
        setupCreate();
        String newID = createContactInItem(knownResourceId, knownItemResourceId);
    }

   /**
    * Creates the contact in item.
    *
    * @param parentcsid the parentcsid
    * @param itemcsid the itemcsid
    * @return the string
    */
   private String createContactInItem(String parentcsid, String itemcsid) {

        final String testName = "createContactInItem";
        if(logger.isDebugEnabled()){
            logger.debug(testName + ":...");
        }
        
        setupCreate();

        // Submit the request to the service and store the response.
        PersonAuthorityClient client = new PersonAuthorityClient();
        String identifier = createIdentifier();
        MultipartOutput multipart =
            ContactClientUtils.createContactInstance(parentcsid,
            itemcsid, identifier, new ContactClient().getCommonPartName());
        
        String newID = null;
        ClientResponse<Response> res =
             client.createContact(parentcsid, itemcsid, multipart);
        try {
	        int statusCode = res.getStatus();
	        // Check the status code of the response: does it match
	        // the expected response(s)?
	        if(logger.isDebugEnabled()){
	            logger.debug(testName + ": status = " + statusCode);
	        }
	        Assert.assertTrue(REQUEST_TYPE.isValidStatusCode(statusCode),
	                invalidStatusCodeMessage(REQUEST_TYPE, statusCode));
	        Assert.assertEquals(statusCode, EXPECTED_STATUS_CODE);

	        newID = PersonAuthorityClientUtils.extractId(res);
        } finally {
        	res.releaseConnection();
        }

        // Store the ID returned from the first contact resource created
        // for additional tests below.
        if (knownContactResourceId == null){
            knownContactResourceId = newID;
            if (logger.isDebugEnabled()) {
                logger.debug(testName + ": knownContactResourceId=" + knownContactResourceId);
            }
        }

        // Store the IDs from any contact resources created
        // by tests, along with the IDs of their parent items,
        // so these items can be deleted after all tests have been run.
        allContactResourceIdsCreated.put(newID, itemcsid);

        return newID;
    }

    // Failure outcomes

    // Placeholders until the three tests below can be uncommented.
    // See Issue CSPACE-401.
    /* (non-Javadoc)
     * @see org.collectionspace.services.client.test.AbstractServiceTestImpl#createWithEmptyEntityBody(java.lang.String)
     */
    @Override
    public void createWithEmptyEntityBody(String testName) throws Exception {
    	//Should this really be empty?
    }

    /* (non-Javadoc)
     * @see org.collectionspace.services.client.test.AbstractServiceTestImpl#createWithMalformedXml(java.lang.String)
     */
    @Override
    public void createWithMalformedXml(String testName) throws Exception {
    	//Should this really be empty?
    }

    /* (non-Javadoc)
     * @see org.collectionspace.services.client.test.AbstractServiceTestImpl#createWithWrongXmlSchema(java.lang.String)
     */
    @Override
    public void createWithWrongXmlSchema(String testName) throws Exception {
    	//Should this really be empty?
    }

/*
    @Override
    @Test(dataProvider="testName", dataProviderClass=AbstractServiceTest.class,
        groups = {"create"}, dependsOnMethods = {"create", "testSubmitRequest"})
    public void createWithEmptyEntityBody(String testName) throws Exception {

        if (logger.isDebugEnabled()) {
            logger.debug(testBanner(testName, CLASS_NAME));
        }
        // Perform setup.
        setupCreateWithEmptyEntityBody();

        // Submit the request to the service and store the response.
        String method = REQUEST_TYPE.httpMethodName();
        String url = getServiceRootURL();
        String mediaType = MediaType.APPLICATION_XML;
        final String entity = "";
        int statusCode = submitRequest(method, url, mediaType, entity);

        // Check the status code of the response: does it match
        // the expected response(s)?
        if(logger.isDebugEnabled()) {
            logger.debug(testName + ": url=" + url +
                " status=" + statusCode);
         }
        Assert.assertTrue(REQUEST_TYPE.isValidStatusCode(statusCode),
        invalidStatusCodeMessage(REQUEST_TYPE, statusCode));
        Assert.assertEquals(statusCode, EXPECTED_STATUS_CODE);
    }

    @Override
    @Test(dataProvider="testName", dataProviderClass=AbstractServiceTest.class,
        groups = {"create"}, dependsOnMethods = {"create", "testSubmitRequest"})
    public void createWithMalformedXml(String testName) throws Exception {

        if (logger.isDebugEnabled()) {
            logger.debug(testBanner(testName, CLASS_NAME));
        }
        // Perform setup.
        setupCreateWithMalformedXml();

        // Submit the request to the service and store the response.
        String method = REQUEST_TYPE.httpMethodName();
        String url = getServiceRootURL();
        String mediaType = MediaType.APPLICATION_XML;
        final String entity = MALFORMED_XML_DATA; // Constant from base class.
        int statusCode = submitRequest(method, url, mediaType, entity);

        // Check the status code of the response: does it match
        // the expected response(s)?
        if(logger.isDebugEnabled()){
            logger.debug(testName + ": url=" + url +
                " status=" + statusCode);
         }
        Assert.assertTrue(REQUEST_TYPE.isValidStatusCode(statusCode),
        invalidStatusCodeMessage(REQUEST_TYPE, statusCode));
        Assert.assertEquals(statusCode, EXPECTED_STATUS_CODE);
    }

    @Override
    @Test(dataProvider="testName", dataProviderClass=AbstractServiceTest.class,
        groups = {"create"}, dependsOnMethods = {"create", "testSubmitRequest"})
    public void createWithWrongXmlSchema(String testName) throws Exception {

        if (logger.isDebugEnabled()) {
            logger.debug(testBanner(testName, CLASS_NAME));
        }
        // Perform setup.
        setupCreateWithWrongXmlSchema();

        // Submit the request to the service and store the response.
        String method = REQUEST_TYPE.httpMethodName();
        String url = getServiceRootURL();
        String mediaType = MediaType.APPLICATION_XML;
        final String entity = WRONG_XML_SCHEMA_DATA;
        int statusCode = submitRequest(method, url, mediaType, entity);

        // Check the status code of the response: does it match
        // the expected response(s)?
        if(logger.isDebugEnabled()){
            logger.debug(testName + ": url=" + url +
                " status=" + statusCode);
         }
        Assert.assertTrue(REQUEST_TYPE.isValidStatusCode(statusCode),
        invalidStatusCodeMessage(REQUEST_TYPE, statusCode));
        Assert.assertEquals(statusCode, EXPECTED_STATUS_CODE);
    }
*/

    // ---------------------------------------------------------------
    // CRUD tests : CREATE LIST tests
    // ---------------------------------------------------------------
    // Success outcomes
    /* (non-Javadoc)
 * @see org.collectionspace.services.client.test.AbstractServiceTestImpl#createList(java.lang.String)
 */
    @Override
    @Test(dataProvider="testName", dataProviderClass=AbstractServiceTestImpl.class,
        groups = {"createList"}, dependsOnGroups = {"create"})
    public void createList(String testName) throws Exception {
         if (logger.isDebugEnabled()) {
            logger.debug(testBanner(testName, CLASS_NAME));
        }
        for (int i = 0; i < nItemsToCreateInList; i++) {
            create(testName);
        }
    }

    /**
     * Creates the item list.
     *
     * @param testName the test name
     * @throws Exception the exception
     */
    @Test(dataProvider="testName", dataProviderClass=AbstractServiceTestImpl.class,
        groups = {"createList"}, dependsOnMethods = {"createList"})
    public void createItemList(String testName) throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug(testBanner(testName, CLASS_NAME));
        }
        // Add items to the initially-created, known parent record.
        for (int j = 0; j < nItemsToCreateInList; j++) {
            createItem(testName);
        }
    }

    /**
     * Creates the contact list.
     *
     * @param testName the test name
     * @throws Exception the exception
     */
    @Test(dataProvider="testName", dataProviderClass=AbstractServiceTestImpl.class,
        groups = {"createList"}, dependsOnMethods = {"createItemList"})
    public void createContactList(String testName) throws Exception {
        // Add contacts to the initially-created, known item record.
        for (int j = 0; j < nItemsToCreateInList; j++) {
            createContact(testName);
        }
    }

    // ---------------------------------------------------------------
    // CRUD tests : READ tests
    // ---------------------------------------------------------------
    // Success outcomes
    /* (non-Javadoc)
     * @see org.collectionspace.services.client.test.AbstractServiceTestImpl#read(java.lang.String)
     */
    @Override
    @Test(dataProvider="testName", dataProviderClass=AbstractServiceTestImpl.class,
        groups = {"read"}, dependsOnGroups = {"create"})
    public void read(String testName) throws Exception {
    	readInternal(testName, knownResourceId, null);
    }

    /**
     * Read by name.
     *
     * @param testName the test name
     * @throws Exception the exception
     */
    @Test(dataProvider="testName", dataProviderClass=AbstractServiceTestImpl.class,
            groups = {"read"}, dependsOnGroups = {"create"})
        public void readByName(String testName) throws Exception {
    	readInternal(testName, null, knownResourceShortIdentifer);
    }
    
    protected void readInternal(String testName, String CSID, String shortId) {
        if (logger.isDebugEnabled()) {
            logger.debug(testBanner(testName, CLASS_NAME));
        }
        // Perform setup.
        setupRead();

        // Submit the request to the service and store the response.
        PersonAuthorityClient client = new PersonAuthorityClient();
        ClientResponse<MultipartInput> res = null;
        if(CSID!=null) {
            res = client.read(CSID);
        } else if(shortId!=null) {
        	res = client.readByName(shortId);
        } else {
        	Assert.fail("readInternal: Internal error. One of CSID or shortId must be non-null");
        }
        try {
	        int statusCode = res.getStatus();
	        // Check the status code of the response: does it match
	        // the expected response(s)?
	        if(logger.isDebugEnabled()){
	            logger.debug(testName + ": status = " + statusCode);
	        }
	        Assert.assertTrue(REQUEST_TYPE.isValidStatusCode(statusCode),
	                invalidStatusCodeMessage(REQUEST_TYPE, statusCode));
	        Assert.assertEquals(statusCode, EXPECTED_STATUS_CODE);
	        //FIXME: remove the following try catch once Aron fixes signatures
	        try {
	            MultipartInput input = (MultipartInput) res.getEntity();
	            PersonauthoritiesCommon personAuthority = (PersonauthoritiesCommon) extractPart(input,
	                    client.getCommonPartName(), PersonauthoritiesCommon.class);
	            Assert.assertNotNull(personAuthority);
	        } catch (Exception e) {
	            throw new RuntimeException(e);
	        }
        } finally {
        	res.releaseConnection();
        }
    }

    /**
     * Read item.
     *
     * @param testName the test name
     * @throws Exception the exception
     */
    @Test(dataProvider="testName", dataProviderClass=AbstractServiceTestImpl.class,
    		groups = {"readItem"}, dependsOnGroups = {"read"})
    public void readItem(String testName) throws Exception {
        readItemInternal(testName, knownResourceId, null, knownItemResourceId, null);
    }

    /**
     * Read item in Named Auth.
     *
     * @param testName the test name
     * @throws Exception the exception
     */
    @Test(dataProvider="testName", dataProviderClass=AbstractServiceTestImpl.class,
    		groups = {"readItem"}, dependsOnGroups = {"read"})
    public void readItemInNamedAuth(String testName) throws Exception {
        readItemInternal(testName, null, knownResourceShortIdentifer, knownItemResourceId, null);
    }

    /**
     * Read named item.
     *
     * @param testName the test name
     * @throws Exception the exception
     */
    @Test(dataProvider="testName", dataProviderClass=AbstractServiceTestImpl.class,
    		groups = {"readItem"}, dependsOnGroups = {"read"})
    public void readNamedItem(String testName) throws Exception {
        readItemInternal(testName, knownResourceId, null, null, knownItemResourceShortIdentifer);
    }

    /**
     * Read Named item in Named Auth.
     *
     * @param testName the test name
     * @throws Exception the exception
     */
    @Test(dataProvider="testName", dataProviderClass=AbstractServiceTestImpl.class,
    		groups = {"readItem"}, dependsOnGroups = {"read"})
    public void readNamedItemInNamedAuth(String testName) throws Exception {
        readItemInternal(testName, null, knownResourceShortIdentifer, null, knownItemResourceShortIdentifer);
    }

    protected void readItemInternal(String testName, 
    		String authCSID, String authShortId, String itemCSID, String itemShortId) 
    	throws Exception {

        if (logger.isDebugEnabled()) {
            logger.debug(testBanner(testName, CLASS_NAME));
        }
        // Perform setup.
        setupRead();

        // Submit the request to the service and store the response.
        PersonAuthorityClient client = new PersonAuthorityClient();
        ClientResponse<MultipartInput> res = null;
        if(authCSID!=null) {
            if(itemCSID!=null) {
                res = client.readItem(authCSID, itemCSID);
            } else if(itemShortId!=null) {
            	res = client.readNamedItem(authCSID, itemShortId);
            } else {
            	Assert.fail("readInternal: Internal error. One of CSID or shortId must be non-null");
            }
        } else if(authShortId!=null) {
            if(itemCSID!=null) {
                res = client.readItemInNamedAuthority(authShortId, itemCSID);
            } else if(itemShortId!=null) {
            	res = client.readNamedItemInNamedAuthority(authShortId, itemShortId);
            } else {
            	Assert.fail("readInternal: Internal error. One of CSID or shortId must be non-null");
            }
        } else {
        	Assert.fail("readInternal: Internal error. One of authCSID or authShortId must be non-null");
        }
        try {
	        int statusCode = res.getStatus();
	
	        // Check the status code of the response: does it match
	        // the expected response(s)?
	        if(logger.isDebugEnabled()){
	            logger.debug(testName + ": status = " + statusCode);
	        }
	        Assert.assertTrue(REQUEST_TYPE.isValidStatusCode(statusCode),
	                invalidStatusCodeMessage(REQUEST_TYPE, statusCode));
	        Assert.assertEquals(statusCode, EXPECTED_STATUS_CODE);
	
	        // Check whether we've received a person.
	        MultipartInput input = (MultipartInput) res.getEntity();
	        PersonsCommon person = (PersonsCommon) extractPart(input,
	                client.getItemCommonPartName(), PersonsCommon.class);
	        Assert.assertNotNull(person);
	        boolean showFull = true;
	        if(showFull && logger.isDebugEnabled()){
	            logger.debug(testName + ": returned payload:");
	            logger.debug(objectAsXmlString(person, PersonsCommon.class));
	        }
	        Assert.assertEquals(person.getInAuthority(), knownResourceId);
        } finally {
        	res.releaseConnection();
        }
    }

    /**
     * Verify item display name.
     *
     * @param testName the test name
     * @throws Exception the exception
     */
    @Test(dataProvider="testName", dataProviderClass=AbstractServiceTestImpl.class,
        dependsOnMethods = {"readItem", "updateItem"})
    public void verifyItemDisplayName(String testName) throws Exception {
        
         if (logger.isDebugEnabled()) {
            logger.debug(testBanner(testName, CLASS_NAME));
        }
        // Perform setup.
        setupUpdate();

        // Submit the request to the service and store the response.
        PersonAuthorityClient client = new PersonAuthorityClient();
        MultipartInput input =null;
        ClientResponse<MultipartInput> res = client.readItem(knownResourceId, knownItemResourceId);
        try {
	        int statusCode = res.getStatus();
	
	        // Check the status code of the response: does it match
	        // the expected response(s)?
	        if(logger.isDebugEnabled()){
	            logger.debug(testName + ": status = " + statusCode);
	        }
	        Assert.assertTrue(REQUEST_TYPE.isValidStatusCode(statusCode),
	                invalidStatusCodeMessage(REQUEST_TYPE, statusCode));
	        Assert.assertEquals(statusCode, EXPECTED_STATUS_CODE);
	
	        // Check whether person has expected displayName.
	        input = (MultipartInput) res.getEntity();
        } finally {
        	res.releaseConnection();
        }
	        
        PersonsCommon person = (PersonsCommon) extractPart(input,
                client.getItemCommonPartName(), PersonsCommon.class);
        Assert.assertNotNull(person);
        String displayName = person.getDisplayName();
        // Make sure displayName matches computed form
        String expectedDisplayName = 
            PersonAuthorityClientUtils.prepareDefaultDisplayName(
	        TEST_FORE_NAME, null, TEST_SUR_NAME,
	        TEST_BIRTH_DATE, TEST_DEATH_DATE);
        Assert.assertNotNull(displayName, expectedDisplayName);
    
        // Update the shortName and verify the computed name is updated.
        person.setCsid(null);
        person.setDisplayNameComputed(true);
        person.setForeName("updated-" + TEST_FORE_NAME);
        expectedDisplayName = 
            PersonAuthorityClientUtils.prepareDefaultDisplayName(
        	"updated-" + TEST_FORE_NAME, null, TEST_SUR_NAME, 
	       	TEST_BIRTH_DATE, TEST_DEATH_DATE);

        // Submit the updated resource to the service and store the response.
        MultipartOutput output = new MultipartOutput();
        OutputPart commonPart = output.addPart(person, MediaType.APPLICATION_XML_TYPE);
        commonPart.getHeaders().add("label", client.getItemCommonPartName());
        res = client.updateItem(knownResourceId, knownItemResourceId, output);
        try {
	        int statusCode = res.getStatus();
	
	        // Check the status code of the response: does it match the expected response(s)?
	        if(logger.isDebugEnabled()){
	            logger.debug("updateItem: status = " + statusCode);
	        }
	        Assert.assertTrue(REQUEST_TYPE.isValidStatusCode(statusCode),
	                invalidStatusCodeMessage(REQUEST_TYPE, statusCode));
	        Assert.assertEquals(statusCode, EXPECTED_STATUS_CODE);
	
	        // Retrieve the updated resource and verify that its contents exist.
	        input = (MultipartInput) res.getEntity();
        } finally {
        	res.releaseConnection();
        }
        
        PersonsCommon updatedPerson =
                (PersonsCommon) extractPart(input,
                        client.getItemCommonPartName(), PersonsCommon.class);
        Assert.assertNotNull(updatedPerson);

        // Verify that the updated resource received the correct data.
        Assert.assertEquals(updatedPerson.getForeName(), person.getForeName(),
            "Updated ForeName in Person did not match submitted data.");
        // Verify that the updated resource computes the right displayName.
        Assert.assertEquals(updatedPerson.getDisplayName(), expectedDisplayName,
            "Updated ForeName in Person not reflected in computed DisplayName.");

        // Now Update the displayName, not computed and verify the computed name is overriden.
        person.setDisplayNameComputed(false);
        expectedDisplayName = "TestName";
        person.setDisplayName(expectedDisplayName);

        // Submit the updated resource to the service and store the response.
        output = new MultipartOutput();
        commonPart = output.addPart(person, MediaType.APPLICATION_XML_TYPE);
        commonPart.getHeaders().add("label", client.getItemCommonPartName());
        res = client.updateItem(knownResourceId, knownItemResourceId, output);
        try {
	        int statusCode = res.getStatus();
	
	        // Check the status code of the response: does it match the expected response(s)?
	        if(logger.isDebugEnabled()){
	            logger.debug("updateItem: status = " + statusCode);
	        }
	        Assert.assertTrue(REQUEST_TYPE.isValidStatusCode(statusCode),
	                invalidStatusCodeMessage(REQUEST_TYPE, statusCode));
	        Assert.assertEquals(statusCode, EXPECTED_STATUS_CODE);
	
	        // Retrieve the updated resource and verify that its contents exist.
	        input = (MultipartInput) res.getEntity();
        } finally {
        	res.releaseConnection();
        }
        
        updatedPerson =
                (PersonsCommon) extractPart(input,
                        client.getItemCommonPartName(), PersonsCommon.class);
        Assert.assertNotNull(updatedPerson);

        // Verify that the updated resource received the correct data.
        Assert.assertEquals(updatedPerson.isDisplayNameComputed(), false,
                "Updated displayNameComputed in Person did not match submitted data.");
        // Verify that the updated resource computes the right displayName.
        Assert.assertEquals(updatedPerson.getDisplayName(),
        		expectedDisplayName,
                "Updated DisplayName (not computed) in Person not stored.");
    }

    /**
     * Verify illegal item display name.
     *
     * @param testName the test name
     * @throws Exception the exception
     */
    @Test(dataProvider="testName", dataProviderClass=AbstractServiceTestImpl.class,
            dependsOnMethods = {"verifyItemDisplayName"})
    public void verifyIllegalItemDisplayName(String testName) throws Exception {

        if (logger.isDebugEnabled()) {
            logger.debug(testBanner(testName, CLASS_NAME));
        }
        // Perform setup.
        testSetup(STATUS_BAD_REQUEST, ServiceRequestType.UPDATE);
    	// setupUpdateWithWrongXmlSchema(testName);

        // Submit the request to the service and store the response.
        PersonAuthorityClient client = new PersonAuthorityClient();
        MultipartInput input = null;
        ClientResponse<MultipartInput> res = client.readItem(knownResourceId, knownItemResourceId);
        try {
	        int statusCode = res.getStatus();
	
	        // Check the status code of the response: does it match
	        // the expected response(s)?
	        if(logger.isDebugEnabled()){
	            logger.debug(testName + ": status = " + statusCode);
	        }
	        Assert.assertTrue(REQUEST_TYPE.isValidStatusCode(statusCode),
	                invalidStatusCodeMessage(REQUEST_TYPE, statusCode));
	        Assert.assertEquals(statusCode, Response.Status.OK.getStatusCode());
	
	        // Check whether Person has expected displayName.
	        input = (MultipartInput) res.getEntity();
        } finally {
        	res.releaseConnection();
        }
        
        PersonsCommon person = (PersonsCommon) extractPart(input,
                client.getItemCommonPartName(), PersonsCommon.class);
        Assert.assertNotNull(person);
        // Try to Update with computed false and no displayName
        person.setDisplayNameComputed(false);
        person.setDisplayName(null);

        // Submit the updated resource to the service and store the response.
        MultipartOutput output = new MultipartOutput();
        OutputPart commonPart = output.addPart(person, MediaType.APPLICATION_XML_TYPE);
        commonPart.getHeaders().add("label", client.getItemCommonPartName());
        res = client.updateItem(knownResourceId, knownItemResourceId, output);
        try {
	        int statusCode = res.getStatus();
	
	        // Check the status code of the response: does it match the expected response(s)?
	        if(logger.isDebugEnabled()){
	            logger.debug("updateItem: status = " + statusCode);
	        }
	        Assert.assertTrue(REQUEST_TYPE.isValidStatusCode(statusCode),
	                invalidStatusCodeMessage(REQUEST_TYPE, statusCode));
	        Assert.assertEquals(statusCode, EXPECTED_STATUS_CODE);
        } finally {
        	res.releaseConnection();
        }
    }
    
    /**
     * Read contact.
     *
     * @param testName the test name
     * @throws Exception the exception
     */
    @Test(dataProvider="testName", dataProviderClass=AbstractServiceTestImpl.class,
        groups = {"readItem"}, dependsOnMethods = {"readItem"})
    public void readContact(String testName) throws Exception {
        
        if (logger.isDebugEnabled()) {
            logger.debug(testBanner(testName, CLASS_NAME));
        }
        // Perform setup.
        setupRead();

        // Submit the request to the service and store the response.
        PersonAuthorityClient client = new PersonAuthorityClient();
        MultipartInput input = null;
        ClientResponse<MultipartInput> res =
            client.readContact(knownResourceId, knownItemResourceId,
            knownContactResourceId);
        try {
	        int statusCode = res.getStatus();
	
	        // Check the status code of the response: does it match
	        // the expected response(s)?
	        if(logger.isDebugEnabled()){
	            logger.debug(testName + ": status = " + statusCode);
	        }
	        Assert.assertTrue(REQUEST_TYPE.isValidStatusCode(statusCode),
	                invalidStatusCodeMessage(REQUEST_TYPE, statusCode));
	        Assert.assertEquals(statusCode, EXPECTED_STATUS_CODE);
	
	        // Check whether we've received a contact.
	        input = (MultipartInput) res.getEntity();
        } finally {
        	res.releaseConnection();
        }
        
        ContactsCommon contact = (ContactsCommon) extractPart(input,
                new ContactClient().getCommonPartName(), ContactsCommon.class);
        Assert.assertNotNull(contact);
        boolean showFull = true;
        if(showFull && logger.isDebugEnabled()){
            logger.debug(testName + ": returned payload:");
            logger.debug(objectAsXmlString(contact, ContactsCommon.class));
        }
        Assert.assertEquals(contact.getInAuthority(), knownResourceId);
        Assert.assertEquals(contact.getInItem(), knownItemResourceId);

    }

    // Failure outcomes
    /* (non-Javadoc)
     * @see org.collectionspace.services.client.test.AbstractServiceTestImpl#readNonExistent(java.lang.String)
     */
    @Override
    @Test(dataProvider="testName", dataProviderClass=AbstractServiceTestImpl.class,
        groups = {"read"}, dependsOnMethods = {"read"})
    public void readNonExistent(String testName) {

        if (logger.isDebugEnabled()) {
            logger.debug(testBanner(testName, CLASS_NAME));
        }
        // Perform setup.
        setupReadNonExistent();

        // Submit the request to the service and store the response.
        PersonAuthorityClient client = new PersonAuthorityClient();
        ClientResponse<MultipartInput> res = client.read(NON_EXISTENT_ID);
        try {
        	int statusCode = res.getStatus();
	        // Check the status code of the response: does it match
	        // the expected response(s)?
	        if(logger.isDebugEnabled()){
	            logger.debug(testName + ": status = " + statusCode);
	        }
	        Assert.assertTrue(REQUEST_TYPE.isValidStatusCode(statusCode),
	                invalidStatusCodeMessage(REQUEST_TYPE, statusCode));
	        Assert.assertEquals(statusCode, EXPECTED_STATUS_CODE);
        } finally {
        	res.releaseConnection();
        }
    }

    /**
     * Read item non existent.
     *
     * @param testName the test name
     */
    @Test(dataProvider="testName", dataProviderClass=AbstractServiceTestImpl.class,
        groups = {"readItem"}, dependsOnMethods = {"readItem"})
    public void readItemNonExistent(String testName) {

        if (logger.isDebugEnabled()) {
            logger.debug(testBanner(testName, CLASS_NAME));
        }
        // Perform setup.
        setupReadNonExistent();

        // Submit the request to the service and store the response.
        PersonAuthorityClient client = new PersonAuthorityClient();
        ClientResponse<MultipartInput> res = client.readItem(knownResourceId, NON_EXISTENT_ID);
        try {
	        int statusCode = res.getStatus();
	
	        // Check the status code of the response: does it match
	        // the expected response(s)?
	        if(logger.isDebugEnabled()){
	            logger.debug(testName + ": status = " + statusCode);
	        }
	        Assert.assertTrue(REQUEST_TYPE.isValidStatusCode(statusCode),
	                invalidStatusCodeMessage(REQUEST_TYPE, statusCode));
	        Assert.assertEquals(statusCode, EXPECTED_STATUS_CODE);
        } finally {
        	res.releaseConnection();
        }
    }

    /**
     * Read contact non existent.
     *
     * @param testName the test name
     */
    @Test(dataProvider="testName", dataProviderClass=AbstractServiceTestImpl.class,
        groups = {"readItem"}, dependsOnMethods = {"readContact"})
    public void readContactNonExistent(String testName) {

        if (logger.isDebugEnabled()) {
            logger.debug(testBanner(testName, CLASS_NAME));
        }
        // Perform setup.
        setupReadNonExistent();

        // Submit the request to the service and store the response.
        PersonAuthorityClient client = new PersonAuthorityClient();
        ClientResponse<MultipartInput> res =
            client.readContact(knownResourceId, knownItemResourceId, NON_EXISTENT_ID);
        try {
	        int statusCode = res.getStatus();
	
	        // Check the status code of the response: does it match
	        // the expected response(s)?
	        if(logger.isDebugEnabled()){
	            logger.debug(testName + ": status = " + statusCode);
	        }
	        Assert.assertTrue(REQUEST_TYPE.isValidStatusCode(statusCode),
	                invalidStatusCodeMessage(REQUEST_TYPE, statusCode));
	        Assert.assertEquals(statusCode, EXPECTED_STATUS_CODE);
        } finally {
        	res.releaseConnection();
        }
    }

    // ---------------------------------------------------------------
    // CRUD tests : READ_LIST tests
    // ---------------------------------------------------------------
    // Success outcomes

    /* (non-Javadoc)
     * @see org.collectionspace.services.client.test.AbstractServiceTestImpl#readList(java.lang.String)
     */
    @Override
    @Test(dataProvider="testName", dataProviderClass=AbstractServiceTestImpl.class,
        groups = {"readList"}, dependsOnGroups = {"createList", "read"})
    public void readList(String testName) throws Exception {

        if (logger.isDebugEnabled()) {
            logger.debug(testBanner(testName, CLASS_NAME));
        }
        // Perform setup.
        setupReadList();

        // Submit the request to the service and store the response.
        PersonAuthorityClient client = new PersonAuthorityClient();
        PersonauthoritiesCommonList list = null;
        ClientResponse<PersonauthoritiesCommonList> res = client.readList();
        try {
	        int statusCode = res.getStatus();
	
	        // Check the status code of the response: does it match
	        // the expected response(s)?
	        if(logger.isDebugEnabled()){
	            logger.debug(testName + ": status = " + statusCode);
	        }
	        Assert.assertTrue(REQUEST_TYPE.isValidStatusCode(statusCode),
	                invalidStatusCodeMessage(REQUEST_TYPE, statusCode));
	        Assert.assertEquals(statusCode, EXPECTED_STATUS_CODE);
	
	        list = res.getEntity();
        } finally {
        	res.releaseConnection();
        }

        // Optionally output additional data about list members for debugging.
        boolean iterateThroughList = false;
        if (iterateThroughList && logger.isDebugEnabled()) {
            List<PersonauthoritiesCommonList.PersonauthorityListItem> items =
                    list.getPersonauthorityListItem();
            int i = 0;
            for (PersonauthoritiesCommonList.PersonauthorityListItem item : items) {
                String csid = item.getCsid();
                logger.debug(testName + ": list-item[" + i + "] csid=" +
                        csid);
                logger.debug(testName + ": list-item[" + i + "] displayName=" +
                        item.getDisplayName());
                logger.debug(testName + ": list-item[" + i + "] URI=" +
                        item.getUri());
                readItemList(csid, null, testName);
                i++;
            }
        }
    }

    /**
     * Read item list.
     */
    @Test(dataProvider="testName", dataProviderClass=AbstractServiceTestImpl.class,
    		groups = {"readList"}, dependsOnMethods = {"readList"})
    public void readItemList(String testName) {
        readItemList(knownResourceId, null, testName);
    }

    /**
     * Read item list by authority name.
     */
    @Test(dataProvider="testName", dataProviderClass=AbstractServiceTestImpl.class,
    		groups = {"readList"}, dependsOnMethods = {"readItemList"})
    public void readItemListByAuthorityName(String testName) {
        readItemList(null, knownResourceShortIdentifer, testName);
    }
    
    /**
     * Read item list.
     *
     * @param vcsid the vcsid
     * @param name the name
     */
    private void readItemList(String vcsid, String name, String testName) {

        // Perform setup.
        setupReadList();
        
	// Submit the request to the service and store the response.
        PersonAuthorityClient client = new PersonAuthorityClient();
        ClientResponse<PersonsCommonList> res = null;
        if (vcsid!= null) {
	        res = client.readItemList(vcsid);
        } else if (name!= null) {
   	        res = client.readItemListForNamedAuthority(name);
        } else {
        	Assert.fail("readItemList passed null csid and name!");
        }
        PersonsCommonList list = null;
        try {
	        int statusCode = res.getStatus();
	
	        // Check the status code of the response: does it match
	        // the expected response(s)?
	        if(logger.isDebugEnabled()){
	            logger.debug(testName + ": status = " + statusCode);
	        }
	        Assert.assertTrue(REQUEST_TYPE.isValidStatusCode(statusCode),
	                invalidStatusCodeMessage(REQUEST_TYPE, statusCode));
	        Assert.assertEquals(statusCode, EXPECTED_STATUS_CODE);

	        list = res.getEntity();
        } finally {
        	res.releaseConnection();
        }

        List<PersonsCommonList.PersonListItem> items =
            list.getPersonListItem();
        int nItemsReturned = items.size();
        // There will be one item created, associated with a
        // known parent resource, by the createItem test.
        //
        // In addition, there will be 'nItemsToCreateInList'
        // additional items created by the createItemList test,
        // all associated with the same parent resource.
        int nExpectedItems = nItemsToCreateInList + 1;
        if(logger.isDebugEnabled()){
            logger.debug(testName + ": Expected "
           		+ nExpectedItems +" items; got: "+nItemsReturned);
        }
        Assert.assertEquals(nItemsReturned, nExpectedItems);

        int i = 0;
        for (PersonsCommonList.PersonListItem item : items) {
        	Assert.assertTrue((null != item.getRefName()), "Item refName is null!");
        	Assert.assertTrue((null != item.getDisplayName()), "Item displayName is null!");
        	// Optionally output additional data about list members for debugging.
	        boolean showDetails = true;
	        if (showDetails && logger.isDebugEnabled()) {
                logger.debug("  " + testName + ": list-item[" + i + "] csid=" +
                        item.getCsid());
                logger.debug("  " + testName + ": list-item[" + i + "] refName=" +
                        item.getRefName());
                logger.debug("  " + testName + ": list-item[" + i + "] displayName=" +
                        item.getDisplayName());
                logger.debug("  " + testName + ": list-item[" + i + "] URI=" +
                        item.getUri());
            }
            i++;
        }
    }

    /**
     * Read contact list.
     */
    @Test(groups = {"readList"}, dependsOnMethods = {"readItemList"})
    public void readContactList() {
        readContactList(knownResourceId, knownItemResourceId);
    }

    /**
     * Read contact list.
     *
     * @param parentcsid the parentcsid
     * @param itemcsid the itemcsid
     */
    private void readContactList(String parentcsid, String itemcsid) {
        final String testName = "readContactList";

        // Perform setup.
        setupReadList();

        // Submit the request to the service and store the response.
        PersonAuthorityClient client = new PersonAuthorityClient();
        ContactsCommonList list = null;
        ClientResponse<ContactsCommonList> res =
                client.readContactList(parentcsid, itemcsid);
        try {
	        int statusCode = res.getStatus();
	
	        // Check the status code of the response: does it match
	        // the expected response(s)?
	        if(logger.isDebugEnabled()){
	            logger.debug(testName + ": status = " + statusCode);
	        }
	        Assert.assertTrue(REQUEST_TYPE.isValidStatusCode(statusCode),
	                invalidStatusCodeMessage(REQUEST_TYPE, statusCode));
	        Assert.assertEquals(statusCode, EXPECTED_STATUS_CODE);
	
	        list = res.getEntity();
        } finally {
        	res.releaseConnection();
        }
        
        List<ContactsCommonList.ContactListItem> listitems =
            list.getContactListItem();
        int nItemsReturned = listitems.size();
        // There will be one item created, associated with a
        // known parent resource, by the createItem test.
        //
        // In addition, there will be 'nItemsToCreateInList'
        // additional items created by the createItemList test,
        // all associated with the same parent resource.
        int nExpectedItems = nItemsToCreateInList + 1;
        if(logger.isDebugEnabled()){
            logger.debug(testName + ": Expected "
           		+ nExpectedItems +" items; got: "+nItemsReturned);
        }
        Assert.assertEquals(nItemsReturned, nExpectedItems);

        int i = 0;
        for (ContactsCommonList.ContactListItem listitem : listitems) {
        	// Optionally output additional data about list members for debugging.
	        boolean showDetails = false;
	        if (showDetails && logger.isDebugEnabled()) {
                logger.debug("  " + testName + ": list-item[" + i + "] csid=" +
                        listitem.getCsid());
                logger.debug("  " + testName + ": list-item[" + i + "] addressPlace=" +
                        listitem.getAddressPlace());
                logger.debug("  " + testName + ": list-item[" + i + "] URI=" +
                        listitem.getUri());
            }
            i++;
        }
    }

    // Failure outcomes

    // There are no failure outcome tests at present.

    // ---------------------------------------------------------------
    // CRUD tests : UPDATE tests
    // ---------------------------------------------------------------
    // Success outcomes
    /* (non-Javadoc)
     * @see org.collectionspace.services.client.test.AbstractServiceTestImpl#update(java.lang.String)
     */
    @Override
    @Test(dataProvider="testName", dataProviderClass=AbstractServiceTestImpl.class,
        groups = {"update"}, dependsOnGroups = {"read", "readList", "readListByPartialTerm"})
    public void update(String testName) throws Exception {

        if (logger.isDebugEnabled()) {
            logger.debug(testBanner(testName, CLASS_NAME));
        }
        // Perform setup.
        setupUpdate();

        // Retrieve the contents of a resource to update.
        PersonAuthorityClient client = new PersonAuthorityClient();
        MultipartInput input = null;
        ClientResponse<MultipartInput> res = client.read(knownResourceId);
        try {
	        if(logger.isDebugEnabled()){
	            logger.debug(testName + ": read status = " + res.getStatus());
	        }
	        Assert.assertEquals(res.getStatus(), EXPECTED_STATUS_CODE);
	
	        if(logger.isDebugEnabled()){
	            logger.debug("got PersonAuthority to update with ID: " + knownResourceId);
	        }
	        input = res.getEntity();
        } finally {
        	res.releaseConnection();
        }
        
        PersonauthoritiesCommon personAuthority = (PersonauthoritiesCommon) extractPart(input,
                client.getCommonPartName(), PersonauthoritiesCommon.class);
        Assert.assertNotNull(personAuthority);

        // Update the contents of this resource.
        personAuthority.setDisplayName("updated-" + personAuthority.getDisplayName());
        personAuthority.setVocabType("updated-" + personAuthority.getVocabType());
        if(logger.isDebugEnabled()){
            logger.debug("to be updated PersonAuthority");
            logger.debug(objectAsXmlString(personAuthority, PersonauthoritiesCommon.class));
        }

        // Submit the updated resource to the service and store the response.
        MultipartOutput output = new MultipartOutput();
        OutputPart commonPart = output.addPart(personAuthority, MediaType.APPLICATION_XML_TYPE);
        commonPart.getHeaders().add("label", client.getCommonPartName());
        res = client.update(knownResourceId, output);
        try {
	        int statusCode = res.getStatus();
	
	        // Check the status code of the response: does it match the expected response(s)?
	        if(logger.isDebugEnabled()){
	            logger.debug(testName + ": status = " + statusCode);
	        }
	        Assert.assertTrue(REQUEST_TYPE.isValidStatusCode(statusCode),
	                invalidStatusCodeMessage(REQUEST_TYPE, statusCode));
	        Assert.assertEquals(statusCode, EXPECTED_STATUS_CODE);
	
	        // Retrieve the updated resource and verify that its contents exist.
	        input = (MultipartInput) res.getEntity();
        } finally {
        	res.releaseConnection();
        }
        
        PersonauthoritiesCommon updatedPersonAuthority =
                (PersonauthoritiesCommon) extractPart(input,
                        client.getCommonPartName(), PersonauthoritiesCommon.class);
        Assert.assertNotNull(updatedPersonAuthority);

        // Verify that the updated resource received the correct data.
        Assert.assertEquals(updatedPersonAuthority.getDisplayName(),
                personAuthority.getDisplayName(),
                "Data in updated object did not match submitted data.");
    }

    /**
     * Update item.
     *
     * @param testName the test name
     * @throws Exception the exception
     */
    @Test(dataProvider="testName", dataProviderClass=AbstractServiceTestImpl.class,
        groups = {"update"}, dependsOnMethods = {"update"})
    public void updateItem(String testName) throws Exception {

        if (logger.isDebugEnabled()) {
            logger.debug(testBanner(testName, CLASS_NAME));
        }
        // Perform setup.
        setupUpdate();

        // Retrieve the contents of a resource to update.
        PersonAuthorityClient client = new PersonAuthorityClient();
        MultipartInput input = null;
        ClientResponse<MultipartInput> res =
                client.readItem(knownResourceId, knownItemResourceId);
        try {
	        if(logger.isDebugEnabled()){
	            logger.debug(testName + ": read status = " + res.getStatus());
	        }
	        Assert.assertEquals(res.getStatus(), EXPECTED_STATUS_CODE);
	
	        if(logger.isDebugEnabled()){
	            logger.debug("got Person to update with ID: " +
	                knownItemResourceId +
	                " in PersonAuthority: " + knownResourceId );
	        }
	        input = res.getEntity();
        } finally {
        	res.releaseConnection();
        }
        
        PersonsCommon person = (PersonsCommon) extractPart(input,
                client.getItemCommonPartName(), PersonsCommon.class);
        Assert.assertNotNull(person);
        
        if (logger.isDebugEnabled() == true) {
        	logger.debug("About to update the following person...");
        	logger.debug(objectAsXmlString(person, PersonsCommon.class));
        }

        // Update the contents of this resource.
        person.setCsid(null);
        person.setForeName("updated-" + person.getForeName());
        if(logger.isDebugEnabled()){
            logger.debug("to be updated Person");
            logger.debug(objectAsXmlString(person,
                PersonsCommon.class));
        }    
        
        // Submit the updated resource to the service and store the response.
        MultipartOutput output = new MultipartOutput();
        OutputPart commonPart = output.addPart(person, MediaType.APPLICATION_XML_TYPE);
        commonPart.getHeaders().add("label", client.getItemCommonPartName());
        res = client.updateItem(knownResourceId, knownItemResourceId, output);
        try {
	        int statusCode = res.getStatus();
	
	        // Check the status code of the response: does it match the expected response(s)?
	        if(logger.isDebugEnabled()){
	            logger.debug(testName + ": status = " + statusCode);
	        }
	        Assert.assertTrue(REQUEST_TYPE.isValidStatusCode(statusCode),
	                invalidStatusCodeMessage(REQUEST_TYPE, statusCode));
	        Assert.assertEquals(statusCode, EXPECTED_STATUS_CODE);
	
	        // Retrieve the updated resource and verify that its contents exist.
	        input = (MultipartInput) res.getEntity();
        } finally {
        	res.releaseConnection();
        }
        
        PersonsCommon updatedPerson =
                (PersonsCommon) extractPart(input,
                        client.getItemCommonPartName(), PersonsCommon.class);
        Assert.assertNotNull(updatedPerson);
        
        if (logger.isDebugEnabled() == true) {
        	logger.debug("Updated to following person to:");
        	logger.debug(objectAsXmlString(updatedPerson, PersonsCommon.class));
        }        

        // Verify that the updated resource received the correct data.
        Assert.assertEquals(updatedPerson.getForeName(),
                person.getForeName(),
                "Data in updated Person did not match submitted data.");
    }

    /**
     * Update contact.
     *
     * @param testName the test name
     * @throws Exception the exception
     */
    @Test(dataProvider="testName", dataProviderClass=AbstractServiceTestImpl.class,
        groups = {"update"}, dependsOnMethods = {"updateItem"})
    public void updateContact(String testName) throws Exception {
        
        if (logger.isDebugEnabled()) {
            logger.debug(testBanner(testName, CLASS_NAME));
        }
        // Perform setup.
        setupUpdate();

        // Retrieve the contents of a resource to update.
        PersonAuthorityClient client = new PersonAuthorityClient();
        MultipartInput input = null;
        ClientResponse<MultipartInput> res =
                client.readContact(knownResourceId, knownItemResourceId, knownContactResourceId);
        try {
	        if(logger.isDebugEnabled()){
	            logger.debug(testName + ": read status = " + res.getStatus());
	        }
	        Assert.assertEquals(res.getStatus(), EXPECTED_STATUS_CODE);
	
	        if(logger.isDebugEnabled()){
	            logger.debug("got Contact to update with ID: " +
	                knownContactResourceId +
	                " in item: " + knownItemResourceId +
	                " in parent: " + knownResourceId );
	        }
	        input = res.getEntity();
        } finally {
        	res.releaseConnection();
        }
        
        ContactsCommon contact = (ContactsCommon) extractPart(input,
                new ContactClient().getCommonPartName(), ContactsCommon.class);
        Assert.assertNotNull(contact);

        // Update the contents of this resource.
        contact.setAddressPlace("updated-" + contact.getAddressPlace());
        if(logger.isDebugEnabled()){
            logger.debug("to be updated Contact");
            logger.debug(objectAsXmlString(contact,
                ContactsCommon.class));
        }

        // Submit the updated resource to the service and store the response.
        MultipartOutput output = new MultipartOutput();
        OutputPart commonPart = output.addPart(contact, MediaType.APPLICATION_XML_TYPE);
        commonPart.getHeaders().add("label", new ContactClient().getCommonPartName());
        res = client.updateContact(knownResourceId, knownItemResourceId, knownContactResourceId, output);
        try {
	        int statusCode = res.getStatus();
	
	        // Check the status code of the response: does it match the expected response(s)?
	        if(logger.isDebugEnabled()){
	            logger.debug(testName + ": status = " + statusCode);
	        }
	        Assert.assertTrue(REQUEST_TYPE.isValidStatusCode(statusCode),
	                invalidStatusCodeMessage(REQUEST_TYPE, statusCode));
	        Assert.assertEquals(statusCode, EXPECTED_STATUS_CODE);
	
	        // Retrieve the updated resource and verify that its contents exist.
	        input = (MultipartInput) res.getEntity();
        } finally {
        	res.releaseConnection();
        }
        ContactsCommon updatedContact =
                (ContactsCommon) extractPart(input,
                        new ContactClient().getCommonPartName(), ContactsCommon.class);
        Assert.assertNotNull(updatedContact);

        // Verify that the updated resource received the correct data.
        Assert.assertEquals(updatedContact.getAddressPlace(),
                contact.getAddressPlace(),
                "Data in updated Contact did not match submitted data.");
    }

    // Failure outcomes
    // Placeholders until the three tests below can be uncommented.
    // See Issue CSPACE-401.
    /* (non-Javadoc)
     * @see org.collectionspace.services.client.test.AbstractServiceTestImpl#updateWithEmptyEntityBody(java.lang.String)
     */
    @Override
    public void updateWithEmptyEntityBody(String testName) throws Exception {
    	//Should this really be empty?
    }

    /* (non-Javadoc)
     * @see org.collectionspace.services.client.test.AbstractServiceTestImpl#updateWithMalformedXml(java.lang.String)
     */
    @Override
    public void updateWithMalformedXml(String testName) throws Exception {
    	//Should this really be empty?
    }

    /* (non-Javadoc)
     * @see org.collectionspace.services.client.test.AbstractServiceTestImpl#updateWithWrongXmlSchema(java.lang.String)
     */
    @Override
    public void updateWithWrongXmlSchema(String testName) throws Exception {
    	//Should this really be empty?
    }

/*
    @Override
    @Test(dataProvider="testName", dataProviderClass=AbstractServiceTest.class,
        groups = {"update"}, dependsOnMethods = {"update", "testSubmitRequest"})
    public void updateWithEmptyEntityBody(String testName) throws Exception {

        if (logger.isDebugEnabled()) {
            logger.debug(testBanner(testName, CLASS_NAME));
        }
        // Perform setup.
        setupUpdateWithEmptyEntityBody(testName, logger);

        // Submit the request to the service and store the response.
        String method = REQUEST_TYPE.httpMethodName();
        String url = getResourceURL(knownResourceId);
        String mediaType = MediaType.APPLICATION_XML;
        final String entity = "";
        int statusCode = submitRequest(method, url, mediaType, entity);

        // Check the status code of the response: does it match
        // the expected response(s)?
        if(logger.isDebugEnabled()){
            logger.debug(testName + ": url=" + url +
                " status=" + statusCode);
         }
        Assert.assertTrue(REQUEST_TYPE.isValidStatusCode(statusCode),
        invalidStatusCodeMessage(REQUEST_TYPE, statusCode));
        Assert.assertEquals(statusCode, EXPECTED_STATUS_CODE);
    }

    @Override
    @Test(dataProvider="testName", dataProviderClass=AbstractServiceTest.class,
        groups = {"update"}, dependsOnMethods = {"update", "testSubmitRequest"})
    public void updateWithMalformedXml(String testName) throws Exception {

        if (logger.isDebugEnabled()) {
            logger.debug(testBanner(testName, CLASS_NAME));
        }
        // Perform setup.
        setupUpdateWithMalformedXml();

        // Submit the request to the service and store the response.
        String method = REQUEST_TYPE.httpMethodName();
        String url = getResourceURL(knownResourceId);
        String mediaType = MediaType.APPLICATION_XML;
        final String entity = MALFORMED_XML_DATA;
        int statusCode = submitRequest(method, url, mediaType, entity);

        // Check the status code of the response: does it match
        // the expected response(s)?
        if(logger.isDebugEnabled()){
            logger.debug(testName + ": url=" + url +
               " status=" + statusCode);
         }
        Assert.assertTrue(REQUEST_TYPE.isValidStatusCode(statusCode),
        invalidStatusCodeMessage(REQUEST_TYPE, statusCode));
        Assert.assertEquals(statusCode, EXPECTED_STATUS_CODE);
    }

    @Override
    @Test(dataProvider="testName", dataProviderClass=AbstractServiceTest.class,
        groups = {"update"}, dependsOnMethods = {"update", "testSubmitRequest"})
    public void updateWithWrongXmlSchema(String testName) throws Exception {

        if (logger.isDebugEnabled()) {
            logger.debug(testBanner(testName, CLASS_NAME));
        }
        // Perform setup.
        setupUpdateWithWrongXmlSchema();

        // Submit the request to the service and store the response.
        String method = REQUEST_TYPE.httpMethodName();
        String url = getResourceURL(knownResourceId);
        String mediaType = MediaType.APPLICATION_XML;
        final String entity = WRONG_XML_SCHEMA_DATA;
        int statusCode = submitRequest(method, url, mediaType, entity);

        // Check the status code of the response: does it match
        // the expected response(s)?
        if(logger.isDebugEnabled()){
            logger.debug("updateWithWrongXmlSchema: url=" + url +
                " status=" + statusCode);
         }
        Assert.assertTrue(REQUEST_TYPE.isValidStatusCode(statusCode),
        invalidStatusCodeMessage(REQUEST_TYPE, statusCode));
        Assert.assertEquals(statusCode, EXPECTED_STATUS_CODE);
    }
*/

    /* (non-Javadoc)
 * @see org.collectionspace.services.client.test.AbstractServiceTestImpl#updateNonExistent(java.lang.String)
 */
@Override
    @Test(dataProvider="testName", dataProviderClass=AbstractServiceTestImpl.class,
        groups = {"update"}, dependsOnMethods = {"update", "testSubmitRequest"})
    public void updateNonExistent(String testName) throws Exception {

        if (logger.isDebugEnabled()) {
            logger.debug(testBanner(testName, CLASS_NAME));
        }
        // Perform setup.
        setupUpdateNonExistent();

        // Submit the request to the service and store the response.
        // Note: The ID(s) used when creating the request payload may be arbitrary.
        // The only relevant ID may be the one used in update(), below.
        PersonAuthorityClient client = new PersonAuthorityClient();
        String displayName = "displayName-NON_EXISTENT_ID";
    	MultipartOutput multipart = PersonAuthorityClientUtils.createPersonAuthorityInstance(
    				displayName, "NON_EXISTENT_SHORT_ID", client.getCommonPartName());
        ClientResponse<MultipartInput> res =
                client.update(NON_EXISTENT_ID, multipart);
        try {
	        int statusCode = res.getStatus();
	
	        // Check the status code of the response: does it match
	        // the expected response(s)?
	        if(logger.isDebugEnabled()){
	            logger.debug(testName + ": status = " + statusCode);
	        }
	        Assert.assertTrue(REQUEST_TYPE.isValidStatusCode(statusCode),
	                invalidStatusCodeMessage(REQUEST_TYPE, statusCode));
	        Assert.assertEquals(statusCode, EXPECTED_STATUS_CODE);
        } finally {
        	res.releaseConnection();
        }
    }

    /**
     * Update non existent item.
     *
     * @param testName the test name
     * @throws Exception the exception
     */
    @Test(dataProvider="testName", dataProviderClass=AbstractServiceTestImpl.class,
        groups = {"update"}, dependsOnMethods = {"updateItem", "testItemSubmitRequest"})
    public void updateNonExistentItem(String testName) throws Exception {

        if (logger.isDebugEnabled()) {
            logger.debug(testBanner(testName, CLASS_NAME));
        }
        // Perform setup.
        setupUpdateNonExistent();

        // Submit the request to the service and store the response.
        // Note: The ID used in this 'create' call may be arbitrary.
        // The only relevant ID may be the one used in update(), below.
        PersonAuthorityClient client = new PersonAuthorityClient();
        Map<String, String> nonexMap = new HashMap<String,String>();
        nonexMap.put(PersonJAXBSchema.SHORT_IDENTIFIER, "nonEX");
        nonexMap.put(PersonJAXBSchema.FORE_NAME, "John");
        nonexMap.put(PersonJAXBSchema.SUR_NAME, "Wayne");
        nonexMap.put(PersonJAXBSchema.GENDER, "male");
        MultipartOutput multipart = 
    	PersonAuthorityClientUtils.createPersonInstance(NON_EXISTENT_ID, 
    			PersonAuthorityClientUtils.createPersonAuthRefName(NON_EXISTENT_ID, null),
    			nonexMap, client.getItemCommonPartName() );
        ClientResponse<MultipartInput> res =
                client.updateItem(knownResourceId, NON_EXISTENT_ID, multipart);
        try {
	        int statusCode = res.getStatus();
	
	        // Check the status code of the response: does it match
	        // the expected response(s)?
	        if(logger.isDebugEnabled()){
	            logger.debug(testName + ": status = " + statusCode);
	        }
	        Assert.assertTrue(REQUEST_TYPE.isValidStatusCode(statusCode),
	                invalidStatusCodeMessage(REQUEST_TYPE, statusCode));
	        Assert.assertEquals(statusCode, EXPECTED_STATUS_CODE);
        } finally {
        	res.releaseConnection();
        }
    }

    /**
     * Update non existent contact.
     *
     * @param testName the test name
     * @throws Exception the exception
     */
    @Test(dataProvider="testName", dataProviderClass=AbstractServiceTestImpl.class,
        groups = {"update"}, dependsOnMethods = {"updateContact", "testContactSubmitRequest"})
    public void updateNonExistentContact(String testName) throws Exception {
        // Currently a no-op test
    }

    // ---------------------------------------------------------------
    // CRUD tests : DELETE tests
    // ---------------------------------------------------------------
    // Success outcomes

    // Note: delete sub-resources in ascending hierarchical order,
    // before deleting their parents.

    /**
     * Delete contact.
     *
     * @param testName the test name
     * @throws Exception the exception
     */
    @Test(dataProvider="testName", dataProviderClass=AbstractServiceTestImpl.class, 
        groups = {"delete"}, dependsOnGroups = {"create", "read", "readList", "readListByPartialTerm", "update"})
    public void deleteContact(String testName) throws Exception {

        if (logger.isDebugEnabled()) {
            logger.debug(testBanner(testName, CLASS_NAME));
        }
        // Perform setup.
        setupDelete();

         if(logger.isDebugEnabled()){
            logger.debug("parentcsid =" + knownResourceId +
                " itemcsid = " + knownItemResourceId +
                " csid = " + knownContactResourceId);
        }

        // Submit the request to the service and store the response.
        PersonAuthorityClient client = new PersonAuthorityClient();
        ClientResponse<Response> res =
            client.deleteContact(knownResourceId, knownItemResourceId, knownContactResourceId);
        int statusCode = res.getStatus();
        res.releaseConnection();

        // Check the status code of the response: does it match
        // the expected response(s)?
        if(logger.isDebugEnabled()){
            logger.debug(testName + ": status = " + statusCode);
        }
        Assert.assertTrue(REQUEST_TYPE.isValidStatusCode(statusCode),
                invalidStatusCodeMessage(REQUEST_TYPE, statusCode));
        Assert.assertEquals(statusCode, EXPECTED_STATUS_CODE);
    }

   /**
    * Delete item.
    *
    * @param testName the test name
    * @throws Exception the exception
    */
   @Test(dataProvider="testName", dataProviderClass=AbstractServiceTestImpl.class,
        groups = {"delete"}, dependsOnMethods = {"deleteContact"})
    public void deleteItem(String testName) throws Exception {

        if (logger.isDebugEnabled()) {
            logger.debug(testBanner(testName, CLASS_NAME));
        }
        // Perform setup.
        setupDelete();

        if(logger.isDebugEnabled()){
            logger.debug("parentcsid =" + knownResourceId +
                " itemcsid = " + knownItemResourceId);
        }

        // Submit the request to the service and store the response.
        PersonAuthorityClient client = new PersonAuthorityClient();
        ClientResponse<Response> res = client.deleteItem(knownResourceId, knownItemResourceId);
        int statusCode = res.getStatus();
        res.releaseConnection();

        // Check the status code of the response: does it match
        // the expected response(s)?
        if(logger.isDebugEnabled()){
            logger.debug(testName + ": status = " + statusCode);
        }
        Assert.assertTrue(REQUEST_TYPE.isValidStatusCode(statusCode),
                invalidStatusCodeMessage(REQUEST_TYPE, statusCode));
        Assert.assertEquals(statusCode, EXPECTED_STATUS_CODE);
    }

    /* (non-Javadoc)
     * @see org.collectionspace.services.client.test.AbstractServiceTestImpl#delete(java.lang.String)
     */
    @Override
    @Test(dataProvider="testName", dataProviderClass=AbstractServiceTestImpl.class,
        groups = {"delete"}, dependsOnMethods = {"deleteItem"})
    public void delete(String testName) throws Exception {

        if (logger.isDebugEnabled()) {
            logger.debug(testBanner(testName, CLASS_NAME));
        }
        // Perform setup.
        setupDelete();

        if(logger.isDebugEnabled()){
            logger.debug("parentcsid =" + knownResourceId);
        }

        // Submit the request to the service and store the response.
        PersonAuthorityClient client = new PersonAuthorityClient();
        ClientResponse<Response> res = client.delete(knownResourceId);
        int statusCode = res.getStatus();
        res.releaseConnection();

        // Check the status code of the response: does it match
        // the expected response(s)?
        if(logger.isDebugEnabled()){
            logger.debug(testName + ": status = " + statusCode);
        }
        Assert.assertTrue(REQUEST_TYPE.isValidStatusCode(statusCode),
                invalidStatusCodeMessage(REQUEST_TYPE, statusCode));
        Assert.assertEquals(statusCode, EXPECTED_STATUS_CODE);
    }

    // Failure outcomes
    /* (non-Javadoc)
     * @see org.collectionspace.services.client.test.AbstractServiceTestImpl#deleteNonExistent(java.lang.String)
     */
    @Override
    @Test(dataProvider="testName", dataProviderClass=AbstractServiceTestImpl.class,
        groups = {"delete"}, dependsOnMethods = {"delete"})
    public void deleteNonExistent(String testName) throws Exception {

        if (logger.isDebugEnabled()) {
            logger.debug(testBanner(testName, CLASS_NAME));
        }
        // Perform setup.
        setupDeleteNonExistent();

        // Submit the request to the service and store the response.
        PersonAuthorityClient client = new PersonAuthorityClient();
        ClientResponse<Response> res = client.delete(NON_EXISTENT_ID);
        int statusCode = res.getStatus();
        res.releaseConnection();

        // Check the status code of the response: does it match
        // the expected response(s)?
        if(logger.isDebugEnabled()){
            logger.debug(testName + ": status = " + statusCode);
        }
        Assert.assertTrue(REQUEST_TYPE.isValidStatusCode(statusCode),
                invalidStatusCodeMessage(REQUEST_TYPE, statusCode));
        Assert.assertEquals(statusCode, EXPECTED_STATUS_CODE);
    }

    /**
     * Delete non existent item.
     *
     * @param testName the test name
     */
    @Test(dataProvider="testName", dataProviderClass=AbstractServiceTestImpl.class,
        groups = {"delete"}, dependsOnMethods = {"deleteItem"})
    public void deleteNonExistentItem(String testName) {

        if (logger.isDebugEnabled()) {
            logger.debug(testBanner(testName, CLASS_NAME));
        }
        // Perform setup.
        setupDeleteNonExistent();

        // Submit the request to the service and store the response.
        PersonAuthorityClient client = new PersonAuthorityClient();
        ClientResponse<Response> res = client.deleteItem(knownResourceId, NON_EXISTENT_ID);
        int statusCode = res.getStatus();
        res.releaseConnection();

        // Check the status code of the response: does it match
        // the expected response(s)?
        if(logger.isDebugEnabled()){
            logger.debug(testName + ": status = " + statusCode);
        }
        Assert.assertTrue(REQUEST_TYPE.isValidStatusCode(statusCode),
                invalidStatusCodeMessage(REQUEST_TYPE, statusCode));
        Assert.assertEquals(statusCode, EXPECTED_STATUS_CODE);
    }

    /**
     * Delete non existent contact.
     *
     * @param testName the test name
     */
    @Test(dataProvider="testName", dataProviderClass=AbstractServiceTestImpl.class,
        groups = {"delete"}, dependsOnMethods = {"deleteContact"})
    public void deleteNonExistentContact(String testName) {

        if (logger.isDebugEnabled()) {
            logger.debug(testBanner(testName, CLASS_NAME));
        }
        // Perform setup.
        setupDeleteNonExistent();

        // Submit the request to the service and store the response.
        PersonAuthorityClient client = new PersonAuthorityClient();
        ClientResponse<Response> res =
            client.deleteContact(knownResourceId, knownItemResourceId, NON_EXISTENT_ID);
        int statusCode = res.getStatus();
        res.releaseConnection();

        // Check the status code of the response: does it match
        // the expected response(s)?
        if(logger.isDebugEnabled()){
            logger.debug(testName + ": status = " + statusCode);
        }
        Assert.assertTrue(REQUEST_TYPE.isValidStatusCode(statusCode),
                invalidStatusCodeMessage(REQUEST_TYPE, statusCode));
        Assert.assertEquals(statusCode, EXPECTED_STATUS_CODE);
    }

    // ---------------------------------------------------------------
    // Utility tests : tests of code used in tests above
    // ---------------------------------------------------------------
    /**
     * Tests the code for manually submitting data that is used by several
     * of the methods above.
     */
    @Test(dependsOnMethods = {"create", "read"})
    public void testSubmitRequest() {

        // Expected status code: 200 OK
        final int EXPECTED_STATUS = Response.Status.OK.getStatusCode();

        // Submit the request to the service and store the response.
        String method = ServiceRequestType.READ.httpMethodName();
        String url = getResourceURL(knownResourceId);
        int statusCode = submitRequest(method, url);

        // Check the status code of the response: does it match
        // the expected response(s)?
        if(logger.isDebugEnabled()){
            logger.debug("testSubmitRequest: url=" + url +
                " status=" + statusCode);
        }
        Assert.assertEquals(statusCode, EXPECTED_STATUS);

    }

    /**
     * Test item submit request.
     */
    @Test(dependsOnMethods = {"createItem", "readItem", "testSubmitRequest"})
    public void testItemSubmitRequest() {

        // Expected status code: 200 OK
        final int EXPECTED_STATUS = Response.Status.OK.getStatusCode();

        // Submit the request to the service and store the response.
        String method = ServiceRequestType.READ.httpMethodName();
        String url = getItemResourceURL(knownResourceId, knownItemResourceId);
        int statusCode = submitRequest(method, url);

        // Check the status code of the response: does it match
        // the expected response(s)?
        if(logger.isDebugEnabled()){
            logger.debug("testItemSubmitRequest: url=" + url +
                " status=" + statusCode);
        }
        Assert.assertEquals(statusCode, EXPECTED_STATUS);

    }

    /**
     * Test contact submit request.
     */
    @Test(dependsOnMethods = {"createContact", "readContact", "testItemSubmitRequest"})
    public void testContactSubmitRequest() {

        // Expected status code: 200 OK
        final int EXPECTED_STATUS = Response.Status.OK.getStatusCode();

        // Submit the request to the service and store the response.
        String method = ServiceRequestType.READ.httpMethodName();
        String url = getContactResourceURL(knownResourceId,
            knownItemResourceId, knownContactResourceId);
        int statusCode = submitRequest(method, url);

        // Check the status code of the response: does it match
        // the expected response(s)?
        if(logger.isDebugEnabled()){
            logger.debug("testItemSubmitRequest: url=" + url +
                " status=" + statusCode);
        }
        Assert.assertEquals(statusCode, EXPECTED_STATUS);

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
    @Override
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
        String contactResourceId;
        // Clean up contact resources.
        PersonAuthorityClient client = new PersonAuthorityClient();
        parentResourceId = knownResourceId;
        for (Map.Entry<String, String> entry : allContactResourceIdsCreated.entrySet()) {
            contactResourceId = entry.getKey();
            itemResourceId = entry.getValue();
            // Note: Any non-success responses from the delete operation
            // below are ignored and not reported.
            ClientResponse<Response> res =
                client.deleteContact(parentResourceId, itemResourceId, contactResourceId);
            res.releaseConnection();
        }
        // Clean up item resources.
        for (Map.Entry<String, String> entry : allItemResourceIdsCreated.entrySet()) {
            itemResourceId = entry.getKey();
            parentResourceId = entry.getValue();
            // Note: Any non-success responses from the delete operation
            // below are ignored and not reported.
            ClientResponse<Response> res =
                client.deleteItem(parentResourceId, itemResourceId);
            res.releaseConnection();
        }
        // Clean up parent resources.
        super.cleanUp();
    }

    // ---------------------------------------------------------------
    // Utility methods used by tests above
    // ---------------------------------------------------------------
    /* (non-Javadoc)
     * @see org.collectionspace.services.client.test.BaseServiceTest#getServicePathComponent()
     */
    @Override
    public String getServicePathComponent() {
        return SERVICE_PATH_COMPONENT;
    }

    /**
     * Gets the item service path component.
     *
     * @return the item service path component
     */
    public String getItemServicePathComponent() {
        return ITEM_SERVICE_PATH_COMPONENT;
    }

    /**
     * Gets the contact service path component.
     *
     * @return the contact service path component
     */
    public String getContactServicePathComponent() {
        return CONTACT_SERVICE_PATH_COMPONENT;
    }

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


    /**
     * Returns the root URL for the contact service.
     *
     * This URL consists of a base URL for all services, followed by
     * a path component for the owning authority, followed by the
     * path component for the owning item, followed by the path component
     * for the contact service.
     *
     * @param  parentResourceIdentifier  An identifier (such as a UUID) for the
     * parent authority resource of the relevant item resource.
     *
     * @param  itemResourceIdentifier  An identifier (such as a UUID) for an
     * item resource.
     *
     * @return The root URL for the contact service.
     */
    protected String getContactServiceRootURL(String parentResourceIdentifier,
        String itemResourceIdentifier) {
        return getItemResourceURL(parentResourceIdentifier, itemResourceIdentifier) + "/" +
                getContactServicePathComponent();
    }

    /**
     * Returns the URL of a specific contact resource managed by a service, and
     * designated by an identifier (such as a universally unique ID, or UUID).
     *
     * @param  parentResourceIdentifier  An identifier (such as a UUID) for the
     * parent resource of the relevant item resource.
     *
     * @param  resourceIdentifier  An identifier (such as a UUID) for an
     * item resource.
     *
     * @return The URL of a specific resource managed by a service.
     */
    protected String getContactResourceURL(String parentResourceIdentifier,
        String itemResourceIdentifier, String contactResourceIdentifier) {
        return getContactServiceRootURL(parentResourceIdentifier,
            itemResourceIdentifier) + "/" + contactResourceIdentifier;
    }
}
