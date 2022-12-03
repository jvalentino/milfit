package com.blogspot.jvalentino.milfit.ui.form

import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.blogspot.jvalentino.milfit.model.DerivativeFormField;
import com.blogspot.jvalentino.milfit.model.FlatForm;
import com.blogspot.jvalentino.milfit.model.FlatFormData;
import com.blogspot.jvalentino.milfit.model.Form;
import com.blogspot.jvalentino.milfit.model.FormElement;
import com.blogspot.jvalentino.milfit.service.CalculationService;
import com.blogspot.jvalentino.milfit.service.PdfGeneratorService;

import spock.lang.Specification;

class FormControllerTestSpec extends Specification {

    FormView view
    String pdfFilename = "somefile.pdf"
    List<BufferedImage> images
    Form template
    FlatForm data
    FormController instance
    
    FormController controller
    
    def setup() {
        images = [new BufferedImage(10, 10, 1), new BufferedImage(10, 10, 1)]
        instance = Mock(FormController)
        view = Mock(FormView)
        template = new Form()
        data = new FlatForm()
        
        controller = new FormController(
            view, pdfFilename, images, template, data, null)
        controller.instance = instance
        
        controller.bus.pdfGeneratorService = Mock(PdfGeneratorService)
        controller.bus.calculationService = Mock(CalculationService)
    }
    
    void "Test constructDataMap"() {
        given:
        List<FlatFormData> elements = [
            new FlatFormData(key:"1"),
            new FlatFormData(key:"2")
        ]
        
        when:
        Map<String, FlatFormData> dataMap = controller.constructDataMap(elements)
        
        then:
        dataMap.containsKey("1")
        dataMap.containsKey("2")
        
    }
    
    void "Test constructionComplete"() {
        given:
        JMenu resizeMenu = Mock(JMenu)
        JButton nextButton = Mock(JButton)
        JButton previousButton = Mock(JButton)
        JMenuItem saveItem = Mock(JMenuItem)
        JMenuItem saveDesignItem = Mock(JMenuItem)
        JMenuItem exportItem = Mock(JMenuItem)
        
        when:
        controller.constructionComplete()
        
        then:
        _ * view.getResizeMenu() >> resizeMenu
        _ * view.getNextButton() >> nextButton
        _ * view.getPreviousButton() >> previousButton
        _ * view.getSaveItem() >> saveItem
        _ * view.getSaveDesignItem() >> saveDesignItem
        _ * view.getExportItem() >> exportItem
        
        and:
        6 * view.resizeMenu.add(_)
        1 * nextButton.addActionListener(_)
        1 * previousButton.addActionListener(_)
        1 * saveItem.addActionListener(_)
        1 * saveDesignItem.addActionListener(_)
        1 * exportItem.addActionListener(_)
        
        and:
        1 * instance.createPage()
    }
    
    void "test nextPage"() {
        given:
        controller.model.currentPage = currentPageBefore
        
        when:
        controller.nextPage()
        
        then:
        1 * instance.createPage()
        
        controller.model.currentPage == currentPageAfter
        
        where:
        currentPageBefore   || currentPageAfter
        0                   || 1
        1                   || 0
        
    }
    
    void "test previousPage"() {
        given:
        controller.model.currentPage = currentPageBefore
        
        when:
        controller.previousPage()
        
        then:
        1 * instance.createPage()
        
        controller.model.currentPage == currentPageAfter
        
        where:
        currentPageBefore   || currentPageAfter
        0                   || 1
        1                   || 0
    }
    
    void "Test createPage"() {
        given:
        JTextField currentField = Mock(JTextField)
        JPanel panel = Mock(JPanel)
        controller.model.currentPage = 0
        
        when:
        controller.createPage()
        
        then:
        _ * view.getCurrentField() >> currentField
        _ * view.getPanel() >> panel
        
        and:
        1 * instance.resizeView()
        1 * instance.createTextComponents()
        1 * view.currentField.setText("1")
        1 * view.panel.revalidate()
        1 * view.panel.repaint()
    }
    
    void "test resizeInThread"() {
        given:
        String dpi = "150"
        BufferedImage image = new BufferedImage(20, 20, 1)
        
        when:
        controller.resizeInThread(dpi)
        
        then:
        1 * controller.bus.pdfGeneratorService.pdfToImages(
            controller.model.pdfFilename, 
            150) >> [image]
        controller.model.dpi == 150
        1 * instance.resizeView()
        1 * instance.updatePositions()
        1 * instance.hideModalFromThread()
        controller.model.images.get(0) == image
    }
    
    void "test resizeView"() {
        when:
        controller.resizeView()
        
        then:
        1 * controller.view.setSize(10, 110)
        
    }
    
    void "Test constructDerivativeFields"() {
        given:
        List<FormElement> elements = [
            new FormElement(key:"A", deriveValueFrom:["B"]),
            new FormElement(key:"B"),
        ]
        Map<String, FlatFormData> dataMap = [
            "A":new FlatFormData(key:"A"),
            "B":new FlatFormData(key:"B")
        ]
        
        when:
        List<DerivativeFormField> results = controller.constructDerivativeFields(
            elements, dataMap)
        
        DerivativeFormField result = results.get(0)
        
        then:
        results.size() == 1
        result.data.key == "A"
        result.derivedFrom.get(0).key == "B"
        result.derivedFromKeys.contains("B")
        result.template.key == "A"
        
    }
    
    void "test handleUpstreamUpdatesForKey"() {
        given:
        String key = "B"
        List<DerivativeFormField> derivativeFields = []
        List<IForm> elements = []
        
        and:
        FormElement template = new FormElement(
            deriveValueUsing:"average",
            valueFormat:"#0.00")
        FlatFormData data = new FlatFormData(key:"A")
        FlatFormData derived = new FlatFormData(key:"B")
        
        and:
        DerivativeFormField field = new DerivativeFormField(
            template:template,
            data:data,
            derivedFrom:[derived])
        
        and:
        List<DerivativeFormField> relatedFields = [field]
        
        when:
        controller.handleUpstreamUpdatesForKey(key, derivativeFields, elements)
        
        then:
        1 * instance.findRelatedDerivativeFields(
            derivativeFields, key) >> relatedFields
        
        1 * controller.bus.calculationService.determineSingleValueFromField(derived) >> 1.0
        
        1 * instance.refreshComponentForKey(field.data.key, elements)
        
        and:
        field.data.value.toString() == "1.00"
    }
}
