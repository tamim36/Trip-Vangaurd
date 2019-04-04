package com.example.tripvanguard.findTrekker;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.tripvanguard.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class Homepage extends AppCompatActivity  {
    private static final String TAG = "ViewOwnerInformation";

    private DrawerLayout mDrawerLayout;
    private DatabaseReference mDatabase;
    private TextView nameTxt,email,nameTxtHome,emailHome;
    private String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Find Trekker");
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_nav_dra);
        NavigationView navigationView = findViewById(R.id.nav_view);

        // NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        nameTxt= (TextView) headerView.findViewById(R.id.userName);
        email = (TextView) headerView.findViewById(R.id.userEmail);
        nameTxtHome=(TextView) findViewById(R.id.userName1);
        emailHome=(TextView) findViewById(R.id.userEmail1);
        mDrawerLayout = findViewById(R.id.drawer_layout);


        mDatabase = FirebaseDatabase.getInstance().getReference();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        userID = user.getUid();


        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds:dataSnapshot.getChildren()){
                    User user = new User();
                    user.setName(ds.child(userID).getValue(User.class).getName());
                    user.setEmail(ds.child(userID).getValue(User.class).getEmail());

                    Log.d(TAG,user.getName());
                    Log.d(TAG,user.getName());
                    nameTxt.setText(user.getName());
                    email.setText(user.getEmail());

                    nameTxtHome.setText(user.getName());
                    emailHome.setText(user.getEmail());

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });




       /* mDatabase.getInstance().getReference().child("Users")
                .child()*/



        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        int id = menuItem.getItemId();
                        switch (id){
                            case R.id.nav_circle:
                                Intent intent1 = new Intent(Homepage.this,Circle.class);
                                startActivity(intent1);
                                break;
                            case R.id.nav_createcircle:
                                Intent intent2 = new Intent(Homepage.this,Creatingcircle.class);
                                startActivity(intent2);
                                break;
                            case R.id.nav_unfriend:
                                Intent intent3 = new Intent(Homepage.this,Unfriend.class);
                                startActivity(intent3);
                                break;
                            case R.id.nav_logout:
                                FirebaseAuth.getInstance().signOut();
                                startActivity(new Intent(Homepage.this,MainTrekker.class));

                                break;
                        }


                        // set item as selected to persist highlight
                        menuItem.setChecked(true);
                        // close drawer when item is tapped
                        mDrawerLayout.closeDrawers();

                        // Add code here to update the UI based on the item selected
                        // For example, swap UI fragments here

                        return true;
                    }
                });


    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void startMap(View view) {
        Intent intent = new Intent(this,Circle.class);
        startActivity(intent);
    }
}
