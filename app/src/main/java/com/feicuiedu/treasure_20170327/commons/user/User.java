package com.feicuiedu.treasure_20170327.commons.user;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 2017/3/30.
 */

public class User {


    /**
     * UserName : qjd
     * Password : 654321
     */

    @SerializedName("UserName")
    private String userName;
    @SerializedName("Password")
    private String password;

    public User(String userName, String password) {
        this.userName = userName;
        this.password = password;
    }
}
