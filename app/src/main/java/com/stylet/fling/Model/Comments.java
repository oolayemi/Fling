package com.stylet.fling.Model;

import com.google.firebase.database.ServerValue;

public class Comments {

    private String user_email;
    private String commentKey;
    private String message;
    private String blog_id;
    private String user_id;
    private String name;
    private String image;
    private Object timestamp;

    public Comments(){

    }

    public Comments(String message, String user_id, String blog_id, String name,  String image, String user_email) {
        this.blog_id = blog_id;
        this.message = message;
        this.user_id = user_id;
        this.timestamp = ServerValue.TIMESTAMP;
        this.name = name;
        this.image = image;
        this.user_email = user_email;
    }

    public String getUser_email() {
        return user_email;
    }

    public void setUser_email(String user_email) {
        this.user_email = user_email;
    }

    public String getCommentKey() {
        return commentKey;
    }

    public void setCommentKey(String commentKey) {
        this.commentKey = commentKey;
    }

    public String getBlog_id() {
        return blog_id;
    }

    public void setBlog_id(String blog_id) {
        this.blog_id = blog_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public Object getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Object timestamp) {
        this.timestamp = timestamp;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}