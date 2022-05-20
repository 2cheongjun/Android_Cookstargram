package com.example.myapplication.Chat_test.ChatList;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.myapplication.Chat_test.chatRoom2Activity;
import com.example.myapplication.Chat_test.chatRoomActivity;
import com.example.myapplication.R;
import com.example.myapplication.mypageFriend.FriendInfoActivity;
import com.example.myapplication.mypage_ViewBookmark.ViewActivity;
import com.example.myapplication.mypage_bookmark.BookmarkGridActivity;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import static android.content.Context.MODE_PRIVATE;
import static com.example.myapplication.Api.Api.BASE_URL;

public class chatListAdapter extends RecyclerView.Adapter<chatListAdapter.RecyclerViewAdapter> {

    private static final String TAG = "chatListAdapter ";
    String roomIdx;

    private List<ChatDate> chatItem;
    private Context context;
    private ItemClickListener itemClickListener;

    public chatListAdapter(List<ChatDate> chatInfos, Context context, ItemClickListener itemClickListener) {
        this.chatItem = chatInfos;
        this.context = context;
        this.itemClickListener = itemClickListener;
    }

    @NonNull
    @NotNull
    @Override
    public RecyclerViewAdapter onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.chatlist_item, parent, false);
        return new RecyclerViewAdapter(view, itemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull chatListAdapter.RecyclerViewAdapter holder, int position) {
        final ChatDate chatInfo = chatItem.get(position);

//        String postName = chatInfo.getTarget_ID();
        String created = chatInfo.getCreated(); // 메시지 보낸시간
        String postImg = chatInfo.getPostImg(); // 이미지?
        String friendID = chatInfo.getFriendID(); // 대화상대이름
        String finalMessage = chatInfo.getFinalMessage(); // 메시지내용1개
        String myID = chatInfo.getMyID(); // 대화상대이름
        String roomName = chatInfo.getRoomIdx();
        String read = chatInfo.getRead(); // 읽음 안읽음
        String idx = chatInfo.getIdx(); // 채팅맨마지막 번호
        String ChatCount = chatInfo.getFileChatCount(); //마지막 채팅 저장내역이후 온 채팅수

        Log.e(TAG, " 채팅맨 마지막번호 /" + idx);


        // 대화상대이름, 마지막 대화내용로드
        holder.tv_postName.setText(friendID + "님과 채팅");
//        holder.tv_postName.setText(roomName + "님과 채팅");
        holder.tv_comment.setText("눌러서 채팅하기");

        // 날짜 가져와서 ~시~분으로 보이게 하기 /오전 오후 표시
        String str = created;
        if(str !=null){
            String result = str.substring(10, 16); // 문자열통으로가져와 앞뒤 자름
            holder.tv_date.setText(result);
        }else{

        }

        // 프사 이미지 URI가져오기
        Uri url = Uri.parse(BASE_URL + "/upload/" + postImg);
        Glide.with(holder.itemView)
                .load(url)
                .skipMemoryCache(true)
                .override(100, 100)
                .centerCrop()
                .circleCrop()
//                .fitCenter() // 이미지뷰의 크기에 맞추기
                .skipMemoryCache(true)
                .error(R.drawable.cooker_man)
                .placeholder(R.drawable.cooker_man)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(holder.iv_coverImg);
    }

    @Override
    public int getItemCount() {
        return chatItem.size();
    }

    class RecyclerViewAdapter extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tv_postName, tv_date, tv_comment;
        ImageView iv_coverImg;
        CardView card_item;
        ItemClickListener itemClickListener;
        TextView tv_redDot; // 빨간 카운트

        RecyclerViewAdapter(@NonNull @NotNull View itemView, ItemClickListener itemClickListener) {
            super(itemView);

            tv_postName = itemView.findViewById(R.id.tv_postName); // 대화상대이름
            tv_comment = itemView.findViewById(R.id.tv_comment);// 마지막대화내용
            tv_date = itemView.findViewById(R.id.tv_date); // 마지막 대화시간
            iv_coverImg = itemView.findViewById(R.id.iv_coverImg);// 대화상대 이미지
//            tv_redDot = itemView.findViewById(R.id.tv_redDot); // 빨간원에 카운트
            card_item = itemView.findViewById(R.id.card_item);
            this.itemClickListener = itemClickListener;
            card_item.setOnClickListener(this);
        }

        // 채팅방리스트에서 클릭시 ->각 채팅방으로 이동하기 chatRoom2Activity로 이동
        @Override
        public void onClick(View v) {
            itemClickListener.onItemClick(v, getAdapterPosition());

            if (chatItem != null) {
                ChatDate chatInfo = chatItem.get(getAdapterPosition());

                String listRoomIdx = chatInfo.getRoomIdx();

                String listFriend = chatInfo.getFriendID();


                // 1:1 채팅방으로 이동
                Intent intent = new Intent(context, chatRoom2Activity.class);
                intent.putExtra("listRoomIdx", listRoomIdx); // 리스트에서 보낸 방번호
                intent.putExtra("listFriend", listFriend); // 친구이름
                context.startActivity(intent);
            }
        }


    }

    public interface ItemClickListener {
        void onItemClick(View view, int position);


    }

}

