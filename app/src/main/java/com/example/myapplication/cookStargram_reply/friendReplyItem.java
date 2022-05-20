package com.example.myapplication.cookStargram_reply;


import java.io.Serializable;

public class friendReplyItem implements Serializable {
    private String userID;
    private String postIdx;
    private String title;
    private String replyDate;
    private int replyIdx;
    private String imgPath;

    public void setUserID(String userID) {
        this.userID = userID;
    }


    public String getImgPath() {
        return imgPath;
    }

    public void setImgPath(String imgPath) {
        this.imgPath = imgPath;
    }

    // 순서와 칼럼갯수에 유의한다. 순서가 다르면 다른 값에 들어갈수 있다.                                               // 들여쓰기
    public friendReplyItem(String userID, String postIdx, String title, String replyDate, int replyIdx, String step, String imgPath) {
        this.userID = userID;
        this.postIdx = postIdx;
        this.title = title;
        this.replyDate = replyDate;
        this.replyIdx = replyIdx;
        this.step = step;
        this.imgPath = imgPath;

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

    public String getReplyDate() {
        return replyDate;
    }

    public String getStep() {
        return step;
    }

    private String step;

}





