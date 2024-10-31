package com.example.ghosttracker;

import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.ghosttracker.sensors.config.SensorConfig;
import com.example.ghosttracker.sensors.config.SensorConfigImpl;

import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.JavaCamera2View;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import android.Manifest;


public class MainActivity extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2{

    private static final Float BASE_OPACITY = 0.2F;

    private static final String TAG = "MainActivity";

    private final int REQUEST_CAMERA_PERMISSION = 1;

    private SensorManager sensorManager;

    private SensorConfig sensorConfig;

    private Map<String, Sensor> sensorsList;

    private Button greenBtn;

    private Button yellowBtn;

    private Button orangeBtn;

    private Button redBtn;

    private JavaCamera2View javaCamera2View;

    private TextView countTextView;

    private CascadeClassifier cascadeClassifier;

    private int personCount = 0;

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

        // Initialize OpenCV
        if (OpenCVLoader.initDebug()) {
            Log.d(TAG, "OpenCV initialized successfully");
        } else {
            Log.e(TAG, "OpenCV initialization failed");
        }

        // Set up Camera View
        javaCamera2View = findViewById(R.id.java_camera_view);
        javaCamera2View.setVisibility(SurfaceView.VISIBLE);
        javaCamera2View.setCvCameraViewListener(this);

        // Set up TextView for displaying person count
        countTextView = findViewById(R.id.countTextView);

        // Request camera permission if not already granted
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
        }

        //TODO - Test for color changes. To delete
        greenBtn.setOnClickListener(v -> changeLight(yellowBtn));
        yellowBtn.setOnClickListener(v -> changeLight(orangeBtn));
        orangeBtn.setOnClickListener(v -> changeLight(redBtn));
        redBtn.setOnClickListener(v -> changeLight(greenBtn));

        System.out.println("onCreate finished");

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

    private void initializeOpenCV() {
        System.out.println("initializeOpenCV called");
        if (OpenCVLoader.initDebug()) {
            Log.d(TAG, "OpenCV initialized successfully");
            loadCascadeClassifier();
            javaCamera2View.enableView();
        } else {
            Log.e(TAG, "OpenCV initialization failed");
        }
    }

    private void loadCascadeClassifier() {
        System.out.println("loadCascadeClassifier called");
        try {
            // Load the classifier file from res/raw
            InputStream is = getResources().openRawResource(R.raw.haarcascade_fullbody); // Adjust this if using another classifier
            File cascadeDir = getDir("cascade", MODE_PRIVATE);
            File mCascadeFile = new File(cascadeDir, "haarcascade_fullbody.xml");

            FileOutputStream os = new FileOutputStream(mCascadeFile);
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            is.close();
            os.close();

            cascadeClassifier = new CascadeClassifier(mCascadeFile.getAbsolutePath());
            if (cascadeClassifier.empty()) {
                Log.e(TAG, "Failed to load cascade classifier");
                cascadeClassifier = null;
            } else {
                Log.d(TAG, "Cascade classifier loaded from " + mCascadeFile.getAbsolutePath());
            }
            cascadeDir.delete();

        } catch (Exception e) {
            Log.e(TAG, "Error loading cascade", e);
        }
    }
    @Override
    public void onResume() {
        super.onResume();
        if (javaCamera2View != null) {
            javaCamera2View.enableView();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (javaCamera2View != null) {
            javaCamera2View.disableView();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (javaCamera2View != null) {
            javaCamera2View.disableView();
        }
    }

    @Override
    public void onCameraViewStarted(int width, int height) {
        // Load the Haar Cascade classifier for full-body detection
        System.out.println("onCameraViewStarted called");
        try {
            InputStream is = getResources().openRawResource(R.raw.haarcascade_fullbody);
            File cascadeDir = getDir("cascade", Context.MODE_PRIVATE);
            File mCascadeFile = new File(cascadeDir, "haarcascade_fullbody.xml");
            FileOutputStream os = new FileOutputStream(mCascadeFile);

            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            is.close();
            os.close();

            cascadeClassifier = new CascadeClassifier(mCascadeFile.getAbsolutePath());
            if (cascadeClassifier.empty()) {
                Log.e(TAG, "Failed to load cascade classifier");
                cascadeClassifier = null;
            } else {
                Log.d(TAG, "Cascade classifier loaded from " + mCascadeFile.getAbsolutePath());
            }
            cascadeDir.delete();
        } catch (Exception e) {
            Log.e(TAG, "Error loading cascade", e);
        }
    }

    @Override
    public void onCameraViewStopped() {
        // Release resources if needed
    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        System.out.println("onCameraFrame called");
        Mat frame = inputFrame.gray(); // Convert the frame to grayscale

        MatOfRect bodyDetections = new MatOfRect();
        if (cascadeClassifier != null) {
            cascadeClassifier.detectMultiScale(frame, bodyDetections, 1.1, 2, 2,
                    new Size(30, 30), new Size());
            List<Rect> bodies = bodyDetections.toList();

            // Draw rectangles around detected bodies and update person count
            for (Rect rect : bodies) {
                Imgproc.rectangle(frame, rect.tl(), rect.br(), new Scalar(0, 255, 0), 3);
            }

            // Update person count and display it
            personCount = bodies.size();
            runOnUiThread(() -> countTextView.setText("People detected: " + personCount));
        }

        return frame;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA_PERMISSION && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            initializeOpenCV();
        } else {
            Toast.makeText(this, "Camera permission is required", Toast.LENGTH_SHORT).show();
        }
    }
}