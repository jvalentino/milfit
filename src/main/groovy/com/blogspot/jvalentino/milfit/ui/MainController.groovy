package com.blogspot.jvalentino.milfit.ui

import groovy.json.JsonSlurper;
import groovy.util.logging.Slf4j;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

import com.blogspot.jvalentino.milfit.AppState;
import com.blogspot.jvalentino.milfit.component.BaseController;
import com.blogspot.jvalentino.milfit.component.JProgressDialog;
import com.blogspot.jvalentino.milfit.component.SettingMenuItem;
import com.blogspot.jvalentino.milfit.model.FlatForm;
import com.blogspot.jvalentino.milfit.model.FlatFormGroup;
import com.blogspot.jvalentino.milfit.model.Form;
import com.blogspot.jvalentino.milfit.model.FormSetting;
import com.blogspot.jvalentino.milfit.model.FormTemplateAndData;
import com.blogspot.jvalentino.milfit.model.FormTemplateAndGroup;
import com.blogspot.jvalentino.milfit.service.ServiceBus;
import com.blogspot.jvalentino.milfit.service.ServiceException;
import com.blogspot.jvalentino.milfit.ui.form.FormView;
import com.blogspot.jvalentino.milfit.ui.multiform.IMultiFormListener;
import com.blogspot.jvalentino.milfit.ui.multiform.MultiForm;

@Slf4j
class MainController extends BaseController implements IMultiFormListener {
    
    MainView view
    ServiceBus service = ServiceBus.getInstance()
    MainController instance = this
    MainModel model
    
    MainController(MainView view) {
        this.view = view
        this.model = new MainModel()
    }
    
    void constructionComplete() {
        
        MainController me = this
        
        //view.newItem.setMnemonic(KeyEvent.VK_N)
        //view.newItem.setAccelerator(KeyStroke.getKeyStroke(
                //KeyEvent.VK_N, ActionEvent.CTRL_MASK))
        
        //
        
        view.openItem.addActionListener(new ActionListener() {
              void actionPerformed(ActionEvent e) {
                  instance.openFile()     
              }
           })
        
        
        view.aboutItem.addActionListener(new ActionListener() {
              void actionPerformed(ActionEvent e) {
                  instance.about()     
              }
           })
        
        extractExamplesAndTemplates()
        
        stretchFrame()
        
        seeIfUpdates()
    }
    
    void seeIfUpdates() {
        new Thread() {
            void run() {
                
                String version = service.settingService.getVersionFromUrl(
                    AppState.getInstance().getVersionUrl())
                    
                boolean update = service.settingService.isUpdateAvailable(
                    AppState.getInstance().getDetailedVersion(), 
                    version)  
                
                instance.buildUpdateMenu(version, update)
                
                
            }
        }.start()
    }
    
    void buildUpdateMenu(String version, boolean update) {
        String text = "Downloads"
        if (update) {
            text = "Update ${version} Available"
        }
        
        JMenu menu = new JMenu(text)
        menu.setOpaque(true)
        if (update) {
            menu.setBackground(Color.green)
        }
        view.menu.add(menu)
        
        JMenuItem webItem = new JMenuItem("<html><b>Java Web Start</b>: " + 
            AppState.getInstance().getJnlpUrl() + "</html>")
        menu.add(webItem)
        webItem.addActionListener(new ActionListener() {
              void actionPerformed(ActionEvent e) {
                  instance.openUrl(AppState.getInstance().getJnlpUrl())     
              }
           })
        
        JMenuItem windows = new JMenuItem("<html><b>Windows</b>: " + 
            AppState.getInstance().getWindowsInstallerUrl() + "</html>")
        menu.add(windows)
        windows.addActionListener(new ActionListener() {
            void actionPerformed(ActionEvent e) {
                instance.openUrl(AppState.getInstance().getWindowsInstallerUrl())
            }
         })
        
        JMenuItem mac = new JMenuItem("<html><b>Mac</b>: " + 
            AppState.getInstance().getMacInstallerUrl() + "</html>")
        menu.add(mac)
        mac.addActionListener(new ActionListener() {
            void actionPerformed(ActionEvent e) {
                instance.openUrl(AppState.getInstance().getMacInstallerUrl())
            }
         })
    }
    
    void openUrl(String url) {
        java.awt.Desktop.getDesktop().browse(java.net.URI.create(url))
    }
    
    void extractExamplesAndTemplates() {
        this.showModalInThread("Updating examples and templates")
        new Thread() {
            void run() {
               
                try {
                    service.settingService.extractTemplatesAndExamples(
                        AppState.getInstance().exampleDir, 
                        AppState.getInstance().templateDir)
                    
                    instance.hideModalFromThread()
                    
                    instance.loadDocumentTypes()
                } catch (ServiceException e) {
                    instance.hideModalFromThread()
                    instance.showExceptionInDialog(e)
                }
                
                
            }
        }.start()
    }
    
    void stretchFrame() {
        int inset = 50
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize()
        int width = screenSize.width  - inset * 2
        int height = screenSize.height - inset * 2
        view.setBounds(inset, inset, width, height)
    }
    
    void openFile() {
        File file = this.browseForFileType(
            [".data.json", ".group.json"] as String[], 
            "Select a data or group file to open")
        
        if (!file) {
            return
        }
        
        this.showModalInThread("Opening document")
        new Thread() {
            void run() {
                if (file.name.endsWith(".data.json")) {                
                    instance.openDataFileInThread(file)
                } else if (file.name.endsWith(".group.json")) {
                    instance.openGroupFileInThread(file)
                }
            }
        }.start()
    }
    
