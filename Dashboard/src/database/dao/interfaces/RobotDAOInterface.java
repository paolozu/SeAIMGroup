package database.dao.interfaces;

import java.sql.SQLException;
import applicationCore.Robot;

public interface RobotDAOInterface {
	
	public void insertRobot(Robot robot) throws SQLException;
	public void updateRobot(Robot robot) throws SQLException;
}
