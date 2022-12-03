package com.blogspot.jvalentino.milfit.service

import java.util.List;

import com.blogspot.jvalentino.milfit.model.FlatForm;
import com.blogspot.jvalentino.milfit.model.FlatFormData;
import com.blogspot.jvalentino.milfit.model.FlatFormGroup;
import com.blogspot.jvalentino.milfit.model.Form;

import spock.lang.Specification;

class CalculationServiceTestSpec extends Specification {

    CalculationService service
    CalculationService instance
    
    def setup() {
        instance = Mock(CalculationService)
        service = new CalculationService(instance:instance)
    }
    void "Test determineKeysForScoring"() {
       when:
       List<String> result = service.determineKeysForScoring(type)
       
       then:
       result == list
       
       where:
       type                         || list
       "NAVPERS 1616/27 (8-10)"     || ["33", "34", "35", "36", "37", "38", "39"]
       "NAVPERS 1616/26 (08-10)"    || ["33", "34", "35", "36", "37", "38", "39"]
       "NAVPERS 1610/2 (11-11)"     || ["33", "34", "35", "36", "37", "38", "39"]
        
    }
    
    void "Test determineSingleValueFromField"() {
        given:
        FlatFormData derived = new FlatFormData()
        derived.value = value
        derived.values = values as String[]
        
        when:
        Double a = service.determineSingleValueFromField(derived)
        
        then:
        a == result
        
        where:
        value   | values    || result
        "5.0"   | null      || 5.0D
        "2.3"   | null      || 2.3D
        "A"     | null      || null
        null    | ["1.2"]   || 1.2D
    }
    
    void "test calculateTraitAverage"() {
        given:
        List<String> scoreKeys = ["33", "34"]
        
        and:
        FlatForm form = new FlatForm()
        form.elements.add(new FlatFormData(key:"33", value:"2.0"))
        form.elements.add(new FlatFormData(key:"34", value:"3.0"))
        
        when:
        double result = service.calculateTraitAverage(scoreKeys, form)
        
        then:
        result == 2.5D
        
    }
    
    void "test calculateSummaryGroupAverage"() {
        given:
        Form template = new Form(type:"NAVPERS 1616/27 (8-10)")
        
        and:
        FlatForm eval1 = new FlatForm()
        eval1.elements.add(new FlatFormData(key:"33", value:"2.0"))
        eval1.elements.add(new FlatFormData(key:"34", value:"2.0"))
        eval1.elements.add(new FlatFormData(key:"35", value:"2.0"))
        eval1.elements.add(new FlatFormData(key:"36", value:"2.0"))
        eval1.elements.add(new FlatFormData(key:"37", value:"2.0"))
        eval1.elements.add(new FlatFormData(key:"38", value:"2.0"))
        eval1.elements.add(new FlatFormData(key:"39", value:"2.0"))
        
        and:
        FlatForm eval2 = new FlatForm()
        eval2.elements.add(new FlatFormData(key:"33", value:"3.0"))
        eval2.elements.add(new FlatFormData(key:"34", value:"3.0"))
        eval2.elements.add(new FlatFormData(key:"35", value:"3.0"))
        eval2.elements.add(new FlatFormData(key:"36", value:"3.0"))
        eval2.elements.add(new FlatFormData(key:"37", value:"3.0"))
        eval2.elements.add(new FlatFormData(key:"38", value:"3.0"))
        eval2.elements.add(new FlatFormData(key:"39", value:"3.0"))
        
        and:
        FlatFormGroup group = new FlatFormGroup(type:template.type, forms:[eval1, eval2])
        
        when:
        double summaryGroupAverage = service.calculateSummaryGroupAverage(template, group)
        
        then:
        summaryGroupAverage == 2.5D
    }
    
    void "test determineSummaryTraitAverageKey"() {
        when:
        String key = service.determineSummaryTraitAverageKey(type)
        
        then:
        key == result
        
        where:
        type                        || result
        "NAVPERS 1616/27 (8-10)"    || "45.D"
        "NAVPERS 1616/26 (08-10)"   || "50.C"
        "NAVPERS 1610/2 (11-11)"    || "45.D"
        "foo"                       || null
    }
    
