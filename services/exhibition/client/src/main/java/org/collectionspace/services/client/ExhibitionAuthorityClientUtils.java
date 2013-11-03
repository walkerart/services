package org.collectionspace.services.client;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import org.apache.commons.io.FileUtils;
import org.collectionspace.services.ExhibitionJAXBSchema;
import org.collectionspace.services.client.test.ServiceRequestType;
import org.collectionspace.services.common.api.Tools;
import org.collectionspace.services.exhibition.ExhibitionTermGroup;
import org.collectionspace.services.exhibition.ExhibitionTermGroupList;
import org.collectionspace.services.exhibition.ExhibitionauthoritiesCommon;
import org.collectionspace.services.exhibition.ExhibitionsCommon;
import org.dom4j.DocumentException;
import org.jboss.resteasy.client.ClientResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExhibitionAuthorityClientUtils {
    private static final Logger logger =
        LoggerFactory.getLogger(ExhibitionAuthorityClientUtils.class);

    /**
     * Creates a new Exhibition Authority
     * @param displayName   The displayName used in UI, etc.
     * @param refName       The proper refName for this authority
     * @param headerLabel   The common part label
     * @return  The PoxPayloadOut payload for the create call
     */
    public static PoxPayloadOut createExhibitionAuthorityInstance(
            String displayName, String shortIdentifier, String headerLabel ) {
        ExhibitionauthoritiesCommon exhibitionAuthority = new ExhibitionauthoritiesCommon();
        exhibitionAuthority.setDisplayName(displayName);
        exhibitionAuthority.setShortIdentifier(shortIdentifier);
        String refName = createExhibitionAuthRefName(shortIdentifier, displayName);
        exhibitionAuthority.setRefName(refName);
        exhibitionAuthority.setVocabType("ExhibitionAuthority"); //FIXME: REM - Should this really be hard-coded?
        PoxPayloadOut multipart = new PoxPayloadOut(ExhibitionAuthorityClient.SERVICE_PAYLOAD_NAME);
        PayloadOutputPart commonPart = multipart.addPart(exhibitionAuthority, MediaType.APPLICATION_XML_TYPE);
        commonPart.setLabel(headerLabel);

        if(logger.isDebugEnabled()){
            logger.debug("to be created, exhibitionAuthority common ", 
                        exhibitionAuthority, ExhibitionauthoritiesCommon.class);
        }

        return multipart;
    }

    /**
     * @param exhibitionRefName  The proper refName for this authority
     * @param exhibitionInfo the properties for the new Exhibition. Can pass in one condition
     *                      note and date string.
     * @param headerLabel   The common part label
     * @return  The PoxPayloadOut payload for the create call
     */
    public static PoxPayloadOut createExhibitionInstance( 
            String exhibitionAuthRefName, Map<String, String> exhibitionInfo, 
        List<ExhibitionTermGroup> terms, String headerLabel){
        ExhibitionsCommon exhibition = new ExhibitionsCommon();
        String shortId = exhibitionInfo.get(ExhibitionJAXBSchema.SHORT_IDENTIFIER);
        exhibition.setShortIdentifier(shortId);

        // Set values in the Term Information Group
        ExhibitionTermGroupList termList = new ExhibitionTermGroupList();
        if (terms == null || terms.isEmpty()) {
            terms = getTermGroupInstance(getGeneratedIdentifier());
        }
        termList.getExhibitionTermGroup().addAll(terms); 
        exhibition.setExhibitionTermGroupList(termList);
        
        PoxPayloadOut multipart = new PoxPayloadOut(ExhibitionAuthorityClient.SERVICE_ITEM_PAYLOAD_NAME);
        PayloadOutputPart commonPart = multipart.addPart(exhibition,
            MediaType.APPLICATION_XML_TYPE);
        commonPart.setLabel(headerLabel);

        if(logger.isDebugEnabled()){
            logger.debug("to be created, exhibition common ", exhibition, ExhibitionsCommon.class);
        }

        return multipart;
    }
    
    /**
     * @param vcsid CSID of the authority to create a new exhibition
     * @param exhibitionAuthorityRefName The refName for the authority
     * @param exhibitionMap the properties for the new Exhibition
     * @param client the service client
     * @return the CSID of the new item
     */
    public static String createItemInAuthority(String vcsid, 
            String exhibitionAuthorityRefName, Map<String,String> exhibitionMap,
            List<ExhibitionTermGroup> terms, ExhibitionAuthorityClient client ) {
        // Expected status code: 201 Created
        int EXPECTED_STATUS_CODE = Response.Status.CREATED.getStatusCode();
        // Type of service request being tested
        ServiceRequestType REQUEST_TYPE = ServiceRequestType.CREATE;
        
        String displayName = "";
        if ((terms !=null) && (! terms.isEmpty())) {
            displayName = terms.get(0).getTermDisplayName();
        }
        if(logger.isDebugEnabled()){
            logger.debug("Creating item with display name: \"" + displayName
                    +"\" in exhibitionAuthority: \"" + vcsid +"\"");
        }
        PoxPayloadOut multipart = 
            createExhibitionInstance( exhibitionAuthorityRefName,
                exhibitionMap, terms, client.getItemCommonPartName() );
        String newID = null;
        ClientResponse<Response> res = client.createItem(vcsid, multipart);
        try {
            int statusCode = res.getStatus();
    
            if(!REQUEST_TYPE.isValidStatusCode(statusCode)) {
                throw new RuntimeException("Could not create Item: \""
                        +exhibitionMap.get(ExhibitionJAXBSchema.SHORT_IDENTIFIER)
                        +"\" in exhibitionAuthority: \"" + exhibitionAuthorityRefName
                        +"\" "+ invalidStatusCodeMessage(REQUEST_TYPE, statusCode));
            }
            if(statusCode != EXPECTED_STATUS_CODE) {
                throw new RuntimeException("Unexpected Status when creating Item: \""
                        +exhibitionMap.get(ExhibitionJAXBSchema.SHORT_IDENTIFIER)
                        +"\" in exhibitionAuthority: \"" + exhibitionAuthorityRefName +"\", Status:"+ statusCode);
            }
            newID = extractId(res);
        } finally {
            res.releaseConnection();
        }

        return newID;
    }

    public static PoxPayloadOut createExhibitionInstance(
            String commonPartXML, String headerLabel)  throws DocumentException {
        PoxPayloadOut multipart = new PoxPayloadOut(ExhibitionAuthorityClient.SERVICE_ITEM_PAYLOAD_NAME);
        PayloadOutputPart commonPart = multipart.addPart(commonPartXML,
            MediaType.APPLICATION_XML_TYPE);
        commonPart.setLabel(headerLabel);

        if(logger.isDebugEnabled()){
            logger.debug("to be created, exhibition common ", commonPartXML);
        }

        return multipart;
    }
    
    public static String createItemInAuthority(String vcsid,
            String commonPartXML,
            ExhibitionAuthorityClient client ) throws DocumentException {
        // Expected status code: 201 Created
        int EXPECTED_STATUS_CODE = Response.Status.CREATED.getStatusCode();
        // Type of service request being tested
        ServiceRequestType REQUEST_TYPE = ServiceRequestType.CREATE;
        
        PoxPayloadOut multipart = 
            createExhibitionInstance(commonPartXML, client.getItemCommonPartName());
        String newID = null;
        ClientResponse<Response> res = client.createItem(vcsid, multipart);
        try {
            int statusCode = res.getStatus();
    
            if(!REQUEST_TYPE.isValidStatusCode(statusCode)) {
                throw new RuntimeException("Could not create Item: \""+commonPartXML
                        +"\" in exhibitionAuthority: \"" + vcsid
                        +"\" "+ invalidStatusCodeMessage(REQUEST_TYPE, statusCode));
            }
            if(statusCode != EXPECTED_STATUS_CODE) {
                throw new RuntimeException("Unexpected Status when creating Item: \""+commonPartXML
                        +"\" in exhibitionAuthority: \"" + vcsid +"\", Status:"+ statusCode);
            }
            newID = extractId(res);
        } finally {
            res.releaseConnection();
        }

        return newID;
    }
    
    /**
     * Creates the from xml file.
     *
     * @param fileName the file name
     * @return new CSID as string
     * @throws Exception the exception
     */
    private String createItemInAuthorityFromXmlFile(String vcsid, String commonPartFileName, 
            ExhibitionAuthorityClient client) throws Exception {
        byte[] b = FileUtils.readFileToByteArray(new File(commonPartFileName));
        String commonPartXML = new String(b);
        return createItemInAuthority(vcsid, commonPartXML, client );
    }    

    /**
     * Creates the exhibitionAuthority ref name.
     *
     * @param shortId the exhibitionAuthority shortIdentifier
     * @param displaySuffix displayName to be appended, if non-null
     * @return the string
     */
    public static String createExhibitionAuthRefName(String shortId, String displaySuffix) {
        String refName = "urn:cspace:org.collectionspace.demo:exhibitionauthority:name("
            +shortId+")";
        if(displaySuffix!=null&&!displaySuffix.isEmpty())
            refName += "'"+displaySuffix+"'";
        return refName;
    }

    /**
     * Creates the exhibition ref name.
     *
     * @param exhibitionAuthRefName the exhibitionAuthority ref name
     * @param shortId the exhibition shortIdentifier
     * @param displaySuffix displayName to be appended, if non-null
     * @return the string
     */
    public static String createExhibitionRefName(
                            String exhibitionAuthRefName, String shortId, String displaySuffix) {
        String refName = exhibitionAuthRefName+":exhibition:name("+shortId+")";
        if(displaySuffix!=null&&!displaySuffix.isEmpty())
            refName += "'"+displaySuffix+"'";
        return refName;
    }

    public static String extractId(ClientResponse<Response> res) {
        MultivaluedMap<String, Object> mvm = res.getMetadata();
        String uri = (String) ((ArrayList<Object>) mvm.get("Location")).get(0);
        if(logger.isDebugEnabled()){
            logger.debug("extractId:uri=" + uri);
        }
        String[] segments = uri.split("/");
        String id = segments[segments.length - 1];
        if(logger.isDebugEnabled()){
            logger.debug("id=" + id);
        }
        return id;
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
    public static String invalidStatusCodeMessage(ServiceRequestType requestType, int statusCode) {
        return "Status code '" + statusCode + "' in response is NOT within the expected set: " +
                requestType.validStatusCodesAsString();
    }


    
    /**
     * Produces a default displayName from one or more supplied field(s).
     * @see ExhibitionAuthorityDocumentModelHandler.prepareDefaultDisplayName() which
     * duplicates this logic, until we define a service-general utils package
     * that is neither client nor service specific.
     * @param exhibitionName  
     * @return a display name
     */
    public static String prepareDefaultDisplayName(
            String exhibitionName ) {
        StringBuilder newStr = new StringBuilder();
            newStr.append(exhibitionName);
        return newStr.toString();
    }
    
    public static List<ExhibitionTermGroup> getTermGroupInstance(String identifier) {
        if (Tools.isBlank(identifier)) {
            identifier = getGeneratedIdentifier();
        }
        List<ExhibitionTermGroup> terms = new ArrayList<ExhibitionTermGroup>();
        ExhibitionTermGroup term = new ExhibitionTermGroup();
        term.setTermDisplayName(identifier);
        term.setTermName(identifier);
        terms.add(term);
        return terms;
    }
    
    private static String getGeneratedIdentifier() {
        return "id" + new Date().getTime(); 
   }
    
}