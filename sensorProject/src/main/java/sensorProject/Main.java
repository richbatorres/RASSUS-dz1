package sensorProject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class Main {
	
	private final static double LAT_MIN = 45.75;
	private final static double LAT_MAX = 45.85;
	private final static double LON_MIN = 15.87;
	private final static double LON_MAX = 16;
	static List<String> mjerenja = new ArrayList<String>();

	public static void main(String[] args) {
		Main main = new Main();
        File file = main.getFileFromResources("mjerenja.csv");
		try (BufferedReader br = new BufferedReader(new FileReader(file))){
			String line;
			while ((line = br.readLine()) != null) {
				mjerenja.add(line);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Random r = new Random();
		double lat = LAT_MIN + (LAT_MAX - LAT_MIN) * r.nextDouble();
		double lon = LON_MIN + (LON_MAX - LON_MIN) * r.nextDouble();
		
		Scanner scan = new Scanner(System.in);  // Create a Scanner object
	    System.out.println("Enter username:");
	    String username = scan.nextLine();
	    System.out.println("Enter port:");
	    int port = scan.nextInt();
		
		reg(username, lon, lat, port);
		
		MultithreadedServer server = new MultithreadedServer(username, port, mjerenja);
		Thread t1 = new Thread(server, "t1");
		t1.start();
		FlexibleTCPClient client = new FlexibleTCPClient(username, mjerenja/*, port*/);
		System.out.println("Insert command: ");
		String inputString;
		while (scan.hasNext()) {
			inputString = scan.next();
			if (inputString.equals(Commands.FIND.toString())) {
				client.findNeigbour();
			}else if (inputString.equals(Commands.START.toString())) {
				Thread t2 = new Thread(client, "t2");
				t2.start();
			}
			System.out.println("Insert command: ");
		}
		scan.close();
	}
	
	private File getFileFromResources(String fileName) {

        //ClassLoader classLoader = getClass().getClassLoader();

        String resource = "C:\\Users\\ebrctnx\\OneDrive - fer.hr\\kolegiji\\RASSUS\\DZ\\RASSUS-dz1\\sensorProject\\target\\classes\\mjerenja.csv";
//        if (resource == null) {
//            throw new IllegalArgumentException("file is not found!");
//        } else {
            return new File(resource);
//        }

    }

	public static void reg(String username, double lon, double lat, int port) {
		
		final String regUri = "http://localhost:8080/serverProject/rest/register";
		
		JSONObject request = new JSONObject();
		request.put("username", username);
		request.put("lon", lon);
		request.put("lat", lat);
		request.put("IP", "localhost");
		request.put("port", port);

		// set headers
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> entity = new HttpEntity<String>(request.toString(), headers);
		RestTemplate restTemplate = new RestTemplate();
		
		ResponseEntity<String> response = restTemplate
				  .exchange(regUri, HttpMethod.POST, entity, String.class);
		if (!Boolean.parseBoolean(response.getBody())) {
			System.exit(1);
		}
		
	}
}
