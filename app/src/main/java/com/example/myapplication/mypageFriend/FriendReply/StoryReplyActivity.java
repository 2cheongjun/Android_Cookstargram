package com.example.myapplication.mypageFriend.FriendReply;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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
import com.example.myapplication.cookStargram_reply.ReplyDeleteRequest;
import com.example.myapplication.cookStargram_reply.ReplyViewActivity;
import com.example.myapplication.cookStargram_reply.friendReplyItem;
import com.example.myapplication.cookStargram_reply.ReplyRequest;
import com.example.myapplication.mypageFriend.FriendView.StoryItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.example.myapplication.Api.Api.BASE_URL;
import static com.example.myapplication.Api.Api.URL_REPLY;
import static com.example.myapplication.Api.Api.URL_REPLY_UPDATE;
import static com.example.myapplication.Api.Api.URL_SECOND_REPLY;

public class StoryReplyActivity extends AppCompatActivity implements StoryReplyNewAdapter.OnPersonItemClickListener, StoryReplyNewAdapter.OnModifyItemClickListener, StoryReplyNewAdapter.OnReplyItemClickListener {

    // 리사이클러뷰
    RecyclerView replyRecyclerView;
    // 어답터
    StoryReplyNewAdapter friendReplyAdapter;

    // 댓글 데이터
    private ArrayList<friendReplyItem> mfriendReplyList;

    private EditText et_reply;// 댓글입력란
    private Button btn_write;// 게시버튼
    private Button btn_modify;// 수정버튼

    private TextView tv_firstText, tv_firstText_name; // 작성자가쓴 첫번째글,작성자 이름
    private ImageView iv_profile_small; // 내가쓴글 작은 프사

    int getNum = 0;
    String userID = "";
    String title = "";
    int postNum = 0;
    String storyPostNum = "";

    // 인텐트 값을 가져오기 위한 구분자
    private String actionType = "";
    private CookItem cookItem = null;
    private StoryItem datalist = null;

    // 인텐트 값을 가져오기 위한 구분자
//    private String actionType2 = "";
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


        // 친구글에서 받아온 게시글 번호
        if (getIntent() != null) {
            actionType = getIntent().getStringExtra("actionType");
            datalist = (StoryItem) getIntent().getSerializableExtra("datalist");
//            postNum = intent.getIntExtra("postNum", Integer.parseInt(postNum+""));
        }

        // 리프레시
        swipe_layout = findViewById(R.id.swipe_layout);
        swipe_layout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipe_layout.setRefreshing(false);
                mfriendReplyList.clear();
                selectReply();
            }
        });


        // 저장된 ID값 불러오기
        SharedPreferences prefsName = getSharedPreferences("NAME", MODE_PRIVATE);
        userID = prefsName.getString("userID", "userID");

        // TODO
        // 댓글상세 상단에 작성자 이름 표기
        tv_firstText_name = findViewById(R.id.tv_firstText_name);
        tv_firstText_name.setText(datalist.getUserID());


        //데이터 모델리스트
        mfriendReplyList = new ArrayList<>();

        // 댓글 작성창
        et_reply = (EditText) findViewById(R.id.et_reply);
        et_reply.requestFocus();

        //키보드 보이게 하는 부분
