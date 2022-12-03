package com.blogspot.jvalentino.milfit.service

import groovy.util.logging.Slf4j;

import java.awt.Font;
import java.awt.Point;

import com.blogspot.jvalentino.milfit.model.Bounds;
import com.blogspot.jvalentino.milfit.model.DefaultFontMapping;
import com.blogspot.jvalentino.milfit.model.FontSettings;
import com.blogspot.jvalentino.milfit.ui.form.IForm;

/**
 * The purpose of this class is to handle converting to and from the two different coordinate
 * systems. The PDF is in one coordinate system while Swing is in another.
 * 
 * @author john.valentino
 *
 */
@Slf4j
class GeometryService {
    
    static final int DPI_72 = 72
    static final int DPI_100 = 100
    static final int DPI_150 = 150
    static final int DPI_200 = 200
    static final int DPI_250 = 250
    static final int DPI_300 = 300

    static final Integer DPI_PDF = DPI_72
    // FIXME: This offset isn't accurate
    // This indicates the way in which height is calculated is off
    static final Integer SWING_MAX_Y_OFFSET = 12

    /** This is used to adjust font sizes based on DPI, since they are not truly scalable */    
    static List<DefaultFontMapping> defaultFontMappings = []
    static {
        defaultFontMappings.add(new DefaultFontMapping(dpi:DPI_72, originalFontSize:8.0, newFontSize:7.3))
        defaultFontMappings.add(new DefaultFontMapping(dpi:DPI_100, originalFontSize:8.0, newFontSize:7.6))
    }
    
    /**
     * Turns an X value from the PDF coordinate system and turns it into an X
     * value in the Swing coordinate system.
     * 
     * @param dpi
     * @param x
     * @return
     */
    int pdfXtoSwingX(Integer dpi, Double x) {
        Double value = dpi.doubleValue() / DPI_PDF.doubleValue() * x.doubleValue()
        return Math.round(value).intValue()
    }
    
    /**
     * Turns a Y value from the PDF coordinate system into a Y value in the Swing
     * coordinate system.
     * 
     * @param swingMax
     * @param dpi
     * @param y
     * @return
     */
    int pdfYtoSwingY(Integer swingMax, Integer dpi, Double y) {
        
      Double pdfMax = (DPI_PDF.doubleValue() / dpi.doubleValue()) * 
           swingMax.doubleValue() - SWING_MAX_Y_OFFSET.doubleValue()
            
       //Double pdfMax = 780.0 This is the actual height of the PDF letter page
        
        Double value = pdfToSwingRatio(dpi) * 
            (y.doubleValue() - pdfMax.doubleValue()) * -1.0
            
        return Math.round(value).intValue()
    }
    
    /**
     * Turns an X,Y coordinate from the PDF coordinate system into the Swing 
     * coordinate system.
     * 
     * @param dpi
     * @param swingMax
     * @param x
     * @param y
     * @return
     */
    Point pdfToSwing(Integer dpi, Integer swingMax, Double x, Double y) {
        int rX = pdfXtoSwingX(dpi, x)
        int rY = pdfYtoSwingY(swingMax, dpi, y)
        return new Point(rX, rY)
    }
    
    /**
     * Returns the ratio between the PDF coordinate system and the swing coordinate system
     * based on the desired DPI in the Swing coordinate system.
     * 
     * @param dpi
     * @return
     */
    Double pdfToSwingRatio(Integer dpi) {
        return dpi.doubleValue() / DPI_PDF.doubleValue()
    }
    
    /**
     * Takes a value from the PDF coordinate space and turns it into a value in the Swing
     * spaced based on the Swing DPI.
     * 
     * @param dpi
     * @param input
     * @return
     */
    int valueFromPdfToSwing(Integer dpi, Double input) {
        if (input == null) {
            input = 20.0
        }
        Double value = input * pdfToSwingRatio(dpi)
        return Math.round(value).intValue()
    }
    
