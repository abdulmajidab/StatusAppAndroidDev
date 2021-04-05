package com.example.statusapp.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.statusapp.DBHelper;
import com.example.statusapp.Fragment.CallsFragment;
import com.example.statusapp.Fragment.ChatFragment;
import com.example.statusapp.Fragment.ScoopFragment;
import com.example.statusapp.Models.User1;
import com.example.statusapp.data.SharedPreferenceHelper;
import com.example.statusapp.data.StaticConfig;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.example.statusapp.Adapters.TopStatusAdapter;
import com.example.statusapp.Adapters.UsersAdapter;
import com.example.statusapp.Models.Status;
import com.example.statusapp.Models.User;
import com.example.statusapp.Models.UserStatus;
import com.example.statusapp.R;

import com.yarolegovich.lovelydialog.LovelyProgressDialog;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;


public class MainActivity extends AppCompatActivity {

  com.example.statusapp.databinding.ActivityMainBinding binding;
    FirebaseDatabase database;
    ArrayList<User> users;
    UsersAdapter usersAdapter;
    TopStatusAdapter statusAdapter;
    ArrayList<UserStatus> userStatuses;
    ProgressDialog dialog;


    private LovelyProgressDialog waitingDialog;

    User user;

    ViewPager pager;
    PagerAdapter adapter;


    DBHelper DB;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = com.example.statusapp.databinding.ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());





        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());



        viewPagerAdapter.addFragment( new ChatFragment(), "Chats");


        viewPagerAdapter.addFragment(new ScoopFragment(), "Scoop");
        viewPagerAdapter.addFragment(new CallsFragment(), "Calls");


        binding.pager.setAdapter(viewPagerAdapter);

        pager = findViewById(R.id.pager);

        binding.tablayout.setupWithViewPager(pager);



        dialog = new ProgressDialog(this);
        dialog.setMessage("Uploading Image...");
        dialog.setCancelable(false);

        database = FirebaseDatabase.getInstance();
        users = new ArrayList<>();
        userStatuses = new ArrayList<>();

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


        usersAdapter = new UsersAdapter(this, users);
        statusAdapter = new TopStatusAdapter(this, userStatuses);

