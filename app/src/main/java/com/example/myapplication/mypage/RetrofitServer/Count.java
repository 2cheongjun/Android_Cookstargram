package com.example.myapplication.mypage.RetrofitServer;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


// 받는거 // 게시글수 , 구독자수, 나의 구독중인 수 가져오기
public class Count {

    @Expose
    @SerializedName("userID")
    private String userID;
    @Expose
    @SerializedName("postCount")
    private int postCount;
    @Expose
    @SerializedName("friendCount")
    private String friendCount;
    @Expose
    @SerializedName("subsCount")
    private String subsCount;
    @Expose
    @SerializedName("followState")
    private String followState;

    public String getFollowState() {
        return followState;
    }

    public int getPostCount() {
        return postCount;
    }

    public void setPostCount(int postCount) {
        this.postCount = postCount;
    }


    public String getFriendCount() {
        return friendCount;
    }

    public void setFriendCount(String friendCount) {
        this.friendCount = friendCount;
    }


    public String getSubsCount() {
        return subsCount;
    }

    public void setSubsCount(String subsCount) {
        this.subsCount = subsCount;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getUserID() {
        return userID;
    }

}
