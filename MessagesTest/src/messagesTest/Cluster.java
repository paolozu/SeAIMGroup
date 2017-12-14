package messagesTest;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class Cluster {
	public Map<Integer,Integer> downs;
	public int IR_cluster;
	public int id_cluster;
	public int id_area;
	public int downtime;
	
	public Cluster(){
		downs = new HashMap<Integer,Integer>();
	}
	public void add_robot(Robot r){
		downs.put(r.robot_id, r.down);
	}
	
	public void update_downtime(){
		Collection<Integer> appoggio = this.downs.values();
		for (int x : appoggio){
			if (x != 0){
				if (this.downtime<60)
						this.downtime+=1;
			}
			else{
				if (this.downtime>0)
					this.downtime-=1;
			}
		}
	}
	
	public void update_IR(){
		this.IR_cluster=this.downtime/60*100;
	}
}
