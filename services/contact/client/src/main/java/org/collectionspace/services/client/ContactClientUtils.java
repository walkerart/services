package org.collectionspace.services.client;

import javax.ws.rs.core.MediaType;

import org.collectionspace.services.client.ContactClient;
import org.collectionspace.services.contact.ContactsCommon;
//import org.jboss.resteasy.plugins.providers.multipart.OutputPart;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ContactClientUtils {

    private static final Logger logger =
        LoggerFactory.getLogger(ContactClientUtils.class);

    public static PoxPayloadOut createContactInstance(String identifier, String headerLabel) {
        final String inAuthority = "";
        final String inItem = "";
        return createContactInstance(
            inAuthority,
            inItem,
            "addressType-" + identifier,
            "addressPlace-" + identifier,
            "emakl-" + identifier,
            headerLabel);
    }

    public static PoxPayloadOut createContactInstance(
        String inAuthority, String inItem, String identifier, String headerLabel) {
        return createContactInstance(
            inAuthority,
            inItem,
            "addressType-" + identifier,
            "addressPlace-" + identifier,
            "emakl-" + identifier,
            headerLabel);
    }

    public static PoxPayloadOut createContactInstance(
        String inAuthority, String inItem, String addressType,
        String addressPlace, String email, String headerLabel) {
        ContactsCommon contact = new ContactsCommon();
        contact.setInAuthority(inAuthority);
        contact.setInItem(inItem);
        contact.setAddressType(addressType);
        contact.setAddressPlace(addressPlace);
        contact.setEmail(email);
        PoxPayloadOut multipart = new PoxPayloadOut(ContactClient.SERVICE_PAYLOAD_NAME);
        @SuppressWarnings("deprecation")
		PayloadOutputPart commonPart =
            multipart.addPart(contact, MediaType.APPLICATION_XML_TYPE);
//        ContactClient client = new ContactClient();
        commonPart.setLabel(headerLabel);

        if(logger.isDebugEnabled()){
            logger.debug("to be created, contact common");
            // logger.debug(objectAsXmlString(contact, ContactsCommon.class));
        }

        return multipart;
    }


}
