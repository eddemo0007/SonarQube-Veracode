package com.veracode.sonarplugin;

import org.sonar.api.batch.ScannerSide;
import org.sonar.api.batch.sensor.SensorContext;
//import org.sonar.api.config.Configuration;
import org.sonar.api.utils.log.Logger;
import org.sonar.api.utils.log.Loggers;

/**
 * Scanner-side class to read the config options from the sonar-properties.config file
 * and make the veracode ones available to the sonar-scanner
 */

@ScannerSide
public class VeracodeSensorConfiguration {

    private final Logger log = Loggers.get(getClass());
    //private final SensorContext m_context;
    private static String m_veracodeAppName;
    //private static String m_veracodeScanType;
    private static String m_veracodeApiId;
    private static String m_veracodeApiKey;
    private static String m_proxyHost;
    private static String m_proxyPort;
    private static String m_proxyUsername;
    private static String m_proxyPassword;

    /** future planning 
    private static Boolean m_staticResult = false;
    private static Boolean m_dynamicResult = false;
    private static Boolean m_manualResult = false;
    */

    private static final String VERACODE_APP_NAME = "sonar.veracode.appName";
    //private static final String VERACODE_SCAN_TYPE = "sonar.veracode.scanType";
    private static final String VERACODE_API_ID = "sonar.veracode.apiId";
    private static final String VERACODE_API_KEY = "sonar.veracode.apiKey";
    private static final String VERACODE_PROXY_HOST = "sonar.veracode.proxyHost";
    private static final String VERACODE_PROXY_PORT = "sonar.veracode.proxyPort";
    private static final String VERACODE_PROXY_USERNAME = "sonar.veracode.proxyUsername";
    private static final String VERACODE_PROXY_PASSWORD = "sonar.veracode.proxyPassword";


    public VeracodeSensorConfiguration(SensorContext context) {

        //m_context = context;

        m_veracodeAppName = context.config().get(VERACODE_APP_NAME).orElse(null);
        //m_veracodeScanType = context.config().get(VERACODE_SCAN_TYPE).orElse(null);
        m_veracodeApiId = context.config().get(VERACODE_API_ID).orElse(null);
        m_veracodeApiKey = context.config().get(VERACODE_API_KEY).orElse(null);

        m_proxyHost = context.config().get(VERACODE_PROXY_HOST).orElse(null);
        m_proxyPort = context.config().get(VERACODE_PROXY_PORT).orElse(null);
        m_proxyUsername = context.config().get(VERACODE_PROXY_USERNAME).orElse(null);
        m_proxyPassword = context.config().get(VERACODE_PROXY_PASSWORD).orElse(null);

        log.debug("Veracode app name = " + m_veracodeAppName);
        log.debug("Veracode API ID = " + m_veracodeApiId);

        // TODO: encryption on the API key?

    }

    public String getAppName() {
        return m_veracodeAppName;
    }

    public String getApiId() {
        return m_veracodeApiId;
    }

    public String getApiKey() {
        return m_veracodeApiKey;
    }

    public String getProxyHost() {
        return m_proxyHost;
    }

    public String getProxyPort() {
        return m_proxyPort;
    }

    public String getProxyUsername() {
        return m_proxyUsername;
    }

    public String getProxyPassword() {
        return m_proxyPassword;
    }
}