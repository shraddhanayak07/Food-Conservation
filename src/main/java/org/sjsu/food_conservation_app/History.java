package org.sjsu.food_conservation_app;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.json.JSONException;
import org.json.JSONObject;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;

@Path("/history")
public class History {

	public static String mongoServerURL = "localhost";
	public static int mongoPort = 27017;
	public static String appDatabaseName = "food_conservation";
	public static String foodCollectionName = "foodbank";
	
	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getHistory(@DefaultValue("-1") @QueryParam("user") String userID) {
		
		JSONObject errorBody;
		MongoClient mongo;
		
		if (userID.equals("-1")) {
			errorBody = getErrorBody("Missing required parameter");
			return Response.status(400).entity(errorBody.toString()).build();
		}
		
		try {
			mongo = new MongoClient( mongoServerURL , mongoPort );
		} catch (UnknownHostException e1) {
			e1.printStackTrace();
			errorBody = getErrorBody("Internal Server Error");
			return Response.status(500).entity(errorBody.toString()).build();
		}
		
		DB db = mongo.getDB(appDatabaseName);
		DBCollection collection = db.getCollection(foodCollectionName);
		
		DBObject userIDDonor = new BasicDBObject("donated_by", userID);
		DBObject userIDVolunteer = new BasicDBObject("distributed_by", userID);
		
		BasicDBList userOrConditionList = new BasicDBList();
		userOrConditionList.add(userIDVolunteer);
		userOrConditionList.add(userIDDonor);
		
		DBObject userOr = new BasicDBObject("$or", userOrConditionList);
		
		DBObject distributed = new BasicDBObject("distributed", true);
		BasicDBList finalList = new BasicDBList();
		finalList.add(distributed);
		finalList.add(userOr);
		
		DBObject searchQuery = new BasicDBObject("$and", finalList);

		
		DBCursor cursor = collection.find(searchQuery);
		List<Map> allfood = new ArrayList<Map>();
		
		while (cursor.hasNext()) {
			BasicDBObject obj = (BasicDBObject) cursor.next();
			Map resultElementMap = obj.toMap();
			resultElementMap.remove("_id");
	    	allfood.add(resultElementMap);
		}
		JSONObject response = new JSONObject();
		try {
			response.put("food", allfood);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return Response.status(200).entity(response.toString()).build();
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
