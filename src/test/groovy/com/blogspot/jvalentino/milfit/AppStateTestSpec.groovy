package com.blogspot.jvalentino.milfit

import spock.lang.Specification;

class AppStateTestSpec extends Specification {
    
    AppState instance
    
    def setup() {
        instance = AppState.getInstance()
    }
    
    void "test getDetailedVersion"() {
        when:
        String result = instance.getDetailedVersion()
        
        then:
        result == "1.0.0"
    }
    
    void "Test getWindowsInstallerUrl"() {
        when:
        String result = instance.getWindowsInstallerUrl()
        
        then:
        result == "http://valentino-tech.com/milfit/milfit-standalone.zip"
    }
    
    void "Test getMacInstallerUrl"() {
        when:
        String result = instance.getMacInstallerUrl()
        
        then:
        result == "http://valentino-tech.com/milfit/milfit-mac.zip"
    }
    
    void "Test getJnlpUrl"() {
        when:
        String result = instance.getJnlpUrl()
        
        then:
        result == "http://valentino-tech.com/milfit/milfit.jnlp"
    }
    
    void "Test getVersionUrl"() {
        when:
        String result = instance.getVersionUrl()
        
        then:
        result == "http://valentino-tech.com/milfit/version.txt"
    }

}
