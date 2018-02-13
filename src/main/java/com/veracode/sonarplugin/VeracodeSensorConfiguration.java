package com.veracode.sonarplugin;

import org.sonar.api.batch.ScannerSide;
import org.sonar.api.config.Configuration;


@ScannerSide
public class VeracodeSensorConfiguration {

    private final Configuration m_config;

    public VeracodeSensorConfiguration(Configuration config) {
        m_config = config;
    }
}