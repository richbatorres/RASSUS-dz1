package hr.fer.clientTest;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import hr.fer.serverProject.Sensor;

public class Test {

	public static void main(String[] args) {
		
//		reg();		
//		list();
//		search();
		//store();
		
		String myString = "27,989,50,0,,0,";
		String rcvString = "22,991,65,163,,0,";
		
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
			if (!myList.get(i).equals("")) {
				int prvi = Integer.parseInt(myList.get(i));
				if (!rcvList.get(i).equals("")) {
					int drugi = Integer.parseInt(rcvList.get(i));
					rez.add((prvi + drugi) / 2);
				} else rez.add(prvi);
			} else {
				if (!rcvList.get(i).equals("")) {
					int drugi = Integer.parseInt(rcvList.get(i));
					rez.add(drugi);
				} else rez.add(0);
			}
		}
		for (int i : rez) System.out.println(i);		
	}
	
	public static void reg() {
		final String uri = "http://localhost:8080/serverProject/rest/register";
		
		JSONObject request = new JSONObject();
		request.put("username", "Richbatorres");
		request.put("lon", 20.10);
		request.put("lat", 50.10);
		request.put("IP", "10.20.20.10");
		request.put("port", 5);

		// set headers
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> entity = new HttpEntity<String>(request.toString(), headers);
		RestTemplate restTemplate = new RestTemplate();
		
		ResponseEntity<String> response = restTemplate
				  .exchange(uri, HttpMethod.POST, entity, String.class);
		//JSONObject userJson = new JSONObject(response.getBody());
		System.out.println(response.getBody());
	}
	
	public static void store() {
		final String uri = "http://localhost:8080/serverProject/rest/measurment";
		
		JSONObject request = new JSONObject();
		request.put("username", "Richbatorres");
		request.put("parameter", "Pressure");
		request.put("avgValue", 50.10);

		// set headers
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> entity = new HttpEntity<String>(request.toString(), headers);
		RestTemplate restTemplate = new RestTemplate();
		
		ResponseEntity<String> response = restTemplate
				  .exchange(uri, HttpMethod.POST, entity, String.class);
		//JSONObject userJson = new JSONObject(response.getBody());
		System.out.println(response.getBody());
	}
	
	public static void list() {
		final String uri = "http://localhost:8080/serverProject/rest/sensors";
		
		HttpHeaders headers = new HttpHeaders();
		HttpEntity<String> entity = new HttpEntity<String>("", headers);
		RestTemplate restTemplate = new RestTemplate();
		
		ResponseEntity<String> response = restTemplate
				  .exchange(uri, HttpMethod.GET, entity, String.class);
		//JSONObject userJson = new JSONObject(response.getBody());
		System.out.println(response.getBody());
	}
	
	public static void search() {
		final String uri = "http://localhost:8080/serverProject/rest/neighbour?username=torres";
		
		HttpHeaders headers = new HttpHeaders();
		HttpEntity<String> entity = new HttpEntity<String>("", headers);
		RestTemplate restTemplate = new RestTemplate();
		
		ResponseEntity<String> response = restTemplate
				  .exchange(uri, HttpMethod.GET, entity, String.class);
		//JSONObject userJson = new JSONObject(response.getBody());
		System.out.println(response.getBody());
	}
	
}
