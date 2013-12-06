package com.ttm3.ithouse.gui;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class ThermometerGUI extends JFrame{
	private String id;
	private int curTempreture = 0;
	
	private int number = 0;
	private JPanel contentPane;
	private JTextArea thermometerName;
	private JLabel labelTempreture;
	private JSlider slider;
	
	private IChangeTempreture iChangeTempretureBehavior;
	
	public ThermometerGUI(String id, IChangeTempreture iChangeTempretureBehavior, int number){
		this.id = id;
		this.number = number;
		this.iChangeTempretureBehavior = iChangeTempretureBehavior;
		initGUI();
	}
	
	public interface IChangeTempreture{
		void onValue(double value);
	}
	
	
	private void initGUI(){
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(600, 100 + this.number*120, 400, 100);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		
		thermometerName = new JTextArea(100,1);
		thermometerName.setEditable(false);
		thermometerName.setText(this.id);
		thermometerName.setBounds(10, 10, 350, 16);
		contentPane.add(thermometerName);
		
		JLabel lblNewLabel = new JLabel("Tempreture :");
		lblNewLabel.setBounds(10, 35, 102, 16);
		contentPane.add(lblNewLabel);
		
		slider = new JSlider();
		slider.setValue(curTempreture);
		slider.setMaximum(700);
		slider.setMinimum(-400);
		slider.setBounds(100, 35, 150, 25);
		slider.addChangeListener(new TempretureScrollChange());
		contentPane.add(slider);	
		
		labelTempreture  = new JLabel(""+curTempreture);
		labelTempreture.setBounds(260, 35, 50, 30);
		contentPane.add(labelTempreture);
		
		
	}
	
	 public class TempretureScrollChange implements ChangeListener{
		  public void stateChanged(ChangeEvent ce){
			  int value = slider.getValue();
			  double tempretureDouble = value/10.0;
			  String str = Double.toString(tempretureDouble);
			  labelTempreture.setText(str);
			  if(iChangeTempretureBehavior!=null){
				  iChangeTempretureBehavior.onValue(tempretureDouble);
			  }
		  }
	 }
	
}
