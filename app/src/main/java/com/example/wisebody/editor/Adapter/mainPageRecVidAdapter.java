package com.example.wisebody.editor.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.wisebody.editor.ListType.recVidData;
import com.example.wisebody.editor.ListType.replyData;
import com.example.wisebody.editor.MainPage;
import com.example.wisebody.editor.R;
import com.example.wisebody.editor.Util.BitmapDownloaderTask;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by wisebody on 2017. 2. 15..
 */

public class mainPageRecVidAdapter extends RecyclerView.Adapter<mainPageRecVidAdapter.ViewHolder> {
    int itemLayout;
    List<recVidData> items;
    BitmapDownloaderTask bitmapDownloaderTask;
    Context context;

    public mainPageRecVidAdapter(int itemLayout, List<recVidData> items) {
        this.itemLayout = itemLayout;
        this.items = items;
    }

    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        View v = LayoutInflater.from(context).inflate(itemLayout, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final mainPageRecVidAdapter.ViewHolder holder, int position) {
        final recVidData item = items.get(position);
        bitmapDownloaderTask = new BitmapDownloaderTask(holder.thumb,null, context, null);
        bitmapDownloaderTask.download("http://203.233.111.62/test/media/clip2/"+item.title+".png",holder.thumb);
        bitmapDownloaderTask = new BitmapDownloaderTask(holder.cpLogo,null,context,null);
        bitmapDownloaderTask.download("http://203.233.111.62/test/media/CP/"+item.cp+".png",holder.cpLogo);
        bitmapDownloaderTask = new BitmapDownloaderTask(holder.profileImg,null,context,null);
        bitmapDownloaderTask.download("http://203.233.111.62/test/media/CP/"+item.nickName+".png",holder.profileImg);
        int sec = item.dur/1000;
        if(sec/(60*60)>0)
            holder.videoDur.setText(String.format("%02d",sec/(60*60))+":"+String.format("%02d",sec%(60*60)/60)+":"+String.format("%02d",sec%60));
        else
        holder.videoDur.setText(String.format("%02d",sec/60)+":"+String.format("%02d",sec%60));
        holder.title.setText("["+item.title+"]");
        holder.nickName.setText(item.nickName);
        holder.language.setText(item.language);
        holder.heartCount.setText(covertCount(item.heartCount));
        if(item.Iheart) {
            Bitmap bitmap  = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_heart_fill);
            holder.heartBtn.setImageBitmap(bitmap);
            holder.heartBtn.setSelected(true);
            bitmap = null;
        }

        holder.thumb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainPage)context).goRecVideo(item.title, item.cp);
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

        ImageButton thumb;
        CircleImageView cpLogo, profileImg;
        TextView videoDur, title, nickName, language, heartCount;
        ImageButton heartBtn;
        public ViewHolder(View itemView) {
            super(itemView);
            thumb = (ImageButton) itemView.findViewById(R.id.thumb);
            cpLogo = (CircleImageView) itemView.findViewById(R.id.cpLogo);
            profileImg = (CircleImageView) itemView.findViewById(R.id.profileImg);
            videoDur = (TextView) itemView.findViewById(R.id.videoDur);
            title = (TextView) itemView.findViewById(R.id.title);
            nickName = (TextView) itemView.findViewById(R.id.nickName);
            language = (TextView) itemView.findViewById(R.id.language);
            heartCount = (TextView) itemView.findViewById(R.id.heartCount);
            heartBtn = (ImageButton) itemView.findViewById(R.id.heartBtn);
        }
    }
}