    Double valueFromPdfToSwingAsDouble(Integer dpi, Double input) {
        if (input == null) {
            input = 20.0
        }
        return input * pdfToSwingRatio(dpi)
    }
    
    /**
     * Takes a rectangle from PDF space and turns it into a rectangle in Swing space
     * 
     * @param dpi
     * @param swingMax
     * @param field
     * @return
     */
    Bounds generateSwingBoundsFromPdf(Integer dpi, int swingMax, 
        Double x, Double y, Double w, Double h) {
        
        Point p = pdfToSwing(
            dpi,
            swingMax,
            x,
            y)
                
        int height = valueFromPdfToSwing(dpi, h)
        
        int width = valueFromPdfToSwing(dpi, w)
        
        return new Bounds(x:p.x, y:p.y, width:width, height:height)
    }
        
    
    
    Double findDefaultFontMapping(Integer dpi, Double originalFontSize) {
        
        for (DefaultFontMapping mapping : defaultFontMappings) {
            if (mapping.dpi == dpi && mapping.originalFontSize == originalFontSize) {
                return mapping.newFontSize
            }
        }
        
        return null
    }
    
    /**
     * Uh, where to start. Fonts are not things which are truly vectored and scalable, so this method
     * is an attempt to make fonts scale proportionally. This means it is possible to have a font size of 
     * 10.5. The purpose of this method is to take a font specification in the PDF coordinate space, which is 
     * at 72 DPI, and convert it to the swing coordinate space at a possibly different DPI.
     *      
     * @param dpi
     * @param templateFontSize
     * @param fontName
     * @param fontType
     * @return
     */
    Font generateDerivativeFont(Integer dpi, Double templateFontSize, String fontName, int fontType) {
        
        // Fonts don't scale, so in some cases we just give up and manually set the font size to make it fit
        Double defaultFontSize = findDefaultFontMapping(dpi, templateFontSize)
        
        if (defaultFontSize != null) {
            templateFontSize = defaultFontSize
        }
                
        Double fontSize = valueFromPdfToSwingAsDouble(dpi, templateFontSize)
        
        // if the font is bigger than 20, subtract .5
        if (fontSize >= 20.0) {
            fontSize -= 0.5
        }        
        
        //fontSize = Math.floor(fontSize)
        
        int fontSizeInt = Math.round(fontSize).intValue()
        
        //log.info("DPI: ${dpi}, Font Size: ${fontSizeInt}, Derived Font Size: ${fontSize}")
        
        Font font = new Font(fontName, fontType, fontSizeInt)
        Font deriveFont = font.deriveFont(fontSize.floatValue())
        
        return deriveFont
    }
    
    /**
     * Handles the case where a component has multiple available fonts, which each of those
     * fonts has a corresponding maximum number of characters per line. Given a specified font,
     * this method determines the appropriate font size and maximum characters per line.
     * 
     * @param specifiedFont
     * @param availableFonts
     * @param maxCharactersPerFont
     * @return
     */
    FontSettings determineFontSettings(String specifiedFont, Float[] availableFonts, 
        Integer[] maxCharactersPerFont, Double fontSize, Integer maxChars) {
        
        if (availableFonts == null) {
            return new FontSettings(
                font:fontSize,
                maxChars:maxChars)
        }
        
        if (specifiedFont == null) {
            return new FontSettings(
                font:availableFonts[0].doubleValue(), 
                maxChars:maxCharactersPerFont[0])
        }
        
        Double fontValue = Double.parseDouble(specifiedFont)
        
        int index = -1
        for (int i = 0; i < availableFonts.length; i++) {
            if (fontValue == availableFonts[i]) {
                index = i
                break
            }
        }
        
        // if the specified font cannot be found...
        if (index == -1) {
            // return the first setting
            return new FontSettings(
                font:availableFonts[0].doubleValue(),
                maxChars:maxCharactersPerFont[0])
        }
        
        return new FontSettings(
            font:availableFonts[index].doubleValue(),
            maxChars:maxCharactersPerFont[index])
    }
    
}
