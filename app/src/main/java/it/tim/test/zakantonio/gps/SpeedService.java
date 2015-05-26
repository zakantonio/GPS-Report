package it.tim.test.zakantonio.gps;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.Toast;

public class SpeedService extends Service {

    private final IBinder mBinder = new MyBinder();

    LocationManager locationManager;
    LocationListener locationListener;

    private float speed;
    private float longitude;
    private float latitude;
    private float altitude;
    private float accuracy;
    private float bearing;

    public SpeedService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return mBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Let it continue running until it is stopped.
        Toast.makeText(this, "Service Started", Toast.LENGTH_LONG).show();

        // Acquire a reference to the system Location Manager
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        // Define a listener that responds to location updates
        locationListener = new LocationListener() {

            // When a different location is detected, the service update the report variable
            // You need to call the getter method to take this infos
            public void onLocationChanged(Location location) {
                location.getLatitude();
                speed = location.getSpeed();
                longitude = (float) location.getLongitude();
                latitude = (float) location.getLatitude();
                altitude = (float) location.getAltitude();
                accuracy = location.getAccuracy();
                bearing = location.getBearing();
                // Sending a custom broadcast intent, it will be detected in the MainActivity
                Intent i = new Intent("it.test.speed");
                sendBroadcast(i);

            }
            public void onStatusChanged(String provider, int status, Bundle extras) { }
            public void onProviderEnabled(String provider) { }
            public void onProviderDisabled(String provider) { }
        };

        // This need to receive the update from the GPS sensor
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

        return START_STICKY;
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        // Stop the GPS updates and destroy the service
        locationManager.removeUpdates(locationListener);
        Toast.makeText(this, "Service Destroyed", Toast.LENGTH_LONG).show();
    }

    public float getSpeed() {
        return speed;
    }

    public float getLongitude() {
        return longitude;
    }

    public float getLatitude() {
        return latitude;
    }

    public float getAltitude () {
        return altitude;
    }


    public float getAccuracy() {
        return accuracy;
    }

    public float getBearing() {
        return bearing;
    }
    
    public class MyBinder extends Binder {
        SpeedService getService() {
            return SpeedService.this;
        }
    }

}
