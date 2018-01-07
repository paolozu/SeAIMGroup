package test;

import java.io.FileWriter;
import java.io.IOException;
import org.bson.Document;
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
		
		long startTime = System.currentTimeMillis();
		
		JSONObject robots_and_clusters_IR = new JSONObject();
		
		MongoDatabase database = DatabaseConnector.CONNECTION.getDatabase();
		MongoCollection<Document> robots_collection = database.getCollection("robot");
		MongoCollection<Document> clusters_collection = database.getCollection("cluster");
		
		robots_collection.createIndex(Indexes.ascending("cluster_id"));
		clusters_collection.createIndex(Indexes.ascending("area_id"));
		
		DistinctIterable<Integer> areas_count = clusters_collection.distinct("area_id", Integer.class);
		MongoCursor<Integer> areas = areas_count.iterator();
		
		try {
			while(areas.hasNext()) {
				Integer current_area = areas.next();
				MongoCursor<Document> clusters = clusters_collection.find(Filters.eq("area_id", current_area)).iterator();
				JSONObject clusters_index_json = new JSONObject();
				try {
					while(clusters.hasNext()) {
						Document current_cluster = clusters.next();
						MongoCursor<Document> robots = robots_collection.find(Filters.eq("cluster_id", current_cluster.getInteger("_id"))).iterator();
						JSONObject robots_json = new JSONObject();
						JSONObject clusters_json = new JSONObject();
						try {
							while(robots.hasNext()) {
								Document current_robot = robots.next();
								robots_json.put(current_robot.getInteger("_id").toString(), current_robot.getDouble("robot_ir"));
							}
						}
						finally {
							clusters_json.put("cluster_ir", current_cluster.getDouble("cluster_ir"));
							clusters_json.put("robots", robots_json);
							clusters_index_json.put(current_cluster.getInteger("_id").toString(), clusters_json);
							robots.close();
						}
					}
				}
				finally {
					robots_and_clusters_IR.put(current_area.toString(), clusters_index_json);
					clusters.close();
				}
			}
		}
		finally{
			areas.close();
		}
		
		JSONObject result = (JSONObject) robots_and_clusters_IR.get("5");
		JSONObject test = (JSONObject) result.get("50");
		JSONObject robots_count = (JSONObject) test.get("robots");
		
		System.out.println(robots_count.toString());
		
		try {
			FileWriter a = new FileWriter("C:/Users/Stefano_Martella/Desktop/robots_and_clusters_IR.txt");
			a.write(robots_and_clusters_IR.toString());
			a.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		long endTime   = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        
        //System.out.println(robots);
        //System.out.println(robots.size());
        //System.out.println(robots.get(99).size());
        System.out.println(totalTime);
	}
}
