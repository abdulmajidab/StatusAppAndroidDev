package com.example.statusapp.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.statusapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

public class ProfileActivity extends AppCompatActivity {



    ImageView expandedImage;

    Toolbar toolbar;

    EditText number;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

      //  getSupportActionBar().hide();



        expandedImage=findViewById(R.id.expandedImage);
        toolbar=findViewById(R.id.toolbar);

       /* setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);*/


        toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // startActivity(new Intent(getApplicationContext(),ChatActivity.class));

                onBackPressed();
            }
        });

        String sessionId = getIntent().getStringExtra("EXTRA_SESSION_ID");



        String currentId = FirebaseAuth.getInstance().getUid();


        //  String uid = "WDIgIub9JwWGkrNKeIea6eHLG9A2"; // FirebaseAuth.getInstance().getCurrentUser().getUid();

        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference uidRef = rootRef.child("users").child(sessionId);
        ValueEventListener valueEventListener = new ValueEventListener() {
            private String TAG;

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String Name = dataSnapshot.child("name").getValue(String.class);
                String Phone = dataSnapshot.child("phoneNumber").getValue(String.class);
                String ProfileImage = dataSnapshot.child("profileImage").getValue(String.class);
                String uidUser = dataSnapshot.child("uid").getValue(String.class);
                Log.d(TAG, Phone);
                Log.d(TAG, Name);
                Log.d(TAG, ProfileImage);
                Log.d(TAG, uidUser);


                toolbar.setTitle(Name);

                toolbar.setTitleTextColor(Color.WHITE);

                Picasso.with(ProfileActivity.this)
                        .load(ProfileImage)
                        .placeholder(R.mipmap.profilesp)
                        .resize(400, 400)
                        .centerCrop()
                        .rotate(0)
                        .into(expandedImage, new Callback() {
                            @Override
                            public void onSuccess() {
                                //      Toast.makeText(getApplicationContext(), "Fetched image from internet", Toast.LENGTH_SHORT).show();

                            }
                            @Override
                            public void onError() {
                                //       Toast.makeText(getApplicationContext(), "An error occurred", Toast.LENGTH_SHORT).show();
                            }
                        });



               /* binding.Name.setText(Name);
                binding.Phone.setText(Phone);
                binding.ProfileImage.setText(ProfileImage);
                binding.Uid.setText(uidUser);
*/

            /*    new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        String nameTXT=  binding.Name.getText().toString();
                        String contactTXT= binding.Phone.getText().toString();
                        String ProfileUrl= binding.ProfileImage.getText().toString();
                        String userkey= binding.Uid.getText().toString();



                        Boolean checkinsertdata = null;


                        checkinsertdata = DB.insertuserdata(nameTXT, contactTXT, userkey, ProfileUrl);


                        if(checkinsertdata==true)
                            Toast.makeText(ProfileActivity.this, "New Entry Inserted", Toast.LENGTH_SHORT).show();
                        else
                            Toast.makeText(ProfileActivity.this, "New Entry Not Inserted", Toast.LENGTH_SHORT).show();









                    }
                }, 3000);
*/


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, databaseError.getMessage()); //Don't ignore errors!
            }
        };
        uidRef.addListenerForSingleValueEvent(valueEventListener);










    }




    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}