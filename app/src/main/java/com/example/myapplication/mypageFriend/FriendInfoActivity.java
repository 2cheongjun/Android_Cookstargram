package com.example.myapplication.mypageFriend;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
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
import com.example.myapplication.Recipe_select.Note;
import com.example.myapplication.Recipe_select.subscribeActivity;
import com.example.myapplication.cookStargram.CookItem;
import com.example.myapplication.cookStargram.CookTalkActivity;
import com.example.myapplication.mypage.MypageActivity;
import com.example.myapplication.mypage.RetrofitServer.ApiClient;
import com.example.myapplication.mypage.RetrofitServer.ApiInterface;
import com.example.myapplication.mypage.RetrofitServer.Count;
import com.example.myapplication.mypage.RetrofitServer.MakeChatRoom;
import com.example.myapplication.mypage.RetrofitServer.subscribe;
import com.example.myapplication.mypageFriend.FriendView.StoryViewActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;

import static com.example.myapplication.Api.Api.BASE_URL;

public class FriendInfoActivity extends AppCompatActivity implements FriendInfoViewAdapter.OnStoryItemClickListener, CompoundButton.OnCheckedChangeListener {

    private static final String TAG = "FriendInfoActivity";
    // 뒤로가기버튼
    private ImageView iv_back;
    private ImageView ic_profile_default;
    // 수정
    private ImageView iv_edit;

    private TextView tv_name, tv_setting, tv_mypostCount, tv_friendCount, tv_subsCount;

    private CheckBox ch_btn_follow;

    private Button btn_chat;

    private com.android.volley.toolbox.JsonObjectRequest JsonObjectRequest;
    RequestQueue queue;

    Bitmap bitmap;
    String count = null;

    // 진행바
    ProgressDialog progressDialog;
    private Object View;

    // 인텐트 값을 가져오기 위한 구분자
    private String actionType = "";
    private String actionType2 = "";
    private CookItem cookItem = null;
    private Note note = null;

    // 리사이클러뷰
    private RecyclerView rv_friend;
    // 어답터
    private FriendInfoViewAdapter friendInfoViewAdapter;
    // cookitem 데이터
    ArrayList<FriendInfoModel> storyDataModels;

    // 볼리 통신
    private RequestQueue mRequestQueue;

    String userID;
    String myID;
    String friendID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend);


        // TODO : Cookadapter에서 받아온 인텐트 // 프사를 누를때 쿡어답터에서 넘겨줌
        if (getIntent() != null) {
            actionType = getIntent().getStringExtra("actionType");
            cookItem = (CookItem) getIntent().getSerializableExtra("cookItem");
        }

        //  subscribeAdapter에서 받아온 값.
        if (getIntent() != null) {
            actionType2 = getIntent().getStringExtra("actionType2");
            note = (Note) getIntent().getSerializableExtra("note");
        }


        // 로그인한 아이디값 가져오기
        SharedPreferences prefsName = getSharedPreferences("NAME", MODE_PRIVATE);
        myID = prefsName.getString("userID", "userID"); //키값, 디폴트값

        // 리사이클러뷰객체
        rv_friend = (RecyclerView) findViewById(R.id.rv_friend);
        // RecyclerView 내의 아이템의 높이가 모두 균일하다면 RecyclerView.setHasFixedSize(true) 를 사용하는 것이 좋다.
        // 이렇게 하면, 데이터가 업데이트 되어도 RecyclerView가 레이아웃을 요청하지 않고 뷰 스스로 invalidate 된다.
        rv_friend.setHasFixedSize(true);
        // 레이아웃으로 부터 리사이클러뷰 참조
        rv_friend.setLayoutManager(new GridLayoutManager(this, 3));
//        rv_list.setLayoutManager(new LinearLayoutManager(getActivity()));
        // 쿡리스트 배열
        storyDataModels = new ArrayList();


        // 조회 볼리서버 연결
        mRequestQueue = Volley.newRequestQueue(this);
