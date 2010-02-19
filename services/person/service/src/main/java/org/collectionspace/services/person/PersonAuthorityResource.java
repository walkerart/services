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
package org.collectionspace.services.person;

import java.net.URI;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import org.collectionspace.services.PersonJAXBSchema;
import org.collectionspace.services.common.AbstractCollectionSpaceResourceImpl;
import org.collectionspace.services.common.ClientType;
import org.collectionspace.services.common.ServiceMain;
import org.collectionspace.services.common.context.MultipartServiceContext;
import org.collectionspace.services.common.context.MultipartServiceContextFactory;
import org.collectionspace.services.common.context.ServiceContext;
import org.collectionspace.services.common.document.BadRequestException;
import org.collectionspace.services.common.document.DocumentFilter;
import org.collectionspace.services.common.document.DocumentHandler;
import org.collectionspace.services.common.document.DocumentNotFoundException;
import org.collectionspace.services.common.security.UnauthorizedException;
import org.collectionspace.services.common.query.IQueryManager;
import org.collectionspace.services.contact.ContactResource;
import org.collectionspace.services.contact.ContactsCommon;
import org.collectionspace.services.contact.ContactsCommonList;
import org.collectionspace.services.contact.ContactJAXBSchema;
import org.collectionspace.services.contact.nuxeo.ContactDocumentModelHandler;
import org.collectionspace.services.person.nuxeo.PersonDocumentModelHandler;
import org.jboss.resteasy.plugins.providers.multipart.MultipartInput;
import org.jboss.resteasy.plugins.providers.multipart.MultipartOutput;
import org.jboss.resteasy.util.HttpResponseCodes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/personauthorities")
@Consumes("multipart/mixed")
@Produces("multipart/mixed")
public class PersonAuthorityResource extends AbstractCollectionSpaceResourceImpl {

    private final static String personAuthorityServiceName = "personauthorities";
    private final static String personServiceName = "persons";
    final Logger logger = LoggerFactory.getLogger(PersonAuthorityResource.class);
    //FIXME retrieve client type from configuration
    final static ClientType CLIENT_TYPE = ServiceMain.getInstance().getClientType();
    private ContactResource contactResource = new ContactResource();

    public PersonAuthorityResource() {
        // do nothing
    }

    @Override
    protected String getVersionString() {
    	/** The last change revision. */
    	final String lastChangeRevision = "$LastChangedRevision$";
    	return lastChangeRevision;
    }
    
    @Override
    public String getServiceName() {
        return personAuthorityServiceName;
    }

    public String getItemServiceName() {
        return personServiceName;
    }

    public String getContactServiceName() {
        return contactResource.getServiceName();
    }

    /*
    public RemoteServiceContext createItemServiceContext(MultipartInput input) throws Exception {
    RemoteServiceContext ctx = new RemoteServiceContextImpl(getItemServiceName());
    ctx.setInput(input);
    return ctx;
    }
     */
    @Override
    public DocumentHandler createDocumentHandler(ServiceContext ctx) throws Exception {
        DocumentHandler docHandler = ctx.getDocumentHandler();
        if (ctx.getInput() != null) {
            Object obj = ((MultipartServiceContext) ctx).getInputPart(ctx.getCommonPartLabel(), PersonauthoritiesCommon.class);
            if (obj != null) {
                docHandler.setCommonPart((PersonauthoritiesCommon) obj);
            }
        }
        return docHandler;
    }

    private DocumentHandler createItemDocumentHandler(
            ServiceContext ctx,
            String inAuthority) throws Exception {
        DocumentHandler docHandler = ctx.getDocumentHandler();
        ((PersonDocumentModelHandler) docHandler).setInAuthority(inAuthority);
        if (ctx.getInput() != null) {
            Object obj = ((MultipartServiceContext) ctx).getInputPart(ctx.getCommonPartLabel(getItemServiceName()),
                    PersonsCommon.class);
            if (obj != null) {
                docHandler.setCommonPart((PersonsCommon) obj);
            }
        }
        return docHandler;
    }

    private DocumentHandler createContactDocumentHandler(
            ServiceContext ctx, String inAuthority,
            String inItem) throws Exception {
        DocumentHandler docHandler = ctx.getDocumentHandler();
        // Set the inAuthority and inItem values, which specify the
        // parent authority (e.g. PersonAuthority, OrgAuthority) and the item
        // (e.g. Person, Organization) with which the Contact is associated.
        ((ContactDocumentModelHandler) docHandler).setInAuthority(inAuthority);
        ((ContactDocumentModelHandler) docHandler).setInItem(inItem);
        if (ctx.getInput() != null) {
            Object obj = ((MultipartServiceContext) ctx)
                .getInputPart(ctx.getCommonPartLabel(getContactServiceName()),
                ContactsCommon.class);
            if (obj != null) {
                docHandler.setCommonPart((ContactsCommon) obj);
            }
        }
        return docHandler;
    }


