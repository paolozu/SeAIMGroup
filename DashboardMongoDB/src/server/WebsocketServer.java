package server;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.json.JSONObject;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class WebsocketServer extends WebSocketServer {

    private final static int TCP_PORT = 4444;
    private final static String IP_ADDRESS = "192.168.1.34";  //Put your local IP here
    private Set<WebSocket> clients;	
    private JSONObject message;

    public WebsocketServer() throws UnknownHostException {
        super(new InetSocketAddress(IP_ADDRESS, TCP_PORT));
        clients = new HashSet<>();
    }
    
    // Getters and setters.
    
    public Set<WebSocket> getClients(){ return this.clients; }
    public JSONObject getMessage() { return this.message; }

    public void setClients(Set<WebSocket> clients){ this.clients = clients; }
    public void setMessage(JSONObject message) { this.message = message; }
    
    // Method to send message to all connected clients.
    public void sendMessageToCLients() {
    	// We better don't iterate on clients variable because it's dynamic.
    	ArrayList<WebSocket> currentClients = new ArrayList<>(clients);
    	for(WebSocket client : currentClients)
    		client.send(message.toString());
    }
    
    // Overriding methods.

    @Override
    public void onOpen(WebSocket client, ClientHandshake handshake) {
        clients.add(client);
        if( message != null )
        	client.send(message.toString());
    }

    @Override
    public void onClose(WebSocket client, int code, String reason, boolean remote) {
    	clients.remove(client);
    }

    @Override
    public void onMessage(WebSocket connection, String message) { }

    @Override
    public void onError(WebSocket client, Exception exception) {
        if (client != null) 
        	clients.remove(client);
    }
    
}
