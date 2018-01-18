package database.dao.interfaces;

import java.util.HashMap;

import model.Cluster;

public interface AreaDAOInterface {
	
	public HashMap<Integer, Cluster> getClusters(Integer area_id);
	
}
