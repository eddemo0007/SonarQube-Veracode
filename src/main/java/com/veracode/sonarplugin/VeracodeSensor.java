package com.veracode.sonarplugin;

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



    public VeracodeSensor(/*VeracodeSensorConfiguration configuration, FileSystem fileSystem,
			                PathResolver pathResolver, Rules rules*/) {

    }
    
    @Override
    public void execute(SensorContext context)
    {
        log.debug("execute");

        m_config = new VeracodeSensorConfiguration(context);

        
        

        // the Veracode detailed report is a new "language" - deal with it

    }

    @Override
    public void describe(SensorDescriptor sensorDescriptor) {
        sensorDescriptor.name(SENSOR_NAME);
    }

}