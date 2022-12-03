package com.blogspot.jvalentino.milfit.ui.form.designdialog

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JTextField;

import com.blogspot.jvalentino.milfit.component.BaseController;
import com.blogspot.jvalentino.milfit.model.Form;

class DesignDialogController extends BaseController {

    DesignDialogModel model = new DesignDialogModel()
    DesignDialog view
    IDesignDialog listener
    DesignDialogController instance = this
    
    DesignDialogController() {
        
    }
    
    DesignDialogController(DesignDialog view, Form form, IDesignDialog listener) {
        this.view = view
        
        this.listener = listener
        
        this.model.form = new Form(
            name:form.name,
            type:form.type,
            file:form.file,
            category:form.category,
            source:form.source)
        
        this.model.fileLocation = new File(model.form.source)
    }
    
    @Override
    void constructionComplete() {
        view.locationField.setEditable(false)
        
        view.locationField.setText(this.model.fileLocation.getAbsolutePath())
        view.nameField.setText(model.form.name)
        view.typeField.setText(model.form.type)
        view.fileField.setText(model.form.file)
        view.categoryField.setText(model.form.category)
        view.sourceField.setText(model.form.source)
        
        view.okButton.addActionListener(new ActionListener() {
            void actionPerformed(ActionEvent e) {
                instance.okButtonPressed()
            }
        })
        
        view.cancelButton.addActionListener(new ActionListener() {
            void actionPerformed(ActionEvent e) {
                instance.cancelButtonPressed()
            }
        })
        
        view.browseButton.addActionListener(new ActionListener() {
            void actionPerformed(ActionEvent e) {
                instance.browseButtonPressed()
            }
        })
    }
    
    void okButtonPressed() {
        model.form.name = view.nameField.getText().trim()
        model.form.type = view.typeField.getText().trim()
        model.form.file = view.fileField.getText().trim()
        model.form.category = view.categoryField.getText().trim()
        model.form.source = view.sourceField.getText().trim()
        
        listener.saveTemplateAs(model.fileLocation, model.form)
        
        view.setVisible(false)
    }
    
    void cancelButtonPressed() {
        view.setVisible(false)
    }
    
    void browseButtonPressed() {
        File file = this.browseForFileSave(getTemplateNameFromSource(model.form.source), "Save Template")
        
        if (file == null) {
            return
        }
        
        this.model.fileLocation = file        
    }
    
    String getTemplateNameFromSource(String source) {
        File file = new File(source)
        return file.getName()
    }
}
