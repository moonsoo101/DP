package com.example.wisebody.editor;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.wisebody.editor.Adapter.LandmainPageRecVidAdapter;
import com.example.wisebody.editor.Adapter.mainPageRecVidAdapter;
import com.example.wisebody.editor.Adapter.mainPageReplyAdapter;
import com.example.wisebody.editor.Adapter.mainSuberAdapter;
import com.example.wisebody.editor.ListType.recVidData;
import com.example.wisebody.editor.ListType.reply;
import com.example.wisebody.editor.ListType.replyData;
import com.example.wisebody.editor.ListType.rereplyData;
import com.example.wisebody.editor.ListType.suberData;
import com.example.wisebody.editor.Player.MainPagePlayer;
import com.example.wisebody.editor.Util.BitmapDownloaderTask;
import com.example.wisebody.editor.Util.Util;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by wisebody on 2017. 2. 10..
 */

public class MainPage extends AppCompatActivity implements View.OnClickListener{
    String TAG = "MainPage";
    Boolean[] selectedList = {true,false,false};
    private final int PORTRAIT = 0;
    private final int LANDSCAPE = 1;
    int Range;
    public int screenState;
    BitmapDownloaderTask bitmapDownloaderTask;
    CollapsingToolbarLayout collapsingToolbarLayout;
    Toolbar toolbar;
    AppBarLayout appBarLayout;
    SeekBar seekBar;
    CircleImageView cpLogo;
    TextView titleView,durTextview, curTextview, menutitle1, replyCount, subtitle;
    Button popularBtn, newestBtn, playBtn, pauseBtn, fullScreenBtn ,selectedPopBtn, selectedNewBtn;
    ImageButton moreReply , subListBtn,dubListBtn,replyListBtn;
    RelativeLayout videoInfoCont;
    FrameLayout videoCont;
    FrameLayout optCont, subFrame, FRecycle;
    RelativeLayout titleCont, recVidCont;
    ImageView thumb;
    ProgressBar progressBar;
   //View nestedContent;
    int appbarOffset;
    int infoMaxmargin;
    int collapoffset;
    String title;
    String name;
    Util util;
    MainPagePlayer videoPlayer;
    Boolean videoInfoContinvisible, expanded = true;
    CoordinatorLayout coordinatorLayout;
    NestedScrollView nestedScrollView;
    RecyclerView replyRecy, recVidRecy, selectedRecy;
    LinearLayout recyclerCont;
    LinearLayoutManager replyRecyManager, recVidRecyManager;
    mainPageReplyAdapter mainPageReplyAdapter;
    mainSuberAdapter mainSuberAdapter;
    mainPageRecVidAdapter mainPageRecVidAdapter;
    LandmainPageRecVidAdapter landmainPageRecVidAdapter;
    FloatingActionButton goEdit;
    int maxFling;
    int pauseTime;
    ArrayList<reply> replyDatas = new ArrayList<>();
    ArrayList<recVidData> recVidDatas = new ArrayList<>();
    ArrayList<ArrayList> rereplyDatas = new ArrayList<>();
    ArrayList<suberData> suberDatas = new ArrayList<>();
    LinearLayout selectedRecyCont;
     @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mainpage);
         if(getWindowManager().getDefaultDisplay().getRotation()== Surface.ROTATION_0) {
             screenState = PORTRAIT;
             UISetPortrait();
         }
         else {
             screenState = LANDSCAPE;
             UISetLandscape();
         }
    }
    @Override
    protected void onSaveInstanceState (Bundle outState) {
        int save = pauseTime;
        outState.putInt("pauseTime", save);
        outState.putString("title", title);
        outState.putString("name", name);
        Log.d(TAG,"save" + save);
    }

    @Override
    protected void onRestoreInstanceState (Bundle savedInstanceState) {
        pauseTime = savedInstanceState.getInt("pauseTime");
        title = savedInstanceState.getString("title");
        name = savedInstanceState.getString("name");
        Log.d(TAG,"restore" + pauseTime);
    }
    @Override
    protected  void onPause()
    {
        super.onPause();
        Log.d(TAG,"pause");
        pauseTime = videoPlayer.getCurrentPosition();
            videoPlayer.pause();
            videoPlayer.thumb.setVisibility(View.VISIBLE);
           videoCont.removeView(videoPlayer);
    }
    @Override
    protected void onResume()
    {
        super.onResume();
            videoPlayer = new MainPagePlayer(this, thumb, optCont, playBtn, pauseBtn, appBarLayout, subFrame, durTextview, curTextview, seekBar, progressBar, recVidRecy);

        videoCont.addView(videoPlayer,0);
        videoPlayer.play =true;
        Log.d(TAG,"title : "+title);
        videoPlayer.pauseTime = pauseTime;
        videoPlayer.loadVideo("http://203.233.111.62/test/media/clip2/" + title + "blur.mp4");
        Log.d(TAG,"pausetime" + pauseTime);
    }
    @Override
    public void onBackPressed()
    {
      if(screenState==PORTRAIT)
          super.onBackPressed();
        else {
          pauseTime = videoPlayer.getCurrentPosition();
          setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
      }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_feed_menu, menu);
        toolbar.setContentInsetStartWithNavigation(-10);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setMaxWidth(Integer.MAX_VALUE);

        searchView.setQueryHint("search");

        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_search) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.popularBtn:
                if(!popularBtn.isSelected()) {
                    popularBtn.setSelected(true);
                    popularBtn.setTextColor(Color.parseColor("#00bcd4"));
                    newestBtn.setSelected(false);
                    newestBtn.setTextColor(Color.parseColor("#61000000"));
                }
                break;
            case R.id.newestBtn:
                if(!newestBtn.isSelected()) {
                    newestBtn.setSelected(true);
                    newestBtn.setTextColor(Color.parseColor("#00bcd4"));
                    popularBtn.setSelected(false);
                    popularBtn.setTextColor(Color.parseColor("#61000000"));
                }
                break;
            case R.id.playbtn:
                if (!videoPlayer.play) {
                        videoPlayer.play = true;
                        if (videoPlayer.isMpPrepared)
                            videoPlayer.play();
                        else
                            videoPlayer.loadVideo("http://203.233.111.62/test/media/clip2/" + title + "blur.mp4");
                        Log.d("Mainpage", "play");
                }
                break;
            case R.id.pausebtn :
                videoPlayer.pause();
                break;
            case R.id.moreReply :
                Log.d(TAG,"more Reply");
                replyDatas.add(new replyData("정크렛","대박이다 씨지가 이렇게 많은줄 몰랐음...감쪽같넹", 3500, 1, false, 0, rereplyDatas.get(0)));
                replyDatas.add(new replyData("정크렛","바쁘신 분들 3분 50부터 보세요. 연출 장난 없음!!!*_* 진짜   감독이 저 장면을 위해 영혼을 갈아 넣은듯", 896, 1, true, 0, rereplyDatas.get(1)));
                replyCount.setText(Integer.toString(replyDatas.size()));
                mainPageReplyAdapter.visibleItems.add(mainPageReplyAdapter.visibleItems.size(),new replyData("정크렛","대박이다 씨지가 이렇게 많은줄 몰랐음...감쪽같넹", 3500, 1, false, 0, rereplyDatas.get(0)));
                mainPageReplyAdapter.visibleItems.add(mainPageReplyAdapter.visibleItems.size(),new replyData("정크렛","바쁘신 분들 3분 50부터 보세요. 연출 장난 없음!!!*_* 진짜   감독이 저 장면을 위해 영혼을 갈아 넣은듯", 896, 1, true, 0, rereplyDatas.get(1)));
                mainPageReplyAdapter.notifyItemRangeInserted(mainPageReplyAdapter.visibleItems.size(), 2);
                break;