    @POST
    public Response createPersonAuthority(MultipartInput input) {
        try {
            ServiceContext ctx = MultipartServiceContextFactory.get().createServiceContext(input, getServiceName());
            DocumentHandler handler = createDocumentHandler(ctx);
            String csid = getRepositoryClient(ctx).create(ctx, handler);
            //personAuthorityObject.setCsid(csid);
            UriBuilder path = UriBuilder.fromResource(PersonAuthorityResource.class);
            path.path("" + csid);
            Response response = Response.created(path.build()).build();
            return response;
        } catch (UnauthorizedException ue) {
            Response response = Response.status(
                    Response.Status.UNAUTHORIZED).entity("Create failed reason " + ue.getErrorReason()).type("text/plain").build();
            throw new WebApplicationException(response);
        } catch (Exception e) {
            if (logger.isDebugEnabled()) {
                logger.debug("Caught exception in createPersonAuthority", e);
            }
            Response response = Response.status(
                    Response.Status.INTERNAL_SERVER_ERROR).entity("Create failed").type("text/plain").build();
            throw new WebApplicationException(response);
        }
    }

    @GET
    @Path("{csid}")
    public MultipartOutput getPersonAuthority(@PathParam("csid") String csid) {
        String idValue = null;
        if (csid == null) {
            logger.error("getPersonAuthority: missing csid!");
            Response response = Response.status(Response.Status.BAD_REQUEST).entity(
                    "get failed on PersonAuthority csid=" + csid).type(
                    "text/plain").build();
            throw new WebApplicationException(response);
        }
        if (logger.isDebugEnabled()) {
            logger.debug("getPersonAuthority with path(id)=" + csid);
        }
        MultipartOutput result = null;
        try {
            ServiceContext ctx = MultipartServiceContextFactory.get().createServiceContext(null, getServiceName());
            DocumentHandler handler = createDocumentHandler(ctx);
            getRepositoryClient(ctx).get(ctx, csid, handler);
            result = (MultipartOutput) ctx.getOutput();
        } catch (UnauthorizedException ue) {
            Response response = Response.status(
                    Response.Status.UNAUTHORIZED).entity("Get failed reason " + ue.getErrorReason()).type("text/plain").build();
            throw new WebApplicationException(response);
        } catch (DocumentNotFoundException dnfe) {
            if (logger.isDebugEnabled()) {
                logger.debug("getPersonAuthority", dnfe);
            }
            Response response = Response.status(Response.Status.NOT_FOUND).entity(
                    "Get failed on PersonAuthority csid=" + csid).type(
                    "text/plain").build();
            throw new WebApplicationException(response);
        } catch (Exception e) {
            if (logger.isDebugEnabled()) {
                logger.debug("getPersonAuthority", e);
            }
            Response response = Response.status(
                    Response.Status.INTERNAL_SERVER_ERROR).entity("Get failed").type("text/plain").build();
            throw new WebApplicationException(response);
        }
        if (result == null) {
            Response response = Response.status(Response.Status.NOT_FOUND).entity(
                    "Get failed, the requested PersonAuthority CSID:" + csid + ": was not found.").type(
                    "text/plain").build();
            throw new WebApplicationException(response);
        }
        return result;
    }

    @GET
    @Produces("application/xml")
    public PersonauthoritiesCommonList getPersonAuthorityList(@Context UriInfo ui) {
        PersonauthoritiesCommonList personAuthorityObjectList = new PersonauthoritiesCommonList();
        try {
            ServiceContext ctx = MultipartServiceContextFactory.get().createServiceContext(null, getServiceName());
            MultivaluedMap<String, String> queryParams = ui.getQueryParameters();
            DocumentHandler handler = createDocumentHandler(ctx);
            DocumentFilter myFilter = new DocumentFilter();
            myFilter.setPagination(queryParams);
            String nameQ = queryParams.getFirst("refName");
            if (nameQ != null) {
                myFilter.setWhereClause("personauthorities_common:refName='" + nameQ + "'");
            }
            handler.setDocumentFilter(myFilter);
            getRepositoryClient(ctx).getFiltered(ctx, handler);
            personAuthorityObjectList = (PersonauthoritiesCommonList) handler.getCommonPartList();
        } catch (UnauthorizedException ue) {
            Response response = Response.status(
                    Response.Status.UNAUTHORIZED).entity("Index failed reason " + ue.getErrorReason()).type("text/plain").build();
            throw new WebApplicationException(response);
        } catch (Exception e) {
            if (logger.isDebugEnabled()) {
                logger.debug("Caught exception in getPersonAuthorityList", e);
            }
            Response response = Response.status(
                    Response.Status.INTERNAL_SERVER_ERROR).entity("Index failed").type("text/plain").build();
            throw new WebApplicationException(response);
        }
        return personAuthorityObjectList;
    }

