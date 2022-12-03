package com.blogspot.jvalentino.milfit.ui

import java.awt.BorderLayout;

import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;

import com.blogspot.jvalentino.milfit.component.MDIDesktopPane;

class MainView extends JFrame {

    MDIDesktopPane desktop = new MDIDesktopPane()
    MainController controller
    
    JMenuBar menu = new JMenuBar()
    
    JMenu fileMenu = new JMenu("File")
    JMenu newItem = new JMenu("New")
    JMenuItem openItem = new JMenuItem("Open")
    JMenu helpMenu = new JMenu("Help")
    JMenuItem aboutItem = new JMenuItem("About")
    JMenu newGroupItem = new JMenu("Group of Forms")
    JMenu newIndividualItem = new JMenu("Individual Form")
    
    MainView() {
        controller = new MainController(this)
        constuct()
        controller.constructionComplete()
    }
    
    void constuct() {
        this.getContentPane().setLayout(new BorderLayout())
                
        
        //this.getContentPane().add(toolbar, BorderLayout.NORTH)
        
        JScrollPane pane = new JScrollPane(desktop)
        this.getContentPane().add(pane, BorderLayout.CENTER)
        
        setJMenuBar(menu)
        
        menu.add(fileMenu)
        fileMenu.add(newItem)
        fileMenu.add(openItem)
        
        menu.add(helpMenu)
        helpMenu.add(aboutItem)
        newItem.add(newIndividualItem)
        newItem.add(newGroupItem)
        //navyItem.add(fitrepItem)
 
        desktop.setDragMode(JDesktopPane.OUTLINE_DRAG_MODE)
    }
}
