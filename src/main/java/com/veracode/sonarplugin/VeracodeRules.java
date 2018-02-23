package com.veracode.sonarplugin;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.sonar.api.server.rule.RulesDefinition;
import org.sonar.api.server.rule.RulesDefinitionXmlLoader;
import org.sonar.api.utils.log.Logger;
import org.sonar.api.utils.log.Loggers;

public final class VeracodeRules implements RulesDefinition {

    private static final String PATH_TO_RULES_XML = "/com/veracode/sonarplugin/veracode-rules.xml";

    public static final String REPO_KEY = VeracodeLanguage.KEY + "-reports";
    protected static final String REPO_NAME = "Veracode report analyzer";

    private final Logger log = Loggers.get(getClass());

    private static Repository m_repository;

    // let others access the rules list (like the Quality Profile)
    public static List<RulesDefinition.Rule> getRulesList() {
        return m_repository.rules();
    }

    @Override
    public void define(Context context) {

        log.debug("Loading Veracode rules");

        NewRepository repository = context.createRepository(REPO_KEY, 
                                            VeracodeLanguage.KEY).setName(REPO_NAME);

        // load the rules from the resource file into the repo
        InputStream rulesXml = this.getClass().getResourceAsStream(PATH_TO_RULES_XML);

        if (rulesXml != null) {
            RulesDefinitionXmlLoader rulesLoader = new RulesDefinitionXmlLoader();
            rulesLoader.load(repository, rulesXml, StandardCharsets.UTF_8.name());
        }

        repository.done();

        // save the repo for later use (NewRepo vs. Repo - sheesh)
        m_repository = context.repository(REPO_KEY);
    }
}