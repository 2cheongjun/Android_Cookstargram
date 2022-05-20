package com.example.myapplication.login;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

import static com.example.myapplication.login.Urls.User_Email_Validate;

public class ValidateEmailRequest extends StringRequest {


     // 서버 URL설정 (PHP파일 연동)
      final static private String URL = User_Email_Validate;

      private Map<String,String> map;

      public ValidateEmailRequest(String userEmail, Response.Listener<String> listener){
          super(Method.POST, URL, listener, null);

          map = new HashMap<>();
          map.put("userEmail", userEmail);
      }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return map;
    }
}
