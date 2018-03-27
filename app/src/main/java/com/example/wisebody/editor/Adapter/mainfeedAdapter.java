package com.example.wisebody.editor.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.devbrackets.android.exomedia.ui.widget.EMVideoView;
import com.example.wisebody.editor.ListType.seqData;
import com.example.wisebody.editor.MainFeed;
import com.example.wisebody.editor.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wisebody on 2017. 2. 1..
 */

public class mainfeedAdapter extends RecyclerView.Adapter<mainfeedAdapter.ViewHolder> {
    private List<String> items;
    private int itemLayout;
    ArrayList<String> selected;
    Context context;

    public mainfeedAdapter(List<String> items, int itemLayout, ArrayList<String> selected) {
        this.items = items;
        this.itemLayout = itemLayout;
        this.selected = selected;

    }

    @Override
    public mainfeedAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(itemLayout, parent, false);
        context = parent.getContext();
        return new mainfeedAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final mainfeedAdapter.ViewHolder holder, final int position) {
       final String item = items.get(position);
        holder.selectOpt.setText(item);
        Log.d("등록",item);
        holder.selectOpt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.setSelected(!(view.isSelected()));
                if(view.isSelected()) {
                    selected.add(item);
                    holder.selectOpt.setTextColor(Color.parseColor("#16afca"));
                }
                else {
                    selected.remove(item);
                    holder.selectOpt.setTextColor(Color.parseColor("#8a000000"));
                }
            }
        });



    }


    @Override
    public int getItemCount() {
        return items.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        Button selectOpt;


        public ViewHolder(View itemView) {
            super(itemView);
            selectOpt = (Button) itemView.findViewById(R.id.selectOpt);
        }
    }
}
