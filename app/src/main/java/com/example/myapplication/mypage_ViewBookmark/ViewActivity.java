package com.example.myapplication.mypage_ViewBookmark;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.multidex.MultiDex;
import androidx.multidex.MultiDexApplication;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.myapplication.App;
import com.example.myapplication.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class ViewActivity extends AppCompatActivity {

    // 리사이클러뷰
    RecyclerView recyclerView;
    // 어답터
    ViewAdaptor adapter;
    // DataModel 데이터
    public ArrayList<ViewDataModel> dataModels;

    // 볼리 통신
    private RequestQueue mRequestQueue;
    private com.android.volley.toolbox.JsonObjectRequest JsonObjectRequest;

    int getNum = 0;
    String userID = "";
    String postNum = "";
    ImageView iv_back;

    public static final int REQUEST_CODE_UPDATE = 102;
    private static final int RESULT_OKK = 100;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookmark_view);


        // 인텐트로 받아온값
        int postIdx = 0;
        if (getIntent() != null) {
            Intent intent = getIntent();
            getNum = intent.getIntExtra("getNum", postIdx);
            postNum = getNum+"";
            // 받아온 번호 -> String으로 변환
//            String postNum = Integer.toString(getNum);

            Log.e("getNum", getNum+"");
        }

        // 저장된 ID값 불러오기
        SharedPreferences prefsName = getSharedPreferences("NAME", MODE_PRIVATE);
        userID = prefsName.getString("userID", "userID");

        // 뒤로가기버튼
        iv_back = findViewById(R.id.iv_back);
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        //데이터 모델리스트
        dataModels = new ArrayList();

        recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));

        // 북마크 조회 볼리서버 연결
        mRequestQueue = Volley.newRequestQueue(this);
        selectBookmarkView();
    }


    private void selectBookmarkView() {

        // Volley 로 회원양식 웹으로 전송 userID, postNum를 전송하고, 디비에 있는 이미지 가져오기..
        ViewRequest viewRequest = new ViewRequest(userID,postNum, responseListener);

        // 서버요청자 // 서버에 요청을 보내는 역할, 쓰레드를 자동으로 생성하여 서버에 요청을 보내고 응답을 받는다.
        RequestQueue queue = Volley.newRequestQueue(ViewActivity.this);
        queue.add(viewRequest);
//        App.getInstance().getRequestQueue().add(request);
    }

    Response.Listener<String> responseListener = new Response.Listener<String>() {
        @Override
        public void onResponse(String response) {
            try {
                // json오브젝트를 만들어서, response를 저장.
                JSONObject jsonObject = new JSONObject(response);

                // 로그인 성공
                {
                    String check = jsonObject.getString("resultCode");
                    if ("true".equals(check)) {

                        JSONArray jsonArray = jsonObject.getJSONArray("bookmarks");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject bookmarks = jsonArray.getJSONObject(i);

                            //  포스트번호
                            int postIdx = bookmarks.getInt("postIdx");
                            // 유저명
                            String userID = bookmarks.getString("userID");

                            // 유저프로필 사진
                            String imgPath = "";//cookArticles.getString("imgPath");
                            // 유저가 작성한 이미지
                            String postImg = bookmarks.getString("postImg");

                            // 포스트 내용
                            String postText = bookmarks.getString("postText");

                            // 하트버튼
                            boolean cb_heart = bookmarks.getBoolean("cb_heart");
                            // 총 하트 수
                            int heartCount = bookmarks.getInt("heartCount");

                            // 하트버튼
                            boolean cb_bookmark = bookmarks.getBoolean("cb_bookmark");

                            // 쿡아이템 arraylist에 추가
                            dataModels.add(new ViewDataModel(postIdx, userID, postText, imgPath, postImg, heartCount, cb_heart, cb_bookmark));

                            // 메인으로 Intent값을 보냄 // 로그인값을 셰어드에 저장하고, 마이페이지에 불러오도록 하기
                            adapter = new ViewAdaptor(ViewActivity.this, dataModels);

                        }
                        recyclerView.setAdapter(adapter);

                    }

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    };

    @Override
    protected void onResume() {
        super.onResume();

        // 셰어드 저장 값 가져오기 // 새로고침 실행하고
        SharedPreferences prefs = getSharedPreferences("VIEWUPDATE", MODE_PRIVATE);
        int viewUpdate = prefs.getInt("VIEWUPDATE", 0); //키값, 디폴트값

        if (viewUpdate > 0) {

            dataModels.clear();
            selectBookmarkView();
        }

        // 세어드 저장값 삭제
        SharedPreferences prefs1 = getSharedPreferences("VIEWUPDATE", MODE_PRIVATE);
        SharedPreferences.Editor editor1 = prefs1.edit();
        editor1.clear();
        editor1.commit();
//
    }
}
