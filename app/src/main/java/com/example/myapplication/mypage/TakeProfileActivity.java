package com.example.myapplication.mypage;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.myapplication.R;
import com.example.myapplication.cookStargram.WriteActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Calendar;

import static com.example.myapplication.Api.Api.URL_PROFILE_INSERT;

public class TakeProfileActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 1;
    private static final int REQUEST_GALLERY = 200;

    TextView tv_file_name, tv_userID;
    String file_path = null;
    Button btn_upload_file, btn_gallery;
    ProgressBar progressBar;
    ImageView iv_result;
    Bitmap bitmap;

    SharedPreferences prefsName;
    String TAG = "ImgActivity";
    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    private JSONObject jsonObject;
    private RequestQueue requestQueue;
    private JsonObjectRequest jsonObjectRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_img);

        prefsName = getSharedPreferences("NAME", MODE_PRIVATE);


        btn_upload_file = findViewById(R.id.btn_upload_file);
        tv_file_name = findViewById(R.id.tv_file_name);
        iv_result = findViewById(R.id.iv_result);
        progressBar = findViewById(R.id.progress);


        // ??????????????? ?????? ??????????????? ?????????.  -> ????????? ????????????
        btn_gallery = (Button) findViewById(R.id.btn_gallery);
        btn_gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // ?????????
                if (CheckPermission()) {
                    Intent opengallery = new Intent(Intent.ACTION_GET_CONTENT);
                    opengallery.setType("image/*");
                    startActivityForResult(opengallery, 1);
                }
            }
        });


        // ????????? ??????
        btn_upload_file.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


    // ????????? ?????? ??????
    public boolean CheckPermission() {
        if (ContextCompat.checkSelfPermission(TakeProfileActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(TakeProfileActivity.this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(TakeProfileActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(TakeProfileActivity.this,
                    Manifest.permission.READ_EXTERNAL_STORAGE) || ActivityCompat.shouldShowRequestPermissionRationale(TakeProfileActivity.this,
                    Manifest.permission.CAMERA) || ActivityCompat.shouldShowRequestPermissionRationale(TakeProfileActivity.this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                new AlertDialog.Builder(TakeProfileActivity.this)
                        .setTitle("Permission")
                        .setMessage("Please accept the permissions")
                        .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(TakeProfileActivity.this,
                                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                        MY_PERMISSIONS_REQUEST_LOCATION);


                                startActivity(new Intent(TakeProfileActivity.this, WriteActivity.class));
                                TakeProfileActivity.this.overridePendingTransition(0, 0);
                            }
                        })
                        .create()
                        .show();


            } else {
                ActivityCompat.requestPermissions(TakeProfileActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }

            return false;
        } else {

            return true;

        }
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {

            // ??????????????? ???????????? ////////////
            case 1: {
//                if (resultCode == RESULT_OK && getIntent().hasExtra("data")) {
//                    try {
                // ????????? ??????, ???????????? Uri??? filePath??? ??????, ?????? ????????? ????????????.
                if (resultCode == RESULT_OK) {

                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    iv_result.setImageBitmap(bitmap);

                    // ????????? Bitmap ????????? resource??? ????????? ??????.
                    Log.e("getData", String.valueOf(data.getData())); // ???????????? URI????????? ?????????

                    // bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
                    // ?????? ??????????????????
                    UploadImage(bitmap);


                } else if (resultCode == RESULT_CANCELED) {
                    Toast.makeText(this, "?????? ?????? ??????", Toast.LENGTH_SHORT).show();
                }
                break;
            }
        }
    }

    // ????????? ????????? ???????????? // ???????????? String?????? ??????
    private void UploadImage(Bitmap reuduceBitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        reuduceBitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream);

        String image = Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);
        String name = String.valueOf(Calendar.getInstance().getTimeInMillis());

        // ???????????? ????????? userID ?????????
        SharedPreferences prefsName = getSharedPreferences("NAME", MODE_PRIVATE);
        String userID = prefsName.getString("userID", "userID"); //??????, ????????????

//        Log.e("name", name);

        try {
            jsonObject = new JSONObject();
            jsonObject.put("name", name);
            jsonObject.put("image", image);
            jsonObject.put("userID", userID);


            jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL_PROFILE_INSERT, jsonObject,
                    new com.android.volley.Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                String message = response.getString("message");
                                Toast.makeText(TakeProfileActivity.this, "" + message, Toast.LENGTH_SHORT).show();
//                                progressDialog.dismiss();

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(TakeProfileActivity.this, "Image Upload Failed", Toast.LENGTH_SHORT).show();
                }
            }

            );
        } catch (JSONException e) {
            e.printStackTrace();
        }
        // ????????? ????????? ??????
        requestQueue = Volley.newRequestQueue(TakeProfileActivity.this);
        requestQueue.add(jsonObjectRequest);
    }


}


