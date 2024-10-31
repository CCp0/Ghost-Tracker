package com.example.ghosttracker;

import static android.hardware.Sensor.TYPE_ACCELEROMETER;
import static android.hardware.Sensor.TYPE_MAGNETIC_FIELD;
import static android.hardware.SensorManager.SENSOR_DELAY_NORMAL;
import static android.hardware.SensorManager.SENSOR_DELAY_UI;
import static java.util.Objects.nonNull;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.ghosttracker.sensors.CompassSensor;
import com.example.ghosttracker.sensors.config.SensorConfig;
import com.example.ghosttracker.sensors.config.SensorConfigImpl;
import com.example.ghosttracker.sensors.impl.CompassSensorImpl;

import java.util.Map;


public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private static final Float BASE_OPACITY = 0.2F;

    private SensorManager sensorManager;
    private SensorConfig sensorConfig;
    private CompassSensor compassSensor;
    private MediaPlayer mediaPlayer;
    private Map<String, Sensor> sensorsList;

    private Button greenBtn;
    private Button yellowBtn;
    private Button orangeBtn;
    private Button redBtn;

    private final float[] accelerometerReading = new float[3];
    private final float[] magnetometerReading = new float[3];

    private float[] rotationMatrix = new float[9];
    private float[] orientationAngles = new float[3];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //FOR SENSOR - PASS INTO THEIR INTERFACES ON CREATE
        sensorConfig = new SensorConfigImpl();
        compassSensor = new CompassSensorImpl();

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mediaPlayer = MediaPlayer.create(this, R.raw.emfsound);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        sensorsList = sensorConfig.createSensors(sensorManager);

        findButtons();
        setBaseOpacity();

        mediaPlayer.setLooping(true);
        mediaPlayer.start();
    }

    private void findButtons() {
        greenBtn = findViewById(R.id.greenBtn);
        yellowBtn = findViewById(R.id.yellowBtn);
        orangeBtn = findViewById(R.id.orangeBtn);
        redBtn = findViewById(R.id.redBtn);
    }

    public void changeLight(Button btn) {
        //Set all the buttons to base low opacity
        setBaseOpacity();

        //Make this button full opacity
        btn.setAlpha(1);
    }

    public void setBaseOpacity() {
        greenBtn.setAlpha(BASE_OPACITY);
        yellowBtn.setAlpha(BASE_OPACITY);
        orangeBtn.setAlpha(BASE_OPACITY);
        redBtn.setAlpha(BASE_OPACITY);
    }

    @Override
    protected void onResume() {
        super.onResume();

        final Sensor accelerometer = sensorManager.getDefaultSensor(TYPE_ACCELEROMETER);
        if (nonNull(accelerometer)) {
            sensorManager.registerListener(
                    this, accelerometer, SENSOR_DELAY_NORMAL, SENSOR_DELAY_UI
            );
        }

        final Sensor magneticFieldSensor = sensorManager.getDefaultSensor(TYPE_MAGNETIC_FIELD);
        if (nonNull(magneticFieldSensor)) {
            sensorManager.registerListener(
                    this, magneticFieldSensor, SENSOR_DELAY_NORMAL, SENSOR_DELAY_UI
            );
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == TYPE_ACCELEROMETER) {
            System.arraycopy(
                event.values,
                0,
                accelerometerReading,
                0,
                accelerometerReading.length
            );
        } else if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            System.arraycopy(
                event.values,
                0,
                magnetometerReading,
                0,
                magnetometerReading.length
            );
        }

        compassSensor.updateRotationMatrix(accelerometerReading,
            magnetometerReading, rotationMatrix);
        compassSensor.updateOrientationAngles(rotationMatrix, orientationAngles);

        updateLightsBasedOnAzimuth();
    }

    private void updateLightsBasedOnAzimuth() {
        float azimuth = orientationAngles[0];

        //Y,O,R,G
        if ((azimuth >= 0.2 && azimuth < 0.35 ) || (azimuth <= 0.8 && azimuth > 0.65)) {
            changeLight(yellowBtn);
        } else if ((azimuth >= 0.35 && azimuth < 0.45 ) || (azimuth <= 0.65 && azimuth > 0.55)) {
            changeLight(orangeBtn);
        } else if ((azimuth >= 0.45 && azimuth < 0.55)) {
            changeLight(redBtn);
        } else if (azimuth < 0.2 || azimuth > 0.8) {
            changeLight(greenBtn);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Do something here if sensor accuracy changes.
        // You must implement this callback in your code.
    }
}