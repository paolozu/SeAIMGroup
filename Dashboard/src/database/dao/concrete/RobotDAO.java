package database.dao.concrete;

import java.sql.SQLException;

import java.sql.Connection;
import java.sql.PreparedStatement;

import applicationCore.Robot;
import database.DatabaseConnector;
import database.dao.interfaces.RobotDAOInterface;

public class RobotDAO implements RobotDAOInterface {
	
	private static final String
	INSERT = "INSERT INTO robot(robot_id, cluster_id, robot_ir) VALUES (?, ?, ?);";
	
	private static final String
	UPDATE = "UPDATE robot SET robot.robot_ir = ? WHERE robot_id = ?;";
	
	
	public void insertRobot(Robot robot) {
		try {
			Connection connection = DatabaseConnector.getInstance().getConnection();
		    PreparedStatement ps = connection.prepareStatement(INSERT);
		    ps.setInt(1, robot.getRobotId());
		    ps.setInt(2, robot.getClusterId());
		    ps.setDouble(3, robot.getRobotIR());
		    ps.executeUpdate();
		    ps.close();
		    connection.close();
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void updateRobot(Robot robot) {
		try {
			Connection connection = DatabaseConnector.getInstance().getConnection();
		    PreparedStatement ps = connection.prepareStatement(UPDATE);
		    ps.setDouble(1, robot.getRobotIR());
		    ps.setInt(2, robot.getRobotId());
		    ps.executeUpdate();
		    ps.close();
		    connection.close();
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
	}
	
}
