package hr.fer.serverProject;

import org.springframework.web.bind.annotation.RequestParam;

public class Sensor {
	
	private String username;
	private double latitude;
	private double longitude;
	private String IPaddress;
	private int port;

	public Sensor(String username, double latitude, double longitude, String IPaddress, int port) {
		this.username = username;
		this.latitude = latitude;
		this.longitude = longitude;
		this.IPaddress = IPaddress;
		this.port = port;
	}
	
	public Sensor() {}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public String getIPaddress() {
		return IPaddress;
	}

	public void setIPaddress(String iPaddress) {
		IPaddress = iPaddress;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}
	
}
