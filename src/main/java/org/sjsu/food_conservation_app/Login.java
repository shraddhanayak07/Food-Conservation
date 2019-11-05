package org.sjsu.food_conservation_app;

import java.net.UnknownHostException;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.json.JSONException;
import org.json.JSONObject;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;


@Path("/login")
public class Login {
	public static String mongoServerURL = "localhost";
	public static int mongoPort = 27017;
	public static String appDatabaseName = "food_conservation";
	public static String userCollectionName = "users";
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response loginUser(String s) {
		
		JSONObject inputBody;
		JSONObject errorBody;
		
		try {
			inputBody = new JSONObject(s);
		} catch (JSONException e) {
			e.printStackTrace();
			errorBody = getErrorBody("Invalid Request Body");
			return Response.status(400).entity(errorBody.toString()).build();
		}
		
		boolean validRequestBody = validateRequestBody(inputBody);
		
		if (! validRequestBody) {
			errorBody = getErrorBody("Required Parameters Missing");
			return Response.status(400).entity(errorBody.toString()).build();
		}
		
		JSONObject outputResponse = matchPassword(inputBody);
		
		if (outputResponse.has("error")) {
			return Response.status(401).entity(outputResponse.toString()).build();
		}
		
		return Response.status(200).entity(outputResponse.toString()).build();
	}
	
	public static boolean validateRequestBody(JSONObject inputBody) {
		if (inputBody.has("email") && inputBody.has("password")) {
			return true;
		}
		return false;
	}
	
	public static JSONObject matchPassword(JSONObject inputBody) {

		JSONObject errorBody;
		
		String email;
		String password;
		try {
			email = inputBody.getString("email");
			password = inputBody.getString("password");
		} catch (JSONException e) {
			e.printStackTrace();
			errorBody = getErrorBody("Invalid Email or Password");
			return errorBody;
		}
		
		JSONObject user = getUserFromEmail(email);
		
		if (user.has("error")) {
			errorBody = getErrorBody("Invalid Email or Password");
			return errorBody;
		}
		
		System.out.println(user);
		boolean passowrdMatch = false;
		try {
			System.out.println(password);
			String salt = user.getString("salt");
			String key = user.getString("password");
			passowrdMatch = Password.verifyPassword(password, key, salt);
			user.remove("password");
			user.remove("salt");
		} catch (JSONException e) {
			e.printStackTrace();
			errorBody = getErrorBody("Invalid Email or Password");
			return errorBody;
		}
		
		if(!passowrdMatch) {
			errorBody = getErrorBody("Invalid Email or Password");
			return errorBody;
		}
		
		return user;
	}
	
	public static JSONObject getUserFromEmail(String email) {
		
		JSONObject errorBody;
		MongoClient mongo;
		try {
			mongo = new MongoClient(mongoServerURL, mongoPort);
		} catch (UnknownHostException e) {
			e.printStackTrace();
			errorBody = getErrorBody("Internal Server Error");
			return errorBody;
		}

		DB db = mongo.getDB(appDatabaseName);
		DBCollection collection = db.getCollection(userCollectionName);
		
		BasicDBObject searchQuery = new BasicDBObject("email", email);
		DBObject document = collection.findOne(searchQuery);
		
		if (document == null) {
			errorBody = getErrorBody("Invalid Email or Password");
			return errorBody;
		}
		
		JSONObject outputBody;
		try {
			outputBody = new JSONObject(document.toString());
			outputBody.remove("_id");
		} catch (JSONException e) {
			e.printStackTrace();
			errorBody = getErrorBody("Invalid Email or Password");
			return errorBody;
		}
		
		return outputBody;
	}
	
	public static JSONObject getErrorBody(String errorMessage) {
		
		JSONObject errorBody = new JSONObject();
		
		try {
			errorBody.put("error", errorMessage);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return errorBody;
	}
} 
