package com.example.myapplication.cookStargram;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.myapplication.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static com.example.myapplication.Api.Api.BASE_URL;
import static com.example.myapplication.Api.Api.URL_COOK_POST_UPDATE;

public class UpdateActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_UPDATE = 102;

    // 갤러리에서 가져오기 버튼
    private ImageView iv_select;
    // 사진찍기버튼
    private Button btn_capture;
    private ImageView iv_image_first;
    private ImageView ic_back;
    private EditText et_enter_text;
    private TextView tv_share;
    private Button btn_galley;
    private TextView tv_upload;


    private JSONObject jsonObject;
    private RequestQueue requestQueue;
    private JsonObjectRequest jsonObjectRequest;

    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private static final int REQUEST_GALLERY = 200;
    int PICK_IMAGE_REQUEST = 111;
    private ProgressDialog progressDialog;

    // 인텐트 값을 가져오기 위한 구분자
    private String actionType = "";
    private CookItem cookItem = null;

    private String imageFilePath;
    private Uri photoUri;
    Bitmap bitmap;
    private static final int RESULT_OKK = 1;
    RequestQueue queue;

    Bitmap image;
    Bitmap resized;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);

//        // TODO : Cookadapter에서 받아온 인텐트
        if (getIntent() != null) {
            actionType = getIntent().getStringExtra("actionType");
            cookItem = (CookItem) getIntent().getSerializableExtra("item");
        }


        //  이전에 작성했던글 가져오기
        et_enter_text = (EditText) findViewById(R.id.et_enter_text);
        if (et_enter_text != null) {
            et_enter_text.setText(cookItem.getPostText());
        } else {
            et_enter_text.setText("");
        }

        // 원래 있던 이미지 가져옴
        iv_image_first = (ImageView) findViewById(R.id.iv_image_first);
        String postImg = cookItem.getPostImg();

        Uri url = Uri.parse(BASE_URL + "/img/" + postImg);

        Glide.with(this)
                .load(url)
                .skipMemoryCache(true)
                .override(1000, 1000)
                .error(android.R.drawable.ic_menu_gallery)
                .placeholder(android.R.drawable.ic_menu_gallery)
                .centerCrop()
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(iv_image_first);


        // 뒤로 가기버튼
        ic_back = findViewById(R.id.ic_back);
        ic_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
            }
        });

        // 진행률 다이얼로그
        progressDialog = new ProgressDialog(UpdateActivity.this);
        progressDialog.setMessage("이미지 업로드성공");

        // 사진찍기 버튼
        btn_capture = findViewById(R.id.btn_capture);
        btn_capture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendTakePhotoIntent();
            }
        });
        // 갤러리에서 가져오기
        btn_galley = findViewById(R.id.btn_galley);
        btn_galley.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                // 갤러리
                if (CheckPermission()) {
                    Intent opengallery = new Intent(Intent.ACTION_GET_CONTENT);
                    opengallery.setType("image/*");
                    startActivityForResult(opengallery, 1);
                }
            }
        });

        // 가져온 이미지 보이기
        iv_image_first = findViewById(R.id.iv_image_first);


        // 공유버튼을 누르면, 서버로 전송 하기
        tv_share = findViewById(R.id.tv_upload);
        tv_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // 입력된 글내용 가져오기
                String postText = cookItem.getPostText();

                // userID 가져옴
                SharedPreferences prefsName = getSharedPreferences("NAME", MODE_PRIVATE);
                String userId = prefsName.getString("userID", "userID"); //키값, 디폴트값
                String postImg = prefsName.getString("postImg", "postImg"); //키값, 디폴트값
                try {
                    postImg = jsonObject.getString("postImg");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Log.e("postImg", postImg);

                // 서버에서 응답받아오기 , request를 보내고 response를 받아온다.

                UpdateRequest updateRequest = new UpdateRequest(userId, postText, postImg, UpdateResponseListener); // 서버에 데이터 보내는 부분

                if (queue == null) {
                    queue = Volley.newRequestQueue(UpdateActivity.this);
                }
                queue.add(updateRequest);

                // 서버 볼리 통신 // 데이터를 넣기, 받아오기 안해도 됨
            }
        });

        //
        // 업로드 버튼은 닫기
        tv_upload = findViewById(R.id.tv_upload);
        tv_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int update = 1;
                // 셰어드에 값저장
                SharedPreferences pref = getSharedPreferences("UPDATE", MODE_PRIVATE);
                SharedPreferences.Editor editor = pref.edit();
                editor.putInt("update", update); //키값, 저장값
                editor.apply();
                Log.e("셰어드저장", update + "");

                // 번들
