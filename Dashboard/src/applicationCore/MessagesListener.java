package applicationCore;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.util.HashMap;
import org.json.JSONException;
import org.json.JSONObject;
import com.sun.net.httpserver.*;

public class MessagesListener {

	// Counter to test speed.
	static int counter = 0;
	static int robots_counter = 0;
	static HashMap<Integer, Area> areas = new HashMap<>();
	private static Thread IRUpdater;

    public static void main(String[] args) throws Exception {
    
    	HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
    	server.createContext("/", new MessagesReceiver());
    	server.setExecutor(null); 
    	server.start();
    	
    	IRUpdater = new Thread(){
			public void run() {
				try {
					sleep(300000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				while( true ) {
					if( ! areas.isEmpty() ) {
						long startTime = System.currentTimeMillis();
						for( Area area : areas.values() ) {
			    			for( Cluster cluster : areas.get(area.getAreaId()).getClusters().values() ) {
			    				for( Robot robot : areas.get(area.getAreaId()).getClusters().get(cluster.getClusterId()).getRobots().values() ) {
			    					if( robot.getDownSignals() > 0)
			    						robot.forceUpdateIR();
			    				}
			    				if ( cluster.getDownRobots() > 0 )
			    					cluster.forceUpdateIR();
			    			}	
			    		}
						long endTime   = System.currentTimeMillis();
			            long totalTime = endTime - startTime;
			            System.out.println(totalTime);
						try {
							sleep(240000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
			}
		};
		IRUpdater.start();

    }

    static class MessagesReceiver implements HttpHandler {    	
    	
	    @Override
	    public void handle(HttpExchange exchange) throws IOException {
	    	
	    	
	    	InputStream robot_message = exchange.getRequestBody();
	    	JSONObject message;
	    	
    		// Message formatted like {"signal_state":1,"cluster_id":12,"robot_id":123,"area_id":123}

    		try {
    			BufferedReader rd = new BufferedReader(new InputStreamReader(robot_message, Charset.forName("UTF-8")));
    			String jsonText = readAll(rd);
    		    message = new JSONObject(jsonText);

    			int area_id = message.getInt("area_id");
	    		int cluster_id = message.getInt("cluster_id");
	    		int robot_id = message.getInt("robot_id");
	    		int signal_state = message.getInt("signal_state");
	    			
				if( areas.containsKey(area_id) ) {
					if( areas.get(area_id).getClusters().containsKey(cluster_id) ) {
						if( areas.get(area_id).getClusters().get(cluster_id).getRobots().containsKey(robot_id) ) {
							areas.get(area_id).getClusters().get(cluster_id).getRobots().get(robot_id).signalCatch(signal_state);
							areas.get(area_id).getClusters().get(cluster_id)
								 .handleRobot(areas.get(area_id).getClusters()
							     .get(cluster_id).getRobots().get(robot_id));
						}
						else {
							Robot current_robot = new Robot(robot_id, cluster_id);
							current_robot.signalCatch(signal_state);
							
							areas.get(area_id).getClusters().get(cluster_id).handleRobot(current_robot);
						}
					}
					else {
						// IT'S IMPORTANT TO CREATE FIRST THE CLUSTER
			    		Cluster current_cluster = new Cluster(cluster_id, area_id);
			    		Robot current_robot = new Robot(robot_id, cluster_id);
						current_robot.signalCatch(signal_state);
						current_cluster.handleRobot(current_robot);
						
						areas.get(area_id).addCluster(current_cluster);
					}
				}
				else {
					// IT'S IMPORTANT TO CREATE FIRST THE CLUSTER
		    		Cluster current_cluster = new Cluster(cluster_id, area_id);
		    		Robot current_robot = new Robot(robot_id, cluster_id);
		    		current_robot.signalCatch(signal_state);
		    		current_cluster.handleRobot(current_robot);
		    		
					areas.put(area_id, new Area(area_id));
					areas.get(area_id).getClusters().put(cluster_id, current_cluster);
				}
	
    		} 
    		catch(JSONException e) {
				e.printStackTrace();
			}
			
	
    		//System.out.println(message);
    		//System.out.println(counter++);

    		String response = "This is the response";
    		exchange.sendResponseHeaders(200, response.getBytes().length);
    		OutputStream os = exchange.getResponseBody();
    		os.write(response.getBytes());
    		os.close();
 
    		
    		if( ++counter == 90000 || counter == 180000 || counter == 270000 || counter == 360000 ||
    			 counter == 450000 || counter == 540000 || counter == 630000 || counter == 720000 ||
    			 counter == 810000 || counter == 900000 || counter == 990000 || counter == 1080000) {
    		
	    		/*for( Area area : areas.values() ) {
	    			System.out.println("\n\n");
	    			for( Cluster cluster : areas.get(area.getAreaId()).getClusters().values() ) {
	    				System.out.println(cluster.getDownRobots());
	    			}
	    		}*/
	    		
    			// Uncomment the following two lines to print the IR of cluster 91.
    			
    			//areas.get(9).getClusters().get(91).forceUpdateIR();
    			//System.out.println(areas.get(9).getClusters().get(91));
    			
    			// Printing all robots informations of cluster 91. 
    			
    			/*areas.get(9).getClusters().get(91).forceUpdateIR();
    			for( Map.Entry<Integer, Robot> robot : areas.get(9).getClusters().get(91).getRobots().entrySet() ) {
    				robot.getValue().forceUpdateIR();
    				System.out.println(robot.getValue());
    			}*/
    			
    			System.out.println("Messages: " + counter);
    			
    		}
				
    		robot_message.close();
	    }
	    
	    // Function to parse received json.
	    private static String readAll(Reader reader) throws IOException {
	        StringBuilder stringBuilder = new StringBuilder();
	        int cp;
	        while ((cp = reader.read()) != -1) {
	          stringBuilder.append((char) cp);
	        }
	        return stringBuilder.toString();
	      }
    }
}