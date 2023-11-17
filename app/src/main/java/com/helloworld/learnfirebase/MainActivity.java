package com.helloworld.learnfirebase;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;
import java.util.Locale;
import java.util.Random;

public class MainActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private EditText inputLongitude, inputLatitude;
    private Button btnSave;
    private DatabaseReference mFirebaseDatabase;
    private String coordinateId;
    private GoogleMap mMap;
    private boolean isClicked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        inputLatitude = findViewById(R.id.latitude);
        inputLongitude = findViewById(R.id.longitude);
        btnSave = findViewById(R.id.btn_save);

        FirebaseDatabase mFirebaseInstance = FirebaseDatabase.getInstance();
        // get reference to 'coordinate' node
        mFirebaseDatabase = mFirebaseInstance.getReference("coordinates");

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mFirebaseInstance.getReference("app_title").setValue("Realtime Coordinate");

        mFirebaseInstance.getReference("app_title").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.e(TAG, "App title updated");

                String appTitle = dataSnapshot.getValue(String.class);

                // update toolbar title
//                getSupportActionBar().setTitle(appTitle);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.e(TAG, "Failed to read app title value.", error.toException());
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String latitude = inputLatitude.getText().toString();
                String longitude = inputLongitude.getText().toString();

                if (TextUtils.isEmpty(latitude) || TextUtils.isEmpty(longitude)) {
                    // Handle empty fields
                    return;
                }

                if (TextUtils.isEmpty(coordinateId)){
                    createCoordinateMarker(latitude, longitude);
                }else{
                    updateCoordinate(latitude, longitude);
                }

            }
        });
    }

    /**
     * Creating new coordinate node under 'coordinates'
     */
    private void createCoordinate(String latitude, String longitude) {
        coordinateId = mFirebaseDatabase.push().getKey();

        Coordinate coordinate = new Coordinate(latitude, longitude);
        mFirebaseDatabase.child(coordinateId).setValue(coordinate);
        addCoordinateChangeListener();
    }

    /**
     * User data change listener
     */
    private void addCoordinateChangeListener() {
        mFirebaseDatabase.child(coordinateId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Coordinate coordinate = dataSnapshot.getValue(Coordinate.class);

                if (coordinate == null) {
                    Log.e(TAG, "Coordinate data is null!");
                    return;
                }

            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.e(TAG, "Failed to read coordinate", error.toException());
            }
        });
    }

    public void createCoordinateMarker(String latitude, String longitude){
        coordinateId = mFirebaseDatabase.push().getKey();

        Coordinate coordinate = new Coordinate(latitude, longitude);
        mFirebaseDatabase.child(coordinateId).setValue(coordinate);
        addMarkerLocation(latitude, longitude);
        addCoordinateChangeListener();
    }
    private void updateCoordinate(String latitude, String longitude) {
        // Updating the coordinate via child nodes
        mFirebaseDatabase.child(coordinateId).child("latitude").setValue(latitude);
        mFirebaseDatabase.child(coordinateId).child("longitude").setValue(longitude);
        addMarkerLocation(latitude, longitude);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMapLongClickListener(this);
    }

    @Override
    public void onMapLongClick(@NonNull LatLng latLng) {
        if (!isClicked) {
            inputLatitude.setText(String.valueOf(latLng.latitude));
            inputLongitude.setText(String.valueOf(latLng.longitude));

            MarkerOptions markerOptions = new MarkerOptions().position(latLng)
                    .title(getCompleteAddressString(latLng.latitude, latLng.longitude));
            mMap.addMarker(markerOptions.icon(BitmapDescriptorFactory.defaultMarker(new Random().nextInt(360))));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

            if (TextUtils.isEmpty(coordinateId)){
                createCoordinate(String.valueOf(latLng.latitude), String.valueOf(latLng.longitude));
            }else {
                updateCoordinate(String.valueOf(latLng.latitude), String.valueOf(latLng.longitude));
            }

            isClicked = true;
        }
    }
    public void addMarkerLocation(String latitude, String longitude){

        if (!latitude.isEmpty() && !longitude.isEmpty()) {
            double latitudeParse = Double.parseDouble(latitude);
            double longitudeParse = Double.parseDouble(longitude);

            MarkerOptions markerOptions = new MarkerOptions().position(new LatLng(latitudeParse, longitudeParse))
                    .title(getCompleteAddressString(latitudeParse, longitudeParse));
            mMap.addMarker(markerOptions.icon(BitmapDescriptorFactory.defaultMarker(new Random().nextInt(360))));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(latitudeParse,longitudeParse)));
        }
    }
    private String getCompleteAddressString(double LATITUDE, double LONGITUDE) {
        String strAdd = "";
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);
            if (addresses != null) {
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder();

                for (int i = 0; i <= returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                }
                strAdd = strReturnedAddress.toString();
                Log.w("Current location adress", strAdd);
            } else {
                Log.w("Current location adress", "No Address returned!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.w("Current loction address", "Canont get Address!");
        }
        return strAdd;
    }

}
