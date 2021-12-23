package org.example.model;

import com.google.gson.annotations.SerializedName;

public class Category {

    @SerializedName("name")
    private String name;

    @SerializedName("id")
    private int id;

    public void setName(String name) {
        this.name = name;
    }
}