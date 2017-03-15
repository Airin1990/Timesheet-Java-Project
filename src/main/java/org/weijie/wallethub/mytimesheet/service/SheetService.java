package org.weijie.wallethub.mytimesheet.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.json.Json;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.weijie.wallethub.mytimesheet.database.ConnectionManager;
import org.weijie.wallethub.mytimesheet.model.Timesheet;
import org.weijie.wallethub.mytimesheet.model.UserResponse;

public class SheetService {

	private Connection con;

	public SheetService() {
		
	}
	
	public Timesheet getTimesheet(long id) throws SQLException {
		Timesheet sheet = null;
		String sql = "SELECT * FROM timesheet_table WHERE tid = ?";
		
		con = ConnectionManager.getConnection();

		PreparedStatement statement = con.prepareStatement(sql);
		statement.setLong(1, id);

		ResultSet rs = statement.executeQuery();

		if (rs.next()) {
			long tid = rs.getLong(1);
			String sheetName = rs.getString(2);
			long uid = rs.getLong(3);
			Timestamp created = rs.getTimestamp(4);
			Timestamp updated = rs.getTimestamp(5);

			sheet = new Timesheet(tid, sheetName, uid, created, updated);
		}

		rs.close();
		statement.close();
		con.close();
		
		return sheet;
	}
	
	public Response addTimesheet (Timesheet ts) throws SQLException {
		String sql = "INSERT INTO timesheet_table (sheet_name, uid) "
				+ "VALUES (?, ?)";

		con = ConnectionManager.getConnection();

		PreparedStatement statement = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
		statement.setString(1, ts.gettName());
		statement.setLong(2, ts.getUid());

		boolean rowInserted = statement.executeUpdate() > 0;
		ResultSet rs = statement.getGeneratedKeys();
		if (rs.next()){
		    ts.setTid(rs.getInt(1));
		}
		statement.close();
		con.close();
		if (rowInserted) {
			return Response.status(Response.Status.OK)
					.entity(ts)
					.type(MediaType.APPLICATION_JSON).build();
		}
		else {
			return Response.serverError().entity("Server Error").build();
		}
	}
	
