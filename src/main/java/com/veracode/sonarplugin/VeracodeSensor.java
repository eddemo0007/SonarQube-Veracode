package com.veracode.sonarplugin;

import org.sonar.api.batch.sensor.Sensor;
import org.sonar.api.batch.sensor.SensorDescriptor;
import org.sonar.api.batch.sensor.SensorContext;
import org.sonar.api.batch.fs.FileSystem;
import org.sonar.api.batch.rule.Rules;
import org.sonar.api.scan.filesystem.PathResolver;

public class VeracodeSensor implements Sensor {

    public static final String SENSOR_NAME = "Veracode Plugin";

    public VeracodeSensor(VeracodeSensorConfiguration configuration, FileSystem fileSystem,
			                PathResolver pathResolver, Rules rules) {

    }
    
    @Override
    public void execute(SensorContext context)
    {
        ;
    }

    @Override
    public void describe(SensorDescriptor sensorDescriptor) {
        sensorDescriptor.name(SENSOR_NAME);
    }

}