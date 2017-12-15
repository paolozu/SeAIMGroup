package messagesTest;

import java.sql.Timestamp;

public class Robot {
	
	private int robot_id;
	private int cluster_id;
	private int down_signals;
	private double robot_IR;
	private Timestamp start_downtime;
	private long total_downtime;
	
	public Robot() {}
	
	public Robot(int robot_id, int cluster_id) {
		this.robot_id = robot_id;
		this.cluster_id = cluster_id;
	}
	
	// Getters and Setters
	
	public int getRobotId() { return robot_id; }
	public int getClusterId() { return cluster_id; }
	public int getDownSignals() { return down_signals; }
	public long getDowntime() { return total_downtime; }
	public Timestamp getStart_downtime() { return start_downtime; }
	public double getRobotIR() { return robot_IR; }
	
	public void setRobotId(int robot_id) { this.robot_id = robot_id; }
	public void setClusterId(int cluster_id) { this.cluster_id = cluster_id; }
	public void setDownSignals(int signal_state) { this.down_signals = signal_state; }
	public void setDowntime(long total_downtime) { this.total_downtime = total_downtime; }
	public void setStart_downtime(Timestamp start_downtime) { this.start_downtime = start_downtime; }
	public void setRobotIR(int robot_IR) { this.robot_IR = robot_IR; }
	
	// Other methods
	
	public void signalCatch(int signal) {
		if ( signal == 1 ){ 
			if ( down_signals > 0 ) {
				this.down_signals -= 1;
				if( down_signals == 0 )
					this.updateDownTime(this.start_downtime);
			}
		}
		else { // signal == 0 
			this.down_signals += 1;
			if( this.down_signals == 1 ) { 
				this.start_downtime = new Timestamp(System.currentTimeMillis());
			}
		}	
	}
	
	public void updateDownTime(Timestamp time) {
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
	
	
	public String toString() {
		return( this.robot_id + " " + this.cluster_id );
	}
}