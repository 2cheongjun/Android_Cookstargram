package com.example.myapplication.mypageFriend.FriendReply;


import java.io.Serializable;

public class StoryReplyItem implements Serializable {
    private String userID;
    private String postIdx;
    private String title;
    private String date;
    private int replyIdx;
    private String step;


    // 순서와 칼럼갯수에 유의한다. 순서가 다르면 다른 값에 들어갈수 있다.                                    // 들여쓰기
    public StoryReplyItem(String userID, String postIdx, String title, String date, int replyIdx, String step) {
        this.userID = userID;
        this.postIdx = postIdx;
        this.title = title;
        this.date = date;
        this.replyIdx = replyIdx;
        this.step = step;
    }


    public int getReplyIdx() {
        return replyIdx;
    }

    public String getUserID() {
        return userID;
    }

    public String getPostIdx() {
        return postIdx;
    }

    public String getTitle() {
        return title;
    }

    public String getDate() {
        return date;
    }

    public String getStep() {
        return step;
    }

}





