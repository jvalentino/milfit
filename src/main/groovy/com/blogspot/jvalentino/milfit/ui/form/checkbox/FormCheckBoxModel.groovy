package com.blogspot.jvalentino.milfit.ui.form.checkbox

import com.blogspot.jvalentino.milfit.model.FlatFormData;
import com.blogspot.jvalentino.milfit.model.FormElement;

class FormCheckBoxModel {

    FormElement formElement
    FlatFormData data
    /** The index of the selection value and X position */
    Integer selectionIndex
    boolean selected = false
    Integer dpi
    List<FormCheckBox> group
}
