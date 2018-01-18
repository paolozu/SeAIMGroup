package test;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import org.bson.Document;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Indexes;
import database.DatabaseConnector;

public class ClusterDAO {
	
	public HashMap<Integer, RobotTest> getRobots(Integer cluster_id) {
		
		MongoDatabase database = DatabaseConnector.CONNECTION.getDatabase();
		MongoCollection<Document> robots_collection = database.getCollection("robot");
		robots_collection.createIndex(Indexes.ascending("cluster_id"));
		MongoCursor<Document> cursor = robots_collection.find(Filters.eq("cluster_id", cluster_id)).iterator();
		HashMap<Integer, RobotTest> robots = new HashMap<>();
		
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
				
				for (Map.Entry<String, Object> ir_table_entry : ir_table.entrySet()) {
					downtime_intervals.put(new Timestamp(Long.valueOf(ir_table_entry.getKey())),
										   Long.valueOf(String.valueOf((ir_table_entry.getValue()))));
					
					last_ir_table_element = Long.valueOf(ir_table_entry.getKey());
				}
				
				start_downtime = new Timestamp(last_ir_table_element);
				
		        RobotTest robot = new RobotTest(robot_id, cluster_id, previous_down_signals,
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
