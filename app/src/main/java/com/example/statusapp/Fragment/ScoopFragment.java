package com.example.statusapp.Fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.statusapp.Activities.AddStatusActivity;
import com.example.statusapp.Adapters.StatusAdapter;
import com.example.statusapp.Models.Status;
import com.example.statusapp.Models.User;
import com.example.statusapp.Models.UserStatus;
import com.example.statusapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import omari.hamza.storyview.StoryView;
import omari.hamza.storyview.callback.StoryClickListeners;
import omari.hamza.storyview.model.MyStory;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ScoopFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ScoopFragment extends Fragment {

    StatusAdapter statusAdapter;
    ArrayList<UserStatus> userStatuses;
    FirebaseDatabase database;
    ProgressDialog dialog;
    User user;

    RecyclerView statusList;

    ImageView editImageBtn,profile_image;




    public ScoopFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ScoopFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ScoopFragment newInstance(String param1, String param2) {
        ScoopFragment fragment = new ScoopFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment

        View view =  inflater.inflate(R.layout.fragment_scoop, container, false);

        statusList=view.findViewById(R.id.statusList);
        editImageBtn=view.findViewById(R.id.editImageBtn);
        profile_image=view.findViewById(R.id.profile_image);





        database = FirebaseDatabase.getInstance();



        database.getReference().child("users").child(FirebaseAuth.getInstance().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        user = snapshot.getValue(User.class);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

      /*  String name = getIntent().getStringExtra("name");
        String profile = getIntent().getStringExtra("image");

        bindings.Mystatus.setText(name);
        Glide.with(com.example.statusapp.Activities.AddStatusActivity.this).load(profile)
                .placeholder(R.mipmap.profilesp)
                .into(bindings.profileImage);*/


        userStatuses = new ArrayList<>();
        statusAdapter = new StatusAdapter(getContext(), userStatuses);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        statusList.setLayoutManager(layoutManager);
        statusList.setAdapter(statusAdapter);



        database.getReference().child("stories").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    userStatuses.clear();
                    for(DataSnapshot storySnapshot : snapshot.getChildren()) {
                        UserStatus status = new UserStatus();
                        status.setName(storySnapshot.child("name").getValue(String.class));
                        status.setProfileImage(storySnapshot.child("profileImage").getValue(String.class));
                        status.setLastUpdated(storySnapshot.child("lastUpdated").getValue(Long.class));



                        ArrayList<Status> statuses = new ArrayList<>();

                        for(DataSnapshot statusSnapshot : storySnapshot.child("statuses").getChildren()) {
                            Status sampleStatus = statusSnapshot.getValue(Status.class);
                            statuses.add(sampleStatus);
                        }

                        status.setStatuses(statuses);






                        userStatuses.add(status);
                    }

                    statusAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });





       editImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, 75);
            }
        });

        profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                UserStatus userStatus = userStatuses.get(0);
                ArrayList<MyStory> myStories = new ArrayList<>();
                for(Status status : userStatus.getStatuses()) {
                    myStories.add(new MyStory(status.getImageUrl()));
                }




                //  String str = String.valueOf(lastStatus.getTimeStamp());

                new StoryView.Builder(((getActivity())).getSupportFragmentManager())
                        .setStoriesList(myStories) // Required
                        .setStoryDuration(5000) // Default is 2000 Millis (2 Seconds)
                        .setTitleText(userStatus.getName()) // Default is Hidden
                        .setSubtitleText("") // Default is Hidden
                        .setTitleLogoUrl(userStatus.getProfileImage()) // Default is Hidden
                        .setStoryClickListeners(new StoryClickListeners() {
                            @Override
                            public void onDescriptionClickListener(int position) {
                                //your action
                            }

                            @Override
                            public void onTitleIconClickListener(int position) {
                                //your action
                            }
                        }) // Optional Listeners
                        .build() // Must be called before calling show method
                        .show();



            }
        });






        return view;
    }
}