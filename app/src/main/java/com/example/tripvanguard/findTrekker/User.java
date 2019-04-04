package com.example.tripvanguard.findTrekker;


import java.util.ArrayList;

public class User  {
    boolean help;

    public boolean isHelp() {
        return help;
    }

    public void setHelp(boolean help) {
        this.help = help;
    }

    public User(boolean help, String name, String email, String key, double lat, double lng) {

        this.help = help;
        this.name = name;
        this.email = email;
        this.key = key;
        this.lat = lat;
        this.lng = lng;
    }

    private String name;
    private String email;
    private String key ;
    private double lat,lng;

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }


    public User()
    {

    }


    public User(String name, String email) {
        this.name = name;
        this.email = email;
    }

   /* public User(String name, String email, String uid) {
        this.name = name;
        this.email = email;
        this.Uid = uid;
    }

    public String getUid() {

        return Uid;
    }

    public void setUid(String uid) {
        Uid = uid;
    }*/

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }



    public void setEmail(String email) {
        this.email = email;
    }
}
