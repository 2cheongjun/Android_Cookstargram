package com.example.myapplication.mypageFriend.FriendView;


import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

import static com.example.myapplication.Api.Api.URL_BOOKMARK_SELECT_VIEW;

public class StoryViewRequest extends StringRequest {

    // 서버 URL설정 (PHP파일 연동)

    private Map<String,String> map;


    // php에 보낸뒤에 결과값을 가져온다.
    public StoryViewRequest(String userID, String postNum, Response.Listener<String> ViewResponseListener) {
        super(Method.POST, URL_BOOKMARK_SELECT_VIEW, ViewResponseListener, null);
        // 서버에 보낼 데이터를 해쉬맵을 사용해서 보낸다.
        map = new HashMap<>();
        map.put("userID", userID);
        map.put("postNum", postNum);
    }


    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return map;
    }
}
