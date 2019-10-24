package de.hsalbsig.msc.mobilesensor;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class mobileSensor extends AppCompatActivity {

    // GUI Variablen
    private TextView labelLocation;
    private TextView labelMotion;
    private TextView labelEnvironment;
    private Button btnGps;
    private Button btnAccelerometer;
    private Button btnTemperature;

    // Temperatur Sensor
    private SensorManager tempSensorManager;
    private Sensor mTemperature;

    private final String[] perms = {"android.permission.INTERNET", "android.permission.ACCESS_FINE_LOCATION"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Variablen den Komponenten zuweisen
        labelLocation = findViewById(R.id.label_gps);
        labelMotion = findViewById(R.id.label_accel);
        labelEnvironment = findViewById(R.id.label_temp);

        btnGps = findViewById(R.id.btn_gps);
        btnGps.setEnabled(true);

        btnAccelerometer = findViewById(R.id.btn_accel);
        btnAccelerometer.setEnabled(true);

        btnTemperature = findViewById(R.id.btn_temp);
        btnTemperature.setEnabled(true);

        if(checkPermissions() == false) {
            try {
                ActivityCompat.requestPermissions((Activity) mobileSensor.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 3);
            } catch (Exception e){
                e.printStackTrace();
                throw e;
            }
        }
    }

    public void locationManager(View view) {

    }

    public void motionManager(View view) {
    }

    public void environmentManager(View view) {
        environmentSensor temp = new environmentSensor();
        // Hier weiter!!!
    }


    private boolean checkPermissions() {
        boolean result = true;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int granted = ContextCompat.checkSelfPermission(mobileSensor.this, Manifest.permission.READ_EXTERNAL_STORAGE);
            if(granted != PackageManager.PERMISSION_GRANTED) {
                result = false;
            }
        }
        return result;
    }
}
