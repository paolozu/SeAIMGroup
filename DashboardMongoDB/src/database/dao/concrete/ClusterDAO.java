package database.dao.concrete;

import java.util.ArrayList;

import org.bson.Document;
//import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
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
		//MongoClient client = DatabaseConnector.CONNECTION.getClient();
		MongoDatabase database = DatabaseConnector.CONNECTION.getDatabase();
		MongoCollection<Document> collection = database.getCollection("cluster");
		Document cluster_db = new Document().append("_id", cluster.getClusterId())
											.append("area_id", cluster.getAreaId())
											.append("cluster_ir", cluster.getClusterIR());
		collection.insertOne(cluster_db);
	}
	
	@Override
	public void updateCluster(Cluster cluster) {
		//MongoClient client = DatabaseConnector.CONNECTION.getClient();
		MongoDatabase database = DatabaseConnector.CONNECTION.getDatabase();
		MongoCollection<Document> collection = database.getCollection("cluster");
		collection.updateOne(Filters.eq("_id", cluster.getClusterId()), Updates.set("cluster_ir", cluster.getClusterIR()));
	}
	
	@Override
	public ArrayList<Robot> getRobots(Integer cluster_id) {
		
		ArrayList<Robot> robots = new ArrayList<>();
		
		return robots;
		
	}
	
}
