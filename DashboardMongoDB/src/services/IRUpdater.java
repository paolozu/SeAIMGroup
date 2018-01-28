package services;

import java.util.concurrent.ConcurrentHashMap;
import model.Area;
import model.Cluster;
import model.Robot;
import server.WebsocketServer;

public class IRUpdater {
	
	private WebsocketServer websocketServer;
	private ConcurrentHashMap<Integer, Area> areas;
	
	public IRUpdater(WebsocketServer websocketServer,  ConcurrentHashMap<Integer, Area> areas) {
		this.websocketServer = websocketServer;
		this.areas = areas;
	}

	public void run() {
		try {
			Thread.sleep(15000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		while( true ) {
			if( ! areas.isEmpty() ) {
				for( Area current_area :  areas.values() ) {
	    			for( Cluster current_cluster : current_area.getClusters().values() ) {
	    				for( Robot robot : current_cluster.getRobots().values() ) {
	    					robot.updateDownTime();
	    				}
	    				current_cluster.updateDownTime();
	    			}	
	    		}	            
				// Starting service to update clients json.
	            ClientsSender clientsSender = new ClientsSender(websocketServer);
	            
	            // Send updated and structured json to clients.
	            clientsSender.run();
			}
			
			// SLEEP TO KEEP UDPATE TIME AROUND 30 SECONDS WITH 90000 ROBOTS.
			
			try {
				Thread.sleep(10000);
			}
			catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
}
