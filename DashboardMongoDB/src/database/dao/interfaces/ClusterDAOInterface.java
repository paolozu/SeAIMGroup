package database.dao.interfaces;

import java.util.concurrent.ConcurrentHashMap;

import model.Cluster;
import model.Robot;

public interface ClusterDAOInterface {
	
	public void insertCluster(Cluster cluster);
	public void updateClusterIR(Cluster cluster);
	public void updateDownRobots(Cluster cluster);
	public void addInIRTable(Cluster cluster, long start_downtime, long downtime_duration);
	public void removeFromIRTable(Cluster cluster, long start_downtime);
	public ConcurrentHashMap<Integer, Robot> getRobots(Integer cluster_id);
}
