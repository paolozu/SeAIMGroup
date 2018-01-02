package database.dao.concrete;

import java.util.ArrayList;
import org.bson.Document;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import applicationCore.Cluster;
import database.DatabaseConnector;
import database.dao.interfaces.AreaDAOInterface;

public class AreaDAO implements AreaDAOInterface {
	
	@Override
	public ArrayList<Cluster> getClusters(Integer area_id){
		ArrayList<Cluster> clusters = new ArrayList<>();
		MongoDatabase database = DatabaseConnector.CONNECTION.getDatabase();
		MongoCollection<Document> collection = database.getCollection("cluster");
		MongoCursor<Document> cursor = collection.find(Filters.eq("area_id", area_id)).iterator();
		try {
		    while (cursor.hasNext()) {
		    	Document current = cursor.next();
		        Cluster cluster = new Cluster(current.getInteger("_id"),
		        							  current.getInteger("area_id"), 
		        							  current.getDouble("cluster_id"));
		        clusters.add(cluster);
		    }
		} 
		finally {
		    cursor.close();
		}
		return clusters;
	}
	
}