//      binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(RecyclerView.HORIZONTAL);
        binding.statusList.setLayoutManager(layoutManager);
        binding.statusList.setAdapter(statusAdapter);

        binding.recyclerView.setAdapter(usersAdapter);

        binding.recyclerView.showShimmerAdapter();
        binding.statusList.showShimmerAdapter();

        database.getReference().child("users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                users.clear();
                for(DataSnapshot snapshot1 : snapshot.getChildren()) {
                    User user = snapshot1.getValue(User.class);
                    if(!user.getUid().equals(FirebaseAuth.getInstance().getUid()))

                      // saveUserInfo();

                        users.add(user);

                }
                binding.recyclerView.hideShimmerAdapter();
                usersAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

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
                    binding.statusList.hideShimmerAdapter();
                    statusAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        binding.bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.status:
                        Intent intent = new Intent();
                        intent.setType("image/*");
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(intent, 75);
                        break;
                }
                return false;
            }
        });


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





            /*    new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        binding.Name.setText(Name);
                        binding.Phone.setText(Phone);
                        binding.ProfileImage.setText(ProfileImage);
                        binding.Uid.setText(uidUser);


                        Boolean checkinsertdata = null;

                        String nameTXT=  binding.Name.getText().toString();
                        String contactTXT= binding.Phone.getText().toString();
                        String ProfileUrl= binding.ProfileImage.getText().toString();
                        String userkey= binding.Uid.getText().toString();

                        checkinsertdata = DB.insertuserdata(nameTXT, contactTXT, ProfileUrl, userkey);


                        if(checkinsertdata==true)
                            Toast.makeText(MainActivity.this, "New Entry Inserted", Toast.LENGTH_SHORT).show();
                        else
                            Toast.makeText(MainActivity.this, "New Entry Not Inserted", Toast.LENGTH_SHORT).show();


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
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(data != null) {
            if(data.getData() != null) {
                dialog.show();
                FirebaseStorage storage = FirebaseStorage.getInstance();
                Date date = new Date();
                StorageReference reference = storage.getReference().child("status").child(date.getTime() + "");

                reference.putFile(data.getData()).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if(task.isSuccessful()) {
                            reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    UserStatus userStatus = new UserStatus();
                                    userStatus.setName(user.getName());
                                    userStatus.setProfileImage(user.getProfileImage());
                                    userStatus.setLastUpdated(date.getTime());

                                    HashMap<String, Object> obj = new HashMap<>();
                                    obj.put("name", userStatus.getName());
                                    obj.put("profileImage", userStatus.getProfileImage());
                                    obj.put("lastUpdated", userStatus.getLastUpdated());

                                    String imageUrl = uri.toString();
                                    Status status = new Status(imageUrl, userStatus.getLastUpdated());

                                    database.getReference()
                                            .child("stories")
                                            .child(FirebaseAuth.getInstance().getUid())
                                            .updateChildren(obj);

                                    database.getReference().child("stories")
                                            .child(FirebaseAuth.getInstance().getUid())
                                            .child("statuses")
                                            .push()
                                            .setValue(status);

                                    dialog.dismiss();
                                }
                            });
                        }
                    }
                });
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        String currentId = FirebaseAuth.getInstance().getUid();
        database.getReference().child("presence").child(currentId).setValue("Online");
    }

    @Override
    protected void onPause() {
        super.onPause();
        String currentId = FirebaseAuth.getInstance().getUid();
        database.getReference().child("presence").child(currentId).setValue("Offline");
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.group:
                startActivity(new Intent(com.example.statusapp.Activities.MainActivity.this, com.example.statusapp.Activities.GroupChatActivity.class));
                break;
            case R.id.search:
                Toast.makeText(this, "Search clicked.", Toast.LENGTH_SHORT).show();
                break;
            case R.id.newgroup:
                startActivity(new Intent(com.example.statusapp.Activities.MainActivity.this, com.example.statusapp.Activities.AddGroupActivity.class));
                break;


            case R.id.settings:
                startActivity(new Intent(com.example.statusapp.Activities.MainActivity.this, com.example.statusapp.Activities.AddStatusActivity.class));
                Toast.makeText(this, "Settings Clicked.", Toast.LENGTH_SHORT).show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.topmenu, menu);
        return super.onCreateOptionsMenu(menu);
    }


/*
    void saveUserInfo() {
        database.getReference().child("users").child(FirebaseAuth.getInstance().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                waitingDialog.dismiss();
                HashMap hashUser = (HashMap) dataSnapshot.getValue();
                User1 userInfo = new User1();
                userInfo.name = (String) hashUser.get("name");
                userInfo.phone = (String) hashUser.get("phone");
                userInfo.avata = (String) hashUser.get("avata");
                SharedPreferenceHelper.getInstance(MainActivity.this).saveUserInfo(userInfo);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    *//**
     * Khoi tao thong tin mac dinh cho tai khoan moi
     *//*
    void initNewUserInfo() {
        User1 newUser = new User1();
        newUser.phone = user.getPhoneNumber();
        newUser.name = user.getPhoneNumber().substring(0, user.getPhoneNumber().indexOf("+"));
        newUser.avata = StaticConfig.STR_DEFAULT_BASE64;
        FirebaseDatabase.getInstance().getReference().child("user/" + user.getUid()).setValue(newUser);
    }*/




    class ViewPagerAdapter extends FragmentPagerAdapter {

        private ArrayList<Fragment> fragments;
        private ArrayList<String> titles;

        ViewPagerAdapter(FragmentManager fm){
            super(fm);
            this.fragments = new ArrayList<>();
            this.titles = new ArrayList<>();
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        public void addFragment(Fragment fragment, String title){
            fragments.add(fragment);
            titles.add(title);
        }

        // Ctrl + O

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return titles.get(position);
        }
    }
















}






