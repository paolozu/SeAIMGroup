package messagesTest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.util.TreeMap;
import org.json.JSONException;
import org.json.JSONObject;
import com.sun.net.httpserver.*;

public class MessagesListener {

	// Counter to test speed.
	static int counter = 0;
	static int robots_counter = 0;
	static TreeMap<Integer, Area> areas = new TreeMap<>();
	
	// Array to test robots initialization time
	// using json'datas or to test updating time 
	// always using json'datas.
	//static Robot[] robots = new Robot[90000];

    public static void main(String[] args) throws Exception {
    	
    	// Useful variables to increase robots and clusters ids
    	// to no repeat them in cluster and areas respectively. 
    	int robot_counter = 0;
    	int cluster_counter = 0;
    	
    	for ( int i = 0; i < 10; i++ ) {
    		areas.put(i, new Area(i));
    		for(int x = 0; x < 10; x++) {
    			areas.get(i).addCluster(new Cluster(x + cluster_counter, i));
    			for(int y = 0; y < 900; y++) {
    				areas.get(i).getClustersIR().get(x + cluster_counter)
    					 .handleRobot(new Robot(y + robot_counter, x + cluster_counter));
    			}
    			robot_counter += 900;
    		}
			cluster_counter += 10;
    	}

    	HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
    	server.createContext("/", new MessagesReceiver());
    	server.setExecutor(null); 
    	server.start();

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
    		    
			} catch (JSONException e) {
				message = null;
			}
    		
    		// Uncomment the lines below to print counter on json reception
    		// or to print received json itself.
	    	
    		//System.out.println(message);
    		//System.out.println(counter++);

    		String response = "This is the response";
    		exchange.sendResponseHeaders(200, response.getBytes().length);
    		OutputStream os = exchange.getResponseBody();
    		os.write(response.getBytes());
    		os.close();
    		
    		try {
    			int area_id = message.getInt("area_id");
	    		int cluster_id = message.getInt("cluster_id");
	    		int robot_id = message.getInt("robot_id");
	    		int signal_state = message.getInt("signal_state");
	    			
				if( areas.containsKey(area_id) ) {
					if( areas.get(area_id).getClustersIR().containsKey(cluster_id) ) {
						if( areas.get(area_id).getClustersIR().get(cluster_id).getRobotsIR().containsKey(robot_id) ) {
							areas.get(area_id).getClustersIR().get(cluster_id).getRobotsIR().get(robot_id).signalCatch(signal_state);
							areas.get(area_id).getClustersIR().get(cluster_id)
								 .handleRobot(areas.get(area_id).getClustersIR()
							     .get(cluster_id).getRobotsIR().get(robot_id));
						}
						else {
							Robot current_robot = new Robot(robot_id, cluster_id);
							current_robot.signalCatch(signal_state);
							
							areas.get(area_id).getClustersIR().get(cluster_id).handleRobot(current_robot);
						}
					}
					else {
						Robot current_robot = new Robot(robot_id, cluster_id);
			    		Cluster current_cluster = new Cluster(cluster_id, area_id);
						current_robot.signalCatch(signal_state);
						current_cluster.handleRobot(current_robot);
						
						areas.get(area_id).addCluster(current_cluster);
					}
				}
				else {
					Robot current_robot = new Robot(robot_id, cluster_id);
		    		Cluster current_cluster = new Cluster(cluster_id, area_id);
		    		current_robot.signalCatch(signal_state);
		    		current_cluster.handleRobot(current_robot);
		    		
					areas.put(area_id, new Area(area_id));
					areas.get(area_id).getClustersIR().put(cluster_id, current_cluster);
				}
	
    		} 
    		catch (JSONException e) {
				e.printStackTrace();
			}
    		
			// Comment the 2 try-catch below to test json reception speed.
			
			// Updating robots signal from information of received json
			// and also updating down time and IR time.
			// Down time and IR shouldn't be updated here.
			
			/*try {
				robots[counter].signalCatch(message.getInt("signal_state"));
				robots[counter++].updateIR();
			} catch (JSONException e) {
				e.printStackTrace();
			}*/
			
			// Uncomment the try-catch below, the for cycle inside the main function
			// and comment the try-catch above to test initialization 
			// time of an array of 90000 robots using json'datas.
			
			/*try {
				robots[counter++] = new Robot(message.getInt("robot_id"), message.getInt("cluster_id"));
			} catch (JSONException e) {
				e.printStackTrace();
			}*/
				
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