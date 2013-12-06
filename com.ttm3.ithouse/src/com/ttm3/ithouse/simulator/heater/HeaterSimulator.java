package com.ttm3.ithouse.simulator.heater;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import com.ttm3.ithouse.gui.HeaterSimulatorGUI;
import com.ttm3.ithouse.sensor.Heater;

import hydna.ntnu.student.api.HydnaApi;
import hydna.ntnu.student.listener.api.HydnaListener;
import aQute.bnd.annotation.component.Activate;
import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.Reference;

@Component
public class HeaterSimulator{
	private static final int NUMBER_OF_SIMULATORS = 4;
	public static final String DOMAIN = "ithouse.hydna.net/";
	public static final String HEATER_CHANEL = "heater";
	private Map <String, HeaterSimulatorItem> heaterMap;
	
	// Simulator Item 
	private class HeaterSimulatorItem{
		private Heater heater;
		private HeaterSimulatorGUI gui;
		public HeaterSimulatorItem(Heater heater, HeaterSimulatorGUI gui){
			this.heater = heater;
			this.gui = gui;
		}
		public void startGUI(){
			this.gui.setVisible(true);
		}
		
		public Heater getHeater(){
			return this.heater;
		}
		public HeaterSimulatorGUI getGUI(){
			return this.gui;
		}
	}
	
	
	private HydnaApi hydnaSvc;
	private HydnaListener listener;
		
	
	private void allocateSimulators(){
		for(int i=0; i<NUMBER_OF_SIMULATORS; i++){
			String uuid = UUID.randomUUID().toString();
			System.out.println("Heater UUID:" + uuid);
			
			HeaterSimulatorItem heaterSimulatroItem = new HeaterSimulatorItem(new Heater(uuid), 
													new HeaterSimulatorGUI(uuid, i));
			heaterSimulatroItem.startGUI();
			// Add to list of simulators
			heaterMap.put(uuid, heaterSimulatroItem);
			hydnaSvc.sendMessage(Heater.CLIENT_MESSAGE+":" +uuid+ ":" + ":"+Heater.CMD_NEW);
		}
	}

	@Activate
	public void activate() {
		System.out.println("Activate");
		
		// Initialization
		heaterMap = new HashMap<String, HeaterSimulatorItem>();
		
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
				processMessage(msg);
				//System.out.println("got msg: "+msg);
			}
		};
		hydnaSvc = hydnaSvc.generateNewApiInstance();
		hydnaSvc.registerListener(this.listener);
		hydnaSvc.connectChannel(DOMAIN + HEATER_CHANEL, "rwe");
		
		allocateSimulators();
	}
	

	@Reference
	public void setHydnaApi(HydnaApi hydna) {
		System.out.println("SETTING SERVICSE");
		this.hydnaSvc = hydna;
	}
	
	// Message  type:src:dst:cmd
	private void processMessage(String message){
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
			if(msgType.equals(Heater.SERVER_MESSAGE)){
				//find address in array
				HeaterSimulatorItem hsi = heaterMap.get(msgDst);
				if(hsi!=null){
					if(msgCmd.equals(Heater.CMD_STATUS)){
						hydnaSvc.sendMessage(Heater.CLIENT_MESSAGE + ":" + msgDst + ":" + msgSrc + ":" + Heater.CMD_STATUS + "@" + hsi.getHeater().getStatus());
					}else if(msgCmd.equals(Heater.CMD_HEATER_ON)){
						hsi.getGUI().startHeating();
						hsi.getHeater().setHeat(true);
						hydnaSvc.sendMessage(Heater.CLIENT_MESSAGE + ":" + msgDst + ":" + msgSrc + ":" + Heater.CMD_STATUS + "@" + hsi.getHeater().getStatus());
					}else if(msgCmd.equals(Heater.CMD_HEATER_OFF)){
						hsi.getGUI().stopHeating();
						hsi.getHeater().setHeat(false);
						hydnaSvc.sendMessage(Heater.CLIENT_MESSAGE + ":" + msgDst + ":" + msgSrc + ":" + Heater.CMD_STATUS + "@" + hsi.getHeater().getStatus());
					}
				}
			}else if(msgType.equals(Heater.CLIENT_MESSAGE)){
				//System.out.println(Heater.CLIENT_MESSAGE +"(Heater Simulator)" +"-> " +msgType+":"+msgSrc+":"+msgDst+":"+msgCmd);
			}
		}
		
	}

	
}
