package database;

import java.beans.PropertyVetoException;
import java.sql.Connection;
import java.sql.SQLException;

import com.mchange.v2.c3p0.ComboPooledDataSource;

public class DatabaseConnector {
	
	private static String URL = "jdbc:mysql://localhost/ir_viewer?autoReconnect=true&useSSL=false";
	private static String DRIVER = "com.mysql.jdbc.Driver";
	private static String USER = "aimgroup";
	private static String PASSWORD = "aim";

	// Method used to establish a connection with database
	private static DatabaseConnector dataSource;
	private ComboPooledDataSource comboPooledDataSource;

	private DatabaseConnector() {
		try {
			comboPooledDataSource = new ComboPooledDataSource();
			comboPooledDataSource.setDriverClass(DRIVER);
			comboPooledDataSource.setJdbcUrl(URL);
			comboPooledDataSource.setUser(USER);
			comboPooledDataSource.setPassword(PASSWORD);
		}
		catch (PropertyVetoException ex1) {
			ex1.printStackTrace();
		}	
	}

	public static DatabaseConnector getInstance() {
		if (dataSource == null)
			dataSource = new DatabaseConnector();
		return dataSource;
   	}
	
	public Connection getConnection() {
		Connection con = null;
		try {
			con = comboPooledDataSource.getConnection();
		} 
		catch (SQLException e) {
			e.printStackTrace();
		}
		return con;
	}

}

