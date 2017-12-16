package messagesTest;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

public class Cluster {
	
	private Map<Integer, Robot> robots_IR;   // Map in which the key is robot_id and the value is robot_IR.
	private int down_robots; 	              // Counter of down robots in this cluster.
	private double cluster_IR;
	private int cluster_id;
	private int area_id;
	private Timestamp start_downtime;         // To keep trace when down time starts.
	private long total_downtime;			  // Total down time in milliseconds.
	
	public Cluster(){
		robots_IR = new HashMap<Integer, Robot>(); 
	}
	
	// Getters and Setters
	
	public Map<Integer, Robot> getRobotsIR() { return robots_IR; }
	public int getDownRobots() { return this.down_robots; }
	public double getClusterIR() { return cluster_IR; }
	public int getClusterid() { return cluster_id; }
	public int getAreaId() { return area_id; }
	public long getDowntime() { return total_downtime; }
	public Timestamp getStartDowntime() { return start_downtime; }

	public void setRobotsIR(Map<Integer, Robot> robots_IR) { this.robots_IR = robots_IR; }
	public void setDownRobots(int down_robots) { this.down_robots = down_robots; }
	public void setClusterIR(int cluster_IR) { this.cluster_IR = cluster_IR; }
	public void setClusterId(int cluster_id) { this.cluster_id = cluster_id; }
	public void setAreaId(int area_id) { this.area_id = area_id; }
	public void setDowntime(long total_downtime) { this.total_downtime = total_downtime; }
	public void setStartDowntime(Timestamp start_downtime) { this.start_downtime = start_downtime; }
	
	// Other methods
	
	public void addRobot(Robot robot){
		if ( this.robots_IR.containsKey(robot.getRobotId()) ) {
			this.updateRobotIR(robot);
		}
		else {
			// Here we insert new robot in this cluster.
			// Here the new robot is no still present
			// so the first message of the new robot needs to be a down signal
			// so we increase the down_robots counter for this cluster.
			this.robots_IR.put(robot.getRobotId(), robot);
			this.down_robots++;
		}
	}
	
	public void updateRobotIR(Robot robot) {
		
		switch( robot.getDownSignals() ) {

			case 0:		this.robots_IR.put(robot.getRobotId(), robot);
						if( --this.down_robots == 0 )
							this.updateDownTime();
						break;
					
			case 1:		if( robot.getPreviuosDownSignals() != 2 ) {
							this.down_robots++;
							this.robots_IR.put(robot.getRobotId(), robot);
							this.start_downtime = new Timestamp(System.currentTimeMillis());
							break;
						}
					
			default:	this.robots_IR.put(robot.getRobotId(), robot);
		
		}
	}
	
	public void updateDownTime(){
		this.total_downtime += new Timestamp(System.currentTimeMillis()).getTime() - start_downtime.getTime();
		this.updateIR();
	}
	
	public void updateIR(){
		// total_downtime --> milliseconds
		// * 1.6667e-5    --> minutes
		// divide to 60   --> IR not in percentage
		// divide to 60   --> IR in percentage
		this.cluster_IR = (double)((total_downtime * 1.6667e-5) / 60) * 100;
	}
	
	public double forceUpdateIR() {
		return 2.0;
	}
}
