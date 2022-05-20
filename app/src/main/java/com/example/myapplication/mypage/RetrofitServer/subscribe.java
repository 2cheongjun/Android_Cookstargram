package com.example.myapplication.mypage.RetrofitServer;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

// 받는부분
public class subscribe{
    @Expose
    @SerializedName("userID") private String userID;
    @Expose
    @SerializedName("title") private String title;
    @Expose
    @SerializedName("postIdx") private String postIdx;
    @Expose
    @SerializedName("date") private String date;
    @Expose
    @SerializedName("cb_subscribe") private Boolean cb_subscribe;
    @Expose
    @SerializedName("success") private Boolean success;
    @Expose
    @SerializedName("message") private String message;

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPostIdx() {
        return postIdx;
    }
    public void setPostIdx(String postIdx) {
        this.postIdx = postIdx;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void getSubscribe(boolean selected) {
        this.cb_subscribe =selected;
    }
    // 구독버튼 체크여부
    public boolean isCb_subscribe(){
        return cb_subscribe;
    }


}