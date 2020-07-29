package com.example.night_out;

public class Posts {
    public String uid, time, date, postimage, caption, profileimage, username;

    public Posts(){

    }

    public Posts(String uid, String time, String date, String postimage, String caption, String profileimage, String username) {
        this.uid = uid;
        this.time = time;
        this.date = date;
        this.postimage = postimage;
        this.caption = caption;
        this.profileimage = profileimage;
        this.username = username;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getPostimage() {
        return postimage;
    }

    public void setPostimage(String postimage) {
        this.postimage = postimage;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getProfileimage() {
        return profileimage;
    }

    public void setProfileimage(String profileimage) {
        this.profileimage = profileimage;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}

