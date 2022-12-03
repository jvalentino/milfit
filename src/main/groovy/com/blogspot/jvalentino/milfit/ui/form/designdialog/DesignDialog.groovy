package com.blogspot.jvalentino.milfit.ui.form.designdialog

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.blogspot.jvalentino.milfit.model.Form;

class DesignDialog extends JDialog {

    DesignDialogController controller
    JButton okButton = new JButton("Save")
    JButton cancelButton = new JButton("Cancel")
    
    JTextField locationField = new JTextField(25)
    JTextField nameField = new JTextField()
    JTextField typeField = new JTextField()
    JTextField fileField = new JTextField()
    JTextField categoryField = new JTextField()
    JTextField sourceField = new JTextField()
    JButton browseButton = new JButton("Browse")
    
    DesignDialog() {
        
    }
    
    DesignDialog(Form form, IDesignDialog listener) {
        this.controller = new DesignDialogController(this, form, listener)
        this.construct()
        this.controller.constructionComplete()
        this.pack()
        this.setLocationRelativeTo(null)
    }
    
    void construct() {
        this.title = "Save Template"
        this.getContentPane().setLayout(new BorderLayout())
        
        JPanel fieldPanel = new JPanel()
        fieldPanel.setLayout(new GridBagLayout())
        
        GridBagConstraints c = new GridBagConstraints()
        c.fill = GridBagConstraints.HORIZONTAL
        c.gridx = 0
        c.gridy = 0
        c.insets = new Insets(2,2,2,2)
        
        int row = 0
        
        c.gridx = 0
        c.gridy = row
        fieldPanel.add(new JLabel("Template File"), c)
        
        c.gridx = 1
        c.gridy = row
        fieldPanel.add(locationField, c)
        
        c.gridx = 2
        c.gridy = row
        fieldPanel.add(browseButton, c)
        
        row++
        
        c.gridx = 0
        c.gridy = row
        c.gridwidth = 1
        fieldPanel.add(new JLabel("Name"), c)
        
        c.gridx = 1
        c.gridy = row
        c.gridwidth = 2
        fieldPanel.add(nameField, c)
        
        row++
        
        c.gridx = 0
        c.gridy = row
        c.gridwidth = 1
        fieldPanel.add(new JLabel("Type"), c)
        
        c.gridx = 1
        c.gridy = row
        c.gridwidth = 2
        fieldPanel.add(typeField, c)
        
        row++
        
        c.gridx = 0
        c.gridy = row
        c.gridwidth = 1
        fieldPanel.add(new JLabel("File"), c)
        
        c.gridx = 1
        c.gridy = row
        c.gridwidth = 2
        fieldPanel.add(fileField, c)
        
        row++
        
        c.gridx = 0
        c.gridy = row
        c.gridwidth = 1
        fieldPanel.add(new JLabel("Category"), c)
        
        c.gridx = 1
        c.gridy = row
        c.gridwidth = 2
        fieldPanel.add(categoryField, c)
        
        row++
        
        c.gridx = 0
        c.gridy = row
        c.gridwidth = 1
        fieldPanel.add(new JLabel("Source"), c)
        
        c.gridx = 1
        c.gridy = row
        c.gridwidth = 2
        fieldPanel.add(sourceField, c)
        
        JPanel buttonPanel = new JPanel()
        buttonPanel.add(okButton)
        buttonPanel.add(cancelButton)
        
        
        this.getContentPane().add(fieldPanel, BorderLayout.CENTER)
        this.getContentPane().add(buttonPanel, BorderLayout.SOUTH)
    }
}
