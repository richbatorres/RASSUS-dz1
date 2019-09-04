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
		return true;
		
	}
	
	@RequestMapping(value = "/neighbour/{username}", method = RequestMethod.GET)
	public String searchNeighbour(@RequestParam(value = "username")String username){
		Sensor user = new Sensor();
		for (Sensor s : sensors) {
			if (s.getUsername().equals(username)) user = s;
		}
		int d = 0;
		Sensor closest = new Sensor();
		for (Sensor s : sensors) {
			if (s.getUsername().equals(username)) continue;
			int tempD = distance(user, s);
			if (tempD < d || d == 0) closest = s;
		}
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
	
	private int distance(Sensor user, Sensor s) {
		// TODO Auto-generated method stub
		return 0;
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
