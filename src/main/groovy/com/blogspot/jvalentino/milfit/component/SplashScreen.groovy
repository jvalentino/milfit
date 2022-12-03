package com.blogspot.jvalentino.milfit.component

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.net.URL;

import javax.swing.JFrame;

class SplashScreen extends JFrame {
	
	private static final long serialVersionUID = 1L;
	private Image image;
	
	public SplashScreen(String imageString) {
		URL imageURL = SplashScreen.class.getResource(imageString);
		image = Toolkit.getDefaultToolkit().getImage(imageURL);
		
	}
	
	public void paint(Graphics graphics) {
		if (image != null) {
			graphics.drawImage(image, 0, 0, this);
		}
	}

}