    @PUT
    @Path("{csid}")
    public MultipartOutput updatePersonAuthority(
            @PathParam("csid") String csid,
            MultipartInput theUpdate) {
        if (logger.isDebugEnabled()) {
            logger.debug("updatePersonAuthority with csid=" + csid);
        }
        if (csid == null || "".equals(csid)) {
            logger.error("updatePersonAuthority: missing csid!");
            Response response = Response.status(Response.Status.BAD_REQUEST).entity(
                    "update failed on PersonAuthority csid=" + csid).type(
                    "text/plain").build();
            throw new WebApplicationException(response);
        }
        MultipartOutput result = null;
        try {
            ServiceContext ctx = MultipartServiceContextFactory.get().createServiceContext(theUpdate, getServiceName());
            DocumentHandler handler = createDocumentHandler(ctx);
            getRepositoryClient(ctx).update(ctx, csid, handler);
            result = (MultipartOutput) ctx.getOutput();
        } catch (UnauthorizedException ue) {
            Response response = Response.status(
                    Response.Status.UNAUTHORIZED).entity("Update failed reason " + ue.getErrorReason()).type("text/plain").build();
            throw new WebApplicationException(response);
        } catch (DocumentNotFoundException dnfe) {
            if (logger.isDebugEnabled()) {
                logger.debug("caugth exception in updatePersonAuthority", dnfe);
            }
            Response response = Response.status(Response.Status.NOT_FOUND).entity(
                    "Update failed on PersonAuthority csid=" + csid).type(
                    "text/plain").build();
            throw new WebApplicationException(response);
        } catch (Exception e) {
            Response response = Response.status(
                    Response.Status.INTERNAL_SERVER_ERROR).entity("Update failed").type("text/plain").build();
            throw new WebApplicationException(response);
        }
        return result;
    }

    @DELETE
    @Path("{csid}")
    public Response deletePersonAuthority(@PathParam("csid") String csid) {

        if (logger.isDebugEnabled()) {
            logger.debug("deletePersonAuthority with csid=" + csid);
        }
        if (csid == null || "".equals(csid)) {
            logger.error("deletePersonAuthority: missing csid!");
            Response response = Response.status(Response.Status.BAD_REQUEST).entity(
                    "delete failed on PersonAuthority csid=" + csid).type(
                    "text/plain").build();
            throw new WebApplicationException(response);
        }
        try {
            ServiceContext ctx = MultipartServiceContextFactory.get().createServiceContext(null, getServiceName());
            getRepositoryClient(ctx).delete(ctx, csid);
            return Response.status(HttpResponseCodes.SC_OK).build();
        } catch (UnauthorizedException ue) {
            Response response = Response.status(
                    Response.Status.UNAUTHORIZED).entity("Delete failed reason " + ue.getErrorReason()).type("text/plain").build();
            throw new WebApplicationException(response);
        } catch (DocumentNotFoundException dnfe) {
            if (logger.isDebugEnabled()) {
                logger.debug("caught exception in deletePersonAuthority", dnfe);
            }
            Response response = Response.status(Response.Status.NOT_FOUND).entity(
                    "Delete failed on PersonAuthority csid=" + csid).type(
                    "text/plain").build();
            throw new WebApplicationException(response);
        } catch (Exception e) {
            Response response = Response.status(
                    Response.Status.INTERNAL_SERVER_ERROR).entity("Delete failed").type("text/plain").build();
            throw new WebApplicationException(response);
        }

    }

