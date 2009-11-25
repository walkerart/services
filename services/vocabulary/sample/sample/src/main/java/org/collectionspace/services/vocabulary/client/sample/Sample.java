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

package org.collectionspace.services.vocabulary.client.sample;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import org.apache.log4j.BasicConfigurator;
import org.collectionspace.services.client.VocabularyClient;
import org.collectionspace.services.client.test.ServiceRequestType;
import org.collectionspace.services.vocabulary.VocabulariesCommon;
import org.collectionspace.services.vocabulary.VocabulariesCommonList;
import org.collectionspace.services.vocabulary.VocabulariesCommonList.VocabularyListItem;
import org.collectionspace.services.vocabulary.VocabularyitemsCommon;
import org.collectionspace.services.vocabulary.VocabularyitemsCommonList;
import org.collectionspace.services.vocabulary.VocabularyitemsCommonList.VocabularyitemListItem;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.plugins.providers.multipart.MultipartOutput;
import org.jboss.resteasy.plugins.providers.multipart.OutputPart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * VocabularyServiceTest, carries out tests against a
 * deployed and running Vocabulary Service.
 *
 * $LastChangedRevision: 753 $
 * $LastChangedDate: 2009-09-23 11:03:36 -0700 (Wed, 23 Sep 2009) $
 */
public class Sample {
    private static final Logger logger =
        LoggerFactory.getLogger(Sample.class);

    // Instance variables specific to this test.
    private VocabularyClient client = new VocabularyClient();
    final String SERVICE_PATH_COMPONENT = "vocabularies";
    final String ITEM_SERVICE_PATH_COMPONENT = "items";


    // Create

    public void createEnumeration(String vocabName, List<String> enumValues ) {

    	// Expected status code: 201 Created
    	int EXPECTED_STATUS_CODE = Response.Status.CREATED.getStatusCode();
    	// Type of service request being tested
    	ServiceRequestType REQUEST_TYPE = ServiceRequestType.CREATE;

    	logger.info("Import: Create vocabulary: \"" + vocabName +"\"");
    	MultipartOutput multipart = createVocabularyInstance(vocabName, 
    			createRefName(vocabName), "enum");
    	ClientResponse<Response> res = client.create(multipart);

    	int statusCode = res.getStatus();

    	if(!REQUEST_TYPE.isValidStatusCode(statusCode)) {
    		throw new RuntimeException("Could not create enumeration: \""+vocabName
    				+"\" "+ invalidStatusCodeMessage(REQUEST_TYPE, statusCode));
    	}
    	if(statusCode != EXPECTED_STATUS_CODE) {
    		throw new RuntimeException("Unexpected Status when creating enumeration: \""
    				+vocabName +"\", Status:"+ statusCode);
    	}

    	// Store the ID returned from this create operation
    	// for additional tests below.
    	String newVocabId = extractId(res);
        logger.info("Import: Created vocabulary: \"" + vocabName +"\" ID:"
    				+newVocabId );
        
        // Add items to the vocabulary
    	for(String itemName : enumValues){
    		createItemInVocab(newVocabId, vocabName, itemName, createRefName(itemName));
    	}
        
    }
    
    private String createItemInVocab(String vcsid, String vocabName, String itemName, String refName) {
    	// Expected status code: 201 Created
    	int EXPECTED_STATUS_CODE = Response.Status.CREATED.getStatusCode();
    	// Type of service request being tested
    	ServiceRequestType REQUEST_TYPE = ServiceRequestType.CREATE;

    	logger.info("Import: Create Item: \""+itemName+"\" in vocabulary: \"" + vocabName +"\"");
    	MultipartOutput multipart = createVocabularyItemInstance(vcsid, itemName, refName);
    	ClientResponse<Response> res = client.createItem(vcsid, multipart);

    	int statusCode = res.getStatus();

    	if(!REQUEST_TYPE.isValidStatusCode(statusCode)) {
    		throw new RuntimeException("Could not create Item: \""+itemName
    				+"\" in vocabulary: \"" + vocabName
    				+"\" "+ invalidStatusCodeMessage(REQUEST_TYPE, statusCode));
    	}
    	if(statusCode != EXPECTED_STATUS_CODE) {
    		throw new RuntimeException("Unexpected Status when creating Item: \""+itemName
    				+"\" in vocabulary: \"" + vocabName +"\", Status:"+ statusCode);
    	}

    	return extractId(res);
    }

