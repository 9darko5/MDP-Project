package net.etfbl.rest.api;

import java.util.ArrayList;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/login")
public class LoginAPIService {
	LoginService service;
	
	public LoginAPIService() {
	}
	
	@GET  
	@Path("/{username}")
	@Produces(MediaType.TEXT_PLAIN)
	public Response getPasswordByUsername(@PathParam("username") String username) {
		service=new LoginService();
		String pass=service.getPassword(username);
		if (pass != null) 
				return Response.status(200).entity(pass).build();
		else 
			return Response.status(404).build();
		
	}
	
	@PUT
	@Path("/{username}")
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.TEXT_PLAIN)
	public Response changePassword(@PathParam("username") String username, String lozinka) {
		service=new LoginService();
		boolean resp=service.updateAccount(username, lozinka);
		if (resp) {
			return Response.status(200).entity(resp).build();
		} else {
			return Response.status(404).build();
		}
	}
	
	@PUT
	@Path("/{username}/statistics")
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.TEXT_PLAIN)
	public Response setStatistics(@PathParam("username") String username, String loginDate_logoutDate_time) {
		service=new LoginService();
		boolean resp=service.setStatistics(username, loginDate_logoutDate_time);
		if (resp) {
			return Response.status(200).entity(resp).build();
		} else {
			return Response.status(404).build();
		}
	}
	
	@GET
	@Path("/{username}/statistics")
	@Produces(MediaType.APPLICATION_JSON)
	public ArrayList<String> getStatistics(@PathParam("username") String username) {
		service=new LoginService();
		return service.getStatistics(username);
		
	}
}
