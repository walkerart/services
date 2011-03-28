/**	
 * RoleClient.java
 *
 * {Purpose of This Class}
 *
 * {Other Notes Relating to This Class (Optional)}
 *
 * $LastChangedBy: $
 * $LastChangedRevision: $
 * $LastChangedDate: $
 *
 * This document is a part of the source code and related artifacts
 * for CollectionSpace, an open source collections management system
 * for museums and related institutions:
 *
 * http://www.collectionspace.org
 * http://wiki.collectionspace.org
 *
 * Copyright (C) 2009 {Contributing Institution}
 *
 * Licensed under the Educational Community License (ECL), Version 2.0.
 * You may not use this file except in compliance with this License.
 *
 * You may obtain a copy of the ECL 2.0 License at
 * https://source.collectionspace.org/collection-space/LICENSE.txt
 */
package org.collectionspace.services.client;

import javax.ws.rs.core.Response;


import org.collectionspace.services.authorization.Role;
import org.collectionspace.services.authorization.RolesList;
import org.jboss.resteasy.client.ClientResponse;

/**
 * A RoleClient.

 * @version $Revision:$
 */
public class RoleClient extends AbstractServiceClientImpl<RoleProxy> {
	public static final String SERVICE_NAME = "authorization/roles";
	public static final String SERVICE_PATH_COMPONENT = SERVICE_NAME;	
	public static final String SERVICE_PATH = "/" + SERVICE_PATH_COMPONENT;
	public static final String SERVICE_PATH_PROXY = SERVICE_PATH + "/";	

    @Override
    public String getServiceName() { 
    	throw new UnsupportedOperationException(); //FIXME: REM - http://issues.collectionspace.org/browse/CSPACE-3498 }
    }
    
    /* (non-Javadoc)
     * @see org.collectionspace.services.client.AbstractServiceClientImpl#getServicePathComponent()
     */
    @Override
	public String getServicePathComponent() {
        return SERVICE_PATH_COMPONENT;
    }

    /**
     * @return
     * @see org.collectionspace.hello.client.RoleProxy#readList()
     */
    public ClientResponse<RolesList> readList() {
        return getProxy().readList();

    }

    public ClientResponse<RolesList> readSearchList(String roleName) {
        return getProxy().readSearchList(roleName);

    }

    /**
     * @param csid
     * @return
     * @see org.collectionspace.hello.client.RoleProxy#getAccount(java.lang.String)
     */
    public ClientResponse<Role> read(String csid) {
        return getProxy().read(csid);
    }

    /**
     * Creates the.
     *
     * @param role the role
     * @return the client response
     */
    public ClientResponse<Response> create(Role role) {
        return getProxy().create(role);
    }

    /**
     * @param csid
     * @param role
     * @return
     * @see org.collectionspace.hello.client.RoleProxy#updateAccount(java.lang.Long, org.collectionspace.services.role.Role)
     */
    public ClientResponse<Role> update(String csid, Role role) {
        return getProxy().update(csid, role);
    }

	@Override
	public Class<RoleProxy> getProxyClass() {
		return RoleProxy.class;
	}
}
