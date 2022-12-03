package com.blogspot.jvalentino.milfit.ui.multiform

import java.awt.image.BufferedImage;
import java.util.List;

import com.blogspot.jvalentino.milfit.model.FlatForm;
import com.blogspot.jvalentino.milfit.model.FlatFormGroup;
import com.blogspot.jvalentino.milfit.model.Form;

class MultiFormModel {

    String pdfFilename
    List<BufferedImage> images
    Form template
    FormTableModel formTableModel
    FlatFormGroup group
    File file
    Integer selectedRow = null
}