   // Read

   private VocabulariesCommonList readVocabularies() {

        // Expected status code: 200 OK
    	int EXPECTED_STATUS_CODE = Response.Status.OK.getStatusCode();
    	// Type of service request being tested
    	ServiceRequestType REQUEST_TYPE = ServiceRequestType.READ;

        // Submit the request to the service and store the response.
        ClientResponse<VocabulariesCommonList> res = client.readList();
        VocabulariesCommonList list = res.getEntity();

        int statusCode = res.getStatus();
    	if(!REQUEST_TYPE.isValidStatusCode(statusCode)) {
    		throw new RuntimeException("Could not read list of vocabularies: "
                + invalidStatusCodeMessage(REQUEST_TYPE, statusCode));
    	}
    	if(statusCode != EXPECTED_STATUS_CODE) {
    		throw new RuntimeException("Unexpected Status when reading " +
                "list of vocabularies, Status:"+ statusCode);
    	}

        return list;

    }
/*
    private List<String> readVocabularyIds() {

        VocabulariesCommonList items = readVocabularies();

        List<String> ids;
        for (VocabulariesCommonList.VocabularyListItem item : items) {
            ids.add(item.getCsid());
        }

        return list;

    }
*/
    private VocabularyitemsCommonList readItemsInVocab(String vocabId) {

        // Expected status code: 200 OK
    	int EXPECTED_STATUS_CODE = Response.Status.OK.getStatusCode();
    	// Type of service request being tested
    	ServiceRequestType REQUEST_TYPE = ServiceRequestType.READ;

        // Submit the request to the service and store the response.
        ClientResponse<VocabularyitemsCommonList> res =
                client.readItemList(vocabId);
        VocabularyitemsCommonList list = res.getEntity();

        int statusCode = res.getStatus();

    	if(!REQUEST_TYPE.isValidStatusCode(statusCode)) {
    		throw new RuntimeException("Could not read items in vocabulary: "
                + invalidStatusCodeMessage(REQUEST_TYPE, statusCode));
    	}
    	if(statusCode != EXPECTED_STATUS_CODE) {
    		throw new RuntimeException("Unexpected Status when reading " +
                "items in vocabulary, Status:"+ statusCode);
    	}

        return list;

    }

    // ---------------------------------------------------------------
    // Utility methods used by tests above
    // ---------------------------------------------------------------

    private MultipartOutput createVocabularyInstance(
    		String displayName, String refName, String vocabType) {
        VocabulariesCommon vocabulary = new VocabulariesCommon();
        vocabulary.setDisplayName(displayName);
        vocabulary.setRefName(refName);
        vocabulary.setVocabType(vocabType);
        MultipartOutput multipart = new MultipartOutput();
        OutputPart commonPart = multipart.addPart(vocabulary, MediaType.APPLICATION_XML_TYPE);
        commonPart.getHeaders().add("label", client.getCommonPartName());

        if(logger.isDebugEnabled()){
        	logger.debug("to be created, vocabulary common ",
        				vocabulary, VocabulariesCommon.class);
        }

        return multipart;
    }

    private MultipartOutput createVocabularyItemInstance(
    		String inVocabulary, String displayName, String refName) {
    	VocabularyitemsCommon vocabularyItem = new VocabularyitemsCommon();
    	vocabularyItem.setInVocabulary(inVocabulary);
    	vocabularyItem.setDisplayName(displayName);
    	vocabularyItem.setRefName(refName);
        MultipartOutput multipart = new MultipartOutput();
        OutputPart commonPart = multipart.addPart(vocabularyItem, MediaType.APPLICATION_XML_TYPE);
        commonPart.getHeaders().add("label", client.getItemCommonPartName());

        if(logger.isDebugEnabled()){
        	logger.debug("to be created, vocabularyitem common ", vocabularyItem, VocabularyitemsCommon.class);
        }

        return multipart;
    }

    // Retrieve individual fields of vocabulary records.

    private String displayVocabularyListDetails(VocabulariesCommonList list) {
        StringBuffer sb = new StringBuffer();
            List<VocabulariesCommonList.VocabularyListItem> items =
                    list.getVocabularyListItem();
            int i = 0;
        for (VocabulariesCommonList.VocabularyListItem item : items) {
            sb.append("vocabulary [" + i + "]" + "\n");
            sb.append(displayVocabularyDetails(item));
            i++;
        }
        return sb.toString();
    }

