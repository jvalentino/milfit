package com.blogspot.jvalentino.milfit.service

import java.awt.Point;

import com.blogspot.jvalentino.milfit.model.Bounds;
import com.blogspot.jvalentino.milfit.model.FontSettings;

import spock.lang.Specification

class GeometryServiceTestSpec extends Specification {

    GeometryService service
    
    def setup() {
        service = new GeometryService()
    }
    
    void "test pdfXtoSwingX"() {
        when:
        int result = service.pdfXtoSwingX(dpi, x)
        
        then:
        rX == result
        
        where:
        dpi     | x     || rX
        72      | 100.0 || 100
        100     | 100.0 || 139
        144     | 100.0 || 200
    }
    
    void "test pdfYtoSwingY"() {
        when:
        int result = service.pdfYtoSwingY(swingMax, dpi, y)
        
        then:
        rY == result
        
        where:
        dpi     | swingMax  | y         || rY
        72      | 1200      | 100.0     || 1088
        100     | 1200      | 100.0     || 1044
        144     | 1200      | 100.0     || 976
    }
    
    void "test pdfToSwing"() {
        when:
        Point p = service.pdfToSwing(dpi, swingMax, x, y)
        
        then:
        rX == p.x
        rY == p.y
        
        where:
        dpi     | swingMax  | x     | y     || rX   | rY
        72      | 1200      | 100.0 | 100.0 || 100  | 1088
        100     | 1200      | 100.0 | 100.0 || 139  | 1044
        144     | 1200      | 100.0 | 100.0 || 200  | 976
    }
    
    void "test valueFromPdfToSwing"() {
        when:
        int result = service.valueFromPdfToSwing(dpi, input)
        
        then:
        result == value
        
        where:
        dpi | input || value
        72  | 100.0 || 100
        100 | 100.0 || 139
        72  | null  || 20
    }
    
    void "Test generateSwingBoundsFromPdf"() {
        when:
        Bounds b = service.generateSwingBoundsFromPdf(dpi, swingMax, x, y, w, h)
        
        then:
        rX == b.x
        rY == b.y
        rW == b.width
        rH == b.height
        
        where:
        dpi     | swingMax  | x     | y     | w     | h     || rX   | rY    | rW    | rH
        72      | 1200      | 100.0 | 100.0 | 10    | 20    || 100  | 1088  | 10    | 20
        100     | 1200      | 100.0 | 100.0 | 10    | 20    || 139  | 1044  | 14    | 28
        144     | 1200      | 100.0 | 100.0 | 10    | 20    || 200  | 976   | 20    | 40
        
    }
    
    void "Test determineFontSettings"() {
        given:
        String specifiedFont = font
        Float[] availableFonts = available as Float[]
        Integer[] maxCharactersPerFont = [100, 120] as Integer[]
        
        when:
        FontSettings result = service.determineFontSettings(
            specifiedFont, availableFonts, maxCharactersPerFont,
            fontSize, maxChars)
        
        then:
        result.font == rSize
        result.maxChars == rMaxChars
        
        where:
        fontSize | maxChars | available     | font    || rSize    | rMaxChars
        null     | null     | [10.0, 12.0]  | null    || 10.0     | 100
        null     | null     | [10.0, 12.0]  | "23"    || 10.0     | 100
        null     | null     | [10.0, 12.0]  | "10"    || 10.0     | 100
        null     | null     | [10.0, 12.0]  | "12"    || 12.0     | 120
        10.0     | 56       | null          | null    || 10.0     | 56
    }
    
    
    
}
