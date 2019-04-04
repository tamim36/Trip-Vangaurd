package com.example.tripvanguard.findTrekker;


import android.content.Intent;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.tripvanguard.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainTrekker extends AppCompatActivity {

    private EditText username,password,email;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference mDatabase;
    private String userID;

    private Button login,register;
    //FirebaseApp.initializeApp(this);

    private void registerUser(final String name , String pass ,final String email_)
    {

        if (name.isEmpty())
        {
            username.setError("name required");
            username.requestFocus();
            return ;
        }
        if (email_.isEmpty())
        {
            email.setError("email required");
            email.requestFocus();
            return ;
        }
        if (pass.isEmpty())
        {
            password.setError("name required");
            password.requestFocus();
            return ;
        }

        firebaseAuth.createUserWithEmailAndPassword(email_ , pass ).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful())
                {
                    final User newuser = new User(
                            name , email_
                    );

                    FirebaseDatabase.getInstance().getReference("Users")
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .setValue(newuser).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful() )
                            {
                                Toast.makeText(MainTrekker.this,"Registration successful",Toast.LENGTH_LONG).show();
                                mDatabase = FirebaseDatabase.getInstance().getReference();
                                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                userID = user.getUid();


                                FirebaseDatabase.getInstance().getReference().child("Users")
                                        .child(userID).child("key").setValue(userID).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful())
                                            Toast.makeText(MainTrekker.this,"HI",Toast.LENGTH_LONG).show();
                                        else
                                            Toast.makeText(MainTrekker.this,"HI er gulli",Toast.LENGTH_LONG).show();
                                    }
                                });
                            }
                        }
                    });

                }else {
                    Toast.makeText(MainTrekker.this,task.getException().getMessage(),Toast.LENGTH_LONG).show();
                }
            }
        });
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trekker);
        FirebaseApp.initializeApp(this);

        if(FirebaseAuth.getInstance().getCurrentUser() != null)
        {
            Intent intent = new Intent(this,Homepage.class);
            startActivity(intent);
        }
        username = findViewById(R.id.Username);
        password = findViewById(R.id.Password);
        email= findViewById(R.id.Email);
        login = findViewById(R.id.Login);
        register = findViewById(R.id.Register);


        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String user=username.getText().toString().trim();
                String pass=password.getText().toString().trim();
                String emai=email.getText().toString().trim();
                firebaseAuth.signInWithEmailAndPassword( emai , pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(MainTrekker.this, "Login Successful", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(getApplicationContext(),Homepage.class);
                            startActivity(intent);
                        }
                        else
                            Toast.makeText(MainTrekker.this, "Login Failed",Toast.LENGTH_LONG).show();

                    }
                });
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String user=username.getText().toString().trim();
                final String pass=password.getText().toString().trim();
                final String emai=email.getText().toString().trim();

                registerUser(user , pass ,emai);
            }
        });

        FirebaseApp.initializeApp(this);
        firebaseAuth = FirebaseAuth.getInstance();
    }

}
