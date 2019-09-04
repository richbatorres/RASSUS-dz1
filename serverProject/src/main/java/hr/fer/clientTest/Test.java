package hr.fer.clientTest;

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
		
		reg();		
		list();
		
	}
	
	public static void reg() {
		final String uri = "http://localhost:8080/serverProject/rest/register";
		
		JSONObject request = new JSONObject();
		request.put("username", "Richbatorres");
		request.put("lon", 10.10);
		request.put("lat", 10.10);
		request.put("IP", "10.10.10.10");
		request.put("port", 10);

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
	
}
