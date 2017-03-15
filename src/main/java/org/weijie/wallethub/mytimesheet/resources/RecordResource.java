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

import org.weijie.wallethub.mytimesheet.model.Record;
import org.weijie.wallethub.mytimesheet.service.RecordService;

@Path("/records")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class RecordResource {

	RecordService recordService = new RecordService();
	
	@GET
	public List<Record> getAllRecords(@QueryParam("tid") long tid) throws SQLException {
		if (tid > 0) {
			return recordService.getTimesheetRecords(tid);
		}
		return recordService.getAllRecords();
	}
	
	@GET
	@Path("/revoked")
	public List<Record> getRevokedRecords(@QueryParam("tid") long tid, @QueryParam("uid") long uid) throws SQLException {
		if (tid > 0 && uid > 0) {
			return recordService.getRevokedRecords(tid, uid);
		}
		return null;
	}
	
	@POST
	public Response addRecord(Record mRecord) throws SQLException{
		return recordService.insertRecord(mRecord);	
	}
	
	@PUT
	@Path("/{rid}")
	public Response updateRecord(@PathParam("rid") long rid, Record mRecord) throws SQLException {
		mRecord.setRid(rid);
		return recordService.updateRecord(mRecord);
	}
	
	@DELETE
	@Path("/{rid}")
	public Response deleteRecord(@PathParam("rid") long rid) throws SQLException {
		return recordService.deleteRecord(rid);
	}
	
	@POST
	@Path("/dummy")
	public Response addDummyRecord(Record mRecord) throws SQLException{
		return recordService.insertDummyRecord(mRecord);	
	}
}
