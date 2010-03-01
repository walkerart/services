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

 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.collectionspace.services.common.vocabulary;

import org.collectionspace.services.common.document.AbstractMultipartDocumentHandlerImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * RefNameUtils is a collection of utilities related to refName URN strings
 * refNames are URNs that reference a document entity, often an authority or 
 * authority term. They are strings that take the form (for authorities):
 * urn:cspace:org.collectionspace.demo:vocabulary:name(Entry Methods)'Entry Methods'
 * or the form (for authority terms):
 * urn:cspace:org.collectionspace.demo:vocabulary:name(Entry Methods):item:name(Loan)'Loan'
 *
 * $LastChangedRevision: $
 * $LastChangedDate: $
 */
public class RefNameUtils {

    private final Logger logger = LoggerFactory.getLogger(RefNameUtils.class);

    private static final String URN_PREFIX = "urn:cspace:";
	// FIXME Should not be hard-coded
    private static final String ITEMS_REGEX = "item|person|organization";
    // In a list of tokens, these are indices for each part
    private static final int DOMAIN_TOKEN = 0; 			// e.g., 'org.collectionspace.demo'
    private static final int RESOURCE_TOKEN = 1; 		// vocabulary, personauthority, etc.
    private static final int AUTH_INSTANCE_TOKEN = 2;	// name(Entry Methods)'Entry Methods'
    private static final int ITEMS_TOKEN = 3;			// 'item', 'person', etc.
    private static final int ITEM_INSTANCE_TOKEN = 4;	// name(Entry Methods)'Entry Methods'
    // Tokenizing the INSTANCE, these are indices for each item-part
    private static final int INSTANCE_SPEC_TYPE_TOKEN = 0;	// 'name' or 'id' 
    private static final int INSTANCE_SPEC_TOKEN = 1; 		// name or id value
    private static final int INSTANCE_DISPLAYNAME_TOKEN = 2;// optional displayName suffix
    private static final int INSTANCE_TOKENS_MIN = 2;
    private static final int INSTANCE_TOKENS_MAX = 3;
    private static final int prefixLen = 11;
    private static final String SEPARATOR = ":";

    public static class AuthorityInfo {
        private final Logger logger = LoggerFactory.getLogger(AuthorityInfo.class);
    	private static int MIN_TOKENS = 3;
        public String domain;
        public String resource;
        public String csid;
        public String name;
        public String displayName;
        
        public AuthorityInfo(String refNameTokens[]) throws Exception {
        	try {
	        	if(refNameTokens.length < MIN_TOKENS) {
	        		throw new IllegalArgumentException("Malformed refName for Authority (too few tokens)");
	        	}
	        	this.domain = refNameTokens[DOMAIN_TOKEN]; 
	        	this.resource = refNameTokens[RESOURCE_TOKEN];
	        	String idTokens[] = refNameTokens[AUTH_INSTANCE_TOKEN].split("[()]", INSTANCE_TOKENS_MAX);
	        	if(idTokens.length<INSTANCE_TOKENS_MIN) {
	        		throw new IllegalArgumentException("Missing/malformed identifier");
	        	}
	        	if(idTokens[INSTANCE_SPEC_TYPE_TOKEN].equals("name")) {
	        		this.name = idTokens[INSTANCE_SPEC_TOKEN];
	        		this.csid = null;
	        	} else if(idTokens[INSTANCE_SPEC_TYPE_TOKEN].startsWith("id")) {
	        		this.csid = idTokens[INSTANCE_SPEC_TOKEN];
	        		this.name = null;
	        	} else {
	        		throw new IllegalArgumentException("Identifier type must be 'name' or 'id'");
	        	}
	        	// displayName is always in quotes, so must have at least 3 chars
	    		this.displayName = 
	    			((idTokens.length<INSTANCE_TOKENS_MAX)||(idTokens[INSTANCE_DISPLAYNAME_TOKEN].length()<3))? null:
	    			idTokens[INSTANCE_DISPLAYNAME_TOKEN].substring(1, idTokens[INSTANCE_DISPLAYNAME_TOKEN].length()-1);
        	} catch (Exception e) {
	            if (logger.isDebugEnabled()) {
	            	logger.debug("Problem Building AuthorityInfo from tokens: " 
	            			+ RefNameUtils.implodeStringArray(refNameTokens, ", "));
	            }
	            throw e;
        	}
        }
        
