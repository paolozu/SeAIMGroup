package TestingSignals;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import com.sun.net.httpserver.*;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class MessagesHandler implements HttpHandler {

	static int counter = 0;

    public static void main(String[] args) throws Exception {

    	HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
    	server.createContext("/", new MessagesHandler());
    	server.setExecutor(null); // creates a default executor
    	server.start();

    }

    InputStream robot_messages;

    @Override
    public void handle(HttpExchange exchange) throws IOException {

    	robot_messages = exchange.getRequestBody();
    	JSONObject message = null;
    	JSONParser jsonParser = new JSONParser();


    		/* message in formato {"signal_state":0,"cluster_id":12,"robot_id":123,"area_id":123}   */

    		try {
				message = (JSONObject)jsonParser.parse(
					      			  new InputStreamReader(robot_messages, "UTF-8"));
			} catch (ParseException e) {
				e.printStackTrace();
			}

    		System.out.println(message);

    		//counter++;
    		//System.out.println(counter);

    		String response = "This is the response";
    		exchange.sendResponseHeaders(200, response.getBytes().length);
    		OutputStream os = exchange.getResponseBody();
    		os.write(response.getBytes());
    		os.close();

    		robot_messages.close();

    }
}
