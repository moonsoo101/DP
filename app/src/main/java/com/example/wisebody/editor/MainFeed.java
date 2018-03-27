package com.example.wisebody.editor;


import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.DisplayMetrics;
import android.util.Log;

import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.Button;

import android.widget.FrameLayout;
import android.widget.LinearLayout;


import com.example.wisebody.editor.Adapter.mainfeedAdapter;
import com.example.wisebody.editor.Adapter.mainfeedCoverAdapter;
import com.example.wisebody.editor.ListType.coverData;
import com.example.wisebody.editor.Player.VideoPlayer;
import com.example.wisebody.editor.Util.CustomLinearLayoutManager;
import com.example.wisebody.editor.Util.Util;

import java.util.ArrayList;



/**
 * Created by wisebody on 2017. 2. 1..
 */

public class MainFeed extends AppCompatActivity{
    private static final String TAG = "MainFeed" ;
    Util util = new Util();
    Boolean selector_can_open = false;
    Boolean[] selectorOpen = {false,false};
    Boolean[] selectorClosing = {false,false};
    AppBarLayout appBarLayout;
    android.support.v7.widget.Toolbar toolbar;

    RecyclerView nationalRecy;
    RecyclerView langRecy;
    RecyclerView genreRecy;
    RecyclerView mainFeedCover;
    View feedOptSelector;
    View lastLine;

    FrameLayout appbarFrame;
    NestedScrollView nestedScrollView;
    LinearLayoutManager nationalRecyManager;
    LinearLayoutManager langRecyManager;
    LinearLayoutManager genreRecyManager;
    CustomLinearLayoutManager mainfeedCoverManager;

    public VideoPlayer playingVideo;
    public ArrayList<VideoPlayer> playingVideos = new ArrayList<>();
    mainfeedAdapter nationalAdp;
    mainfeedAdapter langAdp;
    mainfeedAdapter genreAdp;
    mainfeedCoverAdapter mainfeedCoverAdapter;

    ArrayList<String> nationList = new ArrayList<>();
    ArrayList<String> langList = new ArrayList<>();
    ArrayList<String> genreList = new ArrayList<>();
    public ArrayList<String> nationSelected = new ArrayList<>();
    public ArrayList<String> langSelected = new ArrayList<>();
    public ArrayList<String> genreSelected = new ArrayList<>();
    ArrayList<coverData> coverDatas = new ArrayList<>();
    ArrayList<ArrayList> title_array= new ArrayList<>();
    ArrayList<ArrayList> cp_array= new ArrayList<>();
    ArrayList<String> titles = new ArrayList<>();
    ArrayList<String> cps = new ArrayList<>();
    ArrayList<String> titles1 = new ArrayList<>();
    ArrayList<String> cps1 = new ArrayList<>();
//touch
    float touchStart;
    int total_scroll;

