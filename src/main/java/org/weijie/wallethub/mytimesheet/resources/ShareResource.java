package org.weijie.wallethub.mytimesheet.resources;

import java.sql.SQLException;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.weijie.wallethub.mytimesheet.model.Share;
import org.weijie.wallethub.mytimesheet.service.ShareService;

@Path("/shares")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ShareResource {

	ShareService shareService = new ShareService();
	
	@GET
	public Share getShareRelation(@QueryParam("tid") long tid, @QueryParam("uid") long uid) throws SQLException {
		return shareService.getShareRelation(tid, uid);
	}
	
	@POST
	public Response addShareRelation(Share mShare) throws SQLException {
		return shareService.addShareRelation(mShare);
	}
	
	@PUT
	public Response updateShareRelation(@QueryParam("tid") long tid, @QueryParam("uid") long uid, Share mShare) throws SQLException {
		mShare.setTid(tid);
		mShare.setUid(uid);
		return shareService.updateShareRelation(mShare);
	}
	
	@DELETE
	public Response deleteShareRelation(@QueryParam("tid") long tid, @QueryParam("uid") long uid) throws SQLException {
		return shareService.deleteShareRelation(tid, uid);
	}
}
