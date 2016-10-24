package com.example.jakeduncan.fitnesstracker;

/**
 * Created by jakeduncan on 10/24/16.
 */

public class Product {
    private String name;
    private Integer distanceWalked;

    public Product(String name, Integer walked) {
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
