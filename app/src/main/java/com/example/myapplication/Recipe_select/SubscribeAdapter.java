package com.example.myapplication.Recipe_select;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.myapplication.R;
import com.example.myapplication.mypageFriend.FriendInfoActivity;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import static com.example.myapplication.Api.Api.BASE_URL;

public class SubscribeAdapter extends RecyclerView.Adapter<SubscribeAdapter.RecyclerViewAdapter> {

    private static final String TAG = "구독 어답터";
    private List<Note> notes;
    private Context context;
    private ItemClickListener itemClickListener;

    public SubscribeAdapter(List<Note> notes, Context context, ItemClickListener itemClickListener) {
        this.notes = notes;
        this.context = context;
        this.itemClickListener = itemClickListener;
    }

    @NonNull
    @NotNull
    @Override
    public RecyclerViewAdapter onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.subscriebe_item, parent, false);

        return new RecyclerViewAdapter(view, itemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull SubscribeAdapter.RecyclerViewAdapter holder, int position) {
        final Note note = notes.get(position);
        String btn_check = note.getBtn_check();
        String postName = note.getTarget_ID();
        String postImg = note.getPostImg();
        holder.tv_postName.setText(postName);
        holder.tv_date.setText("레시피 보러가기");

        // 포스트 이미지 URI가져오기
        Uri url = Uri.parse(BASE_URL + "/upload/" + postImg);
        Glide.with(holder.itemView)
                .load(url)
                .skipMemoryCache(true)
                .override(400, 400)
                .centerCrop()
                .fitCenter() // 이미지뷰의 크기에 맞추기
                .skipMemoryCache(true)
                .error(R.drawable.cooker_man)
                .placeholder(R.drawable.cooker_man)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(holder.iv_coverImg);

    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    class RecyclerViewAdapter extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tv_postName, tv_date;
        ImageView iv_coverImg;
        CardView card_item;
        ItemClickListener itemClickListener;

        RecyclerViewAdapter(@NonNull @NotNull View itemView, ItemClickListener itemClickListener) {
            super(itemView);

            tv_postName = itemView.findViewById(R.id.postName);
            tv_date = itemView.findViewById(R.id.date);
            iv_coverImg = itemView.findViewById(R.id.iv_coverImg);
            card_item = itemView.findViewById(R.id.card_item);

            this.itemClickListener = itemClickListener;
            card_item.setOnClickListener(this);
        }


        // 구독자하는 사람 페이지로 이동하기
        @Override
        public void onClick(View v) {
            itemClickListener.onItemClick(v, getAdapterPosition());

            if(notes != null) {
            Note note = notes.get(getAdapterPosition());

            // updateActivity로 인텐트값 전달
            Intent intent = new Intent(context, FriendInfoActivity.class);
            intent.putExtra("actionType2", "Subscribe");
            intent.putExtra("note", note);
            context.startActivity(intent);
        }
    }


}

public interface ItemClickListener {
    void onItemClick(View view, int position);
}

}

