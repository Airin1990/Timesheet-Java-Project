package org.weijie.wallethub.mytimesheet.resources;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.weijie.wallethub.mytimesheet.service.SummaryService;

@Path("/summary")
public class SummaryResource {

	SummaryService summaryService = new SummaryService();
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getSummary(@QueryParam("stids") String stids,@QueryParam("rtids")String rtids, @QueryParam("uid") long uid, @QueryParam("start") String start, @QueryParam("end") String end) throws SQLException, ParseException {
	
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		Date start_d = df.parse(start);
		java.sql.Date start_on = new java.sql.Date(start_d.getTime());
		Date end_d = df.parse(end);
		java.sql.Date end_on = new java.sql.Date(end_d.getTime());
		
		if (rtids == null) {
			System.out.println("rtid is null");
			rtids = "NULL";
		}
		
		if (stids == null) {
			System.out.println("stid is null");
			stids = "NULL";
		}
		
		return summaryService.getSummaryReport(stids, rtids, uid, start_on, end_on);
	}
}
