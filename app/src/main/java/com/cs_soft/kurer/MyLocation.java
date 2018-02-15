package com.cs_soft.kurer;

import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import im.delight.android.location.SimpleLocation;

public class MyLocation extends AppCompatActivity {
    private SimpleLocation location;
    SupportMapFragment mapFragment = null;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_location);
        mapFragment = (SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map);
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
            }});

    }









}
