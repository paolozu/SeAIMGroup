package test;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Random;
import java.util.HashMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import org.json.JSONObject;
public class MessagesSender {
	
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
			
			/*HashMap<Integer, AreaTest> areas = new HashMap<>();
			
			//int robot_counter = 0;
			//int cluster_counter = 0;
			
			for ( int i = 0; i < 10; i++ ) {
	    		areas.put(i, new AreaTest(i));
	    		for( int x = 0; x < 10; x++ ) {
	    			areas.get(i).addCluster(new ClusterTest(x + cluster_counter, i));
	    			for( int y = 0; y < 900; y++ ) {
	    				areas.get(i).getClusters().get(x + cluster_counter)
	    					 .handleRobot(new RobotTest(y + robot_counter, x + cluster_counter));
	    			}
	    			robot_counter += 900;
	    		}
    			cluster_counter += 10;
	    	}*/
			
			
			
			// Variables to generate random messages.
			Integer area_id;
			Integer cluster_id;
			Integer robot_id;
			Integer signal_state = 0;		
			Integer down_signals;
			long message_time;
			
			for(int t = 0; t < 100; t++) {
				// We send 90000 messages for 10 times each 60 seconds.
	            for( int i = 0; i < 90000; i++ ) {
	            	
	            	area_id = new Random().nextInt(10);
	            	cluster_id =  ThreadLocalRandom.current().nextInt(((area_id)*10), ((area_id)*10)+10);
	            	robot_id = ThreadLocalRandom.current().nextInt(((cluster_id)*900), ((cluster_id)*900)+900);  
	            	down_signals = areas.get(area_id).getClusters().get(cluster_id).getRobots().get(robot_id).getDownSignals();
	            	
	            	if( down_signals == 0 ) {
	            		signal_state = 0;
	            		areas.get(area_id).getClusters().get(cluster_id).getRobots().get(robot_id).signalCatch(signal_state);
	            	}
	            	else if ( down_signals == 7 ) {
	            		signal_state = 1;
	            		areas.get(area_id).getClusters().get(cluster_id).getRobots().get(robot_id).signalCatch(signal_state);
	            	}
	            	else {
	            		signal_state += new Random().nextInt(2);
	            		areas.get(area_id).getClusters().get(cluster_id).getRobots().get(robot_id).signalCatch(signal_state);
	            	}
	            	
	            	message_time = System.currentTimeMillis();
	            	
	            	
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
	            TimeUnit.MINUTES.sleep(1);
			} 
	}
}
