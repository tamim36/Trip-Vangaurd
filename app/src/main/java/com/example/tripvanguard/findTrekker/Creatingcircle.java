package com.example.tripvanguard.findTrekker;


import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tripvanguard.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Creatingcircle extends AppCompatActivity {


    private RecyclerView recyclerView;
    private FriendAdapter adapter;
    private DatabaseReference mData;
    private String userID;
    private EditText search_text;
    private Button search_button;
    private List<User> friendList;
    private List<friend>temp = new ArrayList<>();
    AlertDialog.Builder builder1 ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_c_circle);

        search_text = findViewById(R.id.search_text);
        search_button=findViewById(R.id.search_button_Recycler);

        builder1 = new AlertDialog.Builder(Creatingcircle.this);
        builder1.setTitle("Make Friend ");
        builder1.setMessage("Are you sure?");
        builder1.setCancelable(true);


        friendList = new ArrayList<>();
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        mData = FirebaseDatabase.getInstance().getReference().child("Users");
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        userID = user.getUid();



        mData.keepSynced(true);

        mData.addValueEventListener(valueEventListener);
        //mData.addValueEventListener(value);




        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(this,
                recyclerView, new ClickListener() {

            @Override
            public void onClick(View view, final int position) {
                builder1.setPositiveButton(
                        "Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                String title1 = ((TextView) recyclerView.findViewHolderForAdapterPosition(position).itemView.findViewById(R.id.friend_key)).getText().toString();
                                String key= FirebaseDatabase.getInstance().getReference().child("Users").child(userID).child("friends").push().getKey();
                                //.setValue(title1);

                                friend f = new friend(title1,key);
                                friend ff = new friend(userID,key);

                                FirebaseDatabase.getInstance().getReference().child("Users").child(userID).child("friends").child(title1).setValue(f);
                                FirebaseDatabase.getInstance().getReference().child("Users").child(title1).child("friends").child(userID).setValue(ff);

                                //Toast.makeText(Creatingcircle.this,"Friend Added to circle",Toast.LENGTH_LONG).show();

                                dialog.cancel();
                            }
                        });

                builder1.setNegativeButton(
                        "No",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

                AlertDialog alert11 = builder1.create();
                alert11.show();
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        adapter = new FriendAdapter(this, friendList);
        recyclerView.setAdapter(adapter);

        search_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference mData1= FirebaseDatabase.getInstance().getReference().child("Users");
                String search_name = search_text.getText().toString().trim();
                if(!TextUtils.isEmpty(search_name)){
                    Query query = mData1.orderByChild("name").startAt(search_name).endAt(search_name+"\uf8ff");
                    query.addValueEventListener(valueEventListener);
                }
            }
        });

    }
    ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            friendList.clear();
            if (dataSnapshot.exists()) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    User driver = snapshot.getValue(User.class);
                    friendList.add(driver);
                }
                adapter.notifyDataSetChanged();
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }

    };

    public static interface ClickListener{
        public void onClick(View view, int position);
        public void onLongClick(View view,int position);
    }
    class RecyclerTouchListener implements RecyclerView.OnItemTouchListener{

        private ClickListener clicklistener;
        private GestureDetector gestureDetector;

        public RecyclerTouchListener(Context context, final RecyclerView recycleView, final ClickListener clicklistener){

            this.clicklistener=clicklistener;
            gestureDetector=new GestureDetector(context,new GestureDetector.SimpleOnGestureListener(){
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View child=recycleView.findChildViewUnder(e.getX(),e.getY());
                    if(child!=null && clicklistener!=null){
                        clicklistener.onLongClick(child,recycleView.getChildAdapterPosition(child));
                    }
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
            View child=rv.findChildViewUnder(e.getX(),e.getY());
            if(child!=null && clicklistener!=null && gestureDetector.onTouchEvent(e)){
                clicklistener.onClick(child,rv.getChildAdapterPosition(child));
            }

            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {

        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }
    }
}
