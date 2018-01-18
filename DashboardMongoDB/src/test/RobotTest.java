package test;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

public class RobotTest {

	private int robot_id;
	private int cluster_id;
	private int previous_down_signals;						// To keep trace of down signals evolution.
	private int down_signals;								// To keep trace of down signals numbers.
	private double robot_IR;
	private Timestamp start_downtime;						// To keep trace when down time starts.
	private HashMap<Timestamp, Long> downtime_intervals;	// Map in which the key is the down time starts and the value is the 
	 														// down time duration.
	
	public RobotTest() {}
	
	public RobotTest(int robot_id, int cluster_id) {
		this.robot_id = robot_id;
		this.cluster_id = cluster_id;
		this.downtime_intervals = new HashMap<>();
	}
	
	public RobotTest(int robot_id, int cluster_id, int previous_down_signals, int down_signals, double robot_IR,
			Timestamp start_downtime, HashMap<Timestamp, Long> downtime_intervals) {
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
	public void signalCatch(int signal) {
		if ( signal == 0 ){ 
			this.previous_down_signals = this.down_signals;
			this.down_signals++;
			//if( ++this.down_signals == 1 ) 
				//this.start_downtime = new Timestamp(System.currentTimeMillis());	
		}
		else { // signal == 1
			this.previous_down_signals = this.down_signals;
			this.down_signals--;
			//if( --down_signals == 0 )
				//this.updateDownTime();
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
 		this.robot_IR = (double)((downtime_last_hour * 1.6667e-5) / 60) * 100;
	}
	
	// Function to force IR update in case we need current IR and the down_signals is greater than 0.
	// We need this function because otherwise we update total_downtime only when down_signals counter
	// returns to be 0.
	public double forceUpdateIR() {
		if ( down_signals > 0 ) {
			this.updateDownTime();
			this.start_downtime = new Timestamp(System.currentTimeMillis());
		}
		return this.robot_IR;
	}
	
	@Override
	public String toString() {
		return( "Robot ID: " + this.robot_id + "\nCluster ID: " + this.cluster_id +
				"\nDown signals: " + this.down_signals + "\nRobot IR: " + this.robot_IR + "%\n\n" );
	}
}