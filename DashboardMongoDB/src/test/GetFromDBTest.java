package test;

import org.bson.Document;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.mongodb.client.DistinctIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Indexes;
import database.DatabaseConnector;
/*
 * 
 * 
 *  CLASS FOR TEST PURPOSE
 * 
 * 
 */
public class GetFromDBTest {
	public static void main(String[] args) throws JSONException {

		//ArrayList<ArrayList<Robot>> robots = new ArrayList<>();
		
		long startTime = System.currentTimeMillis();
		
		/*for(int i = 0; i < 100; i++) {
			ArrayList<Robot> robot = new ClusterDAO().getRobots(i);
			robots.add(robot);
		}*/
		
		JSONObject robots_json = new JSONObject();
		JSONObject clusters_json = new JSONObject();
		
		MongoDatabase database = DatabaseConnector.CONNECTION.getDatabase();
		MongoCollection<Document> robots_collection = database.getCollection("robot");
		MongoCollection<Document> clusters_collection = database.getCollection("cluster");
		
		robots_collection.createIndex(Indexes.ascending("cluster_id"));
		clusters_collection.createIndex(Indexes.ascending("area_id"));
		
		DistinctIterable<Integer> areas_count = clusters_collection.distinct("area_id", Integer.class);
		MongoCursor<Integer> areas = areas_count.iterator();
		
		try {
			while(areas.hasNext()) {
				MongoCursor<Document> clusters = clusters_collection.find(Filters.eq("area_id", areas.next())).iterator();
				try {
					while(clusters.hasNext()) {
						Document current = clusters.next();
						JSONArray robots_array = new JSONArray();
						MongoCursor<Document> robots = robots_collection.find(Filters.eq("cluster_id", current.getInteger("_id"))).iterator();
						try {
							while(robots.hasNext()) {
								robots_array.put(robots.next().getInteger("_id"));
							}
						}
						finally {
							try {
								robots_json.put(current.getInteger("_id").toString(), (Object) robots_array);
							} catch (JSONException e) {
								e.printStackTrace();
							}
							robots.close();
						}
					}
				}
				finally {
					clusters.close();
				}
			}
		}
		finally{
			areas.close();
		}
		
		
		System.out.println(robots_json.get("0"));
		
		long endTime   = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        
        //System.out.println(robots);
        //System.out.println(robots.size());
        //System.out.println(robots.get(99).size());
        System.out.println(totalTime);
	}
}
