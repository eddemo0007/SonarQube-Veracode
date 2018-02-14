package com.veracode.sonarplugin;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang.StringUtils;
import org.sonar.api.config.Configuration;
import org.sonar.api.resources.AbstractLanguage;

/* 
 * A language must be defined for the rules to get applied.
 * 
 * So, define a dummy language
 */

public class  VeracodeLanguage extends AbstractLanguage {

    public static final String NAME = "Veracode";
    public static final String KEY = "veracode";

    private final Configuration m_config;

    public VeracodeLanguage (Configuration config) {
        super(KEY, NAME);
        m_config = config;
    }

    @Override
    public String[] getFileSuffixes() {
        String[] suffixes = new String[] {".vxml"};
        return suffixes;
    }

    /*
    private String[] filterEmptyStrings(String[] stringArray) {
        List<String> nonEmptyStrings = new ArrayList<>();
        for (String string : stringArray) {
          if (StringUtils.isNotBlank(string.trim())) {
            nonEmptyStrings.add(string.trim());
          }
        }
        return nonEmptyStrings.toArray(new String[nonEmptyStrings.size()]);
      }
      */

}