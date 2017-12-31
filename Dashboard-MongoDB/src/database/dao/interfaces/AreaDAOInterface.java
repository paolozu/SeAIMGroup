package database.dao.interfaces;

import java.util.ArrayList;
import applicationCore.Cluster;

public interface AreaDAOInterface {
	
	public ArrayList<Cluster> getClusters(Integer area_id);
}
