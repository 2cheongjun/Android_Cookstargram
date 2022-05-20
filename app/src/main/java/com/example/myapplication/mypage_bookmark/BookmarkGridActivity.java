package com.example.myapplication.mypage_bookmark;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.multidex.MultiDex;
import androidx.multidex.MultiDexApplication;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.myapplication.R;
import com.example.myapplication.mypage_ViewBookmark.ViewActivity;
import com.google.gson.JsonArray;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class BookmarkGridActivity extends AppCompatActivity implements myRecyclerViewAdapter.OnBookItemClickListener {

    // 리사이클러뷰
    RecyclerView recyclerView;
    // 어답터
    myRecyclerViewAdapter adapter;
    // DataModel 데이터
    public ArrayList<DataModel> dataModels;

    // 볼리 통신
    private RequestQueue mRequestQueue;
    private com.android.volley.toolbox.JsonObjectRequest JsonObjectRequest;

    String userID = "";

    ImageView iv_back;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookmark_grid);

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
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));

        // 북마크 조회 볼리서버 연결
        mRequestQueue = Volley.newRequestQueue(this);
        dataModels.clear();
        selectBookmark();
    }


    private void selectBookmark() {

        // Volley 로 회원양식 웹으로 전송 userID를 전송하고, 디비에 있는 이미지 가져오기..
        BookmarkRequest bookmarkRequest = new BookmarkRequest(userID, responseListener);

        // 서버요청자 // 서버에 요청을 보내는 역할, 쓰레드를 자동으로 생성하여 서버에 요청을 보내고 응답을 받는다.
        RequestQueue queue = Volley.newRequestQueue(BookmarkGridActivity.this);
        queue.add(bookmarkRequest);
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

                            int postIdx = bookmarks.getInt("postIdx");

                            String postImg = bookmarks.getString("postImg");

                            Log.e("postImg", postImg);

                            // 쿡아이템 arraylist에 추가
                            dataModels.add(new DataModel(postIdx, userID, postImg));

                        }
                    }
                    // 메인으로 Intent값을 보냄 // 로그인값을 셰어드에 저장하고, 마이페이지에 불러오도록 하기
                    adapter = new myRecyclerViewAdapter(BookmarkGridActivity.this, dataModels);
                    recyclerView.setAdapter(adapter);
                    adapter.setOnBookItemClickListener(BookmarkGridActivity.this);
//                    adapter.notifyDataSetChanged();

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();


    }

    // 클릭시 상세 페이지로 이동
    @Override
    public void onBookItemClickListener(int position, int postIdx) {

        // 게시글번호 인텐트로, ViewActivity로 넘기기
        Intent intent = new Intent(BookmarkGridActivity.this, ViewActivity.class);
        intent.putExtra("getNum", postIdx);
        startActivity(intent);
    }
}


