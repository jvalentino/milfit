package com.blogspot.jvalentino.milfit.component

import java.awt.BorderLayout;
import java.io.File;
import java.util.prefs.Preferences;

import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

abstract class BaseController {
    
    JProgressDialog dialog
    private static final LAST_USED_FOLDER = "LAST_USED_FOLDER"
    protected Preferences prefs = Preferences.userRoot().node(getClass().getName())

    abstract void constructionComplete()
    
    /**
     * Utility for showing a JProgress dialog in another thread. This is done
     * so that a dialog is modal, but code execution continues after setting the dialog's
     * visibility to true.
     *
     * @author jvalentino2
     *
     */
    protected void showModalInThread(String title) {
        BaseController me = this
        
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                dialog = new JProgressDialog(null, title)
                dialog.setModal(true)
                dialog.setVisible(true)
                
            }
        });
    }
    
    protected void hideModalFromThread() {
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                if (dialog) {
                    dialog.setVisible(false)
                }
                
            }
        });
    }
    
    /**
     * Utility for getting the last folder used for document storage
     * @return
     */
    protected File getLastUserFolder() {
        return new File(prefs.get(LAST_USED_FOLDER, new File(".").getAbsolutePath()))
    }
    
    /**
     * Returns the preference as a File if it exists
     * @param key
     * @return
     */
    protected File getPreferenceAsFile(String key) {
        String result = prefs.get(key, null)
        
        if (result == null) {
            return null
        }
        
        return new File(result)
    }
    
    protected String getPreferenceAsString(String key) {
        return prefs.get(key, null)
    }
    
    protected boolean getPreferenceAsBoolean(String key) {
        String result = prefs.get(key, null)
        if (result == null) {
            return false
        } else {
            return result.equals("true") ? true : false
        }
    }
    
    /**
     * Stores the given file as a preference using the given key
     * @param key
     * @param file
     */
    protected void storeFileAsPreference(String key, File file) {
        prefs.put(key, file.getAbsolutePath())
    }
    
    protected void storeStringAsPreference(String key, String value) {
        prefs.put(key, value)
    }
    
    protected void storeBooleanAsPreference(String key, boolean value) {
        prefs.put(key, value.toString())
    }

    /**
     * Wrapper for the verbose amount of code required for the display a dialog
     * where you are only allowing the selection of a specific file type. Also
     * the directory of the file is saved in system preferences.
     *
     * @param extension
     * @return
     */
    protected File browseForFileType(String extension, String title) {
        String[] extensions = [extension]
        return this.browseForFileType(extensions, title)
    }
    
    protected File browseForFileType(String[] extensions, String title) {
        final JFileChooser fc = new JFileChooser(getLastUserFolder())
        fc.setDialogTitle(title)
        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fc.setFileFilter(new CustomFileFilter(extensions));
        
        int returnVal = fc.showOpenDialog(null)
        
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file =  fc.getSelectedFile()
            prefs.put(LAST_USED_FOLDER, file.getParent())
            return file
        } else {
            return null
        }
    }
    
    protected File browseForFileSave(String filename, String title) {
        JFileChooser fc = new JFileChooser()
        fc.setSelectedFile(new File(filename))
        fc.setDialogTitle(title)
        fc.setCurrentDirectory(getLastUserFolder())
        int retrival = fc.showSaveDialog(null)
        
        if (retrival == JFileChooser.APPROVE_OPTION) {
            File file =  fc.getSelectedFile()
            return file
        } else {
            return null
        }
    }
    
    /**
     * Shortcut for showing an exception in an error dialog, and also printing it
     * out at the command-line.
     *
     * @param e
     */
    protected void showExceptionInDialog(Exception e) {
        e.printStackTrace()
        
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        String stack = sw.toString(); // stack trace as a string
        
        JPanel panel = new JPanel(new BorderLayout())
        
        // create a JTextArea
        JTextArea textArea = new JTextArea(20, 40);
        textArea.setText(stack);
        textArea.setEditable(false);
         
        // wrap a scrollpane around it
        JScrollPane scrollPane = new JScrollPane(textArea);
        
        panel.add(new JLabel(e.getMessage()), BorderLayout.NORTH)
        panel.add(scrollPane, BorderLayout.CENTER)
         
        // display them in a message dialog
        JOptionPane.showMessageDialog(null, panel, "Error",
            JOptionPane.ERROR_MESSAGE);
        
        
        
    }
    
    protected void showInfoMessage(String text) {
        JOptionPane.showMessageDialog(null, text)
    }
}
