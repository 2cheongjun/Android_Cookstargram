package com.example.myapplication.login;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.example.myapplication.Chat_test.socketService;
import com.example.myapplication.Home.HomeActivity;
import com.example.myapplication.R;
import com.example.myapplication.cookStargram.CookTalkActivity;
import com.example.myapplication.mypage_bookmark.BookmarkGridActivity;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "loginActicity";
    private EditText et_login_id, et_login_pass;
    Button btn_login, btn_join, showForgotPasswordDialog, tv_cook, tv_home;
    Button button2, button3;
    TextView tv_cookstagram;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //TODO service실행
        Intent intent = new Intent(getApplicationContext(), socketService.class); // 실행시키고픈 서비스클래스 이름
        // intent.putExtra("", ""); 필요시 인텐트에 필요한 데이터를 담아준다
        startService(intent); // 서비스 실행!
        Log.e(TAG, "로그인시 서비스실행 /" + intent );

        et_login_id = findViewById(R.id.et_login_id);
        et_login_pass = findViewById(R.id.et_login_pass);
        btn_login = findViewById(R.id.btn_login);
        btn_join = findViewById(R.id.btn_join);
        showForgotPasswordDialog = findViewById(R.id.btn_findPassword);

        tv_home = findViewById(R.id.tv_home);
        // 테스트 홈버튼 // 둘러보기
        tv_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, CookTalkActivity.class);
                startActivity(intent);
            }
        });


        // 회원가입 버튼
        btn_join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
        // 로그인 버튼
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userID = et_login_id.getText().toString();
                String userPassword = et_login_pass.getText().toString();

                // 서버에 요청을 통해 받아온 데이터
                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            // json오브젝트를 만들어서, response를 저장.
                            JSONObject jsonObject = new JSONObject(response);
                            // php에서 사용했던 $response["success"] = false; /success라는 변수를
                            // true값으로 만들어줌.
                            boolean success = jsonObject.getBoolean("success");
                            // 로그인 성공
                            if (success) {
                                String userID = jsonObject.getString("userID");
//                                String userPassword = jsonObject.getString("userPassword");

                                Toast.makeText(getApplicationContext(), "로그인에 성공했습니다.", Toast.LENGTH_SHORT).show();
                                // 메인으로 Intent값을 보냄 // 로그인값을 셰어드에 저장하고, 마이페이지에 불러오도록 하기
                                Intent intent = new Intent(LoginActivity.this, CookTalkActivity.class);
                                intent.putExtra("userID", userID);
//                                intent.putExtra("userPassword",userPassword);
                                startActivity(intent);

                                // 로그인 // 패스워드 아이디값 저장하기
                                SharedPreferences pref = getSharedPreferences("NAME", MODE_PRIVATE);
                                SharedPreferences.Editor editor = pref.edit();
                                editor.putString("userID", userID); //키값, 저장값
                                editor.putString("userPassword", userPassword); //키값, 저장값
                                editor.apply();


                            } else { // 로그인 실패
                                Toast.makeText(getApplicationContext(), "로그인에 실패했습니다.", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                };
                // 문자열을 결과로 받는 요청 정보.
                LoginRequest loginRequest = new LoginRequest(userID, userPassword, responseListener);

                // 서버요청자 // 서버에 요청을 보내는 역할, 쓰레드를 자동으로 생성하여 서버에 요청을 보내고 응답을 받는다.
                RequestQueue queue = Volley.newRequestQueue(LoginActivity.this);
                queue.add(loginRequest);

            }
        });

        // 비밀번호찾기 /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        showForgotPasswordDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserForgotPasswordWithEmail();
//                test();
            }
        });

        // TODO 자동로그인 / 로그인한 아이디값이 있으면, 피드페이지가 처음에 뜸
        SharedPreferences prefsName = getSharedPreferences("NAME", MODE_PRIVATE);
        String userID = prefsName.getString("userID", ""); //키값, 디폴트값

        if(!TextUtils.isEmpty(userID)){
            Intent intent2 = new Intent(this, CookTalkActivity.class);
            startActivity(intent2);
        }
    }


    // 비밀번호 재설정
    private void UserForgotPasswordWithEmail() {
        View forgot_password_layout = LayoutInflater.from(this).inflate(R.layout.forgot_password, null);
        // 레이아웃안에 들어 있는 Editbox와 버튼
        EditText forgot_Email = forgot_password_layout.findViewById(R.id.forgot_Email);
        Button btn_findPassword = forgot_password_layout.findViewById(R.id.forgot_password_button);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("FORGOT PASSWORD");
        builder.setView(forgot_password_layout);
        builder.setCancelable(false);

        // 비밀번호 재설정 팝업
        AlertDialog dialog = builder.create();
        dialog.show();


        // 비번찾기 버튼
//        btn_findPassword.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // 앱에서 입력된 이메일값 가져오기
//                String email = forgot_Email.getText().toString().trim();
//                if (email.isEmpty()) {
//                    Toast.makeText(getApplicationContext(), " 이메일 값이 없습니다..", Toast.LENGTH_SHORT).show();
//                }
////                Toast.makeText(getApplicationContext(), "이메일 발송에 성공했습니다.", Toast.LENGTH_LONG).show();
//
//                // 서버에서 응답받는부분
//                Response.Listener<String> responseListener = new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String response) {
//                        try {
//
//                            // 서버에서 가져온 응답, 이메일주소
//                            //{"mail":"send"}를 json으로 받아옴
//                            Log.e("jun124", response);
//
//                            // 가져온 제이슨 mail값을 String mail로 변환
//
//                            JSONObject object = new JSONObject(response);
//                            String mail = object.getString("mail");
//
//                            // 로그찍기
//                            Log.d("jun", mail);
//                            // 서버에서 가져온 KEY값이 SEND이면
//                            if (mail.equals("send")) {
//                                Toast.makeText(getApplicationContext(), " 이메일 발송 성공했습니다.", Toast.LENGTH_SHORT).show();
//                                dialog.dismiss();
//                            } else {
////                                message(response);
//                                dialog.dismiss();
//                                Toast.makeText(getApplicationContext(), (response), Toast.LENGTH_SHORT).show();
//                                return;
//                            }
//
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                };
//
//                new Response.ErrorListener() {
//
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        dialog.dismiss();
//                        Toast.makeText(getApplicationContext(), (error.getMessage()), Toast.LENGTH_SHORT).show();
//                    }
//                };
//
//                // 서버에서 응답받아오기 , request를 보내고 response를 받아온다.
//                MailRequest mailRequest = new MailRequest(email, responseListener);
//                RequestQueue queue = Volley.newRequestQueue(LoginActivity.this);
//                queue.add(mailRequest);
//
//            }
//        });
    }

};