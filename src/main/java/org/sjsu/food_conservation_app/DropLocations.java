package org.sjsu.food_conservation_app;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.json.JSONException;
import org.json.JSONObject;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;

@Path("/locations")
public class DropLocations {
	
	public static String mongoServerURL = "localhost";
	public static int mongoPort = 27017;
	public static String appDatabaseName = "food_conservation";
	public static String locationCollectionName = "points";
	
	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getLocations(@QueryParam("city") String city) {
		JSONObject errorBody;
		
		try {
			if (city.isBlank()) {
				errorBody = getErrorBody("Missing required parameters");
				return Response.status(400).entity(errorBody.toString()).build();
			}
		} catch(NullPointerException e) {
			errorBody = getErrorBody("Missing required parameters");
			return Response.status(400).entity(errorBody.toString()).build();
		}
		
		MongoClient mongo;
		try {
			mongo = new MongoClient(mongoServerURL, mongoPort);
		} catch (UnknownHostException e) {
			e.printStackTrace();
			errorBody = getErrorBody("Internal Server Error");
			return Response.status(500).entity(errorBody.toString()).build();
		}

		DB db = mongo.getDB(appDatabaseName);
		DBCollection collection = db.getCollection(locationCollectionName);
		
		BasicDBObject searchQuery = new BasicDBObject("City", city.toLowerCase());
		DBCursor cursor = collection.find(searchQuery);
		
		List<Map> l = new ArrayList<Map>();
		while(cursor.hasNext()) {
			DBObject resultElement = cursor.next();
			Map resultElementMap = resultElement.toMap();
			resultElementMap.remove("_id");
			l.add(resultElementMap);
		}
		
		JSONObject outputBody;
		try {
			System.out.println(l);
			outputBody = new JSONObject();
			outputBody.put("locations", l);
		} catch (JSONException e) {
			e.printStackTrace();
			errorBody = getErrorBody("No locations found in the city JSON");
			return Response.status(404).entity(errorBody.toString()).build();
		}
		
		return Response.status(200).entity(outputBody.toString()).build();
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
