package sensorProject;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
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
	private int port;
	private HashMap<String, String> neighbour;

	private List<String> mjerenja;
	
	public FlexibleTCPClient(String username, List<String> mjerenja, int port) {
		this.username = username;
		this.mjerenja = mjerenja; 
		this.port = port;
	}

	public void run() {          

		Stopwatch stopwatch = Stopwatch.createStarted();

		//String inputString;

		//		try (Scanner in = new Scanner(System.in);){
		//			while (!(inputString = in.nextLine()).equals("find") && inputString.length() != 0
		//					&& inputString != null) {
		//				System.out.println("Wrong command!");
		//			}


		
//		if (this.neighbourPort == 0) {
//			String neighbour;
//			while ((neighbour = search(username)).equals("failed")) {
//				System.out.println("Could not find neighbour, trying again in 5 seconds");
//				try {
//					Thread.sleep(5000);
//				} catch (InterruptedException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//			}
//			System.out.println("Succesfully found neighbour!");
//
//			System.out.println("Closest neighbour: " + neighbour);
//			JSONObject obj = new JSONObject(neighbour);
//			String serverName = (String) obj.get("ip");
//			this.neighbourPort = (int) obj.get("port");
//		}

		// create a client socket and connect it to the name server on the specified port number
		Socket clientSocket = null;/*SOCKET->CONNECT*/
		DataOutputStream outToServer = null;
		DataInputStream inFromServer = null;

		// get the socket's output stream and open a PrintWriter on it
		//			PrintWriter outToServer = new PrintWriter(new OutputStreamWriter(
		//					clientSocket.getOutputStream()), true);

		// get the socket's input stream and open a BufferedReader on it
		//			BufferedReader inFromServer = new BufferedReader(new InputStreamReader(
		//					clientSocket.getInputStream()));

		try {
			System.out.println("Insert command: ");
			Scanner in = new Scanner(System.in);
			String inputString;
			while (in.hasNext()) {
				inputString = in.next();
				if (inputString.equals(Commands.FIND)) {
					findNeigbour(username);
					clientSocket = new Socket(neighbour.get("ip"), Integer.parseInt(neighbour.get("port")));
					outToServer = new DataOutputStream(clientSocket.getOutputStream());
					inFromServer = new DataInputStream(clientSocket.getInputStream());
				}else if (inputString.equals(Commands.REQUEST)) {
					request(outToServer, inFromServer, inputString, stopwatch);
				}else if (inputString.equals(Commands.SHUTDOWN)) {
					break;
				}

				// send a String then terminate the line and flush
				//					outToServer.writeUTF(inputString);//WRITE
				//					outToServer.flush();
				//					System.out.println("TCPClient sent: " + inputString);
				//					// read a line of text received from server
				//					String rcvString = inFromServer.readLine();//READ
				//					System.out.println("TCPClient received: " + rcvString);
				//					if (rcvString.equals("404")) continue;
				//					int redniBroj = (int) ((stopwatch.elapsed(TimeUnit.SECONDS) % 100) + 2);
				//					String myString = mjerenja.get(redniBroj);
				//					calculateAndStore(rcvString, myString);
				//					if (inputString.equals("shutdown"))
				//						break;
				System.out.println("Insert command: ");
			}
			in.close();
		} catch(IOException ex) {
			System.err.println("Exception caught when opening the socket or trying to read data: " + ex);
			System.exit(1);
		}
		//}
	}
	
	private void request(DataOutputStream outToServer, DataInputStream inFromServer, String inputString, Stopwatch stopwatch) {
		// send a String then terminate the line and flush
		try {
			outToServer.writeUTF(inputString);
			//WRITE
			outToServer.flush();
			System.out.println("TCPClient sent: " + inputString);
			// read a line of text received from server
			String rcvString = inFromServer.readLine();//READ
			System.out.println("TCPClient received: " + rcvString);
			if (rcvString.equals("404")) return;
			int redniBroj = (int) ((stopwatch.elapsed(TimeUnit.SECONDS) % 100) + 2);
			String myString = mjerenja.get(redniBroj);
			calculateAndStore(rcvString, myString);
		} catch(IOException ex) {
			System.err.println("Exception caught when opening the socket or trying to read data: " + ex);
			System.exit(1);
		}
	}

	private void calculateAndStore(String rcvString, String myString) {
		List<String> myList = new ArrayList<String>();
		List<String> rcvList = new ArrayList<String>();
		List<Integer> rez = new ArrayList<Integer>();		
		
		for (int i = 0; i < myString.split(",").length; i++) {
			myList.add(myString.split(",")[i]);
		}
		for (int i = 0; i < rcvString.split(",").length; i++) {
			rcvList.add(rcvString.split(",")[i]);
		}
		
		for (int i = 0; i < myList.size(); i++) {
			if (myList.get(i) != "") {
				int prvi = Integer.parseInt(myList.get(i));
				if (rcvList.get(i) != "") {
					int drugi = Integer.parseInt(rcvList.get(i));
					rez.add((prvi + drugi) / 2);
				} else rez.add(prvi);
			} else {
				if (rcvList.get(i) != "") {
					int drugi = Integer.parseInt(rcvList.get(i));
					rez.add(drugi);
				} else rez.add(0);
			}
		}
		
		final String uri = "http://localhost:8080/serverProject/rest/measurment";
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		RestTemplate restTemplate = new RestTemplate();
		
		String[] params = mjerenja.get(0).split(",");
		//Map<String, Integer> paramsAndValues = new HashMap<String, Integer>();
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
		//JSONObject userJson = new JSONObject(response.getBody());
		return response.getBody();
	}
	
}
