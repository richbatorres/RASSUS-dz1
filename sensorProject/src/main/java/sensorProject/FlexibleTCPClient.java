package sensorProject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Stopwatch;

/**
*
* @author Tin Barić
*/
public class FlexibleTCPClient implements Runnable{
	
	private String username;
	private HashMap<String, String> neighbour;

	private List<String> mjerenja;
	
	public FlexibleTCPClient(String username, List<String> mjerenja) {
		this.username = username;
		this.mjerenja = mjerenja;
	}

	public void run() {          

		Stopwatch stopwatch = Stopwatch.createStarted();

		try (Socket clientSocket = new Socket(neighbour.get("ip"), Integer.parseInt(neighbour.get("port")));/*SOCKET->CONNECT*/
				// get the socket's output stream and open a PrintWriter on it
				PrintWriter outToServer = new PrintWriter(new OutputStreamWriter(
						clientSocket.getOutputStream()), true);
				// get the socket's input stream and open a BufferedReader on it
				BufferedReader inFromServer = new BufferedReader(new InputStreamReader(
						clientSocket.getInputStream()));) {
			
			BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
			String inputString;
			System.out.println("Insert new line of text: ");
			
			while ((inputString = in.readLine()) != null && inputString.length() != 0) {
      		    // send a String then terminate the line and flush
		    	outToServer.println(inputString);//WRITE
		    	System.out.println("TCPClient sent: " + inputString);
		    	if (inputString.equals(Commands.SHUTDOWN.toString())) break;
		    	// read a line of text received from server
				String rcvString = inFromServer.readLine();//READ
				System.out.println("TCPClient received: " + rcvString);
				if (rcvString.equals("404")) continue;
				int redniBroj = (int) ((int)((int)stopwatch.elapsed(TimeUnit.SECONDS) % 100) + 2);
				String myString = mjerenja.get(redniBroj);
				calculateAndStore(rcvString, myString);
			}
		}catch (IOException ex) {
			System.err.println("Exception caught when opening the socket or trying to read data: " + ex);
			System.exit(1);
		}
	}
	
	void findNeigbour() {
		String result;
		while ((result = search(username)).equals("failed")) {
			System.out.println("Could not find neighbour, trying again in 5 seconds");
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		System.out.println("Succesfully found neighbour!");

		ObjectMapper mapper = new ObjectMapper();
		try {
			neighbour = mapper.readValue(result, new TypeReference<Map<String, String>>() {});
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Closest neighbour: " + neighbour.toString());
	}

	private void calculateAndStore(String rcvString, String myString) {
		String[] myValues = myString.split(",");
		String[] rcvValues = rcvString.split(",");
		List<Integer> rez = new ArrayList<Integer>();
		
		for (int i = 0; i < myValues.length; i++) {
			if (!myValues[i].equals("")) {
				int prvi = Integer.parseInt(myValues[i]);
				if (!rcvValues[i].equals("")) {
					int drugi = Integer.parseInt(rcvValues[i]);
					rez.add((prvi + drugi) / 2);
				} else rez.add(prvi);
			} else {
				if (!rcvValues[i].equals("")) {
					int drugi = Integer.parseInt(rcvValues[i]);
					rez.add(drugi);
				} else rez.add(0);
			}
		}
		
		final String uri = "http://localhost:8080/serverProject/rest/measurment";
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		RestTemplate restTemplate = new RestTemplate();
		
		String[] params = mjerenja.get(0).split(",");
		for (int i=0; i<rez.size(); i++) {
			JSONObject request = new JSONObject();
			request.put("username", username);
			request.put("parameter", params[i]);
			request.put("avgValue", rez.get(i));
			
			HttpEntity<String> entity = new HttpEntity<String>(request.toString(), headers);
			ResponseEntity<String> response = restTemplate
					  .exchange(uri, HttpMethod.POST, entity, String.class);
			if (Boolean.parseBoolean(response.getBody())) System.out.println("Measurment stored succesfully!");
			else System.out.println("Measurment not stored succesfully!");
		}
		
	}

	public static String search(String username) {
		final String uri = "http://localhost:8080/serverProject/rest/neighbour?username=" + username;
		
		HttpHeaders headers = new HttpHeaders();
		HttpEntity<String> entity = new HttpEntity<String>("", headers);
		RestTemplate restTemplate = new RestTemplate();
		
		ResponseEntity<String> response = restTemplate
				  .exchange(uri, HttpMethod.GET, entity, String.class);
		return response.getBody();
	}
	
}
