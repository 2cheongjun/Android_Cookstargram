package com.example.myapplication.Chat_test.ChatList;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

// 받는곳!!!!
public class roomInfo implements Serializable {

    @Expose
    @SerializedName("resultCode")
    private String resultCode;
    @Expose
    @SerializedName("chatComment")
    private ArrayList<chatComment> chatComment;

    // post/ chatList_select_message.php
    public class chatComment {

        @Expose
        @SerializedName("success")
        String success;
        @Expose
        @SerializedName("roomIdx")
        String roomIdx;
        @Expose
        @SerializedName("myID")
        String myID;
        @Expose
        @SerializedName("friendID")
        String friendID;
        @Expose
        @SerializedName("created")
        String created;
        @Expose
        @SerializedName("finalMessage")
        String finalMessage;
        @Expose
        @SerializedName("postImg")
        String postImg;
        @Expose
        @SerializedName("imgType")
        String imgType;
        @Expose
        @SerializedName("room")
        String room;

        public String getSuccess() {
            return success;
        }

        public String getRoomIdx() {
            return roomIdx;
        }

        public String getMyID() {
            return myID;
        }

        public String getFriendID() {
            return friendID;
        }

        public String getCreated() {
            return created;
        }

        public String getFinalMessage() {
            return finalMessage;
        }

        public String getPostImg() {
            return postImg;
        }
        public String getImgType() {
            return imgType;
        }
        public String getRoom() {
            return room;
        }

    }

    public boolean isResultCode() {
        return "true".equals(resultCode);
    }

    public ArrayList<roomInfo.chatComment> getChatComment() {
        return chatComment;
    }
}

