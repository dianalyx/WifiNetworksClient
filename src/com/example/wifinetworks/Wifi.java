package com.example.wifinetworks;

import java.io.Serializable;

public class Wifi implements Serializable {
	
	private double lat;
	private double lon;
	private String ssid;
	
	public Wifi(String ssid){
		this.lat = 0;
		this.lon = 0;
		this.ssid = ssid;
	}

	public double getLat() {
		return lat;
	}

	public void setLat(double lat) {
		this.lat = lat;
	}

	public double getLon() {
		return lon;
	}

	public void setLon(double lon) {
		this.lon = lon;
	}

	public String getSsid() {
		return ssid;
	}

	public void setSsid(String ssid) {
		this.ssid = ssid;
	}

}
