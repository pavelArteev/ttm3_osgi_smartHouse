package com.ttm3.ithouse.heatingManager;

import hydna.ntnu.student.api.HydnaApi;

import java.util.Date;

import com.ttm3.ithouse.gui.HeatingManagerGUI;
import com.ttm3.ithouse.sensor.Heater;
import com.ttm3.ithouse.sensor.TemperatureSensor;

class HeaterBinding{
	private String uuid;
	
	public static final String STATE_NORMAL = "state_normal";
	public static final String STATE_HEATING = "state_heating";
	public static final String STATE_COOLING = "state_cooling";
	
	
	private HydnaApi hydnaThermometerChannel;
	private HydnaApi hydnaHeaterChannel;
	
	private TemperatureSensor tempretureSensor;
	private Heater heater;
	

	//HEATER MANAGER PARAMETERS
	private String managerStatus = STATE_NORMAL;
	private double holdTempreture = 15.0;
	private double TEMPRETURE_TRESHOLD = 1.0; // +- TRESHOLD for tempreture
	
	// GUI
	private HeatingManagerGUI heatingManagerGUI;
	
	public HeaterBinding(String uuid, HydnaApi hydnaThermometerChannel, HydnaApi hydnaHeaterChannel){
		this.uuid = uuid;
		this.hydnaThermometerChannel = hydnaThermometerChannel;
		this.hydnaHeaterChannel = hydnaHeaterChannel;
		
		tempretureSensor = new TemperatureSensor();
		heater = new Heater();
		
		this.heatingManagerGUI = new HeatingManagerGUI(this.uuid, holdTempreture);
		this.heatingManagerGUI.setVisible(true);
		
		activateHeaterBinding();
	}
	
	public TemperatureSensor getTempretureSensor(){
		return this.tempretureSensor;
	}
	public Heater getHeater(){
		return this.heater;
	}
	
	public void activateHeaterBinding(){
		// active loop
		new Thread(new Runnable(){

			@Override
			public void run() {
				while(true){
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					//*******************************
					// Getting configuration from GUI
					//*******************************
					if(HeaterBinding.this.heatingManagerGUI!=null){
						HeaterBinding.this.tempretureSensor.setID(HeaterBinding.this.heatingManagerGUI.getThermometerId());
						HeaterBinding.this.heater.setID(HeaterBinding.this.heatingManagerGUI.getHeaterID());
						HeaterBinding.this.holdTempreture = HeaterBinding.this.heatingManagerGUI.getHoldingTempreture();
					}
					//********************************
					// Sending sensors status request
					//********************************
					if(HeaterBinding.this.tempretureSensor.getID().length()>1){
						hydnaThermometerChannel.sendMessage(TemperatureSensor.SERVER_MESSAGE+":"+HeaterBinding.this.uuid+":" + HeaterBinding.this.tempretureSensor.getID() +":"+TemperatureSensor.CMD_STATUS);
					}
					if(HeaterBinding.this.heater.getID().length()>1){
						hydnaHeaterChannel.sendMessage(Heater.SERVER_MESSAGE+":"+HeaterBinding.this.uuid+":" + HeaterBinding.this.heater.getID() +":"+Heater.CMD_STATUS);
					}
					
					//*******************************
					// Check that updates is correct
					//*******************************
					long curTime = new Date().getTime();
					
					
					if(curTime - HeaterBinding.this.tempretureSensor.getLastUpdate() > 3000){
						HeaterBinding.this.tempretureSensor.setAvaileble(false);
						HeaterBinding.this.heatingManagerGUI.setTempreture(HeatingManagerGUI.OFFLINE);
					}else{
						HeaterBinding.this.tempretureSensor.setAvaileble(true);
						HeaterBinding.this.heatingManagerGUI.setTempreture(""+HeaterBinding.this.tempretureSensor.getCurrentValue());
					}
					
					if(curTime - HeaterBinding.this.heater.getLastUpdate() > 3000){
						HeaterBinding.this.heater.setAvaileble(false);
						HeaterBinding.this.heatingManagerGUI.setHeaterStatus(HeatingManagerGUI.OFFLINE);
					}else{
						HeaterBinding.this.heater.setAvaileble(true);
						HeaterBinding.this.heatingManagerGUI.setHeaterStatus(HeaterBinding.this.heater.isHeating());
					}
					
					
					//*******
					// logic
					//*******
					if(!HeaterBinding.this.tempretureSensor.getAvaileble()){
						managerStatus = STATE_NORMAL;
						//check that heater not working
						if(HeaterBinding.this.heater.getAvaileble()){
							if(HeaterBinding.this.heater.isHeating()){
								hydnaHeaterChannel.sendMessage(Heater.SERVER_MESSAGE+":"+HeaterBinding.this.uuid+":" + HeaterBinding.this.heater.getID() +":"+Heater.CMD_HEATER_OFF);
							}
						}
						
						continue;
					}
					
					if(managerStatus.equals(STATE_HEATING)){
						if(HeaterBinding.this.tempretureSensor.getCurrentValue()<holdTempreture){
							//check that heater is working 
							if(HeaterBinding.this.heater.getAvaileble()){
								if(!HeaterBinding.this.heater.isHeating()){
									hydnaHeaterChannel.sendMessage(Heater.SERVER_MESSAGE+":"+HeaterBinding.this.uuid+":" + HeaterBinding.this.heater.getID() +":"+Heater.CMD_HEATER_ON);
								}
							}
						}else{
							managerStatus = STATE_NORMAL;
							if(HeaterBinding.this.heater.getAvaileble()){
								hydnaHeaterChannel.sendMessage(Heater.SERVER_MESSAGE+":"+HeaterBinding.this.uuid+":" + HeaterBinding.this.heater.getID() +":"+Heater.CMD_HEATER_OFF);
							}
						}
					}else{	// NORMAL STATE
						if(HeaterBinding.this.tempretureSensor.getCurrentValue()<holdTempreture-TEMPRETURE_TRESHOLD){
							managerStatus = STATE_HEATING;
							if(HeaterBinding.this.heater.getAvaileble()){
								hydnaHeaterChannel.sendMessage(Heater.SERVER_MESSAGE+":"+HeaterBinding.this.uuid+":" + HeaterBinding.this.heater.getID() +":"+Heater.CMD_HEATER_ON);
							}
						}else{
							//check that heater not working
							if(HeaterBinding.this.heater.getAvaileble()){
								if(HeaterBinding.this.heater.isHeating()){
									hydnaHeaterChannel.sendMessage(Heater.SERVER_MESSAGE+":"+HeaterBinding.this.uuid+":" + HeaterBinding.this.heater.getID() +":"+Heater.CMD_HEATER_OFF);
								}
							}
						}
					}
					
					
				}
			}
			
		}).start();
	}
}