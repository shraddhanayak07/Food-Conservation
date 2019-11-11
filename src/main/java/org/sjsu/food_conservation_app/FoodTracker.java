package org.sjsu.food_conservation_app;

import beans.FoodBean;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.MongoClient;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
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

@Path("/food")
public class FoodTracker {
	
	@GET
	@Path("/getall")
	@Produces(MediaType.APPLICATION_JSON)
	public static ArrayList<FoodBean> findFoodAll() throws UnknownHostException
	{
		MongoClient mongo = new MongoClient( "localhost" , 27017 );
		DB db = mongo.getDB("FoodRepository");
		DBCollection collection = db.getCollection("foodbank");
		
		ArrayList<FoodBean> allfood = new ArrayList<FoodBean>();
		DBCursor cursor = collection.find();
		
		while (cursor.hasNext()) {
			BasicDBObject obj = (BasicDBObject) cursor.next();
			FoodBean foodbean = new FoodBean();
		    try {
		    	foodbean.setType(obj.getString("type"));
		    	foodbean.setQuantity(obj.getString("quantity"));
		    	foodbean.setValid_start_date(obj.getString("valid_start_date"));
		    	foodbean.setValid_end_date(obj.getString("valid_end_date"));
		    	allfood.add(foodbean);
				
		    } catch (Exception e) {
		    	e.printStackTrace();
		    }
		}
		  return allfood;
	}
	
	
	public static ArrayList<FoodBean> findByType(String type) throws UnknownHostException
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
	@Path("/post")
    @Consumes(MediaType.APPLICATION_JSON)
    //@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	
	public static Response addFood(@PathParam("type") String type, @PathParam("quantity") float quantity, @PathParam("valid_start_date") String valid_start_date, @PathParam("valid_end_date") String valid_end_date) throws UnknownHostException {
		boolean add_status = false;
		MongoClient mongo = new MongoClient( "localhost" , 27017 );
		DB db = mongo.getDB("FoodRepository");
		DBCollection collection = db.getCollection("foodbank");
		
		BasicDBObject document = new BasicDBObject();		
		document.put("type", type);
		document.put("quantity", quantity);
		document.put("valid_start_date", valid_start_date);
		document.put("valid_end_date", valid_end_date);
		collection.insert(document);
		return Response.status(201).entity(add_status).build();
	}

    
	public static boolean deleteFoodByType(@PathParam("type") String type) throws UnknownHostException
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
	
	
	public static boolean updateByType(@PathParam("oldname") String oldname, @PathParam("newname") String newname) throws UnknownHostException
	{
		MongoClient mongo = new MongoClient( "localhost" , 27017 );
		DB db = mongo.getDB("FoodRepository");
		DBCollection collection = db.getCollection("foodbank");
		
		boolean update_status = false;
		BasicDBObject query = new BasicDBObject();
		query.put("type", oldname);
		BasicDBObject newDocument = new BasicDBObject();
		newDocument.put("type", newname);			
		BasicDBObject updateObj = new BasicDBObject();
		updateObj.put("$set", newDocument);
		collection.update(query, updateObj);
		return update_status = true;
	}
	
	public static boolean updateByQuantity(@PathParam("old_qty") String old_qty, @PathParam("new_qty") String new_qty) throws UnknownHostException
	{
		MongoClient mongo = new MongoClient( "localhost" , 27017 );
		DB db = mongo.getDB("FoodRepository");
		DBCollection collection = db.getCollection("foodbank");
		boolean update_status = false;
		BasicDBObject query = new BasicDBObject();
		query.put("quantity", old_qty);
		BasicDBObject newDocument = new BasicDBObject();
		newDocument.put("quantity", new_qty);			
		BasicDBObject updateObj = new BasicDBObject();
		updateObj.put("$set", newDocument);
		collection.update(query, updateObj);
		return update_status = true;
	}
	
}