package com.example.tripvanguard;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.tripvanguard.Model.MyPlace;
import com.example.tripvanguard.Model.Results;
import com.example.tripvanguard.Remote.IGoogleApiServices;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.PlaceTypes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.
            ConnectionCallbacks,
            GoogleApiClient.OnConnectionFailedListener,
            GoogleMap.InfoWindowAdapter,
            LocationListener
{

    private static final int MY_PERMISSION_CODE = 1000;
    private static int snippet_check = 0;
    public GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;

    private double latitude;
    private double longtitude;
    private Location mlastLocation;
    private Marker mMarker;
    private LocationRequest mLocationRequest;


    MyPlace currentPlace;
    IGoogleApiServices mServices;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //init service
        mServices = Common.getGoogleAPIService();

        //Request Runtime permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            checkLocationPermission();
        }

        BottomNavigationView bottomNavigationView = (BottomNavigationView)findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                switch (menuItem.getItemId())
                {
                    case R.id.action_tourist_place:
                        snippet_check = 0;
                        placeFromDatabase();
                        break;
                    case R.id.action_entertainment:
                        snippet_check = 1;
                        combinePlace("A");
                        break;
                    case R.id.action_stand:
                        snippet_check = 1;
                        combinePlace("B");
                        break;
                    /*case R.id.action_restaurant:
                        nearbyPlace("restaurant");
                        break; */
                    case R.id.action_atm:
                        snippet_check = 1;
                        nearbyPlace("atm");
                        break;
                    /*case R.id.action_police:
                        nearbyPlace("police");
                        break;*/
                    case R.id.action_travelAgency:
                        snippet_check = 1;
                        nearbyPlace("travel_agency");
                        break;
                    default:
                        break;
                }

                return true;
            }
        });
    }

    private void combinePlace(String a) {
        mMap.clear();
        String[] placeArray = new String[20] ;
        if (a == "A")
            placeArray = new String[]{"amusement_park", "art_gallery", "museum", "park", "zoo"};
        else if (a=="B")
            placeArray = new String[]{"bus_station", "train_station"};

        for (int j=0; j<placeArray.length; j++) {
            String url = getUrl(latitude, longtitude, placeArray[j]);

            mServices.getNearbyPlaces(url)
                    .enqueue(new Callback<MyPlace>() {
                        @Override
                        public void onResponse(Call<MyPlace> call, Response<MyPlace> response) {

                            currentPlace = response.body();   //assign value to viewDetails

                            if (response.isSuccessful()) {
                                for (int i = 0; i < response.body().getResults().length; i++) {
                                    if(i==9)
                                        break;  //For not much place in the same category
                                    MarkerOptions markerOptions = new MarkerOptions();
                                    Results googlePlaces = response.body().getResults()[i];
                                    double lat = Double.parseDouble(googlePlaces.getGeometry().getLocation().getLat());
                                    double lng = Double.parseDouble(googlePlaces.getGeometry().getLocation().getLng());

                                    String placeName = googlePlaces.getName();
                                    String vicsinity = googlePlaces.getVicinity();
                                    LatLng latLng = new LatLng(lat, lng);
                                    markerOptions.position(latLng);
                                    markerOptions.title(placeName);
                                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));

                                    //markerOptions.snippet(String.valueOf(i));

                                    mMap.addMarker(markerOptions);

                                    mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                                    mMap.animateCamera(CameraUpdateFactory.zoomTo(11));

                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<MyPlace> call, Throwable t) {

                        }
                    });
        }
    }


    private void placeFromDatabase() {
        mMap.clear();
        DatabaseAccess databaseAccess = DatabaseAccess.getInstance(getApplicationContext());
        databaseAccess.open();
        ArrayList<Double> lat;
        ArrayList<Double> lng;
        ArrayList<String> placeName;

        lat = databaseAccess.getLat();
        lng = databaseAccess.getLong();
        placeName = databaseAccess.getPlaceName();
        for (int i=0; i<lat.size(); i++){
            LatLng saintMartin = new LatLng(lat.get(i), lng.get(i));
            mMap.addMarker(new MarkerOptions().position(saintMartin).title(placeName.get(i)));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(saintMartin));
        }


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Initialize google play service
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            {
                buildGoogleApiClient();
                mMap.setMyLocationEnabled(true);
            }
        }
        else {
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }
        mMap.setOnInfoWindowClickListener(MyOnInfoWindowClickListener);
        //event click for markers
        /*mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                Log.d("getSnippet", marker.getSnippet());

               // if (marker.getSnippet() == "500"){
                 //   startActivity(new Intent(MapsActivity.this, TripInfo.class));
                    //return true;
                //}
                //else {
                    //get result and assign to static variable n.b getresult return an array
                    Common.currentResults = currentPlace.getResults()[Integer.parseInt(marker.getSnippet())];

                    startActivity(new Intent(MapsActivity.this, ViewDetails.class));

                //}
                return true;
            }
        });*/
    }
    GoogleMap.OnInfoWindowClickListener MyOnInfoWindowClickListener
            = new GoogleMap.OnInfoWindowClickListener(){
        @Override
        public void onInfoWindowClick(Marker marker) {
            /*Toast.makeText(MapsActivity.this,
                    "onInfoWindowClick():\n" +
                            marker.getPosition().latitude + "\n" +
                            marker.getPosition().longitude,
                    Toast.LENGTH_LONG).show();*/
                    if (snippet_check == 0)
                        startActivity(new Intent(MapsActivity.this, mainDatabase.class));
                    //else
                        //startActivity(new Intent(MapsActivity.this, ViewDetails.class));
        }
    };

    private void nearbyPlace(final String placeType) {
        mMap.clear();
        String url = getUrl(latitude,longtitude,placeType);

        mServices.getNearbyPlaces(url)
                .enqueue(new Callback<MyPlace>() {
                    @Override
                    public void onResponse(Call<MyPlace> call, Response<MyPlace> response) {
                        if (response.isSuccessful())
                        {
                            for (int i=0; i<response.body().getResults().length; i++)
                            {
                                MarkerOptions markerOptions = new MarkerOptions();
                                Results googlePlaces = response.body().getResults()[i];
                                double lat = Double.parseDouble(googlePlaces.getGeometry().getLocation().getLat());
                                double lng = Double.parseDouble(googlePlaces.getGeometry().getLocation().getLng());

                                String placeName = googlePlaces.getName();
                                String vicsinity = googlePlaces.getVicinity();
                                LatLng latLng = new LatLng(lat,lng);
                                markerOptions.position(latLng);
                                markerOptions.title(placeName);
                                if (placeType.equals("hospital"))
                                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                                else if (placeType.equals("market"))
                                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                                else if (placeType.equals("restaurant"))
                                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                                else if (placeType.equals("atm"))
                                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                                else
                                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));

                                //indexing marker to view details
                                //markerOptions.snippet(String.valueOf(i));

                                mMap.addMarker(markerOptions);

                                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                                mMap.animateCamera(CameraUpdateFactory.zoomTo(11));
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<MyPlace> call, Throwable t) {

                    }
                });
    }

    private String getUrl(double latitude, double longtitude, String placeType) {
        StringBuilder googlePlacesUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        googlePlacesUrl.append("location=" +latitude+","+longtitude);
        googlePlacesUrl.append("&radius="+10000);
        googlePlacesUrl.append("&type="+placeType);
        googlePlacesUrl.append("&sensor=true");
        googlePlacesUrl.append("&key="+getResources().getString(R.string.google_maps_key));
        Log.d("getUrl",googlePlacesUrl.toString());
        return googlePlacesUrl.toString();
    }


    private boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION))
                ActivityCompat.requestPermissions(this, new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION
                }, MY_PERMISSION_CODE);
            else
                ActivityCompat.requestPermissions(this, new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION
                }, MY_PERMISSION_CODE);
            return false;
        }
        else
            return true;
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode)
        {
            case MY_PERMISSION_CODE:
            {
                if (grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    if (ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED)
                    {
                        if (mGoogleApiClient == null)
                            buildGoogleApiClient();
                        mMap.setMyLocationEnabled(true);
                    }
                }
                else
                    Toast.makeText(this,"permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }




    private synchronized void buildGoogleApiClient()
    {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
        {
             LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,mLocationRequest, this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        mlastLocation = location;

        if (mMarker != null)
            mMarker.remove();

        latitude = location.getLatitude();
        longtitude = location.getLongitude();

        LatLng latLng = new LatLng(latitude, longtitude);
        MarkerOptions markerOptions = new MarkerOptions()
                .position(latLng)
                .title("Your Position")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
        mMarker = mMap.addMarker(markerOptions);

        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(11));

        if (mGoogleApiClient != null)
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient,this);
    }

    @Override
    public View getInfoWindow(Marker marker) {

        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }
}
