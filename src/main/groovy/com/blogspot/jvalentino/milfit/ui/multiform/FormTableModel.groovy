package com.blogspot.jvalentino.milfit.ui.multiform

import javax.swing.table.AbstractTableModel;

import com.blogspot.jvalentino.milfit.model.FlatForm;
import com.blogspot.jvalentino.milfit.model.FlatFormData;
import com.blogspot.jvalentino.milfit.model.FlatFormGroup;
import com.blogspot.jvalentino.milfit.model.Form;
import com.blogspot.jvalentino.milfit.util.TextUtil;

class FormTableModel extends AbstractTableModel {
    
    Form template
    FlatFormGroup group
    
    FormTableModel(Form template, FlatFormGroup group) {
        this.template = template
        this.group = group
    }
    
    FlatForm getRow(int row) {
        if (row >= group.forms.size() || row < 0) {
            return null
        }
        return group.forms.get(row)
    }

    @Override
    int getRowCount() {
        return group.forms.size()
    }

    @Override
    int getColumnCount() {
        return template.elements.size()
    }

    @Override
    Object getValueAt(int rowIndex, int columnIndex) {
        String key = template.elements.get(columnIndex).key
        FlatForm form = group.forms.get(rowIndex)
        
        for (FlatFormData data : form.elements) {            
            
            if (data.key == key) {
                if (data.value != null) {
                    return data.value
                } else if (data.values != null) {
                    return TextUtil.arrayToString(data.values)
                } else {
                    return ""
                }
            }
        }
        
        return ""
    }
    
    @Override
    String getColumnName(int col) {
        return template.elements.get(col).name
    }

}
