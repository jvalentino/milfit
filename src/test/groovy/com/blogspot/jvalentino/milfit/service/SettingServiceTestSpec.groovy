package com.blogspot.jvalentino.milfit.service

import java.io.File;
import java.util.List;

import com.blogspot.jvalentino.milfit.model.FlatForm;
import com.blogspot.jvalentino.milfit.model.Form;
import com.blogspot.jvalentino.milfit.model.FormElement;
import com.blogspot.jvalentino.milfit.model.FormSetting;
import com.blogspot.jvalentino.milfit.model.FormTemplateAndData;

import spock.lang.Specification;

class SettingServiceTestSpec extends Specification {
    
    SettingService service
    SettingService instance
    
    def setup() {
        instance = Mock(SettingService)
        service = new SettingService(instance:instance)
    }
    
    void "test load settings"() {
         when:
         List<FormSetting> results = service.loadSettings("milfit-templates")
         
         then:
         results.size() == 3
         
         FormSetting form = results.get(i)
         form.type == type
         form.name == name
         form.source.getName() == source
         form.category == category
         
         where:
         i  | type                      | name                  | source                                        | category  | file
         0  | "NAVPERS 1616/27 (8-10)"  | "CHIEF EVAL (E7-E9)"  | "NAVY_CHIEF_EVAL_08_10_11_11.template.json"   | "Navy"    | "doc/NAVPERS 1616-27 RE 08-10 11-11_IMAGE.pdf"
         1  | "NAVPERS 1616/26 (08-10)" | "EVAL (E1-E6)"        | "NAVY_E6_EVAL_08_10.template.json"            | "Navy"    | "doc/NAVPERS 1616-26_R11-11_RE_IMAGE.pd"
         2  | "NAVPERS 1610/2 (11-11)"  | "FITREP (W2-06)"      | "NAVY_FITREP_11_11.template.json"             | "Navy"    | "doc/NAVPERS 1610-2_RE 11-11_IMAGE.pdf"
    }
    
    void "test blankDataFromTemplate"() {
        given:
        Form template = new Form(
            type:"foobar",
            elements:[
            new FormElement(key:"1"), 
            new FormElement(key:"1-1"),
            new FormElement(key:"2")
        ])
        
        when:
        FlatForm data = service.blankDataFromTemplate(template)
        
        then:
        data.type == "foobar"
        data.elements.size() == 2
        data.elements.get(0).key == "1"
        data.elements.get(1).key == "2"
    }
    
    void "test openDataFile"() {
        given:
        service.instance = service
        List<FormSetting> availableForms = service.loadSettings("milfit-templates")
        File file = new File("src/test/resources/test.data.json")
        
        when:
        FormTemplateAndData result = service.openDataFile(file, availableForms)
        
        then:
        result.template.type == "NAVPERS 1616/26 (08-10)"
        result.data.type == "NAVPERS 1616/26 (08-10)"
        result.data.elements.size() == 1
    }
    
    void "Test extractTemplatesAndExamples"() {
        when:
        service.extractTemplatesAndExamples("build/milfit-examples", "build/milfit-templates")
        
        then:
        String[] examples = new File("build/milfit-examples").list()
        examples[0] == "Anything in here gets overwritten on startup"
        examples[1] == "CHIEF EVAL (E7-E9).data.json"
        examples[2] == "EVAL (E1-E6).data.json"
        examples[3] == "FITREP (W2-06).data.json"
        
        String[] templates = new File("build/milfit-templates").list()
        templates[0] == "Anything in here gets overwritten on startup"
        templates[1] == "NAVPERS 1610-2_RE 11-11_IMAGE.pdf"
        templates[2] == "NAVPERS 1616-26_R11-11_RE_IMAGE.pdf"
        templates[3] == "NAVPERS 1616-26_R11-11_RE_MARKED.pdf"
        templates[4] == "NAVPERS 1616-27 RE 08-10 11-11_IMAGE.pdf"
        templates[5] == "NAVPERS_1610-1_Rev11-11 SummarySheet Export.pdf"
        templates[6] == "NAVPERS_1610-1_Rev11-11 SummarySheet.pdf"
        templates[7] == "NAVY_CHIEF_EVAL_08_10_11_11.template.json"
        templates[8] == "NAVY_E6_EVAL_08_10.template.json"
        templates[9] == "NAVY_FITREP_11_11.template.json"
    }
    
    void "test isUpdateAvailable"() {
        when:
        boolean result = service.isUpdateAvailable(current, found)
        
        then:
        result == update
        
        where:
        current | found     || update
        null    | "1.0.0"   || false
        "1.0.0" | null      || false
        
        "1.0.0" | "1.0.0"   || false
        "1.0.0" | "2.0.0"   || true
        "1.0.0" | "1.1.0"   || true
        "1.2.0" | "1.3.0"   || true
        
        "1.0.0" | "1.0.1"   || true
        "1.0.1" | "1.0.2"   || true
        
        // opposite
        
        "2.0.0" | "1.0.0"   || false
        "1.1.0" | "1.0.0"   || false
        "1.3.0" | "1.2.0"   || false
        
        "1.0.1" | "1.0.0"   || false
        "1.0.2" | "1.0.1"   || false
    }

}
