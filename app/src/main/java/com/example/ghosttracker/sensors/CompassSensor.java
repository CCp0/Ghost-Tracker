package com.example.ghosttracker.sensors;


public interface CompassSensor {

    void updateRotationMatrix(final float[] accelerometerReading,
        final float[] magnetometerReading, final float[] rotationMatrix
    );

    void updateOrientationAngles(final float[] rotationMatrix, final float[] orientationAngles);
}
