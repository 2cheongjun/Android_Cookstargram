package com.example.myapplication.login;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.myapplication.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Pattern;

// 회원가입
public class RegisterActivity extends AppCompatActivity {

    // 비밀번호 조건
    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^" +
                    //"(?=.*[0-9])" +         //at least 1 digit
                    //"(?=.*[a-z])" +         //at least 1 lower case letter
                    //"(?=.*[A-Z])" +         //at least 1 upper case letter
                    "(?=.*[a-zA-Z])" +      //any letter
                    "(?=.*[@#$%^&+=])" +    //at least 1 special character
                    "(?=\\S+$)" +           //no white spaces
                    ".{8,}" +               //at least 4 characters
                    "$");


    private EditText et_id, et_pass, et_passCheck, et_email;
    // id중복체크
    private Button btn_validate;
    private AlertDialog dialog;
    private boolean validate = false;

    // 이메일중복체크
    private Button btn_EmailValidate;
    private AlertDialog emailDialog;
    private boolean emailValidate = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        et_id = findViewById(R.id.et_id);
        et_pass = findViewById(R.id.et_pass);
        et_email = findViewById(R.id.et_email);

        // 아이디 중복확인 버튼
        btn_validate = findViewById(R.id.btn_validate);
        btn_validate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userID = et_id.getText().toString();
                if (validate) {
                    return;
                }
                if (userID.equals("")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                    dialog = builder.setMessage("아이디는 빈 칸일 수 없습니다")
                            .setPositiveButton("확인", null)
                            .create();
                    dialog.show();
                    return;
                }
                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            boolean success = jsonResponse.getBoolean("success");
                            if (success) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                                dialog = builder.setMessage("사용가능한 아이디입니다.")
                                        .setPositiveButton("중복확인", null)
                                        .create();
                                dialog.show();
                                et_id.setEnabled(true);
                                validate = false;
//                                btn_validate.setText("확인");
                            } else {
                                AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                                dialog = builder.setMessage("사용할 수 없는 아이디입니다.")
                                        .setNegativeButton("확인", null)
                                        .create();
                                dialog.show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                };
                ValidateRequest validateRequest = new ValidateRequest(userID, responseListener);
                RequestQueue queue = Volley.newRequestQueue(RegisterActivity.this);
                queue.add(validateRequest);
            }
        });


        // 이메일 중복확인 버튼
        btn_EmailValidate = findViewById(R.id.btn_emailValidate);
        btn_EmailValidate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userEmail = et_email.getText().toString();
                if (emailValidate) {
                    return;
                }
                if (userEmail.equals("")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                    dialog = builder.setMessage("이메일은 빈 칸일 수 없습니다")
                            .setPositiveButton("확인", null)
                            .create();
                    emailDialog.show();
                    return;
                }
                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            boolean success = jsonResponse.getBoolean("success");
                            if (success) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                                emailDialog = builder.setMessage("사용할 수 있는 이메일입니다.")
                                        .setPositiveButton("중복확인", null)
                                        .create();
                                emailDialog.show();
                                et_email.setEnabled(true);
                                // true이면 한번확인하고, 버튼 비활성화
                                emailValidate = false;
                                btn_EmailValidate.setText("중복확인");
                            } else {
                                AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                                emailDialog = builder.setMessage("사용할 수 없는 이메일입니다.")
                                        .setNegativeButton("중복확인", null)
                                        .create();
                                emailDialog.show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                };
                ValidateEmailRequest validateEmailRequest = new ValidateEmailRequest(userEmail, responseListener);
                RequestQueue queue = Volley.newRequestQueue(RegisterActivity.this);
                queue.add(validateEmailRequest);

            }
        });

        // 이름, 비번, 이메일 조건 체크
        validateUsername();
        validatePassword();

        validateEmail();



        // 회원가입버튼
        Button btn_register = findViewById(R.id.btn_register);
        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // EditText에 입력되어있는값을 get해온다.
                String userID = et_id.getText().toString();
                String userPassword = et_pass.getText().toString();
//                String userPasswordCheck = et_passCheck.getText().toString();
                String userEmail = et_email.getText().toString();
                 // 문자열을 숫자형으로 바꿔줌
//                int userAge = Integer.parseInt(et_age.getText().toString());

                // 특정요청을 한후에 리스너에서 원하는 결과값을 쓸수 있게 해줌.
                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            // 요청값이 JSON에 담김.
                            JSONObject jsonObject = new JSONObject(response);
                            boolean success = jsonObject.getBoolean("success");
                            // 회원가입성공
//                            if (userPassword.equals(userPasswordCheck)) {
//                            if (userPassword) {
                                if (success) {
                                    Toast.makeText(getApplicationContext(), "회원가입에 성공했습니다.", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                    startActivity(intent);

                            } else { // 회원등록실패

                                Toast.makeText(getApplicationContext(), "회원가입에 실패했습니다.", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                };

                // 버튼클릭시 registerRequest가 실행 -> php로부터 onResponse를 받게되고 -> 회원가입 성공실패
                RegisterRequest registerRequest = new RegisterRequest(userID, userPassword, userEmail, responseListener);
                RequestQueue queue = Volley.newRequestQueue(RegisterActivity.this);
                queue.add(registerRequest);
            }
        });

    }


    // 이름 조건확인 함수
    private boolean validateUsername() {
        String usernameInput = et_id.getText().toString().trim();

        if (usernameInput.length() > 15) {
            et_id.setError("15자 이내로 작성해주세요.");
            return false;
        } else {
            et_id.setError(null);
            return true;
        }
    }

    // 비밀번호 조건확인
    private boolean validatePassword() {
        String passwordInput = et_pass.getText().toString().trim();

        if (!PASSWORD_PATTERN.matcher(passwordInput).matches()) {
            et_pass.setError("영문,숫자,특수문자를 포함한 8자리이상");
            return false;
        } else {
            et_pass.setError(null);
            return true;
        }
    }

    // 비밀번호가 위아래 동일 한지 체크
//    private boolean validatePasswordCheck() {
//        String userPassword = et_pass.getText().toString();
//        String userPasswordCheck = et_passCheck.getText().toString().trim();
//
//        if (userPassword.equals(userPasswordCheck)) {
//            et_passCheck.setError("비밀번호가 일치하지 않습니다.");
//            return false;
//        } else {
//            et_passCheck.setError(null);
//            return true;
//        }
//    }

    // 이메일 형식체크
    private boolean validateEmail() {
        String emailInput = et_email.getText().toString().trim();
        if (emailInput.isEmpty()) {
            et_email.setError("이메일주소를 작성해주세요.");
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(emailInput).matches()) {
            et_email.setError("올바른이메일 주소를 입력해주세요.");
            return false;
        } else {
            et_email.setError(null);
            return true;
        }
    }
}
