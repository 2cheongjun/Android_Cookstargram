package com.example.myapplication.Chat_test.ChatList;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.myapplication.Chat_test.MyService3;
import com.example.myapplication.Chat_test.chatRoomActivity;
import com.example.myapplication.Home.HomeActivity;
import com.example.myapplication.R;
import com.example.myapplication.Recipe_select.subscribeActivity;
import com.example.myapplication.cookStargram.CookTalkActivity;
import com.example.myapplication.mypage.MypageActivity;
import com.example.myapplication.mypage_ViewBookmark.ViewActivity;
import com.example.myapplication.mypage_bookmark.BookmarkGridActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class chatListActivity extends AppCompatActivity implements chatListInterface {

    public static Context CONTEXT;


    private static final String TAG = "ChatListActivity /";
    RecyclerView recyclerView;
    chatListPresenter presenter;
    chatListAdapter adapter;
    chatListAdapter.ItemClickListener itemClickListener;
    List<ChatDate> chatInfo;


    String myID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);

        // 하단네비 // 레이아웃 xml에도 넣어줘야함.
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        //home
        bottomNavigationView.setSelectedItemId(R.id.chatRoom);
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

        CONTEXT = this;

        //리사이클러뷰
        recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        presenter = new chatListPresenter(this);
        // 레트로핏 조회하기

        // 내 아이디로드
        SharedPreferences prefsName = getSharedPreferences("NAME", MODE_PRIVATE);
        myID = prefsName.getString("userID", "userID"); //키값, 디폴트값


        presenter.getData(myID);// 내가 채팅방리스트에 저장한 사람들리스트 불러오기  // userID 넣고 서버 Select해오기 채팅목록 가져오기


        itemClickListener = ((view, position) -> { // 채팅방누르면 각 개별 채팅방으로 이동하기 어답터 안에 있음.
        });


    }


    public Boolean isServiceRunning(String class_name) {

        // 시스템 내부의 액티비티 상태를 파악하는 ActivityManager객체를 생성한다.
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);

        //  manager.getRunningServices(가져올 서비스 목록 개수) - 현재 시스템에서 동작 중인 모든 서비스 목록을 얻을 수 있다.
        // 리턴값은 List<ActivityManager.RunningServiceInfo>이다. (ActivityManager.RunningServiceInfo의 객체를 담은 List)
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {

            // ActivityManager.RunningServiceInfo의 객체를 통해 현재 실행중인 서비스의 정보를 가져올 수 있다.
            if (class_name.equals(service.service.getClassName())) {

                return true;
            }

        }
        return false;

    }


    @Override
    public void onGetResult(List<ChatDate> chatInfos) {
        adapter = new chatListAdapter(chatInfos, this, itemClickListener);
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);
        chatInfo = chatInfos;

    }

    @Override
    public void onErrorLoading(String message) {

    }


    @Override
    public void onResume() {
        super.onResume();
        // 노티가 실행되면. 채팅목록 리스트를 업데이트 하고싶은데 어떻게??

        // TODO Myservice가 실행중인지 알수있느 메소드
        String class_name = MyService3.class.getName(); // isServiceRunning 메소드에 들어가는 파라미터 값
        isServiceRunning(class_name);

        Log.e(TAG, "현재 실행중인서비스 Myservice/"+isServiceRunning(class_name));

        if (isServiceRunning(class_name)) {
            presenter.getData(myID);
            Log.e(TAG, " onResume 레트로핏으로 채팅목록 재조회");
        }


    }
}