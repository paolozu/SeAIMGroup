package database.dao.concrete;

import org.bson.Document;
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
		MongoDatabase database = DatabaseConnector.CONNECTION.getDatabase();
		MongoCollection<Document> robots_collection = database.getCollection("robot");
		Document robot_db = new Document().append("_id", robot.getRobotId())
										  .append("cluster_id", robot.getClusterId())
										  .append("robot_ir", robot.getRobotIR());
		
		robots_collection.insertOne(robot_db);
	}
	
	@Override
	public void updateRobot(Robot robot) {
		MongoDatabase database = DatabaseConnector.CONNECTION.getDatabase();
		MongoCollection<Document> robots_collection = database.getCollection("robot");
		robots_collection.updateOne(Filters.eq("_id", robot.getRobotId()), Updates.set("robot_ir", robot.getRobotIR()));
	}
	
}
