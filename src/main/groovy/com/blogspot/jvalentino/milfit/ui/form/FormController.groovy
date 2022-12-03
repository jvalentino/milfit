package com.blogspot.jvalentino.milfit.ui.form

import groovy.util.logging.Slf4j;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;
import java.util.Map;

import javax.swing.JMenuItem;
import javax.swing.JTextField;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.util.ImageIOUtil;

import com.blogspot.jvalentino.milfit.component.BaseController;
import com.blogspot.jvalentino.milfit.model.Bounds;
import com.blogspot.jvalentino.milfit.model.DerivativeFormField;
import com.blogspot.jvalentino.milfit.model.FlatForm;
import com.blogspot.jvalentino.milfit.model.FlatFormData;
import com.blogspot.jvalentino.milfit.model.Form;
import com.blogspot.jvalentino.milfit.model.FormElement;
import com.blogspot.jvalentino.milfit.model.FormElementType;
import com.blogspot.jvalentino.milfit.service.GeometryService;
import com.blogspot.jvalentino.milfit.service.ServiceBus;
import com.blogspot.jvalentino.milfit.ui.form.checkbox.FormCheckBox;
import com.blogspot.jvalentino.milfit.ui.form.combobox.ComboBoxField;
import com.blogspot.jvalentino.milfit.ui.form.designdialog.DesignDialog;
import com.blogspot.jvalentino.milfit.ui.form.designdialog.IDesignDialog;
import com.blogspot.jvalentino.milfit.ui.form.textarea.FormTextArea;
import com.blogspot.jvalentino.milfit.ui.form.textfield.FormTextField;
import com.sun.media.sound.ModelAbstractChannelMixer;

@Slf4j
class FormController extends BaseController implements IFormListener, IDesignDialog {

    FormView view
    FormModel model = new FormModel()
    ServiceBus bus = ServiceBus.getInstance()
    FormController instance = this
    IFormViewListener formViewListener
        
    FormController() {
        
    }
    
    FormController(FormView view, String pdfFilename, List<BufferedImage> images, 
        Form template, FlatForm data, File file, IFormViewListener formViewListener=null) {
        
        this.view = view
        this.model.pdfFilename = pdfFilename
        this.model.images = images
        this.model.template = template
        this.model.form = data
        this.model.file = file
        this.formViewListener = formViewListener
        
        this.model.dataMap = this.constructDataMap(this.model.form.elements)
        
        if (file != null) {
            view.title = file.getName()
        }
        
        this.model.derivativeFields =  this.constructDerivativeFields(
            this.model.template.elements, 
            this.model.dataMap)
    }
        
    Map<String, FlatFormData> constructDataMap(List<FlatFormData> elements) {
        Map<String, FlatFormData> dataMap = new LinkedHashMap<String, FlatFormData>()
        // turn the data into a map by key so we can associate it with the template
        for (FlatFormData field : elements) {
            dataMap.put(field.key, field)
        }
        
        return dataMap
    }
    
    /**
     * Constructs a mapping of fields which get their values from other fields
     * 
     * @param elements
     * @param dataMap
     * @return
     */
    List<DerivativeFormField> constructDerivativeFields(
        List<FormElement> elements, Map<String, FlatFormData> dataMap) {
        
        List<DerivativeFormField> derivativeFields = []
        
        // look for user inputs that are derived from other fields
        for (FormElement element : elements) {
            if (element.deriveValueFrom == null) {
                continue
            }
            
            FlatFormData dependentField = dataMap.get(element.key)
            
            DerivativeFormField derivative = new DerivativeFormField(
                template:element, data:dependentField)
            
            for (String key : element.deriveValueFrom) {
                FlatFormData current = dataMap.get(key)
                
                if (current == null) {
                    log.error("For the template of key ${element.key}, the deriveFromValue " +
                        "key of ${key} does not exist")
                    continue
                }
                
                derivative.derivedFrom.add(current)
                derivative.derivedFromKeys.add(current.key)
                
            }
            
            derivativeFields.add(derivative)
        }
        
        return derivativeFields
    }
    
