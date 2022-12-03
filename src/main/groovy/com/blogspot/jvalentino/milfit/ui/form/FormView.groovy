package com.blogspot.jvalentino.milfit.ui.form

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.JButton;
import javax.swing.JInternalFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.blogspot.jvalentino.milfit.AppState;
import com.blogspot.jvalentino.milfit.model.FlatForm;
import com.blogspot.jvalentino.milfit.model.Form;

class FormView extends JInternalFrame {
    
    static int openFrameCount = 0
    static final int xOffset = 5, yOffset = 5
    
    FormController controller
    JMenuBar menu = new JMenuBar()
    JMenu resizeMenu = new JMenu("DPI")
    JMenu fileMenu = new JMenu("File")
    JMenuItem saveItem = new JMenuItem("Save")
    JMenuItem exportItem = new JMenuItem("Export as PDF")
    JMenuItem saveDesignItem = new JMenuItem("Save Design As...")
    JMenu designMenu = new JMenu("Design")
    
    JPanel panel
    
    JPanel navPanel = new JPanel()
    JButton nextButton = new JButton(">")
    JButton previousButton = new JButton("<")
    JTextField currentField = new JTextField("")


    FormView() {
        
    }
    
    public FormView(String pdfFilename, List<BufferedImage> images, Form template, FlatForm data, 
        File file, IFormViewListener formViewListener=null) {
        
        super("Document #" + (++openFrameCount),
        true, //resizable
        true, //closable
        false, //maximizable
        true)//iconifiable
        
        controller = new FormController(this, pdfFilename, images, template, data, file, formViewListener)
        
        this.getContentPane().setLayout(new BorderLayout())
        
        //...Then set the window size or call pack...
        //
        this.panel = new JPanel() {
            @Override
            void paintComponent(Graphics g) {
                super.paintComponent(g)
                controller.paintComponent(g)
            }
        }
    
        //showPanel.setPreferredSize(new Dimension(100, 900))
        this.getContentPane().add(this.createToolPanel(), BorderLayout.NORTH)
        this.getContentPane().add(panel, BorderLayout.CENTER)
        
        panel.setLayout(null)
        
        //Set the window's location.
        setLocation(xOffset*openFrameCount, yOffset*openFrameCount)

        menu.add(fileMenu)
        fileMenu.add(saveItem)
        fileMenu.add(exportItem)
        
        if (AppState.getInstance().designMode) {
            designMenu.setForeground(Color.red)
            saveDesignItem.setForeground(Color.red)
            menu.add(designMenu)
            designMenu.add(saveDesignItem)
        }
        
        menu.add(resizeMenu)
        
        this.setJMenuBar(menu)
        this.setVisible(true)
        this.setSelected(true)
        
        currentField.setPreferredSize(new Dimension(50,25))
        currentField.setEditable(false)
        
        controller.constructionComplete()
    }
    
    JPanel createToolPanel() {
        navPanel = new JPanel()
        navPanel.add(previousButton)
        navPanel.add(currentField)
        navPanel.add(nextButton)
        return navPanel
    }
}
