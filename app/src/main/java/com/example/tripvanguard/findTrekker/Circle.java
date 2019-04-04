package com.example.tripvanguard.findTrekker;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;
import android.os.Vibrator;

import com.example.tripvanguard.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Circle extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    LocationManager locationManager;
    DatabaseReference mData;
    private String userID;
    private String userName = "",tempuser="";
    public double distanceFriend ,latitude,longitude ;
    public long tempdistance;
    final DatabaseReference friendDatabaseReference = FirebaseDatabase.getInstance().getReference();
    private ArrayList<String> friendList = new ArrayList<>();

    private Context con;

    private Button Satellite,Roadmap;
    private Switch HELP = null;
    AlertDialog.Builder builder1,builder2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        mData = FirebaseDatabase.getInstance().getReference().child("Users");
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        userID = user.getUid();
        userName = getUserName(userID);
        mData.keepSynced(true);

        builder1 = new AlertDialog.Builder(Circle.this);
        builder2 = new AlertDialog.Builder(Circle.this);

        HELP = (Switch) findViewById(R.id.help);
        HELP.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // do something when check is selected
                    FirebaseDatabase.getInstance().getReference().child("Users").child(userID).child("help").setValue(true);
                    builder2.setMessage("Your friends are notified to help you.");
                    builder2.setCancelable(true);
                    AlertDialog alert2 = builder2.create();
                    alert2.show();
                } else {
                    //do something when unchecked
                    FirebaseDatabase.getInstance().getReference().child("Users").child(userID).child("help").setValue(false);
                    builder2.setMessage("Your stopped the helping alert to your friends.");
                    builder2.setCancelable(true);
                    AlertDialog alert2 = builder2.create();
                    alert2.show();
                }
            }
        });

        Satellite=findViewById(R.id.satellite);
        Satellite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMap.setMapType(mMap.MAP_TYPE_SATELLITE);
            }
        });

        Roadmap=findViewById(R.id.roadmap);
        Roadmap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMap.setMapType(mMap.MAP_TYPE_NORMAL);
            }
        });

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        //check the networkprovider is enabled
        if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, new LocationListener() {
                @Override
                public void onLocationChanged(android.location.Location location) {

                    mMap.clear();
                    friendList.clear();
                    //fetchFriends();
                    // get the latitude
                    latitude = location.getLatitude();
                    //get the longitude
                    longitude = location.getLongitude();
                    //instantiate the class LatLang
                    LatLng latlang = new LatLng(latitude, longitude);

                    mData = FirebaseDatabase.getInstance().getReference().child("Users");
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    userID = user.getUid();
                    userName = getUserName(userID);
                    mData.keepSynced(true);

                    FirebaseDatabase.getInstance().getReference().child("Users")
                            .child(userID).child("lat").setValue(latitude);
                    FirebaseDatabase.getInstance().getReference().child("Users")
                            .child(userID).child("lng").setValue(longitude);


                    //instantiate the class Geaocoder
                    Geocoder geocoder = new Geocoder(getApplicationContext());
                    try {
                        List<Address> addressList = geocoder.getFromLocation(latitude, longitude, 1);

                        mMap.addMarker(new MarkerOptions().position(latlang).title(userName).alpha(0.7f));
                        fetchFriends();
                        //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlang, 25.2f));

                        com.google.android.gms.maps.model.Circle circle = mMap.addCircle(new CircleOptions()
                                .center(latlang)
                                .radius(1000)
                                .strokeColor(Color.rgb(00, 11, 22))
                                .strokeWidth(2)
                                .fillColor(0x5500ff00));

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {

                }

                @Override
                public void onProviderEnabled(String provider) {

                }

                @Override
                public void onProviderDisabled(String provider) {

                }
            });
        } else if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            locationManager.requestLocationUpdates(locationManager.GPS_PROVIDER, 0, 0, new LocationListener() {
                @Override
                public void onLocationChanged(android.location.Location location) {
                    mMap.clear();
                    friendList.clear();
                    //fetchFriends();
                    // get the latitude
                    double latitude = location.getLatitude();
                    //get the longitude
                    double longitude = location.getLongitude();

                    //instantiate the class LatLang
                    LatLng latlang = new LatLng(latitude, longitude);

                    mData = FirebaseDatabase.getInstance().getReference().child("Users");
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    userID = user.getUid();
                    userName = getUserName(userID);
                    mData.keepSynced(true);

                    FirebaseDatabase.getInstance().getReference().child("Users")
                            .child(userID).child("position").child("lat").setValue(latitude);
                    FirebaseDatabase.getInstance().getReference().child("Users")
                            .child(userID).child("position").child("lng").setValue(longitude);

                    //instantiate the class Geaocoder
                    Geocoder geocoder = new Geocoder(getApplicationContext());
                    try {
                        List<Address> addressList = geocoder.getFromLocation(latitude, longitude, 1);


                        mMap.addMarker(new MarkerOptions().position(latlang).title(userName).alpha(0.7f));
                        //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlang, 25.2f));

                        fetchFriends();
                        com.google.android.gms.maps.model.Circle circle = mMap.addCircle(new CircleOptions()
                                .center(latlang)
                                .radius(1000)
                                .strokeColor(Color.rgb(00, 11, 22))
                                .strokeWidth(2)
                                .fillColor(0x5500ff00));

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {

                }

                @Override
                public void onProviderEnabled(String provider) {

                }

                @Override
                public void onProviderDisabled(String provider) {

                }
            });
        }



    }
    void fetchFriends()
    {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        userID = user.getUid();
        friendDatabaseReference.child("Users/"+userID+"/friends").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                friend st = dataSnapshot.getValue(friend.class);
                friendList.add(st.getUidm());
                drawFriendsOnMap(st.getUidm(),friendDatabaseReference);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    String getFriendUserName(String uid)
    {
        FirebaseDatabase.getInstance().getReference("Users/"+uid)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        tempuser = dataSnapshot.getValue(User.class).getName();
                        System.out.println(tempuser);
                        //TODO why is it null???
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
        return tempuser;
    }


    void helpalert()
    {

        MediaPlayer mediaPlayer = new MediaPlayer();
        try
        {
            mediaPlayer.setDataSource("https://firebasestorage.googleapis.com/v0/b/androidstudioprojects-3e974.appspot.com/o/Eas%20Beep-SoundBible.com-238025417.mp3?alt=media&token=c48340e3-3ea0-47ab-adde-7530a21a37d7");
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mp.start();
                }
            });
            mediaPlayer.prepare();
        }catch (IOException e)
        {
            e.printStackTrace();
        }

        Vibrator v = (Vibrator) getSystemService(Circle.VIBRATOR_SERVICE);
        // Vibrate for 500 milliseconds
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createOneShot(3000, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            //deprecated in API 26
            v.vibrate(3000);
        }
    }
    String getUserName(String uid)
    {
        FirebaseDatabase.getInstance().getReference("Users/"+uid)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        userName = dataSnapshot.getValue(User.class).getName();
                        System.out.println(userName);
                        //TODO why is it null???
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
        return userName;
    }


    void drawFriendsOnMap(String fkey, final DatabaseReference databaseReference) {
        Query query = databaseReference.child("Users").orderByChild("key").equalTo(fkey);
        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                User fuser = dataSnapshot.getValue(User.class);
                distanceFriend=getDistance( latitude, longitude , fuser.getLat(),fuser.getLng());
                tempdistance = (long) distanceFriend;
                mMap.addMarker(new MarkerOptions().position(new LatLng(fuser.getLat(),fuser.getLng())).title(fuser.getName()+" , "+tempdistance+" meter").alpha(0.7f))
                        .setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
                builder1.setTitle("Help Alert");
                builder1.setMessage(fuser.getName() + "  needs help.");
                builder1.setCancelable(true);

                if (fuser.isHelp()) {
                    AlertDialog alert1 = builder1.create();
                    if(!((Activity) Circle.this).isFinishing())
                    {
                        alert1.show();
                        //show dialog
                    }
                    //Toast.makeText(Circle.this,fuser.getName() + " needs help.",Toast.LENGTH_LONG).show();
                    helpalert();
                }



            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }



    private float getDistance(double lat1, double lon1, double lat2, double lon2) {
        float[] distance = new float[2];
        Location.distanceBetween(lat1, lon1, lat2, lon2, distance);
        return distance[0];
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
        //mMap.setMapType(mMap.MAP_TYPE_SATELLITE);

        //fetchFriends();
//        helpalert();
        // Add a marker in Sydney and move the camera
//        LatLng sydney = new LatLng(-34, 151);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney , 10.2f));
    }


}
