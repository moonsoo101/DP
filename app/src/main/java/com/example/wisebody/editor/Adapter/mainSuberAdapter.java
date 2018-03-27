package com.example.wisebody.editor.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.wisebody.editor.ListType.recVidData;
import com.example.wisebody.editor.ListType.suberData;
import com.example.wisebody.editor.MainPage;
import com.example.wisebody.editor.R;
import com.example.wisebody.editor.Util.BitmapDownloaderTask;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by wisebody on 2017. 2. 15..
 */

public class mainSuberAdapter extends RecyclerView.Adapter<mainSuberAdapter.ViewHolder> {
    int itemLayout;
    List<suberData> items;
    BitmapDownloaderTask bitmapDownloaderTask;
    Context context;

    public mainSuberAdapter(int itemLayout, List<suberData> items) {
        this.itemLayout = itemLayout;
        this.items = items;
    }

    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        View v = LayoutInflater.from(context).inflate(itemLayout, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final mainSuberAdapter.ViewHolder holder, int position) {
        final suberData item = items.get(position);
        bitmapDownloaderTask = new BitmapDownloaderTask(holder.suberLogo,null,context,null);
        bitmapDownloaderTask.download("http://203.233.111.62/test/media/CP/"+item.nickName+".png",holder.suberLogo);
        holder.nickName.setText(item.nickName);
        holder.language.setText(item.language);
        holder.description.setText(item.description);
        holder.heartCount.setText(covertCount(item.heartCount));
        if(item.Iheart) {
            Bitmap bitmap  = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_heart_fill);
            holder.heartBtn.setImageBitmap(bitmap);
            holder.heartBtn.setSelected(true);
            bitmap = null;
        }
        holder.foldBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(view.getRotation()==180) {
                    holder.description.setMaxLines(Integer.MAX_VALUE);
                    holder.description.setEllipsize(null);
                    view.setRotation(0);
                }
                else
                {
                    holder.description.setMaxLines(1);
                    holder.description.setEllipsize(TextUtils.TruncateAt.END);
                    view.setRotation(180);
                }
            }
        });
        holder.heartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.heartBtn.setSelected(!holder.heartBtn.isSelected());
                if(holder.heartBtn.isSelected())
                {
                    Bitmap bitmap  = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_heart_fill);
                    holder.heartBtn.setImageBitmap(bitmap);
                    bitmap = null;
                    item.heartCount +=1;
                    holder.heartCount.setText(covertCount(item.heartCount));

                }
                else
                {
                    Bitmap bitmap  = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_heart);
                    holder.heartBtn.setImageBitmap(bitmap);
                    bitmap = null;
                    item.heartCount -=1;
                    holder.heartCount.setText(covertCount(item.heartCount));
                }
            }
        });
    }
    private String covertCount(int count)
    {
        String covertcount;
        if(count/1000!=0)
            covertcount = String.format("%.1f",(float)count/1000)+"K";
        else
            covertcount = Integer.toString(count);
        return covertcount;

    }
    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageButton heartBtn, foldBtn, fanBtn;
        TextView heartCount, nickName, fanCount, language, description;
        CircleImageView suberLogo;
        public ViewHolder(View itemView) {
            super(itemView);
            heartBtn = (ImageButton) itemView.findViewById(R.id.heartBtn);
            foldBtn = (ImageButton) itemView.findViewById(R.id.foldBtn);
            heartBtn = (ImageButton) itemView.findViewById(R.id.heartBtn);
            fanBtn = (ImageButton) itemView.findViewById(R.id.fanBtn);
            heartCount = (TextView) itemView.findViewById(R.id.heartCount);
            nickName = (TextView) itemView.findViewById(R.id.nickName);
            fanCount = (TextView) itemView.findViewById(R.id.fanCount);
            language = (TextView) itemView.findViewById(R.id.language);
            description = (TextView) itemView.findViewById(R.id.description);
            suberLogo = (CircleImageView) itemView.findViewById(R.id.suberLogo);
        }
    }
}
