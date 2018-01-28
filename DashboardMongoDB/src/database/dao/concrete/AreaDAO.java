package database.dao.concrete;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.bson.Document;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Indexes;
import database.DatabaseConnector;
import database.dao.interfaces.AreaDAOInterface;
import model.Cluster;
import model.Robot;

public class AreaDAO implements AreaDAOInterface {
	
	@Override
	public ConcurrentHashMap<Integer, Cluster> getClusters(Integer area_id){
		MongoDatabase database = DatabaseConnector.CONNECTION.getDatabase();
		MongoCollection<Document> robots_collection = database.getCollection("cluster");
		robots_collection.createIndex(Indexes.ascending("area_id"));
		MongoCursor<Document> cursor = robots_collection.find(Filters.eq("area_id", area_id)).iterator();
		ConcurrentHashMap<Integer, Cluster> clusters = new ConcurrentHashMap<>();
		
		try {
		    while (cursor.hasNext()) {
		    	Document current_cluster = cursor.next();
		    	
		    	int cluster_id = current_cluster.getInteger("_id");
		    	int down_robots = current_cluster.getInteger("down_robots");
		    	double cluster_IR = current_cluster.getDouble("cluster_ir");
		    	Timestamp start_downtime;
		    	ConcurrentHashMap<Integer, Robot> robots = new ClusterDAO().getRobots(cluster_id);
		    	HashMap<Timestamp, Long> downtime_intervals = new HashMap<>();
				long last_ir_table_element = 0;

				Document ir_table = (Document) current_cluster.get("ir_table");
				
				for (ConcurrentMap.Entry<String, Object> ir_table_entry : ir_table.entrySet()) {
					downtime_intervals.put(new Timestamp(Long.valueOf(ir_table_entry.getKey())),
										   Long.valueOf(String.valueOf((ir_table_entry.getValue()))));
					
					last_ir_table_element = Long.valueOf(ir_table_entry.getKey());
				}
				
				start_downtime = new Timestamp(last_ir_table_element);
				
		        Cluster cluster = new Cluster(cluster_id, area_id, down_robots,
		        							  cluster_IR, start_downtime, robots,
		        						      downtime_intervals);
		        
		        clusters.put(cluster_id, cluster);
		    }
		} 
		finally {
		    cursor.close();
		}
		return clusters;
	}
	
}
