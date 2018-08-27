package com.maxtree.tb4license.server;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import org.mindrot.jbcrypt.BCrypt;

import javafx.util.Callback;

public class LoginDialog extends JDialog implements ActionListener{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * 
	 */
	public LoginDialog() {
		this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		JPanel passwordPane = new JPanel();
		passwordPane.add(passwordLabel);
		passwordPane.add(passwordField);
		passwordPane.add(decrypt);
		passwordField.setPreferredSize(new Dimension(200, 25));
		decrypt.addActionListener(this);
		JPanel main = new JPanel();
		main.setLayout(new VerticalFlowLayout());
		main.add(passwordPane);
		this.setContentPane(main);
	}
	
	/**
	 * 
	 * @param callback
	 */
	public void showDialog(Callback<String, ?> callback) {
		this.callback = callback;
		this.setSize(new Dimension(400, 85));
		this.setLocationRelativeTo(null);
		this.setVisible(true);
	}
	
	public void actionPerformed(ActionEvent e) {
		 String hashed = "$2a$04$Cl/Z7WgQeJ8HSZd/yc4qyOQ5gT5DcK9dBvneqi.JjgZS1SYgQu5uq";
		 char[] chs = passwordField.getPassword();
		// Check that an unencrypted password matches one that has
			// previously been hashed
		if (BCrypt.checkpw(new String(chs), hashed)) {
			this.dispose();
			callback.call("");
		} else {
			JOptionPane.showMessageDialog(this, "密钥不对。");
		}
	}
	
	private Callback<String, ?> callback;
	private JLabel passwordLabel = new JLabel("密钥:");
	private JPasswordField passwordField = new JPasswordField();
	private JButton decrypt = new JButton("解密");
	
}
