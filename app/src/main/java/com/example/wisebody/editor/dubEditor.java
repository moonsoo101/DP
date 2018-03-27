package com.example.wisebody.editor;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.IntRange;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.inputmethod.InputMethodManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.devbrackets.android.exomedia.core.video.scale.ScaleType;
import com.devbrackets.android.exomedia.listener.OnBufferUpdateListener;
import com.devbrackets.android.exomedia.listener.OnCompletionListener;
import com.devbrackets.android.exomedia.listener.OnPreparedListener;
import com.devbrackets.android.exomedia.listener.OnSeekCompletionListener;
import com.devbrackets.android.exomedia.ui.widget.EMVideoView;
import com.example.wisebody.editor.Adapter.dubListAdapter;
import com.example.wisebody.editor.Adapter.seqListAdapter;
import com.example.wisebody.editor.AsyncTask.UploadSub;
import com.example.wisebody.editor.ListType.dubData;
import com.example.wisebody.editor.ListType.seqData;
import com.example.wisebody.editor.Util.ItemTouchHelper.ItemTouchHelperCallback;
import com.example.wisebody.editor.Util.ItemTouchHelper.ItemTouchHelperExtension;
import com.example.wisebody.editor.Util.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;

import static java.lang.Float.parseFloat;

public class dubEditor extends Activity implements View.OnClickListener {
    //HorizontalScrollView scrollView;
    private final int MY_PERMISSION_REQUEST_STORAGE = 100;
    String title;
    WebView webView;
    public EMVideoView preview;
    Button playbtn;
    Button pausebtn;
    Button secBtn1;
    Button secBtn2;
    Button secBtn3;
    Button secBtn4;
    Button secBtn5;

    Button uploadBtn;
    Button backBtn;

    FloatingActionButton inputSubBtn;
    ImageButton playSeqBtn;
    ImageButton prevBtn;
    ImageButton eraseBtn;
    ImageButton forwardBtn;

    TextView showSub;
    LinearLayout editmodeBtnCont;
    Boolean isMpPrepared;
    Boolean drawComplete = false;
    ProgressBar webProgressbar;
    ProgressBar videoLoading;
    LinearLayout secCont;
    RecyclerView seqRecylcer;
    public NestedScrollView scrollView;
    LinearLayoutManager seqRecylcerManager;
    dubListAdapter seqRecylcerAdapter;
    ArrayList<dubData> seqLists = new ArrayList<>();
    private final Handler handler = new Handler();
    Boolean autoCalc = false;
    Thread playThread;
    int region_index = 0;
    String autoValue;
    public ItemTouchHelperExtension mItemTouchHelper;
    public ItemTouchHelperExtension.Callback mCallback;

