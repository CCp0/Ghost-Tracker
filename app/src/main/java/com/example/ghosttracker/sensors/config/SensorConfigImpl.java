package com.example.ghosttracker.sensors.config;

import android.hardware.Sensor;
import android.hardware.SensorManager;

import java.util.Map;

import com.example.ghosttracker.sensors.common.Constants;

public class SensorConfigImpl implements SensorConfig {

    @Override
    public Map<String, Sensor> createSensors(final SensorManager sensorManager) {
        final Sensor compassSensor =  sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        return Map.of(
            Constants.COMPASS_KEY, compassSensor
        );
    }
}
