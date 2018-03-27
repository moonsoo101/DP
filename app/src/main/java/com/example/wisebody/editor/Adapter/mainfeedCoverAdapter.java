package com.example.wisebody.editor.Adapter;

import android.content.Context;
import android.graphics.Rect;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.wisebody.editor.ListType.coverData;
import com.example.wisebody.editor.MainFeed;
import com.example.wisebody.editor.Player.VideoPlayer;
import com.example.wisebody.editor.R;
import com.example.wisebody.editor.Util.BitmapDownloaderTask;
import com.example.wisebody.editor.Util.Util;

import java.text.DecimalFormat;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by wisebody on 2017. 2. 7..
 */

public class mainfeedCoverAdapter extends RecyclerView.Adapter<mainfeedCoverAdapter.ViewHolder> {
    int itemLayout;
    List<coverData> items;
    static Context context;
    public mainfeedCoverAdapter(int itemLayout, List<coverData> items)
    {
        this.itemLayout = itemLayout;
        this.items = items;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(itemLayout, parent, false);
        context = parent.getContext();
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final coverData item = items.get(position);
        //holder.parent.setTag(holder);
        for (int i =0; i<7;i++) {
            holder.thumb[i].setVisibility(View.VISIBLE);
            holder.playBtn[i].setVisibility(View.VISIBLE);
            holder.title[i].setText(item.title.get(i));
            Boolean scale;
            if(i==1||i==5)
                scale = true;
            else scale = false;
            holder.videoPlayer[i] = new VideoPlayer(context,holder.playBtn[i], holder.thumb[i], holder.title[i], holder.cpLogo[i], holder.progressBar[i], scale, i);
            holder.videocontainer[i].addView(holder.videoPlayer[i],0);
            holder.videoPlayer[i].setVisibility(View.GONE);
            holder.videoPlayer[i].index1 = position;
            final int finalI = i;
            holder.playBtn[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!((MainFeed)context).playingVideos.contains(holder.videoPlayer[finalI]))
                    ((MainFeed)context).playingVideos.add(holder.videoPlayer[finalI]);
                    Log.d("Index",Integer.toString(finalI));
                    if(!holder.videoPlayer[finalI].isAvailable()) {
                        boolean scale;
                        if(finalI==1||finalI==5)
                            scale = true;
                        else scale = false;
                        holder.videoPlayer[finalI] = new VideoPlayer(context, holder.playBtn[finalI], holder.thumb[finalI], holder.title[finalI], holder.cpLogo[finalI],holder.progressBar[finalI], scale, finalI);
                        holder.videoPlayer[finalI].index1 = position;
                        holder.videocontainer[finalI].addView(holder.videoPlayer[finalI],0);
                    }
                    if(!holder.videoPlayer[finalI].play) {
                        holder.videoPlayer[finalI].play = true;
                        if(holder.videoPlayer[finalI].pause)
                            holder.videoPlayer[finalI].play();
                        else {
                            holder.videoPlayer[finalI].loadVideo("http://203.233.111.62/test/media/clip2/" + item.title.get(finalI) + ".mp4");
                            holder.playBtn[finalI].setVisibility(View.INVISIBLE);
                            holder.videoPlayer[finalI].setVisibility(View.VISIBLE);
                        }
                    }
                }
            });
            holder.thumb[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!holder.videoPlayer[finalI].isAvailable()) {
                        boolean scale;
                        if(finalI==1||finalI==5)
                            scale = true;
                        else scale = false;
                        holder.videoPlayer[finalI] = new VideoPlayer(context, holder.playBtn[finalI], holder.thumb[finalI], holder.title[finalI], holder.cpLogo[finalI], holder.progressBar[finalI], scale, finalI);
                        holder.videoPlayer[finalI].index1 = position;
                        holder.videocontainer[finalI].addView(holder.videoPlayer[finalI],0);
                    }
                    if(!holder.videoPlayer[finalI].play) {
                        holder.videoPlayer[finalI].play = true;
                        if(holder.videoPlayer[finalI].pause)
                            holder.videoPlayer[finalI].play();
                        else {
                            holder.videoPlayer[finalI].loadVideo("http://203.233.111.62/test/media/clip2/" + item.title.get(finalI) + ".mp4");
                            holder.playBtn[finalI].setVisibility(View.INVISIBLE);
                            holder.videoPlayer[finalI].setVisibility(View.VISIBLE);
                            Log.d("mediastate", "thumbclick");
                        }
                    }
                    else
                        holder.videoPlayer[finalI].goMainPage();

                }
            });
            if(i==1||i==5)
            holder.bitmapDownloaderTask = new BitmapDownloaderTask(holder.thumb[i],holder.progressBar[i],context, true);
            else
                holder.bitmapDownloaderTask = new BitmapDownloaderTask(holder.thumb[i],holder.progressBar[i],context, false);
            holder.bitmapDownloaderTask.download("http://203.233.111.62/test/media/clip2/"+item.title.get(i)+".png",holder.thumb[i]);
            holder.bitmapDownloaderTask = new BitmapDownloaderTask(holder.cpLogo[i],holder.progressBar[i],context, false);
            holder.bitmapDownloaderTask.download(item.cp.get(i),holder.cpLogo[i]);
        }
        holder.bitmapDownloaderTask = new BitmapDownloaderTask(holder.profileImg,null,context, false);
        holder.bitmapDownloaderTask.download("http://203.233.111.62/test/media/CP/"+item.name+".png",holder.profileImg);
        holder.nickName.setText(item.name);
        holder.fanCnt.setText(Integer.toString(item.star));
        holder.heartCnt.setText(Integer.toString(item.heart));

    }

    @Override
    public int getItemCount() {
        return items.size();
    }
    public static class ViewHolder extends RecyclerView.ViewHolder {
        View[] videoCont = new View[7];
        ImageView[] thumb = new ImageView[7];
        public Button[] playBtn = new Button[7];
        public FrameLayout[] videocontainer = new FrameLayout[7];
        ProgressBar[] progressBar = new ProgressBar[7];
        CircleImageView[] cpLogo= new CircleImageView[7];
        TextView[] title=new TextView[7];
        public VideoPlayer[] videoPlayer=new VideoPlayer[7];
        CircleImageView profileImg;
        TextView nickName;
        TextView fanCnt;
        TextView heartCnt;
        BitmapDownloaderTask bitmapDownloaderTask;
        public ViewHolder(View itemView) {
            super(itemView);
            context = itemView.getContext();
            videoCont = new View[]{itemView.findViewById(R.id.video1Cont), itemView.findViewById(R.id.video2Cont),
                    itemView.findViewById(R.id.video3Cont), itemView.findViewById(R.id.video4Cont),
                    itemView.findViewById(R.id.video5Cont), itemView.findViewById(R.id.video6Cont),
                    itemView.findViewById(R.id.video7Cont)};
            for (int i=0;i<7;i++)
            {
                thumb[i] = (ImageView)(videoCont[i].findViewById(R.id.thumb));
                playBtn[i] = (Button)(videoCont[i].findViewById(R.id.playbtn));
                videocontainer[i] = (FrameLayout)(videoCont[i].findViewById(R.id.videocontainer));
                progressBar[i] = (ProgressBar)(videoCont[i].findViewById(R.id.progress));
                cpLogo[i] = (CircleImageView)(videoCont[i].findViewById(R.id.cpLogo));
                title[i] = (TextView)(videoCont[i].findViewById(R.id.title));
//                Boolean scale;
//                if(i==1||i==5)
//                    scale = true;
//                else scale = false;
//                videoPlayer[i] = new VideoPlayer(itemView.getContext(),playBtn[i], thumb[i], title[i], cpLogo[i], scale, i);
//                videocontainer[i].addView(videoPlayer[i],0);
            }
            profileImg = (CircleImageView) itemView.findViewById(R.id.profileImg);
            nickName = (TextView) itemView.findViewById(R.id.nickName);
            fanCnt = (TextView) itemView.findViewById(R.id.fanCount);
            heartCnt = (TextView) itemView.findViewById(R.id.heartCount);
        }
    }
}
