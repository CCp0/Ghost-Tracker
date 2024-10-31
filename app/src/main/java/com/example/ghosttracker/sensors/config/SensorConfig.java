package com.example.ghosttracker.sensors.config;

import android.hardware.Sensor;
import android.hardware.SensorManager;

import java.util.Map;

public interface SensorConfig {
    Map<String, Sensor> createSensors(final SensorManager sensorManager);
}
