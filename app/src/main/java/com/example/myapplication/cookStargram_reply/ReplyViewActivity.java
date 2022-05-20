package com.example.myapplication.cookStargram_reply;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.myapplication.App;
import com.example.myapplication.R;
import com.example.myapplication.cookStargram.CookItem;
import com.example.myapplication.mypageFriend.FriendInfoActivity;
import com.example.myapplication.mypageFriend.FriendInfoModel;
import com.example.myapplication.mypageFriend.FriendInfoRequest;
import com.example.myapplication.mypageFriend.FriendInfoViewAdapter;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.example.myapplication.Api.Api.URL_REPLY;
import static com.example.myapplication.Api.Api.URL_REPLY_UPDATE;
import static com.example.myapplication.Api.Api.URL_SECOND_REPLY;


public class ReplyViewActivity extends AppCompatActivity implements ReplyNewAdapter.OnPersonItemClickListener, ReplyNewAdapter.OnModifyItemClickListener, ReplyNewAdapter.OnReplyItemClickListener {

    public static final String BASE_URL ="http://3.37.202.166";
    // 리사이클러뷰
    RecyclerView replyRecyclerView;
    // 어답터
    ReplyNewAdapter replyAdapter;

    // 댓글 데이터
    private ArrayList<friendReplyItem> mReplyList;

    private EditText et_reply;// 댓글입력란
    private Button btn_write;// 게시버튼
    private Button btn_modify;// 수정버튼
    private TextView tv_firstText, tv_firstText_name; // 작성자가쓴 첫번째글,작성자 이름
    private ImageView iv_profile_small; // 내가쓴글 작은 프사
    // 내가 작성한 내용추가
//    private TextView tv_myID,tv_title;

    int getNum = 0;
    String userID = "";
    String title = "";
    String postNum = "";
    String storyPostNum = "";

    // 인텐트 값을 가져오기 위한 구분자
    private String actionType = "";
    private CookItem cookItem = null;
    private friendReplyItem replyItem = null;

    SwipeRefreshLayout swipe_layout;

    // 댓글 입력
    private com.android.volley.toolbox.JsonObjectRequest jsonObjectRequest;
    // 볼리 통신
    private RequestQueue mRequestQueue;
    // 삭제 볼리
    RequestQueue queue;

