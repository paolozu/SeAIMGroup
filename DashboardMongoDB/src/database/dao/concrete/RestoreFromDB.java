package database.dao.concrete;

import java.util.HashMap;
import org.bson.Document;
import com.mongodb.client.DistinctIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import database.DatabaseConnector;
import database.dao.concrete.AreaDAO;
import model.Area;


public class RestoreFromDB {

	public static HashMap<Integer, Area> getAreas() {
		
		MongoDatabase database = DatabaseConnector.CONNECTION.getDatabase();
		MongoCollection<Document> clusters_collection = database.getCollection("cluster");	
		
		HashMap<Integer, Area> areas = new HashMap<>();
		
		DistinctIterable<Integer> areas_count = clusters_collection.distinct("area_id", Integer.class);
		MongoCursor<Integer> areas_iterator = areas_count.iterator();
		
		try {
			while( areas_iterator.hasNext() ) {
				int current_area_id = areas_iterator.next();
				Area current_area = new Area(current_area_id, new AreaDAO().getClusters(current_area_id));
				areas.put(current_area_id, current_area);
			}
		}
		finally {
			areas_iterator.close();
		}
		
		return areas;
		
	}
	
}
