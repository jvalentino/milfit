package com.blogspot.jvalentino.milfit.ui.form.dialog;

import groovy.util.logging.Slf4j;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Field;

import javax.swing.JLabel;

import com.blogspot.jvalentino.milfit.model.FormElement;
import com.blogspot.jvalentino.milfit.model.FormElementType;
import com.blogspot.jvalentino.milfit.ui.form.IForm;
import com.blogspot.jvalentino.milfit.ui.form.IFormListener;
import com.blogspot.jvalentino.milfit.util.TextUtil;

@Slf4j
class EditDialogController {

    EditDialogModel model = new EditDialogModel()
    EditDialog view
    IFormListener listener
    EditDialogController instance = this
    
    
    
    EditDialogController() {
        
    }
    
    EditDialogController(EditDialog view, FormElement template, IFormListener listener) {
        model.template = template
        this.view = view
        this.listener = listener
    }
    
    void constructionComplete() {
        List<AttributeTextField> fields = this.createComponents(view, model.template, TextUtil.attributeMap)
        
        this.addFields(view, fields)
        
        view.okButton.addActionListener(new ActionListener() {
            void actionPerformed(ActionEvent e) {                
                instance.saveFieldsToTemplate(fields, model.template, TextUtil.attributeMap)
            }
        })
        
        view.pack()
        view.setLocationRelativeTo(null)
    }
    
    void saveFieldsToTemplate(List<AttributeTextField> fields, FormElement template, 
        Map<String, AttributeType> supported) {
        
        for (AttributeTextField textField : fields) {
            this.updateFieldWithValue(template, textField.getText(), 
                textField.attribute, textField.type)
        }
        
        EditDialog.isShowing = false
        listener.templateUpdated(template)
        view.setVisible(false)
        
    }
    
    void updateFieldWithValue(FormElement template, String text, String attribute, 
        AttributeType type) {
        
        Field field = template.getClass().getDeclaredField(attribute)
        field.setAccessible(true)
                        
        Object o = TextUtil.getValueForString(text.trim(), type)
        
        field.set(template, o)
        
    }
        
    void addFields(EditDialog view, List<AttributeTextField> fields) {
        GridBagConstraints c = new GridBagConstraints()
        c.fill = GridBagConstraints.HORIZONTAL
        c.gridx = 0
        c.gridy = 0
        c.insets = new Insets(2,2,2,2)
        
        int row = 0
        
        for (AttributeTextField field : fields) {
            JLabel label = new JLabel(field.attribute)
            
            c.gridx = 0
            c.gridy = row
            view.centerPanel.add(label, c)
            
            c.gridx = 1
            c.gridy = row
            field.setPreferredSize(new Dimension(300, 25))
            view.centerPanel.add(field, c)
            
            row++
        }
    }
    
    List<AttributeTextField> createComponents(EditDialog view, FormElement template, 
        Map<String, AttributeType> supported) {
        
        List<AttributeTextField> list = []
            
        Field[] fields = template.getClass().getDeclaredFields();
        for(Field f : fields){
           
           if (this.ignoreField(f.name)) {
               continue
           }
           f.setAccessible(true)
           Object v = f.get(template)
           
           String value = TextUtil.getStringValueForObject(v, supported.get(f.getType().name))
           
           AttributeTextField textField = new AttributeTextField(
               value, f.name, supported.get(f.getType().name))
           
           list.add(textField)
           
        }
        
        return list
        
    }
            
    boolean ignoreField(String name) {
        Set<String> ignoreFields = ["INSTANCE", '$', "metaClass", "_"]
        for (String ignore : ignoreFields) {
            if (name.startsWith(ignore)) {
                return true
            }
        }
        return false
    }
    
    
}
