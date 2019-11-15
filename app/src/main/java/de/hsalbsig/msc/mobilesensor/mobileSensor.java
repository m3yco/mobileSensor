package de.hsalbsig.msc.mobilesensor;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toolbar;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.net.URL;

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
    private static final String TAG = mobileSensor.class.getSimpleName();
    private static final int RC_SIGN_IN = 9001;

    private SignInButton btnSignIn;
    private Button btnSignOut;
    private LinearLayout llProfileLayout;
    private ImageView imgProfilePic;
    private TextView txtName, txtEmail, txtId;

    private GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setActionBar(toolbar);

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
        btnSignIn = (SignInButton) findViewById(R.id.sign_in_button);
        btnSignOut = (Button) findViewById(R.id.btn_logout);
        llProfileLayout = (LinearLayout) findViewById(R.id.llProfile);
        imgProfilePic = (ImageView) findViewById(R.id.imgProfilePic);
        txtName = (TextView) findViewById(R.id.txtName);
        txtEmail = (TextView) findViewById(R.id.txtEmail);
        txtId = (TextView) findViewById(R.id.txtID);

        // Button listeners
        findViewById(R.id.sign_in_button).setOnClickListener(this);
        findViewById(R.id.btn_logout).setOnClickListener(this);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        btnSignIn.setSize(SignInButton.SIZE_STANDARD);
        btnSignIn.setColorScheme(SignInButton.COLOR_LIGHT);

        // SensorManager
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        // Sensoren zuweisen
        mAccel = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mTemp = sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);

        sensorManager.registerListener(this, mAccel, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, mTemp, SensorManager.SENSOR_DELAY_NORMAL);

        if (!checkPermissions()) {
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
            labelLocation.setText("Latitude      " + latitude + "\n" + "Longitude " + longitude);
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
        try {
            updateUI(account);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                handleSignInResult(task);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) throws IOException {
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
                        try {
                            updateUI(null);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    private void revokeAccess() {
        mGoogleSignInClient.revokeAccess()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        try {
                            updateUI(null);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    private void updateUI(@Nullable GoogleSignInAccount account) throws IOException {
        if (account != null) {
            findViewById(R.id.sign_in_button).setVisibility(View.GONE);
            txtName.setText(account.getDisplayName());
            txtEmail.setText(account.getEmail());
            txtId.setText(account.getId());

            // Aufgabe 2 Profilbild
            Uri imgUri = account.getPhotoUrl();
            if (imgUri != null)
            {
                URL imgUrl = new URL(imgUri.toString());
                Bitmap imgPic = BitmapFactory.decodeStream(imgUrl.openConnection().getInputStream());
                imgProfilePic.setImageBitmap(imgPic);
            }

            llProfileLayout.setVisibility(View.VISIBLE);
            findViewById(R.id.btn_logout).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.sign_in_button).setVisibility(View.VISIBLE);
            findViewById(R.id.btn_logout).setVisibility(View.GONE);
            llProfileLayout.setVisibility(View.GONE);
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