package com.veracode.sonarplugin;

import org.sonar.api.server.profile.BuiltInQualityProfilesDefinition;
import org.sonar.api.utils.log.Logger;
import org.sonar.api.utils.log.Loggers;

/**
 * Default, BuiltIn Quality Profile for the projects having files of the language "veracode"
 */
public final class VeracodeQualityProfile implements BuiltInQualityProfilesDefinition {

    private final Logger log = Loggers.get(getClass());

    @Override
    public void define(Context context) {
        
        // create a QualityProfile for the veracode "language"
        NewBuiltInQualityProfile profile = context.createBuiltInQualityProfile("Veracode Rules", 
        VeracodeLanguage.KEY);
        profile.setDefault(true);
  
      NewBuiltInActiveRule rule1 = profile.activateRule(VeracodeRules.REPO_KEY, "ExampleRule1");
      //rule1.overrideSeverity("BLOCKER");
      NewBuiltInActiveRule rule2 = profile.activateRule(VeracodeRules.REPO_KEY, "ExampleRule2");
      //rule2.overrideSeverity("MAJOR");
      NewBuiltInActiveRule rule3 = profile.activateRule(VeracodeRules.REPO_KEY, "ExampleRule3");
      //rule3.overrideSeverity("CRITICAL");
  
      profile.done();
    }
  
  }