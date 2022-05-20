package com.example.myapplication.mypageFriend.FriendView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.myapplication.App;
import com.example.myapplication.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.example.myapplication.Api.Api.URL_BOOKMARK;


// 프로필사진을 눌러 이동한 친구의 포스트 뷰(메인X)
public class StoryViewActivity extends AppCompatActivity implements StoryViewAdaptor.FriendBookMarkClickListener {

    // 리사이클러뷰
    RecyclerView recyclerView;
    // 어답터
    StoryViewAdaptor mStoryAdapter;
    // DataModel 데이터
    public ArrayList<StoryItem> dataModels;

    // 볼리 통신
    private RequestQueue mRequestQueue;
    private com.android.volley.toolbox.JsonObjectRequest JsonObjectRequest;

    int getNum = 0;
    String userID = "";
    String postNum = "";
    ImageView iv_back;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story_view);

        // 인텐트로 받아온값
        int postIdx = 0;
        if (getIntent() != null) {
            Intent intent = getIntent();
            getNum = intent.getIntExtra("getNum", postIdx);
            postNum = getNum + "";
            // 받아온 번호 -> String으로 변환
//            String postNum = Integer.toString(getNum);

            Log.e("getNum", getNum + "");
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
        recyclerView.setAdapter(mStoryAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));

        // 북마크 조회 볼리서버 연결
        mRequestQueue = Volley.newRequestQueue(this);
        parseJson();
    }


    private void parseJson() {

        // Volley 로 회원양식 웹으로 전송 userID, postNum를 전송하고, 디비에 있는 이미지 가져오기..
        StoryViewRequest viewRequest = new StoryViewRequest(userID, postNum, responseListener);

        // 공용큐
        App.getInstance().getRequestQueue().add(viewRequest);
    }

    final Response.Listener<String> responseListener = new Response.Listener<String>() {
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
                            String imgPath = bookmarks.getString("imgPath");
                            // 유저가 작성한 이미지
                            String postImg = bookmarks.getString("postImg");

                            // 포스트 내용
                            String postText = bookmarks.getString("postText");

                            // 작성자의 글 셰어드에 저장하기
                            SharedPreferences pref = getSharedPreferences("myPostText", MODE_PRIVATE);
                            SharedPreferences.Editor editor = pref.edit();
                            editor.putString("myPostText", postText); //키값, 저장값
                            editor.apply();

                            // 하트버튼
                            boolean cb_heart = bookmarks.getBoolean("cb_heart");
                            // 총 하트 수
                            int heartCount = bookmarks.getInt("heartCount");

                            // 하트버튼
                            boolean cb_bookmark = bookmarks.getBoolean("cb_bookmark");

                            // 댓글 수
                            String replySum = bookmarks.getString("replySum");
                            // 다른유저
                            String otherUser = bookmarks.getString("otherUser");
                            // 다른유저가 쓴 댓글
                            String otherUserText = bookmarks.getString("otherUserText");

                            // 쿡아이템 arraylist에 추가
                            dataModels.add(new StoryItem(postIdx, userID, postText, imgPath, postImg, heartCount, cb_heart, cb_bookmark,
                                    replySum, otherUser, otherUserText));

                        }
                        // 메인으로 Intent값을 보냄 // 로그인값을 셰어드에 저장하고, 마이페이지에 불러오도록 하기
                        mStoryAdapter = new StoryViewAdaptor(StoryViewActivity.this, dataModels);

                        // 체크변화후 데이터 갱신
                        mStoryAdapter.notifyDataSetChanged();

                        // 북마크 리스너 설정
                        mStoryAdapter.setFriendBookMarkClickListener(StoryViewActivity.this);
                        recyclerView.setAdapter(mStoryAdapter);

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

//        dataModels.clear();
//        parseJson();

        // 셰어드 저장 값 가져오기 // 새로고침 실행하고
        SharedPreferences prefs = getSharedPreferences("VIEWUPDATE", MODE_PRIVATE);
        int viewUpdate = prefs.getInt("VIEWUPDATE", 0); //키값, 디폴트값

        if (viewUpdate > 0) {
            dataModels.clear();
            parseJson();
        }

        // 세어드 저장값 삭제
        SharedPreferences prefs1 = getSharedPreferences("VIEWUPDATE", MODE_PRIVATE);
        SharedPreferences.Editor editor1 = prefs1.edit();
        editor1.clear();
        editor1.commit();
//
    }


    @Override
    public void friendBookMarkClickListener(boolean isChecked, int postIdx) {
        initDialog();
        showpDialog();

//        Toast.makeText(CookTalkActivity.this, isChecked + ":" + postIdx, Toast.LENGTH_SHORT).show();
        // 셰어드에 저장된 userID 가져옴
        SharedPreferences prefsName = getSharedPreferences("NAME", MODE_PRIVATE);
        String userID = prefsName.getString("userID", "userID"); //키값, 디폴트값

        com.android.volley.toolbox.JsonObjectRequest request = null;

        try {
            JSONObject jsonObject = new JSONObject();
            // 사용자아이디를 보냄
            jsonObject.put("userID", userID);
            // 게시글 번호도 보냄
//            CookItem cookItem = mCookList.get(position);
            // 글번호
            jsonObject.put("postIdx", postIdx);

            // 좋아요 유무 true, false  // 좋아요를 isChecked이면 true를 보내고, Checked이면 false를 서버로 전송
            if (isChecked) {
                jsonObject.put("cb_bookmark", true);
                Log.e("lee", "북마크서버 체크 : " + isChecked);


            } else {
                jsonObject.put("cb_bookmark", false);
                Log.e("lee", "북마크서버 체크: " + isChecked);
            }


            request = new JsonObjectRequest(Request.Method.POST, URL_BOOKMARK, jsonObject,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            hidepDialog();
                            try {
                                String success = response.getString("success");

                                {
                                    String message = response.getString("message");
                                    Log.e("북마크", "북마크 서버응답받아와라" + message + success);
                                    Toast.makeText(StoryViewActivity.this, "북마크" + message, Toast.LENGTH_SHORT).show();
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    hidepDialog();
                    Toast.makeText(getApplicationContext(), "Image Upload Failed", Toast.LENGTH_SHORT).show();
                }
            }

            );
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // 공용 큐
        App.getInstance().getRequestQueue().add(request);

    }

    // 키보드 설정
    public class ExEditText extends androidx.appcompat.widget.AppCompatEditText {

        public ExEditText(Context a_context) {
            super(a_context);

        }

        public ExEditText(Context a_context, AttributeSet a_attributeSet) {
            super(a_context, a_attributeSet);

        }

        public boolean onKeyPreIme(int keyCode, KeyEvent event) {
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    return true; // 사용자가 override한 함수 사용
                }
            }
            return super.onKeyPreIme(keyCode, event); // 시스템 default 함수 사용
        }

    }

    public ProgressDialog pDialog;

    //다이얼로그 생성
    protected void initDialog() {

        pDialog = new ProgressDialog(this);
        pDialog.setMessage("loading");
        pDialog.setCancelable(true);
    }

    //다이얼로그 보여줄 때
    protected void showpDialog() {

        if (!pDialog.isShowing()) pDialog.show();
    }

    //다이얼로그 삭제
    protected void hidepDialog() {

        if (pDialog.isShowing()) pDialog.dismiss();
    }


}
