package com.blogspot.jvalentino.milfit.ui.form

import com.blogspot.jvalentino.milfit.model.FlatFormData;
import com.blogspot.jvalentino.milfit.model.FormElement;

interface IFormListener {
    void dataUpdated(FlatFormData data)
    void templateUpdated(FormElement template)
}
