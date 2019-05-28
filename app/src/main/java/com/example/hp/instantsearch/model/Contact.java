package com.example.hp.instantsearch.model;

import com.google.gson.annotations.SerializedName;

public class Contact {
    String name;

    @SerializedName("image")
    String profileImage;
    String phone;
    String email;

    public String getName() {
        return name;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }
}
