package com.veracode.sonarplugin;

//import java.util.ArrayList;
//import java.util.List;
//import org.apache.commons.lang.StringUtils;
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

    private final Configuration m_config;

    public VeracodeLanguage (Configuration config) {
        super(KEY, NAME);
        m_config = config;
    }

    // not sure if I need this, but make a dummy file extension
    @Override
    public String[] getFileSuffixes() {
        String[] suffixes = new String[] {".veracode"};
        return suffixes;
    }

}