//            case R.id.moreRecVideo :
//                int size = recVidDatas.size();
//                recVidDatas.add(new recVidData("욱이TV","clip2","Tom","English",3600000,3,392,false));
//                recVidDatas.add(new recVidData("욱이TV","clip3","Jenny","English",3456000,4,59594,false));
//                recVidDatas.add(new recVidData("욱이TV","clip4","Adle","English",3600000,5,30000,false));
//                recVidDatas.add(new recVidData("Jenny","clip13","Adle","French",385000,1,2000,false));
//                recVidDatas.add(new recVidData("욱이TV","clip14","Tom","English",631000,6,400000,false));
//                mainPageRecVidAdapter.notifyItemRangeChanged(size, 5);
//                break;
            case R.id.fullScreenBtn :
                if(screenState==PORTRAIT) {
                    pauseTime = videoPlayer.getCurrentPosition();
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                }
                else {
                    pauseTime = videoPlayer.getCurrentPosition();
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                }
                break;
            case R.id.subListBtn :
              if(!selectedList[0]) {
                  subListBtn.setBackgroundResource(R.drawable.togglebtnselect);
                  dubListBtn.setBackgroundResource(R.drawable.togglebtnunselect);
                  replyListBtn.setBackgroundResource(R.drawable.togglebtnunselect);
                  goEdit.setImageDrawable(getDrawable(R.drawable.ic_edit_copy_7));
                  suberDatas.clear();
                  suberDatas.add(new suberData("욱이TV","Korean","FM2016,witcher 게임 컨텐츠","자막이다./n자막이라",213,321,3.0,true));
                  mainSuberAdapter.notifyDataSetChanged();
                  if(selectedList[2]) {
                      selectedRecy.setAdapter(mainSuberAdapter);
                      recyclerCont.addView(selectedRecyCont, 0);
                      nestedScrollView.post(new Runnable() {
                          public void run() {
                              nestedScrollView.scrollTo(0,0);
                          }
                      });
                  }
                  selectedList[0] = true;
                  selectedList[1] = false;
                  selectedList[2] = false;
              }
                break;
            case R.id.dubListBtn :
                if(!selectedList[1])
                {
                    subListBtn.setBackgroundResource(R.drawable.togglebtnunselect);
                    dubListBtn.setBackgroundResource(R.drawable.togglebtnselect);
                    replyListBtn.setBackgroundResource(R.drawable.togglebtnunselect);
                    goEdit.setImageDrawable(getDrawable(R.drawable.ic_microphone));
                    suberDatas.clear();
                    suberDatas.add(new suberData("욱이TV","Korean(더빙)","FM2016,witcher 게임 컨텐츠","자막이다./n자막이라",213,321,3.0,true));
                    mainSuberAdapter.notifyDataSetChanged();
                    if(selectedList[2]) {
                        selectedRecy.setAdapter(mainSuberAdapter);
                        recyclerCont.addView(selectedRecyCont, 0);
                        nestedScrollView.post(new Runnable() {
                        public void run() {
                        nestedScrollView.scrollTo(0,0);
                        }
                        });
                    }
                    selectedList[0] = false;
                    selectedList[1] = true;
                    selectedList[2] = false;
                }
                break;
            case R.id.replyListBtn :
                if(!selectedList[2])
                {
                    subListBtn.setBackgroundResource(R.drawable.togglebtnunselect);
                    dubListBtn.setBackgroundResource(R.drawable.togglebtnunselect);
                    replyListBtn.setBackgroundResource(R.drawable.togglebtnselect);
                    recyclerCont.removeView(selectedRecyCont);
                    selectedList[0] = false;
                    selectedList[1] = false;
                    selectedList[2] = true;
                }
                break;
        }

    }
    public void collapseToolbar() {
        if(screenState==PORTRAIT&&expanded) {
            CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) appBarLayout.getLayoutParams();
            final AppBarLayout.Behavior behavior = (AppBarLayout.Behavior) params.getBehavior();
            if (behavior != null) {
                ValueAnimator valueAnimator = ValueAnimator.ofInt();
                valueAnimator.setInterpolator(new DecelerateInterpolator());
                valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        behavior.setTopAndBottomOffset((Integer) animation.getAnimatedValue());
                        appBarLayout.requestLayout();
                    }
                });
                valueAnimator.setIntValues(appbarOffset, -collapoffset);
                valueAnimator.setDuration(500);
                valueAnimator.start();
                expanded = false;
                Log.d("MainPage collapse", "collapse");
            }
        }
    }
    public void expandToolbar() {
        if (screenState==PORTRAIT&&!expanded) {
            CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) appBarLayout.getLayoutParams();
            final AppBarLayout.Behavior behavior = (AppBarLayout.Behavior) params.getBehavior();
            if (behavior != null) {
                ValueAnimator valueAnimator = ValueAnimator.ofInt();
                valueAnimator.setInterpolator(new DecelerateInterpolator());
                valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        behavior.setTopAndBottomOffset((Integer) animation.getAnimatedValue());
                        appBarLayout.requestLayout();
                    }
                });
                valueAnimator.setIntValues(appbarOffset, 0);
                valueAnimator.setDuration(500);
                valueAnimator.start();
                expanded = true;
            }
        }
    }
    public void goRecVideo(String title, String name)
    {
        Log.d(TAG,"goRecVideo");
        this.title = title;
        this.name = "http://203.233.111.62/test/media/CP/"+name+".png";
        videoPlayer.stop();
        titleView.setText(title);
        bitmapDownloaderTask = new BitmapDownloaderTask(thumb,progressBar,getApplicationContext(), false);
        bitmapDownloaderTask.download("http://203.233.111.62/test/media/clip2/"+title+".png",thumb);
        bitmapDownloaderTask = new BitmapDownloaderTask(cpLogo,progressBar,getApplicationContext(), false);
        bitmapDownloaderTask.download(this.name,cpLogo);
        videoPlayer.pauseTime = -1;
        videoPlayer.play = true;
        videoPlayer.loadVideo("http://203.233.111.62/test/media/clip2/" + title + "blur.mp4");
        if(mainPageReplyAdapter!=null)
        mainPageReplyAdapter.notifyDataSetChanged();
        if(mainPageRecVidAdapter!=null)
        mainPageRecVidAdapter.notifyDataSetChanged();
    }
    protected void UISetPortrait()
    {
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.root);
        util = new Util();
        if(title==null)
        title = getIntent().getStringExtra("title");
        if(name==null)
        name = getIntent().getStringExtra("name");
        //coordinatorLayout = (CoordinatorLayout) findViewById(R.id.root);
        cpLogo = (CircleImageView) findViewById(R.id.cpLogo);
        titleView = (TextView) findViewById(R.id.title);
        titleView.setText(title);
        replyCount = (TextView) findViewById(R.id.replyCount);
        menutitle1 = (TextView) findViewById(R.id.menutitle1);
        infoMaxmargin = (int)util.pxFromDp(getApplicationContext(),40);
        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        toolbar = (Toolbar) findViewById(R.id.mainPageToolbar);
        popularBtn = (Button) findViewById(R.id.popularBtn);
        popularBtn.setOnClickListener(this);
        popularBtn.setSelected(true);
        popularBtn.setTextColor(Color.parseColor("#00bcd4"));
        newestBtn = (Button) findViewById(R.id.newestBtn);
        newestBtn.setOnClickListener(this);
        fullScreenBtn = (Button) findViewById(R.id.fullScreenBtn);
        fullScreenBtn.setOnClickListener(this);
        View mainpage_video = findViewById(R.id.mainpage_video);
        seekBar = (SeekBar)(mainpage_video.findViewById(R.id.videoSeekbar));
        seekBar.setThumb(getDrawable(R.drawable.videoseekbarthumb));
        subtitle = (TextView) (mainpage_video.findViewById(R.id.subtitle));
        playBtn = (Button) (mainpage_video.findViewById(R.id.playbtn));
        playBtn.setOnClickListener(this);
        pauseBtn = (Button) (mainpage_video.findViewById(R.id.pausebtn));
        pauseBtn.setOnClickListener(this);
        subListBtn = (ImageButton) findViewById(R.id.subListBtn);
        subListBtn.setBackgroundResource(R.drawable.togglebtnselect);
        subListBtn.setOnClickListener(this);
        dubListBtn = (ImageButton) findViewById(R.id.dubListBtn);
        dubListBtn.setOnClickListener(this);
        replyListBtn = (ImageButton) findViewById(R.id.replyListBtn);
        replyListBtn.setOnClickListener(this);
        progressBar = (ProgressBar) mainpage_video.findViewById(R.id.progress);
        thumb = (ImageView) (mainpage_video.findViewById(R.id.thumb));
        bitmapDownloaderTask = new BitmapDownloaderTask(thumb,progressBar,getApplicationContext(), false);
        bitmapDownloaderTask.download("http://203.233.111.62/test/media/clip2/"+title+".png",thumb);
        bitmapDownloaderTask = new BitmapDownloaderTask(cpLogo,progressBar,getApplicationContext(), false);
        Log.d(TAG,"name" + name);
        bitmapDownloaderTask.download(name,cpLogo);
        titleCont = (RelativeLayout) findViewById(R.id.titleCont);
        recVidCont = (RelativeLayout) findViewById(R.id.recVidCont);
        //recVidCont.setVisibility(View.GONE);
        durTextview = (TextView)(mainpage_video.findViewById(R.id.videoDur));
        curTextview = (TextView) (mainpage_video.findViewById(R.id.videoCurrent));
        videoInfoCont = (RelativeLayout) findViewById(R.id.videoInfoCont);
        appBarLayout = (AppBarLayout) findViewById(R.id.app_bar);
        collapoffset = (int)util.pxFromDp(getApplicationContext(),82);
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (verticalOffset != appbarOffset) {
                    Log.d("offset", Integer.toString(verticalOffset) + "coll:"+collapoffset);
                    ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) videoInfoCont.getLayoutParams();
                    if (params.topMargin >= 0) {
                        int marginOff = verticalOffset - appbarOffset;
                        Log.d("margin",Integer.toString(marginOff));
                        if(marginOff<0)
                        {
                            if(-verticalOffset>=infoMaxmargin)
                                params.topMargin += marginOff;
                            if (params.topMargin < 0||verticalOffset == -collapoffset) {
                                params.topMargin = 0;
                                videoInfoContinvisible =true;
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        if(videoInfoContinvisible) {
                                            Animation animation = new AlphaAnimation(1, 0);
                                            animation.setDuration(500);
                                            videoInfoCont.setVisibility(View.GONE);
                                            videoInfoCont.setAnimation(animation);
                                        }
                                        videoInfoContinvisible=false;
                                    }
                                },1500);

                            }
                        }
                        else {
                            float alpha = ((float)(collapoffset+verticalOffset)/(float)collapoffset);
                            Log.d("alpha",Float.toString(alpha));
                            videoInfoContinvisible = false;
                            videoInfoCont.setVisibility(View.VISIBLE);
                            videoInfoCont.setAlpha(alpha);
                            Log.d("params",Integer.toString(params.topMargin));
                            params.topMargin += marginOff;
                            if (params.topMargin > infoMaxmargin || verticalOffset == 0) {
                                params.topMargin = infoMaxmargin;
                                videoInfoCont.setAlpha(1);
                            }
                        }
                        videoInfoCont.requestLayout();
                        //appbarOffset = verticalOffset;
                    }
                }
                appbarOffset = verticalOffset;
            }
        });

        optCont = (FrameLayout)(mainpage_video.findViewById(R.id.mainOptcontainer));
        subFrame = (FrameLayout)mainpage_video.findViewById(R.id.subFrame);
        //videoPlayer = new MainPagePlayer(this,thumb, optCont, playBtn, pauseBtn, appBarLayout, subFrame, durTextview, curTextview, seekBar, progressBar);
        videoCont = (FrameLayout) findViewById(R.id.mainvideocontainer);
        //videoCont.addView(videoPlayer,0);
        optCont = (FrameLayout)mainpage_video.findViewById(R.id.mainOptcontainer);
        optCont.setVisibility(View.GONE);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setBackgroundResource(R.drawable.searchview_bottomborder);
        toolbar.setNavigationIcon(R.drawable.ic_drawer);
        nestedScrollView = (NestedScrollView) findViewById(R.id.nestedScroll);
        recyclerCont  = (LinearLayout) findViewById(R.id.recycleCont);
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        selectedRecyCont= (LinearLayout) inflater.inflate(R.layout.recyclerview, null);
        selectedRecy = (RecyclerView)selectedRecyCont.findViewById(R.id.recyclerView);
        selectedPopBtn = (Button)selectedRecyCont.findViewById(R.id.popularBtn);
        selectedNewBtn = (Button)selectedRecyCont.findViewById(R.id.newestBtn);
        LinearLayoutManager selectedRecyManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        selectedRecy.setLayoutManager(selectedRecyManager);
        recyclerCont.addView(selectedRecyCont,0);
        moreReply = (ImageButton) findViewById(R.id.moreReply);
        moreReply.setOnClickListener(this);
        replyRecy = (RecyclerView) findViewById(R.id.replyRecy);
        replyRecyManager = new LinearLayoutManager(getApplicationContext(),LinearLayoutManager.VERTICAL,false);
        replyRecy.setLayoutManager(replyRecyManager);
        ArrayList<rereplyData> rereplyData = new ArrayList();
        rereplyData.add(new rereplyData("정크렛","대박이다 씨지가 이렇게 많은줄 몰랐음...감쪽같넹", 3500, 1, false, 1));
        rereplyData.add(new rereplyData("정크렛","대박이다 씨지가 이렇게 많은줄 몰랐음...감쪽같넹", 3500, 1, false, 1));
        rereplyDatas.add(rereplyData);
        rereplyData = new ArrayList();
        rereplyData.add(new rereplyData("정크렛2","대박이다 씨지가 이렇게 많은줄 몰랐음...감쪽같넹", 3500, 1, false, 1));
        rereplyData.add(new rereplyData("정크렛2","대박이다 씨지가 이렇게 많은줄 몰랐음...감쪽같넹", 3500, 1, false, 1));
        rereplyDatas.add(rereplyData);
        replyDatas.add(new replyData("정크렛","대박이다 씨지가 이렇게 많은줄 몰랐음...감쪽같넹", 3500, 1, false, 0, rereplyDatas.get(0)));
        replyDatas.add(new replyData("정크렛","바쁘신 분들 3분 50부터 보세요. 연출 장난 없음!!!*_* 진짜   감독이 저 장면을 위해 영혼을 갈아 넣은듯", 896, 1, true, 0, rereplyDatas.get(1)));
        replyDatas.add(new replyData("장래희망 건물주","장래희망 건물주", 112, 1, false, 0, null));
        replyCount.setText(Integer.toString(replyDatas.size()));
        suberDatas.add(new suberData("욱이TV","Korean","FM2016,witcher 게임 컨텐츠","자막이다./n자막이라",213,321,3.0,true));
        mainPageReplyAdapter = new mainPageReplyAdapter(R.layout.mainpage_reply, replyDatas);
        mainSuberAdapter = new mainSuberAdapter(R.layout.mainpage_suber, suberDatas);
        selectedRecy.setAdapter(mainSuberAdapter);
        replyRecy.setAdapter(mainPageReplyAdapter);
        replyRecy.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View view, int i, int i1, int i2, int i3, int i4, int i5, int i6, int i7) {
                Log.d(TAG,"replyRecy heigth"+replyRecy.getHeight());
            }
        });
