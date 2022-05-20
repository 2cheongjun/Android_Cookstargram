package com.example.myapplication.cookStargram;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.myapplication.Api.NetworkStatus;
import com.example.myapplication.App;
import com.example.myapplication.Chat_test.ChatList.chatListActivity;
import com.example.myapplication.Chat_test.socketService;
import com.example.myapplication.Home.HomeActivity;
import com.example.myapplication.mypage.MypageActivity;
import com.example.myapplication.R;
import com.example.myapplication.Recipe_select.subscribeActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;

import static com.example.myapplication.Api.Api.URL_BOOKMARK;
import static com.example.myapplication.Api.Api.URL_COOK_POST_SEARCH;
import static com.example.myapplication.Api.Api.URL_COOK_POST_SELECT;

public class CookTalkActivity extends AppCompatActivity implements CookAdapter.BookMarkClickListener {


    // 리사이클러뷰
    private RecyclerView rv_list;
    // 어답터
    private CookAdapter mCookAdapter;
    // cookitem 데이터
    private ArrayList<CookItem> mCookList;

    // 볼리 통신
    private RequestQueue mRequestQueue;

    private ImageView iv_write;
    SwipeRefreshLayout swipeRefreshLayout;

    private String actionType = "";
    private CookItem cookItem = null;
    public static final int REQUEST_CODE_WRITE = 101;
    private static final int RESULT_OKK = 1;
    String messege = null;
    private boolean isViewShown = false;

    private EditText et_search;
    ArrayList<CookItem> filterList = null;

    private Menu mMenu;


    // 하트 체크
    boolean isChecked = false;
    JsonObjectRequest request;

    private boolean itemTouch;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cook);

        //TODO 백그라운드 서비스시작
        // 서비스 시작 MyService2로 인텐트를 보냄
        Intent intent = new Intent(getApplicationContext(), socketService.class);
        // intent.putExtra("", ""); 필요시 인텐트에 필요한 데이터를 담아준다
        startService(intent);


        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        //home
        bottomNavigationView.setSelectedItemId(R.id.talk);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull @NotNull MenuItem menuItem) {

                switch (menuItem.getItemId()) {
//                    case R.id.live:
//                        startActivity(new Intent(getApplicationContext(), HomeActivity.class));
//                        overridePendingTransition(0, 0);
//                        return true;

                    case R.id.chatRoom:
                        startActivity(new Intent(getApplicationContext(), chatListActivity.class));
                        overridePendingTransition(0, 0);
                        return true;

                    case R.id.talk:
                        startActivity(new Intent(getApplicationContext(), CookTalkActivity.class));
                        overridePendingTransition(0, 0);
                        return true;


                    case R.id.recipe:
                        startActivity(new Intent(getApplicationContext(), subscribeActivity.class));
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


        // 리사이클러뷰객체
        rv_list = (RecyclerView) findViewById(R.id.rv_list);
        // RecyclerView 내의 아이템의 높이가 모두 균일하다면 RecyclerView.setHasFixedSize(true) 를 사용하는 것이 좋다.
        // 이렇게 하면, 데이터가 업데이트 되어도 RecyclerView가 레이아웃을 요청하지 않고 뷰 스스로 invalidate 된다.
        rv_list.setHasFixedSize(true);
        // 레이아웃으로 부터 리사이클러뷰 참조
        rv_list.setLayoutManager(new LinearLayoutManager(this));
//        rv_list.setLayoutManager(new LinearLayoutManager(getActivity()));
        // 쿡리스트 배열
        mCookList = new ArrayList<>();
        // 스크롤리스너
        rv_list.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NotNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                int lastVisibleItemPosition = ((LinearLayoutManager) Objects.requireNonNull(rv_list.getLayoutManager())).findLastCompletelyVisibleItemPosition();
                int itemTotalCount = Objects.requireNonNull(rv_list.getAdapter()).getItemCount() - 1;

                if (lastVisibleItemPosition == itemTotalCount) {
//                    Toast.makeText(CookTalkActivity.this, "Last Position", Toast.LENGTH_SHORT).show();
                }
            }
        });


        // 리프레시
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(false);
//                assert getFragmentManager() != null;
//                getFragmentManager().beginTransaction().detach(Frag_talk.this).attach(Frag_talk.this).commit();
                parseJSON();
            }
        });


        // TODO 검색창
        // 상단 검색창 검색기능
        et_search = (EditText) findViewById(R.id.et_search);
