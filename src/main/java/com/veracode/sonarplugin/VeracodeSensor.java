package com.veracode.sonarplugin;

import java.io.IOException;
import java.text.ParseException;

import javax.xml.stream.XMLStreamException;

import org.apache.commons.lang3.StringUtils;

import org.sonar.api.batch.sensor.Sensor;
import org.sonar.api.batch.sensor.SensorDescriptor;
import org.sonar.api.batch.sensor.SensorContext;
import org.sonar.api.utils.log.Logger;
import org.sonar.api.utils.log.Loggers;

import com.veracode.apiwrapper.wrappers.*;

public class VeracodeSensor implements Sensor {

    public static final String SENSOR_NAME = "Veracode Plugin";
    private final Logger log = Loggers.get(getClass());

    private VeracodeSensorConfiguration m_config;
    private CredentialsHelper m_credsHelper;
    private UploadAPIWrapper m_uploadWrapper;
    private SandboxAPIWrapper m_sandboxWrapper;
    private ParseVeracodeXML.BuildInformation m_buildInfo;
    private ResultsAPIWrapper m_resultsWrapper;

    private String m_appName;
    private String m_appID;

    private String m_sandboxName;
    private String m_sandboxID;
    

    public VeracodeSensor() {
        log.debug("Veracode Sensor constructor"); 
    }
    
    @Override
    public void execute(SensorContext context)
    {
        log.debug("Veracode Sensor execute");

        m_config = new VeracodeSensorConfiguration(context);
        m_credsHelper = new CredentialsHelper(m_config);

        m_appName = m_config.getAppName();
        m_sandboxName = m_config.getSandboxName();

        // if there is no app name skip the Veracode flaw import
        if(StringUtils.isBlank(m_appName) ) {
            log.info("Veracode: no appName set, skipping Veracode import.");
            return;
        }

        /**
         * pull the required report(s) from the Veracode Platform and analyze them
         */

        // get app ID
        m_uploadWrapper = new UploadAPIWrapper();

        if(!m_credsHelper.setUpCredentials(m_uploadWrapper))
        {
            log.error("Error setting up the Veracode credentials, skipping Veracode analysis");
            return;
        }

        m_appID = getAppID();
        if (StringUtils.isBlank(m_appID)) {
            return;
        }

        log.info("Found existing app with ID = " + m_appID);

        if(!StringUtils.isBlank(m_sandboxName) ) {
            m_sandboxID = getSandboxID();
            if (StringUtils.isBlank(m_sandboxID)) {
                return;
            }

            log.info("Found existing sandbox with ID = " + m_sandboxID);
        }

        // get latest build (Future: of required type(s))

        // assumes that the most current build is done scanning - how valid is this?

        log.info("Getting info from latest build");

        try {
            String buildInfoXML = m_uploadWrapper.getBuildInfo(m_appID, "", m_sandboxID);
            log.debug("Build Info XML: " + buildInfoXML);

            try {
                // parse the XML and get the buildID
                ParseVeracodeXML parser = new ParseVeracodeXML(buildInfoXML);

                m_buildInfo = parser.getBuildIDFromInfo(m_appID);

                // failed if the build is not ready yet
                if(m_buildInfo == null)
                    return;

                log.info("Latest Build: " + m_buildInfo.m_buildName + " [ID = " + m_buildInfo.m_buildID + "]");
            }
            catch (ParseException p) {
                log.error("Parsing error " + p.toString());
                return;
            }
            catch (XMLStreamException x) {
                log.error("XML Stream error " + x.toString());
                return;
            }
        }
        catch (IOException e) {
            log.error("Error getting the build info: Exception " + e.toString());
            return;
        }

        // get the detailed report(s)
        m_resultsWrapper = new ResultsAPIWrapper();

        // this already worked once, but...
        if(!m_credsHelper.setUpCredentials(m_resultsWrapper))
        {
            log.error("Error setting up the Veracode credentials, skipping Veracode analysis");
            return;
        }

        // future: loop through detailed report(s)

        log.info("Getting detailed report for build: " + m_buildInfo.m_buildName);

        try {
            String detailedReportXML = m_resultsWrapper.detailedReport(m_buildInfo.m_buildID);
            log.debug("Detailed Report XML: " + detailedReportXML);

            try {
                // parse the XML and add flaws 
                ParseVeracodeXML parser = new ParseVeracodeXML(detailedReportXML);

                parser.addFlawsFromReport(context);
                //log.info("Found existing app with ID = " + m_appID);
            }
            /*catch (ParseException p) {
                log.error("Parsing error " + p.toString());
                return;
            }*/
            catch (XMLStreamException x) {
                log.error("XML Stream error " + x.toString());
                return;
            }
        }
        catch (IOException e) {
            log.error("Error getting the detailed report: Exception " + e.toString());
            return;
        }
    }

    @Override
    public void describe(SensorDescriptor sensorDescriptor) {
        sensorDescriptor.name(SENSOR_NAME);
    }


    private String getAppID()
    {
        log.info("Searching for existing app: " + m_appName);

        try {
            String appListXML = m_uploadWrapper.getAppList();
            log.debug("App List XML: " + appListXML);

            try {
                // parse the XML and get the appID
                ParseVeracodeXML parser = new ParseVeracodeXML(appListXML);

                return parser.getAppIDFromList(m_appName);
            }
            catch (ParseException p) {
                log.error("Parsing error " + p.toString());
            }
            catch (XMLStreamException x) {
                log.error("XML Stream error " + x.toString());
            }
        }
        catch (IOException e) {
            log.error("Error getting the app list: Exception " + e.toString());
        }

        return null;
    }

    private String getSandboxID()
    {
        log.info("Searching for sandbox: " + m_sandboxName);

        try {
            String sandboxListXML = m_sandboxWrapper.getSandboxList(m_appID);
            log.debug("Sandbox List XML: " + sandboxListXML);

            try {
                // parse the XML and get the appID
                ParseVeracodeXML parser = new ParseVeracodeXML(sandboxListXML);

                return parser.getSandboxIDFromList(m_sandboxName);
            }
            catch (ParseException p) {
                log.error("Parsing error " + p.toString());
            }
            catch (XMLStreamException x) {
                log.error("XML Stream error " + x.toString());
            }
        }
        catch (IOException e) {
            log.error("Error getting the sandbox list: Exception " + e.toString());
        }

        return null;
    }

}
