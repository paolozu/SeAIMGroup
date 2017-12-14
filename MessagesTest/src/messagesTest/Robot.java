package messagesTest;


public class Robot {
	public int robot_id;
	public int cluster_id;
	public int down;
	public int downtime;
	public int IR;
	
	public Robot() {}
	
	public Robot(int id,int cluster){
		this.robot_id=id;
		this.cluster_id=cluster;
		this.down=0;
		this.downtime=0;
		this.IR=0;
	}
	
	public void signal_catch(int signal) {
		if ( signal != 0 ){ 
			if (down>0)
				this.down-=1;
		}
		else{
			if (down<7)
				this.down+=1;
			}
	}
	
	public void update_DownTime() {
		if (this.down > 0)
			if (this.downtime<60)
				this.downtime+=1;
		else
			if (this.downtime>0)
				this.downtime-=1;
	}

	public void update_IR(){
		this.IR=downtime/60*100;
	}
	
	
	public String toString(){
		return(this.robot_id +" "+this.cluster_id);
	}
}