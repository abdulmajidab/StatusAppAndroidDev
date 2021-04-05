package com.example.statusapp.Models;

import java.util.ArrayList;


public class ListFriend {
    private ArrayList<Friend1> listFriend;

    public ArrayList<Friend1> getListFriend() {
        return listFriend;
    }

    public ListFriend(){
        listFriend = new ArrayList<>();
    }

    public String getAvataById(String id){
        for(Friend1 friend1: listFriend){
            if(id.equals(friend1.id)){
                return friend1.avata;
            }
        }
        return "";
    }

    public void setListFriend(ArrayList<Friend1> listFriend) {
        this.listFriend = listFriend;
    }
}
