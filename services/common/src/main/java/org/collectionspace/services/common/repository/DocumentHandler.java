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
 */
package org.collectionspace.services.common.repository;

import java.util.Map;
import org.collectionspace.services.common.context.ServiceContext;

/**
 *
 * DocumentHandler provides document processing methods. It is an interface
 * between repository client and CollectionSpace service resource. It provides
 * methods to setup request via repository client and handle its response.
 *
 * Typical call sequence is:
 * Create handler and repository client
 * Call XXX operation on the repository client and pass the handler
 * repository client calls prepare on the handler
 * The repository client then calls handle on the handler
 *
 */
public interface DocumentHandler<T, TL> {

    public enum Action {

        CREATE, GET, GET_ALL, UPDATE, DELETE
    }

    /**
     * getServiceContext returns service context
     * @return
     */
    public ServiceContext getServiceContext();

    /**
     * getServiceContextPath such as "/collectionobjects/"
     * @return
     */
    public String getServiceContextPath();

    /**
     * setServiceContext sets service contex to the handler
     * @param ctx
     */
    public void setServiceContext(ServiceContext ctx);

    /**
     * prepare is called by the client for preparation of stuff before
     * invoking repository operation. this is mainly useful for create and 
     * update kind of actions
     * @param action
     * @throws Exception
     */
    public void prepare(Action action) throws Exception;

    /**
     * handle is called by the client to hand over the document processing task
     * @param action 
     * @param doc wrapped doc
     * @throws Exception
     */
    public void handle(Action action, DocumentWrapper docWrap) throws Exception;

    /**
     * handleCreate processes documents before creating document in repository
     * @param wrapDoc
     * @throws Exception
     */
    public void handleCreate(DocumentWrapper wrapDoc) throws Exception;

    /**
     * handleUpdate processes documents for the update of document in repository
     * @param wrapDoc
     * @throws Exception
     */
    public void handleUpdate(DocumentWrapper wrapDoc) throws Exception;

    /**
     * handleGet processes documents from repository before responding to consumer
     * @param wrapDoc
     * @throws Exception
     */
    public void handleGet(DocumentWrapper wrapDoc) throws Exception;

    /**
     * handleGetAll processes documents from repository before responding to consumer
     * @param wrapDoc
     * @throws Exception
     */
    public void handleGetAll(DocumentWrapper wrapDoc) throws Exception;

    /**
     * complete is called by the client to provide an opportunity to the handler
     * to take care of stuff before closing session with the repository. example
     * could be to reclaim resources or to populate response to the consumer
     * @param wrapDoc
     * @throws Exception
     */
    public void complete(Action action, DocumentWrapper wrapDoc) throws Exception;

    /**
     * completeUpdate is called by the client to indicate completion of the update call.
     * this gives opportunity to prepare updated object that should be sent back to the consumer
     * @param wrapDoc
     * @throws Exception
     */
    public void completeUpdate(DocumentWrapper wrapDoc) throws Exception;

    /**
     * extractAllParts extracts all parts of a CS object from given document.
     * this is usually called AFTER the get operation is invoked on the repository
     * Called in handle GET/GET_ALL actions.
     * @param docWrap document
     * @throws Exception
     */
    public void extractAllParts(DocumentWrapper docWrap) throws Exception;

    /**
     * fillAllParts sets parts of CS object into given document
     * this is usually called BEFORE create/update operations are invoked on the
     * repository. Called in handle CREATE/UPDATE actions.
     * @param obj input object
     * @param docWrap target document
     * @throws Exception
     */
    public void fillAllParts(DocumentWrapper docWrap) throws Exception;

    /**
     * extractCommonPart extracts common part of a CS object from given document.
     * this is usually called AFTER the get operation is invoked on the repository.
     * Called in handle GET/GET_ALL actions.
     * @param docWrap document
     * @return common part of CS object
     * @throws Exception
     */
    public T extractCommonPart(DocumentWrapper docWrap) throws Exception;

    /**
     * fillCommonPart sets common part of CS object into given document
     * this is usually called BEFORE create/update operations are invoked on the
     * repository. Called in handle CREATE/UPDATE actions.
     * @param obj input object
     * @param docWrap target document
     * @throws Exception
     */
    public void fillCommonPart(T obj, DocumentWrapper docWrap) throws Exception;

    /**
     * extractCommonPart extracts common part of a CS object from given document.
     * this is usually called AFTER bulk get (index/list) operation is invoked on
     * the repository
     * @param docWrap document
     * @return common part of CS object
     * @throws Exception
     */
    public TL extractCommonPartList(DocumentWrapper docWrap) throws Exception;

    /**
     * fillCommonPartList sets list common part of CS object into given document
     * this is usually called BEFORE bulk create/update on the repository
     * (not yet supported)
     * @param obj input object
     * @param docWrap target document
     * @throws Exception
     */
    public void fillCommonPartList(TL obj, DocumentWrapper docWrap) throws Exception;

    /**
     * Gets the document type.
     * 
     * @return the document type
     */
    public String getDocumentType();

    /**
     * getProperties
     * @return
     */
    public Map<String, Object> getProperties();

    /**
     * setProperties provides means to the CollectionSpace service resource to
     * set up parameters before invoking any request via the client.
     * @param properties
     */
    public void setProperties(Map<String, Object> properties);

    /**
     * getCommonPart provides the common part of a CS object.
     * @return common part of CS object
     */
    public T getCommonPart();

    /**
     * setCommonPart sets common part of CS object as input for operation on
     * repository
     * @param obj input object
     */
    public void setCommonPart(T obj);

    /**
     * getCommonPartList provides the default list object of a CS object.
     * @return default list of CS object
     */
    public TL getCommonPartList();

    /**
     * setCommonPartList sets common part list entry for CS object as input for operation on
     * repository
     * @param default list of CS object
     */
    public void setCommonPartList(TL obj);

    /**
     * getQProperty get qualified property (useful for mapping to repository document property)
     * @param prop
     * @return
     */
    public String getQProperty(String prop);

    /**
     * getUnQProperty unqualifies document property from repository
     * @param qProp qualifeid property
     * @return unqualified property
     */
    public String getUnQProperty(String qProp);
}
