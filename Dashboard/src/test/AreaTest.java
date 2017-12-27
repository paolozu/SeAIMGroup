package test;

import java.util.HashMap;

public class AreaTest {
	
	private int area_id;
	private HashMap<Integer, ClusterTest> clusters;			// Map in which the key is cluster_id and the value is the cluster itself.
	
	public AreaTest(int area_id) {
		this.area_id = area_id;
		this.clusters = new HashMap<>();
	}
	
	// Getters and Setters
	
	public int getAreaId() { return this.area_id; }
	public HashMap<Integer, ClusterTest> getClusters() { return this.clusters; }
	
	public void setAreaId(int area_id) { this.area_id = area_id; } 
	public void setClusters(HashMap<Integer, ClusterTest> clusters) { this.clusters = clusters; }
	
	// Other methods
	
	public void addCluster(ClusterTest cluster) {
		if ( ! this.clusters.containsKey(cluster.getClusterId()) ) 
			this.clusters.put(cluster.getClusterId(), cluster);		
	}
	
	@Override
	public String toString() {
		return ( "Area ID: " + this.area_id + "\n\n" );
	}
}
