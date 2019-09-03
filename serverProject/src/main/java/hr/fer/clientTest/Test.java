package hr.fer.clientTest;

import java.util.List;

import org.springframework.web.client.RestTemplate;

import hr.fer.serverProject.Sensor;

public class Test {

	public static void main(String[] args) {
		
		final String uri = "http://localhost:8080/serverProject/rest/sensors";
		
		JSONObject request = new JSONObject();
		request.put("username", "tin");
		request.put("lat", "10.10");
		request.put("lon", "10.10");
		request.put("IP", "10.10.10.10");
		request.put("lat", "10.10");
		
		RestTemplate restTemplate = new RestTemplate();
		String sensor = restTemplate.getForObject(uri, String.class);
		/*for (Sensor sensor:sensors) {
			System.out.println(sensor.toString());
		}*/
		System.out.println(sensor);
		
	}
	
}
