package org.weijie.wallethub.mytimesheet.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.sql.Date;
import java.util.List;
import javax.json.Json;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.weijie.wallethub.mytimesheet.database.ConnectionManager;
import org.weijie.wallethub.mytimesheet.model.Record;

public class RecordService {


	private Connection con;

	public RecordService() {

	}

	public List<Record> getAllRecords() throws SQLException {
		List<Record> recordList = new ArrayList<>();

		String sql = "SELECT * FROM time_record";

		con = ConnectionManager.getConnection();
		Statement stmt = con.createStatement();
		ResultSet rs=stmt.executeQuery(sql); 

		while(rs.next())  
		{
			long rid = rs.getLong(1);
			Date date = rs.getDate(2);
			Time start_time = rs.getTime(3);
			Time end_time = rs.getTime(4);
			int break_time = rs.getInt(5);
			int work_time = rs.getInt(6);
			String comments = rs.getString(7);
			long tid = rs.getLong(8);
			int is_weekend = rs.getInt(9);
			Timestamp created = rs.getTimestamp(10);
			Timestamp updated = rs.getTimestamp(11);

			Record record = new Record(rid, date, start_time, end_time, break_time, work_time, comments, tid, is_weekend, created, updated);
			recordList.add(record);
		}

		rs.close();
		stmt.close();

		con.close();  

		return recordList;
	}

	public List<Record> getTimesheetRecords(long id) throws SQLException {
		List<Record> recordList = new ArrayList<>();
		String sql = "SELECT * FROM time_record WHERE tid = ?";

		con = ConnectionManager.getConnection();

		PreparedStatement statement = con.prepareStatement(sql);
		statement.setLong(1, id);

		ResultSet rs = statement.executeQuery();

		while(rs.next()) {
			long rid = rs.getLong(1);
			Date date = rs.getDate(2);
			Time start_time = rs.getTime(3);
			Time end_time = rs.getTime(4);
			int break_time = rs.getInt(5);
			int work_time = rs.getInt(6);
			String comments = rs.getString(7);
			long tid = rs.getLong(8);
			int is_weekend = rs.getInt(9);
			Timestamp created = rs.getTimestamp(10);
			Timestamp updated = rs.getTimestamp(11);

			Record record = new Record(rid, date, start_time, end_time, break_time, work_time, comments, tid, is_weekend, created, updated);
			recordList.add(record);
		}

		rs.close();
		statement.close();
		con.close();

		return recordList;
	}

	public Record getSingleRecord(long id) throws SQLException {
		Record mRecord = null;
		String sql = "SELECT * FROM time_record WHERE RID = ?";

		con = ConnectionManager.getConnection();

		PreparedStatement statement = con.prepareStatement(sql);
		statement.setLong(1, id);

		ResultSet rs = statement.executeQuery();

		if (rs.next()) {
			long rid = rs.getLong(1);
			Date date = rs.getDate(2);
			Time start_time = rs.getTime(3);
			Time end_time = rs.getTime(4);
			int break_time = rs.getInt(5);
			int work_time = rs.getInt(6);
			String comments = rs.getString(7);
			long tid = rs.getLong(8);
			int is_weekend = rs.getInt(9);
			Timestamp created = rs.getTimestamp(10);
			Timestamp updated = rs.getTimestamp(11);

			mRecord = new Record(rid, date, start_time, end_time, break_time, work_time, comments, tid, is_weekend, created, updated);
		}

		rs.close();
		statement.close();
		con.close();

		return mRecord;
	}

