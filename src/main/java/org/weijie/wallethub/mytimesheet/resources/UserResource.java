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

import org.weijie.wallethub.mytimesheet.model.User;
import org.weijie.wallethub.mytimesheet.service.UserService;

@Path("/users")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class UserResource {

	UserService userService = new UserService();
	
	@GET 
	@Path("/all")
	public List<User> getAllUser() throws SQLException {
		return userService.getAllUsers();
	}
	
	@GET
	public User getUserProfile(@QueryParam("fbid") long fbid, @QueryParam("email") String email) throws SQLException {
		if (fbid > 0) {
			return userService.getFBUser(fbid);
		}
		if (email != null) {
			return userService.getEmailUser(email);
		}
		return null;
	}
	
	@GET
	@Path("/{uid}")
	public User getUserProfile(@PathParam("uid") long uid) throws SQLException {
		return userService.getUser(uid);
	}
	
	@POST
	public Response addUser(User mUser) throws SQLException {
		return userService.addUser(mUser);
	}
	
	@PUT
	@Path("/{uid}")
	public Response updateUser(@PathParam("uid") long uid,User mUser) throws SQLException{
		mUser.setUid(uid);
		return userService.updateUser(mUser);
	}
	
	@DELETE
	@Path("/{uid}")
	public Response deleteUser(@PathParam("uid") long uid) {
		return userService.deleteUser(uid);
	}
}
