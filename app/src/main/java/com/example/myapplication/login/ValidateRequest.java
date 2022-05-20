package com.example.myapplication.login;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

import static com.example.myapplication.login.Urls.User_Validate;

public class ValidateRequest extends StringRequest {

     // 서버 URL설정 (PHP파일 연동)
      final static private String URL = User_Validate;
      private Map<String,String> map;

      public ValidateRequest(String userID,  Response.Listener<String> listener){
          super(Method.POST, URL, listener, null);

          map = new HashMap<>();
          map.put("userID", userID);
      }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return map;
    }
}