//                int update = 1;
//                Fragment fragment = new Fragment();
//                Bundle bundle = new Bundle();
//                bundle.putInt("update", update);
//                fragment.setArguments(bundle);
                finish();
            }
        });
    }


    // 사진찍기 메서드
    private void sendTakePhotoIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }

            // 파일의 URI를 가져온다.
            if (photoFile != null) {
                photoUri = FileProvider.getUriForFile(this, getPackageName(), photoFile);
                // URI를 같이 onActivityResult로 보냄.
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                startActivityForResult(takePictureIntent, 0);
            }
        }
    }

    // 파일변환하는 메서드
    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "TEST_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = null;
        try {
            image = File.createTempFile(
                    imageFileName,      /* prefix */
                    ".jpg",         /* suffix */
                    storageDir          /* directory */
            );
        } catch (IOException e) {
            e.printStackTrace();
        }
        imageFilePath = image.getAbsolutePath();
        return image;
    }

    // 이미지비율설정
    private float getSampleRatio(int width, int height) {
        // 바꿀사이즈
        final int targetWidth = 1280;
        final int targetHeight = 1280;

        float ratio;

        // 가로사진
        if (width > height) {
            // Landscape
            if (width > targetWidth) {
                ratio = (float) width / (float) targetWidth;

            } else ratio = 1f;
            // 세로사진일경우
        } else {
            // Portrait
            if (height > targetHeight) {
                ratio = (float) height / (float) targetHeight;
            } else ratio = 1f;
        }

        // 나오는 비율 반올림해주기
        return Math.round(ratio);
    }


    private int exifOrientationToDegrees(int exifOrientation) {
        if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) {
            return 90;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {
            return 180;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {
            return 270;
        }
        return 0;
    }

    private Bitmap rotate(Bitmap bitmap, float degree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }


    // 카메라 사진찍기
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            // 사진찍기
            case 0: {
                // 촬영한 사진 가져와 썸네일 띄우기
                if (resultCode == RESULT_OK) {
//                    Bitmap bitmap = (Bitmap) data.getExtras().get("data");


                    // 1. 사진찍고 가져온 URI -> 미리보기 보여주기용
                    iv_image_first.setImageURI(photoUri);

                    // 2. 사진찍고 가져온 비트맵 파일
                    Bitmap bitmap = BitmapFactory.decodeFile(imageFilePath);
//
                    ExifInterface exif = null;
                    try {
                        exif = new ExifInterface(imageFilePath);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    int exifOrientation;
                    int exifDegree;

                    if (exif != null) {
                        exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                        exifDegree = exifOrientationToDegrees(exifOrientation);
                    } else {
                        exifDegree = 0;
                    }

                    // 미리보기시에 이미지회전이 필요할때, 글라이드로 하면 상관없음
                    // iv_image_first.setImageBitmap(rotate(bitmap, exifDegree));

                    // 이미지 줄이기

                    // URI를 비트맵으로 변환해 서버에 업로드
                    // 가져온 URI를 비트맵으로 바꿔서 업로드로 보내기
                    // 업로드는 비트맵으로  회전 적용해주기
                    UploadImage(rotate(bitmap, exifDegree));
                }
                break;
            }

            // 갤러리에서 가져오기
            case 1: {
                if (resultCode == RESULT_OK) {

                    try {

                        bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
                        iv_image_first.setImageBitmap(rotateImage(bitmap, 90));

                    } catch (IOException e) {

                        e.printStackTrace();
                    }
                    // 얻어낸 Bitmap 자원을 resource를 통하여 접근.
                    Log.e("getData", String.valueOf(data.getData())); // 데이터를 URI값으로 가져옴


                    try {
                        image = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    // 이미지 간단히 줄이기

                    int width = 1000; // 축소시킬 너비
                    int height = 1778; // 축소시킬 높이
                    float bmpWidth =  image.getWidth();
                    float bmpHeight =  image.getHeight();

                    if (bmpWidth > width) {
                        // 원하는 너비보다 클 경우의 설정
                        float mWidth = bmpWidth / 100;
                        float scale = width/ mWidth;
                        bmpWidth *= (scale / 100);
                        bmpHeight *= (scale / 100);
                    } else if (bmpHeight > height) {
                        // 원하는 높이보다 클 경우의 설정
                        float mHeight = bmpHeight / 100;
                        float scale = height/ mHeight;
                        bmpWidth *= (scale / 100);
                        bmpHeight *= (scale / 100);
                    }

                    Bitmap resizedBmp = Bitmap.createScaledBitmap( image, (int) bmpWidth, (int) bmpHeight, true);


                    // 이미지업로드
                    UploadImage(rotateImage(resizedBmp, 90));


                }
            }
        }
    }

    public static Bitmap rotateImage(Bitmap src, float degree) {
        // create new matrix
        Matrix matrix = new Matrix();
        // setup rotation degree
        matrix.postRotate(degree);
        Bitmap bmp = Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), matrix, true);
        return bmp;
    }


    // php에서 Response가 건너오면 그것을 받고 사용할수 있게 해줌.
    Response.Listener<String> UpdateResponseListener = new Response.Listener<String>() { // 통신이 완료된후 다음 작업 // 데이터 받는부분
        @Override
        public void onResponse(String response) {
            try {
                // json으로 데이터 받기
                JSONObject jsonObject = new JSONObject(response);
                // php에서 사용했던 $response["success"] = false; /success라는 변수를
                // true값으로 만들어줌.
                boolean success = jsonObject.getBoolean("success");

                // 글작성 성공
                if (success) {
//                    String userId = jsonObject.getString("userId");

                    Toast.makeText(getApplicationContext(), "글 작성완료", Toast.LENGTH_SHORT).show();
                    finish();

                } else { // 로그인 실패
                    Toast.makeText(getApplicationContext(), "글 작성실패", Toast.LENGTH_SHORT).show();
                    return;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };


    // 서버로 보내는부분
    private void UploadImage(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        String image = Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);
        String name = String.valueOf(Calendar.getInstance().getTimeInMillis());


        // 입력된 글내용 가져오기
        String postText = et_enter_text.getText().toString();

        int postIdx = cookItem.getPostIdx();

        String userID = cookItem.getUserID();


        try {
            jsonObject = new JSONObject();
            jsonObject.put("name", name);
            jsonObject.put("image", image);
            jsonObject.put("userID", userID);
            jsonObject.put("postText", postText);
            jsonObject.put("postIdx", postIdx);

//            jsonObject.put("postText", postIdx);

//            Log.e("lee", String.valueOf(postIdx));
            Log.e("lee", postText);
            Log.e("lee", userID);


            jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL_COOK_POST_UPDATE, jsonObject,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                String message = response.getString("message");
                                Toast.makeText(UpdateActivity.this, "success" + message, Toast.LENGTH_SHORT).show();
//

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(UpdateActivity.this, "Image Upload Failed", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            }

            );
        } catch (JSONException e) {
            e.printStackTrace();
        }
        // 이미지 업로드 볼리
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(UpdateActivity.this);
        }
        requestQueue.add(jsonObjectRequest);

    }

    // 카메라 권한 허용
    public boolean CheckPermission() {
        if (ContextCompat.checkSelfPermission(UpdateActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(UpdateActivity.this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(UpdateActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(UpdateActivity.this,
                    Manifest.permission.READ_EXTERNAL_STORAGE) || ActivityCompat.shouldShowRequestPermissionRationale(UpdateActivity.this,
                    Manifest.permission.CAMERA) || ActivityCompat.shouldShowRequestPermissionRationale(UpdateActivity.this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                new AlertDialog.Builder(UpdateActivity.this)
                        .setTitle("Permission")
                        .setMessage("Please accept the permissions")
                        .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(UpdateActivity.this,
                                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                        MY_PERMISSIONS_REQUEST_LOCATION);


                                startActivity(new Intent(UpdateActivity.this, WriteActivity.class));
                                UpdateActivity.this.overridePendingTransition(0, 0);
                            }
                        })
                        .create()
                        .show();


            } else {
                ActivityCompat.requestPermissions(UpdateActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }

            return false;
        } else {

            return true;

        }
    }


}

