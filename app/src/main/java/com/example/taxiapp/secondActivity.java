package com.example.taxiapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.MapView;

import java.util.List;

public class secondActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, View.OnClickListener, LocationListener {

    public static final String TAG = "TAG";
    private static final int REQUEST2_CODE = 1000;

    private GoogleApiClient googleApiClient;

    EditText edtAddress, edtMeterPerMile, edtMilePerHour;
    TextView txtDistance, txtTimeLeft;
    Button btnGetData;

    String destinationAddress = "";

    private TaxiManager taxiManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        edtAddress = findViewById(R.id.edtAddress);
        edtMeterPerMile = findViewById(R.id.edtMeterPerMile);
        edtMilePerHour = findViewById(R.id.edtMilePerHour);

        txtDistance = findViewById(R.id.txtDistance);
        txtTimeLeft = findViewById(R.id.txtTime);

        btnGetData = findViewById(R.id.btnGetData);

        btnGetData.setOnClickListener(this);

        taxiManager = new TaxiManager();

        googleApiClient = new GoogleApiClient.Builder(secondActivity.this)
                .addConnectionCallbacks(secondActivity.this)
                .addOnConnectionFailedListener(secondActivity.this)
                .addApi(LocationServices.API).build();

    }

    @Override
    public void onClick(View v) {

        String addressValue = edtAddress.getText().toString();

        boolean isGeoCoding = true;

        if (!addressValue.equals(destinationAddress)) {

            destinationAddress = addressValue;

            Geocoder geocoder = new Geocoder(getApplicationContext());

            try {
                List<Address> myAddresses = geocoder.getFromLocationName(destinationAddress, 4);

                if (myAddresses != null) {

                    double latitude = myAddresses.get(0).getLatitude();
                    double longitude = myAddresses.get(0).getLongitude();

                    Location locationAddress = new Location("myDestination");

                    locationAddress.setLatitude(latitude);
                    locationAddress.setLongitude(longitude);

                    taxiManager.setDestinationLocation(locationAddress);


                }


            } catch (Exception e) {
                isGeoCoding = false;
                e.printStackTrace();
            }

        }

        int permissionCheck = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION);

        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {

            FusedLocationProviderApi fusedLocationProviderApi = LocationServices.FusedLocationApi;

            Location userCurrentLocation = fusedLocationProviderApi.getLastLocation(googleApiClient);

            if (userCurrentLocation != null && isGeoCoding) {

                txtDistance.setText(taxiManager.returnDistanceInMiles(userCurrentLocation, Integer.parseInt(edtMeterPerMile.getText().toString())));
                txtTimeLeft.setText(taxiManager.timeToReachDestination(userCurrentLocation, Float.parseFloat(edtMilePerHour.getText().toString()), Integer.parseInt(edtMeterPerMile.getText().toString())));


            }

        } else {
            ActivityCompat.requestPermissions(secondActivity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
        }

    }

    @Override
    public void onLocationChanged(Location location) {
        onClick(null);
    }

    @Override
    protected void onPause() {
        super.onPause();

        FusedLocationProviderApi fusedLocationProviderApi=LocationServices.FusedLocationApi;
        fusedLocationProviderApi.removeLocationUpdates(googleApiClient,secondActivity.this);

    }



    @Override
    public void onConnected(Bundle bundle) {

        //in order to get location online

        FusedLocationProviderApi fusedLocationProviderApi = LocationServices.FusedLocationApi;
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(10000);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        locationRequest.setSmallestDisplacement(5);

        if (googleApiClient.isConnected()){
            fusedLocationProviderApi.requestLocationUpdates(googleApiClient,locationRequest,secondActivity.this);
        }else {
            googleApiClient.connect();
        }

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

        if (connectionResult.hasResolution()) {
            try {
                connectionResult.startResolutionForResult(secondActivity.this, REQUEST2_CODE);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(secondActivity.this, "google play service is not working !", Toast.LENGTH_LONG).show();
            finish();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST2_CODE && resultCode == RESULT_OK) {

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


}
