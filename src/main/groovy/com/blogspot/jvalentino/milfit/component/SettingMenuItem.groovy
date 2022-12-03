package com.blogspot.jvalentino.milfit.component

import javax.swing.JMenuItem;

import com.blogspot.jvalentino.milfit.model.FormSetting;

class SettingMenuItem extends JMenuItem {

    FormSetting setting
    
    SettingMenuItem(String name, FormSetting setting) {
        super(name)
        this.setting = setting
    }
}
