package database.dao.concrete;

import java.util.ArrayList;
import applicationCore.Cluster;
import database.dao.interfaces.AreaDAOInterface;

public class AreaDAO implements AreaDAOInterface {
	
	@Override
	public ArrayList<Cluster> getClusters(Integer area_id){
		
		ArrayList<Cluster> clusters = new ArrayList<>();
		
		return clusters;
	}
	
}
