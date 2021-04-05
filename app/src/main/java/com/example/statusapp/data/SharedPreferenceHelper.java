package com.example.statusapp.data;

import android.content.Context;
import android.content.SharedPreferences;


import com.example.statusapp.Models.User1;

public class SharedPreferenceHelper {
    private static  com.example.statusapp.data.SharedPreferenceHelper instance = null;
    private static SharedPreferences preferences;
    private static SharedPreferences.Editor editor;
    private static String SHARE_USER_INFO = "userinfo";
    private static String SHARE_KEY_NAME = "name";
    private static String SHARE_KEY_PHONE = "Phone";
    private static String SHARE_KEY_AVATA = "avata";
    private static String SHARE_KEY_UID = "uid";



    private SharedPreferenceHelper() {}

    public static  com.example.statusapp.data.SharedPreferenceHelper getInstance(Context context) {
        if (instance == null) {
            instance = new  com.example.statusapp.data.SharedPreferenceHelper();
            preferences = context.getSharedPreferences(SHARE_USER_INFO, Context.MODE_PRIVATE);
            editor = preferences.edit();
        }
        return instance;
    }

    public void saveUserInfo(User1 user1) {
        editor.putString(SHARE_KEY_NAME, user1.name);
        editor.putString(SHARE_KEY_PHONE, user1.phone);
        editor.putString(SHARE_KEY_AVATA, user1.avata);
        editor.putString(SHARE_KEY_UID, com.example.statusapp.data.StaticConfig.UID);
        editor.apply();
    }

    public User1 getUserInfo(){
        String userName = preferences.getString(SHARE_KEY_NAME, "");
        String email = preferences.getString(SHARE_KEY_PHONE, "");
        String avatar = preferences.getString(SHARE_KEY_AVATA, "default");

        User1 user1 = new User1();
        user1.name = userName;
        user1.phone = email;
        user1.avata = avatar;

        return user1;
    }

    public String getUID(){
        return preferences.getString(SHARE_KEY_UID, "");
    }

}
