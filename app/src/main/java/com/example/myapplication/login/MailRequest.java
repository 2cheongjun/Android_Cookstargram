package com.example.myapplication.login;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

import static com.example.myapplication.login.Urls.ROOT_URL;

public class MailRequest extends StringRequest {

    // 서버 URL설정 (PHP파일 연동)
    final static private String URL = ROOT_URL+"/forgot.php";
    private Map<String,String> map;

    // php에 보낸뒤에 결과값을 가져온다.
    public MailRequest(String email, Response.Listener<String> listener){
        super(Method.POST, URL, listener, null);

        map = new HashMap<>();
        map.put("email", email);
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return map;
    }
}