//        nestedScrollView.post(new Runnable() {
//            public void run() {
//                nestedScrollView.scrollTo(0,0);
//            }
//        });
        nestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                Log.d("MainPage" , "nestedScrollView scroll : "+scrollY);
            }
        });
        //recVidRecy = (RecyclerView) nestedContent.findViewById(R.id.recommendVideo);
        recVidRecy = (RecyclerView) findViewById(R.id.recommendVideo);
        recVidRecyManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        recVidRecy.setLayoutManager(recVidRecyManager);
//        nestedScrollView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//            @Override
//            public void onGlobalLayout() {
//                int height = coordinatorLayout.getHeight();
//                Log.w("Foo", String.format("layout height: %d", height));
//                Rect r = new Rect();
//                coordinatorLayout.getWindowVisibleDisplayFrame(r);
//                int visible = r.bottom - r.top;
//                Log.w("Foo", String.format("visible height: %d", visible));
//                Log.w("Foo", String.format("keyboard height: %d", height - visible));
//            }
//        });


        recVidDatas.add(new recVidData("욱이TV","clip1","Adle","English",3250000,1,5000,false));
        recVidDatas.add(new recVidData("MUTV","clip8","Kim","Korean",136000,1,323,true));
        recVidDatas.add(new recVidData("KBS","clip9","Tom","Korean",89000,1,154,true));
        recVidDatas.add(new recVidData("MUTV","clip10","Kim","Korean",103000,2,454,true));
        recVidDatas.add(new recVidData("Tom","clip11","Kim","KOREAN",692000,1,3000,true));
