package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnector {
	private static String URL = "jdbc:mysql://localhost/ir_viewer?autoReconnect=true&useSSL=false";
	private static String DRIVER = "com.mysql.jdbc.Driver";
	private static String USER = "aimgroup";
	private static String PASSWORD = "aim";


	//Method used to establish a connection with database
	public static Connection openConnection(){
		try {
			Class.forName(DRIVER);
			Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
			return connection;
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		catch (Exception ex) {
			System.err.println("Impossibile salvare/caricare i dati! Il database non risponde!");
		}
		return null;
  }
}