    /*************************************************************************
     * Person parts - this is a sub-resource of PersonAuthority
     *************************************************************************/
    @POST
    @Path("{csid}/items")
    public Response createPerson(@PathParam("csid") String parentcsid, MultipartInput input) {
        try {
            ServiceContext ctx = MultipartServiceContextFactory.get().createServiceContext(input, getItemServiceName());
            DocumentHandler handler = createItemDocumentHandler(ctx, parentcsid);
            String itemcsid = getRepositoryClient(ctx).create(ctx, handler);
            UriBuilder path = UriBuilder.fromResource(PersonAuthorityResource.class);
            path.path(parentcsid + "/items/" + itemcsid);
            Response response = Response.created(path.build()).build();
            return response;
        } catch (BadRequestException bre) {
            Response response = Response.status(
                    Response.Status.BAD_REQUEST).entity("Create failed reason " + bre.getErrorReason()).type("text/plain").build();
            throw new WebApplicationException(response);
        } catch (UnauthorizedException ue) {
            Response response = Response.status(
                    Response.Status.UNAUTHORIZED).entity("Create failed reason " + ue.getErrorReason()).type("text/plain").build();
            throw new WebApplicationException(response);
        } catch (Exception e) {
            if (logger.isDebugEnabled()) {
                logger.debug("Caught exception in createPerson", e);
            }
            Response response = Response.status(
                    Response.Status.INTERNAL_SERVER_ERROR).entity("Create failed").type("text/plain").build();
            throw new WebApplicationException(response);
        }
    }

    @GET
    @Path("{csid}/items/{itemcsid}")
    public MultipartOutput getPerson(
            @PathParam("csid") String parentcsid,
            @PathParam("itemcsid") String itemcsid) {
        if (logger.isDebugEnabled()) {
            logger.debug("getPerson with parentcsid=" + parentcsid + " and itemcsid=" + itemcsid);
        }
        if (parentcsid == null || "".equals(parentcsid)) {
            logger.error("getPerson: missing csid!");
            Response response = Response.status(Response.Status.BAD_REQUEST).entity(
                    "get failed on Person csid=" + parentcsid).type(
                    "text/plain").build();
            throw new WebApplicationException(response);
        }
        if (itemcsid == null || "".equals(itemcsid)) {
            logger.error("getPerson: missing itemcsid!");
            Response response = Response.status(Response.Status.BAD_REQUEST).entity(
                    "get failed on Person itemcsid=" + itemcsid).type(
                    "text/plain").build();
            throw new WebApplicationException(response);
        }
        MultipartOutput result = null;
        try {
            // Note that we have to create the service context for the Items, not the main service
            ServiceContext ctx = MultipartServiceContextFactory.get().createServiceContext(null, getItemServiceName());
            DocumentHandler handler = createItemDocumentHandler(ctx, parentcsid);
            getRepositoryClient(ctx).get(ctx, itemcsid, handler);
            // TODO should we assert that the item is in the passed personAuthority?
            result = (MultipartOutput) ctx.getOutput();
        } catch (UnauthorizedException ue) {
            Response response = Response.status(
                    Response.Status.UNAUTHORIZED).entity("Get failed reason " + ue.getErrorReason()).type("text/plain").build();
            throw new WebApplicationException(response);
        } catch (DocumentNotFoundException dnfe) {
            if (logger.isDebugEnabled()) {
                logger.debug("getPerson", dnfe);
            }
            Response response = Response.status(Response.Status.NOT_FOUND).entity(
                    "Get failed on Person csid=" + itemcsid).type(
                    "text/plain").build();
            throw new WebApplicationException(response);
        } catch (Exception e) {
            if (logger.isDebugEnabled()) {
                logger.debug("getPerson", e);
            }
            Response response = Response.status(
                    Response.Status.INTERNAL_SERVER_ERROR).entity("Get failed").type("text/plain").build();
            throw new WebApplicationException(response);
        }
        if (result == null) {
            Response response = Response.status(Response.Status.NOT_FOUND).entity(
                    "Get failed, the requested Person CSID:" + itemcsid + ": was not found.").type(
                    "text/plain").build();
            throw new WebApplicationException(response);
        }
        return result;
    }