//         recVidDatas.add(new recVidData("욱이TV","clip2","Tom","English",3600000,3,392,false));
//         recVidDatas.add(new recVidData("욱이TV","clip3","Jenny","English",3456000,4,59594,false));
//         recVidDatas.add(new recVidData("욱이TV","clip4","Adle","English",3600000,5,30000,false));
//         recVidDatas.add(new recVidData("Jenny","clip13","Adle","French",385000,1,2000,false));
//         recVidDatas.add(new recVidData("욱이TV","clip14","Tom","English",631000,6,400000,false));
        mainPageRecVidAdapter = new mainPageRecVidAdapter(R.layout.mainrecvideoitem, recVidDatas);
        recVidRecy.setAdapter(mainPageRecVidAdapter);
        recVidRecy.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View view, int i, int i1, int i2, int i3, int i4, int i5, int i6, int i7) {
                Log.d("recVid","change"+recVidRecy.getHeight());
                recVidRecy.setNestedScrollingEnabled(false);
            }
        });
        Log.d("setadapter","finish");

        seekBar.setOnTouchListener(new SeekBar.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                int action = event.getAction();
                switch (action)
                {
                    case MotionEvent.ACTION_DOWN:
                        // Disallow ScrollView to intercept touch events.
                        Log.d("seekbar","Touch");
                        nestedScrollView.requestDisallowInterceptTouchEvent(true);
                        seekBar.setThumb(getDrawable(R.drawable.videoseekbarthumb_pressed));
                        break;

                    case MotionEvent.ACTION_UP:
                        // Allow ScrollView to intercept touch events.
                        nestedScrollView.requestDisallowInterceptTouchEvent(false);
                        seekBar.setThumb(getDrawable(R.drawable.videoseekbarthumb));
                        break;
                }
                // Handle Seekbar touch events.
                v.onTouchEvent(event);
                return true;
            }
        });
        nestedScrollView.setNestedScrollingEnabled(false);
        maxFling = ViewConfiguration.get(getApplicationContext()).getScaledMaximumFlingVelocity();
        replyRecy.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                 if(Range<=0)
                     Range=replyRecy.computeVerticalScrollRange();
                Log.d(TAG,"Range : "+Range);
            }
        });
        goEdit = (FloatingActionButton) findViewById(R.id.goEdit);
        goEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =new Intent(MainPage.this,subEditor.class);
                intent.putExtra("title", title);
                startActivity(intent);
            }
        });

