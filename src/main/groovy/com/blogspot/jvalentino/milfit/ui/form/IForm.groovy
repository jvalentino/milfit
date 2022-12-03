package com.blogspot.jvalentino.milfit.ui.form

import java.awt.Font;

import com.blogspot.jvalentino.milfit.model.FormElement;

interface IForm {

    void updatePositionAndSize(int swingMaxHeight, int dpi)
    
    IFormListener getListener()
    
    void setListener(IFormListener listener)
    
    void refreshValue()
    
    String getKey()
}
