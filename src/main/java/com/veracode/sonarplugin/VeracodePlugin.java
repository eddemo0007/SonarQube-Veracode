package com.veracode.sonarplugin;

import org.sonar.api.Plugin;
//import org.sonar.api.utils.log.Logger;
//import org.sonar.api.utils.log.Loggers;

/* 
 * entry point for the plugin
 */
public class VeracodePlugin implements Plugin  {

    //private final Logger log = Loggers.get(getClass());

    @Override
    public void define(Context context) {

        context.addExtensions(VeracodeSensor.class,
                                VeracodeLanguage.class,
                                VeracodeRules.class,
                                VeracodeQualityProfile.class);
    }
}