package database.dao.interfaces;

import java.util.ArrayList;

import model.Cluster;
import model.Robot;

public interface ClusterDAOInterface {
	
	public void insertCluster(Cluster cluster);
	public void updateCluster(Cluster cluster);
	public ArrayList<Robot> getRobots(Integer cluster_id);
}