    private String displayVocabularyDetails(
        VocabulariesCommonList.VocabularyListItem item) {
            StringBuffer sb = new StringBuffer();
            sb.append("csid=" + item.getCsid() + "\n");
            sb.append("displayName=" + item.getDisplayName() + "\n");
            sb.append("URI=" + item.getUri() + "\n");
        return sb.toString();
    }

    // Retrieve individual fields of vocabulary item records.

    private String displayVocabularyItemListDetails(VocabularyitemsCommonList list) {
        StringBuffer sb = new StringBuffer();
        List<VocabularyitemListItem> items =
                list.getVocabularyitemListItem();
        int i = 0;
        for (VocabularyitemListItem item : items) {
            sb.append("vocabulary item [" + i + "]" + "\n");
            sb.append(displayVocabularyItemDetails(item));
            i++;
        }
        return sb.toString();
    }

    private String displayVocabularyItemDetails(
        VocabularyitemsCommonList.VocabularyitemListItem item) {
            StringBuffer sb = new StringBuffer();
            sb.append("csid=" + item.getCsid() + "\n");
            sb.append("displayName=" + item.getDisplayName() + "\n");
            sb.append("URI=" + item.getUri() + "\n");
        return sb.toString();
    }

    /**
     * Returns an error message indicating that the status code returned by a
     * specific call to a service does not fall within a set of valid status
     * codes for that service.
     *
     * @param serviceRequestType  A type of service request (e.g. CREATE, DELETE).
     *
     * @param statusCode  The invalid status code that was returned in the response,
     *                    from submitting that type of request to the service.
     *
     * @return An error message.
     */
    protected String invalidStatusCodeMessage(ServiceRequestType requestType, int statusCode) {
        return "Status code '" + statusCode + "' in response is NOT within the expected set: " +
                requestType.validStatusCodesAsString();
    }

    protected String extractId(ClientResponse<Response> res) {
        MultivaluedMap<String, Object> mvm = res.getMetadata();
        String uri = (String) ((ArrayList<Object>) mvm.get("Location")).get(0);
        if(logger.isDebugEnabled()){
        	logger.info("extractId:uri=" + uri);
        }
        String[] segments = uri.split("/");
        String id = segments[segments.length - 1];
        if(logger.isDebugEnabled()){
        	logger.debug("id=" + id);
        }
        return id;
    }
    
    protected String createRefName(String displayName) {
    	return displayName.replaceAll("\\W", "");
    }

	public static void main(String[] args) {
		
		BasicConfigurator.configure();
		logger.info("VocabularyBaseImport starting...");


		Sample vbi = new Sample();
		final String acquisitionMethodsVocabName = "Acquisition Methods";
		final String entryMethodsVocabName = "Entry Methods";
		final String entryReasonsVocabName = "Entry Reasons";
		final String responsibleDeptsVocabName = "Responsible Departments";

		List<String> acquisitionMethodsEnumValues = 
			Arrays.asList("Gift","Purchase","Exchange","Transfer","Treasure");
		List<String> entryMethodsEnumValues = 
			Arrays.asList("In person","Post","Found on doorstep");
		List<String> entryReasonsEnumValues = 
			Arrays.asList("Enquiry","Commission","Loan");
		List<String> respDeptNamesEnumValues = 
			Arrays.asList("Antiquities","Architecture and Design","Decorative Arts",
									"Ethnography","Herpetology","Media and Performance Art",
									"Paintings and Sculpture","Paleobotany","Photographs",
									"Prints and Drawings");

		vbi.createEnumeration(acquisitionMethodsVocabName, acquisitionMethodsEnumValues);
		vbi.createEnumeration(entryMethodsVocabName, entryMethodsEnumValues);
		vbi.createEnumeration(entryReasonsVocabName, entryReasonsEnumValues);
		vbi.createEnumeration(responsibleDeptsVocabName, respDeptNamesEnumValues);

		logger.info("VocabularyBaseImport complete.");

        logger.info("Reading newly-created vocabularies ...");

        // Read vocabularies.
        VocabulariesCommonList vocabularies = vbi.readVocabularies();
        String details = vbi.displayVocabularyListDetails(vocabularies);
        logger.info(details);

        // Read items in each vocabulary.
//        readItemsInVocab(newVocabId);

        // Delete vocabulary items.

        // Delete vocabularies.
	}

}
