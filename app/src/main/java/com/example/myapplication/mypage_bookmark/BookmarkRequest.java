package com.example.myapplication.mypage_bookmark;


import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

import static com.example.myapplication.Api.Api.URL_BOOKMARK_SELECT;

public class BookmarkRequest extends StringRequest {

    // 서버 URL설정 (PHP파일 연동)

    private Map<String, String> map;


    // php에 보낸뒤에 결과값을 가져온다.
    public BookmarkRequest(String userID, Response.Listener<String> profileResponseListener) {
        super(Method.POST, URL_BOOKMARK_SELECT, profileResponseListener, null);
        // 서버에 보낼 데이터를 해쉬맵을 사용해서 보낸다.
        map = new HashMap<>();
        map.put("userID", userID);

    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return map;
    }
}
