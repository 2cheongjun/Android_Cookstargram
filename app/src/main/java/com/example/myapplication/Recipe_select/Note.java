package com.example.myapplication.Recipe_select;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

// 받는곳!!!!
public class Note implements Serializable {
    // @Expose ??
    @Expose
    @SerializedName("btn_check")
    private String btn_check;
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

    public String getBtn_check() {
        return btn_check;
    }

    public void setBtn_check(String btn_check) {
        this.btn_check = btn_check;
    }

    public String getTarget_ID() {
        return target_ID;
    }

    public void setTarget_ID(String target_ID) {
        this.target_ID = target_ID;
    }

    public String getPostImg() {
        return postImg;
    }

    public void setPostImg(String postImg) {
        this.postImg = postImg;
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

}

