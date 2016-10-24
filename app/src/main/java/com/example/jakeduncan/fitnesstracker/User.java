package com.example.jakeduncan.fitnesstracker;

/**
 * Created by jakeduncan on 10/24/16.
 */

public class User {
    private String name;
    private Integer distanceWalked;
    private String password;

    public User(String name, Integer distanceWalked, String password) {
        this.name = name;
        this.distanceWalked = distanceWalked;
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public User(String name, Integer walked) {
        this.name = name;
        this.distanceWalked = walked;
    }

    public String getName() {
        return name;
    }

    public Integer getDistanceWalked() {
        return distanceWalked;
    }
}
