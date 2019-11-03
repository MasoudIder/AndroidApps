package com.example.taxiapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.location.LocationServices;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    public static final String TAG = "TAG";
    private static final int REQEST_CODE = 1000;

    private GoogleApiClient googleApiClient;
    private Location location;

    private TextView txtLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtLocation = findViewById(R.id.txtLocation);

        googleApiClient = new GoogleApiClient.Builder(MainActivity.this)
                .addConnectionCallbacks(MainActivity.this)
                .addOnConnectionFailedListener(MainActivity.this)
                .addApi(LocationServices.API).build();


    }

    @Override
    public void onConnected(Bundle bundle) {

        showLocation();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

        if (connectionResult.hasResolution()) {
            try {
                connectionResult.startResolutionForResult(MainActivity.this, REQEST_CODE);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(MainActivity.this, "google play service is not working !", Toast.LENGTH_LONG).show();
            finish();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQEST_CODE && resultCode == RESULT_OK) {

            googleApiClient.connect();
        }

    }

    @Override
    protected void onStart() {
        super.onStart();

        if (googleApiClient != null) {
            googleApiClient.connect();
        }
    }

    //custom methods

    private void showLocation() {

        int permissionCheck = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION);

        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {

            FusedLocationProviderApi fusedLocationProviderApi = LocationServices.FusedLocationApi;

            location = fusedLocationProviderApi.getLastLocation(googleApiClient);

            if (location != null) {
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();

                txtLocation.setText(latitude + " - " + longitude);
            } else {
                txtLocation.setText("The app is not able to access the location now");
            }


        } else {
            txtLocation.setText("The app is not allowed to access location");
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
        }

    }


}

