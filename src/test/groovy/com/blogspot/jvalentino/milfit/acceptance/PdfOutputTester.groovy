package com.blogspot.jvalentino.milfit.acceptance;

import groovy.json.JsonSlurper;

import java.io.File;

import org.apache.pdfbox.pdmodel.PDDocument;

import com.blogspot.jvalentino.milfit.model.FlatForm;
import com.blogspot.jvalentino.milfit.model.FlatFormData;
import com.blogspot.jvalentino.milfit.model.Form;
import com.blogspot.jvalentino.milfit.service.PdfGeneratorService;


/**
 * The purpose of this class is to serve as a shortcut for providing mock
 * data into the PDF Generation part of the system in order to more quickly 
 * developer data to PDF mappings.
 * 
 * This works by taking the given data from generateFormData() and having
 * it generate a locally stored PDF in your workspace output.pdf, and then
 * opening that file as it sits on the system.
 * 
 * This is the fastest way that I could come up with for testing PDF changes.
 * 
 * @author john.valentino
 *
 */
public abstract class PdfOutputTester {
	
	private FlatForm form = new FlatForm();
	private PdfGeneratorService generator = new PdfGeneratorService();
    
    String templateFile

	public PdfOutputTester(String templateFile) {
		
        this.templateFile = templateFile
		
		generateFormData();
		
		
	}
	
	public void generate(boolean openFile) {
		try {
            File templateFile = new File(templateFile)
			new File("target/pdf").mkdirs();
			File output = new File("target/pdf/${templateFile.name}.pdf");
            
           
            String templateJson = templateFile.text
			
            Form template = new JsonSlurper().parseText(templateJson);
            
			PDDocument doc = generator.generatePdf(template, form);
			doc.save( output );
			doc.close();
			
			if (openFile)
				Runtime.getRuntime().exec("open \""+output.getAbsolutePath()+"\"");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	protected abstract void generateFormData();
	
	protected void data(String key, String name, String value, String font) {
		FlatFormData data = new FlatFormData();
		data.setKey(key);
		data.setName(name);
		data.setValue(value);
		data.setFont(font);
		
		form.getElements().add(data);
		
	}
	
	
	protected void data(String key, String name, String value) {
		FlatFormData data = new FlatFormData();
		data.setKey(key);
		data.setName(name);
		data.setValue(value);
		
		form.getElements().add(data);
		
	}
	
	protected void data(String key, String name, String[] values) {
		FlatFormData data = new FlatFormData();
		data.setKey(key);
		data.setName(name);
		data.setValues(values);
		
		form.getElements().add(data);
	}
	
}