//        replyRecy.setOnFlingListener(new RecyclerView.OnFlingListener() {
//            @Override
//            public boolean onFling(int velocityX, int velocityY) {
//                //위로
//                if(velocityY<0)
//                {
//                    if(Math.abs(velocityY)>=maxFling/2)
//                        nestedScrollView.scrollTo(0,0);
//
//                }
//                //아래로
//                else
//                {
//                  if(velocityY>=maxFling/2)
//                      nestedScrollView.scrollTo(0,replyRecy.getHeight());
//                }
//                Log.d("MainPage" , "replyRecy Fling : "+velocityY + " / " + ViewConfiguration.get(getApplicationContext()).getScaledMaximumFlingVelocity());
//                return false;
//            }
//        });
//         recVidRecy.setOnFlingListener(new RecyclerView.OnFlingListener() {
//             @Override
//             public boolean onFling(int velocityX, int velocityY) {
//                 //위로
//                 if(velocityY<0)
//                 {
//                     if(Math.abs(velocityY)>=maxFling/2)
//                         nestedScrollView.scrollTo(0,replyRecy.getHeight());
//
//                 }
//                 //아래로
//                 else
//                 {
//                     if(velocityY>=maxFling/2)
//                         nestedScrollView.scrollTo(0,replyRecy.getHeight()+recVidRecy.getHeight());
//                 }
//                 Log.d("MainPage" , "replyRecy Fling : "+velocityY + " / " + ViewConfiguration.get(getApplicationContext()).getScaledMaximumFlingVelocity());
//                 return false;
//             }
//         });

    }
    protected void UISetLandscape()
    {
        util = new Util();
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION|View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        decorView.setSystemUiVisibility(uiOptions);
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        if(title==null)
        title = getIntent().getStringExtra("title");
        if(name==null)
        name = getIntent().getStringExtra("name");
        //coordinatorLayout = (CoordinatorLayout) findViewById(R.id.root);
        cpLogo = (CircleImageView) findViewById(R.id.cpLogo);
        titleView = (TextView) findViewById(R.id.title);
        titleView.setText(title);
        fullScreenBtn = (Button) findViewById(R.id.fullScreenBtn);
        fullScreenBtn.setOnClickListener(this);
        seekBar = (SeekBar)findViewById(R.id.videoSeekbar);
        seekBar.setThumb(getDrawable(R.drawable.videoseekbarthumb));

        playBtn = (Button) findViewById(R.id.playbtn);
        playBtn.setOnClickListener(this);
        pauseBtn = (Button) findViewById(R.id.pausebtn);
        pauseBtn.setOnClickListener(this);
        progressBar = (ProgressBar) findViewById(R.id.progress);
        thumb = (ImageView) findViewById(R.id.thumb);
        bitmapDownloaderTask = new BitmapDownloaderTask(thumb,progressBar,getApplicationContext(), false);
        bitmapDownloaderTask.download("http://203.233.111.62/test/media/clip2/"+title+".png",thumb);
        bitmapDownloaderTask = new BitmapDownloaderTask(cpLogo,progressBar,getApplicationContext(), false);
        Log.d(TAG,"name" + name);
        bitmapDownloaderTask.download(name,cpLogo);
        durTextview = (TextView)findViewById(R.id.videoDur);
        curTextview = (TextView) findViewById(R.id.videoCurrent);
        videoInfoCont = (RelativeLayout) findViewById(R.id.videoInfoCont);

        optCont = (FrameLayout) findViewById(R.id.mainOptcontainer);
        subFrame = (FrameLayout) findViewById(R.id.subFrame);
        //videoPlayer = new MainPagePlayer(this,thumb, optCont, playBtn, pauseBtn, appBarLayout, subFrame, durTextview, curTextview, seekBar, progressBar);
        videoCont = (FrameLayout) findViewById(R.id.mainvideocontainer);
        //videoCont.addView(videoPlayer,0);
        optCont = (FrameLayout) findViewById(R.id.mainOptcontainer);
        optCont.setVisibility(View.GONE);
        recVidRecy = (RecyclerView) findViewById(R.id.recommendVideo);
        recVidRecyManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
        recVidRecy.setLayoutManager(recVidRecyManager);

        ArrayList<rereplyData> rereplyData = new ArrayList();
        rereplyData.add(new rereplyData("정크렛","대박이다 씨지가 이렇게 많은줄 몰랐음...감쪽같넹", 3500, 1, false, 1));
        rereplyData.add(new rereplyData("정크렛","대박이다 씨지가 이렇게 많은줄 몰랐음...감쪽같넹", 3500, 1, false, 1));
        rereplyDatas.add(rereplyData);
        rereplyData = new ArrayList();
        rereplyData.add(new rereplyData("정크렛2","대박이다 씨지가 이렇게 많은줄 몰랐음...감쪽같넹", 3500, 1, false, 1));
        rereplyData.add(new rereplyData("정크렛2","대박이다 씨지가 이렇게 많은줄 몰랐음...감쪽같넹", 3500, 1, false, 1));
        rereplyDatas.add(rereplyData);
        replyDatas.add(new replyData("정크렛","대박이다 씨지가 이렇게 많은줄 몰랐음...감쪽같넹", 3500, 1, false, 0, rereplyDatas.get(0)));
        replyDatas.add(new replyData("정크렛","바쁘신 분들 3분 50부터 보세요. 연출 장난 없음!!!*_* 진짜   감독이 저 장면을 위해 영혼을 갈아 넣은듯", 896, 1, true, 0, rereplyDatas.get(1)));
        replyDatas.add(new replyData("장래희망 건물주","장래희망 건물주", 112, 1, false, 0, null));
        replyDatas.add(new replyData("정크렛","대박이다 씨지가 이렇게 많은줄 몰랐음...감쪽같넹", 3500, 1, false, 0, null));
        replyDatas.add(new replyData("정크렛","대박이다 씨지가 이렇게 많은줄 몰랐음...감쪽같넹", 3500, 1, false, 0 , null));
        replyDatas.add(new replyData("정크렛","바쁘신 분들 3분 50부터 보세요. 연출 장난 없음!!!*_* 진짜   감독이 저 장면을 위해 영혼을 갈아 넣은듯", 896, 1, true, 0, null));
        replyDatas.add(new replyData("장래희망 건물주","장래희망 건물주", 112, 1, false, 0, null));
        replyDatas.add(new replyData("정크렛","대박이다 씨지가 이렇게 많은줄 몰랐음...감쪽같넹", 3500, 1, false, 0, null));



        recVidDatas.add(new recVidData("욱이TV","clip1","Adle","English",3250000,1,5000,false));
        recVidDatas.add(new recVidData("MUTV","clip8","Kim","Korean",136000,1,323,true));
        recVidDatas.add(new recVidData("KBS","clip9","Tom","Korean",89000,1,154,true));
        recVidDatas.add(new recVidData("MUTV","clip10","Kim","Korean",103000,2,454,true));
        recVidDatas.add(new recVidData("Tom","clip11","Kim","KOREAN",692000,1,3000,true));
        landmainPageRecVidAdapter = new LandmainPageRecVidAdapter(R.layout.mainlandrecvideoitem, recVidDatas);
        recVidRecy.setAdapter(landmainPageRecVidAdapter);

        Log.d("setadapter","finish");
        seekBar.setOnTouchListener(new SeekBar.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                int action = event.getAction();
                switch (action)
                {
                    case MotionEvent.ACTION_DOWN:
                        // Disallow ScrollView to intercept touch events.
                        Log.d("seekbar","Touch");
                        seekBar.setThumb(getDrawable(R.drawable.videoseekbarthumb_pressed));
                        break;
                    case MotionEvent.ACTION_MOVE:
                        seekBar.setThumb(getDrawable(R.drawable.videoseekbarthumb_pressed));
                        break;
                    case MotionEvent.ACTION_UP:
                        // Allow ScrollView to intercept touch events.
                        seekBar.setThumb(getDrawable(R.drawable.videoseekbarthumb));
                        break;
                }

                // Handle Seekbar touch events.
                v.onTouchEvent(event);
                return true;
            }
        });
        seekBar.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(b)
                    seekBar.setThumb(getDrawable(R.drawable.videoseekbarthumb_pressed));
                else
                    seekBar.setThumb(getDrawable(R.drawable.videoseekbarthumb));

            }
        });
        recVidRecy.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }
        });

    }
    public void hideSystemBar()
    {
//        View decorView = getWindow().getDecorView();
//        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
//        decorView.setSystemUiVisibility(uiOptions);
    }
    public void showSystemBar()
    {
//        View decorView = getWindow().getDecorView();
//        int uiOptions = View.SYSTEM_UI_FLAG_VISIBLE;
//        decorView.setSystemUiVisibility(uiOptions);

    }
    private class ViewHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            recVidCont.setVisibility(View.VISIBLE);
        }
    }
}
