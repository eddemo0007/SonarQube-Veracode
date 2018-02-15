package com.veracode.sonarplugin;

import java.io.InputStream;
import java.io.Reader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import org.sonar.api.server.rule.RulesDefinition;
import org.sonar.api.server.rule.RulesDefinitionXmlLoader;
import org.sonar.api.utils.log.Logger;
import org.sonar.api.utils.log.Loggers;

public final class VeracodeRules implements RulesDefinition {

    private static final String PATH_TO_RULES_XML = "/com/veracode/sonarplugin/veracode-rules.xml";

    // same as Language???  make common?
    //protected static final String KEY = "veracode";
    //protected static final String NAME = "Veracode";

    public static final String REPO_KEY = VeracodeLanguage.KEY + "-reports";
    protected static final String REPO_NAME = "Veracode report analyzer";

    private final Logger log = Loggers.get(getClass());

    //protected String rulesDefinitionFilePath() {
    //    return PATH_TO_RULES_XML;
   // }

    private void defineRulesForLanguage(Context context, String repositoryKey, 
                                String repositoryName, String languageKey) {
        
        log.debug("Loading Veracode rules");

        NewRepository repository = context.createRepository(repositoryKey, 
                                    languageKey).setName(repositoryName);

        InputStream rulesXml = this.getClass().getResourceAsStream(PATH_TO_RULES_XML);

        if (rulesXml != null) {
            RulesDefinitionXmlLoader rulesLoader = new RulesDefinitionXmlLoader();
            rulesLoader.load(repository, rulesXml, StandardCharsets.UTF_8.name());
        }

        repository.done();
    }

    @Override
    public void define(Context context) {
        defineRulesForLanguage(context, REPO_KEY, REPO_NAME, VeracodeLanguage.KEY);
  }

}