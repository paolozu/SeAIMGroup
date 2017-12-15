package messagesTest;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class Cluster {
	
	private Map<Integer,Integer> robots_IR;
	private int cluster_IR;
	private int cluster_id;
	private int area_id;
	private int downtime;
	
	public Cluster(){
		robots_IR = new HashMap<Integer,Integer>();
	}

	public Map<Integer, Integer> getRobotsIR() {
		return robots_IR;
	}

	public void setRobotsIR(Map<Integer, Integer> robots_IR) {
		this.robots_IR = robots_IR;
	}
	
	public int getClusterIR() {
		return cluster_IR;
	}

	public void setClusterIR(int cluster_IR) {
		this.cluster_IR = cluster_IR;
	}

	public int getClusterId() {
		return cluster_id;
	}

	public void setClusterId(int cluster_id) {
		this.cluster_id = cluster_id;
	}

	public int getAreaId() {
		return area_id;
	}

	public void setAreaId(int area_id) {
		this.area_id = area_id;
	}

	public int getDowntime() {
		return downtime;
	}

	public void setDowntime(int downtime) {
		this.downtime = downtime;
	}
	public void addRobot(Robot robot){
		robots_IR.put(robot.getRobotId(), robot.getDownSignals());
	}
	
	public void updateDownTime(){
		
		Collection<Integer> temp = this.robots_IR.values();
		
		for (int x : temp){
			if ( x != 0 ){
				if ( this.downtime < 60 )
					 this.downtime += 1;
			}
			else{
				if ( this.downtime > 0 )
					 this.downtime -= 1;
			}
		}
	}
	
	public void updateIR(){
		this.cluster_IR = this.downtime / 60 * 100;
	}

	public Map<Integer, Integer> getRobots_state() {
		return robots_IR;
	}
}
