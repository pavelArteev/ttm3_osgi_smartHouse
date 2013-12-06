package com.ttm3.ithouse.simulator.thermometer;


import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.ttm3.ithouse.gui.HeaterSimulatorGUI;
import com.ttm3.ithouse.gui.ThermometerGUI;
import com.ttm3.ithouse.sensor.Heater;
import com.ttm3.ithouse.sensor.TemperatureSensor;

import hydna.ntnu.student.api.HydnaApi;
import hydna.ntnu.student.listener.api.HydnaListener;
import aQute.bnd.annotation.component.Activate;
import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.Reference;


@Component
public class ThermometerSimulator {
	private static final int NUMBER_OF_SIMULATORS = 2;
	public static final String DOMAIN = "ithouse.hydna.net/";
	public static final String THERMOMETER_CHANEL = "thermometer";
	public static final String HEATER_CHANEL = "heater";
	
	
	private HydnaApi hydnaSvc;
	private HydnaListener listener;

	private Map <String, ThermometerSimulatorItem> thermometerMap;
	
	// Simulator Item 
	private class ThermometerSimulatorItem{
		private TemperatureSensor tempretureSensor;
		private ThermometerGUI thermometerGUI;
		
		public ThermometerSimulatorItem(TemperatureSensor tempretureSensor, ThermometerGUI thermometerGUI){
			this.tempretureSensor = tempretureSensor;
			this.thermometerGUI = thermometerGUI;
		}
		
		public TemperatureSensor getTempretureSensor(){
			return this.tempretureSensor;
		}
		
		public ThermometerGUI getGUI(){
			return this.thermometerGUI;
		}
		public void showGUI(){
			thermometerGUI.setVisible(true);
		}
	}
	
	
	private void allocateSimulators(){
		for(int i=0; i<NUMBER_OF_SIMULATORS; i++){
			String uuid = UUID.randomUUID().toString();
			System.out.println("Thermometer UUID:" + uuid);
			final TemperatureSensor ts = new TemperatureSensor(uuid);
			ThermometerGUI tgui = new ThermometerGUI(uuid, new ThermometerGUI.IChangeTempreture() {
				@Override
				public void onValue(double value) {
					ts.setCurrentValue(value);
				}
			}, i);
			
			ThermometerSimulatorItem tsi = new ThermometerSimulatorItem(ts, tgui);
			tsi.showGUI();
			
			thermometerMap.put(uuid, tsi);
			hydnaSvc.sendMessage(TemperatureSensor.CLIENT_MESSAGE+":" +uuid+ ":" + ":"+TemperatureSensor.CMD_NEW);
		}
	}
	
	@Activate
	public void activate() {
		thermometerMap = new HashMap<String, ThermometerSimulatorItem>();
		hydnaSvc = hydnaSvc.generateNewApiInstance();
		this.listener = new HydnaListener() {
			
			@Override
			public void systemMessage(String msg) {
				
				//System.out.println("got sysmsg: "+msg);
			}
			
			@Override
			public void signalRecieved(String msg) {
				//System.out.println("got signal: "+msg);
			}
			
			@Override
			public void messageRecieved(String msg) {
				//System.out.println("got msg: "+msg);
				processMessage(msg);
				
			}
		};
		
		hydnaSvc.registerListener(this.listener);
		hydnaSvc.connectChannel(DOMAIN + THERMOMETER_CHANEL, "rwe");

		allocateSimulators();
	}
	
	
	@Reference
	public void setHydnaApi(HydnaApi hydna) {
		System.out.println("Thermometer simulator activated...");
		this.hydnaSvc = hydna;
	}
	
	
	// Message  type:src:dst:cmd
	private void processMessage(String message){
			//System.out.println("Thermometer msg: " + message);
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
				if(msgType.equals(TemperatureSensor.SERVER_MESSAGE)){
					//find address in array
					
					ThermometerSimulatorItem tsi = thermometerMap.get(msgDst);
					if(tsi!=null){
						//System.out.println("msgCmd: " + msgCmd);
						if(msgCmd.contains(TemperatureSensor.CMD_STATUS)){
							//System.out.println("222asdfas");
							double tempretureValue = tsi.getTempretureSensor().getCurrentValue();
							//System.out.println("Send tempreture: " + tempretureValue);
							hydnaSvc.sendMessage(TemperatureSensor.CLIENT_MESSAGE+":" +msgDst+ ":" + msgSrc+":"+TemperatureSensor.CMD_STATUS+"@"+tempretureValue);
						}
					}
				}else if(msgType.equals(TemperatureSensor.CLIENT_MESSAGE)){
					//System.out.println(CLIENT_MESSAGE +"(Thermometer)-> " +msgType+":"+msgSrc+":"+msgDst+":"+msgCmd);
				}
			}
			
	}
	
}
