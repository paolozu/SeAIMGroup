package model;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;
import database.dao.concrete.ClusterDAO;
import java.util.HashMap;

public class Cluster {
	
	private int cluster_id;
	private int area_id;
	private int down_robots; 	              				 // Counter of down robots in this cluster.
	private double cluster_IR;
	private Timestamp start_downtime;        				 // To keep trace when down time starts.
	private HashMap<Integer, Robot> robots;   			     // Map in which the key is robot_id and the value is robot itself.
	private HashMap<Timestamp, Long> downtime_intervals;	 // Map in which the key is the down time starts and the value is the 
															 // down time duration.
	private ReentrantLock lock = new ReentrantLock(true);
	
	public Cluster() {}
	
	// Using this constructor the cluster 
	// is inserted into the database.
	public Cluster(int cluster_id, int area_id){
		this.cluster_id = cluster_id;
		this.area_id = area_id;
		this.robots = new HashMap<>();
		this.downtime_intervals = new HashMap<>();
		// Add cluster to database.
		new ClusterDAO().insertCluster(this);
	}
	
	// This constructor is called from AreaDAO class
	// to return a collection of clusters.
	// This constructor doesn't insert the cluster into the database.
	public Cluster(int cluster_id, int area_id, int down_robots,
				   double cluster_IR, Timestamp start_downtime, HashMap<Integer, Robot> robots,
				   HashMap<Timestamp, Long> downtime_intervals) {
		
		this.cluster_id = cluster_id;
		this.area_id = area_id;
		this.down_robots = down_robots;
		this.cluster_IR = cluster_IR;
		this.start_downtime = start_downtime;
		this.robots = robots;
		this.downtime_intervals = downtime_intervals;
		
	}
	
	// Getters and Setters
	
	public HashMap<Integer, Robot> getRobots() { return robots; }
	public int getDownRobots() { return this.down_robots; }
	public double getClusterIR() { return cluster_IR; }
	public int getClusterId() { return cluster_id; }
	public int getAreaId() { return area_id; }
	public Timestamp getStartDowntime() { return start_downtime; }
	public HashMap<Timestamp, Long> getDowntimeIntervals() { return this.downtime_intervals; }

	public void setRobots(HashMap<Integer, Robot> robots) { this.robots = robots; }
	public void setDownRobots(int down_robots) { this.down_robots = down_robots; }
	public void setClusterIR(int cluster_IR) { this.cluster_IR = cluster_IR; }
	public void setClusterId(int cluster_id) { this.cluster_id = cluster_id; }
	public void setAreaId(int area_id) { this.area_id = area_id; }
	public void setStartDowntime(Timestamp start_downtime) { this.start_downtime = start_downtime; }
	public void setDowntimeIntervals(HashMap<Timestamp, Long> downtime_intervals) { this.downtime_intervals = downtime_intervals; }
	
	// Other methods
	
	// This function in called either to add a robot to this cluster 
	// or to update robot's values and then cluster's down robots.
	public void handleRobot(Robot robot, long message_time){
		
		if ( this.robots.containsKey(robot.getRobotId()) ) {
			this.updateRobot(robot, message_time);
		}
		else {
			// Here we insert new robot in this cluster.
			this.robots.put(robot.getRobotId(), robot);
			if( robot.getDownSignals() > 0 ) {
				this.lock.lock();
				try {
					if( ++down_robots == 1 ) {
						this.start_downtime = new Timestamp(message_time);
						new ClusterDAO().addInIRTable(this, message_time, 0);
					}
					new ClusterDAO().updateDownRobots(this);
				}
				finally {
					this.lock.unlock();
				}
			}
		}
	}
	
	private void updateRobot(Robot robot, long message_time) {

		this.lock.lock();
		try {
			switch( robot.getDownSignals() ) {

			case 0:		if( --this.down_robots == 0 ) {
							long downtime_duration = message_time - start_downtime.getTime();
							this.downtime_intervals.put(start_downtime, downtime_duration);
							new ClusterDAO().addInIRTable(this, start_downtime.getTime(), downtime_duration);
						}
						new ClusterDAO().updateDownRobots(this);
						break;
					
			case 1:		if( robot.getPreviuosDownSignals() == 0 )
							if( ++this.down_robots == 1 ) {
								this.start_downtime = new Timestamp(message_time);
								new ClusterDAO().addInIRTable(this, message_time, 0);
							}
						new ClusterDAO().updateDownRobots(this);
	
			}
		}
		finally {
			this.lock.unlock();
		}		
	}
	
	public void updateDownTime() {
		this.lock.lock();
		try {
			if ( down_robots > 0 ) {	
				long downtime_duration = new Timestamp(System.currentTimeMillis()).getTime() - start_downtime.getTime();
				this.downtime_intervals.put(start_downtime, downtime_duration);
				new ClusterDAO().addInIRTable(this, start_downtime.getTime(), downtime_duration);
				this.start_downtime = new Timestamp(start_downtime.getTime() + downtime_duration);
			}
			this.updateIR();
		}
		finally {
			this.lock.unlock();
		}
	}

	private void updateIR() {
		
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
			new ClusterDAO().removeFromIRTable(this, invalid.getTime());
		}
		// total_downtime --> milliseconds
		// * 1.6667e-5    --> minutes
		// divide by 60   --> IR not in percentage
		// divide by 60   --> IR in percentage
		// Round, * 100d and than / 100d to have 2 decimal places.
 		this.cluster_IR = (double) Math.round((((downtime_last_hour * 1.6667e-5) / 60) * 100) * 100d) / 100d;
 		
 		// Update database.
 		new ClusterDAO().updateCluster(this);
	}
		
	@Override
	public String toString() {
		return ( "Cluster ID: " + this.cluster_id + "\nCluster IR: " + this.cluster_IR + "%" +
				 "\nDown robots: " + this.down_robots + "\n\n" );
	}
}
