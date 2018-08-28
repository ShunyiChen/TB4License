package com.maxtree.tb4license.server;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.UnknownHostException;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import com.maxtree.tb4license.client.Test;

import javafx.util.Callback;
import net.sourceforge.jdatepicker.impl.JDatePanelImpl;
import net.sourceforge.jdatepicker.impl.JDatePickerImpl;
import net.sourceforge.jdatepicker.impl.UtilDateModel;


/**
 * 
 * License generator
 * 
 * @author chens
 *
 */
public class App implements ActionListener
{
	public App() {
		
    	f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    	JPanel main = new JPanel();
    	main.setLayout(new BorderLayout());
    	
    	JPanel grid = new JPanel();
    	grid.setLayout(new GridLayout(7, 1));

    	// logo
    	JLabel logo = new JLabel("Maxtree Software's license");
    	logo.setFont(new Font("Serif", Font.ITALIC, 21));
    	logo.setPreferredSize(new Dimension(main.getWidth(), 53));
    	logo.setForeground(new Color(0, 179, 81));// green
    	logo.setOpaque(true);
    	logo.setBackground(Color.BLACK);
    	logo.setHorizontalAlignment(SwingConstants.CENTER);
    	
    	// MacAddress
    	JPanel mac = new JPanel();
    	mac.setLayout(new FlowLayout(FlowLayout.CENTER));
    	JLabel macLabel = new JLabel("Mac address:");
    	macField.setPreferredSize(new Dimension(260, 23));
    	macLabel.setPreferredSize(new Dimension(100, 23));
    	macLabel.setToolTipText("Mac address");
    	mac.add(macLabel);
    	mac.add(macField);
    	// IP
    	JPanel ip = new JPanel();
    	ip.setLayout(new FlowLayout(FlowLayout.CENTER));
    	JLabel ipLabel = new JLabel("IP address:");
    	ipLabel.setToolTipText("IP address");
    	ipField.setPreferredSize(new Dimension(260, 23));
    	ipLabel.setPreferredSize(new Dimension(100, 23));
    	ip.add(ipLabel);
    	ip.add(ipField);
    	// Hostname
    	JPanel host = new JPanel();
    	host.setLayout(new FlowLayout(FlowLayout.CENTER));
    	JLabel hostLabel = new JLabel("Host name:");
    	hostLabel.setToolTipText("Host name");
    	hostField.setPreferredSize(new Dimension(260, 23));
    	hostLabel.setPreferredSize(new Dimension(100, 23));
    	host.add(hostLabel);
    	host.add(hostField);
    	// disk serial number
    	JPanel disk = new JPanel();
    	disk.setLayout(new FlowLayout(FlowLayout.CENTER));
    	JLabel diskLabel = new JLabel("Disk serial number:");
    	diskLabel.setToolTipText("Disk serial number");
    	diskField.setPreferredSize(new Dimension(260, 23));
    	diskLabel.setPreferredSize(new Dimension(100, 23));
    	disk.add(diskLabel);
    	disk.add(diskField);
    	
    	// motherboard serial number
    	JPanel motherboard = new JPanel();
    	motherboard.setLayout(new FlowLayout(FlowLayout.CENTER));
    	JLabel motherboardLabel = new JLabel("Motherboard serial number:");
    	motherboardLabel.setToolTipText("Motherboard serial number");
    	motherboardField.setPreferredSize(new Dimension(260, 23));
    	motherboardLabel.setPreferredSize(new Dimension(100, 23));
    	motherboard.add(motherboardLabel);
    	motherboard.add(motherboardField);
    	
    	// from date to date
    	JPanel date = new JPanel();
    	date.setLayout(new FlowLayout(FlowLayout.CENTER));
    	Date now = new Date();
    	frommodel = new UtilDateModel(now);
    	JDatePanelImpl fromPanel = new JDatePanelImpl(frommodel);
    	JDatePickerImpl fromPicker = new JDatePickerImpl(fromPanel);
    	fromPicker.setPreferredSize(new Dimension(150, 29));
    	
    	tomodel = new UtilDateModel(now);
    	JDatePanelImpl toPanel = new JDatePanelImpl(tomodel);
    	JDatePickerImpl toPicker = new JDatePickerImpl(toPanel);
    	toPicker.setPreferredSize(new Dimension(150, 29));
    	
    	JLabel fromLabel = new JLabel("From:");
    	JLabel toLabel = new JLabel("To:");
    	date.add(fromLabel);
    	date.add(fromPicker);
    	date.add(toLabel);
    	date.add(toPicker);
    	
    	grid.add(mac);
    	grid.add(ip);
    	grid.add(host);
    	grid.add(disk);
    	grid.add(motherboard);
    	grid.add(date);
    	
    	// buttons
    	JButton verify = new JButton("Verify");
    	verify.setActionCommand("verifying");
    	verify.addActionListener(this);
//    	JButton get = new JButton("Get system information");
//    	get.setActionCommand("getting");
//    	get.addActionListener(this);
    	JButton generate = new JButton("Generate");
    	generate.setActionCommand("generating");
    	generate.addActionListener(this);
    	JPanel bottom = new JPanel();
    	bottom.setLayout(new FlowLayout(FlowLayout.RIGHT));
    	
    	bottom.add(generate);
    	bottom.add(verify);
    	
    	main.add(logo, BorderLayout.NORTH);
    	main.add(grid, BorderLayout.CENTER);
    	main.add(bottom, BorderLayout.SOUTH);
    	
    	f.getContentPane().add(main);
    	f.pack();
    	f.setLocationRelativeTo(null);
    	f.setVisible(true);
	}
	
