package com.example.myapplication.Chat_test.ChatList;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

// 채팅마지막 카운트번호 가져오는 곳
public class ChatCount implements Serializable {

    @Expose
    @SerializedName("target_ID")
    private String target_ID;
    @Expose
    @SerializedName("postImg")
    private String postImg;
    @Expose
    @SerializedName("success")
    private Boolean success;
    @Expose
    @SerializedName("message")
    private String message;
    @Expose
    @SerializedName("comment")
    private String comment;
    @Expose
    @SerializedName("created")
    private String created;
    @Expose
    @SerializedName("finalMessage")
    private String finalMessage;
    @Expose
    @SerializedName("friendID")
    private String friendID;
    @Expose
    @SerializedName("myID")
    private String myID;
    @Expose
    @SerializedName("roomIdx")
    private String roomIdx;


    public String getTarget_ID() {
        return target_ID;
    }

    public String getPostImg() {
        return postImg;
    }

    public Boolean getSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public String getComment() {
        return comment;
    }

    public String getCreated() {
        return created;
    }

    public String getFinalMessage() {
        return finalMessage;
    }

    public String getFriendID() {
        return friendID;
    }

    public String getMyID() {
        return myID;
    }

    public String getRoomIdx() {
        return roomIdx;
    }

}