//        et_search.requestFocus();

        et_search.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                switch (keyCode) {
                    case KeyEvent.KEYCODE_ENTER:
                        // 검색창에 단어를 쓰면, 그단어를 서버로 보내서,테이블을 조회한다. 그결과를 리턴받는다.
                        // 검색하면,새로운 액티 비티가 뜨면서, 검색결과 출력
                        // 썼던글 가져오기
                        String word = et_search.getText().toString().trim();
                        et_search.setText(word);
                        // 검색한 단어 서버로 보내기
                        search();
                        // 단어가 있을때에만 토스트 뜨게 // 스위치문 다시써보기

                        if (word != null) {
                            Toast.makeText(CookTalkActivity.this, word + " 검색", Toast.LENGTH_SHORT).show();
                            break;
                        }

                }
                return false; // true이면 지우기버튼이 안됨

            }
        });


        // 글쓰기 버튼
        iv_write = (ImageView)

                findViewById(R.id.iv_write);
        iv_write.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 글쓰기 액티비티 띄우고, 데이터 받아오기
                Intent intent = new Intent(CookTalkActivity.this, WriteActivity.class);
                startActivityForResult(intent, REQUEST_CODE_WRITE);
            }
        });


        // 네트워크 통신 설정
        int status = NetworkStatus.getConnectivityStatus(this);
        if (status == NetworkStatus.TYPE_MOBILE || status == NetworkStatus.TYPE_WIFI) {
        } else {
            Toast.makeText(this, "인터넷 연결을 확인해주세요.", Toast.LENGTH_SHORT).show();
        }


        // 볼리로 서버 요청응답받기 //////////////////////////////////////////////////////////////////////////
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(this);
            parseJSON();
        }

        // 백키 방지
        onBackPressed();

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OKK) {
            assert data != null;
            String name = data.getStringExtra("name");
            Log.e("", name);
        }

        parseJSON();

    }


    // 백키 잠금
    public void onBackPressed() {

    }


    // 서버 볼리 통신 조회
    private void parseJSON() {

        initDialog();
        showpDialog();

        SharedPreferences prefsName = CookTalkActivity.this.getSharedPreferences("NAME", MODE_PRIVATE);
        String userID = prefsName.getString("userID", "userID"); //키값, 디폴트값

        try {
            JSONObject jsonObject = new JSONObject();


            // 사용자아이디를 보냄
            jsonObject.put("userID", userID);
            // 서버 cookPost select 주소.php // 게시글 조회해서 가져오는 부분
            String getUrl = URL_COOK_POST_SELECT;

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, getUrl, jsonObject,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {

                            hidepDialog();

                            // 서버에서 요청을 통해 받아온 데이터
                            try {
                                String check = response.getString("resultCode");
                                if ("true".equals(check)) {
                                    // 정상처리
                                    // 서버에서 cookArticles라는 이름의 array를 가져와라.
                                    mCookList.clear();
                                    JSONArray jsonArray = response.getJSONArray("cookArticles");
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        // 제이슨 오브젝트 값 받아오기
                                        JSONObject cookArticles = jsonArray.getJSONObject(i);
                                        // 포스트번호
                                        int postIdx = cookArticles.getInt("postIdx");
                                        // 유저명
                                        String userID = cookArticles.getString("userID");

                                        // 유저프로필 사진
                                        String imgPath = cookArticles.getString("imgPath");
                                        // 유저가 작성한 이미지
                                        String postImg = cookArticles.getString("postImg");
                                        // 포스트 내용
                                        String postText = cookArticles.getString("postText");

                                        // 하트버튼
                                        boolean cb_heart = cookArticles.getBoolean("cb_heart");
                                        // 총 하트 수
                                        int heartCount = cookArticles.getInt("heartCount");
                                        // 하트버튼
                                        boolean cb_bookmark = cookArticles.getBoolean("cb_bookmark");
                                        // 댓글 수
                                        String replySum = cookArticles.getString("replySum");
                                        // 다른유저
                                        String otherUser = cookArticles.getString("otherUser");
                                        // 다른유저가 쓴 댓글
                                        String otherUserText = cookArticles.getString("otherUserText");

                                        // 쿡아이템 arraylist에 추가
                                        mCookList.add(new CookItem(postIdx, userID, postText, imgPath, postImg, heartCount, cb_heart, cb_bookmark,
                                                replySum, otherUser, otherUserText));
                                    }


                                    // 어답터에 연결하는 부분
                                    mCookAdapter = new CookAdapter(CookTalkActivity.this, mCookList);

                                    // 체크변화후 데이터 갱신
                                    mCookAdapter.notifyDataSetChanged();

                                    // 북마크 리스너 설정
                                    mCookAdapter.setBookMarkClickListener(CookTalkActivity.this);

                                    // 리사이클러뷰 객체에 어답터 장착
                                    rv_list.setAdapter(mCookAdapter);


                                } else {
                                    //Toast.make error
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();
                    hidepDialog();
                }
            });
            // 캐쉬하지 않고 매번 요청하도록 한다.
            // 서버에 요청
            request.setShouldCache(false);
//            mRequestQueue.add(request);
            // TODO 공통으로 만들어 둔 큐
            App.getInstance().getRequestQueue().add(request);

        } catch (Exception e) {
        }
    }


    // 서버 볼리 통신 조회
    private void search() {

        initDialog();
        showpDialog();

        SharedPreferences prefsName = CookTalkActivity.this.getSharedPreferences("NAME", MODE_PRIVATE);
        String userID = prefsName.getString("userID", "userID"); //키값, 디폴트값

        try {
            JSONObject jsonObject = new JSONObject();

            String word = et_search.getText().toString().trim();
            // 검색한 단어 서버로 보내기
            if (word != null) {
                jsonObject.put("word", word);
            }
            // 사용자아이디를 보냄
            jsonObject.put("userID", userID);
            // 서버 cookPost select 주소.php // 게시글 조회해서 가져오는 부분
            String getUrl = URL_COOK_POST_SEARCH;

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, getUrl, jsonObject,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {

                            hidepDialog();

                            // 서버에서 요청을 통해 받아온 데이터
                            try {
                                String check = response.getString("resultCode");
                                if ("true".equals(check)) {
                                    // 정상처리
                                    // 서버에서 cookArticles라는 이름의 array를 가져와라.
                                    mCookList.clear();
                                    JSONArray jsonArray = response.getJSONArray("cookArticles");
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        // 제이슨 오브젝트 값 받아오기
                                        JSONObject cookArticles = jsonArray.getJSONObject(i);
                                        // 포스트번호
                                        int postIdx = cookArticles.getInt("postIdx");
                                        // 유저명
                                        String userID = cookArticles.getString("userID");
                                        // 유저프로필 사진
                                        String imgPath = cookArticles.getString("imgPath");
                                        // 유저가 작성한 이미지
                                        String postImg = cookArticles.getString("postImg");
                                        // 포스트 내용
                                        String postText = cookArticles.getString("postText");
                                        // 하트버튼
                                        boolean cb_heart = cookArticles.getBoolean("cb_heart");
                                        // 총 하트 수
                                        int heartCount = cookArticles.getInt("heartCount");
                                        // 하트버튼
                                        boolean cb_bookmark = cookArticles.getBoolean("cb_bookmark");
                                        // 댓글 수
                                        String replySum = cookArticles.getString("replySum");
                                        // 다른유저
                                        String otherUser = cookArticles.getString("otherUser");
                                        // 다른유저가 쓴 댓글
                                        String otherUserText = cookArticles.getString("otherUserText");

                                        // 쿡아이템 arraylist에 추가
                                        mCookList.add(new CookItem(postIdx, userID, postText, imgPath, postImg, heartCount, cb_heart, cb_bookmark,
                                                replySum, otherUser, otherUserText));
                                    }

                                    // 어답터에 연결하는 부분
                                    mCookAdapter = new CookAdapter(CookTalkActivity.this, mCookList);

                                    // 체크변화후 데이터 갱신
                                    mCookAdapter.notifyDataSetChanged();

                                    // 북마크 리스너 설정
                                    mCookAdapter.setBookMarkClickListener(CookTalkActivity.this);

                                    // 리사이클러뷰 객체에 어답터 장착
                                    rv_list.setAdapter(mCookAdapter);


                                } else {
                                    //Toast.make error
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();
                    hidepDialog();
                }
            });
            // 캐쉬하지 않고 매번 요청하도록 한다.
            // 서버에 요청
            request.setShouldCache(false);
//            mRequestQueue.add(request);
            // TODO 공통으로 만들어 둔 큐
            App.getInstance().getRequestQueue().add(request);

        } catch (Exception e) {
        }


    }


    @Override
    public void onStart() {
        super.onStart();
        //각종 리스너 등록

    }

    // 사용자와 상호작용 하는 단계 / Activity 스택의 Top에 위치 / 주로 어플 기능이 onResume()에 설정됨
    @Override
    public void onResume() {
        super.onResume();

//        Toast.makeText(getActivity(), "onResume", Toast.LENGTH_SHORT).show();
        // 글쓰기 액티비티 띄우고, 데이터 받아오기
        parseJSON();

        // 셰어드 저장 값 가져오기 // 새로고침 실행하고
        SharedPreferences prefsName = getSharedPreferences("UPDATE", MODE_PRIVATE);
        int result = prefsName.getInt("update", 0); //키값, 디폴트값

        Log.e("셰어드", result + "");

        if (result > 0) {
//            getFragmentManager().beginTransaction().detach(Frag_talk.this).attach(Frag_talk.this).commit();
            mCookList.clear();
            parseJSON();
        }

        // 세어드 저장값 삭제
        SharedPreferences pref = getSharedPreferences("UPDATE", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.clear();
        editor.commit();


        // 셰어드 저장 값 가져오기 // 새로고침 실행하고
        SharedPreferences prefs = getSharedPreferences("LIKE", MODE_PRIVATE);
        int like = prefs.getInt("like", 0); //키값, 디폴트값

        if (like > 0) {
            mCookList.clear();
            parseJSON();
        }

        // 세어드 저장값 삭제
        SharedPreferences prefs1 = getSharedPreferences("LIKE", MODE_PRIVATE);
        SharedPreferences.Editor editor1 = prefs1.edit();
        editor1.clear();
        editor1.commit();
    }


    // 북마크 클릭리스너
    @Override
    public void bookMarkClickListener(boolean isChecked, int postIdx) {

        initDialog();
        showpDialog();

//        Toast.makeText(CookTalkActivity.this, isChecked + ":" + postIdx, Toast.LENGTH_SHORT).show();
        // 셰어드에 저장된 userID 가져옴
        SharedPreferences prefsName = getSharedPreferences("NAME", MODE_PRIVATE);
        String userID = prefsName.getString("userID", "userID"); //키값, 디폴트값

        JsonObjectRequest request = null;

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
//                Log.e("lee", "북마크서버 체크 : " + isChecked);

            } else {
                jsonObject.put("cb_bookmark", false);
//                Log.e("lee", "북마크서버 체크: " + isChecked);
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
//                                    Toast.makeText(CookTalkActivity.this, "북마크" + message, Toast.LENGTH_SHORT).show();
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

//        if (!pDialog.isShowing()) pDialog.show();
    }

    //다이얼로그 삭제
    protected void hidepDialog() {

//        if (pDialog.isShowing()) pDialog.dismiss();
    }

}

