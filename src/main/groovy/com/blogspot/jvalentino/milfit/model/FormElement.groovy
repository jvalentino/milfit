package com.blogspot.jvalentino.milfit.model

class FormElement {

    /** A method for uniquely identifying a form element, which can take into account
     * form elements that share the same sequence but on another page. Example: 33 
     * 33-1 would use the value of 33 somewhere else on the form */
	String key
    /** A friendly name representation of the form element. Example: Professional Expertise */
	String name
    /** The type of this element. Example: TEXT */
	FormElementType type
    /** The X position of this element on the PDF */
	Double x = 0
    /** The Y position of this element on the PDF */
	Double y = 0
    /** The page on which this element is drawn */
	Integer page
    /** Represents values for check boxes */
	String[] selections
    /** For check boxes, represents the X positions of each box */
	Double[] xs
    /** The font size of the text input */
	Double font = 12.0f
    /** For wrapped text types, the max number of characters allowed */
	Integer maxChars = null
    /** Allows the support of multiple font sizes for a form element */
	Float[] fonts
    /** For the case of allowing multiple font sizes, this is the max number of characters
     * for each of the allowed font sizes */
	Integer[] maxCharss
    /** If drawing a shape, this is the width of that shape */
	Double width
    /** If drawing a shape, this is the height of that shape */
	Double height = 14.0
    /** Used to offset the x position of this component on the screen (swing) */
    Double visualOffsetX = 0.0
    /** Used to offset the y position of this component on the screen (swing) */
    Double visualOffsetY = 0.0
    /** Optional method for deriving the value of a data element based on an expression */
    String[] deriveValueFrom
    String deriveValueUsing
    /** In derivative values this is the decimal format used to round the number */
    String valueFormat
    
    
    FormElement() {
        super()
    }
	
	public FormElement(Integer page, String key, String name, FormElementType type, Double x,
			Double y) {
		super()
		this.key = key
		this.name = name
		this.type = type
		this.x = x
		this.y = y
		this.page = page
	}
	
	public FormElement(Integer page, String key, String name, FormElementType type, Double x,
			Double y, Double width, Double height) {
		super()
		this.key = key
		this.name = name
		this.type = type
		this.x = x
		this.y = y
		this.page = page
		this.width = width
		this.height = height
	}
	
	
	
	public FormElement(Integer page, String key, String name, FormElementType type, Float font, Integer maxChars, Double x,
			Double y) {
		super()
		this.key = key
		this.name = name
		this.type = type
		this.x = x
		this.y = y
		this.page = page
		this.font = font
		this.maxChars = maxChars
	}
	
	public FormElement(Integer page, String key, String name, FormElementType type, Float[] fonts, Integer[] maxCharss, Double x,
			Double y) {
		super()
		this.key = key
		this.name = name
		this.type = type
		this.x = x
		this.y = y
		this.page = page
		this.fonts = fonts
		this.maxCharss = maxCharss
	}
	
	public FormElement(Integer page, String key, String name, FormElementType type, Integer maxChars, Double x,
			Double y) {
		super()
		this.key = key
		this.name = name
		this.type = type
		this.x = x
		this.y = y
		this.page = page
		this.maxChars = maxChars
	}
	
	public FormElement(Integer page, String key, String name, FormElementType type,
			String[] selections, Double[] xs, Double y) {
		super()
		this.key = key
		this.name = name
		this.type = type
		this.y = y
		this.page = page
		this.selections = selections
		this.xs = xs
	}
	

}