    @GET
    @Path("{csid}/items")
    @Produces("application/xml")
    public PersonsCommonList getPersonList(
            @PathParam("csid") String parentcsid,
            @QueryParam (IQueryManager.SEARCH_TYPE_PARTIALTERM) String partialTerm,            
            @Context UriInfo ui) {
        PersonsCommonList personObjectList = new PersonsCommonList();
        try {
            // Note that docType defaults to the ServiceName, so we're fine with that.
            ServiceContext ctx = MultipartServiceContextFactory.get().createServiceContext(null, getItemServiceName());
            DocumentHandler handler = createItemDocumentHandler(ctx, parentcsid);
            MultivaluedMap<String, String> queryParams = ui.getQueryParameters();
            DocumentFilter myFilter = new DocumentFilter();
            myFilter.setPagination(queryParams);

            // Add the where clause "persons_common:inAuthority='" + parentcsid + "'"
            myFilter.setWhereClause(PersonJAXBSchema.PERSONS_COMMON + ":" +
            		PersonJAXBSchema.IN_AUTHORITY +
            		"='" + parentcsid + "'" + "AND ecm:isProxy = 0");
            
            // AND persons_common:displayName LIKE '%partialTerm%'
            if (partialTerm != null && !partialTerm.isEmpty()) {
            	String ptClause = "AND " +
            	PersonJAXBSchema.PERSONS_COMMON + ":" +
            		PersonJAXBSchema.DISPLAY_NAME +
            		" LIKE " +
            		"'%" + partialTerm + "%'";
            	myFilter.appendWhereClause(ptClause);
            }
            
            handler.setDocumentFilter(myFilter);
            getRepositoryClient(ctx).getFiltered(ctx, handler);
            personObjectList = (PersonsCommonList) handler.getCommonPartList();
        } catch (UnauthorizedException ue) {
            Response response = Response.status(
                    Response.Status.UNAUTHORIZED).entity("Index failed reason " + ue.getErrorReason()).type("text/plain").build();
            throw new WebApplicationException(response);
        } catch (Exception e) {
            if (logger.isDebugEnabled()) {
                logger.debug("Caught exception in getPersonList", e);
            }
            Response response = Response.status(
                    Response.Status.INTERNAL_SERVER_ERROR).entity("Index failed").type("text/plain").build();
            throw new WebApplicationException(response);
        }
        return personObjectList;
    }

    @PUT
    @Path("{csid}/items/{itemcsid}")
    public MultipartOutput updatePerson(
            @PathParam("csid") String parentcsid,
            @PathParam("itemcsid") String itemcsid,
            MultipartInput theUpdate) {
        if (logger.isDebugEnabled()) {
            logger.debug("updatePerson with parentcsid=" + parentcsid + " and itemcsid=" + itemcsid);
        }
        if (parentcsid == null || "".equals(parentcsid)) {
            logger.error("updatePerson: missing csid!");
            Response response = Response.status(Response.Status.BAD_REQUEST).entity(
                    "update failed on Person parentcsid=" + parentcsid).type(
                    "text/plain").build();
            throw new WebApplicationException(response);
        }
        if (itemcsid == null || "".equals(itemcsid)) {
            logger.error("updatePerson: missing itemcsid!");
            Response response = Response.status(Response.Status.BAD_REQUEST).entity(
                    "update failed on Person=" + itemcsid).type(
                    "text/plain").build();
            throw new WebApplicationException(response);
        }
        MultipartOutput result = null;
        try {
            // Note that we have to create the service context for the Items, not the main service
            ServiceContext ctx = MultipartServiceContextFactory.get().createServiceContext(theUpdate, getItemServiceName());
            DocumentHandler handler = createItemDocumentHandler(ctx, parentcsid);
            getRepositoryClient(ctx).update(ctx, itemcsid, handler);
            result = (MultipartOutput) ctx.getOutput();
        } catch (BadRequestException bre) {
            Response response = Response.status(
                    Response.Status.BAD_REQUEST).entity("Create failed reason " + bre.getErrorReason()).type("text/plain").build();
            throw new WebApplicationException(response);
        } catch (UnauthorizedException ue) {
            Response response = Response.status(
                    Response.Status.UNAUTHORIZED).entity("Update failed reason " + ue.getErrorReason()).type("text/plain").build();
            throw new WebApplicationException(response);
        } catch (DocumentNotFoundException dnfe) {
            if (logger.isDebugEnabled()) {
                logger.debug("caught exception in updatePerson", dnfe);
            }
            Response response = Response.status(Response.Status.NOT_FOUND).entity(
                    "Update failed on Person csid=" + itemcsid).type(
                    "text/plain").build();
            throw new WebApplicationException(response);
        } catch (Exception e) {
            Response response = Response.status(
                    Response.Status.INTERNAL_SERVER_ERROR).entity("Update failed").type("text/plain").build();
            throw new WebApplicationException(response);
        }
        return result;
    }

