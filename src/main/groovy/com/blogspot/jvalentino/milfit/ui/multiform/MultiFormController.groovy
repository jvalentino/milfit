package com.blogspot.jvalentino.milfit.ui.multiform

import groovy.util.logging.Slf4j;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.List;

import javax.swing.JTable;
import javax.swing.table.TableColumn;

import com.blogspot.jvalentino.milfit.component.BaseController;
import com.blogspot.jvalentino.milfit.model.FlatForm;
import com.blogspot.jvalentino.milfit.model.FlatFormGroup;
import com.blogspot.jvalentino.milfit.model.Form;
import com.blogspot.jvalentino.milfit.model.FormSetting;
import com.blogspot.jvalentino.milfit.service.ServiceBus;
import com.blogspot.jvalentino.milfit.service.ServiceException;
import com.blogspot.jvalentino.milfit.ui.form.FormView;
import com.blogspot.jvalentino.milfit.ui.form.IFormViewListener;

@Slf4j
class MultiFormController extends BaseController implements IFormViewListener {
    
    
    MultiFormModel model = new MultiFormModel()
    MultiForm view
    MultiFormController instance = this
    ServiceBus service = ServiceBus.getInstance()
    IMultiFormListener listener
    
    MultiFormController(MultiForm view, String pdfFilename, List<BufferedImage> images, Form template,
        IMultiFormListener listener, FlatFormGroup group, File file=null) {
        
        this.view = view
        this.listener = listener
        this.model.pdfFilename = pdfFilename
        this.model.images = images
        this.model.template = template
        this.model.group = group
        this.model.formTableModel = new FormTableModel(template, this.model.group)
        this.model.file = file
        
    }
    
    @Override
    void constructionComplete() {
        
        this.view.deleteItem.setEnabled(false)
        this.view.openItem.setEnabled(false)
        
        this.view.newItem.addActionListener(new ActionListener() {
            void actionPerformed(ActionEvent e) {
                instance.newIndividualSelected()
            }
        })
        
        this.view.importItem.addActionListener(new ActionListener() {
            void actionPerformed(ActionEvent e) {
                instance.importIndividualSelected()
            }
        })
        
        this.view.saveItem.addActionListener(new ActionListener() {
            void actionPerformed(ActionEvent e) {
                instance.saveGroupItemSelected()
            }
        })
        
        this.view.openItem.addActionListener(new ActionListener() {
            void actionPerformed(ActionEvent e) {
                instance.doubleClickOnRow(model.selectedRow)
            }
        })
        
        this.view.deleteItem.addActionListener(new ActionListener() {
            void actionPerformed(ActionEvent e) {
                instance.deleteForm(model.selectedRow)
            }
        })
        
        if (model.file != null) {
            view.title = model.file.getName()
        }
        
        view.table.addMouseListener(new MouseAdapter() {
            void mousePressed(MouseEvent me) {
                JTable table = me.getSource()
                Point p = me.getPoint()
                int row = table.rowAtPoint(p)
                if (me.getClickCount() == 2) {
                    instance.doubleClickOnRow(row)
                }
                instance.updateSelectedRow(row)
                
            }
        });
    }
    
    void updateSelectedRow(int row) {
        model.selectedRow = row
        
        this.view.deleteItem.setEnabled(true)
        this.view.openItem.setEnabled(true)
    }
    
    /**
     * Handles when the user selected to create a new individual form, which opens
     * a thread and builds the form view.
     */
    void newIndividualSelected() {
        instance.showModalInThread("Loading PDF")
        new Thread() {
            void run() {
                instance.newIndividualSelected(model.pdfFilename, model.template, model.images)                                               
            }
        }.start()
    }
    
    /**
     * Established the Form view and adds it to the desktop using a blank template
     * 
     * @param pdfFilename
     * @param template
     * @param images
     */
    void newIndividualSelected(String pdfFilename, Form template, List<BufferedImage> images) {
        FlatForm data = service.settingService.blankDataFromTemplate(template)
        instance.createAndDisplayForm(pdfFilename, template, images, data)
    }
    
