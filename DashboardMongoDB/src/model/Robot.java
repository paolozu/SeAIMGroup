package model;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.concurrent.locks.ReentrantLock;
import database.dao.concrete.RobotDAO;

public class Robot {
	
	private int robot_id;
	private int cluster_id;
	private int previous_down_signals;								// To keep trace of down signals evolution.
	private int down_signals;										// To keep trace of down signals numbers.
	private double robot_IR;
	private Timestamp start_downtime;								// To keep trace when down time starts.
	private HashMap<Timestamp, Long> downtime_intervals;			// Map in which the key is the down time starts and the value is the 
	 																// down time duration.
	private ReentrantLock lock = new ReentrantLock(true);
	
	public Robot() {}
	
	// Using this constructor the robot 
	// is inserted into the database.
	public Robot(int robot_id, int cluster_id) {
		this.robot_id = robot_id;
		this.cluster_id = cluster_id;
		this.downtime_intervals = new HashMap<>();
		// Add robot to database.
		new RobotDAO().insertRobot(this);
	}
	
	// This constructor is called from ClusterDAO class
	// to return a collection of robots.
	// This constructor doesn't insert the robot into the database.
	public Robot(int robot_id, int cluster_id, int previous_down_signals, 
				 int down_signals, double robot_IR, Timestamp start_downtime, 
				 HashMap<Timestamp, Long> downtime_intervals) {
		
		this.robot_id = robot_id;
		this.cluster_id = cluster_id;
		this.previous_down_signals = previous_down_signals;
		this.down_signals = down_signals;
		this.robot_IR = robot_IR;
		this.start_downtime = start_downtime;
		this.downtime_intervals = downtime_intervals;
		
	}
		
	// Getters and Setters
		
	public int getRobotId() { return this.robot_id; }
	public int getClusterId() { return this.cluster_id; }
	public int getDownSignals() { return this.down_signals; }
	public int getPreviuosDownSignals() { return this.previous_down_signals; }
	public Timestamp getStartDowntime() { return this.start_downtime; }
	public double getRobotIR() { return this.robot_IR; }
	public HashMap<Timestamp, Long> getDowntimeIntervals() { return this.downtime_intervals; }
	
	public void setRobotId(int robot_id) { this.robot_id = robot_id; }
	public void setClusterId(int cluster_id) { this.cluster_id = cluster_id; }
	public void setDownSignals(int signal_state) { this.down_signals = signal_state; }
	public void setPreviousDownSignal(int previous_down_signals) { this.previous_down_signals = previous_down_signals; }
	public void setStartDowntime(Timestamp start_downtime) { this.start_downtime = start_downtime; }
	public void setRobotIR(int robot_IR) { this.robot_IR = robot_IR; }
	public void setDowntimeIntervals(HashMap<Timestamp, Long> downtime_intervals) { this.downtime_intervals = downtime_intervals; }
	
	// Other methods
	
	// Function to update robots according to the received message.
	// In case the number of down signals became 0 we store 
	// the down time beginning and its duration in downtime_intervals map.
	// In case the number of down signals passes from 0 to 1 the we store
	// the current time to keep trace of down time beginning.
	public void signalCatch(int signal, long message_time) {
		this.lock.lock();
		try {
			if ( signal == 0 ){ 
				this.previous_down_signals = this.down_signals;
				if( ++this.down_signals == 1 ) {
					this.start_downtime = new Timestamp(message_time);
					new RobotDAO().addInIRTable(this, message_time, 0);
				}
				new RobotDAO().updateSignals(this);
			}
			else { // signal == 1
				this.previous_down_signals = this.down_signals;
				if( --down_signals == 0 ) {
					long downtime_duration = message_time - start_downtime.getTime();
					this.downtime_intervals.put(start_downtime, downtime_duration);
					new RobotDAO().addInIRTable(this, start_downtime.getTime(), downtime_duration);
				}
				new RobotDAO().updateSignals(this);
			}	
		}
		finally {
			this.lock.unlock();
		}
	}

	public void updateIR() {
		this.lock.lock();
		try {
			double current_IR = 0;
			long downtime_last_hour = 0;
			ArrayList<Timestamp> more_than_an_hour_ago = new ArrayList<>();
				
			for( Map.Entry<Timestamp, Long> interval : downtime_intervals.entrySet() ) {
				
				long time_to_downtime_init = new Timestamp(System.currentTimeMillis()).getTime() - interval.getKey().getTime();
				
				// 3.6e6 milliseconds --> 1 hour.
				if( time_to_downtime_init > 3.6e6 ) {
					if( time_to_downtime_init - interval.getValue() > 3.6e6  ) {
						if( interval.getValue() > 0 )
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
			
			if( this.down_signals > 0 ) {
				if( downtime_last_hour +  new Timestamp(System.currentTimeMillis()).getTime() - start_downtime.getTime() > 3.6e6 )
					downtime_last_hour = 3600000;
				else
					downtime_last_hour += new Timestamp(System.currentTimeMillis()).getTime() - start_downtime.getTime();
			}
			
			for( Timestamp invalid : more_than_an_hour_ago ) {
				this.downtime_intervals.remove(invalid);
				new RobotDAO().removeFromIRTable(this, invalid.getTime());
			}
			
			// total_downtime --> milliseconds
			// * 1.6667e-5    --> minutes
			// divide by 60   --> IR not in percentage
			// divide by 60   --> IR in percentage
			// Round, * 100d and than / 100d to have 2 decimal places.
	 		current_IR = (double) Math.round((((downtime_last_hour * 1.6666667e-5) / 60) * 100) * 100d) / 100d;
	 		
	 		// Update database.
	 		if( this.robot_IR != current_IR ) {
	 			this.robot_IR = current_IR;
	 			new RobotDAO().updateRobotIR(this);
	 		}
		}
		finally {
			this.lock.unlock();
		}
	}
	
	@Override
	public String toString() {
		return( "Robot ID: " + this.robot_id + "\nCluster ID: " + this.cluster_id +
				"\nprevious down signals: " + this.previous_down_signals + "\nDown signals: " + this.down_signals +
				"\nRobot IR: " + this.robot_IR + "%\n\n" );
	}
	
}