    @DELETE
    @Path("{csid}/items/{itemcsid}")
    public Response deletePerson(
            @PathParam("csid") String parentcsid,
            @PathParam("itemcsid") String itemcsid) {
        if (logger.isDebugEnabled()) {
            logger.debug("deletePerson with parentcsid=" + parentcsid + " and itemcsid=" + itemcsid);
        }
        if (parentcsid == null || "".equals(parentcsid)) {
            logger.error("deletePerson: missing csid!");
            Response response = Response.status(Response.Status.BAD_REQUEST).entity(
                    "delete failed on Person parentcsid=" + parentcsid).type(
                    "text/plain").build();
            throw new WebApplicationException(response);
        }
        if (itemcsid == null || "".equals(itemcsid)) {
            logger.error("deletePerson: missing itemcsid!");
            Response response = Response.status(Response.Status.BAD_REQUEST).entity(
                    "delete failed on Person=" + itemcsid).type(
                    "text/plain").build();
            throw new WebApplicationException(response);
        }
        try {
            // Note that we have to create the service context for the Items, not the main service
            ServiceContext ctx = MultipartServiceContextFactory.get().createServiceContext(null, getItemServiceName());
            getRepositoryClient(ctx).delete(ctx, itemcsid);
            return Response.status(HttpResponseCodes.SC_OK).build();
        } catch (UnauthorizedException ue) {
            Response response = Response.status(
                    Response.Status.UNAUTHORIZED).entity("Delete failed reason " + ue.getErrorReason()).type("text/plain").build();
            throw new WebApplicationException(response);
        } catch (DocumentNotFoundException dnfe) {
            if (logger.isDebugEnabled()) {
                logger.debug("caught exception in deletePerson", dnfe);
            }
            Response response = Response.status(Response.Status.NOT_FOUND).entity(
                    "Delete failed on Person itemcsid=" + itemcsid).type(
                    "text/plain").build();
            throw new WebApplicationException(response);
        } catch (Exception e) {
            Response response = Response.status(
                    Response.Status.INTERNAL_SERVER_ERROR).entity("Delete failed").type("text/plain").build();
            throw new WebApplicationException(response);
        }

    }

    /*************************************************************************
     * Contact parts - this is a sub-resource of Person (or "item")
     *************************************************************************/
    @POST
    @Path("{parentcsid}/items/{itemcsid}/contacts")
    public Response createContact(
            @PathParam("parentcsid") String parentcsid,
            @PathParam("itemcsid") String itemcsid,
            MultipartInput input) {
        try {
            // Note that we have to create the service context and document
            // handler for the Contact service, not the main service.
            ServiceContext ctx = MultipartServiceContextFactory.get().createServiceContext(input, getContactServiceName());
            DocumentHandler handler = createContactDocumentHandler(ctx, parentcsid, itemcsid);
            String csid = getRepositoryClient(ctx).create(ctx, handler);
            UriBuilder path = UriBuilder.fromResource(PersonAuthorityResource.class);
            path.path("" + parentcsid + "/items/" + itemcsid + "/contacts/" + csid);
            Response response = Response.created(path.build()).build();
            return response;
        } catch (BadRequestException bre) {
            Response response = Response.status(
                    Response.Status.BAD_REQUEST).entity("Create failed reason " + bre.getErrorReason()).type("text/plain").build();
            throw new WebApplicationException(response);
        } catch (UnauthorizedException ue) {
            Response response = Response.status(
                    Response.Status.UNAUTHORIZED).entity("Create failed reason " + ue.getErrorReason()).type("text/plain").build();
            throw new WebApplicationException(response);
        } catch (Exception e) {
            if (logger.isDebugEnabled()) {
                logger.debug("Caught exception in createContact", e);
            }
            Response response = Response.status(
                Response.Status.INTERNAL_SERVER_ERROR)
                .entity("Attempt to create Contact failed.")
                .type("text/plain").build();
            throw new WebApplicationException(response);
        }
        
    }
        
