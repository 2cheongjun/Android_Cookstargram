package com.example.myapplication.Recipe_select;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.Chat_test.ChatList.chatListActivity;
import com.example.myapplication.Home.HomeActivity;
import com.example.myapplication.R;
import com.example.myapplication.cookStargram.CookTalkActivity;
import com.example.myapplication.mypage.MypageActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.jetbrains.annotations.NotNull;

import java.util.List;

// 구독 리스트 조회
public class subscribeActivity extends AppCompatActivity implements SubscribeView {

    private static final String TAG = "TestActivity-레트로핏";
    FloatingActionButton fab;

    RecyclerView recyclerView;
    SubscribePresenter presenter;
    SubscribeAdapter adapter;
    SubscribeAdapter.ItemClickListener itemClickListener;
    List<Note> note;
    ImageView iv_imgNo;
    TextView list_empty;
    ProgressDialog progressDoalog;
    TextView tv_spinerName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscribe);


        /**
         *
         */

        // 하단네비
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        //home
        bottomNavigationView.setSelectedItemId(R.id.recipe);
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
                        startActivity(new Intent(getApplicationContext(), MypageActivity.class));
                        overridePendingTransition(0, 0);
                        return true;
                }
                return false;
            }
        });

        //리사이클러뷰
        recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        presenter = new SubscribePresenter(this);
        // 레트로핏 조회하기

        // 나의 아이디
        SharedPreferences prefsName = getSharedPreferences("NAME", MODE_PRIVATE);
        String myID = prefsName.getString("userID", "userID"); //키값, 디폴트값

        presenter.getData(myID);

        itemClickListener = ((view,position) ->{
//                    Toast.makeText(this,"터치", Toast.LENGTH_SHORT).show();

        });

        iv_imgNo = findViewById(R.id.iv_imgNo);
        // 팔로잉하는 값이 0일떄

        Log.e((String) TAG, " 노트값 /" + "없음");
    }



    @Override
    public void onGetResult(List<Note> notes) {

        adapter = new SubscribeAdapter(notes, this, itemClickListener);
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);
        note = notes;
    }

    @Override
    public void onErrorLoading(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }


    @Override
    protected void onResume() {
        super.onResume();



    }
}