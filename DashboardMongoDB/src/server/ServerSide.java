package server;

import java.util.ArrayList;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.bson.Document;
import org.json.JSONException;
import org.json.JSONObject;

import com.mongodb.client.DistinctIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Indexes;

import database.DatabaseConnector;
 
@ServerEndpoint(value = "/socket")
public class ServerSide {
	
	// Queue of all opened sessions.
	private static Queue<Session> queue = new ConcurrentLinkedQueue<Session>();
	private static Thread thread;
	
	// Thread that sends messages via socket to clients.
	static{
		thread = new Thread(){
			public void run() {
				while(true) {     
					if( queue != null ) {
						JSONObject robots_and_clusters_IR = new JSONObject();
						MongoDatabase database = DatabaseConnector.CONNECTION.getDatabase();
						MongoCollection<Document> robots_collection = database.getCollection("robot");
						MongoCollection<Document> clusters_collection = database.getCollection("cluster");
						
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
												try {
													robots_json.put(current_robot.getInteger("_id").toString(), current_robot.getDouble("robot_ir"));
												} catch (JSONException e) {
													e.printStackTrace();
												}
											}
										}
										finally {
											try {
												clusters_json.put("cluster_ir", current_cluster.getDouble("cluster_ir"));
												clusters_json.put("robots", robots_json);
												clusters_index_json.put(current_cluster.getInteger("_id").toString(), clusters_json);
											}
											catch (JSONException e) {
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
						sendAll(robots_and_clusters_IR.toString());
					}
					try {
						sleep(30000);
					}
					catch (InterruptedException e) {      
					}
				}
			};
		} ;
		thread.start();
	}
	 
	@OnMessage
	public void onMessage(Session session, String message) {
	//provided for completeness, in out scenario clients don't send any messages.
		try {   
			System.out.println("received msg " + message + " from " + session.getId());
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	 
	@OnOpen
	public void open(Session session) {
		queue.add(session);
		System.out.println("New session opened: " + session.getId());
	}
	
	 @OnError
	 public void error(Session session, Throwable t) {
		 queue.remove(session);
		 System.err.println("Error on session " + session.getId());  
	 }
	  
	 @OnClose
	 public void closedConnection(Session session) { 
		 queue.remove(session);
		 System.out.println("session closed: " + session.getId());
	 }
	 
	 private static void sendAll(String message) {
		 try {
			 /* Send the new rate to all open WebSocket sessions */  
			 ArrayList<Session> closedSessions= new ArrayList<>();
			 for ( Session session : queue ) {
				 if( !session.isOpen() ) {
					 closedSessions.add(session);
				 }
				 else {
					 session.getBasicRemote().sendText(message);
					 //session.getBasicRemote().sendObject(message);
					 //session.getBasicRemote().sendStream(message);
				 }    
			 }
			 queue.removeAll(closedSessions);
			 //System.out.println("Sending " + message + " to " + queue.size() + " clients");
		 }
		 catch (Throwable e) {
			 e.printStackTrace();
		 }
	 }
}
