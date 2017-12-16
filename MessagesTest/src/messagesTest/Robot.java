package messagesTest;

import java.sql.Timestamp;

public class Robot {
	
	private int robot_id;
	private int cluster_id;
	private int previous_down_signals;		// To keep trace of down signals evolution.
	private int down_signals;				// To keep trace of down signals numbers.
	private double robot_IR;
	private Timestamp start_downtime;		// To keep trace when down time starts.
	private long total_downtime;			// Total down time in milliseconds.
	
	public Robot() {}
	
	public Robot(int robot_id, int cluster_id) {
		this.robot_id = robot_id;
		this.cluster_id = cluster_id;
	}
		
	// Getters and Setters
		
	public int getRobotId() { return robot_id; }
	public int getClusterId() { return cluster_id; }
	public int getDownSignals() { return down_signals; }
	public int getPreviuosDownSignals() { return this.previous_down_signals; }
	public long getDowntime() { return total_downtime; }
	public Timestamp getStartDowntime() { return start_downtime; }
	public double getRobotIR() { return robot_IR; }
	
	public void setRobotId(int robot_id) { this.robot_id = robot_id; }
	public void setClusterId(int cluster_id) { this.cluster_id = cluster_id; }
	public void setDownSignals(int signal_state) { this.down_signals = signal_state; }
	public void setPreviousDownSignal(int previous_down_signals) { this.previous_down_signals = previous_down_signals; }
	public void setDowntime(long total_downtime) { this.total_downtime = total_downtime; }
	public void setStartDowntime(Timestamp start_downtime) { this.start_downtime = start_downtime; }
	public void setRobotIR(int robot_IR) { this.robot_IR = robot_IR; }
	
	// Other methods
	
	// Function to update robots according to the received message.
	// In case the number of down signals became 0 we update total down time.
	// In case the number of down signals passes from 0 to 1 the we store
	// the current time to keep trace of down time beginning.
	public void signalCatch(int signal) {
		if ( signal == 1 ){ 
			this.previous_down_signals = this.down_signals;
			if( --down_signals == 0 )
				this.updateDownTime();
		}
		else { // signal == 0 
			this.previous_down_signals = this.down_signals;
			if( ++this.down_signals == 1 ) 
				this.start_downtime = new Timestamp(System.currentTimeMillis());
		}	
	}
	
	public void updateDownTime() {
		this.total_downtime += new Timestamp(System.currentTimeMillis()).getTime() - start_downtime.getTime();
		this.updateIR();
	}

	public void updateIR() {
		// total_downtime --> milliseconds
		// * 1.6667e-5    --> minutes
		// divide to 60   --> IR not in percentage
		// divide to 60   --> IR in percentage
 		this.robot_IR = (double)((total_downtime * 1.6667e-5) / 60) * 100;
	}
	
	// Function to force IR update in case we need current IR and the down_signals is greater than 0.
	// We need this function because otherwise we update total_downtime only when down_signals counter
	// returns to be 0.
	public double forceUpdateIR(Timestamp time) {
		if ( down_signals > 0 ) {
			this.updateDownTime();
			this.start_downtime = new Timestamp(System.currentTimeMillis());
		}
		return this.robot_IR;
	}
	
	public String toString() {
		return( this.robot_id + " " + this.cluster_id );
	}
}