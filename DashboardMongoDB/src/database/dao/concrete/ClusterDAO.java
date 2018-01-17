package database.dao.concrete;

import java.util.ArrayList;
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
	public void updateCluster(Cluster cluster) {
		MongoDatabase database = DatabaseConnector.CONNECTION.getDatabase();
		MongoCollection<Document> clusters_collection = database.getCollection("cluster");
		clusters_collection.updateOne(Filters.eq("_id", cluster.getClusterId()),
									  Updates.set("cluster_ir", cluster.getClusterIR()));
	}
	
	@Override
	public ArrayList<Robot> getRobots(Integer cluster_id) {
		ArrayList<Robot> robots = new ArrayList<>();
		MongoDatabase database = DatabaseConnector.CONNECTION.getDatabase();
		MongoCollection<Document> robots_collection = database.getCollection("robot");
		robots_collection.createIndex(Indexes.ascending("cluster_id"));
		MongoCursor<Document> cursor = robots_collection.find(Filters.eq("cluster_id", cluster_id)).iterator();
		try {
		    while (cursor.hasNext()) {
		        Document current = cursor.next();
		        Robot robot = new Robot(current.getInteger("_id"),
		        						current.getInteger("cluster_id"),
		        						current.getDouble("robot_ir"));
		        robots.add(robot);
		    }
		} 
		finally {
		    cursor.close();
		}
		return robots;
		
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
	
}
