package com.blogspot.jvalentino.milfit

import groovy.util.logging.Slf4j;

import java.awt.Color;

import javax.swing.JFrame;

import com.blogspot.jvalentino.milfit.component.SplashScreen;
import com.blogspot.jvalentino.milfit.ui.MainView;

@Slf4j
class Main {

    static final String APP_TITLE = "MILFIT - Military Fitness and Evaluation Reporting"
    SplashScreen splash
    AppState appState = AppState.getInstance()
    Main instance = this
    
    boolean showSplashScreen = false
    
    static void main(String[] args) {
        new Main(args)
    }
    
    Main(String[] args) {
        log.debug("Starting application...")
        instance.loadConfiguration()
        instance.showSplashScreen()
        instance.showMainView()
    }
    
    void loadConfiguration() {
        Properties prop = new Properties()
        InputStream is = getClass().getResourceAsStream("/application.properties")
        prop.load(is)
        is.close()
        
        appState.version = prop.get("version")
        appState.buildNumber = prop.get("buildNumber")
        appState.host = prop.get("host")
        appState.binaryName = prop.get("binaryName")
        appState.exampleDir = prop.get("exampleDir")
        appState.templateDir = prop.get("templateDir")
        
        log.debug("Version: ${appState.version}, Build: ${appState.buildNumber}")
    }
    
    void showSplashScreen() {        
        
        if (!showSplashScreen) {
            return
        }
        
        splash = new SplashScreen("/Splashscreen-512x512.png");
        splash.setUndecorated(true);
        splash.setSize(512, 512);
        splash.setLocationRelativeTo(null);
        splash.setVisible(true);
        splash.setBackground(new Color(1.0f,1.0f,1.0f,0.5f));
        
        Thread.sleep(1000L)
    }
    
    void showMainView() {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                MainView view = new MainView()
                view.setTitle(APP_TITLE + " " + appState.version + "." +  appState.buildNumber)
                view.setSize(1000 + 320, 1020)
                view.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE)
                view.setLocationRelativeTo(null)
                view.setVisible(true)
                if (splash != null) {
                    splash.setVisible(false)
                }
            }
        })
    }
    
    
    
}
