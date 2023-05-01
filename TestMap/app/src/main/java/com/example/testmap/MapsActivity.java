package com.example.testmap;

import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.testmap.databinding.ActivityMapsBinding;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    double Lat,Lng;
    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent intent = getIntent();

        /** Get Latitude and Longitude from MainActivity */
        Lat = intent.getDoubleExtra("Lat",0);
        Lng = intent.getDoubleExtra("Long",0);

        super.onCreate(savedInstanceState);
        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng myLocation = new LatLng(Lat,Lng);

        /** Add marker, move camera and zoom in location*/
        mMap.addMarker(new MarkerOptions().position(myLocation).title("This is your device location"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(myLocation));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation,23));
    }
}