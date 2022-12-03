package com.blogspot.jvalentino.milfit.ui.form.textfield

import java.awt.Color;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.BorderFactory;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.blogspot.jvalentino.milfit.AppState;
import com.blogspot.jvalentino.milfit.model.Bounds;
import com.blogspot.jvalentino.milfit.model.FlatFormData;
import com.blogspot.jvalentino.milfit.model.FormElement;
import com.blogspot.jvalentino.milfit.service.ServiceBus;
import com.blogspot.jvalentino.milfit.ui.form.IForm;
import com.blogspot.jvalentino.milfit.ui.form.IFormListener;
import com.blogspot.jvalentino.milfit.ui.form.dialog.EditDialog;

class FormTextField extends JTextField implements IForm, MouseListener {

    ServiceBus bus = ServiceBus.getInstance()
    IFormListener listener
    FormElement formElement
    FlatFormData data
    FormTextField instance = this

    FormTextField(FormElement form, FlatFormData data) {
        this.formElement = form
        this.data = data

        this.setMargin(new Insets(0, 0, 0, 0))

        Border border = BorderFactory.createEmptyBorder(0, 0, 0, 0)

        this.setBorder(border)

        this.setOpaque(true)

        this.setBackground(Color.decode("#EEEEEE"))
        
        this.setText(data.value)
        
        this.addMouseListener(this)

        this.getDocument().addDocumentListener(new DocumentListener() {
                    void changedUpdate(DocumentEvent e) {
                        data.value = instance.getText()
                        listener.dataUpdated(data)
                    }
                    void removeUpdate(DocumentEvent e) {
                        data.value = instance.getText()
                        listener.dataUpdated(data)
                    }
                    void insertUpdate(DocumentEvent e) {
                        data.value = instance.getText()
                        listener.dataUpdated(data)
                    }
                })
    }
    
    void updatePositionAndSize(int swingMaxHeight, int dpi) {
        
        Bounds b = bus.geometryService.generateSwingBoundsFromPdf(
            dpi, 
            swingMaxHeight,
            formElement.x - formElement.visualOffsetX,
            formElement.y - formElement.visualOffsetY,
            formElement.width,
            formElement.height)
                
        this.setBounds(b.x, b.y, b.width, b.height)
        
        int fontSize = bus.geometryService.valueFromPdfToSwing(dpi, formElement.font)
        
        Font font = new Font("Courier", Font.PLAIN, fontSize)
        this.setFont(font)
        
    }
    
    void refreshValue() {
        
        SwingUtilities.invokeLater(new Runnable() {
            void run() {
              instance.setText(data.value)
            }
          });
    }

    @Override
    String getKey() {
        return formElement.key
    }

    @Override
    void mouseClicked(MouseEvent e) {
        
        if (SwingUtilities.isRightMouseButton(e) && AppState.getInstance().designMode) {
            EditDialog.display(formElement, listener)
        }
 
    }

    @Override
    void mousePressed(MouseEvent e) {
        
    }

    @Override
    void mouseReleased(MouseEvent e) {
        
    }

    @Override
    void mouseEntered(MouseEvent e) {
        
    }

    @Override
    void mouseExited(MouseEvent e) {
        
    }
    
   
}
