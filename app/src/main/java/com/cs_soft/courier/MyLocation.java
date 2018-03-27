package com.cs_soft.courier;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


/*import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import im.delight.android.location.SimpleLocation;*/

public class MyLocation extends AppCompatActivity implements LocationListener {
    /* private SimpleLocation location;
     SupportMapFragment mapFragment = null;*/
    Button btn = null;
    TextView textView = null;
    LocationManager locationManager = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_location);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        btn = (Button) findViewById(R.id.btn1);
        textView = (TextView) findViewById(R.id.textView4);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        Location location = locationManager.getLastKnownLocation(locationManager.NETWORK_PROVIDER);
        onLocationChanged(location);
        Log.e("location",location.getLatitude()+"");
        Log.e("location",location.getLongitude()+"");
        /*mapFragment = (SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map);
        location = new SimpleLocation(MyLocation.this);
        final double latitude = location.getLatitude();
        final double longitude = location.getLongitude();
        final LatLng llng = new LatLng(42.876368,74.605391);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {
                mMap.addMarker(new MarkerOptions().position(llng)
                        .title("Мое местонахождение"));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(llng));
            }});*/

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }


    @Override
    public void onLocationChanged(Location location) {
        double lat = location.getLatitude();
        double longt = location.getLongitude();
        textView.setText(lat +" : "+longt);
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }
}
