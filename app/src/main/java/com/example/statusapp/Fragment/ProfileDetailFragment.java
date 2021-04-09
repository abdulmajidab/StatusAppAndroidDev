package com.example.statusapp.Fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.statusapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class ProfileDetailFragment extends Fragment {


    TextView textphone,textStatus;


    public ProfileDetailFragment() {

    }


    public static ProfileDetailFragment newInstance(String param1, String param2) {
        ProfileDetailFragment fragment = new ProfileDetailFragment();
        Bundle args = new Bundle();


        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_profile_detail, container, false);


        textphone=view.findViewById(R.id.textphone);

        textStatus=view.findViewById(R.id.textStatus);


        String sessionId = getActivity().getIntent().getStringExtra("EXTRA_SESSION_ID");



        String currentId = FirebaseAuth.getInstance().getUid();


        //  String uid = "WDIgIub9JwWGkrNKeIea6eHLG9A2"; // FirebaseAuth.getInstance().getCurrentUser().getUid();

        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference uidRef = rootRef.child("users").child(currentId);
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

                textphone.setText(Phone);



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




        return  view;
    }
}