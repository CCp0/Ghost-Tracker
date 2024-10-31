package com.example.ghosttracker.sensors.impl;

import static android.hardware.SensorManager.getOrientation;
import static android.hardware.SensorManager.getRotationMatrix;

import com.example.ghosttracker.sensors.CompassSensor;

public class CompassSensorImpl implements CompassSensor {

    @Override
    public void updateRotationMatrix(
        final float[] accelerometerReading, final float[] magnetometerReading,
        final float[] rotationMatrix
    ) {
        getRotationMatrix(rotationMatrix, null, accelerometerReading, magnetometerReading);
    }

    @Override
    public void updateOrientationAngles(final float[] rotationMatrix, final float[] orientationAngles) {
        getOrientation(rotationMatrix, orientationAngles);
    }
}

