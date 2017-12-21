package messagesTest;

import java.util.HashMap;

public class Area {
	
	private int area_id;
	private HashMap<Integer, Cluster> clusters_IR;
	
	public Area(int area_id) {
		this.area_id = area_id;
		this.clusters_IR = new HashMap<>();
	}
	
	// Getters and Setters
	
	public int getAreaId() { return this.area_id; }
	public HashMap<Integer, Cluster> getClustersIR() { return this.clusters_IR; }
	
	public void setAreaId(int area_id) { this.area_id = area_id; } 
	public void setClustersIR(HashMap<Integer, Cluster> clusters_IR) { this.clusters_IR = clusters_IR; }
	
	// Other methods
	
	public void addCluster(Cluster cluster) {
		if ( ! this.clusters_IR.containsKey(cluster.getClusterId()) ) 
			this.clusters_IR.put(cluster.getClusterId(), cluster);		
	}
	
	@Override
	public String toString() {
		return ( "Area ID: " + this.area_id );
	}
}
