package com.example.maptest;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.EditText;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.internal.bind.MapTypeAdapterFactory;

public class MapActivity extends FragmentActivity implements OnMapReadyCallback {
    GoogleMap mMap;
    EditText etSource,etDest;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        init();
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapFragment);
        mapFragment.getMapAsync(this);
    }

    private void init(){
        etSource = (EditText)findViewById(R.id.etSource);
        etDest = (EditText)findViewById(R.id.etDestination);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LatLng source = new LatLng(19.1168512,72.8629248);
        mMap.addMarker(new MarkerOptions().position(source).title("6969 Location").visible(true).draggable(true));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(source));
        mMap.setMyLocationEnabled(true);
        mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker marker) {
                LatLng position = marker.getPosition();
                etSource.setText(position.latitude+","+position.longitude);
            }

            @Override
            public void onMarkerDrag(Marker marker) {
                LatLng position = marker.getPosition();
                etDest.setText(position.latitude+","+position.longitude);
            }

            @Override
            public void onMarkerDragEnd(Marker marker) {
                LatLng position = marker.getPosition();
                etDest.setText(position.latitude+","+position.longitude);
            }
        });
    }
}
