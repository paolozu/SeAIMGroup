package test;

import org.bson.Document;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import database.DatabaseConnector;
import database.dao.interfaces.RobotDAOInterface;
import model.Robot;

public class RobotDAO implements RobotDAOInterface {
	
	@Override
	public void insertRobot(Robot robot) {
		MongoDatabase database = DatabaseConnector.CONNECTION.getDatabase();
		MongoCollection<Document> robots_collection = database.getCollection("robot");
		Document robot_db = new Document().append("_id", robot.getRobotId())
										  .append("cluster_id", robot.getClusterId())
										  .append("robot_ir", robot.getRobotIR())
										  .append("previous_down_signals", 0)
										  .append("down_signals", 0)
										  .append("ir_table", new Document());
		
		robots_collection.insertOne(robot_db);
	}
	
	@Override
	public void updateRobot(Robot robot) {
		MongoDatabase database = DatabaseConnector.CONNECTION.getDatabase();
		MongoCollection<Document> robots_collection = database.getCollection("robot");
		robots_collection.updateOne(Filters.eq("_id", robot.getRobotId()),
									Updates.set("robot_ir", robot.getRobotIR()));
	}	
	
	@Override
	public void updateSignals(Robot robot) {
		MongoDatabase database = DatabaseConnector.CONNECTION.getDatabase();
		MongoCollection<Document> robots_collection = database.getCollection("robot");
		//Updating previous down signals.
		robots_collection.updateOne(Filters.eq("_id", robot.getRobotId()),
									Updates.set("previous_down_signals", robot.getPreviuosDownSignals()));
		//Updating down signals.
		robots_collection.updateOne(Filters.eq("_id", robot.getRobotId()),
									Updates.set("down_signals", robot.getDownSignals()));
	}
	
	@Override
	public void addInIRTable(Robot robot, long start_downtime, long downtime_duration) {
		MongoDatabase database = DatabaseConnector.CONNECTION.getDatabase();
		MongoCollection<Document> robots_collection = database.getCollection("robot");
		robots_collection.updateOne(Filters.eq("_id", robot.getRobotId()),
									Updates.set("ir_table." + String.valueOf(start_downtime), downtime_duration));
	}
	
	@Override
	public void removeFromIRTable(Robot robot, long start_downtime) {
		MongoDatabase database = DatabaseConnector.CONNECTION.getDatabase();
		MongoCollection<Document> robots_collection = database.getCollection("robot");
		robots_collection.updateOne(Filters.eq("_id", robot.getRobotId()),
									Updates.unset("ir_table." + String.valueOf(start_downtime)));
	}
	
}