    void "test updateSummaryGroupAverage"() {
        given:
        Form template = new Form(type:type)
        
        and:
        FlatFormData data1 = new FlatFormData(key:key)
        FlatForm form1 = new FlatForm(elements:[data1])
        
        and:
        FlatFormData data2 = new FlatFormData(key:key)
        FlatForm form2 = new FlatForm(elements:[data2])
        
        and:
        FlatFormGroup group = new FlatFormGroup(type:template.type, forms:[form1, form2])
        
        when:
        service.updateSummaryGroupAverage(template, group)
        
        then:
        1 * instance.calculateSummaryGroupAverage(template, group) >> 4.5D
        
        and:
        data1.value == "4.50"
        data2.value == "4.50"
        
        where:
        type                        | key
        "NAVPERS 1616/27 (8-10)"    | "45.D"
        "NAVPERS 1616/26 (08-10)"   | "50.C"
        "NAVPERS 1610/2 (11-11)"    | "45.D"
    }
    
    void "test updateRecommendationCounts"() {
        given:
        Form template = new Form(type:type)
        String key = service.determineKeyForRecommendation(template.type)
        List<String> countKeys = service.determineKeysForRecommendationCounts(template.type)
        
        and:
        FlatForm form1 = new FlatForm(elements:[new FlatFormData(key:key, value:"NOB")])
        FlatForm form2 = new FlatForm(elements:[new FlatFormData(key:key, value:"Significant Problems")])
        FlatForm form3 = new FlatForm(elements:[new FlatFormData(key:key, value:"Progressing")])
        FlatForm form4 = new FlatForm(elements:[new FlatFormData(key:key, value:"Promotable")])
        FlatForm form5 = new FlatForm(elements:[new FlatFormData(key:key, value:"Must Promote")])
        FlatForm form6 = new FlatForm(elements:[new FlatFormData(key:key, value:"Early Promote")])
        
        and:
        FlatFormGroup group = new FlatFormGroup(type:template.type, forms:[form1, form2, form3, form4, form5, form6])
        
        and: "put in all of the promotion recommendation counts for each form, meaning SP count, MP count..."
        for (FlatForm form : group.forms) {
            for (String countKey : countKeys) {
                form.elements.add(new FlatFormData(key:countKey))
            }
        }
        
        when:
        service.updateRecommendationCounts(template, group)
        
        then:
        for (FlatForm form : group.forms) {
            form.findFlatFormDataByKey(countKeys.get(CalculationService.SIGNIFICANT_PROBLEMS)).value == sp + ""
            form.findFlatFormDataByKey(countKeys.get(CalculationService.PROGRESSING)).value == prog + ""
            form.findFlatFormDataByKey(countKeys.get(CalculationService.PROMOTABLE)).value == promotable + ""
            form.findFlatFormDataByKey(countKeys.get(CalculationService.MUST_PROMOTE)).value == mp + ""
            form.findFlatFormDataByKey(countKeys.get(CalculationService.EARLY_PROMOTE)).value == ep + ""
        }
        
        where:
        type                        || sp   | prog  | promotable    | mp    | ep
        "NAVPERS 1616/27 (8-10)"    || 1    | 1     | 1             | 1     | 1
        "NAVPERS 1616/26 (08-10)"   || 1    | 1     | 1             | 1     | 1
        "NAVPERS 1610/2 (11-11)"    || 1    | 1     | 1             | 1     | 1
        
    }
    
    void "test determineKeyForRecommendation"() {
        when:
        String r = service.determineKeyForRecommendation(type)
        
        then:
        r == result
        
        where:
        type                        || result
        "NAVPERS 1616/27 (8-10)"    || "42"
        "NAVPERS 1616/26 (08-10)"   || "45"
        "NAVPERS 1610/2 (11-11)"    || "42"
        "foo"                       || null
    }
}
