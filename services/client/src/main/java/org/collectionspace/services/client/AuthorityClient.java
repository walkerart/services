package org.collectionspace.services.client;

import javax.ws.rs.core.Response;

import org.collectionspace.services.common.authorityref.AuthorityRefDocList;
import org.collectionspace.services.common.authorityref.AuthorityRefList;
import org.collectionspace.services.jaxb.AbstractCommonList;
import org.jboss.resteasy.client.ClientResponse;

public interface AuthorityClient<TL extends AbstractCommonList, T extends AuthorityProxy<TL>> extends CollectionSpacePoxClient<T> {	
	/*
	 * Basic CRUD operations
	 */
	
    //(C)reate Item
    ClientResponse<Response> createItem(String vcsid, PoxPayloadOut poxPayloadOut);

    //(R)ead Item
    ClientResponse<String> readItem(String vcsid, String csid);

    //(U)pdate Item
    ClientResponse<String> updateItem(String vcsid, String csid, PoxPayloadOut poxPayloadOut);

    //(D)elete Item
    ClientResponse<Response> deleteItem(String vcsid, String csid);
    
    // Get a list of objects that
    ClientResponse<AuthorityRefDocList> getReferencingObjects(
            String parentcsid,
            String itemcsid);
    /**
     * Get a list of objects that reference a given authority term.
     * 
     * @param parentcsid 
     * @param itemcsid 
     * @param csid
     * @return
     * @see org.collectionspace.services.client.IntakeProxy#getAuthorityRefs(java.lang.String)
     */
    public ClientResponse<AuthorityRefList> getItemAuthorityRefs(String parentcsid, String itemcsid);    
    
    /*
     * 
     */
    
    ClientResponse<String> readByName(String name);
    
    /*
     * Item subresource methods
     */
    
    /**
     * Read named item.
     *
     * @param vcsid the vcsid
     * @param shortId the shortIdentifier
     * @return the client response
     */
    public ClientResponse<String> readNamedItem(String vcsid, String shortId);

    /**
     * Read item in Named Authority.
     *
     * @param authShortId the shortIdentifier for the Authority
     * @param csid the csid
     * @return the client response
     */
    public ClientResponse<String> readItemInNamedAuthority(String authShortId, String csid);

    /**
     * Read named item in Named Authority.
     *
     * @param authShortId the shortIdentifier for the Authority
     * @param itemShortId the shortIdentifier for the item
     * @return the client response
     */
    public ClientResponse<String> readNamedItemInNamedAuthority(String authShortId, String itemShortId);
    
    /**
     * Read item list, filtering by partial term match, or keywords. Only one of
     * partialTerm or keywords should be specified. If both are specified, keywords
     * will be ignored.
     *
     * @param inAuthority the parent authority
     * @param partialTerm A partial term on which to match,
     *     which will filter list results to return only matched resources.
     * @param keywords A set of keywords on which to match,
     *     which will filter list results to return only matched resources.
     * @return the client response
     */
    public ClientResponse<TL> readItemList(String inAuthority, String partialTerm, String keywords);
    
    /**
     * Read item list for named vocabulary, filtering by partial term match, or keywords. Only one of
     * partialTerm or keywords should be specified. If both are specified, keywords
     * will be ignored.
     *
     * @param specifier the specifier
     * @param partialTerm A partial term on which to match,
     *     which will filter list results to return only matched resources.
     * @param keywords A set of keywords on which to match,
     *     which will filter list results to return only matched resources.
     * @return the client response
     */
    public ClientResponse<TL> readItemListForNamedAuthority(String specifier, 
    		String partialTerm, String keywords);
    
}