    @GET
    @Produces({"application/xml"})
    @Path("{parentcsid}/items/{itemcsid}/contacts/")
    public ContactsCommonList getContactList(
            @PathParam("parentcsid") String parentcsid,
            @PathParam("itemcsid") String itemcsid,
            @Context UriInfo ui) {
        ContactsCommonList contactObjectList = new ContactsCommonList();
        try {
            ServiceContext ctx = MultipartServiceContextFactory.get().createServiceContext(null, getContactServiceName());
            DocumentHandler handler = createContactDocumentHandler(ctx, parentcsid, itemcsid);
            MultivaluedMap<String, String> queryParams = ui.getQueryParameters();
            DocumentFilter myFilter = new DocumentFilter();
            myFilter.setPagination(queryParams);
            myFilter.setWhereClause(ContactJAXBSchema.CONTACTS_COMMON + ":" +
            		ContactJAXBSchema.IN_AUTHORITY +
            		"='" + parentcsid + "'" +
                        " AND " +
                        ContactJAXBSchema.CONTACTS_COMMON + ":" +
             		ContactJAXBSchema.IN_ITEM +
            		"='" + itemcsid + "'" +
                        " AND ecm:isProxy = 0");
            handler.setDocumentFilter(myFilter);
            getRepositoryClient(ctx).getFiltered(ctx, handler);
            contactObjectList = (ContactsCommonList) handler.getCommonPartList();
        } catch (UnauthorizedException ue) {
            Response response = Response.status(
                    Response.Status.UNAUTHORIZED).entity("Index failed reason " + ue.getErrorReason()).type("text/plain").build();
            throw new WebApplicationException(response);
        } catch (Exception e) {
            if (logger.isDebugEnabled()) {
                logger.debug("Caught exception in getContactsList", e);
            }
            Response response = Response.status(
                    Response.Status.INTERNAL_SERVER_ERROR).entity("Index failed").type("text/plain").build();
            throw new WebApplicationException(response);
        }
        return contactObjectList;
    }

    @GET
    @Path("{parentcsid}/items/{itemcsid}/contacts/{csid}")
    public MultipartOutput getContact(
            @PathParam("parentcsid") String parentcsid,
            @PathParam("itemcsid") String itemcsid,
            @PathParam("csid") String csid) {
        MultipartOutput result = null;
       if (logger.isDebugEnabled()) {
            logger.debug("getContact with parentCsid=" + parentcsid +
            " itemcsid=" + itemcsid + " csid=" + csid);
        }
        try {
            // Note that we have to create the service context and document
            // handler for the Contact service, not the main service.
            ServiceContext ctx = MultipartServiceContextFactory.get().createServiceContext(null, getContactServiceName());
            DocumentHandler handler = createContactDocumentHandler(ctx, parentcsid, itemcsid);
            getRepositoryClient(ctx).get(ctx, csid, handler);
            result = (MultipartOutput) ctx.getOutput();
        } catch (UnauthorizedException ue) {
            Response response = Response.status(
                    Response.Status.UNAUTHORIZED).entity("Get failed reason " + ue.getErrorReason()).type("text/plain").build();
            throw new WebApplicationException(response);
        } catch (DocumentNotFoundException dnfe) {
            if (logger.isDebugEnabled()) {
                logger.debug("getContact", dnfe);
            }
            Response response = Response.status(Response.Status.NOT_FOUND)
                .entity("Get failed, the requested Contact CSID:" + csid + ": was not found.")
                .type("text/plain").build();
            throw new WebApplicationException(response);
        } catch (Exception e) {
            if (logger.isDebugEnabled()) {
                logger.debug("getContact", e);
            }
            Response response = Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity("Get contact failed")
                .type("text/plain").build();
            throw new WebApplicationException(response);
        }
        if (result == null) {
            Response response = Response.status(Response.Status.NOT_FOUND)
                .entity("Get failed, the requested Contact CSID:" + csid + ": was not found.")
                .type("text/plain").build();
            throw new WebApplicationException(response);
        }
        return result;

    }

