package com.blogspot.jvalentino.milfit.service

class ServiceBus {
    
    private static ServiceBus instance
    
    PdfGeneratorService pdfGeneratorService = new PdfGeneratorService()
    SettingService settingService = new SettingService()
    GeometryService geometryService = new GeometryService()
    CalculationService calculationService = new CalculationService()
    
    private ServiceBus() {
        
    }
    
    public static ServiceBus getInstance() {
        if (instance == null) {
            instance = new ServiceBus()
        }
        return instance
    }

}
