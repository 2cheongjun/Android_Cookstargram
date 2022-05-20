package com.example.myapplication.login;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;


// 문자열을 결과 값으로 가져오는 요청 정보.
public class LoginRequest extends StringRequest {

      // 요청을 보낼 url
      final static private String URL = Urls.LOGIN;
      private Map<String,String> map;

      // 응답을 받았을때 호출될 메소드 // 요청방식
      public LoginRequest(String userID, String userPassword, Response.Listener<String> listener){
          super(Method.POST, URL, listener, null);

          // 서버에 보낼 데이터를 해쉬맵을 사용해서 보낸다.
          map = new HashMap<>();
          map.put("userID", userID);
          map.put("userPassword", userPassword);
      }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return map;
    }
}
