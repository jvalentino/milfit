package com.blogspot.jvalentino.milfit.ui.form.dialog

import java.awt.BorderLayout;
import java.awt.GridBagLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;

import com.blogspot.jvalentino.milfit.model.FormElement;
import com.blogspot.jvalentino.milfit.ui.form.IForm;
import com.blogspot.jvalentino.milfit.ui.form.IFormListener;

class EditDialog extends JDialog {

    static boolean isShowing = false
    
    EditDialogController controller
    JButton okButton = new JButton("Done")
    JPanel centerPanel = new JPanel()
    JPanel bottomPanel = new JPanel()
    
    EditDialog() {
        
    }
    
    EditDialog(FormElement template, IFormListener listener) {
        this.title = "Edit Field Settings"
        controller = new EditDialogController(this, template, listener)
        this.construct()
        controller.constructionComplete()
    }
    
    void construct() {
        this.getContentPane().setLayout(new BorderLayout())
        
        centerPanel.setLayout(new GridBagLayout())
        
        bottomPanel.add(okButton)
        
        this.getContentPane().add(centerPanel, BorderLayout.CENTER)
        this.getContentPane().add(bottomPanel, BorderLayout.SOUTH)
    }
    
    static void display(FormElement template, IFormListener listener) {
        if (!EditDialog.isShowing) {
            EditDialog.isShowing = true
            EditDialog dialog = new EditDialog(template, listener)
            dialog.setAlwaysOnTop(true)
            dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE)
            dialog.setVisible(true)
        }
    }
}
