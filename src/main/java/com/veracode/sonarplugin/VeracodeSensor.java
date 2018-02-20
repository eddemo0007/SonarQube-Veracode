package com.veracode.sonarplugin;

import java.io.IOException;
import java.text.ParseException;

import javax.xml.stream.XMLStreamException;

import com.veracode.apiwrapper.wrappers.*;
import org.sonar.api.batch.sensor.Sensor;
import org.sonar.api.batch.sensor.SensorDescriptor;
import org.sonar.api.batch.sensor.SensorContext;
//import org.sonar.api.batch.fs.FileSystem;
//import org.sonar.api.batch.rule.Rules;
//import org.sonar.api.scan.filesystem.PathResolver;
import org.sonar.api.utils.log.Logger;
import org.sonar.api.utils.log.Loggers;


public class VeracodeSensor implements Sensor {

    public static final String SENSOR_NAME = "Veracode Plugin";
    private final Logger log = Loggers.get(getClass());

    private static VeracodeSensorConfiguration m_config;
    private static CredentialsHelper m_credsHelper;
    private static UploadAPIWrapper m_uploadWrapper;
    private static ResultsAPIWrapper m_resultsWrapepr;

    private String m_appName;
    private String m_appID;
    

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

        log.info("[Veracode] Searching for existing app: " + m_appName);

        try {
            String appListXML = m_uploadWrapper.getAppList();
            log.debug("App List XML: " + appListXML);

            try {
                // parse the XML and get the appID
                ParseVeracodeXML parser = new ParseVeracodeXML(appListXML);

                m_appID = parser.getAppIDFromList(m_config.getAppName());
                log.info("Found existing app with ID = " + m_appID);
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
            log.error("Error getting the app list: " + m_appName +
                " Exception " + e.toString());

            return;
        }


        // get latest build (Future: of required type(s))

        // get the detailed report(s)
        //m_resultsWrapper = new ResultsAPIWrapper();

        // loop through detailed report(s)
            // add issue for each flaw - how are dups handled?


    }

    @Override
    public void describe(SensorDescriptor sensorDescriptor) {
        sensorDescriptor.name(SENSOR_NAME);
    }

}