package com.example.wisebody.editor.Player;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.SurfaceTexture;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.wisebody.editor.ListType.seqData;
import com.example.wisebody.editor.MainFeed;
import com.example.wisebody.editor.R;

import java.io.IOException;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

import static java.lang.Float.parseFloat;

/**
 * Created by wisebody on 2017. 2. 9..
 */

public class VideoPlayer extends TextureView implements TextureView.SurfaceTextureListener {

    private static String TAG = "VideoPlayer";


    public boolean isMpPrepared;
    boolean detached;
    public boolean play;
    Boolean scale;
    public boolean pause= false;
    int pauseTime,playTime;


    public String url;
    MediaPlayer mediaPlayer;
    Surface surface;
    Button playbutton;
    TextView title;
    CircleImageView cpLogo;
    public ProgressBar progressBar;
    public ImageView thumb;
    public  int index1=-1;
    int index2;
    Context context;
    ArrayList<VideoPlayer> playingVideos;



    public VideoPlayer(final Context context, final Button playbutton, final ImageView thumb, final TextView title, final CircleImageView cpLogo, ProgressBar progressBar, boolean scale, final int index2) {
        super(context);
        this.playbutton = playbutton;
        this.thumb = thumb;
        this.title = title;
        this.cpLogo = cpLogo;
        this.progressBar = progressBar;
        this.scale = scale;
        this.index2 = index2;
        this.context = context;
        playingVideos = ((MainFeed)context).playingVideos;
        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaPlayer != null) {
                    if (isPlaying()) {
                        pause();
                       goMainPage();
                        Log.d(TAG,"playing->Go Mainpage");
                    }
                }
            }
        });
        setSurfaceTextureListener(this);
    }
    public VideoPlayer(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public void loadVideo(String localPath) {

        this.url = localPath;
        progressBar.setVisibility(VISIBLE);
        if (this.isAvailable()) {
            Log.d(TAG,"avail");
            if(!detached&&play) {
                Log.d(TAG, "avail - !detach ! play");
                prepareVideo(getSurfaceTexture());
            }
        }
        else {
            if(detached&&play) {
                Log.d(TAG, "!avail detach");
                this.onAttachedToWindow();
                loadVideo(url);
            }
        }
    }

    @Override
    public void onSurfaceTextureAvailable(final SurfaceTexture surface, int width, int height) {
        isMpPrepared = false;
        //progressBar.setVisibility(VISIBLE);
        thumb.setVisibility(VISIBLE);
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
//            if(playingVideos.contains(this)) {
//                playingVideos.remove(this);
//                Log.d(TAG,"remove video");
//            }
            if(isPlaying())
                mediaPlayer.stop();
            mediaPlayer.reset();
            mediaPlayer.release();
            mediaPlayer = null;
            this.setVisibility(GONE);
            playbutton.setVisibility(VISIBLE);
            thumb.setVisibility(VISIBLE);
            //cpLogo.setVisibility(VISIBLE);
            //title.setVisibility(VISIBLE);
            isMpPrepared = false;
            detached = true;
            play = false;
            pause = false;
            Log.d(TAG,"destroy");
        }
        return false;
    }
    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {
    }

    public void prepareVideo(SurfaceTexture t)
    {

        this.surface = new Surface(t);
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            public void onPrepared(final MediaPlayer mp) {
                if(!detached) {
                    if(!playingVideos.contains(VideoPlayer.this)) {
                        playingVideos.add(VideoPlayer.this);
                        Log.d(TAG,"playingvideos add");
                    }
                    isMpPrepared = true;
                    VideoPlayer.this.setVisibility(VISIBLE);
                    if(scale) {
                        float viewWidth = getWidth();
                        float viewHeight = getHeight();
                        Matrix matrix = new Matrix();
                        matrix.setScale(2f, 1f, viewWidth / 2, viewHeight / 2);
                        setTransform(matrix);
                    }
                    if (isMpPrepared&&play) {
                        if(!pause) {
                            progressBar.setVisibility(GONE);
                            play();
                        }
                        else
                        mediaPlayer.seekTo(pauseTime);
                        //title.setVisibility(GONE);
                        //cpLogo.setVisibility(GONE);
                        playbutton.setVisibility(GONE);
                    }
                    if(!play) {
                        playbutton.setVisibility(VISIBLE);
                        Log.d("Video","대기");
                    }
                    final Handler detectStartHandler = new Handler() {
                        public void handleMessage(Message msg) {
                            if(getCurrentPosition()>0)
                                thumb.setVisibility(INVISIBLE);
                        }
                    };
                    new Thread(new Runnable() {
                        public void run() {
                            try {
                                while (getCurrentPosition()<=0) {
                                    detectStartHandler.sendMessage(detectStartHandler.obtainMessage());
                                    Thread.sleep(5);
                                }
                                thumb.setVisibility(INVISIBLE);
                            } catch (Throwable t) {
                            }
                        }
                    }).start();
                    Log.d(TAG, "start");
                }
            }
        });
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
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                isMpPrepared = false;
                play = false;
                thumb.setVisibility(VISIBLE);
                //title.setVisibility(VISIBLE);
                //cpLogo.setVisibility(VISIBLE);
                playbutton.setVisibility(VISIBLE);
                VideoPlayer.this.setVisibility(GONE);
                if(mediaPlayer!=null) {
                    mediaPlayer.reset();
                    mediaPlayer.release();
                    mediaPlayer = null;
                    Log.d(TAG,"complete ->null");
                }
                Log.d(TAG,"complete");
            }
        });
        mediaPlayer.setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener() {
            @Override
            public void onSeekComplete(MediaPlayer mediaPlayer) {
                pause = false;
                progressBar.setVisibility(INVISIBLE);
                play();
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
//            if(playingVideos.contains(this)) {
//                playingVideos.remove(this);
//                Log.d(TAG,"remove video");
//            }
            if(isPlaying())
                mediaPlayer.stop();
            mediaPlayer.reset();
            mediaPlayer.release();
            mediaPlayer = null;
            detached = true;
            play = false;
            pause = false;
            Log.d(TAG, "ondetach if not null");
        }
        this.setVisibility(GONE);
        thumb.setVisibility(VISIBLE);
        playbutton.setVisibility(VISIBLE);
        //cpLogo.setVisibility(VISIBLE);
        //title.setVisibility(VISIBLE);
        Log.d(TAG, "detach");
    }

    @Override
    protected void onVisibilityChanged(View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);

    }

    public void play()
    {
        if(!playingVideos.contains(this)) {
            playingVideos.add(this);
            Log.d(TAG,"playingvideos add");
        }
        if(mediaPlayer!=null) {
            if (!isPlaying() && play && isMpPrepared) {
                if(pause) {
                    progressBar.setVisibility(VISIBLE);
                    mediaPlayer.seekTo(pauseTime);
                    Log.d("mediastate","seek");

                }
                else {
                    pause=false;
                    Log.d("Track",url);
                    Log.d("Track","length"+mediaPlayer.getTrackInfo().length);
                    if(url.equals("http://203.233.111.62/test/media/clip2/a.mp4")) {
                        Log.d("Track","select");
                        mediaPlayer.selectTrack(2);
                    }
                    mediaPlayer.start();
                    progressBar.setVisibility(GONE);
//                    final Handler bufferingHandler = new Handler() {
//                        public void handleMessage(Message msg) {
//                            int curTime = getCurrentPosition();
//                            if(playTime==curTime)
//                                progressBar.setVisibility(VISIBLE);
//                            else if(progressBar.getVisibility()==VISIBLE)
//                                progressBar.setVisibility(GONE);
//                            playTime = curTime;
//                        }
//                    };
//                    final Handler progressGone = new Handler() {
//                        public void handleMessage(Message msg) {
//                           progressBar.setVisibility(GONE);
//                        }
//                    };
//                    new Thread(new Runnable() {
//                        @Override
//                        public void run() {
//                                while (mediaPlayer!=null&&mediaPlayer.isPlaying()) {
//                                    bufferingHandler.sendMessage(bufferingHandler.obtainMessage());
//                                    try {
//                                        Thread.sleep(100);
//                                    } catch (InterruptedException e) {
//                                        e.printStackTrace();
//                                    }
//                                }
//                                progressGone.sendMessage(progressGone.obtainMessage());
//                        }
//                    }).start();
                   // cpLogo.setVisibility(INVISIBLE);
                   // title.setVisibility(INVISIBLE);
                    thumb.setVisibility(INVISIBLE);
                    playbutton.setVisibility(INVISIBLE);
                    Log.d("mediastate","start");
                }
            }
        }
        else
        {
            play = true;
            loadVideo(url);
            Log.d(TAG,"media player null");
        }

    }

    public void pause()
    {
        play = false;
        pause =true;
        progressBar.setVisibility(INVISIBLE);
        if(mediaPlayer!=null) {
            if(isPlaying()) {
                pauseTime = mediaPlayer.getCurrentPosition();
                Log.d("videoplayer","pause"+pauseTime);
                pause = true;
                isMpPrepared = false;
                if(mediaPlayer!=null) {
                    if(isPlaying())
                        mediaPlayer.stop();
                    mediaPlayer.reset();
                    mediaPlayer.release();
                    mediaPlayer = null;
                    Log.d("customplayer","stop!");
                }
            }
            else
                stop();


                //title.setVisibility(VISIBLE);
                //cpLogo.setVisibility(VISIBLE);
            progressBar.setVisibility(INVISIBLE);
            playbutton.setVisibility(VISIBLE);
        }
        Log.d("mediastate","pause :"+pause);
        progressBar.setVisibility(INVISIBLE);
    }

    public void stop()
    {
//        if(playingVideos.contains(this))
//        {
//            playingVideos.remove(this);
//            Log.d(TAG,"playingvideos remove");
//        }
        if(mediaPlayer!=null) {
            if(isPlaying())
            mediaPlayer.stop();
            mediaPlayer.reset();
            mediaPlayer.release();
            mediaPlayer = null;
            Log.d("customplayer","stop!");
        }
        play=false;
        pause=false;
        isMpPrepared = false;
        this.setVisibility(GONE);
        //cpLogo.setVisibility(VISIBLE);
        progressBar.setVisibility(INVISIBLE);
        thumb.setVisibility(VISIBLE);
        //title.setVisibility(VISIBLE);
        playbutton.setVisibility(VISIBLE);
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


    public void goMainPage()
    {
        if(index1!=-1)
        {

                    ((MainFeed)context).goMainPage(index1,index2);
            Log.d(TAG,"GoGo" + index1+":"+index2);
//            final Handler pasueHandler = new Handler() {
//                public void handleMessage(Message msg) {
//                    if(isPlaying())pause();
//                }
//            };
//            new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    while (getCurrentPosition()==0)
//                    {
//                        try {
//                            Thread.sleep(100);
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                    pasueHandler.sendMessage(pasueHandler.obtainMessage());
//                }
//            }).start();
        }
    }
protected void attach()
{
    this.onAttachedToWindow();
    Log.d(TAG,"attach method");
    loadVideo(url);
}

}