    private class AndroidBridge {
       @JavascriptInterface
        public void setSeqTime(final String start, final String end,final String id) { // must be final
            handler.post(new Runnable() {
                public void run() {
                    boolean isChange = false;
                    int count = 0;

                    Log.d("WaveSay", "setMessage(" + start + ":" + end +":"+id +")");
                    seqLists.add(new dubData(start, end, id));
                    Collections.sort(seqLists);
                    int position = seqLists.indexOf(new dubData(start,end,id));
                    webView.loadUrl("javascript:drawSeq('" + start + "','" + end + "','" + id + "', '"+(position+1)+"');");
                    JSONArray jsonArray = new JSONArray();
                    JSONObject jsonobj;
                    for(int i =0;i<seqLists.size();i++) {
                        dubData item = seqLists.get(i);
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
                    if(position+1!=seqLists.size()) {
                        isChange = true;
                        count = seqLists.size() - position;
                    }
                    webView.loadUrl("javascript:setseqArray(" +jsonArray+", " +isChange+", "+ (position+1) +", "+ (count-1) +" );");
                    seqRecylcerAdapter.notifyItemInserted(position);
                }
            });
        }

        @JavascriptInterface
        public void drawComplete() {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    if (!drawComplete) {
                        playbtn.setVisibility(View.VISIBLE);
                        webProgressbar.setVisibility(View.GONE);
                        preview.setClickable(true);
                        secBtn1.setClickable(true);
                        secBtn2.setClickable(true);
                        secBtn3.setClickable(true);
                        secBtn4.setClickable(true);
                        drawComplete = true;
                        Log.d(getLocalClassName(), "draw!");
                    }
                }
            });
        }
        @JavascriptInterface
        public void seekVideo(final float cur)
        {   handler.post(new Runnable() {
            @Override
            public void run() {
                if(preview.isPlaying())
                    preview.pause();
                playbtn.setVisibility(View.GONE);
                pausebtn.setVisibility(View.GONE);
                preview.setClickable(false);
                preview.seekTo((int)cur);
            }
        });
        }
        @JavascriptInterface
        public void regionIn(int index)
        {
        final int position = index-1;
          handler.post(new Runnable() {
            @Override
            public void run() {
                Util util = new Util();
               scrollView.smoothScrollTo(0,(int)util.pxFromDp(getApplicationContext(),46.6f)*(seqLists.size()-position-1));
                util = null;
            }
        });
        }
        @JavascriptInterface
        public void regionOut()
        {   handler.post(new Runnable() {
            @Override
            public void run() {

            }
        });
        }
        @JavascriptInterface
        public void updateRegion(final String start, final String end)
        {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    dubData item = seqLists.get(seqRecylcerAdapter.selected_position);
                    item.start = start;item.end=end;
                    item.timeFormat();
                    seqRecylcerAdapter.notifyItemChanged(seqRecylcerAdapter.selected_position);
                }
            });
        }
    }
    @SuppressLint("JavascriptInterface")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.subeditor);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
        checkPermission();
        title = getIntent().getStringExtra("title");
        isMpPrepared = false;
        DisplayMetrics metrics = new DisplayMetrics();
        Log.d("TAG", "densityDPI = " + metrics.densityDpi);
        webView = (WebView) findViewById(R.id.edittor);
        webView.setBackgroundColor(Color.parseColor("#000000"));

        playbtn = (Button) findViewById(R.id.playbtn);
        pausebtn = (Button) findViewById(R.id.pausebtn);

        secCont = (LinearLayout) findViewById(R.id.secCont) ;
        secBtn1 = (Button) findViewById(R.id.secBtn1);
        secBtn2 = (Button) findViewById(R.id.secBtn2);
        secBtn3 = (Button) findViewById(R.id.secBtn3);
        secBtn4 = (Button) findViewById(R.id.secBtn4);
        secBtn5 = (Button) findViewById(R.id.secBtn5);

        uploadBtn = (Button) findViewById(R.id.uploadBtn);
        backBtn = (Button) findViewById(R.id.backBtn);

        playSeqBtn = (ImageButton) findViewById(R.id.playSeqBtn);
        eraseBtn = (ImageButton) findViewById(R.id.eraseBtn);
        prevBtn = (ImageButton) findViewById(R.id.prevBtn);
        forwardBtn  = (ImageButton) findViewById(R.id.forwardBtn);
        inputSubBtn = (FloatingActionButton) findViewById(R.id.inputSubBtn);

        webProgressbar = (ProgressBar) findViewById(R.id.webProgressbar);
        videoLoading = (ProgressBar) findViewById(R.id.videoLoading);
        seqRecylcer = (RecyclerView) findViewById(R.id.seqRecycler);

        showSub = (TextView) findViewById(R.id.showSub);
        editmodeBtnCont = (LinearLayout) findViewById(R.id.editmodeBtnCont);
        editmodeBtnCont.setVisibility(View.INVISIBLE);
        scrollView = (NestedScrollView) findViewById(R.id.nestedScroll);

        preview = (EMVideoView) findViewById(R.id.preview);
        preview.setOnSeekCompletionListener(new OnSeekCompletionListener() {
            @Override
            public void onSeekComplete() {
                playbtn.setVisibility(View.VISIBLE);
                preview.setClickable(true);
                playSeqBtn.setClickable(true);
            }
        });
        preview.setOnBufferUpdateListener(new OnBufferUpdateListener() {
            @Override
            public void onBufferingUpdate(@IntRange(from = 0L, to = 100L) int percent) {
            }
        });

        preview.setOnPreparedListener(new OnPreparedListener() {
            @Override
            public void onPrepared() {
                webView.clearCache(true);
                webView.loadUrl("http://203.233.111.62/test/waveform.html");
                isMpPrepared = true;
                videoLoading.setVisibility(View.GONE);

            }
        });
        preview.setOnCompletionListener(new OnCompletionListener() {
            @Override
            public void onCompletion() {
                videoLoading.setVisibility(View.VISIBLE);
                preview.setVideoURI(Uri.parse("http://203.233.111.62/test/media/clip2/"+title+"blur.mp4"));
                seqLists.clear();
                seqRecylcerAdapter.notifyDataSetChanged();
            }
        });
        seqRecylcerAdapter = new dubListAdapter(seqLists, R.layout.seq_item, preview, webView, pausebtn, seqRecylcer, secCont, editmodeBtnCont);
        seqRecylcerManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        seqRecylcerManager.setReverseLayout(true);
        seqRecylcerManager.setStackFromEnd(true);
        seqRecylcer.setLayoutManager(seqRecylcerManager);
        seqRecylcer.setAdapter(seqRecylcerAdapter);
        mCallback = new ItemTouchHelperCallback();
        mItemTouchHelper = new ItemTouchHelperExtension(mCallback);
        mItemTouchHelper.attachToRecyclerView(seqRecylcer);
        seqRecylcerAdapter.setItemTouchHelperExtension(mItemTouchHelper);

        preview.setOnClickListener(this);
        secBtn1.setOnClickListener(this);
        secBtn2.setOnClickListener(this);
        secBtn3.setOnClickListener(this);
        secBtn4.setOnClickListener(this);
        secBtn5.setOnClickListener(this);

        uploadBtn.setOnClickListener(this);
        backBtn.setOnClickListener(this);

        playSeqBtn.setOnClickListener(this);
        eraseBtn.setOnClickListener(this);
        prevBtn.setOnClickListener(this);
        forwardBtn.setOnClickListener(this);
        inputSubBtn.setOnClickListener(this);

        preview.setClickable(false);
        secBtn1.setClickable(false);
        secBtn2.setClickable(false);
        secBtn3.setClickable(false);
        secBtn4.setClickable(false);
        playSeqBtn.setClickable(false);
        playbtn.setVisibility(View.INVISIBLE);
        pausebtn.setVisibility(View.INVISIBLE);
        playbtn.setOnClickListener(this);
        pausebtn.setOnClickListener(this);
        webView.setBackgroundColor(0x00000000);
        webView.setLayerType(WebView.LAYER_TYPE_SOFTWARE, null);
        webView.clearHistory();
        webView.clearCache(true);
        WebView.setWebContentsDebuggingEnabled(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.addJavascriptInterface(new AndroidBridge(), "Maru");
        webView.setWebViewClient(new WebViewClient() {

            public void onPageFinished(WebView view, String url) {
                Log.d("webview width", Integer.toString(view.getWidth()));
                view.loadUrl("javascript:checkWidth_dur('" + view.getWidth() + "','" + preview.getDuration() + "','"+title+"');");
            }
        });
        webView.setWebChromeClient(new WebChromeClient() {

            public void onPageFinished(WebView view, String url) {
                Log.d("webview width", Integer.toString(view.getWidth()));
                view.loadUrl("javascript:checkWidth_dur('" + view.getWidth() + "','" + preview.getDuration() + "');");
            }
        });
        preview.setScaleType(ScaleType.CENTER_CROP);
        preview.setVideoURI(Uri.parse("http://203.233.111.62/test/media/clip2/"+title+"blur.mp4"));
        final RelativeLayout rootView = (RelativeLayout) findViewById(R.id.rootView);
        rootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                Rect r = new Rect();
                rootView.getWindowVisibleDisplayFrame(r);
                int screenHeight = rootView.getRootView().getHeight();

                // r.bottom is the position above soft keypad or device button.
                // if keypad is shown, the r.bottom is smaller than that before.
                int keypadHeight = screenHeight - r.bottom;

                Log.d("subEditor", "keypadHeight = " + keypadHeight);

                if (keypadHeight > screenHeight * 0.15) { // 0.15 ratio is perhaps enough to determine keypad height.
                    // keyboard is opened
                    if(inputSubBtn.getVisibility()==View.GONE) {
                        editmodeBtnCont.setVisibility(View.VISIBLE);
                        secCont.setVisibility(View.GONE);
                        Animation animation = new AlphaAnimation(0, 1);
                        animation.setDuration(1000);
                        inputSubBtn.setVisibility(View.VISIBLE);
                        inputSubBtn.setAnimation(animation);
                        Log.d("subEditor", "keyboard open");
                    }
                }
                else {
                    if(inputSubBtn.getVisibility()==View.VISIBLE) {
                        Animation animation = new AlphaAnimation(1, 0);
                        animation.setDuration(1000);
                        editmodeBtnCont.setVisibility(View.GONE);
                        secCont.setVisibility(View.VISIBLE);
                        int position;
                        position = seqRecylcerAdapter.selected_position;
                        if(position!=-1) {
                            webView.loadUrl("javascript:selectSeq('" + seqLists.get(position).id + "', '#ffffff');");
                            seqRecylcerAdapter.selected_position = -1;
                            seqRecylcerAdapter.notifyItemChanged(position);
                        }
                        inputSubBtn.setVisibility(View.GONE);
                        inputSubBtn.setAnimation(animation);
                        Log.d("subEditor", "keyboard close");
                    }
                }
            }
        });

    }
    @Override
    public void onPause()
    {
        if(preview.isPlaying()) {
            preview.pause();
            pausebtn.setVisibility(View.VISIBLE);
        }
        super.onPause();
    }
    @Override
    public void onResume()
    {
        super.onResume();
    }

    @Override
    public void onClick(View v)
    {
        mItemTouchHelper.closeOpenedPreItem();
        switch (v.getId()) {
            case R.id.preview :
                if (preview != null) {
                    if (preview.isPlaying()) {
                        preview.pause();
                        playbtn.setVisibility(View.VISIBLE);
                    } else if (!preview.isPlaying() && isMpPrepared) {
                        //thumb.setVisibility(View.GONE);
                        preview.start();
                        makePlayThread("normal");
                        playbtn.setVisibility(View.INVISIBLE);
                    }
                }
                break;
            case R.id.playbtn :
                if (isMpPrepared) {
                    preview.start();
                    makePlayThread("noraml");
                    playbtn.setVisibility(View.INVISIBLE);
                }
                break;
//            case R.id.pausebtn :
//                if (isMpPrepared) {
//                    preview.start();
//                    secCont.setVisibility(View.VISIBLE);
//                    editmodeBtnCont.setVisibility(View.INVISIBLE);
//                   // inputCont.setVisibility(View.GONE);
//                    makePlayThread("normal");
//                    //thumb.setVisibility(View.GONE);
//                    pausebtn.setVisibility(View.INVISIBLE);
//                }
//                break;
            case R.id.playSeqBtn :
                if(seqRecylcerAdapter.selected_position!=-1) {
                    if (preview.isPlaying()) {
                        preview.pause();
                        pausebtn.setVisibility(View.VISIBLE);
                    } else {
                        preview.start();
                        makePlayThread("replay");
                        pausebtn.setVisibility(View.INVISIBLE);
                    }
                }
                break;
            case R.id.prevBtn :
                if(seqRecylcerAdapter.selected_position!=-1) {
                    if (seqRecylcerAdapter.selected_position != 0) {
                        webView.loadUrl("javascript:selectSeq('" + seqLists.get(seqRecylcerAdapter.selected_position).id + "', '#ffffff');");
                        seqRecylcerAdapter.selected_position += -1;
                        webView.loadUrl("javascript:selectSeq('" + seqLists.get(seqRecylcerAdapter.selected_position).id + "', '#00e4f5');");
                        Log.d("scroll prev", Integer.toString(seqRecylcerAdapter.selected_position));
                        seqRecylcerAdapter.notifyItemRangeChanged(seqRecylcerAdapter.selected_position,2);
                        Util util = new Util();
                        scrollView.smoothScrollTo(0,(int)util.pxFromDp(getApplicationContext(),46.6f)*(seqLists.size()-seqRecylcerAdapter.selected_position-1));
                        util = null;
                        seekAndroidViedo((int) (parseFloat(seqLists.get(seqRecylcerAdapter.selected_position).start) * 1000));
                    }
                }
                break;
            case R.id.forwardBtn :
                if(seqRecylcerAdapter.selected_position!=-1) {
                    if (seqRecylcerAdapter.selected_position != seqLists.size()-1)  {
                        webView.loadUrl("javascript:selectSeq('" + seqLists.get(seqRecylcerAdapter.selected_position).id + "', '#ffffff');");
                        seqRecylcerAdapter.selected_position += 1;
                        webView.loadUrl("javascript:selectSeq('" + seqLists.get(seqRecylcerAdapter.selected_position).id + "', '#00e4f5');");
                        Log.d("scroll next", Integer.toString(seqRecylcerAdapter.selected_position));
                        seqRecylcerAdapter.notifyItemRangeChanged(seqRecylcerAdapter.selected_position-1,2);
                        Util util = new Util();
                        scrollView.smoothScrollTo(0,(int)util.pxFromDp(getApplicationContext(),46.6f)*(seqLists.size()-seqRecylcerAdapter.selected_position-1));
                        util = null;
                        seekAndroidViedo((int) (parseFloat(seqLists.get(seqRecylcerAdapter.selected_position).start) * 1000));
                    }
                }
                break;
            case R.id.eraseBtn :
                int selectPos = seqRecylcerAdapter.selected_position;
                if(selectPos !=-1)
                {
                    if(seqLists.get(selectPos).path!=null)
                    seqLists.get(selectPos).path=null;
                    seqRecylcerAdapter.notifyItemChanged(selectPos);
                }
                break;
            case R.id.inputSubBtn :
                if(seqRecylcerAdapter.selected_position!=-1) {
                    seqLists.get(seqRecylcerAdapter.selected_position).path = "";
                    seqRecylcerAdapter.notifyItemChanged(seqRecylcerAdapter.selected_position);
                }
                break;
            case R.id.uploadBtn :
//                int size = seqLists.size();
//                String subText = "";
//                String subName = "test";
//                if(seqLists.size()>0) {
//                    for (int i=0; i< size;i ++) {
//                        seqData temp = seqLists.get(i);
//                        int start = (int)(parseFloat(temp.start)*1000);
//                        Log.d("start",Integer.toString(start)+":"+start);
//                        int end = (int)(parseFloat(temp.end)*1000);
//                        String Shour = String.format("%02d",start/(60*60*1000));
//                        String Smin = String.format("%02d",start/(60*1000)%60);
//                        String Ssec = String.format("%02d",start/1000%60);
//                        String Smsec = String.format("%02d",start%1000);
//                        String Ehour = String.format("%02d",end/(60*60*1000));
//                        String Emin = String.format("%02d",end/(60*1000)%60);
//                        String Esec = String.format("%02d",end/1000%60);
//                        String Emsec = String.format("%02d",end%1000);
//                        String text = temp.text;
//                        subText = subText+(i+1)+"\n"+Shour+":"+Smin+":"+Ssec+","+Smsec+" --> "+
//                                Ehour+":"+Emin+":"+Esec+","+Emsec+"\n"+text+"\n\n";
//                    }
//                    uploadSub(subText,subName);
//                }
//                else
//                    Toast.makeText(getApplicationContext(),"자막 입력하고 저장해",Toast.LENGTH_LONG).show();
                finish();
                break;
            case R.id.backBtn :
                finish();
            case R.id.secBtn5 :
                if(autoValue!=null) {
                    int current = preview.getCurrentPosition();
                    boolean overlap = checkOverlap(parseFloat(autoValue),current,0);
                    if(!overlap) {
                        preview.setClickable(true);
                        preview.start();
                        makePlayThread("normal");
                        webView.loadUrl("javascript:addSeq('" + current + "','" + autoValue + "','" + region_index + "');");
                        region_index++;
                    }
                    else {
                        Toast.makeText(getApplicationContext(), "시퀀스 겹침", Toast.LENGTH_SHORT).show();
                        preview.setClickable(true);
                        playbtn.setVisibility(View.VISIBLE);
                    }
                }
                break;
            default :
                Log.d("button","눌림");
                //Log.d("nested",Integer.toString(nestedScrollView.computeVerticalScrollRange()));
                String value = ((Button)v).getText().toString();
                autoValue = value;
                int current = preview.getCurrentPosition();
                boolean overlap = checkOverlap(parseFloat(value),current,0);
                if(!overlap) {
                    preview.setClickable(true);
                    preview.start();
                    makePlayThread("normal");
                    webView.loadUrl("javascript:addSeq('" + current + "','" + value + "','" + region_index + "');");
                    region_index++;
                }
                else {
                    if(autoCalc) {
                        Toast.makeText(getApplicationContext(), "시퀀스 길이 자동 계산", Toast.LENGTH_SHORT).show();
                        autoCalc = false;
                    }
                    else {
                        Toast.makeText(getApplicationContext(), "시퀀스 겹침", Toast.LENGTH_SHORT).show();
                        preview.setClickable(true);
                        playbtn.setVisibility(View.VISIBLE);
                    }
                }
        }

    }
    public void makePlayThread(final String mode) {
        playThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (preview!=null&&preview.isPlaying()) {
                    webView.post(new Runnable() {
                        @Override
                        public void run() {
                            int selected_position = seqRecylcerAdapter.selected_position;
                            if(selected_position<0)
                                selected_position=0;
                            int cur = preview.getCurrentPosition();
                            if(mode.equals("normal"))
                            webView.loadUrl("javascript:playSurfer('" + cur + "');");
                           else
                            {
                                webView.loadUrl("javascript:playSurfer('" + cur + "');");
                                if(seqLists.size()>0&&cur>= parseFloat(seqLists.get(selected_position).end)*1000)
                                  seekAndroidViedo((int)(parseFloat(seqLists.get(selected_position).start)*1000));
                            }
                        }
                    });
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        playThread.start();
    }
     public void seekAndroidViedo(int position)
     {
         playbtn.setVisibility(View.GONE);
         preview.setClickable(false);
         playSeqBtn.setClickable(false);
         if(preview.isPlaying())
         preview.pause();
         preview.seekTo(position);
         webView.loadUrl("javascript:seekSurfer('"+position+"');");
     }
    public boolean checkOverlap(float offset, int current, int index)
    {

        preview.pause();
        preview.setClickable(false);
        int checkTime = current - (int)(offset*1000);
        //0밑으로 내려가려고 할
        if(checkTime<0)
        {
            autoCalc = true;
            preview.setClickable(true);
            preview.start();
            makePlayThread("normal");
            webView.loadUrl("javascript:addSeq('" + current + "','" + (float)(current)/1000 + "','" + region_index + "');");
            autoValue = Float.toString((float)current/1000);
            region_index++;
            return true;
        }
        int size = seqLists.size();
        dubData temp;
        for(int i=index;i<size;i++)
        {
            temp = seqLists.get(i);
            int start = (int)(parseFloat(temp.start)*1000);
            int end = (int)(parseFloat(temp.end)*1000);
            if(current == end)
                return true;
            else if(start<=checkTime&&end>checkTime) {
                if(end<current) {
                    if(size>i+1)
                    {
                        boolean checkNext = checkOverlap(offset, current, i+1);
                        if(checkNext)
                            return true;
                    }
                    autoCalc = true;
                    preview.setClickable(true);
                    preview.start();
                    makePlayThread("normal");
                    webView.loadUrl("javascript:addSeq('" + current + "','" + (float)(current - end)/1000 + "','" + region_index + "');");
                    region_index++;
                }
                return true;
            }
            else if(start>=checkTime&&end<=current) {
                if (end < current) {
                    if(size>i+1)
                    {
                        boolean checkNext = checkOverlap(offset, current, i+1);
                        if(checkNext)
                            return true;
                    }
                    autoCalc = true;
                    preview.setClickable(true);
                    preview.start();
                    makePlayThread("normal");
                    webView.loadUrl("javascript:addSeq('" + current + "','" + (float)(current - end)/1000 + "','" + region_index + "');");
                    region_index++;
                }
                return true;
            }
            else if(start<current&&end>=current)
                return true;
        }
        return false;
    }
    public void uploadSub(String subText, String subName)
    {
                UploadSub uploadSub = new UploadSub(subText,subName);
                uploadSub.uploadToServer();
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void checkPermission() {
        Log.i("subEditor", "CheckPermission : " + checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE));
        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED
                || checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED || checkSelfPermission(Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                // Explain to the user why we need to write the permission.
                Toast.makeText(this, "Read/Write external storage", Toast.LENGTH_SHORT).show();
            }

            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO},
                    MY_PERMISSION_REQUEST_STORAGE);

            // MY_PERMISSION_REQUEST_STORAGE is an
            // app-defined int constant

        } else {

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSION_REQUEST_STORAGE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED&& grantResults[2] == PackageManager.PERMISSION_GRANTED) {


                    // permission was granted, yay! do the
                    // calendar task you need to do.

                } else {

                    Log.d("permission", "Permission always deny");

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                break;
        }
    }

}