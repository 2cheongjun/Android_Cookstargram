package com.example.myapplication.cookStargram;

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

import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.myapplication.R;
import com.example.myapplication.cookStargram_reply.ReplyViewActivity;
import com.example.myapplication.mypageFriend.FriendInfoActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;
import static com.example.myapplication.Api.Api.BASE_URL;
import static com.example.myapplication.Api.Api.URL_COOK_POST_LIKE;

// public class 어뎁터 이름 extends RecyclerView.Adapter< 어뎁터이름, 뷰홀더 이름 >
public class CookAdapter extends RecyclerView.Adapter<CookAdapter.CookViewHolder> {
    private static final Object REQUEST_CODE_UPDATE = 102;
    private static final String TAG = "쿡포스트어답터";
    private Context mContext;
    private ArrayList<CookItem> mCookList;// 포스트 하나 아이템
    private int position;
    // 하트 체크
    boolean isheartChecked = false;
    private static final int RESULT_OKK = 1;

    boolean cb_heart = false;
    int temp;


    // 좋아요 요청
    private JsonObjectRequest jsonObjectRequest;
    RequestQueue requestQueue;

    // 북마크 인터페이스 등록
    BookMarkClickListener bookMarkClickListener;

    // 북마크 인터페이스 등록
    public interface BookMarkClickListener {
        public void bookMarkClickListener(boolean isChecked, int postIdx);
    }

    public void setBookMarkClickListener(BookMarkClickListener bookMarkClickListener) {
        this.bookMarkClickListener = bookMarkClickListener;
    }


    // ... 인터페이스
    public interface OnItemClickListener {
        public void onItemClick(RecyclerView.ViewHolder holder, View view, int position);
    }

    // 리스너 객체 참조를 저장하는 변수
    private OnItemClickListener mListener = null;

