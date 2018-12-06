package com.myeyes.myeyes;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;


import android.app.Service;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;

public class Location extends Service implements LocationListener {
    LocationManager locationManager;
    double longitud;
    double latitud;
    android.location.Location location;
    boolean gpsActivo;


    int estado;
    Context context;

    public Location(Context applicationContext) {
        super();
        this.context = applicationContext;

        getLocation();
    }

    public Location() {
        super();

    }


    public double getLongitud() {

        return longitud;
    }


    public double getEstado() {
        return estado;
    }

    public double getLatitud() {
        return latitud;
    }


    public void getLocation() {
        try {
            locationManager = (LocationManager) this.context.getSystemService(context.LOCATION_SERVICE);
            gpsActivo = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER );
        } catch (Exception e) {
            System.out.println("Error: 0"+e.getMessage());
        }

        if (locationManager != null) {
            try{

                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                    return;
                }


                locationManager.requestLocationUpdates(locationManager.NETWORK_PROVIDER , 5000, 10, this);

                location = locationManager.getLastKnownLocation(locationManager.NETWORK_PROVIDER );

                latitud = location.getLatitude();
                longitud = location.getLongitude();


                Toast.makeText(context,"ojo "+longitud,Toast.LENGTH_LONG).show();

            }catch (Exception e){
                System.out.println("Error: "+e.getMessage());
            }
        }


    }

    @Override
    public void onLocationChanged(android.location.Location location) {
        double latitude=location.getLatitude();
        double longitude=location.getLongitude();

        System.out.println("Loca: "+location.getLatitude());



        this.longitud = longitude;
        this.latitud = latitude;

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        this.estado = status;
    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
