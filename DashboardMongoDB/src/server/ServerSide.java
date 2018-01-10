package server;

import org.bson.Document;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.json.JSONException;
import org.json.JSONObject;

import com.mongodb.client.DistinctIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Indexes;

import database.DatabaseConnector;

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
					
					MongoDatabase database = DatabaseConnector.CONNECTION.getDatabase();
					MongoCollection<Document> robots_collection = database.getCollection("robot");
					MongoCollection<Document> clusters_collection = database.getCollection("cluster");
					
					while(true) { 
						if( ! a.getConnections().isEmpty() ) {
							JSONObject robots_and_clusters_IR = new JSONObject();
							
							robots_collection.createIndex(Indexes.ascending("cluster_id"));
							clusters_collection.createIndex(Indexes.ascending("area_id"));
							
							DistinctIterable<Integer> areas_count = clusters_collection.distinct("area_id", Integer.class);
							MongoCursor<Integer> areas = areas_count.iterator();
							
							try {
								while(areas.hasNext()) {
									Integer current_area = areas.next();
									MongoCursor<Document> clusters = clusters_collection.find(Filters.eq("area_id", current_area)).iterator();
									JSONObject clusters_index_json = new JSONObject();
									try {
										while(clusters.hasNext()) {
											Document current_cluster = clusters.next();
											MongoCursor<Document> robots = robots_collection.find(Filters.eq("cluster_id", current_cluster.getInteger("_id"))).iterator();
											JSONObject robots_json = new JSONObject();
											JSONObject clusters_json = new JSONObject();
											try {
												while(robots.hasNext()) {
													Document current_robot = robots.next();
													robots_json.put(current_robot.getInteger("_id").toString(), current_robot.getDouble("robot_ir"));
												}
											} catch (JSONException e) {
												e.printStackTrace();
											}
											finally {
												try {
													clusters_json.put("cluster_ir", current_cluster.getDouble("cluster_ir"));
													clusters_json.put("robots", robots_json);
													clusters_index_json.put(current_cluster.getInteger("_id").toString(), clusters_json);
												}
												catch(JSONException e) {
													e.printStackTrace();
												}
												robots.close();
											}
										}
									}
									finally {
										try {
											robots_and_clusters_IR.put(current_area.toString(), clusters_index_json);
										} catch (JSONException e) {
											e.printStackTrace();
										}
										clusters.close();
									}
								}
							}
							finally{
								areas.close();
							}
							for (WebSocket sock : a.getConnections()) {
								sock.send(robots_and_clusters_IR.toString());
					        }
						}
						try {
							sleep(15000);
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
	    private final static String IP_ADDRESS = "10.172.13.108";
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
}