package org.sjsu.food_conservation_app;

import beans.FoodBean;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.ws.rs.HeaderParam;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.util.JSON;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.json.JSONException;
import org.json.JSONObject;


@Path("/food")
public class FoodTracker {

	public static String mongoServerURL = "localhost";
	public static int mongoPort = 27017;
	public static String appDatabaseName = "food_conservation";
	public static String foodCollectionName = "foodbank";
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response findFoodAll(@DefaultValue("======") @QueryParam("type") String type, @DefaultValue("-1") @QueryParam("location") String location)
	{
		MongoClient mongo;
		JSONObject errorBody;
		
		System.out.println(type);
		System.out.println(location);
		try {
			mongo = new MongoClient( mongoServerURL , mongoPort );
		} catch (UnknownHostException e1) {
			e1.printStackTrace();
			errorBody = getErrorBody("Internal Server Error");
			return Response.status(500).entity(errorBody.toString()).build();
		}
		
		DB db = mongo.getDB(appDatabaseName);
		DBCollection collection = db.getCollection(foodCollectionName);
		
		BasicDBObject searchQuery = new BasicDBObject();
		searchQuery.put("distributed", false);
		if (! type.equals("======")) {
			searchQuery.put("type", type);
		}
		
		if(! location.equals("-1")) {
			searchQuery.put("location", location);
		}
		
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
	
	@GET
	@Path("/getfoodbytype")
	@Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
	public static ArrayList<FoodBean> findByType(@HeaderParam("type") String type) throws UnknownHostException
	{
		MongoClient mongo = new MongoClient( "localhost" , 27017 );
		DB db = mongo.getDB("FoodRepository");
		DBCollection collection = db.getCollection("foodbank");
		ArrayList<FoodBean> allfoodbytype = new ArrayList<FoodBean>();
		BasicDBObject searchQuery = new BasicDBObject();
		searchQuery.put("type", type);
		DBCursor cursor = collection.find(searchQuery);
		while (cursor.hasNext()) {
			BasicDBObject obj = (BasicDBObject) cursor.next();
			FoodBean foodbean = new FoodBean();
		    try {
		    	foodbean.setType(obj.getString("type"));
		    	foodbean.setQuantity(obj.getString("quantity"));
		    	foodbean.setValid_start_date(obj.getString("valid_start_date"));
		    	foodbean.setValid_end_date(obj.getString("valid_end_date"));
		    	allfoodbytype.add(foodbean);
		    } catch (Exception e) {
		    	e.printStackTrace();
		    }
		}
		  return allfoodbytype;
	}

    @POST
//	@Path("/addfood")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
	public Response addFood(String input){
		
    	JSONObject errorBody;
    	JSONObject inputBody;
		try {
			inputBody = new JSONObject(input);
		} catch (JSONException e) {
			e.printStackTrace();
			errorBody = getErrorBody("Invalid request body");
			return Response.status(400).entity(errorBody.toString()).build();
		}
    	
    	if (! (inputBody.has("type") && inputBody.has("quantity") && inputBody.has("location") && inputBody.has("donated_by"))) {
    		errorBody = getErrorBody("Missing required parameters");
    		return Response.status(400).entity(errorBody.toString()).build();
    	}

		MongoClient mongo;
		try {
			mongo = new MongoClient( mongoServerURL, mongoPort);
		} catch (UnknownHostException e) {
			e.printStackTrace();
			errorBody = getErrorBody("Internal Server Error");
			return Response.status(500).entity(errorBody.toString()).build();
		}
		DB db = mongo.getDB(appDatabaseName);
		DBCollection collection = db.getCollection(foodCollectionName);
		
		BasicDBObject document = new BasicDBObject();		

		DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		Date date = new Date();
		
		try {
			document.put("type", inputBody.getString("type"));
			document.put("quantity", inputBody.getString("quantity"));
			document.put("location", inputBody.getString("location"));
			document.put("donated_by", inputBody.getString("donated_by"));
			document.put("donated_date", dateFormat.format(date));
			document.put("distributed", false);
		} catch (JSONException e) {
			e.printStackTrace();
			errorBody = getErrorBody("Internal Server Error");
			return Response.status(500).entity(errorBody.toString()).build();
		}

		collection.insert(document);
		return Response.status(201).build();
	}
    
    @DELETE
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteFood(String input) {
    	JSONObject errorBody;
    	JSONObject inputBody;
		try {
			inputBody = new JSONObject(input);
		} catch (JSONException e) {
			e.printStackTrace();
			errorBody = getErrorBody("Invalid request body");
    		return Response.status(400).entity(errorBody.toString()).build();
		}
    	
    	if (!(inputBody.has("location") && inputBody.has("type") && inputBody.has("distributed_by"))) {
    		errorBody = getErrorBody("Invalid request body");
    		return Response.status(400).entity(errorBody.toString()).build();
    	}
    	
    	String location;
    	String type;
    	String distributedBy;

    	try {
			location = inputBody.getString("location");
			type = inputBody.getString("type");
			distributedBy = inputBody.getString("distributed_by");
			
		} catch (JSONException e) {
			e.printStackTrace();
			errorBody = getErrorBody("Invalid request body");
    		return Response.status(400).entity(errorBody.toString()).build();
		}
    	
    	MongoClient mongo;
		try {
			mongo = new MongoClient(mongoServerURL, mongoPort);
		} catch (UnknownHostException e) {

			e.printStackTrace();
			errorBody = getErrorBody("Internal Server Error");
    		return Response.status(400).entity(errorBody.toString()).build();
		}

		DB db = mongo.getDB(appDatabaseName);
		DBCollection collection = db.getCollection(foodCollectionName);
		
		BasicDBObject searchQuery = new BasicDBObject();
		searchQuery.put("location", location);
		searchQuery.put("type", type);
		searchQuery.put("distributed", false);
		
		DBObject object = collection.findOne(searchQuery);
		
		if (object == null) {
			errorBody = getErrorBody("Food not found");
			return Response.status(404).entity(errorBody.toString()).build();
		}
		
		System.out.println(object);
		DBObject updateObject = object;
		JSONObject outputBody;
		try {
			outputBody = new JSONObject(object.toString());
			outputBody.remove("_id");
		} catch (JSONException e) {
			e.printStackTrace();
			errorBody = getErrorBody("Internal Server Error");
			return Response.status(500).entity(errorBody.toString()).build();
		}
		
		BasicDBObject document = new BasicDBObject();		
		DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		Date date = new Date();
		
//		collection.findAndRemove(searchQuery);
		updateObject.put("distributed", true);
		updateObject.put("distributed_on", dateFormat.format(date));
		updateObject.put("distributed_by", distributedBy);

		collection.findAndModify(searchQuery, updateObject);
    	return Response.status(200).entity(outputBody.toString()).build();
    }
    
    @POST
	@Path("/deletefoodbytype")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public static boolean deleteFoodByType(@HeaderParam("type") String type) throws UnknownHostException
	{
		MongoClient mongo = new MongoClient( "localhost" , 27017 );
		DB db = mongo.getDB("FoodRepository");
		DBCollection collection = db.getCollection("foodbank");
		
		boolean del_status = false;
		BasicDBObject searchQuery = new BasicDBObject();
		searchQuery.put("type", type);
		collection.remove(searchQuery);
		return del_status = true;
	}
	
    @POST
	@Path("/deleteallfood")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public static boolean deleteAllFood() throws UnknownHostException
	{
		MongoClient mongo = new MongoClient( "localhost" , 27017 );
		DB db = mongo.getDB("FoodRepository");
		DBCollection collection = db.getCollection("foodbank");
		
		boolean del_status = false;
		DBCursor cursor = collection.find();
		while (cursor.hasNext()) {
			collection.remove(cursor.next());
		}
		return del_status = true;
	}
	
    @POST
	@Path("/updatebytype")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public static boolean updateByType(@HeaderParam("type") String type, @HeaderParam("new_qty") String new_qty) throws UnknownHostException
	{
		MongoClient mongo = new MongoClient( "localhost" , 27017 );
		DB db = mongo.getDB("FoodRepository");
		DBCollection collection = db.getCollection("foodbank");
		
		boolean update_status = false;
		BasicDBObject query = new BasicDBObject();
		query.put("type", type);
		BasicDBObject newDocument = new BasicDBObject();
		newDocument.put("quantity", new_qty);			
		BasicDBObject updateObj = new BasicDBObject();
		updateObj.put("$set", newDocument);
		collection.update(query, updateObj);
		return update_status = true;
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