    // OnItemClickListener 리스너 객체 참조를 어댑터에 전달하는 메서드
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener;
    }

    //
    public CookAdapter(Context context, ArrayList<CookItem> cookList) {
        mContext = context;
        mCookList = cookList;

    }

    // 뷰홀더를 새로 만들어야 할때 호출한다. 연결된 뷰 생성 및 초기화 역할
    // 뷰 홀더가 만들어지는 지점에서 호출되는 메소드. // 메모리 관리를 강제하는 역할을 수행하기도 함.
    @Override
    public CookViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // inflate 권한을 생성할 때 부터 받음
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.cook_item, parent, false);
        return new CookViewHolder(itemView);
    }


    // 뷰홀더를 재활용하고, 뷰 내부의 데이터만 갈아끼운다. // 클릭리스터 달기
    @Override
    public void onBindViewHolder(CookViewHolder holder, int position) {
        // 각 위치값을 가져와, currentItem에 담는다.// final로 선언해야 체크박스(T/F)이 바뀌지 않는다.
        final CookItem currentItem = mCookList.get(position);
        String userID = currentItem.getUserID();
        String postText = currentItem.getPostText();
        int postIdx = currentItem.getPostIdx();
        String postImg = currentItem.getPostImg();
        int heartCount = currentItem.getHeartCount();
        boolean cb_heart = currentItem.isCb_heart();
        boolean cb_bookmark = currentItem.isCb_bookmark();
        String replySum =currentItem.getReplySum();
        String otherUser = currentItem.getOtherUser();
        String otherUserText = currentItem.getOtherUserText();
        String imgPath = currentItem.getImgPath(); // 게시글작성사 프사
//        String postTime = currentItem.getPostTime(); // 작성시간 서버에서 가져오기

        // 댓글이 0일 경우
        if(replySum.equals("0")){
            holder.mtv_otherUser.setVisibility(View.GONE);
            holder.mtv_otherUserText.setVisibility(View.GONE);
            holder.mtv_replySumText.setVisibility(View.GONE);
        }


        // 포스트 이미지 URI가져오기
        Uri url = Uri.parse(BASE_URL + "/img/" + postImg);
        Log.e(TAG, "url /" + url);
        Glide.with(holder.itemView)
                .load(url)
                .skipMemoryCache(true)
                .override(1000, 1000)
                .centerCrop()
                .fitCenter() // 이미지뷰의 크기에 맞추기
                .skipMemoryCache(true)
                .error(android.R.drawable.ic_menu_gallery)
                .placeholder(R.drawable.ic_baseline_food_bank_24)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(holder.post_image_view);

        // 프로필사진 이미지
        Uri profileUrl = Uri.parse(BASE_URL + "/upload/"+ imgPath);
        Glide.with(holder.itemView)
                .load(profileUrl)
                .skipMemoryCache(true)
                .override(100, 100)
                .centerCrop()
                .circleCrop()
//                .fitCenter() // 이미지뷰의 크기에 맞추기
                .skipMemoryCache(true)
                .error(R.drawable.cooker_man) // 오류시
                .placeholder(R.drawable.cooker_man)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(holder.miv_userImg);

        holder.mTextViewUserID.setText(userID);
        holder.mPostText.setText(postText);
        holder.mtv_username.setText(userID);
        // 하트수
        holder.mTextViewHeartCount.setText("좋아요" + heartCount + "개");
        // 댓글 수
        holder.mtv_replySumText.setText("댓글" + replySum + "개 모두 보기");
        holder.mtv_otherUser.setText(otherUser);
        holder.mtv_otherUserText.setText(otherUserText);


        // final로 선언해야 체크박스의 체크 상태값(T/F)이 바뀌지 않는다./////////////////////////////////////////////////
        // TODO 북마크
        // 북마크 리스너 // 경우에 따라 원치 않는 상황을 방지합니다.
//        holder.cb_bookmark.setOnCheckedChangeListener(null);
        // true이면 체크박스가 선택되고, 그렇지 않으면 선택되지 않음
        holder.cb_bookmark.setChecked(cb_bookmark);

        //      북마크
        holder.cb_bookmark.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (bookMarkClickListener != null) {
                    bookMarkClickListener.bookMarkClickListener(isChecked, currentItem.getPostIdx());
                    mCookList.get(position).setCb_bookmark(isChecked);
                }
            }
        });

        holder.cb_heart.setOnCheckedChangeListener(null);
        Log.e("heart", cb_heart + "");
        holder.cb_heart.setChecked(cb_heart);
        // 하트 체크가 바뀌면 서버로 보내기?
        holder.cb_heart.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                // 좋아요가 true값이면 insert

                // 셰어드에 저장된 userID 가져옴
                SharedPreferences prefsName = mContext.getSharedPreferences("NAME", MODE_PRIVATE);
                String userID = prefsName.getString("userID", "userID"); //키값, 디폴트값


                try {
                    JSONObject jsonObject = new JSONObject();
                    // 사용자아이디
                    jsonObject.put("userID", userID);

                    CookItem cookItem = mCookList.get(position);
                    // 글번호
                    jsonObject.put("postIdx", cookItem.getPostIdx());

                    // 좋아요 유무 true, false

                    // 하트ON
                    if (isChecked) {
                        jsonObject.put("cb_heart", true);
                        Log.e("lee", "좋아요 서버 업로드 : " + isChecked);

                        // 좋아오가 눌렸는지 안눌렸는지 확인은 어떻게?
                        // ♡ 좋아요를 처음누른다.
                        // 안눌려있으면,0
                        if (!isheartChecked) {
                            int heartplus = heartCount + 1;
                            holder.mTextViewHeartCount.setText("좋아요" + heartplus + "개");
                            isheartChecked = false;
                            // 눌려있으면,
                        } else {
                            if (heartCount > 0) {
                                int heartminus = heartCount - 1;
                                holder.mTextViewHeartCount.setText("좋아요" + heartminus + "개");
                            }
                            holder.mTextViewHeartCount.setText("좋아요" + 0 + "개");

                            isheartChecked = false;
                        }
                        // 하트OFF
                    } else {
                        jsonObject.put("cb_heart", false);
                        Log.e("lee", "좋아요 서버 업로드 : " + isChecked);
                        // ♡ 좋아요를 처음누른다.
                        // 안눌려져있으면 0
                        if (isheartChecked) {
                            int heartplus = heartCount + 1;
                            holder.mTextViewHeartCount.setText("좋아요" + heartplus + "개");
                            isheartChecked = false;
                        } else {
//                            int heartplus = heartCount - 1;
                            if (heartCount > 0) {
                                int heartminus = heartCount - 1;
                                holder.mTextViewHeartCount.setText("좋아요" + heartminus + "개");
                            }
                            holder.mTextViewHeartCount.setText("좋아요" + 0 + "개");

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
                                            Log.e("좋아요", success + message);
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
                if (requestQueue == null) {
                    requestQueue = Volley.newRequestQueue(mContext.getApplicationContext());
                    requestQueue.add(jsonObjectRequest);
                }


                // false 이면  Delete

                // 연동후 좋아요가 체크되어있는지 아닌지 체크한다.
                mCookList.get(position).setCb_heart(isChecked);
                Log.e("북마크", cb_heart + "" + "연동후");
                // 체크변화후 데이터 갱신
//                notifyItemInserted(position);
                Log.e("북마크", position + "" + "번째 글 데이터 갱신");
                Log.e("북마크", "좋아요 서버 업로드 : " + isChecked);
            }
        });


    }


    // 가져온 이미지가 돌려있으니, 다시 회전을 시켜서 보여줘야함
    // 데이터 세트의 크기를 가져올때 사용한다.
    @Override
    public int getItemCount() {
        // 삼항연산자 arrayList 가 null이면 ~ 아니면 ~해라.
        return (mCookList != null ? mCookList.size() : 0);
    }




    // 포스트 삭제하기
    public void delete(int position) {
        mCookList.remove(position);
        notifyItemRemoved(position);

        this.notifyDataSetChanged();
    }


    // 뷰홀더 생성자 // 쿡뷰홀더안에 온클릭
    public class CookViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, PopupMenu.OnMenuItemClickListener {
        private static final String TAG = "MYViewHolder";
        // 뷰 홀더 내에 들어가는 뷰 객체 선언
        public ImageView post_image_view;
        public TextView mTextViewUserID;
        public TextView mPostText;
        public ImageView mMore;
        public TextView mtv_username;
        public TextView mTextViewHeartCount;
        public CheckBox cb_heart;
        public CheckBox cb_bookmark;
        private String user_id;
        // 작성자 프사
        public ImageView miv_userImg;
        // 댓글상세 아이콘
        public ImageView mtv_reply;
        // 자식 리사이클러뷰
        public TextView mtv_replySumText;
        // 다른 유저1
        public TextView mtv_otherUser;
        // 다른유저가 댓글1
        public TextView mtv_otherUserText;


        // 객체 찾아오기
        public CookViewHolder(View itemView) {
            super(itemView);
            // 이미지
            post_image_view = itemView.findViewById(R.id.post_image_view);
            // 작성자
            mTextViewUserID = itemView.findViewById(R.id.text_view_creator);
            mPostText = itemView.findViewById(R.id.tv_comment);
            mtv_username = itemView.findViewById(R.id.tv_username);
            // 프사
            miv_userImg = itemView.findViewById(R.id.iv_userImg);

            // 댓글작성자1
            mtv_otherUser= itemView.findViewById(R.id.tv_otherUser);
            mtv_otherUserText=itemView.findViewById(R.id.tv_otherUserText);


            // 북마크
            cb_bookmark = itemView.findViewById(R.id.cb_bookmark);
            // ... 더보기
            mMore = itemView.findViewById(R.id.iv_more);
            mMore.setOnClickListener(this);

            // 하트 수
            mTextViewHeartCount = itemView.findViewById(R.id.tv_heartCount);
            // 하트 버튼
            cb_heart = itemView.findViewById(R.id.cb_heart);
            // 댓글 아이콘
            mtv_reply = itemView.findViewById(R.id.tv_reply);
            // 댓글 ~개 더보기
            mtv_replySumText = itemView.findViewById(R.id.tv_replySumText);

            mtv_reply.setOnClickListener(null);
            mtv_replySumText.setOnClickListener(null);

            mtv_replySumText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 아이템의 내용을 mCookList에서 가져와 cookItem에 담아 인텐트로 전달.
                    CookItem cookItem = mCookList.get(getAdapterPosition());

                    if(cookItem != null && cookItem.getPostIdx() > 0) {

                        Log.e("lee", "onclick mtv_replySumText");

                        // updateActivity로 인텐트값 전달
                        Intent intent = new Intent(mContext, ReplyViewActivity.class);
                        intent.putExtra("actionType", "REPLY");
                        intent.putExtra("cookItem", cookItem);
                        mContext.startActivity(intent);
                    }

                }
            });

            // 댓글 -> 댓글상세로 이동(ReplyViewActivity)
            mtv_reply.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    // 아이템의 내용을 mCookList에서 가져와 cookItem에 담아 인텐트로 전달.

                    CookItem cookItem = mCookList.get(getAdapterPosition());
                    if(cookItem != null && cookItem.getPostIdx() > 0) {

                        Log.e("lee", "onclick mtv_reply");

                        // updateActivity로 인텐트값 전달
                        Intent intent = new Intent(mContext, ReplyViewActivity.class);
                        intent.putExtra("actionType", "REPLY");
                        intent.putExtra("cookItem", cookItem);
                        mContext.startActivity(intent);
                    }

                }
            });

            // 프사누르면, 친구 프로필 사진으로 이동 -> FriendInfoActivity
            miv_userImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    // 아이템의 내용을 mCookList에서 가져와 cookItem에 담아 인텐트로 전달.

                    CookItem cookItem = mCookList.get(getAdapterPosition());
                    if(cookItem != null && cookItem.getPostIdx() > 0) {

                        Log.e("lee", "onclick mtv_reply");

                        // updateActivity로 인텐트값 전달
                        Intent intent = new Intent(mContext, FriendInfoActivity.class);
                        intent.putExtra("actionType", "FRIEND");
                        intent.putExtra("cookItem", cookItem);
                        mContext.startActivity(intent);
                    }

                }
            });


            // ... (더보기)리스너 설정
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    if (mListener != null) {
                        mListener.onItemClick(CookViewHolder.this, view, position);
                    }
                }
            });
        }


        // 팝업메뉴 (수정, 삭제)클릭 이벤트
        @Override
        public void onClick(View v) {
            showPopupMenu(v);
        }

        // 팝업 메뉴 설정 - 게시글작성자만 눌러짐
        private void showPopupMenu(View view) {

            // 셰어드에 저장된 userID 가져옴 // 지금 로그인한 아이디 OK
            SharedPreferences prefsName = mContext.getSharedPreferences("NAME", MODE_PRIVATE);
            String result = prefsName.getString("userID", "userID"); //키값, 디폴트값

            // 게시글번호 // position으로 하면 다른 값이 가져와졌음.
            CookItem currentItem = mCookList.get(getAdapterPosition());
            String writer = currentItem.getUserID();
            Log.e("result", result + writer);

            PopupMenu popupMenu = new PopupMenu(view.getContext(), view);
            popupMenu.inflate(R.menu.popup_menu);
            popupMenu.setOnMenuItemClickListener(this);
            // 게시글작성자만 눌러짐
            if (result.equals(writer)) {
                popupMenu.show();
            } else {
                Toast.makeText(mContext, "작성자가 아닙니다.", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {

            switch (item.getItemId()) {
                case R.id.itemModify:
                    Log.e(TAG, "onMenuItemClick : 수정하기" + getAdapterPosition());
                    // 수정하기

                    // 아이템의 내용을 mCookList에서 가져와 cookItem에 담아 인텐트로 전달.
                    CookItem cookItem = mCookList.get(getAdapterPosition());
                    // updateActivity로 인텐트값 전달
                    Intent intent = new Intent(mContext, UpdateActivity.class);
                    intent.putExtra("actionType", "Update");
                    intent.putExtra("item", cookItem);
                    mContext.startActivity(intent);

                    return true;

                case R.id.itemDelete:
                    // 삭제하기 // 포스트 번호의 위치값을 가져와 삭제한다. // 맨위의 게시물만 삭제가 됨

                    Intent deleteIntent = new Intent(mContext, DeleteActivity.class);
                    deleteIntent.putExtra("actionType", "Delete");
                    CookItem dcookItem = mCookList.get(getAdapterPosition());
                    deleteIntent.putExtra("item", dcookItem);
                    mContext.startActivity(deleteIntent);

                    // 삭제하기
                    delete(position);
                    temp = (mCookList.get(position).getPostIdx());

                    Log.e(TAG, "onMenuItemClick : 삭제하기" + getAdapterPosition());
                    return true;


                default:
                    break;
            }
            return false;

        }
    }
}
