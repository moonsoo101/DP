package com.example.wisebody.editor.Player;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.SurfaceTexture;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.AppBarLayout;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.wisebody.editor.MainFeed;
import com.example.wisebody.editor.MainPage;
import com.example.wisebody.editor.R;
import com.example.wisebody.editor.Util.Util;
import com.google.android.exoplayer.metadata.MetadataTrackRenderer;

import java.io.IOException;
import java.util.TimerTask;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by wisebody on 2017. 2. 9..
 */

public class MainPagePlayer extends TextureView implements TextureView.SurfaceTextureListener {

    private static String TAG = "MainPagePlayer";


    public boolean isMpPrepared;
    boolean detached;
    public boolean play;
    public boolean pause= false;
    public int pauseTime;
    Util util = new Util();
    String url;
    MediaPlayer mediaPlayer;
    Surface surface;
    Button playbutton, pausebutton;
    FrameLayout optCont, subFrame;
    public ImageView thumb;
    AppBarLayout appBarLayout;
    TextView durTextview,curTextview;
    SeekBar seekBar;
    Context context;
    Runnable opthiding;
    Handler optHandler;
    Thread curTimeThread;
    ProgressBar progressBar;
    RecyclerView recVidRecy;
    ViewGroup.MarginLayoutParams params;


    private static final int MAX_CLICK_DURATION = 1000;

    /**
     * Max allowed distance to move during a "click", in DP.
     */
    private static final int MAX_CLICK_DISTANCE = 15;

    float RecVidMaxMargin;
    float upThreshold;
    float orginRecVidMargin;

    private long pressStartTime;
    private float pressedX;
    private float pressedY;
    float moveY;



