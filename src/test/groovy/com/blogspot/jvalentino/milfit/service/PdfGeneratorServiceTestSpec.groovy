package com.blogspot.jvalentino.milfit.service

import spock.lang.Specification;

class PdfGeneratorServiceTestSpec extends Specification {

    PdfGeneratorService service
    
    def setup() {
        service = new PdfGeneratorService()
    }
    
    void "test percentToDpi"() {
        when:
        int result = service.percentToDpi(percent)
        
        then:
        result == output
        
        where:
        percent || output
        "100%"  || 100
        "200%"  || 200    
        "150%"  || 150
        "foo%"  || 100
        "3"     || 100  
    }
}
