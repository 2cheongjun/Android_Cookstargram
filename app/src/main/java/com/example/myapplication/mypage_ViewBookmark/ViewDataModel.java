package com.example.myapplication.mypage_ViewBookmark;

import java.io.Serializable;

public class ViewDataModel  implements Serializable {
    // 변수 선언
    private int postIdx;
    private String userID;
    private String imgPath;
    private String postText;

    private String postImg;
    // 하트
    private int heartCount;
    private boolean cb_heart;
    // 북마크
//    private String bookmark;
    private boolean cb_bookmark;



    public ViewDataModel(int postIdx, String userID, String postText, String imgPath, String postImg, int heartCount, boolean cb_heart, boolean cb_bookmark) {
        this.postIdx = postIdx;
        this.userID = userID;
        this.postText = postText;
        this.imgPath = imgPath;
        this.postImg = postImg;
        this.heartCount = heartCount;
        this.cb_heart = cb_heart;
        this.cb_bookmark = cb_bookmark;
    }

    // gettet,setter 메소드
    public int getPostIdx() {
        return postIdx;
    }

    public String getUserID() {
        return userID;
    }

    public String getImgPath() {
        return imgPath;
    }

    public String getPostImg() {
        return postImg;
    }

    public String getPostText() {
        return postText;
    }

    public int getHeartCount() {
        return heartCount;
    }

    public boolean isCb_heart() {
        return cb_heart;
    }

//    public String getBookmark() {
//        return bookmark;
//    }

    public void setCb_heart(boolean b) {
        this.cb_heart = b;
    }

    public boolean isCb_bookmark() {
        return cb_bookmark;
    }

    public void setCb_bookmark(boolean b) {
        this.cb_heart = b;
    }

    }


