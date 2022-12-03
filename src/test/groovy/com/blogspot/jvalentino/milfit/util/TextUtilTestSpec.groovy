package com.blogspot.jvalentino.milfit.util

import com.blogspot.jvalentino.milfit.model.FormElementType;
import com.blogspot.jvalentino.milfit.ui.form.dialog.AttributeType;

import spock.lang.Specification;

class TextUtilTestSpec extends Specification {

    void "Test getStringValueForObject"() {
        when:
        String r = TextUtil.getStringValueForObject(v, type)
        
        then:
        r == result
        
        where:
        v                       | type                              || result
        new String("hello")     | AttributeType.STRING              || "hello"
        null                    | AttributeType.STRING              || ""
        FormElementType.TEXT    | AttributeType.FORM_ELEMENT_TYPE   || "TEXT"
        new Double(1.5)         | AttributeType.DOUBLE              || "1.5"
        new Integer(1)          | AttributeType.INTEGER             || "1"
        ["A", "B"] as String[]  | AttributeType.STRING_ARRAY        || "A, B"
        [1.5, 2.3] as Double[]  | AttributeType.DOUBLE_ARRAY        || "1.5, 2.3"
        [1.5, 2.3] as Float[]   | AttributeType.FLOAT_ARRAY         || "1.5, 2.3"
        [1, 2] as Integer[]     | AttributeType.INTEGER_ARRAY       || "1, 2"
    }
    
    void "Test getValueForString"() {
        when:
        Object r = TextUtil.getValueForString(text, type)
        
        then:
        r == result
        
        where:
        text            | type                              || result
        "hi"            | AttributeType.STRING              || "hi"
        "TEXT"          | AttributeType.FORM_ELEMENT_TYPE   || FormElementType.TEXT
        "2.3"           | AttributeType.DOUBLE              || new Double(2.3)
        "3"             | AttributeType.INTEGER             || new Integer(3)
        "A, B, C"       | AttributeType.STRING_ARRAY        || ["A", "B", "C"] as String[]
        ""              | AttributeType.STRING_ARRAY        || null
        "A"             | AttributeType.STRING_ARRAY        || ["A"] as String[]
        "2.3, 4.5"      | AttributeType.DOUBLE_ARRAY        || [2.3, 4.5] as Double[]
        "2.3"           | AttributeType.DOUBLE_ARRAY        || [2.3] as Double[]
        "2.3, 4.5"      | AttributeType.FLOAT_ARRAY         || [2.3, 4.5] as Float[]
        "1, 2"          | AttributeType.INTEGER_ARRAY       || [1, 2] as Integer[]
    }
}
