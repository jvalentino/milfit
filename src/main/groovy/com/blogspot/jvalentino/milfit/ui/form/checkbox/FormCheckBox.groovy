package com.blogspot.jvalentino.milfit.ui.form.checkbox

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JPanel;

import com.blogspot.jvalentino.milfit.model.Bounds;
import com.blogspot.jvalentino.milfit.model.FlatFormData;
import com.blogspot.jvalentino.milfit.model.FormElement;
import com.blogspot.jvalentino.milfit.model.FormElementType;
import com.blogspot.jvalentino.milfit.service.ServiceBus;
import com.blogspot.jvalentino.milfit.ui.form.IForm;
import com.blogspot.jvalentino.milfit.ui.form.IFormListener;

class FormCheckBox extends JPanel implements IForm {
   
    FormCheckBoxController controller
    
    FormCheckBox() {
        
    }
    
    FormCheckBox(FormElement form, FlatFormData data, int selectionIndex, Integer dpi, 
        List<FormCheckBox> group=null) {
        
        controller = new FormCheckBoxController(this, form, data, selectionIndex, dpi, group)
        controller.construct()
    }
    
    FormCheckBox(FormElement form, FlatFormData data, Integer dpi) {
        controller = new FormCheckBoxController(this, form, data, dpi)
        controller.construct()
    }    
    
    @Override
    void paintComponent(Graphics g) {
        super.paintComponent(g)
        controller.paintComponent(g, this.width, this.height)
    }
    
    @Override
    void updatePositionAndSize(int swingMaxHeight, int dpi) {
        controller.updatePositionAndSize(swingMaxHeight, dpi)
    }    
    
    void setSelected(boolean state) {
        controller.model.selected = state
        revalidate()
        repaint()
    }

    @Override
    IFormListener getListener() {
        controller.listener
    }

    @Override
    void setListener(IFormListener listener) {
        controller.listener = listener
    }
    
    void refreshValue() {
        // not needed
    }
    
    @Override
    String getKey() {
        return controller.model.formElement.key
    }

}
