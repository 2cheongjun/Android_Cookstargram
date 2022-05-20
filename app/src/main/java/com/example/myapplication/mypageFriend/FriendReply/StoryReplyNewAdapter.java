package com.example.myapplication.mypageFriend.FriendReply;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.myapplication.R;
import com.example.myapplication.cookStargram_reply.ReplyViewActivity;
import com.example.myapplication.cookStargram_reply.friendReplyItem;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;
import static com.example.myapplication.Api.Api.BASE_URL;

public class StoryReplyNewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    /*
      어댑터의 동작원리 및 순서
      1.(getItemCount) 데이터 개수를 세어 어댑터가 만들어야 할 총 아이템 개수를 얻는다.
      2.(getItemViewType)[생략가능] 현재 itemview의 viewtype을 판단한다
      3.(onCreateViewHolder)viewtype에 맞는 뷰 홀더를 생성하여 onBindViewHolder에 전달한다.
      4.(onBindViewHolder)뷰홀더와 position을 받아 postion에 맞는 데이터를 뷰홀더의 뷰들에 바인딩한다.
      */
    String TAG = "RecyclerViewAdapter";
    //리사이클러뷰에 넣을 데이터 리스트
    private final Context mContext;
    private final ArrayList<friendReplyItem> mfriendReplyList; // 북마크 각아이템 데이터

    // 인텐트 값을 가져오기 위한 구분자
    private String actionType = "";
    private StoryReplyItem replyItem = null;
    // ?
    RecyclerView.RecycledViewPool viewPool = null;

    // 수정버튼 리스너
    OnModifyItemClickListener onModifyItemClickListener;

    public interface OnModifyItemClickListener {
        public void onModifyItemClickListener(int position, String title);
    }

    public void setOnModifyItemClickListener(OnModifyItemClickListener onModifyItemClickListener) {
        this.onModifyItemClickListener = onModifyItemClickListener;
    }


    // 삭제버튼 리스너
    OnPersonItemClickListener onPersonItemClickListener;

    public interface OnPersonItemClickListener {
        void onPersonItemClickListener(int replyIdx);
    }

    public void setOnPersonItemClickListener(OnPersonItemClickListener onPersonItemClickListener) {
        this.onPersonItemClickListener = onPersonItemClickListener;
    }

    // 답글버튼 리스너
    OnReplyItemClickListener onReplyItemClickListener;

    // 게시글 번호 담아 보내기
    public interface OnReplyItemClickListener {
        void onReplyItemClickListener(int replyIdx);
    }

    public void setOnReplyItemClickListener(OnReplyItemClickListener onReplyItemClickListener) {
        this.onReplyItemClickListener = onReplyItemClickListener;
    }


    //생성자를 통하여 데이터 리스트 context를 받음
    public StoryReplyNewAdapter(Context context, ArrayList<friendReplyItem> friendReplylist) {
        mContext = context;
        mfriendReplyList = friendReplylist;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder");


        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.reply_item, parent, false);

        //생성된 뷰홀더를 리턴하여 onBindViewHolder에 전달한다.
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder");

        ViewHolder mHolder = (ViewHolder) holder;
        // 데이터 연결부분
        friendReplyItem replyItem = mfriendReplyList.get(position);
        String postIdx = replyItem.getPostIdx();
        String userID = replyItem.getUserID();
//        String postImg = replyItem.getPostImg();
        String title = replyItem.getTitle();
        String date = replyItem.getReplyDate();
        int replyIdx = replyItem.getReplyIdx();
        String step = replyItem.getStep();

        // 서버에서 받아온 step이 1이면 들여쓰기 한다.
        if (step.equals("1")) {
            mHolder.mUserID.setText("└─ " + userID);
            mHolder.mTitle.setText("   " + title);
            mHolder.mTime.setText("   " + date);
            mHolder.mReplyBtn.setText("   " + "답글달기");
        } else {
            mHolder.mUserID.setText(userID);
            mHolder.mTitle.setText(title);
            mHolder.mTime.setText(date);
//      답글달기
            mHolder.mReplyBtn.setText("답글달기");
        }



        // 셰어드에 저장된 userID 가져옴 // 지금 로그인한 아이디 OK
        SharedPreferences prefsName = mContext.getSharedPreferences("NAME", MODE_PRIVATE);
        String result = prefsName.getString("userID", "userID"); //키값, 디폴트값
        // 작성자만 수정가능

        if (!result.equals(userID)) {
            mHolder.mModityBtn.setVisibility(View.GONE);
            mHolder.mdelBtn.setVisibility(View.GONE);
        }



    }


    public int getReplyIdx(int position) {
        return mfriendReplyList.get(position).getReplyIdx();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView mPostImg;
        public TextView mUserID;
        public TextView mPostIdx;
        public TextView mTitle;
        public TextView mTime;
        public TextView mDelete;
        public ImageView mdelBtn;
        public ImageView mModityBtn;
        public TextView mReplyBtn;
        private final RecyclerView rvSubItem;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mUserID = itemView.findViewById(R.id.tv_userID);
            mPostIdx = itemView.findViewById(R.id.tv_postIdx);
            mTitle = itemView.findViewById(R.id.tv_title);
//            mPostImg = itemView.findViewById(R.id.iv_postImg);
            mTime = itemView.findViewById(R.id.tv_time);
            mDelete = itemView.findViewById(R.id.tv_delete);

            mdelBtn = itemView.findViewById(R.id.iv_delBtn);
            mModityBtn = itemView.findViewById(R.id.iv_modifyBtn);
            // 답글버튼
            mReplyBtn = itemView.findViewById(R.id.tv_reply_text);

            // 자식아이템 영역
            rvSubItem = itemView.findViewById(R.id.rv_sub_item);


            // 셰어드에 저장된 userID 가져옴 // 지금 로그인한 아이디 OK
            SharedPreferences prefsName = mContext.getSharedPreferences("NAME", MODE_PRIVATE);
            String result = prefsName.getString("userID", "userID"); //키값, 디폴트값


            // 삭제버튼 // 아이템뷰에 온클릭리스너 설정하기
            mdelBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    // 아이템뷰 클릭시 미리 정의한 다른 리스너의 메서드 호출하기
                    if (onPersonItemClickListener != null) {
                        onPersonItemClickListener.onPersonItemClickListener(mfriendReplyList.get(getAdapterPosition()).getReplyIdx());

                    }
                }
            });

            // 수정 버튼 // 아이템뷰에 온클릭리스너 설정하기
            mModityBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    int position = getAdapterPosition();
                    // 아이템뷰 클릭시 미리 정의한 다른 리스너의 메서드 호출하기
                    if (onModifyItemClickListener != null) {

                        // 아이템의 내용을 mReplyList 가져와 인텐트로 전달.
                        friendReplyItem replyItem = mfriendReplyList.get(getAdapterPosition());

                        onModifyItemClickListener.onModifyItemClickListener(position, replyItem.getTitle());
                    }

                }
            });

            // 답글버튼 //온클릭리스너
            mReplyBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();

                    // 아이템뷰 클릭시 미리 정의한 다른 리스너의 메서드 호출하기
                    if (onReplyItemClickListener != null) {
                        onReplyItemClickListener.onReplyItemClickListener(mfriendReplyList.get(getAdapterPosition()).getReplyIdx());
                    }
                }
            });

        }

    }

    @Override
    public int getItemCount() {
        //데이터 리스트의 크기를 전달해주어야 함

        return (mfriendReplyList != null ? mfriendReplyList.size() : 0);
    }
}