	public void actionPerformed(ActionEvent e) {
		if(e.getActionCommand().equals("generating")) {
			
			if (!compareDates()) {
				JOptionPane.showMessageDialog(f,
						"End date must be greater than from date.", "null value",
						JOptionPane.INFORMATION_MESSAGE);
				return;
			}
			
			LicenseCheckModel m = new LicenseCheckModel(macField.getText(), ipField.getText(), hostField.getText(), diskField.getText(), motherboardField.getText(), frommodel.getValue(), tomodel.getValue());
			
			// args ok, run program
			new LicenseServer("output", "Customer", m);
			
		} else if(e.getActionCommand().equals("verifying")) {
			Test test = new Test();
			if(test.verify()) {
				JOptionPane.showMessageDialog(f, 
			            "Congratulation! You have verified successfully.",
			            "Message",
			            JOptionPane.INFORMATION_MESSAGE);
			} else {
				JOptionPane.showMessageDialog(f, 
			            "The license has failed verification.\nNote:Please ensure the license file located in the \"output\" folder.",
			            "Error",
			            JOptionPane.ERROR_MESSAGE);
			}
		}
	}
	
	/**
	 * 
	 * @return
	 */
	private boolean compareDates() {
		if(frommodel.getValue().getTime() >= tomodel.getValue().getTime()) {
			return false;
		} 
		return true;
	}
	
	/**
	 * 
	 */
	public void populate() {
		Getinfo getinfo = new Getinfo();
		macField.setText(getinfo.getMacAddress());
		try {
			ipField.setText(getinfo.getLocalHostLANAddress());
		} catch (UnknownHostException e1) {
			e1.printStackTrace();
		}
		hostField.setText(getinfo.getComputerName());
		diskField.setText(getinfo.getDriveSerialNumber("C"));
		motherboardField.setText(getinfo.getMotherboardSN());
	}
	
	/**
	 * 
	 * @param args
	 */
	public static void main(String[] args){
		Callback callback = new Callback() {
			public Object call(Object param) {
				final App app = new App();
				
				Thread t = new Thread() {
					public void run() {
						app.populate();
					}
				};
				t.start();
				
				return null;
			}
		};
		LoginDialog login = new LoginDialog();
		login.showDialog(callback);
    }

	private JFrame f = new JFrame("License generator v1.2");
	private JTextField macField = new JTextField();
	private JTextField ipField = new JTextField();
	private JTextField hostField = new JTextField();
	private JTextField diskField = new JTextField();
	private JTextField motherboardField = new JTextField();
	private UtilDateModel frommodel;
	private UtilDateModel tomodel;
}

