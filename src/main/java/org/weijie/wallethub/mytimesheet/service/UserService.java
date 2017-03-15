package org.weijie.wallethub.mytimesheet.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.weijie.wallethub.mytimesheet.database.ConnectionManager;
import org.weijie.wallethub.mytimesheet.model.User;

public class UserService {

	private Connection con;

	public UserService() {

	}

	public User getUser(long id) throws SQLException {
		User mUser = null;
		String sql = "SELECT * FROM user_table WHERE uid = ?";

		con = ConnectionManager.getConnection();

		PreparedStatement statement = con.prepareStatement(sql);
		statement.setLong(1, id);

		ResultSet rs = statement.executeQuery();

		if (rs.next()) {
			long uid = rs.getLong(1);
			String firstName = rs.getString(2);
			String lastName = rs.getString(3);
			String email = rs.getString(4);
			long fbid = rs.getLong(5);
			Timestamp created = rs.getTimestamp(6);

			mUser = new User(uid, firstName, lastName, email, fbid, created);
		}

		rs.close();
		statement.close();
		con.close();

		return mUser;
	}
	
	public User getFBUser(long id) throws SQLException {
		User mUser = null;
		String sql = "SELECT * FROM user_table WHERE facebook_id = ?";

		con = ConnectionManager.getConnection();

		PreparedStatement statement = con.prepareStatement(sql);
		statement.setLong(1, id);

		ResultSet rs = statement.executeQuery();

		if (rs.next()) {
			long uid = rs.getLong(1);
			String firstName = rs.getString(2);
			String lastName = rs.getString(3);
			String email = rs.getString(4);
			long fbid = rs.getLong(5);
			Timestamp created = rs.getTimestamp(6);

			mUser = new User(uid, firstName, lastName, email, fbid, created);
		}

		rs.close();
		statement.close();
		con.close();

		return mUser;
	}
	
	public User getEmailUser(String userEmail) throws SQLException {
		User mUser = null;
		String sql = "SELECT * FROM user_table WHERE email = ?";

		con = ConnectionManager.getConnection();

		PreparedStatement statement = con.prepareStatement(sql);
		statement.setString(1, userEmail);

		ResultSet rs = statement.executeQuery();

		if (rs.next()) {
			long uid = rs.getLong(1);
			String firstName = rs.getString(2);
			String lastName = rs.getString(3);
			String email = rs.getString(4);
			long fbid = rs.getInt(5);
			Timestamp created = rs.getTimestamp(6);

			mUser = new User(uid, firstName, lastName, email, fbid, created);
		}

		rs.close();
		statement.close();
		con.close();

		return mUser;
	}
	
	public Response addUser(User mUser) throws SQLException {
		String sql = "INSERT INTO user_table (first_name, last_name,"
				+ " email, facebook_id) "
				+ "VALUES (?, ?, ?, ?)";

		con = ConnectionManager.getConnection();

		PreparedStatement statement = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
		statement.setString(1, mUser.getFirstName());
		statement.setString(2, mUser.getLastName());
		statement.setString(3, mUser.getEmail());
		statement.setLong(4, mUser.getFbid());

		boolean rowInserted = statement.executeUpdate() > 0;
		ResultSet rs = statement.getGeneratedKeys();
		if (rs.next()){
		    mUser.setUid(rs.getInt(1));
		}
		statement.close();
		con.close();
		if (rowInserted) {
			
			return Response.status(Response.Status.OK)
					.entity(mUser)
					.type(MediaType.APPLICATION_JSON).build();
		}
		else {
			return Response.serverError().entity("Server Error").build();
		}
	}

	public Response updateUser(User mUser) throws SQLException {

		String sql = "UPDATE user_table SET first_name = ?, last_name = ?,"
				+ " email = ?, facebook_id = ? WHERE uid = ?";
		
		con = ConnectionManager.getConnection();

		PreparedStatement statement = con.prepareStatement(sql);
		statement.setString(1, mUser.getFirstName());
		statement.setString(2, mUser.getLastName());
		statement.setString(3, mUser.getEmail());
		statement.setLong(4, mUser.getFbid());
		statement.setLong(5, mUser.getUid());
		
		boolean rowUpdated = statement.executeUpdate() > 0;
		statement.close();
		con.close();
		if (rowUpdated) {
			return Response.status(Response.Status.OK)
					.entity(mUser)
					.type(MediaType.APPLICATION_JSON).build();
		}
		else {
			return Response.serverError().entity("Server Error").build();
		}
	}
	
	public Response deleteUser (long id) {
		// Be careful!
		//Delete a user will result in delete all timesheet related to the user
		return null;
	}

	public List<User> getAllUsers() throws SQLException {
		List<User> userList = new ArrayList<>();
		String sql = "SELECT * FROM user_table";

		con = ConnectionManager.getConnection();

		PreparedStatement statement = con.prepareStatement(sql);

		ResultSet rs = statement.executeQuery();

		while (rs.next()) {
			long uid = rs.getLong(1);
			String firstName = rs.getString(2);
			String lastName = rs.getString(3);
			String email = rs.getString(4);
			long fbid = rs.getLong(5);
			Timestamp created = rs.getTimestamp(6);

			userList.add(new User(uid, firstName, lastName, email, fbid, created));
		}

		rs.close();
		statement.close();
		con.close();

		return userList;
	}
}
