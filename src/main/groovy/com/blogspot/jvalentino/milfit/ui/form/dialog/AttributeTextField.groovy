package com.blogspot.jvalentino.milfit.ui.form.dialog

import javax.swing.JTextField;

class AttributeTextField extends JTextField {
    
    String attribute
    AttributeType type

    AttributeTextField() {
        
    }
    
    AttributeTextField(String value, String attribute, AttributeType type) {
        super(value)
        this.attribute = attribute
        this.type = type
    }
    
    String values() {
        return "${attribute}: " + this.getText()
    }
    
}
