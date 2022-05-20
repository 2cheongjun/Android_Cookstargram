package com.example.myapplication.mypage_bookmark;

public class DataModel {
    private String userID;
    private int postIdx;
    private String postImg;




    public DataModel(int postIdx, String userID, String postImg) {
        this.userID = userID;
        this.postIdx = postIdx;
        this.postImg = postImg;
    }

    public int getPostIdx() {
        return postIdx;
    }

    public String getUserID() {
        return userID;
    }

    public String getPostImg() {
        return postImg;
    }


    }


