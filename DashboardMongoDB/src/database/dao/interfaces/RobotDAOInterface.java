package database.dao.interfaces;

import model.Robot;

public interface RobotDAOInterface {
	
	public void insertRobot(Robot robot);
	public void updateRobot(Robot robot);
	public void updateSignals(Robot robot);
	public void addInIRTable(Robot robot, long start_downtime, long downtime_duration);
	public void removeFromIRTable(Robot robot, long start_downtime);
}
