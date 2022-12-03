package com.blogspot.jvalentino.milfit.ui.form.textarea

import groovy.util.logging.Slf4j;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.BorderFactory;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.blogspot.jvalentino.milfit.AppState;
import com.blogspot.jvalentino.milfit.model.Bounds;
import com.blogspot.jvalentino.milfit.model.FlatFormData;
import com.blogspot.jvalentino.milfit.model.FontSettings;
import com.blogspot.jvalentino.milfit.model.FormElement;
import com.blogspot.jvalentino.milfit.service.ServiceBus;
import com.blogspot.jvalentino.milfit.ui.form.IForm;
import com.blogspot.jvalentino.milfit.ui.form.IFormListener;
import com.blogspot.jvalentino.milfit.ui.form.dialog.EditDialog;
import com.blogspot.jvalentino.milfit.util.TextUtil;

@Slf4j
class FormTextArea extends JTextArea implements IForm, MouseListener {
    
    static final int X_OFFSET = 3
    static final int Y_OFFSET = 3
    
    IFormListener listener
    ServiceBus bus = ServiceBus.getInstance()
    FormElement formElement
    FlatFormData data
    FormTextArea instance = this

    FormTextArea(FormElement form, FlatFormData data) {
        this.formElement = form
        this.data = data
                
        this.setMargin(new Insets(0, 0, 0, 0))
        Border border = BorderFactory.createEmptyBorder(0, 0, 0, 0)
        this.setBorder(border)
        this.setOpaque(true)
        this.setBackground(Color.decode("#EEEEEE"))
        this.setText(data.value)
        this.setWrapStyleWord(true)
        this.setLineWrap(true)
        this.addMouseListener(this)
        
        this.getDocument().addDocumentListener(new DocumentListener() {
            void changedUpdate(DocumentEvent e) {
                data.value = instance.getText()
                data.font = form.font.intValue() + ""
                listener.dataUpdated(data)
            }
            void removeUpdate(DocumentEvent e) {
                data.value = instance.getText()
                data.font = form.font.intValue() + ""
                listener.dataUpdated(data)
            }
            void insertUpdate(DocumentEvent e) {
                data.value = instance.getText()
                data.font = form.font.intValue() + ""
                listener.dataUpdated(data)
            }
        })
    }
    
    @Override
    void updatePositionAndSize(int swingMaxHeight, int dpi) {
        
        // text areas can have multiple supported font sizes and max characters per line
        FontSettings settings = bus.geometryService.determineFontSettings(
            data.font, formElement.fonts, formElement.maxCharss, formElement.font, 
            formElement.maxChars)
                
        Font deriveFont = bus.geometryService.generateDerivativeFont(
            dpi, settings.font, "Courier", Font.PLAIN)
        this.setFont(deriveFont)
        
        FontMetrics fontMetrics = this.getFontMetrics(deriveFont)
        
        Integer width = null
        
        if (formElement.width != null) {
            // this is when a width is directly given
            width = bus.geometryService.valueFromPdfToSwing(dpi, formElement.width)
        } else {
            // this is when a text area supports multiple different fonts
            width = TextUtil.determineWidthBasedOnMaxCharsPerLine(fontMetrics, settings.maxChars)
        }
                
        Bounds b = bus.geometryService.generateSwingBoundsFromPdf(
            dpi, 
            swingMaxHeight,
            formElement.x - X_OFFSET - formElement.visualOffsetX,
            formElement.y - Y_OFFSET - - formElement.visualOffsetY,
            width,
            formElement.height)
        
        // override the width because we calculated it separately
        b.width = width
                
        this.setBounds(b.x, b.y, b.width, b.height)
                
    }
    
    void refreshValue() {
       SwingUtilities.invokeLater(new Runnable() {
            public void run() {
              instance.setText(data.value)
            }
          });
    }
    
    @Override
    String getKey() {
        return formElement.key
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (SwingUtilities.isRightMouseButton(e) && AppState.getInstance().designMode) {
            EditDialog.display(formElement, listener)
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        
    }

    @Override
    public void mouseExited(MouseEvent e) {
        
    }

}
