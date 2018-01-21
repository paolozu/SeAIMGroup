package model;

import java.io.IOException;
import java.net.InetSocketAddress;
import com.sun.net.httpserver.HttpServer;
import database.dao.concrete.RestoreFromDB;
import server.MessagesHandler;
import server.WebsocketServer;
import services.IRUpdater;

public class StartSystem {
	
	public static void main(String[]args) throws IOException {
		
		/*
		 * 
		 * We have 3 different threads. 
		 * 
		 * The first one(listenerServer) cares about receive messages and store clusters and robots into the database.
		 * The second one(websocketServer) is charged to keep a web socket connection with connected clients.
		 * The last one(this one) is charged to update records into the database and send messages to connected clients. 
		 * 
		 */
		
		// Starting server to create web socket with clients.
		WebsocketServer websocketServer = new WebsocketServer();
		websocketServer.start();
		
		// Starting server to handle received messages.
		HttpServer listenerServer = HttpServer.create(new InetSocketAddress(8000), 0);
		//MessagesHandler messagesHandler = new MessagesHandler();
		MessagesHandler messagesHandler = new MessagesHandler(RestoreFromDB.getAreas());
		listenerServer.createContext("/", messagesHandler);
		listenerServer.setExecutor(null); 
		listenerServer.start();
		
		// Starting service to update records into the database
		// This method calls the run method of ClientsSender to send messages to clients.
		IRUpdater ir_updater = new IRUpdater(websocketServer, messagesHandler.getAreas());
		ir_updater.run();
		
	}
	
}
	