    DisplayMetrics metrics = new DisplayMetrics();
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_feed);


        feedOptSelector = findViewById(R.id.feedOptSelector);
        nationalRecy = (RecyclerView) feedOptSelector.findViewById(R.id.nationalRecy);
        langRecy = (RecyclerView) feedOptSelector.findViewById(R.id.langRecy);
        genreRecy = (RecyclerView) feedOptSelector.findViewById(R.id.genrelRecy);
        nestedScrollView = (NestedScrollView) findViewById(R.id.nestedScroll);
        mainFeedCover = (RecyclerView) findViewById(R.id.mainfeed_Recy);
        appBarLayout = (AppBarLayout) findViewById(R.id.col);
        lastLine = findViewById(R.id.lastLine);
        //lastLine.setVisibility(View.GONE);
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        nationList.add("헝가리");
        nationList.add("오스트리아");
        nationList.add("체코");
        langList.add("Slovenčina");
        langList.add("한국어");
        langList.add("简体中文");
        genreList.add("서스펜스");
        genreList.add("애니메이션");
        genreList.add("뉴스");
        genreList.add("액션");
        titles.add("bates");
        titles.add("clip2");
        titles.add("clip3");
        titles.add("clip4");
        titles.add("clip5");
        titles.add("clip6");
        titles.add("clip7");
        title_array.add(titles);
        cps.add("http://203.233.111.62/test/media/CP/욱이TV.png");
        cps.add("http://203.233.111.62/test/media/CP/욱이TV.png");
        cps.add("http://203.233.111.62/test/media/CP/욱이TV.png");
        cps.add("http://203.233.111.62/test/media/CP/욱이TV.png");
        cps.add("http://203.233.111.62/test/media/CP/욱이TV.png");
        cps.add("http://203.233.111.62/test/media/CP/욱이TV.png");
        cps.add("http://203.233.111.62/test/media/CP/Adle.png");
        cp_array.add(cps);
        titles1.add("clip8");
        titles1.add("clip9");
        titles1.add("clip10");
        titles1.add("bates");
        titles1.add("clip12");
        titles1.add("clip13");
        titles1.add("clip14");
        title_array.add(titles1);
        cps1.add("http://203.233.111.62/test/media/CP/MUTV.png");
        cps1.add("http://203.233.111.62/test/media/CP/KBS.png");
        cps1.add("http://203.233.111.62/test/media/CP/MUTV.png");
        cps1.add("http://203.233.111.62/test/media/CP/Tom.png");
        cps1.add("http://203.233.111.62/test/media/CP/Kim.png");
        cps1.add("http://203.233.111.62/test/media/CP/JTBC.png");
        cps1.add("http://203.233.111.62/test/media/CP/욱이TV.png");
        cp_array.add(cps1);
        titles.add("clip1");
        titles.add("clip2");
        titles.add("clip3");
        titles.add("clip4");
        titles.add("clip5");
        titles.add("clip6");
        titles.add("clip7");
        title_array.add(titles);
        cps.add("http://203.233.111.62/test/media/CP/욱이TV.png");
        cps.add("http://203.233.111.62/test/media/CP/욱이TV.png");
        cps.add("http://203.233.111.62/test/media/CP/욱이TV.png");
        cps.add("http://203.233.111.62/test/media/CP/욱이TV.png");
        cps.add("http://203.233.111.62/test/media/CP/욱이TV.png");
        cps.add("http://203.233.111.62/test/media/CP/욱이TV.png");
        cps.add("http://203.233.111.62/test/media/CP/Adle.png");
        cp_array.add(cps);
        titles1.add("clip8");
        titles1.add("clip9");
        titles1.add("clip10");
        titles1.add("bates");
        titles1.add("clip12");
        titles1.add("clip13");
        titles1.add("clip14");
        title_array.add(titles1);
        cps1.add("http://203.233.111.62/test/media/CP/MUTV.png");
        cps1.add("http://203.233.111.62/test/media/CP/KBS.png");
        cps1.add("http://203.233.111.62/test/media/CP/MUTV.png");
        cps1.add("http://203.233.111.62/test/media/CP/Tom.png");
        cps1.add("http://203.233.111.62/test/media/CP/Kim.png");
        cps1.add("http://203.233.111.62/test/media/CP/JTBC.png");
        cps1.add("http://203.233.111.62/test/media/CP/욱이TV.png");
        cp_array.add(cps1);
        titles.add("clip1");
        titles.add("clip2");
        titles.add("clip3");
        titles.add("clip4");
        titles.add("clip5");
        titles.add("clip6");
        titles.add("clip7");
        title_array.add(titles);
        cps.add("http://203.233.111.62/test/media/CP/욱이TV.png");
        cps.add("http://203.233.111.62/test/media/CP/욱이TV.png");
        cps.add("http://203.233.111.62/test/media/CP/욱이TV.png");
        cps.add("http://203.233.111.62/test/media/CP/욱이TV.png");
        cps.add("http://203.233.111.62/test/media/CP/욱이TV.png");
        cps.add("http://203.233.111.62/test/media/CP/욱이TV.png");
        cps.add("http://203.233.111.62/test/media/CP/Adle.png");
        cp_array.add(cps);
        titles1.add("clip8");
        titles1.add("clip9");
        titles1.add("clip10");
        titles1.add("bates");
        titles1.add("clip12");
        titles1.add("clip13");
        titles1.add("clip14");
        title_array.add(titles1);
        cps1.add("http://203.233.111.62/test/media/CP/MUTV.png");
        cps1.add("http://203.233.111.62/test/media/CP/KBS.png");
        cps1.add("http://203.233.111.62/test/media/CP/MUTV.png");
        cps1.add("http://203.233.111.62/test/media/CP/Tom.png");
        cps1.add("http://203.233.111.62/test/media/CP/Kim.png");
        cps1.add("http://203.233.111.62/test/media/CP/JTBC.png");
        cps1.add("http://203.233.111.62/test/media/CP/욱이TV.png");
        cp_array.add(cps1);

        coverDatas.add(new coverData(title_array.get(0),cp_array.get(0),"욱이TV",815,301));
        coverDatas.add(new coverData(title_array.get(1),cp_array.get(1),"Tom",515,201));
        coverDatas.add(new coverData(title_array.get(0),cp_array.get(0),"욱이TV",815,301));
        coverDatas.add(new coverData(title_array.get(1),cp_array.get(1),"Tom",515,201));
        coverDatas.add(new coverData(title_array.get(0),cp_array.get(0),"욱이TV",815,301));
        coverDatas.add(new coverData(title_array.get(1),cp_array.get(1),"Tom",515,201));

        nationalRecyManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
        langRecyManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
        genreRecyManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
        mainfeedCoverManager = new CustomLinearLayoutManager(getApplicationContext());

        nationalAdp = new mainfeedAdapter(nationList,R.layout.main_feed_select_item,nationSelected);
        langAdp = new mainfeedAdapter(langList,R.layout.main_feed_select_item,langSelected);
        genreAdp = new mainfeedAdapter(genreList,R.layout.main_feed_select_item,genreSelected);
        mainfeedCoverAdapter = new mainfeedCoverAdapter(R.layout.mainfeedrecy_item, coverDatas);

        nationalRecy.setLayoutManager(nationalRecyManager);
        langRecy.setLayoutManager(langRecyManager);
        genreRecy.setLayoutManager(genreRecyManager);
        mainFeedCover.setLayoutManager(mainfeedCoverManager);

        nationalRecy.setAdapter(nationalAdp);
        langRecy.setAdapter(langAdp);
        genreRecy.setAdapter(genreAdp);
        mainFeedCover.setAdapter(mainfeedCoverAdapter);
        nestedScrollView.post(new Runnable() {
        public void run() {
            int startMargin = (int) util.pxFromDp(getApplicationContext(), 380);
            nestedScrollView.scrollTo(0,startMargin);
        }
    });

        nestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                total_scroll = scrollY;
                Rect scrollPosition = new Rect();
                final int standardLine = (int) util.pxFromDp(getApplicationContext(), 203);
                for (int i = 0; i < mainfeedCoverAdapter.getItemCount(); i++) {
                    com.example.wisebody.editor.Adapter.mainfeedCoverAdapter.ViewHolder viewHolder =
                    (com.example.wisebody.editor.Adapter.mainfeedCoverAdapter.ViewHolder) mainFeedCover.findViewHolderForAdapterPosition(i);
                    for (int j = 0; j < viewHolder.playBtn.length; j++) {
                        viewHolder.playBtn[j].getGlobalVisibleRect(scrollPosition);
                        if (scrollPosition.top <= standardLine && scrollPosition.bottom >= standardLine) {
                            if (playingVideo == null) {
                                playingVideo = viewHolder.videoPlayer[j];
                                viewHolder.videoPlayer[j].play = true;
                                viewHolder.videoPlayer[j].loadVideo("http://000.000.000.000/media/clip2/" + coverDatas.get(i).title.get(j) + ".mp4");
                                viewHolder.playBtn[j].setVisibility(View.INVISIBLE);
                                viewHolder.videoPlayer[j].setVisibility(View.VISIBLE);
                            } else if (!playingVideo.url.equals(viewHolder.videoPlayer[j].url)) {
                                playingVideo.pause();
                                playingVideo = viewHolder.videoPlayer[j];
                                if (playingVideo.isMpPrepared && !playingVideo.play) {
                                    playingVideo.play = true; playingVideo.play();
                                } else if (!playingVideo.isMpPrepared && !playingVideo.play) {
                                    viewHolder.videoPlayer[j].play = true;
                                    viewHolder.videoPlayer[j].loadVideo("http://000.000.000.000/media/clip2/" + coverDatas.get(i).title.get(j) + ".mp4");
                                    viewHolder.playBtn[j].setVisibility(View.INVISIBLE);
                                    viewHolder.videoPlayer[j].setVisibility(View.VISIBLE);
                                }
                            }
                        }
                        if (scrollPosition.top < 0)
                            viewHolder.videoPlayer[j].pause();
                        if (scrollPosition.top > metrics.heightPixels)
                            viewHolder.videoPlayer[j].pause();
                    }
                }
            }
        });
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.parseColor("#00bcd4"));
        toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.mainFeedToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setBackgroundResource(R.drawable.searchview_bottomborder);
        toolbar.setNavigationIcon(R.drawable.ic_drawer);
    }
    @Override
    protected void onPause()
    {
        int size = playingVideos.size();
        VideoPlayer tempPlayer;
        for (int i =0;i<size;i++){
            if(playingVideos.get(i)!=null) {
                tempPlayer = playingVideos.get(i);
                    tempPlayer.pause();
                tempPlayer.progressBar.setVisibility(View.INVISIBLE);
                tempPlayer.thumb.setVisibility(View.VISIBLE);
            }
        }
        Log.d(TAG,"onpause :" + playingVideos.size());
        playingVideos.clear();
        super.onPause();
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
    public void goMainPage(int index1, int index2)
    {
        Intent intent =new Intent(this,MainPage.class);
        intent.putExtra("title", coverDatas.get(index1).title.get(index2));
        intent.putExtra("name",coverDatas.get(index1).cp.get(index2));
        Log.d("intent",Integer.toString(index1)+Integer.toString(index2));
        startActivity(intent);
    }

}
