package messagesTest;

import java.util.TreeMap;

public class Area {
	
	private int area_id;
	private TreeMap<Integer, Cluster> clusters_IR;
	
	public Area(int area_id) {
		this.area_id = area_id;
		this.clusters_IR = new TreeMap<>();
	}
	
	// Getters and Setters
	
	public int getAreaId() { return this.area_id; }
	public TreeMap<Integer, Cluster> getClustersIR() { return this.clusters_IR; }
	
	public void setAreaId(int area_id) { this.area_id = area_id; } 
	public void setClustersIR(TreeMap<Integer, Cluster> clusters_IR) { this.clusters_IR = clusters_IR; }
	
	// Other methods
	
	@Override
	public String toString() {
		return ( "Area ID: " + this.area_id );
	}
}
