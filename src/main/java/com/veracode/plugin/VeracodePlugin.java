package com.veracode.plugin;

import org.sonar.api.Plugin;
import org.sonar.api.utils.log.Logger;
import org.sonar.api.utils.log.Loggers;


public class VeracodePlugin implements Plugin 
{
    //private static final Logger log = Loggers.get("VeracodePlugin");
    private final Logger log = Loggers.get(getClass());

    @Override
    public void define(Context context) {

        log.info("Hello from the Veracode plugin");

        
    }
    
}