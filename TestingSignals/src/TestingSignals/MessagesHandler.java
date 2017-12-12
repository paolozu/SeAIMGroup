package TestingSignals;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import com.sun.net.httpserver.*;

import org.json.JSONException;
import org.json.JSONObject;

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

    	try {
    	
    		BufferedReader buffer_reader = new BufferedReader(new InputStreamReader(robot_messages, Charset.forName("UTF-8")));
    		String jsonText = readAll(buffer_reader);

    		/* message in formato {cluster_id: { robot_id:123 ; signal:01 ; area:123 }   */

    		JSONObject message = new JSONObject(jsonText);
    		//System.out.println(message);
    		counter++;
    		if (counter % 1000 == 0) {
    			System.out.println(counter);
    		}
    		String response = "This is the response";
    		exchange.sendResponseHeaders(200, response.getBytes().length);
    		OutputStream os = exchange.getResponseBody();
    		os.write(response.getBytes());
    		os.close();
    	}
    	catch (JSONException e) {
    		e.printStackTrace();
    	}
    	finally {
    		robot_messages.close();
    	}
    }

    // Function to return a string from given json.
    private static String readAll(Reader reader) throws IOException {
    	StringBuilder string_builder = new StringBuilder();
    	int cp;
    	while ((cp = reader.read()) != -1) {
    		string_builder.append((char) cp);
    	}
    	return string_builder.toString();
	}
}