    public MainPagePlayer(final Context context, final ImageView thumb, final FrameLayout optCont, Button playbutton, Button pausebutton, AppBarLayout appBarLayout, FrameLayout subFrame, TextView durTextview, final TextView curTextview, SeekBar seekBar, final ProgressBar progressBar, final RecyclerView recVidRecy) {
        super(context);
        this.thumb = thumb;
        this.context = context;
        this.playbutton = playbutton;
        this.pausebutton = pausebutton;
        this.optCont = optCont;
        this.appBarLayout = appBarLayout;
        this.subFrame = subFrame;
        this.curTextview = curTextview;
        this.durTextview = durTextview;
        this.seekBar = seekBar;
        this.progressBar = progressBar;
        this.recVidRecy = recVidRecy;
        this.setClickable(false);
        RecVidMaxMargin = util.pxFromDp(context,10);
        upThreshold = util.pxFromDp(context,60);
        params = (ViewGroup.MarginLayoutParams)recVidRecy.getLayoutParams();
        orginRecVidMargin = util.pxFromDp(context,-90);
        pausebutton.setVisibility(INVISIBLE);
        opthiding = new Runnable() {
            @Override
            public void run() {
                Animation animation = new AlphaAnimation(1, 0);
                animation.setDuration(500);
                ((MainPage)context).hideSystemBar();
                optCont.setVisibility(INVISIBLE);
                optCont.setAnimation(animation);
                params.bottomMargin = (int)orginRecVidMargin;
                recVidRecy.requestLayout();
                Log.d("click","video visible");
            }
        };
        optHandler = new Handler();
        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation animation = new AlphaAnimation(0, 1);
                animation.setDuration(500);
                ((MainPage)context).showSystemBar();
                optCont.setVisibility(VISIBLE);
                optCont.setAnimation(animation);
                Log.d("click","video visible");
                optHandler.removeCallbacks(opthiding);
                optHandler.postDelayed(opthiding,2000);
            }
        });
        thumb.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Animation animation = new AlphaAnimation(0, 1);
                animation.setDuration(500);
                optCont.setVisibility(VISIBLE);
                optCont.setAnimation(animation);
                Log.d(TAG,"Thumb Click");
                optHandler.removeCallbacks(opthiding);
                optHandler.postDelayed(opthiding,2000);
            }
        });

        optCont.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction())
                {
                    case MotionEvent.ACTION_DOWN :
                        optHandler.removeCallbacks(opthiding);
                        pressStartTime = System.currentTimeMillis();
                        pressedX = motionEvent.getX();
                        pressedY = motionEvent.getY();
                        break;
                    case MotionEvent.ACTION_MOVE :
                        if(moveY>motionEvent.getRawY())
                        {
                            if(((MainPage)context).screenState==1) {
                                params.bottomMargin +=10;
                                if(params.bottomMargin>RecVidMaxMargin)
                                    params.bottomMargin = (int)RecVidMaxMargin;
                                recVidRecy.setLayoutParams(params);
                                recVidRecy.requestLayout();
                            }
                        }

                        moveY = motionEvent.getRawY();
                        Log.d(TAG,"move" + moveY);
                        break;
                    case MotionEvent.ACTION_UP :
                        long pressDuration = System.currentTimeMillis() - pressStartTime;
                        if (pressDuration < MAX_CLICK_DURATION && distance(pressedX, pressedY, motionEvent.getX(), motionEvent.getY()) < MAX_CLICK_DISTANCE) {
                            // Click event has occurred
                            optHandler.removeCallbacks(opthiding);
                            optCont.setVisibility(VISIBLE);
                            Animation animation = new AlphaAnimation(1, 0);
                            animation.setDuration(500);
                            ((MainPage) context).hideSystemBar();
                            optCont.setVisibility(INVISIBLE);
                            Animation valueAni = new TranslateAnimation(0,0,0,orginRecVidMargin);
                            valueAni.setDuration(500);
                            //params.bottomMargin = (int)orginRecVidMargin;
                            recVidRecy.setAnimation(valueAni);
                            //recVidRecy.requestLayout();
                            optCont.setAnimation(animation);
                        }
                        else
                        {
                            optHandler.removeCallbacks(opthiding);
                            optCont.setVisibility(VISIBLE);
                            optHandler.postDelayed(opthiding, 2000);
                        }

                }
                return false;
            }
        });
        recVidRecy.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction())
                {
                    case MotionEvent.ACTION_DOWN :
                        Log.d(TAG,"recydown");
                        break;
                    case MotionEvent.ACTION_MOVE :
                        optHandler.removeCallbacks(opthiding);
                        Log.d(TAG,"recymove");
                        break;
                    case MotionEvent.ACTION_UP :
                        optHandler.removeCallbacks(opthiding);
                        optCont.setVisibility(VISIBLE);
                        optHandler.postDelayed(opthiding, 2000);
                }
                return false;
            }
        });

        seekBar.setEnabled(false);
        seekBar.setFocusable(false);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if(b) {
                        optCont.setVisibility(VISIBLE);
                    optHandler.removeCallbacks(opthiding);
                    if(isMpPrepared) {
                        if(mediaPlayer!=null) {
                            curTimeThread = null;
                            progressBar.setVisibility(VISIBLE);
                            mediaPlayer.seekTo(i);
                        }
                    }
                    else {
                        if(mediaPlayer==null) {
                            curTextview.setText(String.format("%02d",i/1000/60)+":"+String.format("%02d",i/1000%60));
                            pauseTime = i;
                            Log.d(TAG, "seekbar change from user not prepared and null" + i + "MAx:"+seekBar.getMax());
                        }
                    }
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                optHandler.removeCallbacks(opthiding);
                seekBar.setThumb(context.getDrawable(R.drawable.videoseekbarthumb_pressed));
                Log.d(TAG,"seek bar start");
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                ((MainPage)context).collapseToolbar();
                optHandler.postDelayed(opthiding,2000);
                seekBar.setThumb(context.getDrawable(R.drawable.videoseekbarthumb));
                if(!play) {
                    play = true;
                    loadVideo(url);
                }
            }
        });
        setSurfaceTextureListener(this);
    }
    public MainPagePlayer(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public void loadVideo(String localPath) {

        this.url = localPath;
        if (this.isAvailable()) {
            Log.d(TAG,"Avail");
            if(!detached&&play) {
                Log.d(TAG,"!detached");
                prepareVideo(getSurfaceTexture());
            }
            else
                Log.d(TAG,"detached");
        }
        else
            Log.d(TAG,"not avail");

    }

    @Override
    public void onSurfaceTextureAvailable(final SurfaceTexture surface, int width, int height) {
        isMpPrepared = false;
        progressBar.setVisibility(VISIBLE);
            prepareVideo(surface);
            Log.d(TAG,"avail"+Boolean.toString(play));
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {

        if(mediaPlayer!=null)
        {
            if(isPlaying())
                mediaPlayer.stop();
            mediaPlayer.reset();
            mediaPlayer.release();
            mediaPlayer = null;
            this.setVisibility(GONE);
            progressBar.setVisibility(INVISIBLE);
            playbutton.setVisibility(VISIBLE);
            thumb.setVisibility(VISIBLE);
            subFrame.setVisibility(VISIBLE);
            isMpPrepared = false;
            detached = true;
            play = false;
            pause = false;
            Log.d("customerplayer","destroy");
        }
        ((MainPage)context).getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {
    }

    public void prepareVideo(final SurfaceTexture t)
    {

        this.surface = new Surface(t);
        mediaPlayer = new MediaPlayer();

        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            public void onPrepared(final MediaPlayer mp) {
                if(!detached) {
                    isMpPrepared = true;
                    MainPagePlayer.this.setVisibility(VISIBLE);
                    if (isMpPrepared&&play) {
                        playbutton.setVisibility(GONE);
                        pausebutton.setVisibility(VISIBLE);
                        thumb.setVisibility(INVISIBLE);
                        subFrame.setVisibility(INVISIBLE);
                        seekBar.setEnabled(true);
                        seekBar.setMax(getDuration());
                        if(pauseTime!=-1||pauseTime!=0) {
                            progressBar.setVisibility(VISIBLE);
                            mediaPlayer.seekTo(pauseTime);
                            Log.d(TAG,"prepare-> seek");
                        }
                        else {
                            Log.d(TAG,"prepare-> play");
                            play();
                            progressBar.setVisibility(INVISIBLE);
                        }
                        mediaPlayer.setOnInfoListener(new MediaPlayer.OnInfoListener() {

                            @Override
                            public boolean onInfo(MediaPlayer mp, int what, int extra) {
                                switch (what) {
                                    case MediaPlayer.MEDIA_INFO_BUFFERING_START:
                                        progressBar.setVisibility(View.VISIBLE);
                                        break;
                                    case MediaPlayer.MEDIA_INFO_BUFFERING_END:
                                        progressBar.setVisibility(View.GONE);
                                        break;
                                }
                                return false;
                            }
                        });

                        mediaPlayer.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
                            @Override
                            public void onBufferingUpdate(MediaPlayer mediaPlayer, int i) {
                                double ratio = i / 100.0;
                                int bufferingLevel = (int)(getDuration() * ratio);
                                seekBar.setSecondaryProgress(bufferingLevel);
                            }
                        });
                        curTimeThread();
                        int durSec = getDuration()/1000;
                        durTextview.setText(String.format("%02d",durSec/60)+":"+String.format("%02d",durSec%60));
                        ((MainPage)context).collapseToolbar();
                        optCont.setClickable(true);

                    }
                    Log.d("customplayer", "start");
                }
            }
        });
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                isMpPrepared = false;
                play = false;
                pauseTime = -1;
                seekBar.setProgress(0);
                curTextview.setText("00:00");
                ((MainPage)context).expandToolbar();
                subFrame.setVisibility(VISIBLE);
                thumb.setVisibility(VISIBLE);
                pausebutton.setVisibility(GONE);
                playbutton.setVisibility(VISIBLE);
                MainPagePlayer.this.setVisibility(GONE);
                if(mediaPlayer!=null) {
                    mediaPlayer.reset();
                    mediaPlayer.release();
                    mediaPlayer = null;
                }
                Log.d("mediastate","complete");
            }
        });
        mediaPlayer.setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener() {
            @Override
            public void onSeekComplete(MediaPlayer mp) {
                pause=false;
                play();
                progressBar.setVisibility(INVISIBLE);
                subFrame.setVisibility(INVISIBLE);
                thumb.setVisibility(INVISIBLE);
                playbutton.setVisibility(INVISIBLE);
                pausebutton.setVisibility(VISIBLE);
                Log.d("MainPagePlayer","seek and start");
            }
        });

        try {
            mediaPlayer.setDataSource(url);
            mediaPlayer.setSurface(this.surface);
            mediaPlayer.prepareAsync();
            Log.d("mediastate","loading");
        } catch (IllegalArgumentException e1) {
            e1.printStackTrace();
        } catch (SecurityException e1) {
            e1.printStackTrace();
        } catch (IllegalStateException e1) {
            e1.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        try {

        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        detached = false;
        //this.setVisibility(INVISIBLE);
        Log.d("mediastate","attach");
    }
    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mediaPlayer != null) {
            if(isPlaying())
                mediaPlayer.stop();
            mediaPlayer.reset();
            mediaPlayer.release();
            mediaPlayer = null;
            detached = true;
            play = false;
            pause = false;
            Log.d("detatch", "detach");
        }
        ((MainPage)context).getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        this.setVisibility(GONE);
        subFrame.setVisibility(VISIBLE);
        thumb.setVisibility(VISIBLE);
        playbutton.setVisibility(VISIBLE);
        Log.d("detatch", "detach");
    }

    @Override
    protected void onVisibilityChanged(View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);

    }

    public void play()
    {
        if(mediaPlayer!=null) {
            ((MainPage)context).getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            if (!isPlaying() && play && isMpPrepared) {
                if(pause) {
                    ((MainPage)context).collapseToolbar();
                    mediaPlayer.seekTo(pauseTime);
                    playbutton.setVisibility(INVISIBLE);
                    pausebutton.setVisibility(VISIBLE);
                    Log.d(TAG,"pause to play");

                }
                else {
                    ((MainPage)context).collapseToolbar();
                    pause=false;
                    mediaPlayer.start();
                    mediaPlayer.selectTrack(2);
                    curTimeThread();
                    progressBar.setVisibility(INVISIBLE);
                    subFrame.setVisibility(INVISIBLE);
                    thumb.setVisibility(INVISIBLE);
                    playbutton.setVisibility(INVISIBLE);
                    pausebutton.setVisibility(VISIBLE);
                }
            }
        }
        else
        {
            loadVideo(url);
        }

    }

    public void pause()
    {
        if(mediaPlayer!=null) {
            if(isPlaying()) {
                pause = true;
                mediaPlayer.pause();
                pauseTime = mediaPlayer.getCurrentPosition();
                Log.d(TAG,"pause"+pauseTime);
            }
            else if(mediaPlayer.getCurrentPosition()<=0) {
                stop();
                Log.d(TAG,"stop"+pauseTime);
            }
            ((MainPage)context).getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            thumb.setVisibility(VISIBLE);
            pausebutton.setVisibility(INVISIBLE);
            playbutton.setVisibility(VISIBLE);
            ((MainPage)context).expandToolbar();
        }
        play = false;
        Log.d("mediastate","pause :"+pause);
    }

    public void stop()
    {
        if(mediaPlayer!=null) {
            if(isPlaying())
            mediaPlayer.stop();
            mediaPlayer.reset();
            mediaPlayer.release();
            mediaPlayer = null;
            Log.d("customplayer","stop!");
            ((MainPage)context).expandToolbar();
        }
        play=false;
        pause=false;
        isMpPrepared = false;
        ((MainPage)context).getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        this.setVisibility(GONE);
        subFrame.setVisibility(VISIBLE);
        thumb.setVisibility(VISIBLE);
        playbutton.setVisibility(VISIBLE);
        pausebutton.setVisibility(INVISIBLE);
        Log.d("customplayer","stop");
    }
    public boolean isPlaying()
    {
        if(mediaPlayer !=null)
            return mediaPlayer.isPlaying();
        else
            return false;
    }
    public int getCurrentPosition()
    {
        if(mediaPlayer!=null)
            return mediaPlayer.getCurrentPosition();
        else return 0;
    }
    public int getDuration()
    {
        if(mediaPlayer!=null&&mediaPlayer.getDuration()!=0)
            return mediaPlayer.getDuration();
        else return 0;
    }
    private void curTimeThread()
    {
        final Handler timeHandler = new Handler() {
            public void handleMessage(Message msg) {
                if(isPlaying()) {
                    int curSec =  getCurrentPosition()/1000;
                    seekBar.setProgress(curSec*1000);
                    curTextview.setText(String.format("%02d",curSec/60)+":"+String.format("%02d",curSec%60));
                }
            }
        };
        curTimeThread = new Thread(new Runnable() {
            public void run() {
                try {
                    while (isPlaying()) {
                        timeHandler.sendMessage(timeHandler.obtainMessage());
                        Thread.sleep(400);
                    }
                } catch (Throwable t) {
                }
            }
        });
        curTimeThread.start();
    }
    private float distance(float x1, float y1, float x2, float y2) {
        float dx = x1 - x2;
        float dy = y1 - y2;
        float distanceInPx = (float) Math.sqrt(dx * dx + dy * dy);
        return util.dpFromPx(context,distanceInPx);
    }
}

