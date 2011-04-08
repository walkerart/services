/**
 * RoleProxy.java
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

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;


import org.collectionspace.services.authorization.Role;
import org.collectionspace.services.authorization.RolesList;
import org.jboss.resteasy.client.ClientResponse;

/**
 * @version $Revision:$
 */
@Path("/authorization/roles")
@Produces({"application/xml"})
@Consumes({"application/xml"})
public interface RoleProxy extends CollectionSpaceProxy<RolesList> {

    @GET
    @Produces({"application/xml"})
    ClientResponse<RolesList> readList();

    @GET
    ClientResponse<RolesList> readSearchList(@QueryParam("r") String roleName);

    //(C)reate
    @POST
    ClientResponse<Response> create(Role role);

    //(R)ead
    @GET
    @Path("/{csid}")
    ClientResponse<Role> read(@PathParam("csid") String csid);

    //(U)pdate
    @PUT
    @Path("/{csid}")
    ClientResponse<Role> update(@PathParam("csid") String csid, Role role);
}
