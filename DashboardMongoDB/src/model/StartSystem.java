package model;

import java.io.IOException;
import java.net.InetSocketAddress;
import com.sun.net.httpserver.HttpServer;

import server.MessagesHandler;
import server.WebsocketServer;
import threads.IRUpdater;

public class StartSystem {
	
	public static void main(String[]args) throws IOException {
		
		// Starting server to handle received messages.
		HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
		MessagesHandler messagesHandler = new MessagesHandler();
		server.createContext("/", messagesHandler);
		server.setExecutor(null); 
		server.start();
		
		// Starting server to create websocket to clients.
		WebsocketServer websocketServer = new WebsocketServer();
		websocketServer.start();
		
		// Starting thread to update records into the database
		// This thread calls ClientsSender Thread itself to update clients.
		Runnable ir_updater = new IRUpdater( websocketServer.getConnections(), messagesHandler.getAreas());
		new Thread(ir_updater).start();
		
	}
	
}
	

