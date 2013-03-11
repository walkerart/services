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
package org.collectionspace.services.common.workflow.service.nuxeo;

import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.core.MediaType;

import org.collectionspace.services.client.workflow.WorkflowClient;
import org.collectionspace.services.common.context.MultipartServiceContext;
import org.collectionspace.services.common.context.ServiceContext;
import org.collectionspace.services.common.document.DocumentWrapper;
import org.collectionspace.services.common.workflow.jaxb.WorkflowJAXBSchema;
import org.collectionspace.services.config.service.ObjectPartType;
import org.collectionspace.services.lifecycle.TransitionDef;
import org.collectionspace.services.nuxeo.client.java.DocHandlerBase;
import org.collectionspace.services.nuxeo.client.java.DocumentModelHandler;
import org.collectionspace.services.workflow.WorkflowCommon;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WorkflowDocumentModelHandler
        extends DocHandlerBase<WorkflowCommon> {

    /** The logger. */
    private static final Logger logger = LoggerFactory.getLogger(WorkflowDocumentModelHandler.class);
    /*
     * Workflow transitions
     *
     * See the "Nuxeo core default life cycle definition", an XML configuration
     * for Nuxeo's "lifecycle" extension point that specifies valid workflow
     * states and the operations that transition documents to those states, via:
     *
     * org.nuxeo.ecm.core.LifecycleCoreExtensions--lifecycle (as opposed to --types)
     */
    private static final String TRANSITION_UNKNOWN = "unknown";

    
    @Override
    public void handleUpdate(DocumentWrapper<DocumentModel> wrapDoc) throws Exception {
    	//
    	// First, call the parent document handler to give it a chance to handle the workflow transition -otherwise, call
    	// the super/parent handleUpdate() method.
    	//
    	ServiceContext ctx = this.getServiceContext();
    	DocumentModelHandler docHandler = (DocumentModelHandler)ctx.getProperty(WorkflowClient.PARENT_DOCHANDLER);
    	TransitionDef transitionDef =  (TransitionDef)ctx.getProperty(WorkflowClient.TRANSITION_ID);
    	docHandler.handleWorkflowTransition(wrapDoc, transitionDef);
    	//
    	// If no exception occurred, then call the super's method
    	//
    	super.handleUpdate(wrapDoc);
    }
    
    @Override
    protected void handleRefNameChanges(ServiceContext ctx, DocumentModel docModel) throws ClientException {
    	//
    	// We are intentionally overriding this method to do nothing since the Workflow resource is a meta-resource without a refname
    	//
    }    
    
    /*
     * Handle read (GET)
     */
    @Override
    protected Map<String, Object> extractPart(DocumentModel docModel,
            String schema,
            ObjectPartType partMeta,
            Map<String, Object> addToMap)
            throws Exception {
        Map<String, Object> result = null;

        MediaType mt = MediaType.valueOf(partMeta.getContent().getContentType()); //FIXME: REM - This is no longer needed.  Everything is POX
        if (mt.equals(MediaType.APPLICATION_XML_TYPE)) {
            Map<String, Object> unQObjectProperties =
                    (addToMap != null) ? addToMap : (new HashMap<String, Object>());
            unQObjectProperties.put(WorkflowJAXBSchema.WORKFLOW_LIFECYCLEPOLICY, docModel.getLifeCyclePolicy());
            unQObjectProperties.put(WorkflowJAXBSchema.WORKFLOW_CURRENTLIFECYCLESTATE, docModel.getCurrentLifeCycleState());
            result = unQObjectProperties;
        } //TODO: handle other media types

        return result;
    }

    @Override
    public void extractAllParts(DocumentWrapper<DocumentModel> wrapDoc)
            throws Exception {
        DocumentModel docModel = wrapDoc.getWrappedObject();
        String[] schemas = {WorkflowClient.SERVICE_COMMONPART_NAME};
        Map<String, ObjectPartType> partsMetaMap = getServiceContext().getPartsMetadata();
        for (String schema : schemas) {
            ObjectPartType partMeta = partsMetaMap.get(schema);
            if (partMeta == null) {
                continue; // unknown part, ignore
            }
            Map<String, Object> unQObjectProperties = extractPart(docModel, schema, partMeta);
            addOutputPart(unQObjectProperties, schema, partMeta);
        }
    }

    /**
     * Get the identifier for the transition that sets a document to
     * the supplied, destination workflow state.
     *
     * @param state a destination workflow state.
     * @return an identifier for the transition required to
     * place the document in that workflow state.
     */
    @Deprecated 
    private String getTransitionFromState(String state) {
        String result = TRANSITION_UNKNOWN;

        // FIXME We may wish to add calls, such as those in
        // org.nuxeo.ecm.core.lifecycle.impl.LifeCycleImpl, to validate incoming
        // destination workflow state and the set of allowable state transitions.

        if (state.equalsIgnoreCase(WorkflowClient.WORKFLOWSTATE_DELETED)) {
            result = WorkflowClient.WORKFLOWTRANSITION_DELETE;
        } else if (state.equalsIgnoreCase(WorkflowClient.WORKFLOWSTATE_ACTIVE)) {
            result = WorkflowClient.WORKFLOWTRANSITION_UNDELETE; //FIXME, could also be transition WORKFLOWTRANSITION_UNLOCK
        } else if (state.equalsIgnoreCase(WorkflowClient.WORKFLOWSTATE_LOCKED)) {
            result = WorkflowClient.WORKFLOWTRANSITION_LOCK;
        } else {
        	logger.warn("An attempt was made to transition a document to an unknown workflow state = "
        			+ state);
        }        
        
        return result;
    }
    
    /*
     * Handle Update (PUT)
     */

    @Override
    public void fillAllParts(DocumentWrapper<DocumentModel> wrapDoc, Action action) throws Exception {
    	String transitionToFollow = null;
    	
        DocumentModel docModel = wrapDoc.getWrappedObject();
        MultipartServiceContext ctx = (MultipartServiceContext) getServiceContext();
    	
    	try {
    		TransitionDef transitionDef = (TransitionDef)this.getServiceContext().getProperty(WorkflowClient.TRANSITION_ID);
    		transitionToFollow = transitionDef.getName();
	        docModel.followTransition(transitionToFollow);
    	} catch (Exception e) {
    		String msg = "Unable to follow workflow transition to state = "
    				+ transitionToFollow;
    		logger.error(msg, e);
    		ClientException ce = new ClientException("Unable to follow workflow transition: " + transitionToFollow);
    		throw ce;
    	}
    }    
}

