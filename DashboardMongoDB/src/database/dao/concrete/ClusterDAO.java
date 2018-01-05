package database.dao.concrete;

import java.util.ArrayList;

import org.bson.Document;

import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import applicationCore.Cluster;
import applicationCore.Robot;
import database.DatabaseConnector;
import database.dao.interfaces.ClusterDAOInterface;

public class ClusterDAO implements ClusterDAOInterface {

	@Override
	public void insertCluster(Cluster cluster) {
		MongoDatabase database = DatabaseConnector.CONNECTION.getDatabase();
		MongoCollection<Document> collection = database.getCollection("cluster");
		Document cluster_db = new Document().append("_id", cluster.getClusterId())
											.append("area_id", cluster.getAreaId())
											.append("cluster_ir", cluster.getClusterIR());
		collection.insertOne(cluster_db);
	}
	
	@Override
	public void updateCluster(Cluster cluster) {
		MongoDatabase database = DatabaseConnector.CONNECTION.getDatabase();
		MongoCollection<Document> collection = database.getCollection("cluster");
		collection.updateOne(Filters.eq("_id", cluster.getClusterId()), Updates.set("cluster_ir", cluster.getClusterIR()));
	}
	
	@Override
	public ArrayList<Robot> getRobots(Integer cluster_id) {
		ArrayList<Robot> robots = new ArrayList<>();
		MongoDatabase database = DatabaseConnector.CONNECTION.getDatabase();
		MongoCollection<Document> collection = database.getCollection("robot");
		BasicDBObject index = new BasicDBObject("cluster_id", 1);
		collection.createIndex(index);
		MongoCursor<Document> cursor = collection.find(Filters.eq("cluster_id", cluster_id)).iterator();
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
	
}
