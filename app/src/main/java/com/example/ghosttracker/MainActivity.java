package com.example.ghosttracker;

import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    Button greenBtn;

    Button yellowBtn;

    Button orangeBtn;

    Button redBtn;

    final Float baseOpacity = 0.2F;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        greenBtn = findViewById(R.id.greenBtn);
        yellowBtn = findViewById(R.id.yellowBtn);
        orangeBtn = findViewById(R.id.orangeBtn);
        redBtn = findViewById(R.id.redBtn);

        setBaseOpacity();
        //Test for color changes
        greenBtn.setOnClickListener(v -> changeLight(yellowBtn));
        yellowBtn.setOnClickListener(v -> changeLight(orangeBtn));
        orangeBtn.setOnClickListener(v -> changeLight(redBtn));
        redBtn.setOnClickListener(v -> changeLight(greenBtn));

    }

    public void changeLight(Button btn) {
        //Set all the buttons to base low opacity
        setBaseOpacity();

        //Make this button full opacity
        btn.setAlpha(1);
    }

    public void setBaseOpacity() {
        greenBtn.setAlpha(baseOpacity);
        yellowBtn.setAlpha(baseOpacity);
        orangeBtn.setAlpha(baseOpacity);
        redBtn.setAlpha(baseOpacity);
    }
}