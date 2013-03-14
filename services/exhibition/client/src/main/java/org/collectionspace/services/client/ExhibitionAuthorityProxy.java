package org.collectionspace.services.client;

import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

/**
 * @version $Revision:$
 * ILT = Item list type
 * LT = List type
 */
@Path(ExhibitionAuthorityClient.SERVICE_PATH + "/")
@Produces("application/xml")
@Consumes("application/xml")
public interface ExhibitionAuthorityProxy extends AuthorityProxy {
}
