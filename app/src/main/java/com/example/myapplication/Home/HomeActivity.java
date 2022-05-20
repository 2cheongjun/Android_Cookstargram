package com.example.myapplication.Home;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.myapplication.Chat_test.ChatList.chatListActivity;
import com.example.myapplication.R;
import com.example.myapplication.Recipe_select.subscribeActivity;
import com.example.myapplication.cookStargram.CookTalkActivity;
import com.example.myapplication.mypage.MypageActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.jetbrains.annotations.NotNull;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        //home
        bottomNavigationView.setSelectedItemId(R.id.talk);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull @NotNull MenuItem menuItem) {

                switch (menuItem.getItemId()) {

                    case R.id.talk:
                        startActivity(new Intent(getApplicationContext(), CookTalkActivity.class));
                        overridePendingTransition(0, 0);
                        return true;

                    case R.id.chatRoom:
                        startActivity(new Intent(getApplicationContext(), chatListActivity.class));
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

        // mainView는 액티비티의 메인 레이아웃 뷰이고, getDecorView는 화면의 최상단뷰이다.
        // 토스트처럼 컨텍스트에 종속되는 것이 아니라 뷰에 띄우는 것이기 때문에 뷰를 매개변수로 받는다.

//        Snackbar.make(bottomNavigationView,"라이브", BaseTransientBottomBar.LENGTH_LONG).show();
//        Snackbar.make(getWindow().getDecorView(),"안녕", BaseTransientBottomBar.LENGTH_LONG).show();


//        Snackbar.make(bottomNavigationView,"라이브", BaseTransientBottomBar.LENGTH_LONG)
//        // 스낵바 Action 설정("표시할 텍스트", onClick)
//        .setAction("OK", new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                // 스낵바의 OK 클릭시 실행할 작업
//                Toast.makeText(view.getContext(), "스낵바 Action 클릭", Toast.LENGTH_SHORT).show();
//            }
//        }).show();
    }


}