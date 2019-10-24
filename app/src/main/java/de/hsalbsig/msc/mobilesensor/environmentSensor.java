package de.hsalbsig.msc.mobilesensor;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.widget.TextView;

public class environmentSensor extends Activity implements SensorEventListener {
    private final SensorManager tempSensorManager;
    private final Sensor mTemp;
    private final TextView tempLabel;

    public environmentSensor() {
        tempSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mTemp = tempSensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);
        tempLabel = findViewById(R.id.label_temp);
    }

    protected void onResume() {
        super.onResume();
        tempSensorManager.registerListener(this, mTemp, SensorManager.SENSOR_DELAY_NORMAL);
    }

    protected void onPause() {
        super.onPause();
        tempSensorManager.unregisterListener(this);
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    public void onSensorChanged(SensorEvent event) {
        float ambient_temperature = event.values[0];
        tempLabel.setText(String.valueOf(ambient_temperature) + getResources().getString(R.string.celsius));
    }
}