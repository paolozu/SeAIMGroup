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
import com.mongodb.client.model.Updates;
import database.DatabaseConnector;
import database.dao.interfaces.ClusterDAOInterface;
import model.Cluster;
import model.Robot;

public class ClusterDAO implements ClusterDAOInterface {

	@Override
	public void insertCluster(Cluster cluster) {
		MongoDatabase database = DatabaseConnector.CONNECTION.getDatabase();
		MongoCollection<Document> clusters_collection = database.getCollection("cluster");
		Document cluster_db = new Document().append("_id", cluster.getClusterId())
											.append("area_id", cluster.getAreaId())
											.append("cluster_ir", cluster.getClusterIR())
											.append("down_robots", cluster.getDownRobots())
											.append("ir_table", new Document());
		
		clusters_collection.insertOne(cluster_db);
	}
	
	@Override
	public void updateClusterIR(Cluster cluster) {
		MongoDatabase database = DatabaseConnector.CONNECTION.getDatabase();
		MongoCollection<Document> clusters_collection = database.getCollection("cluster");
		clusters_collection.updateOne(Filters.eq("_id", cluster.getClusterId()),
									  Updates.set("cluster_ir", cluster.getClusterIR()));
	}
	
	@Override
	public void updateDownRobots(Cluster cluster) {
		MongoDatabase database = DatabaseConnector.CONNECTION.getDatabase();
		MongoCollection<Document> clusters_collection = database.getCollection("cluster");
		clusters_collection.updateOne(Filters.eq("_id", cluster.getClusterId()),
									  Updates.set("down_robots", cluster.getDownRobots()));
	}
	
	@Override
	public void addInIRTable(Cluster cluster, long start_downtime, long downtime_duration) {
		MongoDatabase database = DatabaseConnector.CONNECTION.getDatabase();
		MongoCollection<Document> clusters_collection = database.getCollection("cluster");
		clusters_collection.updateOne(Filters.eq("_id", cluster.getClusterId()),
									  Updates.set("ir_table." + String.valueOf(start_downtime), downtime_duration));
	}
	
	@Override
	public void removeFromIRTable(Cluster cluster, long start_downtime) {
		MongoDatabase database = DatabaseConnector.CONNECTION.getDatabase();
		MongoCollection<Document> clusters_collection = database.getCollection("cluster");
		clusters_collection.updateOne(Filters.eq("_id", cluster.getClusterId()),
				  					  Updates.unset("ir_table." + String.valueOf(start_downtime)));
	}
	
	@Override
	public ConcurrentHashMap<Integer, Robot> getRobots(Integer cluster_id) {
		
		MongoDatabase database = DatabaseConnector.CONNECTION.getDatabase();
		MongoCollection<Document> robots_collection = database.getCollection("robot");
		robots_collection.createIndex(Indexes.ascending("cluster_id"));
		MongoCursor<Document> cursor = robots_collection.find(Filters.eq("cluster_id", cluster_id)).iterator();
		ConcurrentHashMap<Integer, Robot> robots = new ConcurrentHashMap<>();
		
		try {
		    while (cursor.hasNext()) {
		    	Document current_robot = cursor.next();
		    	
		    	int robot_id = current_robot.getInteger("_id");
		    	int previous_down_signals = current_robot.getInteger("previous_down_signals");
		    	int down_signals = current_robot.getInteger("down_signals");
		    	double robot_IR = current_robot.getDouble("robot_ir");
		    	Timestamp start_downtime;
		    	HashMap<Timestamp, Long> downtime_intervals = new HashMap<>();
				long last_ir_table_element = 0;

				Document ir_table = (Document) current_robot.get("ir_table");
				
				for (ConcurrentMap.Entry<String, Object> ir_table_entry : ir_table.entrySet()) {
					downtime_intervals.put(new Timestamp(Long.valueOf(ir_table_entry.getKey())),
										   Long.valueOf(String.valueOf((ir_table_entry.getValue()))));
					
					last_ir_table_element = Long.valueOf(ir_table_entry.getKey());
				}
				
				start_downtime = new Timestamp(last_ir_table_element);
				
		        Robot robot = new Robot(robot_id, cluster_id, previous_down_signals,
		        						down_signals, robot_IR, start_downtime,
		        						downtime_intervals);

		        robots.put(robot_id, robot);
		    }
		} 
		finally {
		    cursor.close();
		}
		return robots;
		
	}
	
}