    /**
     * Called when the view construction is completed.
     * Handles listeners and other dynamic content on startup.
     */
    void constructionComplete() {
                
        int[] resizeValues = [
            GeometryService.DPI_300, 
            GeometryService.DPI_250, 
            GeometryService.DPI_200, 
            GeometryService.DPI_150, 
            GeometryService.DPI_100, 
            GeometryService.DPI_72] as int[]
            
        for (int resize : resizeValues) {
            JMenuItem item = new JMenuItem(resize + "")
            view.resizeMenu.add(item)
            
            item.addActionListener(new ActionListener() {
                void actionPerformed(ActionEvent e) {
                    String dpi = e.actionCommand
                    instance.resize(dpi)
                }
            })
        }
        
        view.nextButton.addActionListener(new ActionListener() {
            void actionPerformed(ActionEvent e) {
                instance.nextPage()
            }
        })
        
        view.previousButton.addActionListener(new ActionListener() {
            void actionPerformed(ActionEvent e) {
                instance.previousPage()
            }
        })
        
        view.saveItem.addActionListener(new ActionListener() {
            void actionPerformed(ActionEvent e) {
                instance.saveData()
            }
        })
        
        view.saveDesignItem.addActionListener(new ActionListener() {
            void actionPerformed(ActionEvent e) {
                instance.saveDesign()
            }
        })
        
        view.exportItem.addActionListener(new ActionListener() {
            void actionPerformed(ActionEvent e) {
                instance.exportAsPdf()
            }
        })
        
       instance.createPage()
        
    }
    
    void nextPage() {
        model.currentPage = (model.currentPage + 1) % model.images.size()
        instance.createPage()
    }
    
    void previousPage() {
        int previousPage = model.currentPage - 1
        if (previousPage < 0) {
            model.currentPage = model.images.size() - 1
        } else {
            model.currentPage = previousPage
        }
        instance.createPage()
    }
    
    /**
     * Handles creating the current page representation of using the PDF images,
     * the user input, and the template
     */
    void createPage() {
                
        instance.resizeView()
        
        instance.createTextComponents()
        
        view.currentField.setText(model.currentPage + 1 + "")
        
        view.panel.revalidate()
        view.panel.repaint()
    }
    
    /**
     *  Resizes the page component, which handles loading the appropriate page image
     *  and re-drawing text representations on user inputs.
     */
    void resize(String dpi) {
        instance.showModalInThread("Resizing PDF")
        new Thread() {
            void run() {
                instance.resizeInThread(dpi)
            }
        }.start()
    }
    
    void resizeInThread(String dpiString) {
        model.dpi = Integer.parseInt(dpiString)
        List<BufferedImage> images = bus.pdfGeneratorService.pdfToImages(
            model.pdfFilename, model.dpi)
        model.images = images
        instance.resizeView()
        instance.updatePositions()
        instance.hideModalFromThread()
    }
    
    /**
     * Resizes the view based on the current page size
     */
    void resizeView() {
        //FIXME: Need a way to calculate the height of the menu bar and panel
        this.view.setSize(
            getCurrentPageImage().getWidth(), 
            getCurrentPageImage().getHeight() + 100)
    }
    
    /**
     * Overrides the view paint functinality to draw the current PDF page
     * and an image on the panel background
     * @param g
     */
    void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g
        
        //draw the background of the entire window as white
        g.setColor(Color.white)
        g.fillRect(0,0, view.getSize().getWidth().intValue(), view.getSize().getHeight().intValue())

        //set the default color as black
        g.setColor(Color.black)

        //7-29-2006 Changed to apply to g instead of this
        //g.setFont(model.numberFont)

        //if the field has not been drawn do so...
        if (getCurrentPageImage() != null) {
            g.drawImage(getCurrentPageImage(),0,0, this.view.panel)
        }
        
        //drawLines(g)

