package com.ttm3.ithouse.heatingManager;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


import hydna.ntnu.student.api.HydnaApi;
import hydna.ntnu.student.listener.api.HydnaListener;
import aQute.bnd.annotation.component.Activate;
import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.Reference;


@Component
public class HeatingManager {
	private static final int BINDINGS_AMOUNT = 4;
	
	public static final String DOMAIN = "ithouse.hydna.net/";
	public static final String HEATER_CHANEL = "heater";
	public static final String THERMOMETER_CHANEL = "thermometer";
	public static final String AIR_CONDITION = "air_condition";
	
	private HydnaApi hydnaSvc;
	private HydnaApi hydnaThermometerChannel;
	private HydnaApi hydnaHeaterChannel;
	
	private HydnaListener thermometerListener;
	private HydnaListener heaterListener;
	
	private Map <String, HeaterBinding> heaterBindingMap;
	
	
	
	private void activateHeaterBinding(HydnaApi hydnaThermometerChannel, HydnaApi hydnaHeaterChannel){
		heaterBindingMap = new HashMap<String, HeaterBinding>();
		
		for(int i=0; i<BINDINGS_AMOUNT; i++){
			String uuid = UUID.randomUUID().toString();
			HeaterBinding heaterBinding = new HeaterBinding(uuid, hydnaThermometerChannel, hydnaHeaterChannel);
		
			heaterBindingMap.put(uuid, heaterBinding);
			
		}
	}
	
	
	@Activate
	public void activate() {
		System.out.println("Activate");
		
		
		
		
		
		this.hydnaThermometerChannel = this.hydnaSvc.generateNewApiInstance();
		this.hydnaHeaterChannel = this.hydnaSvc.generateNewApiInstance();
		
		this.thermometerListener =  new HydnaListener() {
			@Override
			public void systemMessage(String msg) {
				//System.out.println("New system message");
			}
			@Override
			public void signalRecieved(String msg) {
				//System.out.println("New signal");
			}
			@Override
			public void messageRecieved(String msg) {
				//System.out.println("New message");
				processThermometerMessage(msg);
			}
		};
		this.heaterListener =  new HydnaListener() {
			@Override
			public void systemMessage(String msg) {
				//System.out.println("New system message");
			}
			@Override
			public void signalRecieved(String msg) {
				//System.out.println("New signal");
			}
			@Override
			public void messageRecieved(String msg) {
				//System.out.println("New message");
				processHeaterMessage(msg);
			}
		};
		
		// Creating hydna channels
		hydnaThermometerChannel.registerListener(this.thermometerListener);
		hydnaThermometerChannel.connectChannel(DOMAIN + THERMOMETER_CHANEL, "rwe");
		
		
		hydnaHeaterChannel.registerListener(this.heaterListener);
		hydnaHeaterChannel.connectChannel(DOMAIN + HEATER_CHANEL, "rwe");
		
		
		activateHeaterBinding(hydnaThermometerChannel, hydnaHeaterChannel);
		
		
	}
		
	@Reference
	public void setHydnaApi(HydnaApi hydna) {
		System.out.println("SETTING SERVICSE");
		this.hydnaSvc = hydna;
	}

	final public static String CMD_NEW = "cmd_new";
	final public static String CMD_STATUS = "cmd_status";
	final private static String SERVER_MESSAGE = "server";
	final private static String CLIENT_MESSAGE = "client";
	
	
	public void processThermometerMessage(String message){
		String[] strArray = {};
		strArray = message.split(":");
		if(strArray.length<4){
			return;
		}else{
			String msgType = strArray[0];
			String msgSrc = strArray[1];
			String msgDst = strArray[2];
			String msgCmd = strArray[3];
			// trying to process message
			if(msgType.equals(CLIENT_MESSAGE)){
				
				HeaterBinding hb = heaterBindingMap.get(msgDst);
				if(hb!=null){
					if(msgCmd.contains(CMD_STATUS+"@")){
						String[] valueArray = msgCmd.split("@");
						try{
							double curTempreture = Double.parseDouble(valueArray[1]);
							hb.getTempretureSensor().setCurrentValue(curTempreture);
						}catch(Exception e){
							
						}
					}
				}
				
				
			}else if(msgType.equals(SERVER_MESSAGE)){
			}
		}
	}
	
	public void processHeaterMessage(String message){
		String[] strArray = {};
		strArray = message.split(":");
		if(strArray.length<4){
			return;
		}else{
			String msgType = strArray[0];
			String msgSrc = strArray[1];
			String msgDst = strArray[2];
			String msgCmd = strArray[3];
			// trying to process message
			if(msgType.equals(CLIENT_MESSAGE)){
				
				HeaterBinding hb = heaterBindingMap.get(msgDst);
				
				if(hb!=null){
					if(msgCmd.contains(CMD_STATUS+"@")){
						String[] valueArray = msgCmd.split("@");
						try{
							if(valueArray[1].equals("true")){
								hb.getHeater().setHeat(true);
							}else{
								hb.getHeater().setHeat(false);
							}
						}catch(Exception e){
							
						}
					}
				}
			}else if(msgType.equals(SERVER_MESSAGE)){
				//System.out.println("msg:" + message);
			}
		}
	}
	
}