	public Response insertRecord(Record mRecord) throws SQLException {
		String sql = "INSERT INTO time_record (record_date, start_time, end_time,"
				+ " break_time, work_time, comments, tid, is_weekend) "
				+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

		con = ConnectionManager.getConnection();

		PreparedStatement statement = con.prepareStatement(sql);
		statement.setDate(1, mRecord.getDate() );
		statement.setTime(2, mRecord.getStart_time());
		statement.setTime(3, mRecord.getEnd_time());
		statement.setInt(4, mRecord.getBreak_time());
		statement.setInt(5, mRecord.getWork_time());
		statement.setString(6, mRecord.getComments());
		statement.setLong(7, mRecord.getTid());
		statement.setInt(8, mRecord.getIs_weekend());

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

	public Response updateRecord(Record mRecord) throws SQLException {
		String sql = "UPDATE time_record SET record_date = ?, start_time = ?, end_time = ?, "
				+ "break_time = ?, work_time = ?, comments = ?, tid = ?, is_weekend = ?, updated_time = default "
				+ "WHERE rid = ?";

		con = ConnectionManager.getConnection();

		PreparedStatement statement = con.prepareStatement(sql);
		statement.setDate(1, mRecord.getDate() );
		statement.setTime(2, mRecord.getStart_time());
		statement.setTime(3, mRecord.getEnd_time());
		statement.setInt(4, mRecord.getBreak_time());
		statement.setInt(5, mRecord.getWork_time());
		statement.setString(6, mRecord.getComments());
		statement.setLong(7, mRecord.getTid());
		statement.setInt(8, mRecord.getIs_weekend());
		statement.setLong(9, mRecord.getRid());

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

	public Response deleteRecord(long rid) throws SQLException {
		String sql = "DELETE FROM time_record WHERE rid = ?";

		con = ConnectionManager.getConnection();

		PreparedStatement statement = con.prepareStatement(sql);
		statement.setLong(1, rid);

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
			return Response.serverError().entity("Server Error").build();
		}     
	}
	
	public List<Record> getRevokedRecords(long id1, long id2) throws SQLException {
		List<Record> recordList = new ArrayList<>();
		String sql = "SELECT * FROM revoked_record WHERE tid = ? AND revoked_uid = ?";

		con = ConnectionManager.getConnection();

		PreparedStatement statement = con.prepareStatement(sql);
		statement.setLong(1, id1);
		statement.setLong(2, id2);

		ResultSet rs = statement.executeQuery();

		while(rs.next()) {
			long rid = rs.getLong(1);
			Date date = rs.getDate(3);
			Time start_time = rs.getTime(4);
			Time end_time = rs.getTime(5);
			int break_time = rs.getInt(6);
			int work_time = rs.getInt(7);
			String comments = rs.getString(8);
			long tid = rs.getLong(9);
			int is_weekend = rs.getInt(10);
			Timestamp created = rs.getTimestamp(11);
			Timestamp updated = rs.getTimestamp(12);

			Record record = new Record(rid, date, start_time, end_time, break_time, work_time, comments, tid, is_weekend, created, updated);
			recordList.add(record);
		}

		rs.close();
		statement.close();
		con.close();

		return recordList;
	}
	
	public Response insertDummyRecord(Record mRecord) throws SQLException {
		String sql = "INSERT INTO time_record (record_date, start_time, end_time,"
				+ " break_time, work_time, comments, tid, is_weekend) "
				+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
		
//		sql = "INSERT INTO date_helper VALUES (?)";

		con = ConnectionManager.getConnection();
		
		boolean rowInserted =false;
		
		for (int i = 0; i < 50; i++) {

			PreparedStatement statement = con.prepareStatement(sql);
			statement.setDate(1, mRecord.getDate() );

			Date dt = mRecord.getDate();
			Calendar cal = Calendar.getInstance();
			cal.setTime(dt);
			cal.add(Calendar.DAY_OF_YEAR,1);
			mRecord.setDate(new Date(cal.getTimeInMillis()));

			statement.setTime(2, mRecord.getStart_time());
			statement.setTime(3, mRecord.getEnd_time());
			statement.setInt(4, mRecord.getBreak_time());
			statement.setInt(5, mRecord.getWork_time());
			statement.setString(6, mRecord.getComments());
			statement.setLong(7, mRecord.getTid());
			statement.setInt(8, mRecord.getIs_weekend());

			rowInserted = statement.executeUpdate() > 0;
			statement.close();
		}
		con.close();
		if (rowInserted) {
			String json = Json.createObjectBuilder()
					.add("key1", "value1")
					.add("key2", "value2")
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

}
