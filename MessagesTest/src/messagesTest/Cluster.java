package messagesTest;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

public class Cluster {
	
	private TreeMap<Integer, Robot> robots_IR;   			// Map in which the key is robot_id and the value is robot_IR.
	private int down_robots; 	              				// Counter of down robots in this cluster.
	private double cluster_IR;
	private int cluster_id;
	private int area_id;
	private Timestamp start_downtime;        				 // To keep trace when down time starts.
	private TreeMap<Timestamp, Long> downtime_intervals;
	
	public Cluster(int cluster_id, int area_id){
		this.cluster_id = cluster_id;
		this.area_id = area_id;
		this.robots_IR = new TreeMap<>();
		this.downtime_intervals = new TreeMap<>();
	}
	
	// Getters and Setters
	
	public TreeMap<Integer, Robot> getRobotsIR() { return robots_IR; }
	public int getDownRobots() { return this.down_robots; }
	public double getClusterIR() { return cluster_IR; }
	public int getClusterid() { return cluster_id; }
	public int getAreaId() { return area_id; }
	public Timestamp getStartDowntime() { return start_downtime; }
	public TreeMap<Timestamp, Long> getDowntimeIntervals() { return this.downtime_intervals; }

	public void setRobotsIR(TreeMap<Integer, Robot> robots_IR) { this.robots_IR = robots_IR; }
	public void setDownRobots(int down_robots) { this.down_robots = down_robots; }
	public void setClusterIR(int cluster_IR) { this.cluster_IR = cluster_IR; }
	public void setClusterId(int cluster_id) { this.cluster_id = cluster_id; }
	public void setAreaId(int area_id) { this.area_id = area_id; }
	public void setStartDowntime(Timestamp start_downtime) { this.start_downtime = start_downtime; }
	public void setDowntimeIntervals(TreeMap<Timestamp, Long> downtime_intervals) { this.downtime_intervals = downtime_intervals; }
	
	// Other methods
	
	public void addUpRobot(Robot robot) {
		if ( this.robots_IR.containsKey(robot.getRobotId()) ) {
			this.updateRobotIR(robot);
		}
	}
	
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
	
	public void updateDownTime() {
		long downtime_duration = new Timestamp(System.currentTimeMillis()).getTime() - start_downtime.getTime();
		this.downtime_intervals.put(start_downtime, downtime_duration);
		this.updateIR();
	}

	public void updateIR() {
		
		long downtime_last_hour = 0;
		ArrayList<Timestamp> more_than_an_hour_ago = new ArrayList<>();
			
		for( Map.Entry<Timestamp, Long> interval : downtime_intervals.entrySet() ) {
			
			long time_to_downtime_init = new Timestamp(System.currentTimeMillis()).getTime() - interval.getKey().getTime();
			
			// 3.6e6 milliseconds --> 1 hour.
			if( time_to_downtime_init > 3.6e6 ) {
				if( time_to_downtime_init - interval.getValue() > 3.6e6  ) {
					more_than_an_hour_ago.add(interval.getKey());			
				}
				else {
					downtime_last_hour += (3.6e6 - time_to_downtime_init + interval.getValue());
				}
			}
			else {
				downtime_last_hour += interval.getValue();
			}
			
		}
		
		for( Timestamp invalid : more_than_an_hour_ago ) {
			this.downtime_intervals.remove(invalid);
		}
		// total_downtime --> milliseconds
		// * 1.6667e-5    --> minutes
		// divide to 60   --> IR not in percentage
		// divide to 60   --> IR in percentage
 		this.cluster_IR = (double)((downtime_last_hour * 1.6667e-5) / 60) * 100;
	}
	
	// Function to force IR update in case we need current IR and the down_robots is greater than 0.
	// We need this function because otherwise we update total_downtime only when down_signals counter
	// returns to be 0.
	public double forceUpdateIR() {
		if ( down_robots > 0 ) {
			this.updateDownTime();
			this.start_downtime = new Timestamp(System.currentTimeMillis());
		}
		return this.cluster_IR;
	}
		
	@Override
	public String toString() {
		return ( "Cluster ID: " + this.cluster_id + "\nCluster IR: " + this.cluster_IR + "%" );
	}
}
