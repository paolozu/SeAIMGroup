package database.dao.concrete;

import org.bson.Document;
//import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import applicationCore.Robot;
import database.DatabaseConnector;
import database.dao.interfaces.RobotDAOInterface;

public class RobotDAO implements RobotDAOInterface {
	
	@Override
	public void insertRobot(Robot robot) {
		//MongoClient client = DatabaseConnector.CONNECTION.getClient();
		MongoDatabase database = DatabaseConnector.CONNECTION.getDatabase();
		MongoCollection<Document> collection = database.getCollection("robot");
		Document robot_db = new Document().append("robot_id", robot.getRobotId())
										  .append("cluster_id", robot.getClusterId())
										  .append("robot_ir", robot.getRobotIR());
		collection.insertOne(robot_db);
	}
	
	@Override
	public void updateRobot(Robot robot) {
		//MongoClient client = DatabaseConnector.CONNECTION.getClient();
		MongoDatabase database = DatabaseConnector.CONNECTION.getDatabase();
		MongoCollection<Document> collection = database.getCollection("robot");
		collection.updateOne(Filters.eq("robot_id", robot.getRobotId()), Updates.set("robot_ir", robot.getRobotIR()));
	}
	
}
