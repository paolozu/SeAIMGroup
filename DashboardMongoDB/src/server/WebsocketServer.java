package server;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.HashSet;
import java.util.Set;

public class WebsocketServer extends WebSocketServer {

    private final static int TCP_PORT = 4444;
    private final static String IP_ADDRESS = "192.168.159.111";
    private Set<WebSocket> connections;	    

    public WebsocketServer() throws UnknownHostException {
        super(new InetSocketAddress(IP_ADDRESS, TCP_PORT));
        connections = new HashSet<>();
    }
    
    public Set<WebSocket> getConnections(){
    	return this.connections;
    }

    @Override
    public void onOpen(WebSocket connection, ClientHandshake handshake) {
        connections.add(connection);
    }

    @Override
    public void onClose(WebSocket connection, int code, String reason, boolean remote) {
    	connections.remove(connection);
    }

    @Override
    public void onMessage(WebSocket connection, String message) { }

    @Override
    public void onError(WebSocket connection, Exception exception) {
        if (connection != null) 
        	connections.remove(connection);
    }
    
}
