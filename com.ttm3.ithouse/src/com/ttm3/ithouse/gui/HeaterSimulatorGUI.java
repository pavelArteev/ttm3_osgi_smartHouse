package com.ttm3.ithouse.gui;



import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;


public class HeaterSimulatorGUI extends JFrame {

	/**
	 * 
		*/
	private int number = 0;
	
	private static final long serialVersionUID = 1L;
	private String heaterId;
	private JPanel contentPane;
	private JLabel statusDisplayer;
	private JTextArea heaterName;

	final static private String STATUS_HEATING_ON = "Heating...";
	final static private String STATUS_HEATING_OFF = "Not heating...";
	/**
	 * Create the frame.
	 */
	public HeaterSimulatorGUI(String heaterId, int number) {
		this.number = number;
		this.heaterId = heaterId;
		initGUI();
	}
	
	private void initGUI(){
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100 + this.number*120, 400, 100);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		heaterName = new JTextArea(100,1);
		heaterName.setEditable(false);
		heaterName.setText(heaterId);
		heaterName.setBounds(10, 10, 350, 16);
		contentPane.add(heaterName);
		
		JLabel lblNewLabel = new JLabel("Heater Status:");
		lblNewLabel.setBounds(10, 35, 102, 16);
		contentPane.add(lblNewLabel);
		
		statusDisplayer = new JLabel(STATUS_HEATING_OFF);
		statusDisplayer.setBounds(110, 35, 140, 16);
		contentPane.add(statusDisplayer);
		
		
		
	}
	
	public void startHeating(){
		statusDisplayer.setText(STATUS_HEATING_ON);
	}
	
	public void stopHeating(){
		statusDisplayer.setText(STATUS_HEATING_OFF);
	}
}