//        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);

        // 화면 밀림방지
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
//        // UI가림방지
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

        // TODO
        // 작성자가 쓴 글내용
        tv_firstText = findViewById(R.id.tv_firstText);
        tv_firstText.setText(datalist.getPostText());
        iv_profile_small = findViewById(R.id.iv_profile_small);

        String imgPath = datalist.getImgPath();


        Glide.with(StoryReplyActivity .this)
                .load(BASE_URL + "/upload/" +imgPath )
                .skipMemoryCache(true)
                .override(200, 200)
                .centerCrop()
                .circleCrop()
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(iv_profile_small);


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
                mfriendReplyList.clear();
                // 댓글 리스트 조회
                selectReply();
            }
        });


        selectReply();

        // 리사이클러뷰
        replyRecyclerView = (RecyclerView) findViewById(R.id.replyRecyclerView);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        replyRecyclerView.setLayoutManager(manager);

        friendReplyAdapter = new StoryReplyNewAdapter(StoryReplyActivity.this, mfriendReplyList);
        replyRecyclerView.setAdapter(friendReplyAdapter);
    }




    // TODO 상위아이템 큰박스 아이템 루트 댓글
    List<friendReplyItem> itemList = new ArrayList<>();


    // 댓글 조회
    private void selectReply() {

        if (datalist != null) {
            int postNum = datalist.getPostIdx();
            // 댓글 조회 Select
            // Volley 로 회원양식 웹으로 전송 userID를 전송하고 댓글정보 가져오기
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

                        mfriendReplyList.clear();

                        JSONArray jsonArray = jsonObject.getJSONArray("reply");
                        for (int i = 0; i < jsonArray.length(); i++) {

                            JSONObject reply = jsonArray.getJSONObject(i);

                            String userID = reply.getString("userID");

                            String postIdx = reply.getString("postIdx");

                            String title = reply.getString("title");

//                            String postImg = reply.getString("postImg");

                            String date = reply.getString("date");

                            int replyIdx = reply.getInt("replyIdx");

                            String step = reply.getString("step");

                            String imgPath = reply.getString("imgPath");

                            // 쿡아이템 arraylist에 추가
                            mfriendReplyList.add(new friendReplyItem(userID, postIdx, title, date, replyIdx, step,imgPath));

                        }
                    }
                }

                // 어댑터가생성하고 나서 클릭이벤트를 붙여야한다.(수정,삭제,답글달기 리스너)
                friendReplyAdapter.setOnPersonItemClickListener((StoryReplyNewAdapter.OnPersonItemClickListener) StoryReplyActivity.this);
                friendReplyAdapter.setOnModifyItemClickListener((StoryReplyNewAdapter.OnModifyItemClickListener) StoryReplyActivity.this);
                friendReplyAdapter.setOnReplyItemClickListener((StoryReplyNewAdapter.OnReplyItemClickListener) StoryReplyActivity.this);

                friendReplyAdapter.notifyDataSetChanged();

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    };


    //   댓글입력 서버 insert로 보냄.
    private void reply() {
        // 게시글번호 // 전역변수로 가져온 cookItem에서 가져온값

        int postNum = datalist.getPostIdx();

        // 입력된 글내용 가져오기
        String title = et_reply.getText().toString();


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
            // 루트댓글은 0을 보낸다.
//            jsonObject.put("ref", userID+1);



            jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL_REPLY, jsonObject,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
//                                    String success = response.getString("success");


                                if (true) {
                                    String message = response.getString("message");
//                                        Toast.makeText(StoryReplyActivity.this.getApplicationContext(),
//                                                "댓글이" + message, Toast.LENGTH_SHORT).show();
                                } else {
                                    String message = response.getString("message");
//                                        Toast.makeText(StoryReplyActivity.this.getApplicationContext(),
//                                                "댓글" + message, Toast.LENGTH_SHORT).show();
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(StoryReplyActivity.this.getApplicationContext(), "댓글 Failed", Toast.LENGTH_SHORT).show();
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
        mfriendReplyList.clear();
        selectReply();
    }


    // 댓글 삭제 버튼
    @Override
    public void onPersonItemClickListener(int replyIdx) {


        // 삭제 다이얼로그
        AlertDialog.Builder builder = new AlertDialog.Builder(StoryReplyActivity.this);
        builder.setTitle("댓글삭제").setMessage("삭제하시겠습니까?");
        builder.setPositiveButton("예", new DialogInterface.OnClickListener() {


            @Override
            public void onClick(DialogInterface dialog, int which) {

//                Toast.makeText(ReplyViewActivity.this, "예 눌렀습니다", Toast.LENGTH_SHORT).show();
                // 댓글 삭제 볼리
                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            boolean success = jsonObject.getBoolean("success");
                            String message = jsonObject.getString("message");

                            if (success) {  // 삭제완료
                                Toast.makeText(StoryReplyActivity.this, "댓글" + message, Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(StoryReplyActivity.this, "댓글" + message, Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(StoryReplyActivity.this, "삭제 에러발생!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                };

                // Volley 로 회원양식 웹으로 전송
                ReplyDeleteRequest replyDeleteRequest = new ReplyDeleteRequest(datalist.getPostIdx() + "", replyIdx + "", responseListener, errorListener);
                replyDeleteRequest.setShouldCache(false);

                App.getInstance().getRequestQueue().add(replyDeleteRequest);

                // 댓글리스트 초기화
                mfriendReplyList.clear();
                // 댓글 리스트 조회
                selectReply();
            }
        });

        builder.setNegativeButton("아니요", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                Toast.makeText(StoryReplyActivity.this.getApplicationContext(), "아니요 눌렀습니다", Toast.LENGTH_SHORT).show();
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

            final friendReplyItem replyItem = mfriendReplyList.get(position);
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
//                                            String success = response.getString("success");


                                        if (true) {
                                            String message = response.getString("message");
//                                                Toast.makeText(StoryReplyActivity.this.getApplicationContext(),
//                                                        "댓글이" + message , Toast.LENGTH_SHORT).show();
                                        } else {
                                            String message = response.getString("message");
//                                                Toast.makeText(StoryReplyActivity.this.getApplicationContext(),
//                                                        "댓글" + message, Toast.LENGTH_SHORT).show();
                                        }

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(StoryReplyActivity.this.getApplicationContext(), "댓글 Failed", Toast.LENGTH_SHORT).show();
                        }
                    }

                    );
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                // 공용큐
                App.getInstance().getRequestQueue().add(jsonObjectRequest);

                // 입력된 글지움
                et_reply.setText("");
                // 댓글리스트 초기화
                mfriendReplyList.clear();
                // 댓글 리스트 조회
                selectReply();

                // 게시 버튼 보이기
                btn_write.setVisibility(View.VISIBLE);
                // 수정버튼 숨기기
                btn_modify.setVisibility(View.GONE);

            }
        });
    }

    // 답글버튼 클릭리스너
    @Override
    public void onReplyItemClickListener(int replyIdx) {

        //키보드 보이게 하는 부분
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);


        AlertDialog.Builder builder = new AlertDialog.Builder(StoryReplyActivity.this);

        builder.setTitle("답글 달기");

        //다이얼로그에  EditText 삽입하기
        final EditText editText = new EditText(StoryReplyActivity.this);
        builder.setView(editText);


        builder.setPositiveButton("작성", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                // 키보드내림
                InputMethodManager manager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                manager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

                // 게시버튼 활성화
                btn_write.setVisibility(View.VISIBLE);

                // TODO 서버로전송 Second 댓글
                // 서버로 답글을 댓글 넣고
                //   댓글입력 서버 insert로 보냄.
                // 게시글번호 // 전역변수로 가져온 cookItem에서 가져온값
                int postNum = datalist.getPostIdx();
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
                    // 들여쓰기


                    jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL_SECOND_REPLY, jsonObject,
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    try {

//                                        String success = response.getString("success");


                                        if (true) {
                                            String message = response.getString("message");
                                            Toast.makeText(StoryReplyActivity.this.getApplicationContext(),
                                                    "댓글이 " + message, Toast.LENGTH_SHORT).show();
                                        } else {
                                            String message = response.getString("message");
                                            Toast.makeText(StoryReplyActivity.this.getApplicationContext(),
                                                    "댓글 " + message, Toast.LENGTH_SHORT).show();
                                        }

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(StoryReplyActivity.this.getApplicationContext(), "댓글 Failed", Toast.LENGTH_SHORT).show();
                        }
                    }

                    );
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                // 공용큐
                App.getInstance().getRequestQueue().add(jsonObjectRequest);

                // 입력된 글지움
                et_reply.setText("");
                // 댓글리스트 초기화
                mfriendReplyList.clear();
                // 댓글 리스트 조회
                selectReply();
            }
        });

        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

//                    Toast.makeText(getApplicationContext(), "아니요 눌렀습니다", Toast.LENGTH_SHORT).show();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }


}