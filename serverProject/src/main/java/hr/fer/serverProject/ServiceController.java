package hr.fer.serverProject;

import java.util.ArrayList;
import java.util.List;

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
	public boolean register(@RequestParam(value = "username")String username, @RequestParam(value = "lat")double latitude, 
			@RequestParam(value = "lon")double longitude, @RequestParam(value = "IP")String IPaddress, @RequestParam(value = "port")int port) {
		
		Sensor sensor = new Sensor(username, latitude, longitude, IPaddress, port);
		if (!sensors.contains(sensor)) {
			sensors.add(sensor);
			return true;
		}else {
			return false;
		}
		
	}
	
	@RequestMapping(value = "/sensors", method = RequestMethod.GET)
	public String listSensors(){
		Sensor sensor = new Sensor("tin", 10.10, 10.10, "10.10.10.10", 8080);
		
		sensors.add(sensor);
		//return new ResponseEntity<List>(sensors, HttpStatus.OK);
		ObjectMapper Obj = new ObjectMapper();
		String jsonStr = null;
		try {
			jsonStr = Obj.writeValueAsString(sensor);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(jsonStr);
		return jsonStr;
	}	
	
}
