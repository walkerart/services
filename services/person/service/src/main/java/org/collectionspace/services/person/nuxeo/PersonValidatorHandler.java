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
 *//**
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
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.collectionspace.services.person.nuxeo;

import java.util.regex.Pattern;

import org.collectionspace.services.common.Tools;
import org.collectionspace.services.person.PersonsCommon;
import org.collectionspace.services.common.context.MultipartServiceContext;
import org.collectionspace.services.common.context.ServiceContext;
import org.collectionspace.services.common.document.DocumentHandler.Action;
import org.collectionspace.services.common.document.InvalidDocumentException;
import org.collectionspace.services.common.document.ValidatorHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author 
 */
public class PersonValidatorHandler implements ValidatorHandler {

    final Logger logger = LoggerFactory.getLogger(PersonValidatorHandler.class);
    private static final Pattern shortIdBadPattern = Pattern.compile("[\\W]"); //.matcher(input).matches()

    @Override
    public void validate(Action action, ServiceContext ctx)
            throws InvalidDocumentException {
        if(logger.isDebugEnabled()) {
            logger.debug("validate() action=" + action.name());
        }
        try {
            MultipartServiceContext mctx = (MultipartServiceContext) ctx;
            PersonsCommon person = (PersonsCommon) mctx.getInputPart(mctx.getCommonPartLabel(),
                    PersonsCommon.class);
            String msg = "";
            String displayName = person.getDisplayName();
            boolean invalid = false;
            if(!person.isDisplayNameComputed() && (displayName==null)) {
                invalid = true;
                msg += "displayName must be non-null if displayNameComputed is false!";
            }
            String shortId = person.getShortIdentifier();

            if(shortId==null){
                if (Tools.notEmpty(displayName)){  //TODO: && action == CREATE   ......
                    //CSPACE-3178  OK to have null shortIdentifier if displayName set, because we will compute the shortIdentifier before storing.
                    invalid = false;
                } else {
                    invalid = true;
                    msg += "shortIdentifier must be non-null";
                }
            } else if(shortIdBadPattern.matcher(shortId).find()) {
                invalid = true;
                msg += "shortIdentifier must only contain standard word characters";
            }
            /*
            if(action.equals(Action.CREATE)) {
                //create specific validation here
            } else if(action.equals(Action.UPDATE)) {
                //update specific validation here
            }
            */

            if (invalid) {
                logger.error(msg);
                throw new InvalidDocumentException(msg);
            }
        } catch (InvalidDocumentException ide) {
            throw ide;
        } catch (Exception e) {
            throw new InvalidDocumentException(e);
        }
    }
}
