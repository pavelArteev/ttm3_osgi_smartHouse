package com.ttm3.ithouse.sensor;

import java.util.Date;

public class Heater {
	final public static String CMD_NEW = "cmd_new";
	final public static String CMD_STATUS = "cmd_status";
	final public static String CMD_HEATER_ON = "cmd_heater_on";
	final public static String CMD_HEATER_OFF = "cmd_heater_off";
	final public static String SERVER_MESSAGE = "server";
	final public static String CLIENT_MESSAGE = "client";
	
	private String id;
	private long lastUpdateTime = 0;
	private boolean heating;
	private boolean _available = false;
	
	public void setID(String id){
		this.id = id;
	}
	public String getID(){
		return this.id;
	}
	
	public void setAvaileble(boolean val){
		this._available = val;
	}
	public boolean getAvaileble(){
		return this._available;
	}
	public String getHeaterId(){
		return this.id;
	}

	public long getLastUpdate(){
		return lastUpdateTime;
	}
	
	public void setHeat(boolean val){
		this.heating = val;
		this.lastUpdateTime = new Date().getTime();
		
	}
	
	public boolean isHeating(){
		return this.heating;
	}
	public String getStatus(){
		if(this.heating){
			return "true";
		}else{
			return "false";
		}
	}
	public Heater(){
		
	}
	
	public Heater(String id){
		this.id = id;
		this.heating = false;
	}
}
