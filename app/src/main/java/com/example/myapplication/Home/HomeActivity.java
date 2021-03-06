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

        // mainView??? ??????????????? ?????? ???????????? ?????????, getDecorView??? ????????? ??????????????????.
        // ??????????????? ??????????????? ???????????? ?????? ????????? ?????? ????????? ????????? ????????? ?????? ??????????????? ?????????.

//        Snackbar.make(bottomNavigationView,"?????????", BaseTransientBottomBar.LENGTH_LONG).show();
//        Snackbar.make(getWindow().getDecorView(),"??????", BaseTransientBottomBar.LENGTH_LONG).show();


//        Snackbar.make(bottomNavigationView,"?????????", BaseTransientBottomBar.LENGTH_LONG)
//        // ????????? Action ??????("????????? ?????????", onClick)
//        .setAction("OK", new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                // ???????????? OK ????????? ????????? ??????
//                Toast.makeText(view.getContext(), "????????? Action ??????", Toast.LENGTH_SHORT).show();
//            }
//        }).show();
    }


}