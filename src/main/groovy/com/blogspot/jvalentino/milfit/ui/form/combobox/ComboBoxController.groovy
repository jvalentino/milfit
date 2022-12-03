package com.blogspot.jvalentino.milfit.ui.form.combobox

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.SwingUtilities;

import com.blogspot.jvalentino.milfit.AppState;
import com.blogspot.jvalentino.milfit.model.Bounds;
import com.blogspot.jvalentino.milfit.model.FlatFormData;
import com.blogspot.jvalentino.milfit.model.FormElement;
import com.blogspot.jvalentino.milfit.service.ServiceBus;
import com.blogspot.jvalentino.milfit.ui.form.IFormListener;
import com.blogspot.jvalentino.milfit.ui.form.dialog.EditDialog;

class ComboBoxController implements MouseListener {
    
    ComboBoxModel model = new ComboBoxModel()
    ComboBoxField view
    ServiceBus bus = ServiceBus.getInstance()
    
    ComboBoxController(ComboBoxField view, IFormListener listener, FormElement form, FlatFormData data) {
        model.listener = listener
        model.form = form
        model.data = data
        this.view = view
    }
    
    void constructionComplete() {
        view.setSelectedItem(model.data.value)
        
        view.addActionListener(new ActionListener() {
            void actionPerformed(ActionEvent e) {
                model.data.value = view.getSelectedItem()
                
            }
        })
        
        view.addMouseListener(this)
    }
    
    void updatePositionAndSize(int swingMaxHeight, int dpi) {
        Bounds b = bus.geometryService.generateSwingBoundsFromPdf(
            dpi,
            swingMaxHeight,
            model.form.x - model.form.visualOffsetX - 6,
            model.form.y - model.form.visualOffsetY,
            model.form.width,
            model.form.height)
                
        view.setBounds(b.x, b.y, b.width, b.height)
        
        int fontSize = bus.geometryService.valueFromPdfToSwing(dpi, model.form.font - 3)
        
        Font font = new Font("Courier", Font.PLAIN, fontSize)
        view.setFont(font)
    
    }
    
    void refreshValue() {
       SwingUtilities.invokeLater(new Runnable() {
            void run() {
              view.setSelectedItem(model.data.value)
            }
          })
    }
    
    @Override
    void mouseClicked(MouseEvent e) {
        if (SwingUtilities.isRightMouseButton(e) && AppState.getInstance().designMode) {
            EditDialog.display(model.form, model.listener)
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
