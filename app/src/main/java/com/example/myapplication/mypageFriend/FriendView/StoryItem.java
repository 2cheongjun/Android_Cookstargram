package com.example.myapplication.mypageFriend.FriendView;

import java.io.Serializable;

public class StoryItem implements Serializable {
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
    private boolean cb_bookmark;
    private String replySum;
    private String otherUser;
    private String otherUserText;




    public StoryItem(int postIdx, String userID, String postText, String imgPath, String postImg, int heartCount, boolean cb_heart, boolean cb_bookmark,
                     String replySum, String otherUser, String otherUserText) {
        this.postIdx = postIdx;
        this.userID = userID;
        this.postText = postText;
        this.imgPath = imgPath;
        this.postImg = postImg;
        this.heartCount = heartCount;
        this.cb_heart = cb_heart;
        this.cb_bookmark = cb_bookmark;
        // 댓글 수
        this.replySum =replySum;
        // 다른유저,글
        this.otherUser=otherUser;
        this.otherUserText=otherUserText;
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

    // 하트 수
    public int getHeartCount() {
        return heartCount;
    }

    // 좋아요
    public void setCb_heart(boolean selected) {
        this.cb_heart = selected;
    }

    // 좋아요 눌림 여부
    public boolean isCb_heart() {
        return cb_heart;
    }

    // 북마크
    public void setCb_bookmark(boolean selected) {
        this.cb_bookmark = selected;
    }
    // 북마크 눌림 여부
    public boolean isCb_bookmark() {
        return cb_bookmark;
    }

    // 댓글 수
    public String getReplySum() {
        return replySum;
    }
    // 댓글단 유저1
    public String getOtherUser() {
        return otherUser;
    }
    // 댓글단 유저가 쓴댓글
    public String getOtherUserText() {
        return otherUserText;
    }



}


