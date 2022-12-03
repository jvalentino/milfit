package com.blogspot.jvalentino.milfit.service

import java.text.DecimalFormat;
import java.text.NumberFormat;

import com.blogspot.jvalentino.milfit.model.FlatForm;
import com.blogspot.jvalentino.milfit.model.FlatFormData;
import com.blogspot.jvalentino.milfit.model.FlatFormGroup;
import com.blogspot.jvalentino.milfit.model.Form;
import com.blogspot.jvalentino.milfit.model.FormElement;

class CalculationService {

    static final String DECIMAL_FORMAT = "#0.00"
    static final int SIGNIFICANT_PROBLEMS = 0
    static final int PROGRESSING = 1
    static final int PROMOTABLE = 2
    static final int MUST_PROMOTE = 3
    static final int EARLY_PROMOTE = 4
    
    CalculationService instance = this
    
    /**
     * Updates the summary group average for the given group of forms (evals/fitreps)
     * 
     * @param template
     * @param group
     */
    void updateSummaryGroupAverage(Form template, FlatFormGroup group) {
        
        Double summaryGroupAverage = instance.calculateSummaryGroupAverage(template, group)
        
        if (summaryGroupAverage == null) {
            return
        }
        
        // determine which field is the summary group average
        String summaryKey = this.determineSummaryTraitAverageKey(template.type)
        
        // update the summary group average in every form within the group
        for (FlatForm form : group.forms) {
            FlatFormData data = form.findFlatFormDataByKey(summaryKey)
            
            NumberFormat formatter = new DecimalFormat(DECIMAL_FORMAT)
            data.value = formatter.format(summaryGroupAverage)                       
        }
        
    }
    
    /**
     * Calculates the summary group average for the given list of FITREPS/Evals 
     * (flat form group).
     * 
     * @param template
     * @param group
     * @return
     */
    Double calculateSummaryGroupAverage(Form template, FlatFormGroup group) {
        List<String> scoreKeys = this.determineKeysForScoring(template.type)
        
        // for each form in the group....
        double sum = 0
        int total = 0
        for (FlatForm form : group.forms) {
            
            Double average = this.calculateTraitAverage(scoreKeys, form)
            
            if (average == null) {
                continue
            }
            
            total++
            sum += average
        }
        
        if (total == 0) {
            return null
        }
        
        return sum / total.doubleValue()
    }
    
    /**
     * Given a list of keys that represent the trait average (score) fields,
     * calculates the average for the given form.
     * 
     * @param scoreKeys
     * @param form
     * @return
     */
    Double calculateTraitAverage(List<String> scoreKeys, FlatForm form) {
        // calculate the trait average
        double sum = 0.0
        double elementsInSum = 0
        
        // for each score (trait) key...
        for (String key : scoreKeys) {
            FlatFormData element = form.findFlatFormDataByKey(key)
            
            if (element == null) {
                continue
            }
            
            Double value = this.determineSingleValueFromField(element)
            
            if (value == null) {
                continue
            }
            
            sum += value
            elementsInSum++
            
        }
        
        if (elementsInSum == 0) {
            return null
        }
        
        return sum / elementsInSum
    }
    
    /**
     * This is a manual method for determining based on template type, which
     * keys are used in calculating the score. TODO: Consider replacing this with
     * a configuration file as a part of the template JSON at a later date.
     * 
     * @param type
     * @return
     */
    List<String> determineKeysForScoring(String type) {
        return ["33", "34", "35", "36", "37", "38", "39"]
    }
    
    /**
     * Method for determining a double-based value on a flat form data object, where
     * if the field is blank then null is returned. In the event that this object
     * has a list of values, then the first value is returned.
     * 
     * @param derived
     * @return
     */
    Double determineSingleValueFromField(FlatFormData derived) {
        String value = derived.value
        
        if (value == null && derived.values != null && derived.values.length == 1) {
            value = derived.values[0]
        }
        
        try {
            return Double.parseDouble(value)
        } catch (Exception e) {
            return null
        }
    }
    
    /**
     * Determines the field key that represents the summary group average based on the form type.
     * TODO: consider moving this into an external configuration
     * 
     * @param type
     * @return
     */
    String determineSummaryTraitAverageKey(String type) {
        switch (type) {
            case "NAVPERS 1616/27 (8-10)":
                return "45.D"
            case "NAVPERS 1616/26 (08-10)":
                return "50.C"
            case "NAVPERS 1610/2 (11-11)":
                return "45.D"
        }
        return null
    }
    
    String determineKeyForRecommendation(String type) {
        switch (type) {
            case "NAVPERS 1616/27 (8-10)":
                return "42"
            case "NAVPERS 1616/26 (08-10)":
                return "45"
            case "NAVPERS 1610/2 (11-11)":
                return "42"
        }
        return null
    }
    
    List<String> determineKeysForRecommendationCounts(String type) {
        switch (type) {
            case "NAVPERS 1616/27 (8-10)":
                return ["43.A", "43.B", "43.C", "43.D", "43.E"]
            case "NAVPERS 1616/26 (08-10)":
                return ["46.A", "46.B", "46.C", "46.D", "46.E"]
            case "NAVPERS 1610/2 (11-11)":
                return ["43.A", "43.B", "43.C", "43.D", "43.E"]
        }
        return null
    }
    
    /**
     * Handles updating the counts for Significant Problems, Progressing, Promotable,
     * Must Promote, and Early Promotion recommendations in the group of given forms
     * (fitreps/evals).
     * 
     * @param template
     * @param group
     */
    void updateRecommendationCounts(Form template, FlatFormGroup group) {
        String promotionKey = this.determineKeyForRecommendation(template.type)
        
        // count the different number of promotion recommendations
        int significantProblems = 0
        int progressing = 0
        int promotable = 0
        int mustPromote = 0
        int earlyPromote = 0
        
        // for each form in the group...
        for (FlatForm form : group.forms) {
            FlatFormData element = form.findFlatFormDataByKey(promotionKey)
            
            switch(element.value) {
                case "Significant Problems":
                    significantProblems++
                    break
                case "Progressing":
                    progressing++
                    break
                case "Promotable":
                    promotable++
                    break
                case "Must Promote":
                    mustPromote++
                    break
                case "Early Promote":
                    earlyPromote++
                    break
            }
        }
        
        List<String> promotionCountKeys = this.determineKeysForRecommendationCounts(template.type)
        
        // update the promotion recommendations in every form
        for (FlatForm form : group.forms) {
            form.findFlatFormDataByKey(promotionCountKeys.get(SIGNIFICANT_PROBLEMS)).value = significantProblems + ""
            form.findFlatFormDataByKey(promotionCountKeys.get(PROGRESSING)).value = progressing + ""
            form.findFlatFormDataByKey(promotionCountKeys.get(PROMOTABLE)).value = promotable + ""
            form.findFlatFormDataByKey(promotionCountKeys.get(MUST_PROMOTE)).value = mustPromote + ""
            form.findFlatFormDataByKey(promotionCountKeys.get(EARLY_PROMOTE)).value = earlyPromote + ""
        }
    }
    

}
