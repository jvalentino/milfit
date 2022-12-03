package com.blogspot.jvalentino.milfit.component

import java.awt.BorderLayout;
import java.awt.Dialog;

import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.border.EmptyBorder;

class JProgressDialog extends JDialog {
	
	private JProgressBar progress;
	
	JProgressDialog(JFrame view, String title) {
		super(view, title)
		this.setSize(300,100)
		this.setTitle(title)
		
		this.getContentPane().setLayout(new BorderLayout())
		
		JPanel panel = (JPanel )this.getContentPane();
		panel.setBorder(new EmptyBorder(10, 10, 10, 10) )
			
		progress = new JProgressBar()
		
		progress.setStringPainted(true)
		progress.setIndeterminate(true)
		
		this.add(BorderLayout.CENTER, progress);
		this.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		
		
		this.setLocationRelativeTo(view)
        this.setAlwaysOnTop(true)
	}
	
	

}
