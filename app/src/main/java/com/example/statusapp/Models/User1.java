package com.example.statusapp.Models;



public class User1 {
    public String name;
    public String phone;
    public String avata;
    public Status1 status1;
    public Message1 message1;


    public User1(){
        status1 = new Status1();
        message1 = new Message1();
        status1.isOnline = false;
        status1.timestamp = 0;
        message1.idReceiver = "0";
        message1.idSender = "0";
        message1.text = "";
        message1.timestamp = 0;
    }
}
