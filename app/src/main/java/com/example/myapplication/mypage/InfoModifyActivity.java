package com.example.myapplication.mypage;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.example.myapplication.R;
import com.example.myapplication.login.LoginActivity;

// 마이키친 // 개인정보
// 이름 클릭시  > 개인정보수정페이지로 넘어감
public class InfoModifyActivity extends AppCompatActivity {


    private TextView tv_name, tv_logout;
    private ImageView ic_back;
    RequestQueue queue;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_modify);

        // 뒤로가기
        ic_back = (ImageView) findViewById(R.id.ic_back);
        ic_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        // 로그인 아이디
        tv_name = (TextView) findViewById(R.id.tv_name);
        // 로그인한 아이디값 불러오기
        SharedPreferences pref = getSharedPreferences("NAME", MODE_PRIVATE);
        String result = pref.getString("userID", "로그인이 필요합니다."); //키값, 디폴트값
        tv_name.setText(result);

        tv_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(InfoModifyActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });


        // 로그아웃
        tv_logout = (TextView) findViewById(R.id.tv_logout);
        tv_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 아이값 셰어드에서 삭제
                tv_name.setText("");
                SharedPreferences pref = getSharedPreferences("NAME", MODE_PRIVATE);
                SharedPreferences.Editor editor = pref.edit();
                editor.remove("userID");
                editor.apply();


                // 로그아웃시, 로그인화면으로 이동
                Toast.makeText(getApplicationContext(), "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(InfoModifyActivity.this, LoginActivity.class);
                startActivity(intent);

            }
        });
    }

}

