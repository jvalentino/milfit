package com.blogspot.jvalentino.milfit.service;

import groovy.util.logging.Slf4j;

import java.awt.Color;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.edit.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.imgscalr.Scalr;
import org.imgscalr.Scalr.Method;
import org.imgscalr.Scalr.Mode;

import com.blogspot.jvalentino.milfit.model.FlatForm;
import com.blogspot.jvalentino.milfit.model.FlatFormData;
import com.blogspot.jvalentino.milfit.model.Form;
import com.blogspot.jvalentino.milfit.model.FormElement;
import com.blogspot.jvalentino.milfit.model.FormElementType;
import com.blogspot.jvalentino.milfit.util.TextUtil;

@Slf4j
public class PdfGeneratorService {

	
	
	
	/**
	 * This is just a hack to pull the PDF file from the local file system instead of the database.
	 * The purpose is to allow a quick and local way to play around with the PDF settings.
	 * @param version
	 * @return
	 * @throws Exception
	 */
	private byte[] getPdfFileFromLocalFile(String inFile) throws Exception {
		
		FileInputStream stream = new FileInputStream(inFile);
		byte[] bytes = IOUtils.toByteArray(stream);
		stream.close();
		return bytes;
		
	}
	
	
	
	public PDDocument generatePdf(Form template, FlatForm form) throws Exception {
		
       
        
		byte[] bytes = this.getPdfFileFromLocalFile(template.file)
		
		ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
		
		PDDocument doc = PDDocument.load( bis );
		bis.close();
        
		
		// create a hash map of the flat form data
		Map<String, FlatFormData> map = new HashMap<String, FlatFormData>();
		for (FlatFormData data : form.getElements()) {
			//System.out.println(data.getKey()+" = "+data.getValue());
			map.put(data.getKey(), data);
		}
				
		List<FormElement> format = template.elements //factory.getFormat(form.getVersion());
		
		generate(1, doc, format, map);
		generate(2, doc, format, map);
		                        
		return doc;
	}
	
	private void drawGraph(PDPageContentStream stream) throws Exception {
		PDFont font = PDType1Font.COURIER;
		
		boolean alternate = true;
		for (int i = 740; i > 0; i -= 20) {
			if (alternate) {
				stream.setNonStrokingColor( Color.red );
				stream.setStrokingColor(Color.red);
			} else {
				stream.setNonStrokingColor( Color.blue );
				stream.setStrokingColor(Color.blue);
			}
			stream.drawLine(20, (float) i, 600, (float) i);
			stream.setFont( font, 8.0f );
			stream.beginText();
			stream.setTextTranslation(0, (double)i - 2);
			stream.drawString( i+"" );
			stream.endText();
			alternate = !alternate;
		}
		
		for (int i = 20; i < 700; i+= 20) {
			if (alternate) {
				stream.setNonStrokingColor( Color.green.darker() );
				stream.setStrokingColor(Color.green.darker());
			} else {
				stream.setNonStrokingColor( Color.orange );
				stream.setStrokingColor(Color.orange);
			}
			stream.drawLine((float) i, 0, (float) i, 750);
			stream.setFont( font, 8.0f );
			stream.beginText();
			stream.setTextTranslation((double) i - 5, 750);
			stream.drawString( i+"" );
			stream.endText();
			alternate = !alternate;
		}
		
	}
	
    String getValueFromMapAsString(Map<String, FlatFormData> map, String key) {
        if (map == null) {
            return ""
        }
        
        if (map.get(key) == null) {
            return ""
        }
        
        if (map.get(key).getValue() == null) {
            return ""
        }
        return map.get(key).getValue()
    }
    
