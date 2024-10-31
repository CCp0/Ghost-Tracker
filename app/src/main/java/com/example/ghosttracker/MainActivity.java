package com.example.ghosttracker;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.ghosttracker.sensors.config.SensorConfig;
import com.example.ghosttracker.sensors.config.SensorConfigImpl;

import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private static final Float BASE_OPACITY = 0.2F;

    private SensorManager sensorManager;

    private SensorConfig sensorConfig;

    private Map<String, Sensor> sensorsList;

    private Button greenBtn;

    private Button yellowBtn;

    private Button orangeBtn;

    private Button redBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //FOR SENSOR - PASS INTO THEIR INTERFACES ON CREATE
        sensorConfig = new SensorConfigImpl();
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        super.onCreate(savedInstanceState);
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

        //TODO - Test for color changes. To delete
        greenBtn.setOnClickListener(v -> changeLight(yellowBtn));
        yellowBtn.setOnClickListener(v -> changeLight(orangeBtn));
        orangeBtn.setOnClickListener(v -> changeLight(redBtn));
        redBtn.setOnClickListener(v -> changeLight(greenBtn));

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
}