    @PUT
    @Path("{parentcsid}/items/{itemcsid}/contacts/{csid}")
    MultipartOutput updateContact(
            @PathParam("parentcsid") String parentcsid,
            @PathParam("itemcsid") String itemcsid,
            @PathParam("csid") String csid,
            MultipartInput theUpdate) {
       if (logger.isDebugEnabled()) {
            logger.debug("updateContact with parentcsid=" + parentcsid +
            " itemcsid=" + itemcsid + " csid=" + csid);
        }
       if (parentcsid == null || parentcsid.trim().isEmpty()) {
            logger.error("updateContact: missing csid!");
            Response response = Response.status(Response.Status.BAD_REQUEST).entity(
                    "update failed on Contact parentcsid=" + parentcsid).type(
                    "text/plain").build();
            throw new WebApplicationException(response);
        }
        if (itemcsid == null || itemcsid.trim().isEmpty()) {
            logger.error("updateContact: missing itemcsid!");
            Response response = Response.status(Response.Status.BAD_REQUEST).entity(
                    "update failed on Contact=" + itemcsid).type(
                    "text/plain").build();
            throw new WebApplicationException(response);
        }
        if (csid == null || csid.trim().isEmpty()) {
            logger.error("updateContact: missing csid!");
            Response response = Response.status(Response.Status.BAD_REQUEST).entity(
                    "update failed on Contact=" + csid).type(
                    "text/plain").build();
            throw new WebApplicationException(response);
        }
        MultipartOutput result = null;
        try {
            // Note that we have to create the service context and document
            // handler for the Contact service, not the main service.
            ServiceContext ctx = MultipartServiceContextFactory.get()
                .createServiceContext(theUpdate, getContactServiceName());
            DocumentHandler handler = createContactDocumentHandler(ctx, parentcsid, itemcsid);
            getRepositoryClient(ctx).update(ctx, csid, handler);
            result = (MultipartOutput) ctx.getOutput();
        } catch (BadRequestException bre) {
            Response response = Response.status(
                    Response.Status.BAD_REQUEST).entity("Create failed reason " + bre.getErrorReason()).type("text/plain").build();
            throw new WebApplicationException(response);
        } catch (UnauthorizedException ue) {
            Response response = Response.status(
                    Response.Status.UNAUTHORIZED).entity("Update failed reason " + ue.getErrorReason()).type("text/plain").build();
            throw new WebApplicationException(response);
        } catch (DocumentNotFoundException dnfe) {
            if (logger.isDebugEnabled()) {
                logger.debug("caught exception in updateContact", dnfe);
            }
            Response response = Response.status(Response.Status.NOT_FOUND).entity(
                    "Update failed on Contact csid=" + itemcsid).type(
                    "text/plain").build();
            throw new WebApplicationException(response);
        } catch (Exception e) {
            Response response = Response.status(
                    Response.Status.INTERNAL_SERVER_ERROR).entity("Update failed").type("text/plain").build();
            throw new WebApplicationException(response);
        }
        return result;
    }

    @DELETE
    @Path("{parentcsid}/items/{itemcsid}/contacts/{csid}")
    Response deleteContact(
            @PathParam("parentcsid") String parentcsid,
            @PathParam("itemcsid") String itemcsid,
            @PathParam("csid") String csid) {
        if (logger.isDebugEnabled()) {
            logger.debug("deleteContact with parentCsid=" + parentcsid +
            " itemcsid=" + itemcsid + " csid=" + csid);
        }
        if (parentcsid == null || parentcsid.trim().isEmpty()) {
            logger.error("deleteContact: missing parentcsid!");
            Response response = Response.status(Response.Status.BAD_REQUEST).entity(
                    "delete contact failed on parentcsid=" + parentcsid).type(
                    "text/plain").build();
            throw new WebApplicationException(response);
        }
        if (itemcsid == null || itemcsid.trim().isEmpty()) {
            logger.error("deleteContact: missing itemcsid!");
            Response response = Response.status(Response.Status.BAD_REQUEST).entity(
                    "delete contact failed on itemcsid=" + itemcsid).type(
                    "text/plain").build();
            throw new WebApplicationException(response);
        }
        if (csid == null || csid.trim().isEmpty()) {
            logger.error("deleteContact: missing csid!");
            Response response = Response.status(Response.Status.BAD_REQUEST).entity(
                    "delete contact failed on csid=" + csid).type(
                    "text/plain").build();
            throw new WebApplicationException(response);
        }
        try {
            // Note that we have to create the service context for the
            // Contact service, not the main service.
            ServiceContext ctx =
                MultipartServiceContextFactory.get().createServiceContext(null, getContactServiceName());
            getRepositoryClient(ctx).delete(ctx, csid);
            return Response.status(HttpResponseCodes.SC_OK).build();   
         } catch (UnauthorizedException ue) {
            Response response = Response.status(
                    Response.Status.UNAUTHORIZED).entity("Delete failed reason " + ue.getErrorReason()).type("text/plain").build();
            throw new WebApplicationException(response);
         } catch (DocumentNotFoundException dnfe) {
            if (logger.isDebugEnabled()) {
                logger.debug("Caught exception in deleteContact", dnfe);
            }
            Response response = Response.status(Response.Status.NOT_FOUND)
                .entity("Delete failed, the requested Contact CSID:" + csid + ": was not found.")
                .type("text/plain").build();
            throw new WebApplicationException(response);
       } catch (Exception e) {
            Response response = Response.status(
                    Response.Status.INTERNAL_SERVER_ERROR).entity("Delete failed").type("text/plain").build();
            throw new WebApplicationException(response);
        }
    }

}
