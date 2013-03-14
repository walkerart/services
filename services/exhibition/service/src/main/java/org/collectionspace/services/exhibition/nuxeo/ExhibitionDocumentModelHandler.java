/**
 *  This document is a part of the source code and related artifacts
 *  for CollectionSpace, an open source collections management system
 *  for museums and related institutions:
 *
 *  http://www.collectionspace.org
 *  http://wiki.collectionspace.org
 *
 *  Copyright 2009 University of California at Berkeley
 *
 *  Licensed under the Educational Community License (ECL), Version 2.0.
 *  You may not use this file except in compliance with this License.
 *
 *  You may obtain a copy of the ECL 2.0 License at
 *
 *  https://source.collectionspace.org/collection-space/LICENSE.txt
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.collectionspace.services.exhibition.nuxeo;

import org.collectionspace.services.ExhibitionJAXBSchema;
import org.collectionspace.services.client.ExhibitionAuthorityClient;
import org.collectionspace.services.common.document.DocumentWrapper;
import org.collectionspace.services.common.vocabulary.nuxeo.AuthorityItemDocumentModelHandler;
import org.collectionspace.services.exhibition.ExhibitionsCommon;
import org.nuxeo.ecm.core.api.DocumentModel;

/**
 * ExhibitionDocumentModelHandler
 *
 */
public class ExhibitionDocumentModelHandler
        extends AuthorityItemDocumentModelHandler<ExhibitionsCommon> {

    /**
     * Common part schema label
     */
    private static final String COMMON_PART_LABEL = "exhibitions_common";
    
    public ExhibitionDocumentModelHandler() {
        super(COMMON_PART_LABEL);
    }

    @Override
    public String getAuthorityServicePath(){
        return ExhibitionAuthorityClient.SERVICE_PATH_COMPONENT;    //  CSPACE-3932
    }

        /**
     * Handle display name.
     *
     * @param docModel the doc model
     * @throws Exception the exception
     */
//    @Override
//    protected void handleComputedDisplayNames(DocumentModel docModel) throws Exception {
//        String commonPartLabel = getServiceContext().getCommonPartLabel("exhibitions");
//      Boolean displayNameComputed = (Boolean) docModel.getProperty(commonPartLabel,
//              ExhibitionJAXBSchema.DISPLAY_NAME_COMPUTED);
//      Boolean shortDisplayNameComputed = (Boolean) docModel.getProperty(commonPartLabel,
//              ExhibitionJAXBSchema.SHORT_DISPLAY_NAME_COMPUTED);
//      if(displayNameComputed==null)
//          displayNameComputed = true;
//      if(shortDisplayNameComputed==null)
//          shortDisplayNameComputed = true;
//      if (displayNameComputed || shortDisplayNameComputed) {
//                // Obtain the primary exhibition name from the list of exhibition names, for computing the display name.
//          String xpathToExhibitionName = ExhibitionJAXBSchema.WORK_TERM_NAME_GROUP_LIST 
//                        + "/[0]/" + ExhibitioneJAXBSchema.WORK_TERM_NAME;
//          String exhibitionName = getXPathStringValue(docModel, COMMON_PART_LABEL, xpathToExhibitionName);
//          String displayName = prepareDefaultDisplayName(exhibitionName);
//          if (displayNameComputed) {
//              docModel.setProperty(commonPartLabel, ExhibitionJAXBSchema.DISPLAY_NAME,
//                      displayName);
//          }
//          if (shortDisplayNameComputed) {
//              docModel.setProperty(commonPartLabel, ExhibitionJAXBSchema.SHORT_DISPLAY_NAME,
//                      displayName);
//          }
//      }
//    }
    
    /**
     * Produces a default displayName from one or more supplied fields.
     * @see ExhibitionAuthorityClientUtils.prepareDefaultDisplayName() which
     * duplicates this logic, until we define a service-general utils package
     * that is neither client nor service specific.
     * @param exhibitionName
     * @return the default display name
     * @throws Exception
     */
    private static String prepareDefaultDisplayName(
            String exhibitionName ) throws Exception {
            StringBuilder newStr = new StringBuilder();
            newStr.append(exhibitionName);
            return newStr.toString();
    }
    
    /**
     * getQProperty converts the given property to qualified schema property
     * @param prop
     * @return
     */
    @Override
    public String getQProperty(String prop) {
        return ExhibitionConstants.NUXEO_SCHEMA_NAME + ":" + prop;
    }
}