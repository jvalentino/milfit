package com.blogspot.jvalentino.milfit.service

import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import groovy.json.JsonBuilder;
import groovy.json.JsonSlurper;
import groovy.util.logging.Slf4j;

import com.blogspot.jvalentino.milfit.model.FlatForm;
import com.blogspot.jvalentino.milfit.model.FlatFormData;
import com.blogspot.jvalentino.milfit.model.FlatFormGroup;
import com.blogspot.jvalentino.milfit.model.Form;
import com.blogspot.jvalentino.milfit.model.FormElement;
import com.blogspot.jvalentino.milfit.model.FormSetting;
import com.blogspot.jvalentino.milfit.model.FormTemplateAndData;
import com.blogspot.jvalentino.milfit.model.FormTemplateAndGroup;

@Slf4j
class SettingService {

    SettingService instance = this
    
    void extractTemplatesAndExamples(String exampleDir, String templateDir) throws ServiceException {
        extractZipToDir(exampleDir)
        extractZipToDir(templateDir)
    }
    
    /**
     * Looks for a ${dirName}.zipdir in the jar on the root of the classpath, and extracts it
     * to ${dirName} relative to where this application is running.
     * 
     * @param dirName
     * @throws ServiceException
     */
    void extractZipToDir(String dirName) throws ServiceException {
        File dir = new File(dirName)
        
        if (!dir.exists()) {
            log.info("${dirName} does not exist, creating it")
            boolean made = dir.mkdirs()
            
            if (!made) {
                throw new ServiceException("Unable to create a directory at " +
                    dir.getAbsolutePath())
            }
        }
        
        String zipDir = dir.getName() + ".zipdir"
        InputStream is = this.getClass().getResourceAsStream("/" + zipDir)
     
        File targetFile = new File(dir.getName() + ".zip.tmp");
        
        FileUtils.copyInputStreamToFile(is, targetFile);
        
        ZipFile zipFile = new ZipFile(targetFile)
        
        Enumeration entries = zipFile.entries()
        
        while(entries.hasMoreElements()) {
            ZipEntry entry = (ZipEntry) entries.nextElement();
         
            InputStream fileIs = zipFile.getInputStream(entry)
            
            IOUtils.copy(fileIs, new FileOutputStream(
                dir.getAbsolutePath() + File.separator + entry.getName()))
        }
        
        targetFile.delete()
    }
    
    /**
     * Loads all template.json files from the doc directory relative
     * to where this application is running.
     * 
     * @return
     */
    List<FormSetting> loadSettings(String templateDir) {
        
        List<FormSetting> results = []
        
        File dir = new File(templateDir)
        
        File[] files = dir.listFiles()
        
        for (File file : files) {
            if (!file.getName().endsWith(".template.json")) {
                continue
            }
            Form form = this.loadTemplate(file)
            
            FormSetting setting = new FormSetting(
                name: form.name,
                type:form.type, 
                category:form.category, 
                source:file,
                file:form.file)
            
            results.add(setting)
        }
        
        return results
    }
    
    /**
     * Creates a form object from a template.json file
     * 
     * @param file
     * @return
     * @throws ServiceException
     */
    Form loadTemplate(File file) throws ServiceException {
        try {
            return new JsonSlurper().parseText(file.text)
        } catch (Exception e) {
            log.error("There was a problem opening the template file", e)
            throw new ServiceException("${file.name} is an invalid template file", e)
        }
    }
    
    
    boolean hasCompoundKey(FormElement element) {
        if (element.key == null) {
            return false
        }
        
        if (element.key.contains("-")) {
            return true
        }
        
        return false
    }
    
    /**
     * Creates blank user entry data based on the given template
     * 
     * @param template
     * @return
     */
    FlatForm blankDataFromTemplate(Form template) {
        FlatForm form = new FlatForm(type:template.type)
        
        for (FormElement element : template.elements) {
            String key = element.key
            
            // if this is a compound key ignore it
            // it is already defined
            if (hasCompoundKey(element)) {
                continue
            }
            
            FlatFormData data = new FlatFormData(
                key:element.key, 
                name:element.name)
            
            form.elements.add(data)
        }
        
        return form
    }
    
