package database.dao.concrete;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import applicationCore.Cluster;
import applicationCore.Robot;
import database.DatabaseConnector;
import database.dao.interfaces.ClusterDAOInterface;

public class ClusterDAO implements ClusterDAOInterface {
	
	private static final String
	INSERT = "INSERT INTO cluster(cluster_id, area_id, cluster_ir) VALUES (?, ?, ?);";
	
	private static final String
	UPDATE = "UPDATE cluster SET cluster.cluster_ir = ? WHERE cluster_id = ?;";
	
	private static final String
	ROBOTS = "SELECT * FROM robot WHERE cluster_id = ?;";

	@Override
	public void insertCluster(Cluster cluster) {
		try {
			Connection connection = DatabaseConnector.getInstance().getConnection();
		    PreparedStatement ps = connection.prepareStatement(INSERT);
		    ps.setInt(1, cluster.getClusterId());
		    ps.setInt(2, cluster.getAreaId());
		    ps.setDouble(3, cluster.getClusterIR());
		    ps.executeUpdate();
		    ps.close();
		    connection.close();
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void updateCluster(Cluster cluster) {
		try {
			Connection connection = DatabaseConnector.getInstance().getConnection();
		    PreparedStatement ps = connection.prepareStatement(UPDATE);
		    ps.setDouble(1, cluster.getClusterIR());
		    ps.setInt(2, cluster.getClusterId());
		    ps.executeUpdate();
		    ps.close();
		    connection.close();
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public ArrayList<Robot> getRobots(Integer cluster_id) {
		
	    ArrayList<Robot> robots = new ArrayList<>();
	    
	    try {
		    Connection connection = DatabaseConnector.getInstance().getConnection();
		    PreparedStatement ps = connection.prepareStatement(ROBOTS);
		    ps.setInt(1, cluster_id);
		    ResultSet rset = ps.executeQuery();
		    while (rset.next()){
		      Robot robot = new Robot(rset.getInt(1), rset.getInt(2), rset.getDouble(3));
		      robots.add(robot);
		    }
		    ps.close();
		    rset.close();
		    connection.close();
	    }
	    catch(SQLException e) {
	    	e.printStackTrace();
	    }
	    return robots;
	  }
	
}
