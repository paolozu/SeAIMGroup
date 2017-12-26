package database.dao.concrete;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import applicationCore.Cluster;
import database.DatabaseConnector;
import database.dao.interfaces.ClusterDAOInterface;

public class ClusterDAO implements ClusterDAOInterface {
	
	private static final String
	INSERT = "INSERT INTO cluster(cluster_id, area_id, cluster_ir) VALUES (?, ?, ?);";
	
	private static final String
	UPDATE = "UPDATE cluster SET cluster.cluster_ir = ? WHERE cluster_id = ?;";

	
	public void insertCluster(Cluster cluster) throws SQLException{
		Connection connection = DatabaseConnector.openConnection();
	    PreparedStatement ps = connection.prepareStatement(INSERT);
	    ps.setInt(1, cluster.getClusterId());
	    ps.setInt(2, cluster.getAreaId());
	    ps.setDouble(3, cluster.getClusterIR());
	    ps.executeUpdate();
	    ps.close();
	    connection.close();
	}
	
	public void updateCluster(Cluster cluster) throws SQLException{
		Connection connection = DatabaseConnector.openConnection();
	    PreparedStatement ps = connection.prepareStatement(UPDATE);
	    ps.setDouble(1, cluster.getClusterIR());
	    ps.setInt(1, cluster.getClusterId());
	    ps.executeUpdate();
	    ps.close();
	    connection.close();
	}
	
}
