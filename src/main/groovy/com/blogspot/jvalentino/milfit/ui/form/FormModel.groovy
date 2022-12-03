package com.blogspot.jvalentino.milfit.ui.form

import java.awt.image.BufferedImage;

import com.blogspot.jvalentino.milfit.model.DerivativeFormField;
import com.blogspot.jvalentino.milfit.model.FlatForm;
import com.blogspot.jvalentino.milfit.model.FlatFormData;
import com.blogspot.jvalentino.milfit.model.Form;

class FormModel {
    /** image representation of each page of the PF */
    List<BufferedImage> images = []
    /** The currently displayed page, starting at 0 */
    int currentPage = 0
    /** The path to the file that created the PDF */
    String pdfFilename
    /** The current DPI of the PDF to image rendering */
    int dpi = 100
    /** The form template that we are representing */
    Form template
    /** The currently displayed swing text components */
    List<IForm> elements = []
    /** The current form */
    FlatForm form
    /** A map of keys (which are used in the template fields) to the objects 
     * which store user entered data on a particular field */
    Map<String, FlatFormData> dataMap = new LinkedHashMap<String, FlatFormData>()
    /** Used to keep track of user input fields that's values are derived from 
     * one or more other fields */
    List<DerivativeFormField> derivativeFields = []
    /** The place this is saved to */
    File file
}
