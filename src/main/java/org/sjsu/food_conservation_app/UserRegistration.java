package org.sjsu.food_conservation_app;

import java.net.UnknownHostException;
import java.util.Optional;

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
import com.mongodb.util.JSON;

@Path("/user")
public class UserRegistration {

	public static String mongoServerURL = "localhost";
	public static int mongoPort = 27017;
	public static String appDatabaseName = "food_conservation";
	public static String userCollectionName = "users";
	
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response registerUser(String s) {
		
		JSONObject errorBody;
		JSONObject inputBody;
		try {
			 inputBody = new JSONObject(s);
			System.out.println(inputBody);
			
		} catch (JSONException e) {
			e.printStackTrace();
			errorBody = new JSONObject();
			
			try {
				errorBody.put("error", "Invalid Request Body");
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
			
			return Response.status(400).entity(errorBody.toString()).header("Access-Control-Allow-Origin", "*").build();
		}
		
		boolean validRequestBody = validateRequestBody(inputBody);
		
		if (!validRequestBody) {
			errorBody = new JSONObject();
			try {
				errorBody.put("error", "Missing Required Parameters");
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return Response.status(400).entity(errorBody.toString()).header("Access-Control-Allow-Origin", "*").build();
		}
		
		JSONObject addUserToDB = addUserToDB(inputBody);
		System.out.println(addUserToDB);
		if(addUserToDB.has("error")) {
			return Response.status(500).entity(addUserToDB.toString()).header("Access-Control-Allow-Origin", "*").build();
		}
		
		return Response.status(200).entity(addUserToDB.toString()).header("Access-Control-Allow-Origin", "*").build();
	}
	
	public static boolean validateRequestBody(JSONObject inputBody) {
		
		if(inputBody.has("firstName") && inputBody.has("lastName") && inputBody.has("email") && inputBody.has("password") && inputBody.has("city") && inputBody.has("userType")) {
			return true;
		}
		
		return false;
	}
	
	public static JSONObject addUserToDB(JSONObject inputBody) {
		
		MongoClient mongo;
		JSONObject errorBody;
		try {
			mongo = new MongoClient(mongoServerURL, mongoPort);
		} catch (UnknownHostException e) {
			e.printStackTrace();
			errorBody = new JSONObject();
			
			try {
				errorBody.put("error", "Internal Server Error");
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
			
			return errorBody;
		}

		DB db = mongo.getDB(appDatabaseName);
		DBCollection collection = db.getCollection(userCollectionName);
		
		String userID = getNextSeq(db, "users");
		try {
			inputBody.put("userID", userID);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		try {
			String password = inputBody.getString("password");
			String salt = Password.generateSalt(512).get();
			Optional<String> key = Password.hashPassword(password, salt);
			inputBody.remove("password");
			inputBody.put("password", key.get());
			inputBody.put("salt", salt);
		} catch (JSONException e) {
			e.printStackTrace();
			errorBody = new JSONObject();
			
			try {
				errorBody.put("error", "Internal Server Error");
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
			return errorBody;	
		}
		
		DBObject document = (DBObject) JSON.parse(inputBody.toString());
		try {
			collection.insert(document);
		} catch (Exception e){
			errorBody = new JSONObject();
			System.out.println("Inside exception");
			try {
				errorBody.put("error", "Email already exists");
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
			
			return errorBody;
		}
		
		inputBody.remove("password");
		inputBody.remove("salt");
		
		return inputBody;
	}
	
	public static String getNextSeq(DB db, String collectionField) {
		
		String counterCollectionName = "counters";
		DBCollection collection = db.getCollection(counterCollectionName);
		
		BasicDBObject find = new BasicDBObject();
		find.put("_id", collectionField);
		
		BasicDBObject update = new BasicDBObject();
		update.put("$inc", new BasicDBObject("seq", 1));
	    DBObject  obj =  collection.findAndModify(find, update);
	    String id = obj.get("seq").toString();
	    id = id.substring(0, id.length() - 2);
	    return id;
	}
	
}