	public Response updateTimesheet (Timesheet ts) throws SQLException {
		String sql = "UPDATE timesheet_table SET sheet_name = ?, uid = ?, "
				+ "update_time = default WHERE tid = ?";

		con = ConnectionManager.getConnection();

		PreparedStatement statement = con.prepareStatement(sql);
		statement.setString(1, ts.gettName());
		statement.setLong(2, ts.getUid());
		statement.setLong(3, ts.getTid());

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
			return Response.serverError().entity("Server Error").build();
		}
	}
	
	public Response deleteTimesheet (long tid) {
		//Be careful!
		//delete a timesheet will delete all the records as well as share relations.
		return null;
	}
	
	public List<Timesheet> getUserTimesheets(long id) throws SQLException {
		List<Timesheet> sheets = new ArrayList<>();
		String sql = "SELECT * FROM timesheet_table WHERE uid = ?";
		
		con = ConnectionManager.getConnection();

		PreparedStatement statement = con.prepareStatement(sql);
		statement.setLong(1, id);
		ResultSet rs = statement.executeQuery();

		while (rs.next()) {
			long tid = rs.getLong(1);
			String sheetName = rs.getString(2);
			long uid = rs.getLong(3);
			Timestamp created = rs.getTimestamp(4);
			Timestamp updated = rs.getTimestamp(5);

			sheets.add(new Timesheet(tid, sheetName, uid, created, updated));
		}

		rs.close();
		statement.close();
		con.close();
		
		return sheets;
	}
	
	public List<Timesheet> getShareTimesheets(long id) throws SQLException {
		List<Timesheet> sheets = new ArrayList<>();
		String sql = "SELECT t.*, s.share_mode, s.share_status, u.first_name, u.last_name FROM timesheet_table t "
				+ "JOIN share_relation s ON t.tid = s.tid "
				+ "JOIN user_table u ON t.uid = u.uid WHERE s.uid = ?";
		
		con = ConnectionManager.getConnection();

		PreparedStatement statement = con.prepareStatement(sql);
		statement.setLong(1, id);
		ResultSet rs = statement.executeQuery();

		while (rs.next()) {
			long tid = rs.getLong(1);
			String sheetName = rs.getString(2);
			long uid = rs.getLong(3);
			Timestamp created = rs.getTimestamp(4);
			Timestamp updated = rs.getTimestamp(5);
			int shareMode = rs.getInt(6);
			int shareStatus = rs.getInt(7);
			String firstName = rs.getString(8);
			String lastName = rs.getString(9);

			sheets.add(new Timesheet(tid, sheetName, uid, created, updated, firstName, lastName, shareMode, shareStatus));
		}

		rs.close();
		statement.close();
		con.close();
		
		return sheets;
	}

	public List<Timesheet> getAllTimesheets() throws SQLException {
		List<Timesheet> sheets = new ArrayList<>();
		String sql = "SELECT * FROM timesheet_table";
		
		con = ConnectionManager.getConnection();

		PreparedStatement statement = con.prepareStatement(sql);

		ResultSet rs = statement.executeQuery();

		while (rs.next()) {
			long tid = rs.getLong(1);
			String sheetName = rs.getString(2);
			long uid = rs.getLong(3);
			Timestamp created = rs.getTimestamp(4);
			Timestamp updated = rs.getTimestamp(5);

			sheets.add(new Timesheet(tid, sheetName, uid, created, updated));
		}

		rs.close();
		statement.close();
		con.close();
		
		return sheets;
	}

	public List<Timesheet> getAllShareTimesheets() throws SQLException {
		List<Timesheet> sheets = new ArrayList<>();
		String sql = "SELECT t.*,s.share_mode, s.share_status, u.first_name, u.last_name FROM timesheet_table t "
				+ "JOIN share_relation s ON t.tid = s.tid "
				+ "JOIN user_table u ON t.uid = u.uid";
		
		con = ConnectionManager.getConnection();

		PreparedStatement statement = con.prepareStatement(sql);
		ResultSet rs = statement.executeQuery();

		while (rs.next()) {
			long tid = rs.getLong(1);
			String sheetName = rs.getString(2);
			long uid = rs.getLong(3);
			Timestamp created = rs.getTimestamp(4);
			Timestamp updated = rs.getTimestamp(5);
			int shareMode = rs.getInt(6);
			int shareStatus = rs.getInt(7);
			String firstName = rs.getString(8);
			String lastName = rs.getString(9);

			sheets.add(new Timesheet(tid, sheetName, uid, created, updated, firstName, lastName, shareMode, shareStatus));
		}

		rs.close();
		statement.close();
		con.close();
		
		return sheets;
	}
	
	public List<Timesheet> getUserAllTimesheets(long id) throws SQLException {
		List<Timesheet> result = new ArrayList<>();
		result.addAll(getUserTimesheets(id));
		result.addAll(getShareTimesheets(id));
		return result;
	}

	public List<UserResponse> getShareUserList(long id) throws SQLException {
		
		List<UserResponse> userList = new ArrayList<>();
		String sql = "SELECT u.*,s.tid, s.share_mode, s.share_status FROM user_table u "
				+ "JOIN share_relation s ON u.uid = s.uid WHERE s.tid = ?";
		
		con = ConnectionManager.getConnection();

		PreparedStatement statement = con.prepareStatement(sql);
		statement.setLong(1, id);
		ResultSet rs = statement.executeQuery();
		
		while (rs.next()) {
			long uid = rs.getLong(1);
			String firstName = rs.getString(2);
			String lastName = rs.getString(3);
			String email = rs.getString(4);
			long fbid = rs.getLong(5);
			Timestamp created = rs.getTimestamp(6);
			long tid = rs.getLong(7);
			int shareMode = rs.getInt(8);
			int shareStatus = rs.getInt(9);
			
			userList.add(new UserResponse(uid, firstName, lastName, email, fbid, created, tid, shareMode, shareStatus));
		}

		rs.close();
		statement.close();
		con.close();
		
		return userList;
	}
}