        public String getRelativeUri() {
        	StringBuilder uri = new StringBuilder();
        	// FIXME This should not be hard-coded.
        	if(resource.equals("vocabulary")) {
        		uri.append("/vocabularies/");
        	} else if(resource.equals("personauthority")) {
        		uri.append("/personauthorities/");
        	} else if(resource.equals("orgauthority")) {
        		uri.append("/orgauthorities/");
        	} else {
        		throw new RuntimeException("Illegal Authority Type: " + resource);
        	}
        	if(csid!=null) {
        		uri.append(csid);
        	} else if(name!=null) {
        		uri.append("urn:cspace:name("+name+")");
        	} else {
        		throw new RuntimeException("Missing id/name specifier");
        	}
        	return uri.toString();
        }
    };

    public static class AuthorityTermInfo {
        private final Logger logger = LoggerFactory.getLogger(AuthorityTermInfo.class);
    	private static int MIN_TOKENS = 5;
    	public AuthorityInfo inAuthority;
    	public String csid;
    	public String name;
    	public String displayName;
        
        public AuthorityTermInfo(String refNameTokens[]) throws Exception {
        	try {
	        	if(refNameTokens.length < MIN_TOKENS) {
	        		throw new IllegalArgumentException("Malformed refName for AuthorityTerm (too few tokens)");
	        	}
	        	this.inAuthority = new AuthorityInfo(refNameTokens); 
	        	if(!refNameTokens[ITEMS_TOKEN].matches(ITEMS_REGEX)) {
	        		throw new IllegalArgumentException("Item spec must be one of: "+ITEMS_REGEX);
	        	}
	        	String idTokens[] = refNameTokens[ITEM_INSTANCE_TOKEN].split("[\\(\\)]", 3);
	        	if(idTokens.length<INSTANCE_TOKENS_MIN) {
	        		throw new IllegalArgumentException("Missing/malformed identifier");
	        	}
	        	if(idTokens[INSTANCE_SPEC_TYPE_TOKEN].equals("name")) {
	        		this.name = idTokens[INSTANCE_SPEC_TOKEN];
	        		this.csid = null;
	        	} else if(idTokens[INSTANCE_SPEC_TYPE_TOKEN].startsWith("id")) {
	        		this.csid = idTokens[INSTANCE_SPEC_TOKEN];
	        		this.name = null;
	        	} else {
	        		throw new IllegalArgumentException("Identifier type must be 'name' or 'id'");
	        	}
	        	// displayName is always in quotes, so must have at least 3 chars
	        	this.displayName = 
	        		((idTokens.length<INSTANCE_TOKENS_MAX)||(idTokens[INSTANCE_DISPLAYNAME_TOKEN].length()<3))? null:
	    			idTokens[INSTANCE_DISPLAYNAME_TOKEN].substring(1, idTokens[INSTANCE_DISPLAYNAME_TOKEN].length()-1);
        	} catch (Exception e) {
	            if (logger.isDebugEnabled()) {
	            	logger.debug("Problem Building AuthorityTermInfo from tokens: " 
	            			+ RefNameUtils.implodeStringArray(refNameTokens, ", "));
	            }
	            throw e;
        	}
        }
        
        public String getRelativeUri() {
        	StringBuilder uri = new StringBuilder(inAuthority.getRelativeUri()+"/items/");
        	if(csid!=null) {
        		uri.append(csid);
        	} else if(name!=null) {
        		uri.append("urn:cspace:name("+name+")");
        	} else {
        		throw new RuntimeException("Missing id/name specifier");
        	}
        	return uri.toString();
        }
    };

    public static AuthorityInfo parseAuthorityInfo(String refName)
            throws Exception {
    	if(refName==null || !refName.startsWith(URN_PREFIX))
    		throw new RuntimeException( "Null or invalid refName syntax");
    	return new AuthorityInfo(refName.substring(prefixLen).split(SEPARATOR));
    }

    public static AuthorityTermInfo parseAuthorityTermInfo(String refName)
            throws Exception {
    	if(refName==null || !refName.startsWith(URN_PREFIX))
    		throw new RuntimeException( "Null or invalid refName syntax");
    	return new AuthorityTermInfo(refName.substring(prefixLen).split(SEPARATOR));
    }

    public static String implodeStringArray(String tokens[], String separator) {
    	if (tokens.length==0) {
    		return "";
    	} else {
    		StringBuffer sb = new StringBuffer();
    		sb.append(tokens[0]);
    		for (int i=1;i<tokens.length;i++) {
    			sb.append(separator);
    			sb.append(tokens[i]);
    		}
    		return sb.toString();
    	}
    }
    
}

