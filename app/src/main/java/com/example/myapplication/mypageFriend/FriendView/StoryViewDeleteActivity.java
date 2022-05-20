package com.example.myapplication.mypageFriend.FriendView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.example.myapplication.R;
import com.example.myapplication.cookStargram.CookItem;
import com.example.myapplication.cookStargram.DeleteRequest;

import org.json.JSONException;
import org.json.JSONObject;

public class StoryViewDeleteActivity extends AppCompatActivity {

    TextView tv_notice;
    Button btn_delete, btn_cancel;
    private CookItem cookItem = null;
    private static final int RESULT_OKK = 1;
    // 인텐트 값을 가져오기 위한 구분자
    private String actionType = "";
    private StoryItem datalist2 = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete);

        tv_notice = findViewById(R.id.tv_notice);
        btn_delete = findViewById(R.id.btn_delete);
        btn_cancel = findViewById(R.id.btn_cancel);

        // TODO : Cookadapter에서 받아온 인텐트
        if (getIntent() != null) {
            actionType = getIntent().getStringExtra("actionType");
            datalist2 = (StoryItem)getIntent().getSerializableExtra("data");
        }

        // 취소 버튼
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // 포스트 삭제버튼
        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent2 = new Intent();
                // 응답코드, intent
                setResult(RESULT_OKK, intent2);
                DeleteBookmark();
                finish();
            }
        });
    }// Oncreate끝


    private void DeleteBookmark() {

        String postIdx = String.valueOf(datalist2.getPostIdx());
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    boolean success = jsonObject.getBoolean("success");
                    String message = jsonObject.getString("message");

                    if (success) {  // 삭제완료
                        Toast.makeText(getApplicationContext(), "삭제 성공!" + message, Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(getApplicationContext(), "삭제 실패!" + message, Toast.LENGTH_SHORT).show();
                        return;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "삭제 에러발생!", Toast.LENGTH_SHORT).show();
                return;
            }
        };

        // Volley 로 회원양식 웹으로 전송
        DeleteRequest deleteRequest = new DeleteRequest(postIdx, responseListener, errorListener);
        deleteRequest.setShouldCache(false);

        // 쓰레드를 만들어 응답을 요청하는 큐
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        queue.add(deleteRequest);
    }


}

