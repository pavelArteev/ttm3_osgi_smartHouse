package com.ttm3.ithouse.gui;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.ttm3.ithouse.gui.ThermometerGUI.TempretureScrollChange;

public class HeatingManagerGUI  extends JFrame {
	private JPanel contentPane;
	private String uuid;
	
	public static final String OFFLINE = "OFFLINE";
	
	private JTextArea textThermometerID;
	private JTextArea textHeaterID;
	private JLabel currentTempreture;
	private JLabel heaterStatus;
	private JSlider tempretureSlider;
	private JLabel labelUserTempreture;
	
	public double getHoldingTempreture(){
		int val = tempretureSlider.getValue();
		return val/10.0;
	}
	
	public void setTempreture(String val){
		currentTempreture.setText(val);
	}
	public void setHeaterStatus(boolean status){
		if(status){
			heaterStatus.setText("ON");
		}else{
			heaterStatus.setText("OFF");
		}
	}
	public void setHeaterStatus(String status){
		heaterStatus.setText(status);
	}
	
	
	public HeatingManagerGUI(String uuid, double defaultTempreture){
		this.uuid = uuid;
		initGUI(defaultTempreture);
	}
	
	public String getThermometerId(){
		return textThermometerID.getText();
	}
	
	public String getHeaterID(){
		return textHeaterID.getText();
	}
	
	private void initGUI(double defaultTempreture){
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(500, 500, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		
		JLabel lblNewLabel = new JLabel(""+this.uuid);
		lblNewLabel.setBounds(10, 5, 300, 20);
		contentPane.add(lblNewLabel);
		// Tempreture sensor
		lblNewLabel = new JLabel("Thermometer");
		lblNewLabel.setBounds(10, 20, 102, 20);
		contentPane.add(lblNewLabel);
		
		lblNewLabel  = new JLabel("Address:");
		lblNewLabel.setBounds(10, 40, 102, 20);
		contentPane.add(lblNewLabel);
		
		textThermometerID = new JTextArea(100, 1); 
		textThermometerID.setBounds(70, 40, 300, 20);
		contentPane.add(textThermometerID);
		
		lblNewLabel = new JLabel("Val :");
		lblNewLabel.setBounds(10, 65, 40, 16);
		contentPane.add(lblNewLabel);
		
		currentTempreture = new JLabel("OFFLINE");
		currentTempreture.setBounds(50, 65, 80, 16);
		contentPane.add(currentTempreture);
		
		// Heater
		lblNewLabel = new JLabel("Heater");
		lblNewLabel.setBounds(10, 100, 102, 20);
		contentPane.add(lblNewLabel);
		
		lblNewLabel  = new JLabel("Address:");
		lblNewLabel.setBounds(10, 130, 102, 20);
		contentPane.add(lblNewLabel);
		
		textHeaterID = new JTextArea(100, 1); 
		textHeaterID.setBounds(70, 130, 300, 20);
		contentPane.add(textHeaterID);
		
		lblNewLabel = new JLabel("Val :");
		lblNewLabel.setBounds(10, 155, 40, 16);
		contentPane.add(lblNewLabel);
		
		heaterStatus = new JLabel("OFFLINE");
		heaterStatus.setBounds(50, 155, 80, 16);
		contentPane.add(heaterStatus);
		
		labelUserTempreture = new JLabel(""+defaultTempreture);
		labelUserTempreture.setBounds(10, 175, 80, 16);
		contentPane.add(labelUserTempreture);
		
		tempretureSlider = new JSlider();
		tempretureSlider.setMaximum(700);
		tempretureSlider.setMinimum(-400);
		tempretureSlider.setValue((int)(defaultTempreture*10));
		tempretureSlider.setBounds(100, 175, 150, 25);
		tempretureSlider.addChangeListener(new TempretureScrollChange());
		contentPane.add(tempretureSlider);	
		
	}
	
	 public class TempretureScrollChange implements ChangeListener{
		  public void stateChanged(ChangeEvent ce){
			  	int value = tempretureSlider.getValue();
			  	double tempretureDouble = value/10.0;
			  	labelUserTempreture.setText(""+ tempretureDouble);
			  }
	 }
	 

}
