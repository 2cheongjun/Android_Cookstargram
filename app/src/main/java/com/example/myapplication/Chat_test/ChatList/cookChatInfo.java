package com.example.myapplication.Chat_test.ChatList;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

// 받는곳!!!! 채팅내용 받아오는곳
public class cookChatInfo implements Serializable {

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
        @SerializedName("chatRead")
        String chatRead;


        public String getChatRead() {
            return chatRead;
        }

        public String getIdx() {
            return idx;
        }

        @Expose
        @SerializedName("idx")
        private String idx;

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

    }

    public boolean isResultCode() {
        return "true".equals(resultCode);
    }

    public ArrayList<cookChatInfo.chatComment> getChatComment() {
        return chatComment;
    }
}

