package test;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

public class ClusterTest {

	private HashMap<Integer, RobotTest> robots;   			    // Map in which the key is robot_id and the value is robot itself.
	private int down_robots; 	              				// Counter of down robots in this cluster.
	private double cluster_IR;
	private int cluster_id;
	private int area_id;
	private Timestamp start_downtime;        				 // To keep trace when down time starts.
	private HashMap<Timestamp, Long> downtime_intervals;	 // Map in which the key is the down time starts and the value is the 
															 // down time duration.
	
	public ClusterTest(int cluster_id, int area_id){
		this.cluster_id = cluster_id;
		this.area_id = area_id;
		this.robots = new HashMap<>();
		this.downtime_intervals = new HashMap<>();
	}
	
	public ClusterTest(int cluster_id, int area_id, int down_robots, double cluster_IR,  Timestamp start_downtime,
			HashMap<Integer, RobotTest> robots, HashMap<Timestamp, Long> downtime_intervals) {
		this.robots = robots;
		this.down_robots = down_robots;
		this.cluster_IR = cluster_IR;
		this.cluster_id = cluster_id;
		this.area_id = area_id;
		this.start_downtime = start_downtime;
		this.downtime_intervals = downtime_intervals;
	}
	
	// Getters and Setters
	
	public HashMap<Integer, RobotTest> getRobots() { return robots; }
	public int getDownRobots() { return this.down_robots; }
	public double getClusterIR() { return cluster_IR; }
	public int getClusterId() { return cluster_id; }
	public int getAreaId() { return area_id; }
	public Timestamp getStartDowntime() { return start_downtime; }
	public HashMap<Timestamp, Long> getDowntimeIntervals() { return this.downtime_intervals; }

	public void setRobots(HashMap<Integer, RobotTest> robots) { this.robots = robots; }
	public void setDownRobots(int down_robots) { this.down_robots = down_robots; }
	public void setClusterIR(int cluster_IR) { this.cluster_IR = cluster_IR; }
	public void setClusterId(int cluster_id) { this.cluster_id = cluster_id; }
	public void setAreaId(int area_id) { this.area_id = area_id; }
	public void setStartDowntime(Timestamp start_downtime) { this.start_downtime = start_downtime; }
	public void setDowntimeIntervals(HashMap<Timestamp, Long> downtime_intervals) { this.downtime_intervals = downtime_intervals; }
	
	// Other methods
	
	// This function in called either to add a robot to this cluster 
	// or to update robot's values and then cluster's down robots.
	public void handleRobot(RobotTest robot){
		if ( this.robots.containsKey(robot.getRobotId()) ) {
			this.updateRobotIR(robot);
		}
		else {
			// Here we insert new robot in this cluster.
			this.robots.put(robot.getRobotId(), robot);
			if( robot.getDownSignals() == 1 )
				this.down_robots++;
		}
	}
	
	public void updateRobotIR(RobotTest robot) {
		
		switch( robot.getDownSignals() ) {

			case 0:		this.down_robots--;
						break;
						/*if( --this.down_robots == 0 )
							this.updateDownTime();
						break;*/
					
			case 1:		if( robot.getPreviuosDownSignals() != 2 ) {
							this.down_robots++;
							//this.start_downtime = new Timestamp(System.currentTimeMillis());
						}
		
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
		return ( "Cluster ID: " + this.cluster_id + "\nCluster IR: " + this.cluster_IR + "%" +
				 "\nDown robots: " + this.down_robots + "\n\n" );
	}
}
