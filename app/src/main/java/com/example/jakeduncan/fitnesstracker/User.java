package com.example.jakeduncan.fitnesstracker;

/**
 * Created by jakeduncan on 10/24/16.
 */

public class User {
    private String name;
    private Integer distanceWalked;

    public User(String name, Integer price) {
        this.name = name;
        this.distanceWalked = price;
    }

    public String getName() {
        return name;
    }

    public Integer getDistanceWalked() {
        return distanceWalked;
    }
}
