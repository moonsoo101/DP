package com.example.wisebody.editor.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.wisebody.editor.ListType.recVidData;
import com.example.wisebody.editor.MainPage;
import com.example.wisebody.editor.R;
import com.example.wisebody.editor.Util.BitmapDownloaderTask;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by wisebody on 2017. 2. 15..
 */

public class LandmainPageRecVidAdapter extends RecyclerView.Adapter<LandmainPageRecVidAdapter.ViewHolder> {
    int itemLayout;
    List<recVidData> items;
    BitmapDownloaderTask bitmapDownloaderTask;
    Context context;

    public LandmainPageRecVidAdapter(int itemLayout, List<recVidData> items) {
        this.itemLayout = itemLayout;
        this.items = items;
    }

    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        View v = LayoutInflater.from(context).inflate(itemLayout, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final LandmainPageRecVidAdapter.ViewHolder holder, int position) {
        final recVidData item = items.get(position);
        bitmapDownloaderTask = new BitmapDownloaderTask(holder.thumb,null, context, null);
        bitmapDownloaderTask.download("http://203.233.111.62/test/media/clip2/"+item.title+".png",holder.thumb);
        bitmapDownloaderTask = new BitmapDownloaderTask(holder.cpLogo,null,context,null);
        bitmapDownloaderTask.download("http://203.233.111.62/test/media/CP/"+item.cp+".png",holder.cpLogo);
        holder.part.setText("Part."+item.part);
        int sec = item.dur/1000;
        if(sec/(60*60)>0)
            holder.videoDur.setText(String.format("%02d",sec/(60*60))+":"+String.format("%02d",sec%(60*60)/60)+":"+String.format("%02d",sec%60));
        else
        holder.videoDur.setText(String.format("%02d",sec/60)+":"+String.format("%02d",sec%60));

        holder.thumb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainPage)context).goRecVideo(item.title, item.cp);
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
        CircleImageView cpLogo;
        TextView part, videoDur;
        public ViewHolder(View itemView) {
            super(itemView);
            thumb = (ImageButton) itemView.findViewById(R.id.thumb);
            cpLogo = (CircleImageView) itemView.findViewById(R.id.cpLogo);
            part = (TextView) itemView.findViewById(R.id.part);
            videoDur = (TextView) itemView.findViewById(R.id.videoDur);
        }
    }
}
