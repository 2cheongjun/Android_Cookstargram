package com.example.myapplication.cookStargram_reply;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

import static com.example.myapplication.Api.Api.URL_REPLY_DELETE;


public class ReplyDeleteRequest extends StringRequest {
    private final static String URL = URL_REPLY_DELETE;
    private Map<String, String> map;

    public ReplyDeleteRequest(String postNum,String replyIdx, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(Request.Method.POST, URL, listener, errorListener);

        map = new HashMap<>();
        map.put("postNum",postNum);
        map.put("replyIdx",replyIdx);
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return map;
    }
}