//        storyDataModels.clear();
        selectProfileImg();


        // 바텀네비
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.mypage);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull @NotNull MenuItem menuItem) {

                switch (menuItem.getItemId()) {
//                    case R.id.live:
//                        startActivity(new Intent(getApplicationContext(), HomeActivity.class));
//                        overridePendingTransition(0, 0);
//                        return true;

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
                        startActivity(new Intent(getApplicationContext(), MypageActivity.class));
                        overridePendingTransition(0, 0);
                        return true;
                }
                return false;
            }
        });

        // 뒤로가기버튼
        iv_back = findViewById(R.id.iv_back);
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(android.view.View v) {
                finish();
            }
        });

        // 로그인한 아이디값 불러오기
        tv_name = findViewById(R.id.tv_name);

        // 친구프로필 아이디 불러오기

        if (actionType != null) {
            userID = cookItem.getUserID();
            Log.e(TAG, " 친구프사눌러서 넘어왔을때  /" + userID);
        } else {
            userID = note.getTarget_ID();
            Log.e(TAG, " 채팅리스트 눌러서 넘어 왔을때? 확인필요 /" + userID);
        }
        tv_name.setText(userID);

        /**
         *  채팅하기 버튼 ->  MainActivity 채팅 창으로이동하면서 친구의 아이디값을 채팅방으로 보냄
         */
        btn_chat = findViewById(R.id.btn_chat);
        btn_chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(android.view.View v) {
                Intent intent = new Intent(FriendInfoActivity.this, chatRoomActivity.class);

                // 친구마이페이지의 이름 저장하기
                SharedPreferences pref = getSharedPreferences("friendNAME", MODE_PRIVATE);
                SharedPreferences.Editor editor = pref.edit();
                editor.putString("friendNAME",userID); //키값, 저장값기
                editor.apply();

                startActivity(intent);

                Log.e(TAG, "///////////////////친구정보에서 ->> 채팅버튼누름 -->> 채팅시작");

                // TODO 레트로핏 채팅하기 버튼을 누르면, 채팅방리스트 테이블에 추가한다.
                String roomName = myID+"&"+userID;// 여기선 상대ID가 userID이다.

                makeChatRoom(myID,userID,roomName);
            }
        });


        // 나의 게시글수,구독자수, 내가 구독하는사람수
        tv_mypostCount = findViewById(R.id.tv_mypostCount);
        tv_friendCount = findViewById(R.id.tv_friendCount);
        tv_subsCount = findViewById(R.id.tv_subsCount);


        // 프로필 사진
        ic_profile_default = (ImageView) findViewById(R.id.ic_profile_default);
        ic_profile_default.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 서버에 요청을 통해 받아온 데이터
                selectProfile();
            }
        });

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("로딩중..");

        // 팔로잉버튼 초기화
