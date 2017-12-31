package database;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;

public enum DatabaseConnector {
	
    CONNECTION;
	
    private MongoClient client = null;
    private MongoDatabase database = null;

    private DatabaseConnector() {
        try {
            client = new MongoClient("localhost" , 27017);
            database = client.getDatabase("ir_viewer");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public MongoDatabase getDatabase() {
        if( client == null || database == null )
            throw new RuntimeException();
        return database;
    }
}
