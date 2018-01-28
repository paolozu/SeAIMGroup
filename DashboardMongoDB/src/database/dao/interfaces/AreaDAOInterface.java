package database.dao.interfaces;

import java.util.concurrent.ConcurrentHashMap;

import model.Cluster;

public interface AreaDAOInterface {
	
	public ConcurrentHashMap<Integer, Cluster> getClusters(Integer area_id);
	
}
