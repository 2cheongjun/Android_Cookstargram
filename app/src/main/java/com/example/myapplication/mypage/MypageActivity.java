package com.example.myapplication.mypage;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.myapplication.Api.ProfileRequest;
import com.example.myapplication.Chat_test.chatRoomActivity;
import com.example.myapplication.Chat_test.ChatList.chatListActivity;
import com.example.myapplication.Home.HomeActivity;
import com.example.myapplication.R;
import com.example.myapplication.Recipe_select.subscribeActivity;
import com.example.myapplication.cookStargram.CookTalkActivity;
import com.example.myapplication.login.LoginActivity;
import com.example.myapplication.mypage.RetrofitServer.ApiClient;
import com.example.myapplication.mypage.RetrofitServer.ApiInterface;
import com.example.myapplication.mypage.RetrofitServer.Count;
import com.example.myapplication.mypage_bookmark.BookmarkGridActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;

import static com.example.myapplication.Api.Api.BASE_URL;

public class MypageActivity extends AppCompatActivity {

    private static final String TAG = "레트로핏";
    // 뒤로가기버튼
    private ImageView iv_back;
    private ImageView iv_setting;
    private ImageView ic_profile_default;
    // 수정
    private ImageView iv_edit;

    private TextView tv_name, tv_setting, tv_mypostCount,tv_subsCount,tv_friendCount;
    private ImageView iv_arrow_right;

    private TextView tv_bookmark_all;

    private Button btn_chat;

    private com.android.volley.toolbox.JsonObjectRequest JsonObjectRequest;
    RequestQueue queue;

    Bitmap bitmap;
    String count = null;

