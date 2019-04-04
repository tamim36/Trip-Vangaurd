package com.example.tripvanguard.findTrekker;


public class friend {
    private String uid,key;

    public friend(String uid, String key) {
        this.uid = uid;
        this.key = key;
    }

    public friend() {
    }

    public String getUidm() {
        return uid;
    }

    public void setUidm(String uidm) {
        this.uid = uidm;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