    int position = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reply_view);


        // 뒤로가기버튼
        ImageView iv_back = (ImageView) findViewById(R.id.iv_back);
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // 게시글 번호
        if (getIntent() != null) {
            actionType = getIntent().getStringExtra("actionType");
            cookItem = (CookItem) getIntent().getSerializableExtra("cookItem");
        }

        // 리프레시
        swipe_layout = findViewById(R.id.swipe_layout);
        swipe_layout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipe_layout.setRefreshing(false);
                mReplyList.clear();
                selectReply();
            }
        });

        // 작성자가 쓴 글내용
        tv_firstText = findViewById(R.id.tv_firstText);
        tv_firstText.setText(cookItem.getPostText());
        iv_profile_small = findViewById(R.id.iv_profile_small);

        String imgPath = cookItem.getImgPath();


        Glide.with(ReplyViewActivity.this)
                .load(BASE_URL + "/upload/" +imgPath )
                .skipMemoryCache(true)
                .override(200, 200)
                .centerCrop()
                .circleCrop()
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(iv_profile_small);


        // 저장된 ID값 불러오기
        SharedPreferences prefsName = getSharedPreferences("NAME", MODE_PRIVATE);
        userID = prefsName.getString("userID", "userID");

        tv_firstText_name = findViewById(R.id.tv_firstText_name);
        tv_firstText_name.setText(cookItem.getUserID());

        //데이터 모델리스트
        mReplyList = new ArrayList<>();

        // 댓글 작성창
        et_reply = (EditText) findViewById(R.id.et_reply);
        et_reply.requestFocus();

        // 화면 밀림방지
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
        // UI가림방지
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);


        // 엔터키로 키보드 내리기
        et_reply.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(et_reply.getWindowToken(), 0);    //hide keyboard
                    return true;
                }
                return false;
            }
        });


        // 댓글 조회
        mRequestQueue = Volley.newRequestQueue(this);

        // 댓글 입력버튼을 누름과 동시에 댓글을 조회한다.
        btn_write = findViewById(R.id.btn_write);
        btn_write.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 키보드내림
                InputMethodManager manager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                manager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

                // 서버로 댓글 넣고
                reply();
                // 입력된 글지움
                et_reply.setText("");
                // 댓글리스트 초기화
                mReplyList.clear();
                // 댓글 리스트 조회
                selectReply();
            }
        });


        // 조회
        selectReply();

        // 리사이클러뷰
        replyRecyclerView = (RecyclerView) findViewById(R.id.replyRecyclerView);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        replyRecyclerView.setLayoutManager(manager);

        replyAdapter = new ReplyNewAdapter(ReplyViewActivity.this, mReplyList);
        replyRecyclerView.setAdapter(replyAdapter);
    }

    // TODO 상위아이템 큰박스 아이템 루트 댓글
    List<friendReplyItem> itemList = new ArrayList<>();


    // 댓글 조회
    private void selectReply() {

        if (cookItem != null) {
            int postNum = cookItem.getPostIdx();
            // 댓글 조회 Select
            // Volley로 게시글 번호 전송 userID를 전송하고 게시글에 달린 댓글정보 가져오기
            ReplyRequest replyRequest = new ReplyRequest(postNum + "", responseListener);

            App.getInstance().getRequestQueue().add(replyRequest);
        }
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

                        mReplyList.clear();

                        JSONArray jsonArray = jsonObject.getJSONArray("reply");
                        for (int i = 0; i < jsonArray.length(); i++) {

                            JSONObject reply = jsonArray.getJSONObject(i);

                            // 댓글작성자명 가져옴
                            String userID = reply.getString("userID");

                            String postIdx = reply.getString("postIdx");

                            String title = reply.getString("title");


                            String date = reply.getString("date");

                            int replyIdx = reply.getInt("replyIdx");

                            String step = reply.getString("step");

                            String imgPath =reply.getString("imgPath");

                            // 쿡아이템 arraylist에 추가
                            mReplyList.add(new friendReplyItem(userID, postIdx, title, date, replyIdx, step, imgPath));

                        }
                    }
                }

                replyAdapter.notifyDataSetChanged();

                // 어댑터가생성하고 나서 클릭이벤트를 붙여야한다.(수정,삭제,답글달기 리스너)
                replyAdapter.setOnPersonItemClickListener(ReplyViewActivity.this);
                replyAdapter.setOnModifyItemClickListener(ReplyViewActivity.this);
                replyAdapter.setOnReplyItemClickListener(ReplyViewActivity.this);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    };


    // 댓글입력 서버 insert로 보냄.
    private void reply() {
        // 게시글번호 // 전역변수로 가져온 cookItem에서 가져온값

        int postNum = cookItem.getPostIdx();

        // 입력된 글내용 가져오기
        String title = et_reply.getText().toString();

        // 댓글번호

        // 글번호

        String step = 0 + "";

        try {
            JSONObject jsonObject = new JSONObject();
            // 사용자아이디
            jsonObject.put("userID", userID);
            if (postNum > 0) {
                jsonObject.put("postNum", postNum + "");
            }


            jsonObject.put("title", title);
            //루트댓글은 step이 0이다.
            jsonObject.put("step", step);

            jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL_REPLY, jsonObject,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                String success = response.getString("success");

                                if (true) {
                                    String message = response.getString("message");
//                                    Toast.makeText(ReplyViewActivity.this.getApplicationContext(),
//                                            "댓글이" + message, Toast.LENGTH_SHORT).show();
                                } else {
                                    String message = response.getString("message");
//                                    Toast.makeText(ReplyViewActivity.this.getApplicationContext(),
//                                            "댓글" + message, Toast.LENGTH_SHORT).show();
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(ReplyViewActivity.this.getApplicationContext(), "댓글 Failed", Toast.LENGTH_SHORT).show();
                }
            }

            );
        } catch (JSONException e) {
            e.printStackTrace();
        }
        // 댓글 업로드 리퀘스트  큐 / 요청과 응답을 받아옴

        App.getInstance().getRequestQueue().add(jsonObjectRequest);
    }


    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    public void onResume() {
        super.onResume();
//
//        mReplyList.clear();
//        selectReply();
    }


    // 댓글 삭제 버튼
    @Override
    public void onPersonItemClickListener(int replyIdx) {

//        Toast.makeText(ReplyViewActivity.this, "예 눌렀습니다" + position, Toast.LENGTH_SHORT).show();

        // 삭제 다이얼로그
        AlertDialog.Builder builder = new AlertDialog.Builder(ReplyViewActivity.this);
        builder.setTitle("댓글 삭제").setMessage("삭제하시겠습니까?");
        builder.setPositiveButton("예", new DialogInterface.OnClickListener() {


            @Override
            public void onClick(DialogInterface dialog, int which) {

                // 댓글 삭제 볼리
                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            boolean success = jsonObject.getBoolean("success");
                            String message = jsonObject.getString("message");

                            if (success) {  // 삭제완료
                                Toast.makeText(ReplyViewActivity.this, "댓글 " + message, Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(ReplyViewActivity.this, "댓글 " + message, Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(ReplyViewActivity.this, "삭제 에러발생!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                };

                // Volley 로 회원양식 웹으로 전송
                ReplyDeleteRequest replyDeleteRequest = new ReplyDeleteRequest(cookItem.getPostIdx() + "", replyIdx + "", responseListener, errorListener);
                replyDeleteRequest.setShouldCache(false);

                // 쓰레드를 만들어 응답을 요청하는 큐

                App.getInstance().getRequestQueue().add(replyDeleteRequest);

                // 재조회
                selectReply();
            }
        });

        builder.setNegativeButton("아니요", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
//                Toast.makeText(ReplyViewActivity.this.getApplicationContext(), "아니요 눌렀습니다", Toast.LENGTH_SHORT).show();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    // 댓글 수정버튼
    @Override
    public void onModifyItemClickListener(int position, String title) {
        // update쿼리 서버로 날림

        // 어답터에서 댓글내용값을 넘겨받았다.

        //  이전에 작성했던 댓글 가져와 넣어주고,
        et_reply = (EditText) findViewById(R.id.et_reply);
        if (et_reply != null) {
            et_reply.setText(title);
        }
        // UPdate를 해준다.
        btn_write.setVisibility(View.GONE);
        // 수정버튼 보이기
        btn_modify = findViewById(R.id.btn_modify);
        btn_modify.setVisibility(View.VISIBLE);

        // 수정버튼을 누르면

        btn_modify.setOnClickListener(new View.OnClickListener() {

            final friendReplyItem replyItem = mReplyList.get(position);
            final int replyIdx = replyItem.getReplyIdx();
            final String postNum = replyItem.getPostIdx();

            @Override
            public void onClick(View v) {
                try {
                    JSONObject jsonObject = new JSONObject();
                    // 사용자아이디, 댓글번호, 댓글내용
                    jsonObject.put("userID", userID);
                    jsonObject.put("replyIdx", replyIdx + "");
                    jsonObject.put("postNum", postNum + "");
                    jsonObject.put("title", et_reply.getText().toString());

                    jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL_REPLY_UPDATE, jsonObject,
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    try {
//                                        String success = response.getString("success");


                                        if (true) {
                                            String message = response.getString("message");

                                            Toast.makeText(ReplyViewActivity.this.getApplicationContext(),
                                                    "댓글이 " + message, Toast.LENGTH_SHORT).show();
                                        } else {
                                            String message = response.getString("message");
                                            Toast.makeText(ReplyViewActivity.this.getApplicationContext(),
                                                    "댓글 " + message, Toast.LENGTH_SHORT).show();
                                        }

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(ReplyViewActivity.this.getApplicationContext(), "댓글 Failed", Toast.LENGTH_SHORT).show();
                        }
                    }

                    );
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                App.getInstance().getRequestQueue().add(jsonObjectRequest);

                // 입력된 글지움
                et_reply.setText("");
                // 댓글리스트 초기화
                mReplyList.clear();
                // 댓글 리스트 조회
                selectReply();

                // 게시 버튼 보이기
                btn_write.setVisibility(View.VISIBLE);
                // 수정버튼 숨기기
                btn_modify.setVisibility(View.GONE);

            }
        });
    }

    // TODO 답글 보내기,다이얼로그
    // 답글버튼 클릭리스너 // 답글 insert
    @Override
    public void onReplyItemClickListener(int replyIdx) {

        //키보드 보이게 하는 부분
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);


        AlertDialog.Builder builder = new AlertDialog.Builder(ReplyViewActivity.this);

        builder.setTitle("답글 달기");
//        builder. setMessage("정말 탈퇴하시겠습니까?");

        //다이얼로그에  EditText 삽입하기
        final EditText editText = new EditText(ReplyViewActivity.this);
        builder.setView(editText);


        builder.setPositiveButton("작성", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

//                Toast.makeText(getApplicationContext(), "예 눌렀습니다", Toast.LENGTH_SHORT).show();

                // 키보드내림
                InputMethodManager manager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                manager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

                // 게시버튼 활성화
                btn_write.setVisibility(View.VISIBLE);

                // TODO 서버로전송 Second 댓글
                // 서버로 답글을 댓글 넣고
                //   댓글입력 서버 insert로 보냄.
                // 게시글번호 // 전역변수로 가져온 cookItem에서 가져온값
                int postNum = cookItem.getPostIdx();
                // 다이얼로그에 입력된 글내용 가져오기
                String title = editText.getText().toString();

                // 댓글번호

                try {
                    JSONObject jsonObject = new JSONObject();
                    // 사용자아이디
                    jsonObject.put("userID", userID);
                    jsonObject.put("postNum", postNum + "");
                    jsonObject.put("title", title);
                    // 댓글번호가져오기 // 부모글번호와 자식글 번호는 같다. = 그룹번호 // 부모댓글번호값을 자식에게도 주기
                    jsonObject.put("ref", replyIdx + "");
                    // 루트댓글은 0을 보낸다.
                    jsonObject.put("step", "1");
//                    jsonObject.put("step",step + 1);
                    // 들여쓰기 step값을 받아와서 1씩 더한값을 넣는다.


                    jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL_SECOND_REPLY, jsonObject,
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    try {

//                                        String success = response.getString("success");

                                        if (true) {
                                            String message = response.getString("message");
                                            Toast.makeText(ReplyViewActivity.this.getApplicationContext(),
                                                    "댓글이" + message, Toast.LENGTH_SHORT).show();
                                        } else {
                                            String message = response.getString("message");
                                            Toast.makeText(ReplyViewActivity.this.getApplicationContext(),
                                                    "댓글" + message, Toast.LENGTH_SHORT).show();
                                        }

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(ReplyViewActivity.this.getApplicationContext(), "댓글 Failed", Toast.LENGTH_SHORT).show();
                        }
                    }

                    );
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                // 댓글 업로드 리퀘스트  큐 / 요청과 응답을 받아옴
//                RequestQueue requestQueue = Volley.newRequestQueue(ReplyViewActivity.this.getApplicationContext());
//                requestQueue.add(jsonObjectRequest);

                App.getInstance().getRequestQueue().add(jsonObjectRequest);

                // 입력된 글지움
                et_reply.setText("");
                // 댓글리스트 초기화
                mReplyList.clear();
                // 댓글 리스트 조회
                selectReply();
            }
        });

        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
//                Toast.makeText(getApplicationContext(), "아니요 눌렀습니다", Toast.LENGTH_SHORT).show();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }



}
