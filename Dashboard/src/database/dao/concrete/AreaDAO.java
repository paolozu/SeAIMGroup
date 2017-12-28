package database.dao.concrete;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import applicationCore.Area;
import applicationCore.Cluster;
import database.DatabaseConnector;
import database.dao.interfaces.AreaDAOInterface;

public class AreaDAO implements AreaDAOInterface {
	
	private static final String
	CLUSTERS = "SELECT * FROM cluster WHERE area_id = ?;";
	
	@Override
	public ArrayList<Cluster> getClusters(Area area){
		
	    ArrayList<Cluster> clusters = new ArrayList<>();
	    
	    try {
		    Connection connection = DatabaseConnector.getInstance().getConnection();
		    PreparedStatement ps = connection.prepareStatement(CLUSTERS);
		    ps.setInt(1, area.getAreaId());
		    ResultSet rset = ps.executeQuery();
		    while (rset.next()){
		      Cluster cluster = new Cluster(rset.getInt(1), rset.getInt(2), rset.getDouble(3));
		      clusters.add(cluster);
		    }
		    ps.close();
		    rset.close();
		    connection.close();
	    }
	    catch(SQLException e) {
	    	e.printStackTrace();
	    }
	    return clusters;
	}
	
}
