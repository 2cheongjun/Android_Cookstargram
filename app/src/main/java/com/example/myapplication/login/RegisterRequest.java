package com.example.myapplication.login;

import androidx.annotation.Nullable;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

import static com.example.myapplication.login.Urls.REGISTER;

public class RegisterRequest extends StringRequest {

     // 서버 URL설정 (PHP파일 연동)
      final static private String URL = REGISTER;
      private Map<String,String> map;

      // 안드로이드로 입력창에서 받아와서, php로 전송
      public RegisterRequest(String userID, String userPassword, String userEmail, Response.Listener<String> listener){
          // POST방식으로 전송
          super(Method.POST, URL, listener, null);

          map = new HashMap<>();
          map.put("userID", userID);
          map.put("userPassword", userPassword);
          map.put("userEmail", userEmail);
          // Age는 인트형 이므로 + "" 해준다.
//          map.put("userAge", userAge + "");
      }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return map;
    }
}
