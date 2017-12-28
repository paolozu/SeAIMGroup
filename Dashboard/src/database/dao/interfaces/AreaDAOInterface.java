package database.dao.interfaces;

import java.util.ArrayList;
import applicationCore.Area;
import applicationCore.Cluster;

public interface AreaDAOInterface {
	
	public ArrayList<Cluster> getClusters(Area area);
}