	private void generate(int page, PDDocument doc, List<FormElement> format, Map<String, FlatFormData> map) throws Exception {
		
		PDFont font = PDType1Font.COURIER;
		
		@SuppressWarnings("unchecked")
		List<PDPage> pages = doc.getDocumentCatalog().getAllPages();
		
		PDPageContentStream stream = new PDPageContentStream(doc, pages.get(page-1), true, true,true);
		
		
		//this.drawGraph(stream);
		
		
		for (FormElement element: format) {
			
			if (element.getPage() != page)
				continue;
			
			String key = element.getKey();
			// handle increments on a key, for example 1-2 means key 1 increment 2.
			if (key.contains("-")) {
				key = key.split("-")[0];
			}
			
			stream.setNonStrokingColor(0, 0, 0);
			
			float fontSize = element.getFont();
			
			
			
			if (map.get(key) != null && map.get(key).getFont() != null) {
				if (map.get(key).getFont().equals("10")) {
					fontSize = 10.0f;
				} else
					fontSize = 12.0f;
			}
			
			stream.setFont( font, fontSize );
			
			switch(element.getType()) {
			case FormElementType.TEXT:
				stream.beginText();
				stream.setTextTranslation(element.getX(), element.getY());
                
                String v = this.getValueFromMapAsString(map, key)
                
				stream.drawString(v);
				stream.endText();
				break;
			case FormElementType.TEXT_WRAP:
				String text = this.getValueFromMapAsString(map, key)
				Integer maxChars = element.getMaxChars();
                
                if (maxChars == null) {
                    maxChars = 0
                }
                
				if (element.getFonts() != null) {
					if (fontSize == 10.0)
						maxChars = element.getMaxCharss()[0];
					else
						maxChars = element.getMaxCharss()[1];
				}
				
				String[] wrap = TextUtil.splitIntoLine(text, maxChars);
				double cY = element.getY();
				for (String line : wrap) {
					stream.beginText();
					stream.setTextTranslation(element.getX(), cY);
					stream.drawString(line);
					stream.endText();
					
					if (fontSize == 10.0f)
						cY -= 11.5;
					else if (fontSize == 12.0f)
						cY -= 14.0;
					else
						cY -=7.5;
				}
				break;
			case FormElementType.CHECK_BOX_BOOLEAN:
				if (map.get(key).getValue().equals("true")) {
					stream.beginText();
					stream.setTextTranslation(element.getX(), element.getY());
					stream.drawString( "X" );
					stream.endText();
				}
				break;
			case FormElementType.CHECK_BOX_MULTI:
				String[] values = map.get(key).getValues();
				for (int i = 0; i < element.getSelections().length; i++) {
					String selection = element.getSelections()[i];
					
					for (String value : values) {
						if (value.equals(selection)) {
							double x = element.getXs()[i];
							stream.beginText();
							stream.setTextTranslation(x, element.getY());
							stream.drawString("X" );
							stream.endText();
							break;
						}
					}
				}
				break;
			case FormElementType.CHECK_BOX_SINGLE:
				String value = map.get(key).getValue();
				for (int i = 0; i < element.getSelections().length; i++) {
					String selection = element.getSelections()[i];
					
					if (value.equals(selection)) {
						double x = element.getXs()[i];
						stream.beginText();
						stream.setTextTranslation(x, element.getY());
						stream.drawString("X");
						stream.endText();
						break;
					}
					
				}
				break;
			case FormElementType.WHITE_BOX:
				stream.setNonStrokingColor( Color.white );
				float x = Float.parseFloat(element.getX()+"");
				float y = Float.parseFloat(element.getY()+"");
				float width = Float.parseFloat(element.getWidth()+"");
				float height = Float.parseFloat(element.getHeight()+"");
		        stream.fillRect( x, y, width, height );
				break;
			}
			
		}
		
		stream.close();
		
		
	}
    
    List<BufferedImage> pdfToImages(String pdfFilename, int dpi) {
        List<BufferedImage> images = []
        
        PDDocument document = PDDocument.loadNonSeq(new File(pdfFilename), null);
        List<PDPage> pdPages = document.getDocumentCatalog().getAllPages();
        for (PDPage pdPage : pdPages)
        {
            BufferedImage bim = pdPage.convertToImage(BufferedImage.TYPE_INT_RGB, 300)
            
            // resize to dpi level
            int height = bim.getHeight() * dpi / 300
            int width = bim.getWidth() * dpi / 300
                       //Method.ULTRA_QUALITY
            images.add(Scalr.resize(bim, Method.ULTRA_QUALITY, width, height))
        }
        document.close()
        return images
    }
    
    int percentToDpi(String percent) {
        if (!percent.endsWith("%")) {
            return 100
        }
        
        String value = percent[0..-2]
        
        try {
            return Integer.parseInt(value)
        } catch (NumberFormatException e) {
            return 100
        }        
    }
	
}
