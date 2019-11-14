package de.hsalbsig.msc.mobilesensor;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
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
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.util.Log;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import static android.content.ContentValues.TAG;

public class mobileSensor extends Activity implements SensorEventListener, View.OnClickListener {

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
    private ImageView iView;
    private static final String logMain = mobileSensor.class.getSimpleName();
    LocationTracker tracker;
    // Accelerometer
    public float[] gravity = {0, 0, 0};
    float celsius = 0;

    private final String[] perms = {"android.permission.INTERNET", "android.permission.ACCESS_FINE_LOCATION"};

    //Aufgabe 2

    private static final String TAG = "SignIn";
    private static final int RC_SIGN_IN = 9001;

    private GoogleSignInClient mGoogleSignInClient;
    private TextView mStatusTextView;

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

        iView = findViewById(R.id.panel);

        //Aufgabe 2
        // Views
        mStatusTextView = findViewById(R.id.status);

        // Button listeners
        findViewById(R.id.sign_in_button).setOnClickListener(this);
        findViewById(R.id.btn_logout).setOnClickListener(this);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        SignInButton signInButton = findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_STANDARD);
        signInButton.setColorScheme(SignInButton.COLOR_LIGHT);

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
                Log.e(logMain, "Permission denined!");
                e.printStackTrace();
                throw e;
            }
        }
    }

    public void locationManager(View view) {
        tracker=new LocationTracker(mobileSensor.this);
        if(tracker.isLocationEnabled)
        {
            Log.i(logMain, "GPS Daten werden gelesen");
            double latitude=tracker.getLatitude();
            double longitude=tracker.getLongitude();
            labelLocation.setText("Latitude= " + latitude + "\n Longitude= " + longitude);
        }
        else
        {
        }
    }

    public void motionManager(View view) {

    }

    public void environmentManager(View view) {
        int id = 0;
        if(celsius <= 0)
        {
            iView.setImageResource(R.drawable.snow);
        }
        else
        {
            iView.setImageResource(R.drawable.sun);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            gravity[0] = event.values[0];
            gravity[1] = event.values[1];
            gravity[2] = event.values[2];
            Log.i(logMain, "Accelerometer Daten werden gelesen");
            labelMotion.setText(gravity[0] + "\n" + gravity[1] + "\n" + gravity[2]  );
            Log.i(logMain, "Accelerometer Daten wurden ausgelesen");
        }
        if (event.sensor.getType() == Sensor.TYPE_AMBIENT_TEMPERATURE) {
            celsius = event.values[0];
            Log.i(logMain, "Temperatur Daten werden gelesen");
            labelEnvironment.setText(celsius +"° Celsius");
            Log.i(logMain, "Temperatur Daten wurden ausgelesen");
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

    @Override
    public void onStart() {
        super.onStart();

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        updateUI(account);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            updateUI(account);
        } catch (ApiException e) {
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
            updateUI(null);
        }
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void signOut() {
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        updateUI(null);
                    }
                });
    }

    private void revokeAccess() {
        mGoogleSignInClient.revokeAccess()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        updateUI(null);
                    }
                });
    }

    private void updateUI(@Nullable GoogleSignInAccount account) {
        if (account != null) {
            mStatusTextView.setText(getString(R.string.signed_in_fmt, "\n"+ account.getDisplayName()) + "\n"+ account.getEmail()
                    + "\n"+ account.getId() + "\n"+ account.getPhotoUrl());

            findViewById(R.id.sign_in_button).setVisibility(View.GONE);
            findViewById(R.id.btn_logout).setVisibility(View.VISIBLE);
        } else {
            mStatusTextView.setText(R.string.signed_out);

            findViewById(R.id.sign_in_button).setVisibility(View.VISIBLE);
            findViewById(R.id.btn_logout).setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_in_button:
                Log.i("LoggingIn", "Login completed!");
                signIn();
                break;
            case R.id.btn_logout:
                Log.i("LoggingOut", "Logout completed!");
                signOut();
                break;
        }
    }
}