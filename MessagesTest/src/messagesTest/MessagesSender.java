package messagesTest;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONObject;

public class MessagesSender {
	
	public static void main(String[] args) throws Exception {

			// To keep trace of starting time.
			long startTime = System.currentTimeMillis();
		
            String query = "http://127.0.0.1:8000";
            JSONObject robot_message = new JSONObject();
            robot_message.put("robot_id", 123);
            robot_message.put("signal_state", 0);
            robot_message.put("cluster_id", 123);
            robot_message.put("area_id", 123);
            
            for( int i = 0; i < 90000; i++ ) {

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
