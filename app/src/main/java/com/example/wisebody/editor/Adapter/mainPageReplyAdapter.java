package com.example.wisebody.editor.Adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.wisebody.editor.ListType.reply;
import com.example.wisebody.editor.ListType.replyData;
import com.example.wisebody.editor.ListType.rereplyData;
import com.example.wisebody.editor.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wisebody on 2017. 2. 15..
 */

public class mainPageReplyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    int itemLayout;
    List<reply> items;
    public ArrayList<reply> visibleItems = new ArrayList<>();
    ArrayList<String> day =new ArrayList<>();
    private final int PARENT_ITEM_VIEW = 0;
    private final int CHILD_ITEM_VIEW = 1;

    public mainPageReplyAdapter(int itemLayout, List<reply> items) {
        this.itemLayout = itemLayout;
        this.items = items;
        day.add("3일 전");
        day.add("하루 전");
        day.add("하루 전");
        day.add("하루 전");
        day.add("하루 전");
        day.add("하루 전");
        day.add("하루 전");
        day.add("3일 전");
        day.add("하루 전");
        day.add("하루 전");
        day.add("하루 전");
        day.add("하루 전");
        day.add("하루 전");
        day.add("하루 전");
        day.add("3일 전");
        day.add("하루 전");
        day.add("하루 전");
        day.add("하루 전");
        day.add("하루 전");
        day.add("하루 전");
        day.add("하루 전");
        Log.d("add","visible add");
        for(int i =0; i<items.size();i++)
                visibleItems.add(items.get(i));
    }
    @Override
    public int getItemViewType(int position) {
        for (int i=0;i<visibleItems.size();i++)
            Log.d("add","getitem"+position+visibleItems.get(i).viewType);
        Log.d("add","type" +visibleItems.get(position).viewType);
        return visibleItems.get(position).viewType;
    }
    @Override
    public int getItemCount() {
        return visibleItems.size();
    }

    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        switch(viewType){
            case 0:
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.mainpage_reply, parent, false);
                viewHolder = new ViewHolder(view);
                Log.d("add","리플");
                break;
            case 1:
                View subview = LayoutInflater.from(parent.getContext()).inflate(R.layout.mainpage_rereply, parent, false);
                viewHolder = new childViewHolder(subview);
                Log.d("add","리리플");
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        final reply item = visibleItems.get(position);
        if(holder instanceof  ViewHolder) {
            final ViewHolder viewHolder = (ViewHolder)holder;
            final replyData replyItem = (replyData)item;
            viewHolder.nickName.setText(replyItem.name);
            viewHolder.replyDay.setText(day.get(position));
            viewHolder.replyText.setText(replyItem.text);
            String covertHeart = covertCount(replyItem.heartcount);
            viewHolder.heartCount.setText(covertHeart);
            String covertReply = covertCount((replyItem.rereplycount));
            viewHolder.rereplyBtn.setText("답글 "+covertReply);
            if (replyItem.Iheart) {
                viewHolder.heartBtn.setBackgroundResource(R.drawable.ic_heart_fill);
                viewHolder.heartBtn.setSelected(true);
            }
            viewHolder.heartBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    viewHolder.heartBtn.setSelected(!viewHolder.heartBtn.isSelected());
                    if (viewHolder.heartBtn.isSelected()) {
                        viewHolder.heartBtn.setBackgroundResource(R.drawable.ic_heart_fill);
                        replyItem.heartcount += 1;
                        viewHolder.heartCount.setText(covertCount(replyItem.heartcount));

                    } else {
                        viewHolder.heartBtn.setBackgroundResource(R.drawable.ic_heart);
                        replyItem.heartcount -= 1;
                        viewHolder.heartCount.setText(covertCount(replyItem.heartcount));
                    }
                }
            });
            viewHolder.rereplyBtn.setTag(viewHolder);
            viewHolder.rereplyBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int holderPosition = ((RecyclerView.ViewHolder)view.getTag()).getAdapterPosition();
                    if(((replyData)visibleItems.get(holderPosition)).visibilityOfChildItems){
                        collapseChildItems(holderPosition);
                    }else{
                        expandChildItems(holderPosition);
                    }
                }
            });
        }
        else if(holder instanceof childViewHolder) {
            final childViewHolder childViewHolder = (mainPageReplyAdapter.childViewHolder) holder;
            final rereplyData rereplyItem = (rereplyData) item;
            childViewHolder.nickName.setText(rereplyItem.name);
            childViewHolder.replyDay.setText(day.get(position));
            childViewHolder.replyText.setText(rereplyItem.text);
            String covertHeart = covertCount(rereplyItem.heartcount);
            childViewHolder.heartCount.setText(covertHeart);
            if (rereplyItem.Iheart) {
                childViewHolder.heartBtn.setBackgroundResource(R.drawable.ic_heart_fill);
                childViewHolder.heartBtn.setSelected(true);
            }
            childViewHolder.heartBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    childViewHolder.heartBtn.setSelected(!childViewHolder.heartBtn.isSelected());
                    if (childViewHolder.heartBtn.isSelected()) {
                        childViewHolder.heartBtn.setBackgroundResource(R.drawable.ic_heart_fill);
                        rereplyItem.heartcount += 1;
                        childViewHolder.heartCount.setText(covertCount(rereplyItem.heartcount));

                    } else {
                        childViewHolder.heartBtn.setBackgroundResource(R.drawable.ic_heart);
                        rereplyItem.heartcount -= 1;
                        childViewHolder.heartCount.setText(covertCount(rereplyItem.heartcount));
                    }
                }
            });
        }
        Log.d("setadapter","position  "+ position);
    }
    private void collapseChildItems(int position){
        replyData parentItem = (replyData) visibleItems.get(position);
        parentItem.visibilityOfChildItems = false;

        int subItemSize = getVisibleChildItemSize(position);
        for(int i = 0; i < subItemSize; i++){
            parentItem.unvisibleChildItems.add((rereplyData) visibleItems.get(position + 1));
            visibleItems.remove(position + 1);
        }

        notifyItemRangeRemoved(position + 1, subItemSize);
    }
    private int getVisibleChildItemSize(int parentPosition){
        int count = 0;
        parentPosition++;
        while(true){
            if(parentPosition == visibleItems.size() || visibleItems.get(parentPosition).viewType == PARENT_ITEM_VIEW){
                break;
            }else{
                parentPosition++;
                count++;
            }
        }
        return count;
    }
    private void expandChildItems(int position){

        replyData parentItem = (replyData) visibleItems.get(position);
        if(parentItem.unvisibleChildItems!=null) {
            parentItem.visibilityOfChildItems = true;
            int childSize = parentItem.unvisibleChildItems.size();

            for (int i = childSize - 1; i >= 0; i--) {
                visibleItems.add(position + 1, parentItem.unvisibleChildItems.get(i));
                Log.d("add", "더했다" + position + parentItem.unvisibleChildItems.get(i).viewType);
            }
            Log.d("add", "더했다 더한거" + ((rereplyData) visibleItems.get(position + 1)).viewType);
            parentItem.unvisibleChildItems.clear();

            notifyItemRangeInserted(position + 1, childSize);
        }
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

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView nickName, replyDay ,replyText, heartCount;
        Button heartBtn, rereplyBtn;
        View lastLine;
        public ViewHolder(View itemView) {
            super(itemView);
            nickName = (TextView)itemView.findViewById(R.id.name);
            replyDay = (TextView)itemView.findViewById(R.id.replyday);
            replyText = (TextView)itemView.findViewById(R.id.replyText);
            heartCount = (TextView)itemView.findViewById(R.id.heartCount);
            heartBtn = (Button)itemView.findViewById(R.id.heartBtn);
            rereplyBtn = (Button)itemView.findViewById(R.id.rereplyBtn);
            lastLine = (View)itemView.findViewById(R.id.lastLine);

        }
    }
    public static class childViewHolder extends RecyclerView.ViewHolder {

        TextView nickName, replyDay ,replyText, heartCount;
        Button heartBtn;
        public childViewHolder(View itemView) {
            super(itemView);
            nickName = (TextView)itemView.findViewById(R.id.name);
            replyDay = (TextView)itemView.findViewById(R.id.replyday);
            replyText = (TextView)itemView.findViewById(R.id.replyText);
            heartCount = (TextView)itemView.findViewById(R.id.heartCount);
            heartBtn = (Button)itemView.findViewById(R.id.heartBtn);

        }
    }
}
