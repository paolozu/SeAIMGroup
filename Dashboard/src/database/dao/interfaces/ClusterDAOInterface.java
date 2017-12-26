package database.dao.interfaces;

import java.sql.SQLException;
import applicationCore.Cluster;
import applicationCore.Robot;

public interface ClusterDAOInterface {
	
	public void insertCluster(Cluster cluster) throws SQLException;
	public void updateCluster(Cluster cluster) throws SQLException;
}