    /**
     * Creates a new form component with the given data and displays it on the desktop
     * @param pdfFilename
     * @param template
     * @param images
     * @param data
     */
    void createAndDisplayForm(String pdfFilename, Form template, List<BufferedImage> images, FlatForm data) {
        model.group.forms.add(data)
        
        FormView frame = new FormView(pdfFilename, images, template, data, null, this)
        listener.formSelectedToAddToDesktop(frame)
        
        instance.hideModalFromThread()
        frame.setSelected(true)
        
        // update the table model
        this.model.formTableModel = new FormTableModel(template, this.model.group)
        view.table.setModel(this.model.formTableModel)
        
        instance.updateFrameTitleAfterUpdate()
    }
    
    /**
     * Callback for when a form is selected for save, which means it needs to be 
     * updated in the table model.
     * 
     */
    @Override
    void saveFormData(FlatForm form) {
        // update all of the summary group averages
        service.getCalculationService().updateSummaryGroupAverage(this.model.template, this.model.group)
        
        // update the counts for hte different promotion categories (SP, P, MP, EP)
        service.getCalculationService().updateRecommendationCounts(this.model.template, this.model.group)
        
        // update the table model
        instance.model.formTableModel = new FormTableModel(this.model.template, this.model.group)
        view.table.setModel(this.model.formTableModel)
                
        instance.updateFrameTitleAfterUpdate()
        
        
    }
    
    void updateFrameTitleAfterUpdate() {
        if (this.model.file == null) {
            view.title = model.template.type + "*"
        } else {
            view.title = model.file.getName() + "*"
        }
    }
    
    /**
     * Called when the user selects to import an individual form.
     * This will prompt the user to open a file, and then handle the opening in a thread
     */
    void importIndividualSelected() {
        // browser for the file
        final File file = this.browseForFileType(".data.json", "Select a data file")
        
        if (file == null) {
            return
        }
                
        this.showModalInThread("Loading document")
        new Thread() {
            void run() {
                instance.importIndividualSelected(file)
            }
        }.start()
    }
    
    /**
     * Import form data from the given file, adds it to the desktop
     * 
     * @param file
     */
    void importIndividualSelected(File file) {
        // load the file
        FlatForm data = null
        try {
            data = service.settingService.loadData(file)
        } catch (ServiceException e) {
            instance.hideModalFromThread()
            instance.showExceptionInDialog(e)
        }
        
        if (data == null) {
            return
        }
        
        // verify that this is the correct type
        if (data.type != this.model.template.type) {
            instance.hideModalFromThread()
            instance.showInfoMessage("You can only import data of the " + this.model.template.type +
                " type into this group. The type you selected was " + data.type + ".")
            return
        }
        
        instance.createAndDisplayForm(model.pdfFilename, model.template, model.images, data)
        
        instance.hideModalFromThread()
    }
    
    /**
     * Called when the user selects to save the group
     */
    void saveGroupItemSelected() {
        if (model.file == null) {
            
            File file = this.browseForFileSave(model.template.name, "Save")
        
            if (!file.getName().endsWith(".group.json")) {
                file = new File(file.getAbsolutePath() + ".group.json")
            }
            
            model.file = file
        }
        
        if (model.file == null) {
            return
        }
        
        view.title = model.file.getName()
        
        service.settingService.saveGroup(model.group, model.file)
    }
    
    void doubleClickOnRow(int row) {
        
        FormTableModel tableModel = view.table.getModel()
        FlatForm data = tableModel.getRow(row)
        
        if (data == null) {
            return
        }
        
        instance.showModalInThread("Loading Form")
        
        new Thread() {
            void run() {
                
                FormView frame = new FormView(model.pdfFilename, model.images, model.template, data, null, instance)
                listener.formSelectedToAddToDesktop(frame)
                
                instance.hideModalFromThread()
                frame.setSelected(true)
            }
        }.start()
        
    }
    
    void deleteForm(int currentRow) {
        
        FlatFormGroup group = new FlatFormGroup(type:this.model.group.type)
        
        for (int i = 0; i < this.model.group.forms.size(); i++) {
            if (i != currentRow) {
                group.forms.add(this.model.group.forms.get(i))
            }
        }
        
        this.model.group = group
        
        instance.model.formTableModel = new FormTableModel(this.model.template, this.model.group)
        view.table.setModel(this.model.formTableModel)
                
        
        instance.updateFrameTitleAfterUpdate()
    }

}
