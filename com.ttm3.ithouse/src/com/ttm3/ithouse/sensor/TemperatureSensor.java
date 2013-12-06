package com.ttm3.ithouse.sensor;

import java.util.Date;

public class TemperatureSensor {
	final public static String CMD_NEW = "cmd_new";
	final public static String CMD_STATUS = "cmd_status";
	final public static String SERVER_MESSAGE = "server";
	final public static String CLIENT_MESSAGE = "client";
	
	
	private String id;
	private double currentValue;
	private long lastUpdateTime = 0;
	private boolean _available = false;
	
	public void setAvaileble(boolean val){
		this._available = val;
	}
	public boolean getAvaileble(){
		return this._available;
	}
	
	public long getLastUpdate(){
		return lastUpdateTime;
	}
	
	public TemperatureSensor(){
		
	}
	
	public void setID(String id){
		this.id = id;
	}
	public String getID(){
		return this.id;
	}
	
	public TemperatureSensor(String id){
		this.id = id;
	}
	
	public void setCurrentValue(double value){
		this.currentValue = value;
		this.lastUpdateTime = new Date().getTime();
	}
	
	public double getCurrentValue() {
		return currentValue;
	}
	
}
