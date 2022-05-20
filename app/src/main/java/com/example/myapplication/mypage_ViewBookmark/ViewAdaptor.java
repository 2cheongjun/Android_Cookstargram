package com.example.myapplication.mypage_ViewBookmark;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
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

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;
import static com.example.myapplication.Api.Api.BASE_URL;
import static com.example.myapplication.Api.Api.URL_BOOKMARK;
import static com.example.myapplication.Api.Api.URL_COOK_POST_LIKE;

public class ViewAdaptor extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    /*
      어댑터의 동작원리 및 순서
      1.(getItemCount) 데이터 개수를 세어 어댑터가 만들어야 할 총 아이템 개수를 얻는다.
      2.(getItemViewType)[생략가능] 현재 itemview의 viewtype을 판단한다
      3.(onCreateViewHolder)viewtype에 맞는 뷰 홀더를 생성하여 onBindViewHolder에 전달한다.
      4.(onBindViewHolder)뷰홀더와 position을 받아 postion에 맞는 데이터를 뷰홀더의 뷰들에 바인딩한다.
      */
    String TAG = "RecyclerViewAdapter";
    //리사이클러뷰에 넣을 데이터 리스트
    public Context mContext;
    private ArrayList<ViewDataModel> mDatalist; // 북마크 각아이템 데이터
    private int position;
    // 하트에니메이션
    boolean isheartChecked = false;
    private static final int RESULT_OKK = 1;
    // 삭제할때
    int temp;

    // 좋아요 요청
    private JsonObjectRequest jsonObjectRequest;

    //생성자를 통하여 데이터 리스트 context를 받음
    public ViewAdaptor(Context context, ArrayList<ViewDataModel> datalist) {
        mContext = context;
        mDatalist = datalist;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder");

        //자신이 만든 itemview를 inflate한 다음 뷰홀더 생성
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.bookmarkview_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);

        //생선된 뷰홀더를 리턴하여 onBindViewHolder에 전달한다.
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder");

        ViewHolder mHolder = (ViewHolder) holder;
        // 데이터 연결부분
        ViewDataModel datalist = mDatalist.get(position);

        String imgPath = datalist.getImgPath();
        String userID = datalist.getUserID();
        String postText = datalist.getPostText();

        int postIdx = datalist.getPostIdx();
        mHolder.mTextViewUserID.setText(userID);
        String postImg = datalist.getPostImg();

        int heartCount = datalist.getHeartCount();
        boolean cb_heart = datalist.isCb_heart();
        boolean cb_bookmark = datalist.isCb_bookmark();

        // 이미지 URI가져오기
        Uri url = Uri.parse(BASE_URL + "/img/" + postImg);

        Glide.with(mHolder.itemView)
                .load(url)
                .skipMemoryCache(true)
                .override(400, 400)
                .centerCrop()
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .error(android.R.drawable.ic_menu_gallery)
                .placeholder(android.R.drawable.ic_menu_gallery)
                .into(mHolder.mpost_image_view);

        mHolder.mPostText.setText(postText);
        mHolder.mtv_username.setText(userID);
        // 하트수
        mHolder.mTextViewHeartCount.setText("좋아요" + heartCount + "개");
        // 하트 버튼을 눌렀느냐? // boolean cb_heart = currentItem.isCb_heart();
        // 하트버튼 리스너
        mHolder.cb_heart.setOnCheckedChangeListener(null);
        mHolder.cb_heart.setChecked(cb_heart);

        // 북마크 리스너
        mHolder.cb_bookmark.setOnCheckedChangeListener(null);
        mHolder.cb_bookmark.setChecked(cb_bookmark);

        mHolder.cb_bookmark.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // 셰어드에 저장된 userID 가져옴
                SharedPreferences prefsName = mContext.getSharedPreferences("NAME", MODE_PRIVATE);
                String userID = prefsName.getString("userID", "userID"); //키값, 디폴트값

                try {
                    JSONObject jsonObject = new JSONObject();
                    // 사용자아이디
                    jsonObject.put("userID", userID);

                    ViewDataModel datalist = mDatalist.get(position);
                    // 글번호
                    jsonObject.put("postIdx", datalist.getPostIdx());

                    // 북마크 유무 true, false  //TODO
                    if (isChecked) {
                        jsonObject.put("cb_bookmark", true);
                        Log.e("lee", "북마크서버 업로드 : " + isChecked);

                    } else {
                        jsonObject.put("cb_bookmark", false);
                        Log.e("lee", "북마크서버 업로드 : " + isChecked);
                    }

                    jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL_BOOKMARK, jsonObject,
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    try {
                                        String success = response.getString("success");

                                        {
                                            String message = response.getString("message");
                                            Toast.makeText(mContext.getApplicationContext(), "북마크"+message, Toast.LENGTH_SHORT).show();
                                        }

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(mContext.getApplicationContext(), "Image Upload Failed", Toast.LENGTH_SHORT).show();
                        }
                    }

                    );
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                // 이미지 업로드 볼리

                RequestQueue requestQueue = Volley.newRequestQueue(mContext.getApplicationContext());
                requestQueue.add(jsonObjectRequest);

                // 연동후 북마크가 체크되어있는지 아닌지 체크한다.
                mDatalist.get(position).setCb_bookmark(isChecked);

                // 체크변화후 데이터 갱신
                notifyItemInserted(position);
            }
        });


        // 체크가 바뀌면 서버로 보내기?
        mHolder.cb_heart.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                // 셰어드에 저장된 userID 가져옴
                SharedPreferences prefsName = mContext.getSharedPreferences("NAME", MODE_PRIVATE);
                String userID = prefsName.getString("userID", "userID"); //키값, 디폴트값

                try {
                    JSONObject jsonObject = new JSONObject();
                    // 사용자아이디
                    jsonObject.put("userID", userID);

                    ViewDataModel datalist = mDatalist.get(position);
                    // 글번호
                    jsonObject.put("postIdx", datalist.getPostIdx());

                    // 좋아요 유무 true, false  //TODO
                    if (isChecked) {
                        jsonObject.put("cb_heart", true);
                        Log.e("lee", "좋아요 서버 업로드 : " + isChecked);

                        // 좋아오가 눌렸는지 안눌렸는지 확인은 어떻게?
                        // ♡ 좋아요를 처음누른다.
                        // 안눌려있으면,0
                        if(!isheartChecked){
                            int heartplus = heartCount + 1;
                            mHolder.mTextViewHeartCount.setText("좋아요" + heartplus + "개");
                            isheartChecked = false;
                            // 눌려있으면,
                        }else {
                            if (heartCount > 0) {
                                int heartminus = heartCount - 1;
                                mHolder.mTextViewHeartCount.setText("좋아요" + heartminus + "개");
                            }
                            mHolder.mTextViewHeartCount.setText("좋아요" + 0 + "개");

                            isheartChecked = false;
                        }

                    } else {
                        jsonObject.put("cb_heart", false);
                        Log.e("lee", "좋아요 서버 업로드 : " + isChecked);

                        if(isheartChecked){
                            int heartplus = heartCount + 1;
                            mHolder.mTextViewHeartCount.setText("좋아요" + heartplus + "개");
                            isheartChecked = false;
                        }else{
//                            int heartplus = heartCount - 1;
                            if(heartCount >0){
                                int heartminus = heartCount - 1;
                                mHolder.mTextViewHeartCount.setText("좋아요" + heartminus + "개");
                            }
                            mHolder.mTextViewHeartCount.setText("좋아요" + 0 + "개");

                            isheartChecked = false;

                        }
                    }
                    jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL_COOK_POST_LIKE, jsonObject,
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    try {
                                        String success = response.getString("success");

                                        if (true) {
                                            String message = response.getString("message");
//                                            Toast.makeText(mContext.getApplicationContext(), "좋아요" + message, Toast.LENGTH_SHORT).show();
                                        }

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(mContext.getApplicationContext(), "좋아요실패", Toast.LENGTH_SHORT).show();
                        }
                    }

                    );
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                // 이미지 업로드 볼리


                RequestQueue requestQueue = Volley.newRequestQueue(mContext.getApplicationContext());
                requestQueue.add(jsonObjectRequest);

                // false 이면  Delete

                // 연동후 좋아요가 체크되어있는지 아닌지 체크한다.
                mDatalist.get(position).setCb_heart(isChecked);

            }
        });


    }


    @Override
    public int getItemCount() {
        //데이터 리스트의 크기를 전달해주어야 함

        return (mDatalist !=null ? mDatalist.size(): 0);
    }

    // 포스트 삭제하기
    public void delete(int position) {
        mDatalist.remove(position);

        this.notifyDataSetChanged();

    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, PopupMenu.OnMenuItemClickListener {
        private static final String TAG = "MYViewHolder";
        // 뷰 홀더 내에 들어가는 뷰 객체 선언
        public ImageView mpost_image_view;
        public TextView mTextViewUserID;
        public TextView mPostText;

        public ImageView mMore;
        public TextView mtv_username;
        public TextView mTextViewHeartCount;
        public CheckBox cb_heart;
        // public TextView mTextViewBookmark;
        public CheckBox cb_bookmark;
        private String user_id;
        private LottieAnimationView animationView;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mpost_image_view = itemView.findViewById(R.id.post_image_view);
            mTextViewUserID = itemView.findViewById(R.id.text_view_creator);
            mPostText = itemView.findViewById(R.id.tv_comment);
            mtv_username = itemView.findViewById(R.id.tv_username);
            // 북마크
            cb_bookmark = itemView.findViewById(R.id.cb_bookmark);
            // 더보기
            mMore = itemView.findViewById(R.id.iv_more);
            mMore.setOnClickListener((View.OnClickListener) this);
            // 하트 수
            mTextViewHeartCount = itemView.findViewById(R.id.tv_heartCount);
            // 하트 버튼
            cb_heart = itemView.findViewById(R.id.cb_heart);
        }

        @Override
        public void onClick(View v) {
            showPopupMenu(v);
        }

        // 팝업 메뉴가 뜸
        private void showPopupMenu(View view) {
            // 셰어드에 저장된 userID 가져옴 // 지금 로그인한 아이디 OK
            SharedPreferences prefsName = mContext.getSharedPreferences("NAME", MODE_PRIVATE);
            String result = prefsName.getString("userID", "userID"); //키값, 디폴트값

            // 게시글번호 // position으로 하면 다른 값이 가져와졌음.
            ViewDataModel datalist  =mDatalist.get(getAdapterPosition());
            String writer = datalist.getUserID();
            Log.e("result", result + writer);

            PopupMenu popupMenu = new PopupMenu(view.getContext(), view);
            popupMenu.inflate(R.menu.popup_menu);
            popupMenu.setOnMenuItemClickListener(this);
            if (result.equals(writer)) {
            popupMenu.show();
            } else {
                Toast.makeText(mContext, "작성자가 아닙니다.", Toast.LENGTH_SHORT).show();
            }
        }

        // 수정, 삭제 해결하기 @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
        @SuppressLint("NonConstantResourceId")
        @Override
        public boolean onMenuItemClick(MenuItem item) {

            switch (item.getItemId()) {
                case R.id.itemModify:
                    Log.e(TAG, "onMenuItemClick : 수정하기" + getAdapterPosition());
                    // 수정하기

                    // 아이템의 내용을 mCookList에서 가져와 cookItem에 담아 인텐트로 전달.
//                    CookItem cookItem = mCookList.get(getAdapterPosition());
                    ViewDataModel datalist = (ViewDataModel)mDatalist.get(getAdapterPosition());
                    // updateActivity로 인텐트값 전달
                    Intent intent = new Intent(mContext,ViewUpdateActivity.class);
                    intent.putExtra("actionType", "Update");
                    intent.putExtra("data", datalist);
                    mContext.startActivity(intent);

                    return true;

                case R.id.itemDelete:
                    // 삭제하기 // 포스트 번호의 위치값을 가져와 삭제한다. // 맨위의 게시물만 삭제가 됨
                    ViewDataModel datalist2 = (ViewDataModel)mDatalist.get(getAdapterPosition());
                    Intent deleteIntent = new Intent(mContext, ViewDeleteActivity.class);
                    deleteIntent.putExtra("actionType", "Delete");
                    deleteIntent.putExtra("data",  datalist2);
                    mContext.startActivity(deleteIntent);

                    // 삭제하기
                    delete(position);
                    temp = (mDatalist.get(getAdapterPosition()).getPostIdx());


                    Log.e(TAG, "onMenuItemClick : 삭제하기" + getAdapterPosition());
                    return true;

//                case R.id.itemShare:
//                    Log.e(TAG, "onMenuItemClick : 취소하기" + getAdapterPosition());
//                    return true;

                default:
                    break;
            }
            return false;
        }
    }


}