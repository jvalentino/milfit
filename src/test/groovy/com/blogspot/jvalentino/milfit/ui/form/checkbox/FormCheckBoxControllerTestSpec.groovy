package com.blogspot.jvalentino.milfit.ui.form.checkbox

import com.blogspot.jvalentino.milfit.model.FlatFormData;
import com.blogspot.jvalentino.milfit.model.FormElement;
import com.blogspot.jvalentino.milfit.model.FormElementType;

import spock.lang.Specification;

class FormCheckBoxControllerTestSpec extends Specification {

    FormCheckBoxController service
    FormCheckBox view
    FormCheckBoxModel model
    FormCheckBoxController instance
    
    def setup() {
        model = new FormCheckBoxModel(
            data:new FlatFormData(), 
            formElement:new FormElement())
        
        view = Mock(FormCheckBox)
        
        instance = Mock(FormCheckBoxController)
        
        service = new FormCheckBoxController(
            view:view, 
            model:model,
            instance:instance)
    }
    
    void "test determineMultiSelection"() {
        
         when:
         String[] results = service.determineMultiSelection(selectionValue, currentValues as String[])
         
         then:
         results.toString() == result
         
         where:
         selectionValue  | currentValues || result
         "A"             | []            || "[A]"
         "A"             | ["A"]         || "[]"
         "A"             | ["B"]         || "[A, B]"
         
     }
     
     void "test isSelected"() {
         
          when:
          boolean results = service.isSelected(selectionValue, currentValues as String[])
          
          then:
          results == selected
          
          where:
          selectionValue  | currentValues || selected
          "A"             | []            || false
          "A"             | ["A"]         || true
          "A"             | ["B"]         || false
          
      }
     
     void "test construct"() {
         
         when:
         service.construct()
         
         then:
         1 * instance.determineInitialValue()
         1 * view.addMouseListener(service)
         
     }
     
     void "test getSelectionValue"() {
         given:
         model.selectionIndex = selectionIndex
         model.formElement.selections = selections as String[]
         
         when:
         String result = service.getSelectionValue()
         
         then:
         selectionValue == result
         
         where:
         selectionIndex | selections        || selectionValue
         1              | ["A", "B", "C"]   || "B"
         null           | null              || "true"
     }
     
     void "test findX"() {
         given:
         model.selectionIndex = selectionIndex
         model.formElement.xs = xs as Double[]
         model.formElement.x = x
         
         when:
         Double result = service.findX()
         
         then:
         result == rx
         
         where:
         selectionIndex | x     | xs            || rx
         1              | null  | [1.0, 2.0]    || 2.0
         null           | 1.0   | null          || 1.0
     }
     
     void "Test handleMultiCheckBoxClick"() {
         given:
         model.data.values = values as String[]
         
         when:
         service.handleMultiCheckBoxClick(selectionValue)
         
         then:
         model.selected == selected
         model.data.values.toString() == result
         
         where:
         values     | selectionValue    || selected | result
         null       | "A"               || true     | "[A]"
         ["A"]      | "A"               || false    | "[]"
         ["A, B"]   | "C"               || true     | "[C, A, B]"
     }
     
     void "test handleBooleanCheckBoxClick"() {
         given:
         model.selected = initialSelected
         
         when:
         service.handleBooleanCheckBoxClick()
         
         then:
         model.selected == resultSelected
         
         where:
         initialSelected    || resultSelected
         false              || true
         true               || false
     }
     
    
     
     void "test handleSingleCheckBoxClick"() {
         given:
         String selectionValue = "A"
         model.data.value = null
         model.formElement.selections = ["A", "B"] as String[]
         FormCheckBox otherView = Mock(FormCheckBox)
         model.group = [view, otherView]
         
         when:
         service.handleSingleCheckBoxClick(selectionValue)
         
         then:
         model.selected == true
         1 * otherView.setSelected(false)
     }
     
     void "test determineInitialValue"() {
         given:
         model.formElement.type = type
         model.data.values = values as String[]
         model.formElement.selections = selections as String[]
         model.selectionIndex = selectionIndex
         model.data.value = value
         
         when:
         service.determineInitialValue()
         
         then:
         model.selected == selected
         
         where:
         type                               | value     | values        | selections        | selectionIndex    || selected
         FormElementType.CHECK_BOX_MULTI    | null      | ["A", "B"]    | ["A", "B", "C"]   | 1                 || true
         FormElementType.CHECK_BOX_MULTI    | null      | []            | ["A", "B", "C"]   | 1                 || false
         FormElementType.CHECK_BOX_MULTI    | null      | null          | ["A", "B", "C"]   | 1                 || false
         FormElementType.CHECK_BOX_BOOLEAN  | "true"    | null          | null              | null              || true
         FormElementType.CHECK_BOX_BOOLEAN  | null      | null          | null              | null              || false
         FormElementType.CHECK_BOX_SINGLE   | "B"       | null          | ["A", "B", "C"]   | 1                 || true
         FormElementType.CHECK_BOX_SINGLE   | null      | null          | ["A", "B", "C"]   | 1                 || false
         FormElementType.CHECK_BOX_SINGLE   | "B"       | null          | ["A", "B", "C"]   | 0                 || false
         
     }
}
