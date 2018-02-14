package com.veracode.sonarplugin;

import org.sonar.api.batch.ScannerSide;
import org.sonar.api.batch.sensor.SensorContext;
//import org.sonar.api.config.Configuration;
import org.sonar.api.utils.log.Logger;
import org.sonar.api.utils.log.Loggers;

@ScannerSide
public class VeracodeSensorConfiguration {

    private final Logger log = Loggers.get(getClass());
    private final SensorContext m_context;
    private static String m_veracodeAppName;
    private static String m_veracodeScanType;
    private static String m_veracodeApiId;
    private static String m_veracodeApiKey;
    private static Boolean m_staticResult = false;
    private static Boolean m_dynamicResult = false;
    private static Boolean m_manualResult = false;

    private static final String VERACODE_APP_NAME = "sonar.veracode.appName";
    private static final String VERACODE_SCAN_TYPE = "sonar.veracode.scanType";
    private static final String VERACODE_API_ID = "sonar.veracode.apiId";
    private static final String VERACODE_API_KEY = "sonar.veracode.apiKey";

    public VeracodeSensorConfiguration(SensorContext context) {

        log.debug("SensorConfiguration");

        m_context = context;

        m_veracodeAppName = context.config().get(VERACODE_APP_NAME).orElse(null);
        m_veracodeScanType = context.config().get(VERACODE_SCAN_TYPE).orElse(null);

        log.debug("Veracode app name = " + m_veracodeAppName);
        log.debug("Veracode scan type(s) = [" + m_veracodeScanType + "]");


    }
}