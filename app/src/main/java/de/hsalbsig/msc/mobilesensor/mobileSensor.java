package de.hsalbsig.msc.mobilesensor;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import static android.content.ContentValues.TAG;

public class mobileSensor extends Activity implements SensorEventListener {

    // GUI Variablen
    private TextView labelLocation;
    private TextView labelMotion;
    private TextView labelEnvironment;
    private Button btnGps;
    private Button btnAccelerometer;
    private Button btnTemperature;

    private Boolean flag = false;

    // Sensor Variablen
    private SensorManager sensorManager;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private Sensor mAccel;
    private Sensor mTemp;
    private Sensor mLocation;

    // Accelerometer
    public float[] gravity = {0, 0, 0};

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

        // SensorManager
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        // Sensoren zuweisen
        mAccel = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mTemp = sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);



        sensorManager.registerListener(this, mAccel, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, mTemp, SensorManager.SENSOR_DELAY_NORMAL);

        if (checkPermissions() == false) {
            try {
                ActivityCompat.requestPermissions((Activity) mobileSensor.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 3);
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        }
    }

    public void locationManager(View view) {
        Log.v(TAG, "onClick");
        labelLocation.setText("Please!! move your device to" +
                " see the changes in coordinates." + "\nWait..");
        LocationListener locationListener = new LocListener(view);

        try {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, locationListener);
        } catch (SecurityException e) {
            e.printStackTrace();
            throw e;
        }

    }

    public void motionManager(View view) {
    }

    public void environmentManager(View view) {
        environmentSensor temp = new environmentSensor();
        // Hier weiter!!!
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            gravity[0] = event.values[0];
            gravity[1] = event.values[1];
            gravity[2] = event.values[2];
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    private boolean checkPermissions() {
        boolean result = true;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int granted = ContextCompat.checkSelfPermission(mobileSensor.this, Manifest.permission.ACCESS_FINE_LOCATION);
            if (granted != PackageManager.PERMISSION_GRANTED) {
                result = false;
            }
        }
        return result;
    }
}
