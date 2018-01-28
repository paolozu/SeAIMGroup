package model;

import java.util.concurrent.ConcurrentHashMap;

public class Area {
	
	private int area_id;
	private ConcurrentHashMap<Integer, Cluster> clusters;	// Map in which the key is cluster_id and the value is the cluster itself.
	
	public Area() {}
	
	public Area(int area_id) {
		this.area_id = area_id;
		this.clusters = new ConcurrentHashMap<>();
	}
	
	public Area(int area_id, ConcurrentHashMap<Integer, Cluster> clusters) {
		this.area_id = area_id;
		this.clusters = clusters;
	}
	
	// Getters and Setters
	
	public int getAreaId() { return this.area_id; }
	public ConcurrentHashMap<Integer, Cluster> getClusters() { return this.clusters; }
	
	public void setAreaId(int area_id) { this.area_id = area_id; } 
	public void setClusters(ConcurrentHashMap<Integer, Cluster> clusters) { this.clusters = clusters; }
	
	// Other methods
	
	public void addCluster(Cluster cluster) {
		if ( ! this.clusters.containsKey(cluster.getClusterId()) ) 
			this.clusters.put(cluster.getClusterId(), cluster);		
	}
	
	@Override
	public String toString() {
		return ( "Area ID: " + this.area_id + "\n\n" );
	}
}
