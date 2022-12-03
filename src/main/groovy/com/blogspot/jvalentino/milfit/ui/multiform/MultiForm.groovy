package com.blogspot.jvalentino.milfit.ui.multiform

import javax.swing.JMenuItem;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.util.List;

import com.blogspot.jvalentino.milfit.model.FlatForm;
import com.blogspot.jvalentino.milfit.model.FlatFormGroup;
import com.blogspot.jvalentino.milfit.model.Form;

import javax.swing.JInternalFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.TableColumn;

class MultiForm extends JInternalFrame {
    
    MultiFormController controller
    
    JTable table
    JScrollPane pane 
    JMenuBar menu = new JMenuBar()
    JMenu fileMenu = new JMenu("File")
    JMenuItem saveItem = new JMenuItem("Save Form Group")
    JMenuItem newItem = new JMenuItem("New Form")
    JMenuItem importItem = new JMenuItem("Import Form")
    
    JMenu individualMenu = new JMenu("Manage Selected")
    JMenuItem openItem = new JMenuItem("Open Selected Form")
    JMenuItem deleteItem = new JMenuItem("Delete Selected Form")
    
    
    MultiForm() {
        
    }
    
    MultiForm(String pdfFilename, List<BufferedImage> images, Form template, 
        IMultiFormListener listener, FlatFormGroup group, File file=null) {
        
        super(template.type,
        true, //resizable
        true, //closable
        false, //maximizable
        true)//iconifiable
        
        this.controller = new MultiFormController(this, pdfFilename, images, template, listener, group, file)
        this.getContentPane().setLayout(new BorderLayout())
        
        this.setSize(1000, 500)
        this.setLocation(5, 5)
        
        
        table = new JTable(controller.model.formTableModel)
        pane = new JScrollPane(table)
        //pane.setPreferredSize(new Dimension(1600, 500))
        
        //table.setPreferredScrollableViewportSize(new Dimension(1600, 500));
        table.setFillsViewportHeight(true);
        
        for (TableColumn column : table.getColumnModel().getColumns()) {
            column.setPreferredWidth(100)
            //column.setMinWidth(100)
        }
        
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        
        
        this.getContentPane().add(pane, BorderLayout.CENTER)
        
        menu.add(fileMenu)
        fileMenu.add(saveItem)
        fileMenu.add(newItem)
        fileMenu.add(importItem)
        
        menu.add(individualMenu)
        individualMenu.add(openItem)
        individualMenu.add(deleteItem)
        
        this.setJMenuBar(menu)
        
        this.setVisible(true)
        this.setSelected(true)
        
        
        
        controller.constructionComplete()
    }

}
