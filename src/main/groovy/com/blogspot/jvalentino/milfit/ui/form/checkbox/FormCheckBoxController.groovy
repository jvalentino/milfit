package com.blogspot.jvalentino.milfit.ui.form.checkbox;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.SwingUtilities;

import com.blogspot.jvalentino.milfit.AppState;
import com.blogspot.jvalentino.milfit.model.Bounds;
import com.blogspot.jvalentino.milfit.model.FlatFormData;
import com.blogspot.jvalentino.milfit.model.FormElement;
import com.blogspot.jvalentino.milfit.model.FormElementType;
import com.blogspot.jvalentino.milfit.service.ServiceBus;
import com.blogspot.jvalentino.milfit.ui.form.IFormListener;
import com.blogspot.jvalentino.milfit.ui.form.dialog.EditDialog;

class FormCheckBoxController implements MouseListener {

    ServiceBus bus = ServiceBus.getInstance()
    FormCheckBox view
    FormCheckBoxModel model
    FormCheckBoxController instance = this
    IFormListener listener
    
    FormCheckBoxController() {
        
    }
    
    FormCheckBoxController(FormCheckBox view, FormElement form, 
        FlatFormData data, int selectionIndex, Integer dpi, 
        List<FormCheckBox> group=null) {
        
        this.view = view
        
        model = new FormCheckBoxModel()
        model.formElement = form
        model.data = data
        model.selectionIndex = selectionIndex
        model.dpi = dpi
        model.group = group
    }
        
    FormCheckBoxController(FormCheckBox view, FormElement form,
        FlatFormData data, Integer dpi) {
        
        this.view = view
        
        model = new FormCheckBoxModel()
        model.formElement = form
        model.data = data
        model.dpi = dpi
    }
    
    void construct() {
        
        instance.determineInitialValue()
        
        view.addMouseListener(this)
        
    }
    
    void determineInitialValue() {
        switch (model.formElement.type) {
            case FormElementType.CHECK_BOX_MULTI:
                model.selected = this.isSelected(this.getSelectionValue(), model.data.values)
                break
           case FormElementType.CHECK_BOX_SINGLE:
                model.data.values = null
                model.selected = this.getSelectionValue() == model.data.value
                break
            case FormElementType.CHECK_BOX_BOOLEAN:
                model.data.values = null
                model.selected = model.data.value == "true"
                break
        }
    }
    
    
    String getSelectionValue() {
        if (model.selectionIndex != null) {
            return model.formElement.selections[model.selectionIndex]
        } else {
            return "true"
        }
    }
    
    void paintComponent(Graphics g, int width, int height) {
        Graphics2D g2d = (Graphics2D) g
        
        g.setColor(Color.decode("#EEEEEE"))
        g.fillRect(0,0, width, height)

        g.setColor(Color.black)
        
        if (model.selected && model.dpi != null) {
           g.drawString("X",
               bus.geometryService.valueFromPdfToSwing(model.dpi, 1),
               (width / 2) + bus.geometryService.valueFromPdfToSwing(model.dpi, 2))
        }
        
        RenderingHints renderHints = new RenderingHints(
            RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF)
        g2d.setRenderingHints(renderHints)
    }
    
    Double findX() {
        if (model.selectionIndex != null) {
            return model.formElement.xs[model.selectionIndex]
        } else {
            return model.formElement.x
        }
    }
    
    void updatePositionAndSize(int swingMaxHeight, int dpi) {
        model.dpi = dpi
        
        int width = 12
        
        if (model.formElement.width != null) {
            width = model.formElement.width.intValue()
        }
        
        int height = 5
        
        if (model.formElement.height != null) {
            height = model.formElement.height.intValue()
        }
                
        Bounds b = bus.geometryService.generateSwingBoundsFromPdf(
            dpi,
            swingMaxHeight,
            this.findX() - model.formElement.visualOffsetX - 3,
            model.formElement.y - model.formElement.visualOffsetY - 3,
            width,
            height)
        
        view.setBounds(b.x, b.y, b.width, b.height)
        
        int fontSize = bus.geometryService.valueFromPdfToSwing(dpi, model.formElement.font)
        
        Font font = new Font("Courier", Font.PLAIN, fontSize)
        view.setFont(font)
        
        view.setPreferredSize(new Dimension(b.width, b.height))
                
    }
    
    @Override
    void mouseClicked(MouseEvent e) {
        
        if (SwingUtilities.isRightMouseButton(e) && AppState.getInstance().designMode) {
            EditDialog.display(model.formElement, listener)
            return
        }
        
        String selectionValue = instance.getSelectionValue()
       
        switch (model.formElement.type) {
            case FormElementType.CHECK_BOX_MULTI:
                instance.handleMultiCheckBoxClick(selectionValue)
                break
           case FormElementType.CHECK_BOX_SINGLE:
                instance.handleSingleCheckBoxClick(selectionValue)
                break
            case FormElementType.CHECK_BOX_BOOLEAN:
                instance.handleBooleanCheckBoxClick()
                break
        }
        
        listener.dataUpdated(model.data)
        
        view.revalidate()
        view.repaint()
        
    }
    
    void handleMultiCheckBoxClick(String selectionValue) {
        if (model.data.values == null) {
            model.data.values = []
        }
        model.data.values = this.determineMultiSelection(
            selectionValue, model.data.values)
        
        model.selected = this.isSelected(
            selectionValue, model.data.values)
    }
    
    void handleBooleanCheckBoxClick() {
        model.selected = !model.selected
        if (model.selected) {
            model.data.value = "true"
        } else {
            model.data.value = "false"
        }
    }
    
    void handleSingleCheckBoxClick(String selectionValue) {
        model.data.value = selectionValue
                
        model.selected = selectionValue == model.data.value
        
        for (FormCheckBox box : model.group) {
            
            if (view == box) {
                continue
            } 
            
            box.setSelected(false)
            
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
    
    /**
     * This is used to act like a multi selection series of check boxes.
     * If the current selections are [A, B] and the new selection value is C, then
     * the resulting list is [A,B,C]. If a new selection is made as A, then the result
     * is [B, C].
     *
     * @param selectionValue
     * @param currentValues
     * @return
     */
    String[] determineMultiSelection(String selectionValue, String[] currentValues) {
        List<String> newList = []
        
        //only add this as a value if it already is not there
        boolean alreadySelected = false
        for (String value : currentValues) {
            if (value == selectionValue) {
                alreadySelected = true
                break
            }
        }
        
        if (!alreadySelected) {
            newList.add(selectionValue)
            for (String value : currentValues) {
                    newList.add(value)
            }
        } else {
            for (String value : currentValues) {
                if (value != selectionValue) {
                    newList.add(value)
                }
            }
            
        }
        
        return newList as String[]
    }
    
    /**
     * Returns true if the given value is present in the given array, indicating selection.
     *
     * @param selectionValue
     * @param currentValues
     * @return
     */
    boolean isSelected(String selectionValue, String[] currentValues) {
        boolean selected = false
        
        for (String value : currentValues) {
            if (value == selectionValue) {
                selected = true
                break
            }
        }
        
        return selected
    }
}
