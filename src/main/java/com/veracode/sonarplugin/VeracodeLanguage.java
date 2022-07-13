package com.veracode.sonarplugin;

import org.sonar.api.config.Configuration;
import org.sonar.api.resources.AbstractLanguage;

/* 
 * A language must be defined for the rules to get applied.
 * 
 * So, define a dummy language
 */

public class VeracodeLanguage extends AbstractLanguage {

    public static final String NAME = "Veracode";
    public static final String KEY = "veracode";

    public VeracodeLanguage (Configuration config) {
        super(KEY, NAME);
    }

    // not sure if I need this, but make a dummy file extension
    @Override
    public String[] getFileSuffixes() {
        String[] suffixes = new String[] {".veracode"};
        return suffixes;
    }

}