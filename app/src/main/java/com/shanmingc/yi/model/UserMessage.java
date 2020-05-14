package com.shanmingc.yi.model;

public class UserMessage {
    private String username;
    private String message;
    private long uid;

    public UserMessage(String username, String message, long uid) {
        this.username = username;
        this.message = message;
        this.uid = uid;
    }

    public UserMessage(String username, String message) {
    }

    public String getMessage() {
        return message;
    }

    public String getUsername() {
        return username;
    }

    public long getUid() {
        return uid;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
