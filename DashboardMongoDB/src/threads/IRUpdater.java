package threads;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import org.java_websocket.WebSocket;
import model.Area;
import model.Cluster;
import model.Robot;

public class IRUpdater implements Runnable {
	
	private Set<WebSocket> connections;
	private  HashMap<Integer, Area> areas;
	
	public IRUpdater(Set<WebSocket> connections,  HashMap<Integer, Area> areas) {
		this.connections = connections;
		this.areas = areas;
	}
	
	@Override
	public void run() {
		try {
			Thread.sleep(15000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		while( true ) {
			if( ! areas.isEmpty() ) {
				ArrayList<Area> current_areas = new ArrayList<Area>(areas.values());
				for( Area current_area :  current_areas) {
					ArrayList<Cluster> current_clusters = new ArrayList<Cluster>(current_area.getClusters().values());
	    			for( Cluster current_cluster : current_clusters ) {
	    				ArrayList<Robot> current_robots = new ArrayList<Robot>(current_cluster.getRobots().values());
	    				for( Robot robot : current_robots ) {
	    					robot.forceUpdateIR();
	    				}
	    				current_cluster.forceUpdateIR();
	    			}	
	    		}	            
				// Starting thread to update clients json.
	            Runnable clientsSender = new ClientsSender(connections);
				new Thread(clientsSender).start();
			}
		}
	}

}
