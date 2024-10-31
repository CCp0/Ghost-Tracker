package com.example.ghosttracker.sensors.config;

import static android.hardware.Sensor.TYPE_ACCELEROMETER;
import static android.hardware.Sensor.TYPE_MAGNETIC_FIELD;

import android.hardware.Sensor;
import android.hardware.SensorManager;

import java.util.Map;

import com.example.ghosttracker.sensors.common.Constants;

public class SensorConfigImpl implements SensorConfig {

    @Override
    public Map<String, Sensor> createSensors(final SensorManager sensorManager) {
        final Sensor accelerometer =  sensorManager.getDefaultSensor(TYPE_ACCELEROMETER);
        final Sensor magneticField = sensorManager.getDefaultSensor(TYPE_MAGNETIC_FIELD);

        return Map.of(
            Constants.ACCELEROMETER_KEY, accelerometer,
            Constants.MAGNETIC_FIELD_KEY, magneticField
        );
    }
}
