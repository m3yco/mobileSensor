package de.hsalbsig.msc.mobilesensor;

import android.Manifest;
import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

public class LocationTracker extends Service implements LocationListener {

    private static final long MIN_DISTANCE_TO_REQUEST_LOCATION=1;

    Location location;
    //latitude and longitude
    double latitude,longitude;
    //Declaring a LocationManager

    private final Context con;

    //flag for gps
    boolean isGPSOn=false;
    //flag for network location
    boolean isNetWorkEnabled=false;
    //flag to getlocation
    boolean isLocationEnabled=false;

    static String logLoc = LocationManager.class.getSimpleName();

    LocationManager locationManager;
    public LocationTracker(Context context)
    {
        this.con=context;
        checkIfLocationAvailable();
    }
    public Location checkIfLocationAvailable()
    {
        try
        {
            locationManager=(LocationManager)con.getSystemService(LOCATION_SERVICE);
            //check for gps availability
            isGPSOn=locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            //check for network availablity
            isNetWorkEnabled=locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            if(!isGPSOn && !isNetWorkEnabled)
            {
                isLocationEnabled=false;
                // no location provider is available show toast to user
                Toast.makeText(con,"No Location Provider is Available",Toast.LENGTH_SHORT).show();
            }
            else {
                isLocationEnabled=true;
                // if network location is available request location update
                if(isNetWorkEnabled)
                {
                    if(locationManager!=null)
                    {
                        try {
                            location=locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        }
                        catch (SecurityException e){
                            Log.i(logLoc, e.getMessage());
                        }
                        if(location!=null)
                        {
                            latitude=location.getLatitude();
                            longitude=location.getLongitude();
                        }
                    }
                }
                if(isGPSOn)
                {
                    if(locationManager!=null)
                    {
                        try {
                            location=locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        }
                        catch (SecurityException e){
                            Log.i(logLoc, e.getMessage());
                        }
                        if(location!=null)
                        {
                            latitude=location.getLatitude();
                            longitude=location.getLongitude();
                        }
                    }
                }
            }
        }catch (Exception e)
        {
        }
        return location;
    }

    public double getLatitude()
    {
        if(location!=null)
        {
            latitude=location.getLatitude();
        }
        return latitude;
    }
    //call this to getLongitude
    public double getLongitude()
    {
        if(location!=null)
        {
            longitude=location.getLongitude();
        }
        return longitude;
    }

    @Override
    public void onLocationChanged(Location location) {
        this.location=location;
    }

    @Override
    public void onProviderDisabled(String provider) {
    }
    @Override
    public void onProviderEnabled(String provider) {
    }
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