//        ch_btn_follow.setOnCheckedChangeListener(null);


        // TODO 팔로우 버튼 // 체크박스
        ch_btn_follow = findViewById(R.id.ch_btn_follow);
        ch_btn_follow.setOnCheckedChangeListener(null);


        // 레트로핏 // 친구 아이디 넣기
        getPostCount();
    }


    // 친구들 이미지 사진불러오기
    private void selectProfileImg() {

        if (actionType != null) {
            userID = cookItem.getUserID();
        }
        if (actionType2 != null) {
            userID = note.getTarget_ID();
        }

        // Volley 로 회원양식 웹으로 전송 userID를 전송하고, 디비에 있는 이미지 가져오기..
        FriendInfoRequest storyRequest = new FriendInfoRequest(userID, responseListener);
        // 서버요청자 // 서버에 요청을 보내는 역할, 쓰레드를 자동으로 생성하여 서버에 요청을 보내고 응답을 받는다.
        RequestQueue queue = Volley.newRequestQueue(FriendInfoActivity.this);
        queue.add(storyRequest);
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

                            // 쿡아이템 arraylist에 추가
                            storyDataModels.add(new FriendInfoModel(postIdx, "", postImg));
//                            }
                        }
                    }
                    // 메인으로 Intent값을 보냄 // 로그인값을 셰어드에 저장하고, 마이페이지에 불러오도록 하기
                    friendInfoViewAdapter = new FriendInfoViewAdapter(FriendInfoActivity.this, storyDataModels);
                    rv_friend.setAdapter(friendInfoViewAdapter);
                    friendInfoViewAdapter.setOnStoryItemClickListener((FriendInfoViewAdapter.OnStoryItemClickListener) FriendInfoActivity.this);

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

    // TODO 프사아이디값을 보내고, 레트로핏 게시글수, 구독자수,내가 구독한 사람수를 받아온다.
    private void getPostCount() {
        ApiInterface apiInterface = ApiClient.getApiClient().create(ApiInterface.class);

        // 구독자에서 넘어왔다면,note값이 있다. 있다면, userID와, 노트에서 얻어온 타겟아이디를 보낸다.
        if (note != null) {
            //userID를 보내고, 게시글수를 받아온다. + 구독자 + 구독수 가져오기
            Call<Count> call = apiInterface.getCount(userID, note.getTarget_ID());
            call.enqueue(new Callback<Count>() {

                @Override
                public void onResponse(@NotNull Call<Count> call, retrofit2.@NotNull Response<Count> response) {
                    if (response.isSuccessful() && response.body() != null) {

                        Count result = response.body();

                        tv_mypostCount.setText(result.getPostCount() + "");
                        tv_friendCount.setText(result.getFriendCount());
                        tv_subsCount.setText(result.getSubsCount());
                        ch_btn_follow.setChecked("0".equals(result.getFollowState()) ? false : true);

                        if(ch_btn_follow.isChecked()){
                            ch_btn_follow.setText("팔로잉");
                        }else{
                            ch_btn_follow.setText("팔로잉 하기");
                        }
                        ch_btn_follow.setOnCheckedChangeListener(FriendInfoActivity.this);


                        Log.e(TAG, " 서버: " + response.body().getUserID());
                    } else {

                    }
                }

                @Override
                public void onFailure(@NonNull Call<Count> call, @NonNull Throwable t) {
                    Log.e("getNameHobby()", "에러 : " + t.getMessage());
                }
            });
        } else {
            // 프사 눌러서 들어왔을때
            Call<Count> call = apiInterface.getCount(userID,cookItem.getUserID());
            call.enqueue(new Callback<Count>() {

                @Override
                public void onResponse(@NotNull Call<Count> call, retrofit2.@NotNull Response<Count> response) {
                    if (response.isSuccessful() && response.body() != null) {

                        Count result = response.body();

                        tv_mypostCount.setText(result.getPostCount() + "");
                        tv_friendCount.setText(result.getFriendCount());
                        tv_subsCount.setText(result.getSubsCount());
                        ch_btn_follow.setChecked("0".equals(result.getFollowState()) ? false : true);

                        if(ch_btn_follow.isChecked()){
                            ch_btn_follow.setText("팔로잉");
                        }else{
                            ch_btn_follow.setText("팔로잉 하기");
                        }
                        ch_btn_follow.setOnCheckedChangeListener(FriendInfoActivity.this);


                    } else {

                    }
                }

                @Override
                public void onFailure(@NonNull Call<Count> call, @NonNull Throwable t) {
                    Log.e("getNameHobby()", "에러 : " + t.getMessage());
                }

            });
        }

    }

    // TODO 레트로핏으로 게시글작성자 userID insert후 / 구독한사람 게시글만 조회
    private void makeChatRoom(String myID, String userID, String roomName) {
        ApiInterface apiInterface = ApiClient.getApiClient().create(ApiInterface.class);


        Call<MakeChatRoom> call = apiInterface.makeChatRoom(myID, userID,roomName);
        call.enqueue(new Callback<MakeChatRoom>() {

            @Override
            public void onResponse(@NotNull Call<MakeChatRoom> call, retrofit2.@NotNull Response<MakeChatRoom> response) {
                if (response.isSuccessful() && response.body() != null) {

                    Boolean success = response.body().getSuccess();
                    if (success) {
//                        Snackbar.make(getWindow().getDecorView(), "채팅방생성", BaseTransientBottomBar.LENGTH_LONG).show();
                    } else {
//                        Snackbar.make(getWindow().getDecorView(), "이미있는 채팅방.", BaseTransientBottomBar.LENGTH_LONG).show();
                    }

                    Log.e(TAG, " 서버: " + response.body().getUserID());
                } else {


                }
            }

            @Override
            public void onFailure(@NonNull Call<MakeChatRoom> call, @NonNull Throwable t) {
                Log.e("getNameHobby()", "에러 : " + t.getMessage());
            }
        });
    }




    // TODO 레트로핏으로 게시글작성자 userID insert후 / 구독한사람 게시글만 조회
    private void getSubscribe(String myID, String userID, boolean check) {
        ApiInterface apiInterface = ApiClient.getApiClient().create(ApiInterface.class);

        Call<subscribe> call = apiInterface.getSubscribe(myID, userID, check);
        call.enqueue(new Callback<subscribe>() {

            @Override
            public void onResponse(@NotNull Call<subscribe> call, retrofit2.@NotNull Response<subscribe> response) {
                if (response.isSuccessful() && response.body() != null) {

                    Boolean success = response.body().getSuccess();
                    if (success) {
                        Snackbar.make(getWindow().getDecorView(), "구독 추가하였습니다.", BaseTransientBottomBar.LENGTH_LONG).show();
                    } else {
                        Snackbar.make(getWindow().getDecorView(), "구독 취소하였습니다.", BaseTransientBottomBar.LENGTH_LONG).show();
                    }

                    Log.e(TAG, " 서버: " + response.body().getUserID());
                } else {


                }
            }

            @Override
            public void onFailure(@NonNull Call<subscribe> call, @NonNull Throwable t) {
                Log.e("getNameHobby()", "에러 : " + t.getMessage());
            }
        });
    }


    //  Volley 프로필 사진 이미지 가져오기
    private void selectProfile() {

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

                        Glide.with(FriendInfoActivity.this)
                                .load(BASE_URL + "/upload/" + postImg)
                                .skipMemoryCache(true)
                                .override(200, 200)
                                .error(android.R.drawable.ic_menu_gallery)
                                .placeholder(android.R.drawable.ic_menu_gallery)
                                .centerCrop()
                                .circleCrop()
                                .skipMemoryCache(true)
                                .diskCacheStrategy(DiskCacheStrategy.NONE)
                                .into(ic_profile_default);


//                        Toast.makeText(getActivity(),"프사가져오기 성공.",Toast.LENGTH_SHORT).show();
                        // 메인으로 Intent값을 보냄 // 로그인값을 셰어드에 저장하고, 마이페이지에 불러오도록 하기

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
            RequestQueue queue = Volley.newRequestQueue(FriendInfoActivity.this);
            queue.add(profileRequest);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        selectProfile();
    }


    // 썸네일 누르면 상세글로 이동한다.
    @Override
    public void onStoryItemClickListener(int position, int postIdx) {
        // 게시글번호 인텐트로, ViewActivity로 넘기기
        Intent intent = new Intent(FriendInfoActivity.this, StoryViewActivity.class);
        intent.putExtra("getNum", postIdx);
        startActivity(intent);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

        // 레트로핏으로 구독하기 누르면, ID값을 보내고, 구독테이블에 저장됨

        // 구독하기ON
        if (isChecked) {
            //내아이디와, 구독하는 친구아이디 테이블에 넣기
            getSubscribe(myID, userID, true);
            ch_btn_follow.setText("팔로잉");
//                    Snackbar.make(getWindow().getDecorView(), "구독 추가하였습니다.", BaseTransientBottomBar.LENGTH_LONG).show();
        }
        // 구독하기OFF // 구독테이블에서 삭제
        else {
            getSubscribe(myID, userID, false);
            ch_btn_follow.setText("팔로잉 하기");
//                    Snackbar.make(getWindow().getDecorView(), "구독 취소하였습니다.", BaseTransientBottomBar.LENGTH_LONG).show();
        }
    }
}

