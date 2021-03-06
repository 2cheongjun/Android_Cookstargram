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

    // ??????????????????
    RecyclerView replyRecyclerView;
    // ?????????
    StoryReplyNewAdapter friendReplyAdapter;

    // ?????? ?????????
    private ArrayList<friendReplyItem> mfriendReplyList;

    private EditText et_reply;// ???????????????
    private Button btn_write;// ????????????
    private Button btn_modify;// ????????????

    private TextView tv_firstText, tv_firstText_name; // ??????????????? ????????????,????????? ??????
    private ImageView iv_profile_small; // ???????????? ?????? ??????

    int getNum = 0;
    String userID = "";
    String title = "";
    int postNum = 0;
    String storyPostNum = "";

    // ????????? ?????? ???????????? ?????? ?????????
    private String actionType = "";
    private CookItem cookItem = null;
    private StoryItem datalist = null;

    // ????????? ?????? ???????????? ?????? ?????????
//    private String actionType2 = "";
    private friendReplyItem replyItem = null;


    SwipeRefreshLayout swipe_layout;

    // ?????? ??????
    private com.android.volley.toolbox.JsonObjectRequest jsonObjectRequest;
    // ?????? ??????
    private RequestQueue mRequestQueue;
    // ?????? ??????
    RequestQueue queue;

    int position = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reply_view);


        // ??????????????????
        ImageView iv_back = (ImageView) findViewById(R.id.iv_back);
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        // ??????????????? ????????? ????????? ??????
        if (getIntent() != null) {
            actionType = getIntent().getStringExtra("actionType");
            datalist = (StoryItem) getIntent().getSerializableExtra("datalist");
//            postNum = intent.getIntExtra("postNum", Integer.parseInt(postNum+""));
        }

        // ????????????
        swipe_layout = findViewById(R.id.swipe_layout);
        swipe_layout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipe_layout.setRefreshing(false);
                mfriendReplyList.clear();
                selectReply();
            }
        });


        // ????????? ID??? ????????????
        SharedPreferences prefsName = getSharedPreferences("NAME", MODE_PRIVATE);
        userID = prefsName.getString("userID", "userID");

        // TODO
        // ???????????? ????????? ????????? ?????? ??????
        tv_firstText_name = findViewById(R.id.tv_firstText_name);
        tv_firstText_name.setText(datalist.getUserID());


        //????????? ???????????????
        mfriendReplyList = new ArrayList<>();

        // ?????? ?????????
        et_reply = (EditText) findViewById(R.id.et_reply);
        et_reply.requestFocus();


        // ?????? ????????????
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
//        // UI????????????
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);


        // ???????????? ????????? ?????????
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
        // ???????????? ??? ?????????
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


        // ?????? ??????
        mRequestQueue = Volley.newRequestQueue(this);

        // ?????? ??????????????? ????????? ????????? ????????? ????????????.
        btn_write = findViewById(R.id.btn_write);
        btn_write.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // ???????????????
                InputMethodManager manager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                manager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

                // ????????? ?????? ??????
                reply();
                // ????????? ?????????
                et_reply.setText("");
                // ??????????????? ?????????
                mfriendReplyList.clear();
                // ?????? ????????? ??????
                selectReply();
            }
        });


        selectReply();

        // ??????????????????
        replyRecyclerView = (RecyclerView) findViewById(R.id.replyRecyclerView);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        replyRecyclerView.setLayoutManager(manager);

        friendReplyAdapter = new StoryReplyNewAdapter(StoryReplyActivity.this, mfriendReplyList);
        replyRecyclerView.setAdapter(friendReplyAdapter);
    }




    // TODO ??????????????? ????????? ????????? ?????? ??????
    List<friendReplyItem> itemList = new ArrayList<>();


    // ?????? ??????
    private void selectReply() {

        if (datalist != null) {
            int postNum = datalist.getPostIdx();
            // ?????? ?????? Select
            // Volley ??? ???????????? ????????? ?????? userID??? ???????????? ???????????? ????????????
            ReplyRequest replyRequest = new ReplyRequest(postNum + "", responseListener);
            App.getInstance().getRequestQueue().add(replyRequest);
        }


    }

    Response.Listener<String> responseListener = new Response.Listener<String>() {
        @Override
        public void onResponse(String response) {
            try {
                // json??????????????? ????????????, response??? ??????.
                JSONObject jsonObject = new JSONObject(response);

                // ????????? ??????
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

                            // ???????????? arraylist??? ??????
                            mfriendReplyList.add(new friendReplyItem(userID, postIdx, title, date, replyIdx, step,imgPath));

                        }
                    }
                }

                // ???????????????????????? ?????? ?????????????????? ???????????????.(??????,??????,???????????? ?????????)
                friendReplyAdapter.setOnPersonItemClickListener((StoryReplyNewAdapter.OnPersonItemClickListener) StoryReplyActivity.this);
                friendReplyAdapter.setOnModifyItemClickListener((StoryReplyNewAdapter.OnModifyItemClickListener) StoryReplyActivity.this);
                friendReplyAdapter.setOnReplyItemClickListener((StoryReplyNewAdapter.OnReplyItemClickListener) StoryReplyActivity.this);

                friendReplyAdapter.notifyDataSetChanged();

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    };


    //   ???????????? ?????? insert??? ??????.
    private void reply() {
        // ??????????????? // ??????????????? ????????? cookItem?????? ????????????

        int postNum = datalist.getPostIdx();

        // ????????? ????????? ????????????
        String title = et_reply.getText().toString();


        String step = 0 + "";


        try {
            JSONObject jsonObject = new JSONObject();
            // ??????????????????
            jsonObject.put("userID", userID);
            if (postNum > 0) {
                jsonObject.put("postNum", postNum + "");
            }


            jsonObject.put("title", title);
            //??????????????? step??? 0??????.
            jsonObject.put("step", step);
            // ??????????????? 0??? ?????????.
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
//                                                "?????????" + message, Toast.LENGTH_SHORT).show();
                                } else {
                                    String message = response.getString("message");
//                                        Toast.makeText(StoryReplyActivity.this.getApplicationContext(),
//                                                "??????" + message, Toast.LENGTH_SHORT).show();
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(StoryReplyActivity.this.getApplicationContext(), "?????? Failed", Toast.LENGTH_SHORT).show();
                }
            }

            );
        } catch (JSONException e) {
            e.printStackTrace();
        }
        // ?????? ????????? ????????????  ??? / ????????? ????????? ?????????

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


    // ?????? ?????? ??????
    @Override
    public void onPersonItemClickListener(int replyIdx) {


        // ?????? ???????????????
        AlertDialog.Builder builder = new AlertDialog.Builder(StoryReplyActivity.this);
        builder.setTitle("????????????").setMessage("?????????????????????????");
        builder.setPositiveButton("???", new DialogInterface.OnClickListener() {


            @Override
            public void onClick(DialogInterface dialog, int which) {

//                Toast.makeText(ReplyViewActivity.this, "??? ???????????????", Toast.LENGTH_SHORT).show();
                // ?????? ?????? ??????
                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            boolean success = jsonObject.getBoolean("success");
                            String message = jsonObject.getString("message");

                            if (success) {  // ????????????
                                Toast.makeText(StoryReplyActivity.this, "??????" + message, Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(StoryReplyActivity.this, "??????" + message, Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(StoryReplyActivity.this, "?????? ????????????!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                };

                // Volley ??? ???????????? ????????? ??????
                ReplyDeleteRequest replyDeleteRequest = new ReplyDeleteRequest(datalist.getPostIdx() + "", replyIdx + "", responseListener, errorListener);
                replyDeleteRequest.setShouldCache(false);

                App.getInstance().getRequestQueue().add(replyDeleteRequest);

                // ??????????????? ?????????
                mfriendReplyList.clear();
                // ?????? ????????? ??????
                selectReply();
            }
        });

        builder.setNegativeButton("?????????", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                Toast.makeText(StoryReplyActivity.this.getApplicationContext(), "????????? ???????????????", Toast.LENGTH_SHORT).show();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    // ?????? ????????????
    @Override
    public void onModifyItemClickListener(int position, String title) {
        // update?????? ????????? ??????

        // ??????????????? ?????????????????? ???????????????.

        //  ????????? ???????????? ?????? ????????? ????????????,
        et_reply = (EditText) findViewById(R.id.et_reply);
        if (et_reply != null) {
            et_reply.setText(title);
        }
        // UPdate??? ?????????.
        btn_write.setVisibility(View.GONE);
        // ???????????? ?????????
        btn_modify = findViewById(R.id.btn_modify);
        btn_modify.setVisibility(View.VISIBLE);

        // ??????????????? ?????????

        btn_modify.setOnClickListener(new View.OnClickListener() {

            final friendReplyItem replyItem = mfriendReplyList.get(position);
            final int replyIdx = replyItem.getReplyIdx();
            final String postNum = replyItem.getPostIdx();

            @Override
            public void onClick(View v) {
                try {
                    JSONObject jsonObject = new JSONObject();
                    // ??????????????????, ????????????, ????????????
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
//                                                        "?????????" + message , Toast.LENGTH_SHORT).show();
                                        } else {
                                            String message = response.getString("message");
//                                                Toast.makeText(StoryReplyActivity.this.getApplicationContext(),
//                                                        "??????" + message, Toast.LENGTH_SHORT).show();
                                        }

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(StoryReplyActivity.this.getApplicationContext(), "?????? Failed", Toast.LENGTH_SHORT).show();
                        }
                    }

                    );
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                // ?????????
                App.getInstance().getRequestQueue().add(jsonObjectRequest);

                // ????????? ?????????
                et_reply.setText("");
                // ??????????????? ?????????
                mfriendReplyList.clear();
                // ?????? ????????? ??????
                selectReply();

                // ?????? ?????? ?????????
                btn_write.setVisibility(View.VISIBLE);
                // ???????????? ?????????
                btn_modify.setVisibility(View.GONE);

            }
        });
    }

    // ???????????? ???????????????
    @Override
    public void onReplyItemClickListener(int replyIdx) {

        //????????? ????????? ?????? ??????
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);


        AlertDialog.Builder builder = new AlertDialog.Builder(StoryReplyActivity.this);

        builder.setTitle("?????? ??????");

        //??????????????????  EditText ????????????
        final EditText editText = new EditText(StoryReplyActivity.this);
        builder.setView(editText);


        builder.setPositiveButton("??????", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                // ???????????????
                InputMethodManager manager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                manager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

                // ???????????? ?????????
                btn_write.setVisibility(View.VISIBLE);

                // TODO ??????????????? Second ??????
                // ????????? ????????? ?????? ??????
                //   ???????????? ?????? insert??? ??????.
                // ??????????????? // ??????????????? ????????? cookItem?????? ????????????
                int postNum = datalist.getPostIdx();
                // ?????????????????? ????????? ????????? ????????????
                String title = editText.getText().toString();

                // ????????????

                try {
                    JSONObject jsonObject = new JSONObject();
                    // ??????????????????
                    jsonObject.put("userID", userID);
                    jsonObject.put("postNum", postNum + "");
                    jsonObject.put("title", title);
                    // ???????????????????????? // ?????????????????? ????????? ????????? ??????. = ???????????? // ???????????????????????? ??????????????? ??????
                    jsonObject.put("ref", replyIdx + "");
                    // ??????????????? 0??? ?????????.
                    jsonObject.put("step", "1");
                    // ????????????


                    jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL_SECOND_REPLY, jsonObject,
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    try {

//                                        String success = response.getString("success");


                                        if (true) {
                                            String message = response.getString("message");
                                            Toast.makeText(StoryReplyActivity.this.getApplicationContext(),
                                                    "????????? " + message, Toast.LENGTH_SHORT).show();
                                        } else {
                                            String message = response.getString("message");
                                            Toast.makeText(StoryReplyActivity.this.getApplicationContext(),
                                                    "?????? " + message, Toast.LENGTH_SHORT).show();
                                        }

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(StoryReplyActivity.this.getApplicationContext(), "?????? Failed", Toast.LENGTH_SHORT).show();
                        }
                    }

                    );
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                // ?????????
                App.getInstance().getRequestQueue().add(jsonObjectRequest);

                // ????????? ?????????
                et_reply.setText("");
                // ??????????????? ?????????
                mfriendReplyList.clear();
                // ?????? ????????? ??????
                selectReply();
            }
        });

        builder.setNegativeButton("??????", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

//                    Toast.makeText(getApplicationContext(), "????????? ???????????????", Toast.LENGTH_SHORT).show();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }


}