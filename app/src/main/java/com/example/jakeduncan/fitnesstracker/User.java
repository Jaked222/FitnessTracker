package com.example.jakeduncan.fitnesstracker;

/**
 * Created by jakeduncan on 10/24/16.
 */

public class User {
    private String name;
    private Integer price;

    public User(String name, Integer price) {
        this.name = name;
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public Integer getPrice() {
        return price;
    }
}
