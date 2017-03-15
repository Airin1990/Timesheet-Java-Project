package org.weijie.wallethub.mytimesheet.resources;

import java.sql.SQLException;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.weijie.wallethub.mytimesheet.model.Timesheet;
import org.weijie.wallethub.mytimesheet.model.UserResponse;
import org.weijie.wallethub.mytimesheet.service.SheetService;

@Path("/sheets")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class SheetResource {

	SheetService sheetService = new SheetService();
	
	@GET
	public List<Timesheet> getTimesheets(@QueryParam("uid") long uid) throws SQLException {
		if (uid > 0) {
			return sheetService.getUserTimesheets(uid);
		}
		return sheetService.getAllTimesheets();
	}
	
	@GET
	@Path("/share")
	public List<Timesheet> getSharedTimesheet(@QueryParam("uid") long uid) throws SQLException {
		if (uid > 0) {
			return sheetService.getShareTimesheets(uid);
		}
		return sheetService.getAllShareTimesheets();
	}
	
	@GET
	@Path("/ulist")
	public List<UserResponse> getShareUserList(@QueryParam("tid") long tid) throws SQLException {
		if (tid > 0) {
			return sheetService.getShareUserList(tid);
		}
		return null;
	}
	
	@GET
	@Path("/all")
	public List<Timesheet> getAllUserTimesheets(@QueryParam("uid") long uid) throws SQLException {
		if (uid > 0) {
			return sheetService.getUserAllTimesheets(uid);
		}
		return null;
	}
	
	@GET
	@Path("/{tid}")
	public Timesheet getTimesheet(@PathParam("tid") long tid) throws SQLException {
		if (tid > 0) {
			return sheetService.getTimesheet(tid);
		}
		return null;
	}
	
	@POST
	public Response addTimesheet(Timesheet mTimesheet) throws SQLException {
		return sheetService.addTimesheet(mTimesheet);
	}
	
	@PUT
	@Path("/{tid}")
	public Response updateTimesheet(@PathParam("tid") long tid, Timesheet mTimesheet) throws SQLException {
		mTimesheet.setTid(tid);
		return sheetService.updateTimesheet(mTimesheet);
	}
	
	@DELETE
	@Path("/{tid}")
	public Response deleteTimesheet(@PathParam("tid") long tid) {
		return sheetService.deleteTimesheet(tid);
	}
}