    /**
     * Saves the user form data to the specified file
     * 
     * @param form
     * @param file
     */
    void saveData(FlatForm form, File file) {
        log.info("Saving to file ${file}")
        String json = new JsonBuilder(form).toPrettyString()
        file.text = json
    }
    
    void saveGroup(FlatFormGroup group, File file) {
        log.info("Saving to file ${file}")
        String json = new JsonBuilder(group).toPrettyString()
        file.text = json
    }
    
    void saveTemplate(Form form, File file) {
        log.info("Saving to file ${file}")
        String json = new JsonBuilder(form).toPrettyString()
        file.text = json
    }
    
    /**
     * Loads user data from the specified file
     * 
     * @param file
     * @return
     * @throws ServiceException
     */
    FlatForm loadData(File file) throws ServiceException {
        try {
            return new JsonSlurper().parseText(file.text)
        } catch (Exception e) {
            log.error("There was a problem opening the data file", e)
            throw new ServiceException("${file.name} is an invalid data file", e)
        }
    }
    
    FlatFormGroup loadGroup(File file) throws ServiceException {
        try {
            return new JsonSlurper().parseText(file.text)
        } catch (Exception e) {
            log.error("There was a problem opening the group file", e)
            throw new ServiceException("${file.name} is an invalid group file", e)
        }
    }
    
    /**
     * Opens a user data file and determines the the associated template and also returns it
     * 
     * @param file
     * @param availableForms
     * @return
     * @throws ServiceException
     */
    FormTemplateAndData openDataFile(File file, List<FormSetting> availableForms) throws ServiceException {
        // load the data file
        FlatForm data = instance.loadData(file)
        
        // attempt to find the template and load it...
        FormSetting setting = instance.findFormSettingByType(availableForms, data.type)
        
        if (setting == null) {
            throw new ServiceException("A template of type ${data.type} could not be located")
        }
        
        Form template = instance.loadTemplate(setting.source)
        
        return new FormTemplateAndData(data:data, template:template)
        
    }
    
    FormTemplateAndGroup openGroupFile(File file, List<FormSetting> availableForms) throws ServiceException {
        FlatFormGroup group = instance.loadGroup(file)
        
        // attempt to find the template and load it...
        FormSetting setting = instance.findFormSettingByType(availableForms, group.type)
        
        if (setting == null) {
            throw new ServiceException("A template of type ${group.type} could not be located")
        }
        
        Form template = instance.loadTemplate(setting.source)
        
        return new FormTemplateAndGroup(group:group, template:template)
    }
    
    FormSetting findFormSettingByType(List<FormSetting> availableForms, String type) {
        for (FormSetting setting : availableForms) {
            if (type == setting.type) {
                return setting
            }
       }
        return null
    }
    
    String getFirstPartOfKey(FormElement element) {
        if (hasCompoundKey(element)) {
            return element.key.split("-")[0]
        }
        return element.key
    }
    
    String getVersionFromUrl(String url) {
        try {
            return new URL(url).text
        } catch (Exception e) {
            log.error("Unable to get version from ${url}", e)
        }
    }
    
    boolean isUpdateAvailable(String currentVersion, String foundVersion) {
        if (foundVersion == null) {
            return false
        }
        
        if (currentVersion == null) {
            return false
        }
        
        String[] splitCurrentVersion = currentVersion.split("\\.")
        double currentFirstPart = Double.parseDouble(splitCurrentVersion[0] + "." + splitCurrentVersion[1])
        
        String[] splitFoundVersion = foundVersion.split("\\.")
        double foundFirstPart = Double.parseDouble(splitFoundVersion[0] + "." + splitFoundVersion[1])
        
        if (foundFirstPart > currentFirstPart) {
            return true
        } else if (foundFirstPart == currentFirstPart) {
            int currentLast = Integer.parseInt(splitCurrentVersion[2])
            int foundLast = Integer.parseInt(splitFoundVersion[2])
            
            if (foundLast > currentLast) {
                return true
            }
        }
        
        return false
    }
    
}
