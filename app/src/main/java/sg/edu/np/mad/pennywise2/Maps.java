package sg.edu.np.mad.pennywise2;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.List;

import sg.edu.np.mad.pennywise2.FetchRouteData;
import sg.edu.np.mad.pennywise2.databinding.ActivityMapsBinding;

public class Maps extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    private FusedLocationProviderClient fusedLocationProviderClient;
    public static final int Request_code = 101;
    private double lat, lng;
    Button atm, bank, walk, drive;

    SharedPreferences sharedPreferences;
    private static final String GLOBAL_PREFS = "myPrefs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        atm = findViewById(R.id.btnATM);
        bank = findViewById(R.id.btnBank);
        walk = findViewById(R.id.btnWalk);
        drive = findViewById(R.id.btnDrive);
        walk.setVisibility(View.GONE);
        drive.setVisibility(View.GONE);


        sharedPreferences = getSharedPreferences(GLOBAL_PREFS, MODE_PRIVATE);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this.getApplicationContext());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // Clear the map, get the location markers, Store atm or bank in shared preference
        atm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMap.clear();
                getAtmMarker();
                walk.setVisibility(View.GONE);
                drive.setVisibility(View.GONE);

                atm.setBackgroundTintList(getResources().getColorStateList(R.color.darkblue));
                bank.setBackgroundTintList(getResources().getColorStateList(R.color.blue));

                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("location","atm");
                editor.apply();
            }
        });

        bank.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMap.clear();
                getBankMarker();
                walk.setVisibility(View.GONE);
                drive.setVisibility(View.GONE);

                bank.setBackgroundTintList(getResources().getColorStateList(R.color.darkblue));
                atm.setBackgroundTintList(getResources().getColorStateList(R.color.blue));

                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("location","bank");
                editor.apply();
            }
        });

        // Go back to home page
        TextView homeTv = findViewById(R.id.map_home);
        homeTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Maps.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        getCurrentLocation();

        // Add marker click listener
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                walk.setVisibility(View.VISIBLE);
                drive.setVisibility(View.VISIBLE);
                resetMapAndGetMarkers();
                LatLng originLatLng = new LatLng(lat, lng);
                LatLng destinationLatLng = marker.getPosition();

                // Draw the new route
                drawRoute(originLatLng, destinationLatLng);
                return false;
            }
        });
    }

    private void getCurrentLocation(){
        // Request permission if not granted
        if(ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},Request_code);
            return;
        }
        mMap.setMyLocationEnabled(true);
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(60000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setFastestInterval(5000);
        LocationCallback locationCallback = new LocationCallback();

        fusedLocationProviderClient.requestLocationUpdates(locationRequest,locationCallback,null);
        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if(location != null){
                    lat = location.getLatitude();
                    lng = location.getLongitude();

                    LatLng latLng = new LatLng(lat, lng);
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,15));
                }
            }
        });
    }


    // Draw the route from current location to the selected location markers
    private void drawRoute(LatLng origin, LatLng destination) {

        // Default mode : walk
        String url = "https://maps.googleapis.com/maps/api/directions/json?" +
                "origin=" + origin.latitude + "," + origin.longitude +
                "&destination=" + destination.latitude + "," + destination.longitude + "&mode=walking" +
                "&key=AIzaSyAVFyRUxiLvUNtdJqJRBkH5XuALEVGCC_Y";
        FetchRouteData fetchRouteData = new FetchRouteData(mMap);
        fetchRouteData.execute(url);
        walk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                walk.setBackgroundTintList(getResources().getColorStateList(R.color.darkblue));
                drive.setBackgroundTintList(getResources().getColorStateList(R.color.blue));
                resetMapAndGetMarkers();
                String location = sharedPreferences.getString("location","");
                if (location.equals("bank")){

                }
                String url = "https://maps.googleapis.com/maps/api/directions/json?" +
                        "origin=" + origin.latitude + "," + origin.longitude +
                        "&destination=" + destination.latitude + "," + destination.longitude + "&mode=walking" +
                        "&key=AIzaSyAVFyRUxiLvUNtdJqJRBkH5XuALEVGCC_Y";
                FetchRouteData fetchRouteData = new FetchRouteData(mMap);
                fetchRouteData.execute(url);
            }
        });
        drive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drive.setBackgroundTintList(getResources().getColorStateList(R.color.darkblue));
                walk.setBackgroundTintList(getResources().getColorStateList(R.color.blue));
                mMap.clear();
                resetMapAndGetMarkers();
                String url = "https://maps.googleapis.com/maps/api/directions/json?" +
                        "origin=" + origin.latitude + "," + origin.longitude +
                        "&destination=" + destination.latitude + "," + destination.longitude + "&mode=driving" +
                        "&key=AIzaSyAVFyRUxiLvUNtdJqJRBkH5XuALEVGCC_Y";
                FetchRouteData fetchRouteData = new FetchRouteData(mMap);
                fetchRouteData.execute(url);
            }
        });
    }

    // Get location markers for ATMs
    private void getAtmMarker(){
        StringBuilder stringBuilder = new StringBuilder("http://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        stringBuilder.append("location="+lat+","+lng);
        stringBuilder.append("&radius=2000");
        stringBuilder.append("&type=atm");
        stringBuilder.append("&sensor=true");
        stringBuilder.append("&key=AIzaSyAVFyRUxiLvUNtdJqJRBkH5XuALEVGCC_Y");

        fetchData(stringBuilder);
    }

    // Get location markers for banks
    private void getBankMarker(){
        StringBuilder stringBuilder = new StringBuilder("http://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        stringBuilder.append("location="+lat+","+lng);
        stringBuilder.append("&radius=2000");
        stringBuilder.append("&type=bank");
        stringBuilder.append("&sensor=true");
        stringBuilder.append("&key=AIzaSyAVFyRUxiLvUNtdJqJRBkH5XuALEVGCC_Y");

        fetchData(stringBuilder);
    }

    // Call the api with the URL
    private void fetchData(StringBuilder urlBuilder){
        String url = urlBuilder.toString();
        Object dataFetch[] = new Object[2];
        dataFetch[0] = mMap;
        dataFetch[1] = url;

        FetchData fetchData = new FetchData();
        fetchData.execute(dataFetch);
    }

    // Resets map and get markers
    private void resetMapAndGetMarkers(){
        mMap.clear();
        String location = sharedPreferences.getString("location","");
        if (location.equals("atm")){
            getAtmMarker();
        }
        if (location.equals("bank")){
            getBankMarker();
        }
    }
}