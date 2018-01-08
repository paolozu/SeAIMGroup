package server;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.json.JSONException;
import org.json.JSONObject;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.HashSet;
import java.util.Set;

public class ServerSide {
	
	public static void main(String[]args) throws UnknownHostException {
		ServerSide serverSide = new ServerSide();
		WebsocketServer a = serverSide.new WebsocketServer();
		a.start();
		Thread thread;
			thread = new Thread(){
				public void run() {
					while(true) {     
						if( !a.conns.isEmpty() ) {
							for (WebSocket sock : a.conns) {
								JSONObject b = new JSONObject();
								try {
									b.put("ciao", 3);
								} catch (JSONException e) {
									e.printStackTrace();
								}
					            sock.send(b.toString());
					        }
						}
						try {
							sleep(5000);
						}
						catch (InterruptedException e) {      
						}
					}
				};
			} ;
			thread.start();
		}


	public class WebsocketServer extends WebSocketServer {
	
	    private final static int TCP_PORT = 4444;
		
	
	    public Set<WebSocket> conns;
	    
	    
	
	    public WebsocketServer() throws UnknownHostException {
	        super(new InetSocketAddress("192.168.159.111", TCP_PORT));
	        conns = new HashSet<>();
	    }
	
	    @Override
	    public void onOpen(WebSocket conn, ClientHandshake handshake) {
	        conns.add(conn);
	        System.out.println("New connection from " + conn.getRemoteSocketAddress().getAddress().getHostAddress());
	    }
	
	    @Override
	    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
	        conns.remove(conn);
	        System.out.println("Closed connection to " + conn.getRemoteSocketAddress().getAddress().getHostAddress());
	    }
	
	    @Override
	    public void onMessage(WebSocket conn, String message) {
	        System.out.println("Message from client: " + message);
	        for (WebSocket sock : conns) {
	            sock.send(message);
	        }
	    }
	
	    @Override
	    public void onError(WebSocket conn, Exception ex) {
	        //ex.printStackTrace();
	        if (conn != null) {
	            conns.remove(conn);
	            // do some thing if required
	        }
	        System.out.println("ERROR from " + conn.getRemoteSocketAddress().getAddress().getHostAddress());
	    }
	    
	}
}