package com.blogspot.jvalentino.milfit.ui.form.dialog

import java.util.List;
import java.util.Map;

import com.blogspot.jvalentino.milfit.model.FormElement;
import com.blogspot.jvalentino.milfit.model.FormElementType;
import com.blogspot.jvalentino.milfit.util.TextUtil;

import spock.lang.Specification;

class EditDialogControllerTestSpec extends Specification {

    EditDialogController controller
    EditDialogModel model
    EditDialog view
    
    def setup() {
        model = new EditDialogModel()
        
        view = Mock(EditDialog)
        
        controller = new EditDialogController(
            model:model,
            view:view)
    }
    
    void "Test createComponents"() {
        given:
        Map<String, AttributeType> supported = TextUtil.attributeMap
        
        FormElement template = new FormElement(
            key:"A",
            name:"name here",
            type:FormElementType.TEXT,
            x:10.0,
            y:20.0,
            page:1,
            selections:["A1","B2","C3"],
            xs:[1.0, 2.0, 3.0],
            font:10.0,
            maxChars:92,
            fonts:[10.0, 11.0, 12.0],
            maxCharss:[10,20,30],
            width:100.0,
            height:200.0,
            visualOffsetX:0.0,
            visualOffsetY:0.0,
            deriveValueFrom:["B","C"],
            deriveValueUsing:"average",
            valueFormat:"#0.00"
        )
        
        when:
        List<AttributeTextField> fields = controller.createComponents(view, template, supported)
        
        then:
        fields.size() == 19
        int i = 0
        fields.get(i++).values() == "key: A"
        fields.get(i++).values() == "name: name here"
        fields.get(i++).values() == "type: TEXT"
        fields.get(i++).values() == "x: 10.0"
        fields.get(i++).values() == "y: 20.0"
        fields.get(i++).values() == "page: 1"
        fields.get(i++).values() == "selections: A1, B2, C3"
        fields.get(i++).values() == "xs: 1.0, 2.0, 3.0"
        fields.get(i++).values() == "font: 10.0"
        fields.get(i++).values() == "maxChars: 92"
        fields.get(i++).values() == "fonts: 10.0, 11.0, 12.0"
        fields.get(i++).values() == "maxCharss: 10, 20, 30"
        fields.get(i++).values() == "width: 100.0"
        fields.get(i++).values() == "height: 200.0"
        fields.get(i++).values() == "visualOffsetX: 0.0"
        fields.get(i++).values() == "visualOffsetY: 0.0"
        fields.get(i++).values() == "deriveValueFrom: B, C"
        fields.get(i++).values() == "deriveValueUsing: average"
        fields.get(i++).values() == "valueFormat: #0.00"
    }
    
    void "Test createComponents for types"() {
        given:
        Map<String, AttributeType> supported = TextUtil.attributeMap
        
        FormElement template = new FormElement()
        
        when:
        List<AttributeTextField> fields = controller.createComponents(view, template, supported)
        
        then:
        fields.size() == 19
        int i = 0
        fields.get(i++).type == AttributeType.STRING
        fields.get(i++).type == AttributeType.STRING
        fields.get(i++).type == AttributeType.FORM_ELEMENT_TYPE
        fields.get(i++).type == AttributeType.DOUBLE
        fields.get(i++).type == AttributeType.DOUBLE
        fields.get(i++).type == AttributeType.INTEGER
        fields.get(i++).type == AttributeType.STRING_ARRAY
        fields.get(i++).type == AttributeType.DOUBLE_ARRAY
        fields.get(i++).type == AttributeType.DOUBLE
        fields.get(i++).type == AttributeType.INTEGER
        fields.get(i++).type == AttributeType.FLOAT_ARRAY
        fields.get(i++).type == AttributeType.INTEGER_ARRAY
        fields.get(i++).type == AttributeType.DOUBLE
        fields.get(i++).type == AttributeType.DOUBLE
        fields.get(i++).type == AttributeType.DOUBLE
        fields.get(i++).type == AttributeType.DOUBLE
        fields.get(i++).type == AttributeType.STRING_ARRAY
        fields.get(i++).type == AttributeType.STRING
        fields.get(i++).type == AttributeType.STRING
    }
    
    
    
    void "Test updateFieldWithValue"() {
        given:
        FormElement template = new FormElement(key:"A")
        String text = "B"
        String attribute = "key"
        
        when:
        controller.updateFieldWithValue(template, text, attribute, AttributeType.STRING)
        
        then:
        template.key == "B"
    }
    
    void "Test updateFieldWithValue with doubles"() {
        given:
        FormElement template = new FormElement(xs:null)
        String text = "1.0, 2.0"
        String attribute = "xs"
        
        when:
        controller.updateFieldWithValue(template, text, attribute, AttributeType.DOUBLE_ARRAY)
        
        then:
        template.xs == [1.0, 2.0] as Double[]
    }
}
