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

public class FoodTracker {
		
		/* This has insert, update, delete, search operations of all food entities */
		
		public static String mongoServerURL = "localhost";
		public static int mongoPort = 27017;
		public static String appDatabaseName = "food_conservation";
		public static String locationCollectionName = "foodbank";
	
		
	public static boolean addFood(String type, float quantity, String valid_start_date, String valid_end_date) {
		boolean add_status = false;
		//System.out.println("Adding food into database : "+ type + quantity);
		BasicDBObject document = new BasicDBObject();		
		document.put("type", type);
		document.put("quantity", quantity);
		document.put("valid_start_date", valid_start_date);
		document.put("valid_end_date", valid_end_date);
		locationCollectionName.insert(document);
		add_status = true;
	}


	public static JSONArray findByType(DBCollection table, String type)
	{
		JSONArray jsonarray = new JSONArray();
		BasicDBObject searchQuery = new BasicDBObject();
		searchQuery.put("type", type);
		DBCursor cursor = table.find(searchQuery);
		while (cursor.hasNext()) {
			BasicDBObject obj = (BasicDBObject) cursor.next();
		    JSONObject jsonobj = new JSONObject();
		    BasicDBList name = (BasicDBList) obj.get("Name");
		    try {
				jsonobj.put("type", obj.getString("type"));
				jsonobj.put("quantity", obj.getString("quantity"));
				jsonobj.put("valid_start_date", obj.getString("valid_start_date"));
				jsonobj.put("valid_end_date", obj.getString("valid_end_date"));
				jsonarray.put(jsonobj);
		    } catch (JSONException e) {
		    	// TODO Auto-generated catch block
		    	e.printStackTrace();
		    }
		}
		  return jsonarray;
	}
	
	public static boolean deleteFoodByType(String type)
	{
		boolean del_status = false;
		BasicDBObject searchQuery = new BasicDBObject();
		searchQuery.put("type", type);
		locationCollectionName.remove(searchQuery);
		return del_status = true;
	}
	
	public static boolean deleteAllFood()
	{
		boolean del_status = false;
		DBCursor cursor = locationCollectionName.find();
		while (cursor.hasNext()) {
			locationCollectionName.remove(cursor.next());
		}
		return del_status = true;
	}
	
	public static JSONArray findFoodAll(DBCollection locationCollectionName)
	{
		JSONArray jsonarray = null;
		DBCursor cursor = locationCollectionName.find();
		while (cursor.hasNext()) {
			BasicDBObject obj = (BasicDBObject) cursor.next();
		    JSONObject jsonobj = new JSONObject();
		    BasicDBList name = (BasicDBList) obj.get("Name");
		    try {
				jsonobj.put("type", obj.getString("type"));
				jsonobj.put("quantity", obj.getString("quantity"));
				jsonobj.put("valid_start_date", obj.getString("valid_start_date"));
				jsonobj.put("valid_end_date", obj.getString("valid_end_date"));
				jsonarray.put(jsonobj);
		    } catch (JSONException e) {
		    	// TODO Auto-generated catch block
		    	e.printStackTrace();
		    }
		}
		  return jsonarray;
	}
	
	
	public static boolean updateByType(String oldname, String newname)
	{
		boolean update_status = false;
		BasicDBObject query = new BasicDBObject();
		query.put("type", oldname);
		BasicDBObject newDocument = new BasicDBObject();
		newDocument.put("type", newname);			
		BasicDBObject updateObj = new BasicDBObject();
		updateObj.put("$set", newDocument);
		locationCollectionName.update(query, updateObj);
		return update_status = true;
	}
	
	public static boolean updateByQuantity(String old_qty, String new_qty)
	{
		boolean update_status = false;
		BasicDBObject query = new BasicDBObject();
		query.put("quantity", old_qty);
		BasicDBObject newDocument = new BasicDBObject();
		newDocument.put("quantity", new_qty);			
		BasicDBObject updateObj = new BasicDBObject();
		updateObj.put("$set", newDocument);
		locationCollectionName.update(query, updateObj);
		return update_status = true;
	}

}