        //this is for the fucking mac, who likes to anti alias every fucking thing
        RenderingHints renderHints = new RenderingHints(
            RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF)
        g2d.setRenderingHints(renderHints)
    }
    
    void drawLines(Graphics g) {
        g.setColor(Color.red)
        
        for (int i = 0; i < view.getSize().getWidth().intValue(); i += 50) {
            g.drawString("${i}", i, 20)
            g.drawLine(i, 0, i, view.getSize().getHeight().intValue())
        }
        
        g.setColor(Color.black)
        
    }
    
    /**
     * Returns the buffered image that represents the current PDF page
     * @return
     */
    BufferedImage getCurrentPageImage() {
        return model.images.get(model.currentPage)
    }
    
    void clear() {
        view.panel.removeAll()
        model.elements.clear()
    }
    
    /**
     * Handles creating the components that represent user inputs
     */
    void createTextComponents() {
        
        instance.clear()
        
        for (FormElement element : model.template.elements) {
            
            // if this element is not on the current page
            if (element.page != (model.currentPage + 1)) {
                continue
            }
            
            String key = bus.settingService.getFirstPartOfKey(element)
            
            // find the associated data for this element
            FlatFormData data = model.dataMap.get(key)
            
            if (data == null) {
                log.error("${key} does not exist in the provided user input data")
                continue
            }
            
            // generate all of the require form field components
            List<IForm> fields = instance.generateFormFieldsBasedOnTemplate(element, data)
                        
            // add the fields to the panel, listen to changes     
            for (IForm field : fields) {
                field.setListener(this)
                view.panel.add(field)
                model.elements.add(field)
            }
            
            // update those positions
            instance.updatePositions()
        }
        
        
        
    }
    
    List<IForm> generateFormFieldsBasedOnTemplate(FormElement element, FlatFormData data) {
        List<IForm> fields = []
        
        switch (element.type) {
            case FormElementType.TEXT:
                
                if (element.selections == null) {
                    fields.add(new FormTextField(element, data))
                } else {
                    fields.add(new ComboBoxField(this, element, data))
                }
                break
            case FormElementType.TEXT_WRAP:
                fields.add(new FormTextArea(element, data))
                break
            case FormElementType.CHECK_BOX_MULTI:
                int i = 0
                for (String selection : element.selections) {
                    fields.add(new FormCheckBox(element, data, i, model.dpi))
                    i++
                }
                break
             case FormElementType.CHECK_BOX_BOOLEAN:
                 fields.add(new FormCheckBox(element, data, model.dpi))
                 break
             case FormElementType.CHECK_BOX_SINGLE:
                 int i = 0
                 List<FormCheckBox> group = []
                 for (String selection : element.selections) {
                     FormCheckBox box = new FormCheckBox(element, data, i, model.dpi, group)
                     fields.add(box)
                     group.add(box)
                     i++
                 }
                 break
        }
        
        return fields
    }

    
    
    /**
     * Updates all text component positions on the view
     */
    void updatePositions() {
                
        for (IForm field : model.elements) {
            field.updatePositionAndSize(
                    this.getCurrentPageImage().getHeight(), 
                    model.dpi)
        }
                
        view.panel.revalidate()
        view.panel.repaint()
    }
    
    /**
     * Called when "Save" is selected from the file menu
     */
    void saveData() {
        
        // if there is no form view listener
        if (formViewListener == null) {
            // we handle saving to a file
            instance.saveDataToFile()
        } else {
            // we notify the listener to handle the saving
            instance.saveDataToListener()
        }
    }
    
    /**
     * Handles saving the current form data to a data.json file
     */
    void saveDataToFile() {
        if (model.file == null) {
            
            File file = this.browseForFileSave(model.template.name, "Save")
        
            if (!file.getName().endsWith(".data.json")) {
                file = new File(file.getAbsolutePath() + ".data.json")
            }
            
            model.file = file
        }
        
        if (model.file == null) {
            return
        }
        
        view.title = model.file.getName()
        
        instance.updateFormModel()
        
        bus.settingService.saveData(model.form, model.file)
    }
    
    void saveDataToListener() {
        instance.updateFormModel()
        formViewListener.saveFormData(model.form)
    }
    
    /**
     * Handles clearing the current form model and re-building it with
     * current data from the form. This was needed in order to get updates
     * to be written to existing model.
     */
    void updateFormModel() {
        model.form.elements.clear()
        
        for (String key : model.dataMap.keySet()) {
            model.form.elements.add(model.dataMap.get(key))
        }
    }
    
    /**
     * Returns a list of derivative fields that are affected by a data
     * update to the specified key
     * 
     * @param derivativeFields
     * @param key
     * @return
     */
    List<DerivativeFormField> findRelatedDerivativeFields(
        List<DerivativeFormField> derivativeFields, String key) {
        
        // find all fields that use this to derive its value
        List<DerivativeFormField> relatedFields = []
        
        for (DerivativeFormField field : derivativeFields) {
            if (field.derivedFromKeys.contains(key)) {
                relatedFields.add(field)
            }
        }
        
        return relatedFields
    }

    @Override
    void dataUpdated(FlatFormData data) {
        this.handleUpstreamUpdatesForKey(data.key, model.derivativeFields, model.elements)
    }
    
    /**
     * Called when the data for a key is updated, so that all components are inspected to see
     * if they have a downstream dependency on that key. If there is a dependency, the value
     * dependent on that key that was updated is then derived.
     * 
     * @param key
     * @param derivativeFields
     * @param elements
     */
    void handleUpstreamUpdatesForKey(String key, List<DerivativeFormField> derivativeFields,
        List<IForm> elements) {
        
        // find all fields that use this to derive its value
        List<DerivativeFormField> relatedFields = instance.findRelatedDerivativeFields(
            derivativeFields, key)
        
        // for all of the effected fields
        for (DerivativeFormField field : relatedFields) {
            
            String method = field.template.deriveValueUsing
            double sum = 0.0
            double elementsInSum = 0
            
            for (FlatFormData derived : field.derivedFrom) {
                Double value = bus.getCalculationService().determineSingleValueFromField(derived)
                
                if (value == null) {
                    continue
                }
                
                if (method == "average") {
                     sum += value.doubleValue()
                     elementsInSum++
                } else {
                    log.error("The method ${method} is not valid for deriving values on key ${field.data.key}")
                }
            }
            
            if (method == "average") {
                double calculated = sum / elementsInSum
                NumberFormat formatter = new DecimalFormat(field.template.valueFormat);
                field.data.value = formatter.format(calculated)
            }
            
            // update the associated field
            instance.refreshComponentForKey(field.data.key, elements)
        }
    }
    
    /**
     * Given a key, searches through all form elements and calls the refresh method on any matching
     * components
     * 
     * @param key
     * @param elements
     */
    void refreshComponentForKey(String key, List<IForm> elements) {
        // update the associated field
        for (IForm element : elements) {
            if (element.getKey() == key) {
                element.refreshValue()
                return
            }
        }
    }  
    
    int findIndexOfKey(String key, List<FormElement> elements) {
        for (int i = 0; i < elements.size(); i++) {
            if (elements.get(i).key == key) {
                return i
            }
        }
        return -1
    }  

    @Override
    void templateUpdated(FormElement template) {
        
        // update the model
        int index = instance.findIndexOfKey(template.key, model.template.elements)
        
        if (index != -1) {
            
            model.template.elements.remove(index)
            model.template.elements.add(index, template)
        
            instance.updatePositions()
        } else {
            log.error("Unable to find a matching component for the key of ${template.key}")
        }
        
    }
    
    void saveDesign() {
        DesignDialog dialog = new DesignDialog(model.template, this)
        dialog.setVisible(true)
    }

    @Override
    void saveTemplateAs(File file, Form form) {
        
        if (!file.getName().endsWith(".template.json")) {
            file = new File(file.getAbsolutePath() + ".template.json")
        }
        
        form.elements = model.template.elements
        
        bus.settingService.saveTemplate(form, file)
        
       this.showInfoMessage("Template saved as " + file.getName())
    }
 
    void exportAsPdf() {
        
        String fileName = "export.pdf"
        
        if (model.file != null) {
            fileName = model.file.getName() + ".pdf"
        }
        
        File file = this.browseForFileSave(fileName, "Export as PDF")
        
        if (file == null) {
            return
        }
        
        instance.showModalInThread("Exporting PDF")
        new Thread() {
            void run() {         
                
                try {       
                    PDDocument doc = bus.pdfGeneratorService.generatePdf(model.template, model.form)
                    doc.save( file )
                    doc.close()
                    instance.hideModalFromThread()
                    instance.showInfoMessage("PDF exported to " + file.getAbsolutePath())
                } catch (Exception e) {
                    instance.showExceptionInDialog(e)
                }
                                
            }
        }.start()
        
    }
}
