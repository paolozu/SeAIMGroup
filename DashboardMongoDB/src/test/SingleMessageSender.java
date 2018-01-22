package test;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

import org.json.JSONObject;

public class SingleMessageSender {
	
	public static void main(String[] args) throws Exception {
			
	    	// Useful variables to increase robots and clusters ids
	    	// to no repeat them in cluster and areas respectively. 
			
			// Server and port we're using to send messages.
			String query = "http://127.0.0.1:8000";
			
			// Data structure to keep trace of areas --> clusters --> robots.
			HashMap<Integer, AreaTest> areas = RestoreFromDB.getAreas();
			
			int robot_counter = 0;
			int cluster_counter = 0;
			
			for ( int i = 0; i < 10; i++ ) {
				if( ! areas.containsKey(i) )
					areas.put(i, new AreaTest(i));
	    		for( int x = 0; x < 10; x++ ) {
	    			if( ! areas.get(i).getClusters().containsKey(x + cluster_counter) )
	    				areas.get(i).addCluster(new ClusterTest(x + cluster_counter, i));
	    			for( int y = 0; y < 900; y++ ) {
	    				if( ! areas.get(i).getClusters().get(x + cluster_counter).getRobots().containsKey(y + robot_counter) )
	    					areas.get(i).getClusters().get(x + cluster_counter)
	    						 .handleRobot(new RobotTest(y + robot_counter, x + cluster_counter));
	    			}
	    			robot_counter += 900;
	    		}
    			cluster_counter += 10;
	    	}
	
			// Variables to generate random messages.
			Integer area_id;
			Integer cluster_id;
			Integer robot_id;
			Integer signal_state;
			long message_time;
			
			area_id = 0;
        	cluster_id =  0;
        	robot_id = 4;
        	signal_state = 1;
        	
        	message_time = System.currentTimeMillis() - 1500000;
        	
        	
        	JSONObject robot_message = new JSONObject();
            robot_message.put("robot_id", robot_id);
            robot_message.put("cluster_id", cluster_id);
            robot_message.put("area_id", area_id);
            robot_message.put("signal_state", signal_state);
            robot_message.put("message_time", message_time);

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
}
