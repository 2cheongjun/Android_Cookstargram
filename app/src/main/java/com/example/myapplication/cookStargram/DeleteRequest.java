package com.example.myapplication.cookStargram;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

import static com.example.myapplication.Api.Api.URL_COOK_POST_DELETE;

public class DeleteRequest extends StringRequest {
    private final static String URL = URL_COOK_POST_DELETE;
    private Map<String, String> map;

    public DeleteRequest(String postIdx, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(Method.POST, URL, listener, errorListener);

        map = new HashMap<>();
        map.put("postIdx",postIdx);

    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return map;
    }
}
