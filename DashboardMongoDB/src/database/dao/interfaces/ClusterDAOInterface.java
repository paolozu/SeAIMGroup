package database.dao.interfaces;

import java.util.ArrayList;

import model.Cluster;
import model.Robot;

public interface ClusterDAOInterface {
	
	public void insertCluster(Cluster cluster);
	public void updateCluster(Cluster cluster);
	public ArrayList<Robot> getRobots(Integer cluster_id);
	public void updateDownRobots(Cluster cluster);
	public void addInIRTable(Cluster cluster, long start_downtime, long downtime_duration);
	public void removeFromIRTable(Cluster cluster, long start_downtime);
}
