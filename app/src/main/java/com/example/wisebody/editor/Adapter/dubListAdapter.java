package com.example.wisebody.editor.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.devbrackets.android.exomedia.ui.widget.EMVideoView;
import com.example.wisebody.editor.ListType.dubData;
import com.example.wisebody.editor.ListType.seqData;
import com.example.wisebody.editor.R;
import com.example.wisebody.editor.Util.ItemTouchHelper.Extension;
import com.example.wisebody.editor.Util.ItemTouchHelper.ItemTouchHelperExtension;
import com.example.wisebody.editor.Util.Util;
import com.example.wisebody.editor.subEditor;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;


/**
 * Created by wisebody on 2016. 8. 2..
 */
public class dubListAdapter extends RecyclerView.Adapter<dubListAdapter.ViewHolder> {
    private List<dubData> items;
    private int itemLayout;
    public int selected_position = -1;
    MediaRecorder recorder;
    Context context;
    EMVideoView preview;
    WebView webView;
    Button pasueBtn;
    RecyclerView seqRecylcer;
    LinearLayout secCont;
    LinearLayout editmodeBtnCont;
    InputMethodManager mgr;
    Util util;
    private ItemTouchHelperExtension mItemTouchHelperExtension;

    public dubListAdapter(List<dubData> items, int itemLayout, EMVideoView preview, WebView webView, Button pauseBtn, RecyclerView seqRecylcer, LinearLayout secCont, LinearLayout editmodeBtnCont) {
        this.items = items;
        this.itemLayout = itemLayout;
        this.preview = preview;
        this.webView = webView;
        this.pasueBtn = pauseBtn;
        this.seqRecylcer = seqRecylcer;
        this.secCont = secCont;
        this.editmodeBtnCont = editmodeBtnCont;
    }
    public void setItemTouchHelperExtension(ItemTouchHelperExtension itemTouchHelperExtension) {
        mItemTouchHelperExtension = itemTouchHelperExtension;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(itemLayout, parent, false);
        context = parent.getContext();
        return new ViewHolder(v);
    }



    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final dubData item = items.get(position);
        ((subEditor)context).mItemTouchHelper.closeOpenedPreItem();
        holder.deletebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = holder.getAdapterPosition();
                boolean isChange = false;
                int count = 0;
                webView.loadUrl("javascript:removeRegion('"+items.get(position).id+"');");
                items.remove(position);
                notifyItemRemoved(position);
                Collections.sort(items);
                JSONArray jsonArray = new JSONArray();
                JSONObject jsonobj;
                for(int i =0;i<items.size();i++) {
                    dubData item = items.get(i);
                    try {
                        jsonobj = new JSONObject();
                        jsonobj.put("start", item.start);
                        jsonobj.put("end", item.end);
                        jsonobj.put("id", item.id);
                        jsonArray.put(jsonobj);
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
                if(position!=items.size()) {
                    isChange = true;
                    count = items.size() - position;
                    notifyItemRangeChanged(position, count);
                }
                webView.loadUrl("javascript:setseqArray(" +jsonArray+", " +isChange+", "+ position +", "+ count +" );");
                if(selected_position==position) {
                    selected_position = -1;
                    editmodeBtnCont.setVisibility(View.GONE);
                    secCont.setVisibility(View.VISIBLE);
                }
            }
        });
        holder.time.setText(item.time);
        holder.seq_index.setText(Integer.toString(position+1));
        if(selected_position == position){
            holder.time.setTextColor(Color.parseColor("#de000000"));
            holder.seq_text.setTextColor(Color.parseColor("#de000000"));
            holder.seq_index.setTextColor(Color.parseColor("#ffffff"));
            holder.seqIndexCont.setBackgroundResource(R.drawable.seqnumcontselected);
            holder.swipeCont.setBackgroundColor(Color.parseColor("#f3fcfd"));
        }else{
            holder.time.setTextColor(Color.parseColor("#61333333"));
            holder.seq_text.setTextColor(Color.parseColor("#61333333"));
            holder.seq_index.setTextColor(Color.parseColor("#61000000"));
            holder.seqIndexCont.setBackgroundResource(R.drawable.seqnumcontunsel);
            holder.swipeCont.setBackgroundColor(Color.parseColor("#ffffff"));
        }
                holder.swipeCont.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // 첫 선택
                        if (selected_position == -1) {
                            selected_position = holder.getAdapterPosition();
                            webView.loadUrl("javascript:selectSeq('" + items.get(selected_position).id + "', '#00e4f5');");
                        }
                        else {
                            //선택 해제하기
                            if(selected_position==holder.getAdapterPosition()) {
                                webView.loadUrl("javascript:selectSeq('" + items.get(selected_position).id + "', '#ffffff');");
                                selected_position = -1;
                            }
                            //선택 옮기기
                            else {
                                webView.loadUrl("javascript:selectSeq('" + items.get(selected_position).id + "', '#ffffff');");
                                selected_position = holder.getAdapterPosition();
                                webView.loadUrl("javascript:selectSeq('" + items.get(selected_position).id + "', '#00e4f5');");
                            }
                        }
                        if(selected_position!=-1) {

                            preview.pause();
                            preview.setClickable(false);
                            webView.loadUrl("javascript:seekSurfer('" + (int) (Float.parseFloat(items.get(selected_position).start) * 1000) + "');");
                            preview.seekTo((int) (Float.parseFloat(items.get(selected_position).start) * 1000));
                            ((subEditor)context).scrollView.smoothScrollTo(0,holder.swipeCont.getHeight()*(items.size()-selected_position-1));
                        }
                        else
                        {

                        }
                        notifyDataSetChanged();
                    }
                });
    }
    public void startRec(){
        try {
            recorder = new MediaRecorder();
            File file= Environment.getExternalStorageDirectory();
//갤럭시 S4기준으로 /storage/emulated/0/의 경로를 갖고 시작한다.
            String path=file.getAbsolutePath()+"/test.3gp";
            Log.d("Recored",path);

            recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
//첫번째로 어떤 것으로 녹음할것인가를 설정한다. 마이크로 녹음을 할것이기에 MIC로 설정한다.
            recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
//이것은 파일타입을 설정한다. 녹음파일의경우 3gp로해야 용량도 작고 효율적인 녹음기를 개발할 수있다.
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
//이것은 코덱을 설정하는 것이라고 생각하면된다.
            recorder.setOutputFile(path);
//저장될 파일을 저장한
            preview.pause();
            recorder.prepare();
            recorder.start();
            preview.start();
//시작하면된다.
            Toast.makeText(context, "start Record", Toast.LENGTH_LONG).show();
        } catch (IllegalStateException e) {
// TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
// TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    public void stopRec(){
        recorder.stop();
//멈추는 것이다.
        recorder.release();
        recorder = null;
        Toast.makeText(context,"stop Record", Toast.LENGTH_LONG).show();
    }


    @Override
    public int getItemCount() {
        return items.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder implements Extension {
        TextView time;
        public TextView seq_text;
        TextView seq_index;
        public Button deletebtn;
        public View swipeCont;
        public View delCont;
        FrameLayout seqIndexCont;

        public ViewHolder(View itemView) {
            super(itemView);
            time = (TextView) itemView.findViewById(R.id.seq_time);
            seq_text = (TextView) itemView.findViewById(R.id.seq_text);
            seq_index = (TextView) itemView.findViewById(R.id.seq_index);
            seqIndexCont = (FrameLayout) itemView.findViewById(R.id.seq_index_Cont);
            swipeCont =  itemView.findViewById(R.id.swipeCont);
            delCont =  itemView.findViewById(R.id.delCont);
            deletebtn = (Button) itemView.findViewById(R.id.deleteSeq);
        }
        @Override
        public float getActionWidth() {
            return delCont.getWidth();
        }
    }
    public class RecordTask extends AsyncTask<Void,Void,String> {
        MediaRecorder recorder;
        Context context;

        public RecordTask(Context context) {
            this.context = context;

        }
        @Override
        protected String doInBackground(Void... params) {
            try {
                recorder = new MediaRecorder();
                File file= Environment.getExternalStorageDirectory();
//갤럭시 S4기준으로 /storage/emulated/0/의 경로를 갖고 시작한다.
                String path=file.getAbsolutePath()+"/test.3gp";
                Log.d("Recored",path);

                recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
//첫번째로 어떤 것으로 녹음할것인가를 설정한다. 마이크로 녹음을 할것이기에 MIC로 설정한다.
                recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
//이것은 파일타입을 설정한다. 녹음파일의경우 3gp로해야 용량도 작고 효율적인 녹음기를 개발할 수있다.
                recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
//이것은 코덱을 설정하는 것이라고 생각하면된다.
                recorder.setOutputFile(path);
//저장될 파일을 저장한
                recorder.prepare();
                recorder.start();
                Handler handler;
                handler =  new Handler(context.getMainLooper());
                handler.post( new Runnable(){
                    public void run(){
                        Toast.makeText(context, "record start",Toast.LENGTH_LONG).show();
                    }
                });
//시작하면된다.
               // Toast.makeText(context.getApplicationContext(), "start Record", Toast.LENGTH_LONG).show();
            } catch (IllegalStateException e) {
// TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
// TODO Auto-generated catch block
                e.printStackTrace();
            }
            return "fuck";
        }
        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(context.getApplicationContext(),"완료",Toast.LENGTH_SHORT).show();
        }
        /**
         * Process the sample here
         */
    }
}