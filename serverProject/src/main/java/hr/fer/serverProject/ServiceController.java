package hr.fer.serverProject;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
public class ServiceController {

	private static final String SUCCESS_STATUS = "success";
	private static final String ERROR_STATUS = "error";
	private static final int POST_CODE_SUCCESS = 100;
	private static final int AUTH_FAILURE = 102;
	public List<Sensor> sensors = new ArrayList<Sensor>();
	public List<String> usernames = new ArrayList<String>();
	
	@RequestMapping(value = "/register", method = RequestMethod.POST)
	public boolean register(@RequestBody String jsonStr) {
		
		JSONObject jsonObject = new JSONObject(jsonStr);
		String username = jsonObject.getString("username");
		double lat = jsonObject.getDouble("lat");
		double lon = jsonObject.getDouble("lon");
		String ip = jsonObject.getString("IP");
		int port = jsonObject.getInt("port");
		for (Sensor sensor : sensors) {
			if (sensor.getUsername().equals(username)) {
				return false;
			} 
		}
		Sensor sensor = new Sensor(username, lat, lon, ip, port);
		sensors.add(sensor);
		usernames.add(username);
		return true;
		
	}
	
	@RequestMapping(value = "/measurment", method = RequestMethod.POST)
	public boolean storeMeasurment(@RequestBody String jsonStr) {

		JSONObject jsonObject = new JSONObject(jsonStr);
		if (jsonObject.has("username") && jsonObject.has("parameter") && jsonObject.has("avgValue")) {
			String username;
			if (usernames.contains(jsonObject.getString("username"))) {
				username = jsonObject.getString("username");
			}else return false;
			String parameter = jsonObject.getString("parameter");
			float avgValue = jsonObject.getFloat("avgValue");
			System.out.println(username + parameter + avgValue);
			return true;
		}else return false;
		
	}
	
	@RequestMapping(value = "/deletion", method = RequestMethod.POST)
	public boolean delete(@RequestBody String jsonStr) {

		JSONObject jsonObject = new JSONObject(jsonStr);
		if (jsonObject.has("username")) {
			String username= jsonObject.getString("username");
			for (Sensor s : sensors) {
				if (s.getUsername().equals(username)) {
					sensors.remove(s);
					usernames.remove(username);
					return true;
				}
			}
		}
		return false;
		
	}
	
	@RequestMapping(value = "/neighbour", method = RequestMethod.GET)
	public String searchNeighbour(@RequestParam(value = "username")String username){
		Sensor user = new Sensor();
		for (Sensor s : sensors) {
			if (s.getUsername().equals(username)) user = s;
		}
		double d = 0;
		Sensor closest = null;
		for (Sensor s : sensors) {
			if (s.getUsername().equals(username)) continue;
			double tempD = distance(user, s);
			if (tempD < d || d == 0) {
				closest = s;
				System.out.println("Distance between " + user.getUsername() + " and " + s.getUsername() + " is " + tempD);
			}
		}
		if (closest == null) return "failed";
		UserAddress address = new UserAddress(closest.getIPaddress(), closest.getPort());
		ObjectMapper Obj = new ObjectMapper();
		try {
			return Obj.writeValueAsString(address);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "failed";
	}
	
	private double distance(Sensor s1, Sensor s2) {
		
		int r = 6371;
		double dlon = s2.getLongitude() - s1.getLongitude();
		double dlat = s2.getLatitude() - s1.getLatitude();
		double a = Math.pow(Math.sin(dlat/2), 2) + Math.cos(s1.getLatitude()) * Math.cos(s2.getLatitude()) * Math.pow(Math.sin(dlon/2), 2);
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
		return r*c;
		
	}

	@RequestMapping(value = "/sensors", method = RequestMethod.GET)
	public String listSensors(){
		//return new ResponseEntity<List>(sensors, HttpStatus.OK);
		ObjectMapper Obj = new ObjectMapper();
		String jsonStr = null;
		try {
			jsonStr = Obj.writeValueAsString(sensors);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		//System.out.println(jsonStr);
		return jsonStr.toString();
	}	
	
}
