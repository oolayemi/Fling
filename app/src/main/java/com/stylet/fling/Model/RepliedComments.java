package com.stylet.fling.Model;

import com.google.firebase.database.ServerValue;

public class RepliedComments {

    private String repliedCommentKey;
    private String message;
    private String user_id;
    private String name;
    private String image;
    private Object timestamp;

    public RepliedComments(){

    }



    public RepliedComments(String message, String user_id, String name, String image) {
        this.message = message;
        this.user_id = user_id;
        this.name = name;
        this.image = image;
        this.timestamp = ServerValue.TIMESTAMP;

    }

    public String getRepliedCommentKey() {
        return repliedCommentKey;
    }

    public void setRepliedCommentKey(String repliedCommentKey) {
        this.repliedCommentKey = repliedCommentKey;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
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

}
