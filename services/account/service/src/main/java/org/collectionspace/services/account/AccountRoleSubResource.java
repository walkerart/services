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
package org.collectionspace.services.account;

import org.collectionspace.services.authorization.AccountRole;
import org.collectionspace.services.authorization.AccountRoleRel;
import org.collectionspace.services.authorization.SubjectType;

import org.collectionspace.services.common.AbstractCollectionSpaceResourceImpl;
import org.collectionspace.services.common.context.RemoteServiceContextFactory;
import org.collectionspace.services.common.context.ServiceContext;
import org.collectionspace.services.common.context.ServiceContextFactory;
import org.collectionspace.services.common.document.DocumentHandler;
import org.collectionspace.services.common.storage.StorageClient;
import org.collectionspace.services.common.storage.jpa.JpaRelationshipStorageClient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * AccountRoleSubResource is used to manage account-role relationship
 * @author
 */
public class AccountRoleSubResource
        extends AbstractCollectionSpaceResourceImpl<AccountRole, AccountRole> {

    //this service is never exposed as standalone RESTful service...just use unique
    //service name to identify binding
    /** The service name. */
    final private String serviceName = "accounts/accountroles";
    /** The logger. */
    final Logger logger = LoggerFactory.getLogger(AccountRoleSubResource.class);
    /** The storage client. */
    final StorageClient storageClient = new JpaRelationshipStorageClient();

    /* (non-Javadoc)
     * @see org.collectionspace.services.common.AbstractCollectionSpaceResourceImpl#getVersionString()
     */
    @Override
    protected String getVersionString() {
        /** The last change revision. */
        final String lastChangeRevision = "$LastChangedRevision: 1165 $";
        return lastChangeRevision;
    }

    /* (non-Javadoc)
     * @see org.collectionspace.services.common.AbstractCollectionSpaceResourceImpl#getServiceName()
     */
    @Override
    public String getServiceName() {
        return serviceName;
    }

    /* (non-Javadoc)
     * @see org.collectionspace.services.common.CollectionSpaceResource#getCommonPartClass()
     */
    @Override
    public Class<AccountRole> getCommonPartClass() {
        return AccountRole.class;
    }

    /* (non-Javadoc)
     * @see org.collectionspace.services.common.CollectionSpaceResource#getServiceContextFactory()
     */
    @Override
    public ServiceContextFactory<AccountRole, AccountRole> getServiceContextFactory() {
        return RemoteServiceContextFactory.get();
    }

    /**
     * Creates the service context.
     * 
     * @param input the input
     * @param subject the subject
     * 
     * @return the service context< account role, account role>
     * 
     * @throws Exception the exception
     */
    private ServiceContext<AccountRole, AccountRole> createServiceContext(AccountRole input,
            SubjectType subject) throws Exception {
        ServiceContext<AccountRole, AccountRole> ctx = createServiceContext(input);
        ctx.setDocumentType(AccountRole.class.getPackage().getName()); //persistence unit
        ctx.setProperty("entity-name", AccountRoleRel.class.getName());
        //subject name is necessary to indicate if role or account is a subject
        ctx.setProperty("subject", subject);
        //set context for the relationship query
        ctx.setProperty("object-class", AccountsCommon.class);
        ctx.setProperty("object-id", "account_id");
        return ctx;
    }

    /* (non-Javadoc)
     * @see org.collectionspace.services.common.AbstractCollectionSpaceResourceImpl#getStorageClient(org.collectionspace.services.common.context.ServiceContext)
     */
    @Override
    public StorageClient getStorageClient(ServiceContext<AccountRole, AccountRole> ctx) {
        //FIXME use ctx to identify storage client
        return storageClient;
    }

    /**
     * createAccountRole creates one or more account-role relationships
     * between object (account/role) and subject (role/account)
     * @param input
     * @param subject
     * @return
     * @throws Exception
     */
    public String createAccountRole(AccountRole input, SubjectType subject)
            throws Exception {

        ServiceContext<AccountRole, AccountRole> ctx = createServiceContext(input, subject);
        DocumentHandler handler = createDocumentHandler(ctx);
        return getStorageClient(ctx).create(ctx, handler);
    }

    /**
     * getAccountRole retrieves account-role relationships using given
     * csid of object (account/role) and subject (role/account)
     * @param csid
     * @param subject
     * @return
     * @throws Exception
     */
    public AccountRole getAccountRole(
            String csid, SubjectType subject) throws Exception {

        if (logger.isDebugEnabled()) {
            logger.debug("getAccountRole with csid=" + csid);
        }
        AccountRole result = null;
        ServiceContext<AccountRole, AccountRole> ctx = createServiceContext((AccountRole) null, subject);
        DocumentHandler handler = createDocumentHandler(ctx);
        getStorageClient(ctx).get(ctx, csid, handler);
        result = (AccountRole) ctx.getOutput();

        return result;
    }

    /**
     * deleteAccountRole deletes account-role relationships using given
     * csid of object (account/role) and subject (role/account)
     * @param csid
     * @param subject
     * @return
     * @throws Exception
     */
    public void deleteAccountRole(String csid,
            SubjectType subject) throws Exception {

        if (logger.isDebugEnabled()) {
            logger.debug("deleteAccountRole with csid=" + csid);
        }
        ServiceContext<AccountRole, AccountRole> ctx = createServiceContext((AccountRole) null, subject);
        getStorageClient(ctx).delete(ctx, csid);
    }
}