    // 진행바
    ProgressDialog progressDialog;
    private Object View;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mypage);

        btn_chat = findViewById(R.id.btn_chat);
        btn_chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(android.view.View v) {
                Intent intent = new Intent(MypageActivity.this, chatRoomActivity.class);
                 startActivity(intent);
            }
        });

        // 바텀네비
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.mypage);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull @NotNull MenuItem menuItem) {

                switch (menuItem.getItemId()) {

                    case R.id.recipe:
                        startActivity(new Intent(getApplicationContext(), subscribeActivity.class));
                        overridePendingTransition(0, 0);
                        return true;

                    case R.id.talk:
                        startActivity(new Intent(getApplicationContext(), CookTalkActivity.class));
                        overridePendingTransition(0, 0);
                        return true;

                    case R.id.chatRoom:
                        startActivity(new Intent(getApplicationContext(), chatListActivity.class));
                        overridePendingTransition(0, 0);
                        return true;

                    case R.id.mypage:
//                        startActivity(new Intent(getApplicationContext(), MypageActivity.class));
//                        overridePendingTransition(0, 0);
                        return true;
                }
                return false;
            }
        });


        // 뒤로가기버튼
        iv_back = findViewById(R.id.iv_back);
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        // 로그인한 아이디값 불러오기
        tv_name = findViewById(R.id.tv_name);
        SharedPreferences prefsName = getSharedPreferences("NAME", MODE_PRIVATE);
        String userID = prefsName.getString("userID", "userID"); //키값, 디폴트값
        tv_name.setText(userID);

        // 나의 게시글수,구독자수, 내가 구독하는사람수
        tv_mypostCount =findViewById(R.id.tv_mypostCount);
        tv_friendCount=findViewById(R.id.tv_friendCount);
        tv_subsCount=findViewById(R.id.tv_subsCount);

        // 레트로핏
        getPostCount(userID);

        // 로그인 정보가 없을때
        if(userID == null || userID.equals("userID")){
            tv_name.setText("로그인이 필요합니다.");
            tv_name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(android.view.View v) {
                    Intent intent = new Intent(MypageActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
            });
        }



        // getSharedPreferences는 Context의 객체의 메소드 이므로, Fragment에서 자동하지 않는다. 그러므로 + getActivity()를 사용한다.

        // 연필클릭시 프로필사진수정
        iv_edit = findViewById(R.id.iv_edit);
        iv_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 카메라액티비티로 이동
                Intent intent = new Intent(MypageActivity.this, TakeProfileActivity.class);
                startActivity(intent);
            }
        });


        // 프로필 사진
        ic_profile_default = (ImageView) findViewById(R.id.ic_profile_default);
        ic_profile_default.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 서버에 요청을 통해 받아온 데이터
                selectProfile();
            }
        });


        // 이름 옆 화살표 클릭 > 개인정보수정으로 이동
        iv_arrow_right = findViewById(R.id.iv_arrow_right);
        iv_arrow_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MypageActivity.this, InfoModifyActivity.class);
                startActivity(intent);
            }
        });


        // 북마크
        tv_bookmark_all = findViewById(R.id.tv_bookmark_all);
        tv_bookmark_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MypageActivity.this, BookmarkGridActivity.class);
                startActivity(intent);
            }
        });

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("로딩중..");


    }


    // TODO 레트로핏
    private void getPostCount(String userID) {
        ApiInterface apiInterface = ApiClient.getApiClient().create(ApiInterface.class);

        //userID를 보내고, 게시글수를 받아온다. + 구독자 + 구독수 가져오기
        Call<Count> call = apiInterface.getCount(userID,"");
        call.enqueue(new Callback<Count>() {

            @Override
            public void onResponse(@NotNull Call<Count> call, retrofit2.@NotNull Response<Count> response) {
                if (response.isSuccessful() && response.body() != null) {

                    int postCount = response.body().getPostCount();
                    String friendCount = response.body().getFriendCount();
                    String subsCount = response.body().getSubsCount();

                    tv_mypostCount.setText(postCount+"");
                    tv_friendCount.setText(friendCount);
                    tv_subsCount.setText(subsCount);

//                    Toast.makeText(MypageActivity.this,postCount+""+friendCount+subsCount, Toast.LENGTH_SHORT).show();

                    Log.e("count", postCount+"");

                    Log.e(TAG, " 서버: " + response.body().getUserID());
                } else {

                }
            }

            @Override
            public void onFailure(@NonNull Call<Count> call, @NonNull Throwable t) {
                Log.e("getNameHobby()", "에러 : " + t.getMessage());
            }
        });
    }


    // 프로필 사진 이미지 가져오기
    private void selectProfile() {
        SharedPreferences prefsName = getSharedPreferences("NAME", MODE_PRIVATE);
        String userID = prefsName.getString("userID", "userID");

        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    // json오브젝트를 만들어서, response를 저장.
                    JSONObject jsonObject = new JSONObject(response);
                    // php에서 사용했던 $response["success"] = false; /success라는 변수를
                    // true값으로 만들어줌.
                    String success = jsonObject.getString("success");
                    // 로그인 성공

                    {

                        String postImg = jsonObject.getString("postImg");

                        Glide.with(MypageActivity.this)
                                .load(BASE_URL + "/upload/" + postImg)
                                .skipMemoryCache(true)
                                .override(1000, 1000)
                                .error(R.drawable.cooker_man)
                                .placeholder(R.drawable.cooker_man)
                                .centerCrop()
                                .circleCrop()
                                .skipMemoryCache(true)
                                .diskCacheStrategy(DiskCacheStrategy.NONE)
                                .into(ic_profile_default);

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        // Volley 로 회원양식 웹으로 전송 userID를 전송하고, 디비에 있는 이미지 가져오기..
        ProfileRequest profileRequest = new ProfileRequest(userID, responseListener);

        // 서버요청자 // 서버에 요청을 보내는 역할, 쓰레드를 자동으로 생성하여 서버에 요청을 보내고 응답을 받는다.
        if (queue == null) {
            RequestQueue queue = Volley.newRequestQueue(MypageActivity.this);
            queue.add(profileRequest);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        selectProfile();
    }

}


