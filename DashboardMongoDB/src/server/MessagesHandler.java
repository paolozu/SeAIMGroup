package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.HashMap;
import org.json.JSONException;
import org.json.JSONObject;
import com.sun.net.httpserver.*;
import model.Area;
import model.Cluster;
import model.Robot;

public class MessagesHandler implements HttpHandler {    
	
	private HashMap<Integer, Area> areas = new HashMap<>();
	
	public HashMap<Integer, Area> getAreas(){
		return this.areas;
	}
	
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
				areas.get(area_id).getClusters().put(cluster_id, current_cluster);
			}

		} 
		catch(JSONException e) {
			e.printStackTrace();
		}

		String response = "This is the response";
		exchange.sendResponseHeaders(200, response.getBytes().length);
		OutputStream os = exchange.getResponseBody();
		os.write(response.getBytes());
		os.close();
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
