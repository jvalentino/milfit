package com.blogspot.jvalentino.milfit.component

import javax.swing.*;
import javax.swing.filechooser.*;

import org.apache.commons.io.FilenameUtils;


class CustomFileFilter extends FileFilter {
	
	private String[] extensions
	
	CustomFileFilter(String extension) {
		this.extensions = [extension]
	}
	
	CustomFileFilter(String[] extensions) {
		this.extensions = extensions
	}
	
	public boolean accept(File f) {
		if (f.isDirectory()) {
			return true;
		}
		
		for (String extension : extensions) {
		
			if (f.getName().toLowerCase().endsWith(extension.toLowerCase())) {
                return true
            }
		}
		
		return false;
	}
 
	//The description of this filter
	public String getDescription() {
		String supported = arrayToCommaSeparatedString(extensions)
		return supported + " files only";
	}
    
    /**
     * Utility for taking an array of anything and turning it to a comma separated string
     * @param list
     * @return
     */
    @SafeVarargs
    static <T> String arrayToCommaSeparatedString(final T[] list) {
        String result = ""
        for (int i = 0; i < list.length; i++) {
            if (i != list.length - 1) {
                result += list[i] + ", "
            } else {
                result += list[i]
            }
        }
        return result
    }
}
