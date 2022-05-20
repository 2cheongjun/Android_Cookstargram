package com.example.myapplication.cookStargram;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

import static com.example.myapplication.Api.Api.URL_COOK_POST_INSERT;
import static com.example.myapplication.Api.Api.URL_COOK_POST_UPDATE;

public class UpdateRequest extends StringRequest {


     // 서버 URL설정 (PHP파일 연동)
      private Map<String,String> map;


    // php에 보낸뒤에 결과값을 가져온다.
    public UpdateRequest(String userID, String postText, String postImg, Response.Listener<String> updateResponseListener) {
        super(Method.POST, URL_COOK_POST_UPDATE, updateResponseListener, null);
        // 서버에 보낼 데이터를 해쉬맵을 사용해서 보낸다.
        map = new HashMap<>();
        map.put("userID", userID);
        map.put("postText", postText);
        map.put("postImg",postImg);
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return map;
    }
}
