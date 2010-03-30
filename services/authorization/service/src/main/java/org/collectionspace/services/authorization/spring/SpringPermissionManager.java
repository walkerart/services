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

package org.collectionspace.services.authorization.spring;

import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.collectionspace.services.authorization.CSpaceAction;
import org.collectionspace.services.authorization.spi.CSpacePermissionManager;
import org.collectionspace.services.authorization.CSpaceResource;
import org.springframework.security.acls.model.AccessControlEntry;
import org.springframework.security.acls.model.MutableAcl;
import org.springframework.security.acls.model.MutableAclService;
import org.springframework.security.acls.model.NotFoundException;
import org.springframework.security.acls.model.ObjectIdentity;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.acls.model.Sid;

/**
 * Manages permissions in Spring Security
 * @author 
 */
public class SpringPermissionManager implements CSpacePermissionManager {

    final Log log = LogFactory.getLog(SpringPermissionEvaluator.class);
    private SpringAuthorizationProvider provider;

    SpringPermissionManager(SpringAuthorizationProvider provider) {
        this.provider = provider;
    }

    @Override
    public void addPermission(CSpaceResource res, String[] principals, CSpaceAction perm) {
        ObjectIdentity oid = SpringAuthorizationProvider.mapResource(res);
        Sid[] sids = SpringAuthorizationProvider.mapPrincipal(principals);
        Permission p = SpringAuthorizationProvider.mapPermssion(perm);
        for (Sid sid : sids) {
            addPermission(oid, sid, p);
        }
    }

    private void addPermission(ObjectIdentity oid, Sid recipient, Permission permission) {
        MutableAcl acl;
        MutableAclService mutableAclService = provider.getProviderAclService();
        try {
            acl = (MutableAcl) mutableAclService.readAclById(oid);
        } catch (NotFoundException nfe) {
            acl = mutableAclService.createAcl(oid);
        }

        acl.insertAce(acl.getEntries().size(), permission, recipient, true);
        mutableAclService.updateAcl(acl);

    }

    @Override
    public void deletePermission(CSpaceResource res, String[] principals, CSpaceAction perm) {
        ObjectIdentity oid = SpringAuthorizationProvider.mapResource(res);
        Sid[] sids = SpringAuthorizationProvider.mapPrincipal(principals);
        Permission p = SpringAuthorizationProvider.mapPermssion(perm);
        for (Sid sid : sids) {
            deletePermission(oid, sid, p);
        }
    }

    private void deletePermission(ObjectIdentity oid, Sid recipient, Permission permission) {

        MutableAclService mutableAclService = provider.getProviderAclService();
        MutableAcl acl = (MutableAcl) mutableAclService.readAclById(oid);

        // Remove all permissions associated with this particular recipient (string equality to KISS)
        List<AccessControlEntry> entries = acl.getEntries();

        for (int i = 0; i < entries.size(); i++) {
            if (entries.get(i).getSid().equals(recipient)
                    && entries.get(i).getPermission().equals(permission)) {
                acl.deleteAce(i);
            }
        }
        mutableAclService.updateAcl(acl);
    }
}