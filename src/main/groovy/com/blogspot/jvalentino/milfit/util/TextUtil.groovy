package com.blogspot.jvalentino.milfit.util;

import groovy.util.logging.Slf4j;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.util.Map;
import java.util.StringTokenizer;

import com.blogspot.jvalentino.milfit.model.FormElementType;
import com.blogspot.jvalentino.milfit.ui.form.dialog.AttributeType;

@Slf4j
class TextUtil {
	
    static Map<String, AttributeType> attributeMap = generateAttributeMap()
    
    static Map<String, AttributeType> generateAttributeMap() {
        Map<String, AttributeType> map = [:]
        
        map.put("java.lang.String", AttributeType.STRING)
        map.put("com.blogspot.jvalentino.milfit.model.FormElementType", AttributeType.FORM_ELEMENT_TYPE)
        map.put("java.lang.Double", AttributeType.DOUBLE)
        map.put("java.lang.Integer", AttributeType.INTEGER)
        map.put("[Ljava.lang.String;", AttributeType.STRING_ARRAY)
        map.put("[Ljava.lang.Double;", AttributeType.DOUBLE_ARRAY)
        map.put("java.lang.Double", AttributeType.DOUBLE)
        map.put("java.lang.Integer", AttributeType.INTEGER)
        map.put("[Ljava.lang.Float;", AttributeType.FLOAT_ARRAY)
        map.put("[Ljava.lang.Integer;", AttributeType.INTEGER_ARRAY)
        
        return map
    }
    
	public static String wrapMonospace(String text) {
		
		if (text == null)
			return null;
		
		if (text.length() == 0)
			return text;
		
		//gsub('(.{1,90})(\\s|$)', '\\1\n', s)
		//return text.replaceAll("(.{1,96})(\\s|$)", "\\1\n");
		
		String[] split = splitIntoLine(text, 96);
		
		String result = "";
		for (int i= 0; i < split.length; i++) {
			if (i != split.length - 1) {
				result += split[i] + "\n";
			} else {
				result += split[i];
			}
		}
		
		return result;
		
	}
	
	public static String[] splitIntoLineBad(String input, int maxCharInLine){

	    StringTokenizer tok = new StringTokenizer(input, " ");
	    StringBuilder output = new StringBuilder(input.length());
	    int lineLen = 0;
	    while (tok.hasMoreTokens()) {
	        String word = tok.nextToken();

	        while(word.length() > maxCharInLine){
	            output.append(word.substring(0, maxCharInLine-lineLen) + "\n");
	            word = word.substring(maxCharInLine-lineLen);
	            lineLen = 0;
	        }

	        if (lineLen + word.length() > maxCharInLine) {
	            output.append("\n");
	            lineLen = 0;
	        }
	        output.append(word + " ");

	        lineLen += word.length() + 1;
	    }
	    return output.toString().split("\n");
	}
	
	public static String[] splitIntoLine(String input, int max) {
		String result = wordWrap(input, max);
		return result.split("\n");
	}
	
	public static String wrapWithIndexProblem(String input, int len) {
		input = input.trim();
		if (input.length() < len)
			return input;
		if (input.substring(0, len).contains("\n"))
			return input.substring(0, input.indexOf("\n")).trim() + "\n\n"
					+ wrapWithIndexProblem(input.substring(input.indexOf("\n") + 1), len);
		int place = Math.max(
				Math.max(input.lastIndexOf(" ", len), input.lastIndexOf("\t", len)),
				input.lastIndexOf("-", len));
		if (place == -1) {
			System.out.println("foo");
		}
		return input.substring(0, place).trim() + "\n"
				+ wrapWithIndexProblem(input.substring(place), len);
	}
	
	//|| wordWrap
	
	private static final String newline = "\n";
	
	/**
	 * Performs word wrapping.  Returns the input string with long lines of
	 * text cut (between words) for readability.
	 * 
	 * @param input     text to be word-wrapped
	 * @param length number of characters input a line
	 */
	public static String wordWrap(String input, int length) {
	    //:: Trim
	    while(input.length() > 0 && (input.charAt(0) == '\t' || input.charAt(0) == ' '))
	        input = input.substring(1);
	    
	    //:: If Small Enough Already, Return Original
	    if(input.length() < length)
	        return input;
	    
	    //:: If Next length Contains Newline, Split There
	    if(input.substring(0, length).contains(newline))
	        return input.substring(0, input.indexOf(newline)).trim() + newline +
	               wordWrap(input.substring(input.indexOf("\n") + 1), length);
	    
	    //:: Otherwise, Split Along Nearest Previous Space/Tab/Dash
	    int spaceIndex = Math.max(Math.max( input.lastIndexOf(" ",  length),
	                                        input.lastIndexOf("\t", length)),
	                                        input.lastIndexOf("-",  length));
	    
	    //:: If No Nearest Space, Split At length
	    if(spaceIndex == -1)
	        spaceIndex = length;
	    
	    //:: Split
	    return input.substring(0, spaceIndex).trim() + newline + wordWrap(input.substring(spaceIndex), length);
	}
    
