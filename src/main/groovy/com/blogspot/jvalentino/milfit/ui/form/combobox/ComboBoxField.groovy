package com.blogspot.jvalentino.milfit.ui.form.combobox

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JComboBox
import javax.swing.SwingUtilities;

import com.blogspot.jvalentino.milfit.AppState;
import com.blogspot.jvalentino.milfit.model.FlatFormData;
import com.blogspot.jvalentino.milfit.model.FormElement;
import com.blogspot.jvalentino.milfit.ui.form.IForm
import com.blogspot.jvalentino.milfit.ui.form.IFormListener;
import com.blogspot.jvalentino.milfit.ui.form.dialog.EditDialog;

class ComboBoxField extends JComboBox implements IForm {

    ComboBoxController controller
    
    ComboBoxField(IFormListener listener, FormElement form, FlatFormData data) {
        super(form.selections)
        controller = new ComboBoxController(this, listener, form, data)
        controller.constructionComplete()        
    }
    
    @Override
    void updatePositionAndSize(int swingMaxHeight, int dpi) {
        controller.updatePositionAndSize(swingMaxHeight, dpi)
    }

    @Override
    void refreshValue() {
       controller.refreshValue()
    }

    @Override
    String getKey() {
        return controller.model.form.key
    }

    @Override
    IFormListener getListener() {
        return controller.model.listener
    }

    @Override
    void setListener(IFormListener listener) {
        controller.model.listener = listener
    }
    

}
