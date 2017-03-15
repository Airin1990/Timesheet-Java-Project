package org.weijie.wallethub.mytimesheet.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.json.Json;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.weijie.wallethub.mytimesheet.database.ConnectionManager;
import org.weijie.wallethub.mytimesheet.model.Share;

public class ShareService {

	private Connection con;
	
	public ShareService() {
		
	}
	
	public Share getShareRelation(long tid, long uid) throws SQLException {
		Share mShare = null;
		String sql = "SELECT * FROM share_relation WHERE tid = ? AND uid = ?";

		con = ConnectionManager.getConnection();

		PreparedStatement statement = con.prepareStatement(sql);
		statement.setLong(1, tid);
		statement.setLong(2, uid);

		ResultSet rs = statement.executeQuery();

		if (rs.next()) {
			long sid = rs.getLong(1);
			int shareMode = rs.getInt(4);
			int shareStatus = rs.getInt(5);

			mShare = new Share(sid, tid, uid, shareMode, shareStatus);
		}

		rs.close();
		statement.close();
		con.close();

		return mShare;
	}
	
	
	public Response addShareRelation (Share mShare) throws SQLException {
		
		long tid = mShare.getTid();
		long uid = mShare.getUid();
		String sql = "INSERT INTO share_relation (tid, uid, share_mode, share_status) VALUES (?, ?, ?, ?)";
		
		if (getShareRelation(tid, uid) == null) {
			con = ConnectionManager.getConnection();

			PreparedStatement statement = con.prepareStatement(sql);
			statement.setLong(1, tid);
			statement.setLong(2, uid);
			statement.setInt(3, mShare.getShareMode());
			statement.setInt(4, mShare.getShareStatus());
			
			boolean rowInserted = statement.executeUpdate() > 0;
			statement.close();
			con.close();
			if (rowInserted) {
				String json = Json.createObjectBuilder()
						.add("description", "insert successfully!")
						.add("status", ""+Response.Status.OK)
						.build()
						.toString();
				return Response.status(Response.Status.OK)
						.entity(json)
						.type(MediaType.APPLICATION_JSON).build();
			}
			else {
				return Response.serverError().entity("Server Error").build();
			}
		}
		else {
			return Response.status(Response.Status.BAD_REQUEST).build();
		}
		
	}
	
	public Response updateShareRelation (Share mShare) throws SQLException {
		
		String sql = "UPDATE share_relation SET share_mode = ?, share_status = ? WHERE tid = ? AND uid = ?";
		con = ConnectionManager.getConnection();
		if (mShare.getShareStatus() == 2)
			copyToTable(mShare.getTid(), mShare.getUid());

		PreparedStatement statement = con.prepareStatement(sql);
		statement.setInt(1, mShare.getShareMode());
		statement.setInt(2, mShare.getShareStatus());
		statement.setLong(3, mShare.getTid());
		statement.setLong(4, mShare.getUid());
		
		boolean rowUpdated = statement.executeUpdate() > 0;
		statement.close();
		con.close();
		
		if (rowUpdated) {
			String json = Json.createObjectBuilder()
					.add("description", "update successfully!")
					.add("status", ""+Response.Status.OK)
					.build()
					.toString();
			return Response.status(Response.Status.OK)
					.entity(json)
					.type(MediaType.APPLICATION_JSON).build();
		}
		else {
			return Response.status(Response.Status.BAD_REQUEST).build();
		}
	}
	
	public Response deleteShareRelation (long tid, long uid) throws SQLException {
		String sql = "DELETE FROM share_relation WHERE tid = ? AND uid = ?";

		con = ConnectionManager.getConnection();

		PreparedStatement statement = con.prepareStatement(sql);
		statement.setLong(1, tid);
		statement.setLong(2, uid);

		boolean rowDeleted = statement.executeUpdate() > 0;
		statement.close();
		con.close();
		if (rowDeleted) {
			String json = Json.createObjectBuilder()
					.add("description", "delete successfully!")
					.add("status", ""+Response.Status.OK)
					.build()
					.toString();
			return Response.status(Response.Status.OK)
					.entity(json)
					.type(MediaType.APPLICATION_JSON).build();
		}
		else {
			return Response.status(Response.Status.BAD_REQUEST).build();
		}     
	}
	
	//this action happened whenever a user add the share relation
	public void SendToEmail(String toEmail, String fromUser) {
		
	}
	//this action happened whenever a user is revoked from a share relation
	//the back end will make a view only copy of that user
	public void copyToTable(long tid, long uid) throws SQLException {
		String sql = "INSERT INTO revoked_record (revoked_uid, record_date, start_time, end_time, "
				+ "break_time, work_time, comments, tid, is_weekend) SELECT ?, record_date, "
				+ "start_time, end_time, break_time, work_time, comments, tid, is_weekend "
				+ "FROM time_record WHERE time_record.tid = ?";
		
		PreparedStatement statement = con.prepareStatement(sql);
		statement.setLong(1, uid);
		statement.setLong(2, tid);
		
		statement.executeUpdate();
	}
}