    /**
     * This uses a font metrics object in order to determine the width needed to display
     * a single line of text in the given font type and size. This of course assumes a mono spaced
     * font.
     * 
     * @param fontMetrics
     * @param max
     * @return
     */
    static int determineWidthBasedOnMaxCharsPerLine(FontMetrics fontMetrics, int max) {
        
        String message = ""
        for (int i = 0; i < max; i++) {
            message += "W"
        }
        
        int width = fontMetrics.stringWidth(message)
        
        return width
    }
    
    static Object getValueForString(String text, AttributeType type) {
        
        if (text.length() == 0) {
            return null
        }
        
        Object result = null
        
        switch (type) {
            case AttributeType.STRING:
               result = text
            break
            
            case AttributeType.FORM_ELEMENT_TYPE:
                result =  FormElementType.values().find {
                    it.toString() == text
                }
            break
            
            case AttributeType.DOUBLE:
               try {
                   result = Double.parseDouble(text)
               } catch (NumberFormatException e) {
                   log.error("${text} is not a valid double", e)
               }
            break
            
            case AttributeType.INTEGER:
                try {
                    result = Integer.parseInt(text)
                } catch (NumberFormatException e) {
                    log.error("${text} is not a valid integer", e)
                }
            break
            
            case AttributeType.STRING_ARRAY:
                result = this.convertCommaSeparatedTextIntoArray(text)
            break
            
            case AttributeType.DOUBLE_ARRAY:
                String[] values = this.convertCommaSeparatedTextIntoArray(text)
                result = new Double[values.length]
                try {
                    for (int i = 0; i < values.length; i++) {
                        result[i] = Double.parseDouble(values[i])
                    }
                } catch (NumberFormatException e) {
                    log.error("${text} is not a valid list of doubles", e)
                }
            break
            
            case AttributeType.FLOAT_ARRAY:
                String[] values = this.convertCommaSeparatedTextIntoArray(text)
                result = new Float[values.length]
                try {
                    for (int i = 0; i < values.length; i++) {
                        result[i] = Float.parseFloat(values[i])
                    }
                } catch (NumberFormatException e) {
                    log.error("${text} is not a valid list of floats", e)
                }
            break
            
            case AttributeType.INTEGER_ARRAY:
                String[] values = this.convertCommaSeparatedTextIntoArray(text)
                result = new Integer[values.length]
                try {
                    for (int i = 0; i < values.length; i++) {
                        result[i] = Integer.parseInt(values[i])
                    }
                } catch (NumberFormatException e) {
                    log.error("${text} is not a valid list of integers", e)
                }
            break
            
            default:
                log.error("There is no support for type ${type}")
            
        }
        
        return result
    }
    
    static String[] convertCommaSeparatedTextIntoArray(String text) {
        String[] split = text.split(",")
        String[] results = new String[split.length]
        
        for (int i = 0; i < split.length; i++) {
            results[i] = split[i].trim()
        }
        return results
    }
    
    static String getStringValueForObject(Object v, AttributeType type) {
        
        if (v == null) {
            return ""
        }
                
        switch (type) {
            case AttributeType.STRING:
                return (String) v
            break
            
            case AttributeType.FORM_ELEMENT_TYPE:
                FormElementType fT = v
                return fT.toString()
            break
            
            case AttributeType.DOUBLE:
                return ((Double) v).toString()
            break
            
            case AttributeType.INTEGER:
                return ((Integer) v).toString()
            break
            
            case AttributeType.STRING_ARRAY:
                return arrayToString(v)
            break
            
            case AttributeType.DOUBLE_ARRAY:
                Double[] results = v
                String result = ""
                for (Double current : results) {
                    result += current.toString() + ", "
                }
                return result[0..-3]
            break
            
            case AttributeType.FLOAT_ARRAY:
                Float[] results = v
                String result = ""
                for (Float current : results) {
                    result += current.toString() + ", "
                }
                return result[0..-3]
            break
            
            case AttributeType.INTEGER_ARRAY:
                Integer[] results = v
                String result = ""
                for (Integer current : results) {
                    result += current.toString() + ", "
                }
                return result[0..-3]
            break
            
            default:
                log.error("There is no support for type ${type}")
            
        }
        
        return null
    }
    
    static String arrayToString(String[] v) {
        String[] results = v
        String result = ""
        for (String current : results) {
            result += current + ", "
        }
        return result[0..-3]
    }
    
	
}