    void openDataFileInThread(File file) {
        
        try {
            FormTemplateAndData result = service.settingService.openDataFile(file, model.availableForms)
            
            // turn the source PDF into images
            List<BufferedImage> images = service.pdfGeneratorService.pdfToImages(
                result.template.file, 100)
            
            log.info(result.template.file + " converted into " + images.size() + " images")
            
            log.info("Form template loaded as " + result.template.elements.size() + " elements")
            
            FormView frame = new FormView(result.template.file, images, result.template, result.data, file)
            view.desktop.add(frame)
            
            frame.setSelected(true)
            
        } catch (ServiceException e) {
            this.showExceptionInDialog(e.underlying)
        }
        
        instance.hideModalFromThread()
    }
    
    void loadDocumentTypes() {
        this.showModalInThread("Loading available documents")
        new Thread() {
            void run() {
               
                model.availableForms = service.settingService.loadSettings(
                    AppState.getInstance().templateDir)
                
                instance.constructIndividualMenu(model.availableForms)
                instance.constructGroupMenu(model.availableForms)
                
                instance.hideModalFromThread()
            }
        }.start()
    }
    
    void constructIndividualMenu(List<FormSetting> availableForms) {
        Map<String, JMenu> categoryMap = [:]
        
        for (FormSetting form : availableForms) {
            
            // handle the category menu
            JMenu categoryMenu = null
            
            if (categoryMap.containsKey(form.category)) {
                categoryMenu = categoryMap.get(form.category)
            } else {
                categoryMenu = new JMenu(form.category)
                categoryMap.put(form.category, categoryMenu)
                view.newIndividualItem.add(categoryMenu)
            }
            
            // handle adding this item to the category
            SettingMenuItem item = new SettingMenuItem(form.name + " - " + form.type, form)
            
            item.addActionListener(new ActionListener() {
                void actionPerformed(ActionEvent e) {
                    SettingMenuItem  m = e.getSource()
                    instance.formSelected(m.setting)
                }
            })
            
            categoryMenu.add(item)
        }
    }
    
    void constructGroupMenu(List<FormSetting> availableForms) {
        Map<String, JMenu> categoryMap = [:]
        
        for (FormSetting form : availableForms) {
            
            // handle the category menu
            JMenu categoryMenu = null
            
            if (categoryMap.containsKey(form.category)) {
                categoryMenu = categoryMap.get(form.category)
            } else {
                categoryMenu = new JMenu(form.category)
                categoryMap.put(form.category, categoryMenu)
                view.newGroupItem.add(categoryMenu)
            }
            
            // handle adding this item to the category
            SettingMenuItem item = new SettingMenuItem(form.name + " - " + form.type, form)
            
            item.addActionListener(new ActionListener() {
                void actionPerformed(ActionEvent e) {
                    SettingMenuItem  m = e.getSource()
                    instance.groupFormSelected(m.setting)
                }
            })
            
            categoryMenu.add(item)
        }
    }
    
    void groupFormSelected(FormSetting setting) {
        this.showModalInThread("Loading PDF Template")
        new Thread() {
            void run() {
                log.info("group form selected from " + setting.source.getName())
                
                // turn the source PDF into images
                List<BufferedImage> images = service.pdfGeneratorService.pdfToImages(
                    setting.file, 100)
                
                log.info(setting.file + " converted into " + images.size() + " images")
                
                // load the template
                Form template = service.settingService.loadTemplate(setting.source)
                
                log.info("Form template loaded as " + template.elements.size() + " elements")
                
                // Create empty form data
                FlatFormGroup group = new FlatFormGroup(type:template.type)
                
                MultiForm frame = new MultiForm(setting.file, images, template, instance, group)
                view.desktop.add(frame)
                
                instance.hideModalFromThread()
                frame.setSelected(true)
            }
        }.start()
    }
    
    void openGroupFileInThread(File file) {
        
        try {
            log.info("group form selected from " + file.getAbsoluteFile())
            
            FormTemplateAndGroup result = service.settingService.openGroupFile(
                file, model.availableForms)
            
            List<BufferedImage> images = service.pdfGeneratorService.pdfToImages(
                result.template.file, 100)
            
            MultiForm frame = new MultiForm(result.template.file, images, result.template, 
                instance, result.group, file)
            view.desktop.add(frame)
            
            instance.hideModalFromThread()
            frame.setSelected(true)
            
        } catch (ServiceException e) {
            this.showExceptionInDialog(e.underlying)
        }
        
        instance.hideModalFromThread()
    }
    
    void formSelected(FormSetting formSetting) {
        this.showModalInThread("Loading PDF")
        new Thread() {
            void run() {
                log.info("Form selected from " + formSetting.source.getName())
                
                // turn the source PDF into images
                List<BufferedImage> images = service.pdfGeneratorService.pdfToImages(
                    formSetting.file, 100)
                
                log.info(formSetting.file + " converted into " + images.size() + " images")
                
                // load the template
                Form template = service.settingService.loadTemplate(formSetting.source)
                
                log.info("Form template loaded as " + template.elements.size() + " elements")
                
                FlatForm data = service.settingService.blankDataFromTemplate(template)
                
                log.info("Blank form data created as " + data.elements.size() + " unique elements")
                
                FormView frame = new FormView(formSetting.file, images, template, data, null)
                view.desktop.add(frame)
                
                instance.hideModalFromThread()
                frame.setSelected(true)
            }
        }.start()
    }
    
    void about() {
        String message = 
        """
        This application is not for use without explicit permission from John Valentino (jvalentino2@gmail.com)\n
        \n
        This application is a work-in-progress, and is not yet ready for general release.
        """
        
        this.showInfoMessage(message)
    }

    @Override
    void formSelectedToAddToDesktop(FormView frame) {
        view.desktop.add(frame)
    }

}
