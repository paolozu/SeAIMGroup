package messagesTest;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.TreeMap;

import org.json.JSONObject;

public class MessagesSender {
	
	public static void main(String[] args) throws Exception {

			// To keep trace of starting time.
			long startTime;
			
			// Values of each message.
			int robot_id = 0;
			int cluster_id = 0;
			int area_id = 0;
			int signal_state = 0;
			
	    	// Useful variables to increase robots and clusters ids
	    	// to no repeat them in cluster and areas respectively. 
			int robot_counter = 0;
			int cluster_counter = 0;
			
			// Server and port we're using to send messages.
			String query = "http://127.0.0.1:8000";
			
			// Data structure to keep trace of areas --> clusters --> robots.
			TreeMap<Integer, Area> areas = new TreeMap<>();
			
			// Initializing areas, clusters and robots.
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
            
			System.out.println("Initialization terminated");
			
			startTime = System.currentTimeMillis();
			
            for( int i = 0; i < 90000; i++ ) {
            	
            	JSONObject robot_message = new JSONObject();
                robot_message.put("robot_id", robot_id);
                robot_message.put("cluster_id", cluster_id);
                robot_message.put("area_id", area_id);
                robot_message.put("signal_state", signal_state);

	            URL url = new URL(query);
	            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
	            conn.setConnectTimeout(0);
	            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
	            conn.setDoOutput(true);
	            conn.setDoInput(true);
	            conn.setRequestMethod("POST");
	
	            
	            OutputStream os = conn.getOutputStream();
	            
	            os.write(robot_message.toString().getBytes("UTF-8"));
	            os.close();
	
	            // Reading the response.
	            InputStream in = new BufferedInputStream(conn.getInputStream());
	
	            in.close();
	            conn.disconnect();
            }
            
            // Printing execution time.
            long endTime   = System.currentTimeMillis();
            long totalTime = endTime - startTime;
            System.out.println(totalTime);
    
	}
}
