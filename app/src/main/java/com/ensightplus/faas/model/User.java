package com.ensightplus.faas.model;

import java.io.Serializable;

public class User implements Serializable {
    private String user;
    private String token;
    private String expires;
    private String refreshToken;
    private String fcmToken;

    public User(String user, String token, String expires, String refreshToken) {
        this.user = user;
        this.token = token;
        this.expires = expires;
        this.refreshToken = refreshToken;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getExpires() {
        return expires;
    }

    public void setExpires(String expires) {
        this.expires = expires;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getFcmToken() {
        return fcmToken;
    }

    public void setFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }

    @Override
    public String toString() {
        return "User{" +
                "user='" + user + '\'' +
                ", token='" + token + '\'' +
                ", expires='" + expires + '\'' +
                ", refreshToken='" + refreshToken + '\'' +
                ", fcmToken='" + fcmToken + '\'' +
                '}';
    }
}
