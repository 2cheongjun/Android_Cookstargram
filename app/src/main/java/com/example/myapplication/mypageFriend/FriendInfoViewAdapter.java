package com.example.myapplication.mypageFriend;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.myapplication.R;

import java.util.ArrayList;

import static com.example.myapplication.Api.Api.BASE_URL;

public class FriendInfoViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    /*
      어댑터의 동작원리 및 순서
      1.(getItemCount) 데이터 개수를 세어 어댑터가 만들어야 할 총 아이템 개수를 얻는다.
      2.(getItemViewType)[생략가능] 현재 itemview의 viewtype을 판단한다
      3.(onCreateViewHolder)viewtype에 맞는 뷰 홀더를 생성하여 onBindViewHolder에 전달한다.
      4.(onBindViewHolder)뷰홀더와 position을 받아 postion에 맞는 데이터를 뷰홀더의 뷰들에 바인딩한다.
      */
    String TAG = "RecyclerViewAdapter";
    //리사이클러뷰에 넣을 데이터 리스트
    private Context mContext;
    private final ArrayList<FriendInfoModel> storyDataModels; // 북마크 각아이템 데이터


    // 인터페이스 설정 // 클릭시 보낼값을 담는다.(position값, postIdx값 전달)
    OnStoryItemClickListener onStoryItemClickListener;
    public interface OnStoryItemClickListener {
        public void onStoryItemClickListener(int position,int postIdx);
    }

    public void setOnStoryItemClickListener(OnStoryItemClickListener onStoryItemClickListener) {
        this.onStoryItemClickListener = onStoryItemClickListener;
    }


    //생성자를 통하여 데이터 리스트 context를 받음
    public FriendInfoViewAdapter(Context context, ArrayList<FriendInfoModel> datalist) {
        storyDataModels = datalist;
        mContext = context;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder");

        //자신이 만든 itemview를 inflate한 다음 뷰홀더 생성

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.friend_grid_item, parent, false);

        return new ViewHolder(itemView);

        // 뷰홀더를 리턴하여 onBindViewHolder에 전달한다.
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder");

        ViewHolder mHolder = (ViewHolder) holder;
        // 데이터 연결부분
        FriendInfoModel datalist = storyDataModels.get(position);

        int postIdx = datalist.getPostIdx();
        String userID = datalist.getUserID();
        String postImg = datalist.getPostImg();


        // 이미지 URI가져오기
//        Uri url = Uri.parse(BASE_URL + "/img/" +"1628233340903.JPG");
        Uri url = Uri.parse(BASE_URL+"/img/"+postImg);

        Glide.with(holder.itemView)
                .load(url)
                .skipMemoryCache(true)
                .override(1000, 1000)
                .thumbnail()
                .centerCrop()
                .error(android.R.drawable.ic_input_delete)
                .placeholder(android.R.drawable.ic_menu_gallery)
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(mHolder.mPostImg);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView mPostImg;
        public TextView mUserId;
        public TextView mPostIdx;
        public CheckBox mCh_btn_follow;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            mPostImg = itemView.findViewById(R.id.iv_thumbnail);
            mCh_btn_follow= itemView.findViewById(R.id.ch_btn_follow);

            // 북마크 썸네일을 클릭시 -> 각 게시글로 이동한다.
            mPostImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    int position = getAdapterPosition();

                    if(onStoryItemClickListener !=null){

                        FriendInfoModel datalist = storyDataModels.get(position);
                        int postIdx = datalist.getPostIdx();
                        // 클릭리스너에, 어답터 포지션값과, 게시글 번호를 담아 보낸다.
                        onStoryItemClickListener.onStoryItemClickListener(position,postIdx);
                    }
                }
            });
        }
    }


    @Override
    public int getItemCount() {
        //데이터 리스트의 크기를 전달해주어야 함

        if (storyDataModels != null) {
            return storyDataModels.size();
        }
        return 0;
    }
}
