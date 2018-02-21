package com.veracode.sonarplugin;

import java.util.List;

import org.sonar.api.server.profile.BuiltInQualityProfilesDefinition;
import org.sonar.api.server.rule.RulesDefinition;
//import org.sonar.api.server.rule.RulesDefinition.Context;
import org.sonar.api.utils.log.Logger;
import org.sonar.api.utils.log.Loggers;


/**
 * Default, BuiltIn Quality Profile for the projects having files of the language "veracode"
 */
public final class VeracodeQualityProfile implements BuiltInQualityProfilesDefinition {

    private final Logger log = Loggers.get(getClass());

    @Override
    public void define(Context context) {
        
        log.debug("Loading Veracode Quality Profile");

        // create a QualityProfile for the veracode "language"
        NewBuiltInQualityProfile profile = context.createBuiltInQualityProfile("Veracode Rules", 
        VeracodeLanguage.KEY);

        // and set it as a default
        profile.setDefault(true);
  
        // load all the rules into this Quality Profile
        // (SQ loads Rules before Quality Profiles, so we're good here)
        List<RulesDefinition.Rule> rulesList = VeracodeRules.getRulesList();
       
        for(RulesDefinition.Rule rule : rulesList) {
            //log.debug("Quality Profile loading rule " + rule.key());
            profile.activateRule(VeracodeRules.REPO_KEY, rule.key());  
        }
  
        profile.done();
    }
  
  }