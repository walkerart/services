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
package org.collectionspace.services.objectexit.nuxeo;

import org.collectionspace.services.nuxeo.client.java.DocHandlerBase;
import org.collectionspace.services.objectexit.ObjectexitCommon;
import org.collectionspace.services.jaxb.AbstractCommonList;

public class ObjectExitDocumentModelHandler extends DocHandlerBase<ObjectexitCommon, AbstractCommonList> {

    public static DocHandlerBase.CommonListReflection clr;
    static {
        clr = new DocHandlerBase.CommonListReflection();
        clr.NuxeoSchemaName= "objectexit";
        clr.SummaryFields = "exitNumber|currentOwner|uri|csid";
        clr.AbstractCommonListClassname = "org.collectionspace.services.objectexit.ObjectexitCommonList";
        clr.CommonListItemClassname = "org.collectionspace.services.objectexit.ObjectexitCommonList$ObjectexitListItem";
        clr.ListItemMethodName = "getObjectexitListItem";
        clr.ListItemsArray =   new String[][] {{"setExitNumber", "exitNumber", "", ""},
                                               {"setCurrentOwner", "currentOwner", "", ""}};
    }
    public DocHandlerBase.CommonListReflection getCommonListReflection(){
        return clr;
    }
}

