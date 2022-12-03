package com.blogspot.jvalentino.milfit.model

import java.util.ArrayList
import java.util.List



class FlatForm {

	// FormData
	List<FlatFormData> elements = []
	String type
	
    FlatFormData findFlatFormDataByKey(String key) {
        for (FlatFormData element : elements) {
            if (element.key == key) {
                return element
            }
        }
        return null
    }
    
}
