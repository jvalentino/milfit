package com.blogspot.jvalentino.milfit

import java.util.List;

import javax.swing.JFrame;

import com.blogspot.jvalentino.milfit.model.FormSetting;


class AppState {
    private static AppState instance
    
    String version = "1.0"
    String buildNumber = "0"
    String binaryName = "milfit"
    String host = "valentino-tech.com"
    boolean designMode = false
    String exampleDir
    String templateDir
    
    JFrame frame
    
    private AppState() {
        
    }
    
    static AppState getInstance() {
        if (instance == null)
            instance = new AppState()
        return instance
    }
    
    String getDetailedVersion() {
        return "${version}.${buildNumber}"
    }
    
    String getWindowsInstallerUrl() {
        return "http://" + host + "/" + binaryName + "/" + binaryName + "-standalone.zip"
    }
    
    String getMacInstallerUrl() {
        return "http://" + host + "/" + binaryName + "/" + binaryName + "-mac.zip"
    }
    
    String getJnlpUrl() {
        return "http://" + host + "/" + binaryName + "/" + binaryName + ".jnlp"
    }
    
    String getVersionUrl() {
        return "http://" + host + "/" + binaryName + "/version.txt"
    }
}
