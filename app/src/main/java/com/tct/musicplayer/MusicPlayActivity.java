package com.tct.musicplayer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.tct.musicplayer.entity.Song;
import com.tct.musicplayer.fragment.LyricsFragment;
import com.tct.musicplayer.fragment.MusicPlayFragment;
import com.tct.musicplayer.fragment.PlayListFragment;
import com.tct.musicplayer.receiver.BaseReceiver;
import com.tct.musicplayer.service.MusicService;
import com.tct.musicplayer.utils.MusicUtils;
import com.tct.musicplayer.utils.NotificationUtils;
import com.tct.musicplayer.utils.ToastUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MusicPlayActivity extends AppCompatActivity implements View.OnClickListener {

    private View circleView1,circleView2,circleView3;
    private ViewPager viewPager;
    private List<Fragment> fragment_list;
    private PlayListFragment playListFragment;
    private MusicPlayFragment musicPlayFragment;
    private LyricsFragment lyricsFragment;

    private ImageView backImg,moreImg;
    private TextView musicName,musicSinger;
    private TextView currTime,totalTime;
    private ImageView playMusic,pauseMusic,lastMusic,nextMusic;
    private ImageView addFavorite,removeFavorite;
    private SeekBar seekBar;
    private ImageView playInOrder,playSingleCycle,playRandom;

    private MusicService musicService = MainActivity.musicService;

    private MusicStateReceiver musicStateReceiver;

    private boolean isClosed = false;
    private boolean isFirst = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_play);


        //通知栏透明
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }

        initViews();

        initViewPager();

        if (musicService != null) {
            if (!musicService.isPlaying()) {
                playMusic.setVisibility(View.VISIBLE);
                pauseMusic.setVisibility(View.GONE);
            }else {
                playMusic.setVisibility(View.GONE);
                pauseMusic.setVisibility(View.VISIBLE);
            }
            if (musicService.getMusicIndex() == -1) {
                currTime.setText(R.string.default_time);
                totalTime.setText(R.string.default_time);
                //musicImg.setImageResource(R.drawable.ic_default_music);
                //musicPlayFragment.setDefaultMusicImg();
                musicName.setText(R.string.bottom_music_default_text);
                musicSinger.setText("");
                isFirst = true;
            }else {
                Song song = musicService.getMusicList().get(musicService.getMusicIndex());
                //musicImg.setImageBitmap(song.getAlbumBmp());
                //musicPlayFragment.setMusicImgBitmap(song.getAlbumBmp());
                musicName.setText(song.getName());
                musicSinger.setText(song.getSinger());
                totalTime.setText(MusicUtils.formatTime(song.getDuration()));
                seekBar.setMax(song.getDuration());//设置进度条的最大值
                if (song.isFavorite()) {
                    addFavorite.setVisibility(View.GONE);
                    removeFavorite.setVisibility(View.VISIBLE);
                }
                //高斯模糊
                //Bitmap bitmap = BlurUtil.doBlur(song.getAlbumBmp(),300,200);
                //layout.setBackground(new BitmapDrawable(getResources(),bitmap));
                isFirst = false;
            }

            Timer timer = new Timer();
            TimerTask timerTask = new TimerTask() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (musicService.getIsSetDataSource()) {
                                //Log.d("qianqingming","time---"+musicService.getCurrPosition());
                                currTime.setText(MusicUtils.formatTime(musicService.getCurrPosition()));
                                seekBar.setProgress(musicService.getCurrPosition());//设置进度条位置
                            }
                        }
                    });
                }
            };
            timer.schedule(timerTask,0,1000);
        }

        //初始化播放模式
        MusicUtils.initPlayMode(this);
        switch (MusicUtils.playMode) {
            case MusicUtils.PLAY_MODE_IN_ORDER:
                break;
            case MusicUtils.PLAY_MODE_SINGLE_CYCLE:
                playInOrder.setVisibility(View.GONE);
                playSingleCycle.setVisibility(View.VISIBLE);
                break;
            case MusicUtils.PLAY_MODE_RANDOM:
                playInOrder.setVisibility(View.GONE);
                playRandom.setVisibility(View.VISIBLE);
                break;
        }

        //------------注册广播----------
        IntentFilter notificationFilter = new IntentFilter();
        notificationFilter.addAction(NotificationUtils.ACTION_CLOSE);
        notificationFilter.addAction(NotificationUtils.ACTION_PLAY_MUSIC);
        notificationFilter.addAction(NotificationUtils.ACTION_PAUSE_MUSIC);
        notificationFilter.addAction(NotificationUtils.ACTION_LAST_MUSIC);
        notificationFilter.addAction(NotificationUtils.ACTION_NEXT_MUSIC);
        notificationFilter.addAction("ACTION_PLAY_COMPLETED");
        musicStateReceiver = new MusicStateReceiver();
        registerReceiver(musicStateReceiver,notificationFilter);
    }

    private void initViews() {
        //初始化小圆点选中状态
        circleView1 = findViewById(R.id.circle1);
        circleView1.setEnabled(false);
        circleView2 = findViewById(R.id.circle2);
        circleView2.setEnabled(true);
        circleView3 = findViewById(R.id.circle3);
        circleView3.setEnabled(false);

        viewPager = findViewById(R.id.view_pager);
        backImg = findViewById(R.id.back_image_view);
        moreImg = findViewById(R.id.more_image_view);
        musicName = findViewById(R.id.music_name);
        musicSinger = findViewById(R.id.music_singer);
        currTime = findViewById(R.id.music_curr_time);
        totalTime = findViewById(R.id.music_total_time);
        playMusic = findViewById(R.id.play_music);
        pauseMusic = findViewById(R.id.pause_music);
        lastMusic = findViewById(R.id.last_music);
        nextMusic = findViewById(R.id.next_music);
        seekBar = findViewById(R.id.seek_bar);
        addFavorite = findViewById(R.id.add_favorite);
        removeFavorite = findViewById(R.id.remove_favorite);
        playInOrder = findViewById(R.id.play_in_order);
        playSingleCycle = findViewById(R.id.play_single_cycle);
        playRandom = findViewById(R.id.play_random);

        backImg.setOnClickListener(this);
        moreImg.setOnClickListener(this);
        playMusic.setOnClickListener(this);
        pauseMusic.setOnClickListener(this);
        lastMusic.setOnClickListener(this);
        nextMusic.setOnClickListener(this);
        addFavorite.setOnClickListener(this);
        removeFavorite.setOnClickListener(this);
        playInOrder.setOnClickListener(this);
        playSingleCycle.setOnClickListener(this);
        playRandom.setOnClickListener(this);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                //Log.d("qianqingming","fromUser:"+b);
                //Log.d("qianqingming","i:"+i);
                if (b) {
                    //如果是用户拖动导致的进度改变
                    if (musicService.getIsSetDataSource()) {
                        //currTime.setText(MusicUtils.formatTime(musicService.getCurrPosition()));
                        musicService.seekToPosition(i);
                    }
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private void initViewPager() {
        fragment_list = new ArrayList<>();

        playListFragment = new PlayListFragment();
        musicPlayFragment = new MusicPlayFragment();
        lyricsFragment = new LyricsFragment();

        fragment_list.add(playListFragment);
        fragment_list.add(musicPlayFragment);
        fragment_list.add(lyricsFragment);

        viewPager.setOffscreenPageLimit(2);

        viewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager(),FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
            @NonNull
            @Override
            public Fragment getItem(int position) {
                return fragment_list.get(position);
            }

            @Override
            public int getCount() {
                return fragment_list.size();
            }
        });

        viewPager.setCurrentItem(1);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    circleView1.setEnabled(true);
                    circleView2.setEnabled(false);
                    circleView3.setEnabled(false);
                }else if (position == 1) {
                    circleView1.setEnabled(false);
                    circleView2.setEnabled(true);
                    circleView3.setEnabled(false);
                }else if (position == 2) {
                    circleView1.setEnabled(false);
                    circleView2.setEnabled(false);
                    circleView3.setEnabled(true);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }


    @Override
    public void onClick(View view) {
        Intent intent;
        SharedPreferences.Editor editor = getSharedPreferences("playMode",Context.MODE_PRIVATE).edit();
        Song song = musicService.getMusicList().get(musicService.getMusicIndex());
        List<Song> favoriteList = MusicUtils.getFavoriteList();
        SharedPreferences.Editor editor1 = getSharedPreferences("favorite",Context.MODE_PRIVATE).edit();
        switch (view.getId()){
            case R.id.back_image_view:
                finish();
                break;
            case R.id.more_image_view:
                //share();
                break;
            case R.id.add_favorite:
                addFavorite.setVisibility(View.GONE);
                removeFavorite.setVisibility(View.VISIBLE);

                song.setFavorite(true);

                editor1.putInt(""+song.getId(),1);
                editor1.apply();

                favoriteList.add(song);

                intent = new Intent("ACTION_ADD_FAVORITE");
                sendBroadcast(intent);

                ToastUtils.showToast(this,this.getResources().getString(R.string.add_favorite_success));
                break;
            case R.id.remove_favorite:
                removeFavorite.setVisibility(View.GONE);
                addFavorite.setVisibility(View.VISIBLE);

                song.setFavorite(false);

                editor1.remove(""+song.getId());
                editor1.apply();

                for (int i = 0; i < favoriteList.size(); i++) {
                    if (favoriteList.get(i).getId() == song.getId()) {
                        favoriteList.remove(i);
                        break;
                    }
                }

                intent = new Intent("ACTION_REMOVE_FAVORITE");
                sendBroadcast(intent);

                ToastUtils.showToast(this,this.getResources().getString(R.string.remove_favorite_success));
                break;
            case R.id.play_in_order:
                playInOrder.setVisibility(View.GONE);
                playSingleCycle.setVisibility(View.VISIBLE);
                ToastUtils.showToast(MusicPlayActivity.this,getResources().getString(R.string.play_single_cycle));

                editor.putInt("play_mode",MusicUtils.PLAY_MODE_SINGLE_CYCLE);
                editor.apply();
                MusicUtils.setPlayMode(MusicUtils.PLAY_MODE_SINGLE_CYCLE);
                break;
            case R.id.play_single_cycle:
                playSingleCycle.setVisibility(View.GONE);
                playRandom.setVisibility(View.VISIBLE);
                ToastUtils.showToast(MusicPlayActivity.this,getResources().getString(R.string.play_random));

                editor.putInt("play_mode",MusicUtils.PLAY_MODE_RANDOM);
                editor.apply();
                MusicUtils.setPlayMode(MusicUtils.PLAY_MODE_RANDOM);
                break;
            case R.id.play_random:
                playRandom.setVisibility(View.GONE);
                playInOrder.setVisibility(View.VISIBLE);
                ToastUtils.showToast(MusicPlayActivity.this,getResources().getString(R.string.play_in_order));

                editor.putInt("play_mode",MusicUtils.PLAY_MODE_IN_ORDER);
                editor.apply();
                MusicUtils.setPlayMode(MusicUtils.PLAY_MODE_IN_ORDER);
                break;
            case R.id.play_music:
                intent = new Intent(NotificationUtils.ACTION_PLAY_MUSIC);
                sendBroadcast(intent);
                break;
            case R.id.pause_music:
                intent = new Intent(NotificationUtils.ACTION_PAUSE_MUSIC);
                sendBroadcast(intent);
                break;
            case R.id.last_music:
                intent = new Intent(NotificationUtils.ACTION_LAST_MUSIC);
                sendBroadcast(intent);
                break;
            case R.id.next_music:
                intent = new Intent(NotificationUtils.ACTION_NEXT_MUSIC);
                sendBroadcast(intent);
                break;
            default:
                break;
        }
    }

    private void share() {
        Intent share_intent = new Intent();
        share_intent.setAction(Intent.ACTION_SEND);//设置分享行为
        share_intent.setType("audio/*");
        //share_intent.setType("text/plain");//设置分享内容的类型

        Song song = MainActivity.musicService.getMusicList().get(MainActivity.musicService.getMusicIndex());
        Log.d("qianqingming","path:"+song.getPath());
        File file = new File(song.getPath());
        Uri uri = Uri.fromFile(file);

        share_intent.putExtra(Intent.EXTRA_STREAM,uri);

        //share_intent.putExtra(Intent.EXTRA_SUBJECT, "share");//添加分享内容标题
        //share_intent.putExtra(Intent.EXTRA_TEXT, "share with you:"+"android");//添加分享内容
        //创建分享的Dialog
        share_intent = Intent.createChooser(share_intent, "分享到");
        startActivity(share_intent);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(musicStateReceiver);
    }

    public class MusicStateReceiver extends BaseReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            super.onReceive(context, intent);
            String action = intent.getAction();
            Log.d("qianqingming","musicPlayActivity:"+action);
            switch (action){
                case NotificationUtils.ACTION_LAST_MUSIC:
                case NotificationUtils.ACTION_NEXT_MUSIC:
                case "ACTION_PLAY_COMPLETED":
                    //objectAnimator.pause();
                    musicPlayFragment.pauseObjectAnimator();
                    if (playMusic.getVisibility() == View.VISIBLE) {
                        //needleImg.startAnimation(playAnimation);
                        musicPlayFragment.startNeedleImgPlayAnim();
                    }
                    Song song = musicService.getMusicList().get(musicService.getMusicIndex());
                    //musicImg.setImageBitmap(song.getAlbumBmp());
                    musicPlayFragment.setMusicImgBitmap(song.getAlbumBmp());
                    musicName.setText(song.getName());
                    musicSinger.setText(song.getSinger());
                    totalTime.setText(MusicUtils.formatTime(song.getDuration()));
                    //---高斯模糊
                    //Bitmap bitmap = BlurUtil.doBlur(song.getAlbumBmp(),300,200);
                    //layout.setBackground(new BitmapDrawable(getResources(),bitmap));
                    //---
                    if (musicService.isPlaying()) {
                        playMusic.setVisibility(View.GONE);
                        pauseMusic.setVisibility(View.VISIBLE);
                    }else {
                        pauseMusic.setVisibility(View.GONE);
                        playMusic.setVisibility(View.VISIBLE);
                    }

                    if (song.isFavorite()) {
                        addFavorite.setVisibility(View.GONE);
                        removeFavorite.setVisibility(View.VISIBLE);
                    }else {
                        removeFavorite.setVisibility(View.GONE);
                        addFavorite.setVisibility(View.VISIBLE);
                    }

                    seekBar.setMax(song.getDuration());
                    //objectAnimator.start();
                    musicPlayFragment.startObjectAnimator();
                    break;
                case NotificationUtils.ACTION_PLAY_MUSIC:
                    //timer.schedule(timerTask,0,1000);
                    if (isFirst) {
                        //Song song2 = MusicUtils.getMusicList(MusicPlayActivity.this).get(musicService.getMusicIndex());
                        Song song2 = musicService.getMusicList().get(musicService.getMusicIndex());
                        //musicImg.setImageBitmap(song2.getAlbumBmp());
                        musicPlayFragment.setMusicImgBitmap(song2.getAlbumBmp());
                        musicName.setText(song2.getName());
                        musicSinger.setText(song2.getSinger());
                        totalTime.setText(MusicUtils.formatTime(song2.getDuration()));
                        seekBar.setMax(song2.getDuration());
                    }
                    //needleImg.startAnimation(playAnimation);
                    musicPlayFragment.startNeedleImgPlayAnim();
                    playMusic.setVisibility(View.GONE);
                    pauseMusic.setVisibility(View.VISIBLE);
                    if (isClosed){
                        //通知栏点击关闭后的处理
                        //Song song1 = MusicUtils.getMusicList(MusicPlayActivity.this).get(musicService.getMusicIndex());
                        Song song1 = musicService.getMusicList().get(musicService.getMusicIndex());
                        totalTime.setText(MusicUtils.formatTime(song1.getDuration()));
                        //musicImg.setImageBitmap(song1.getAlbumBmp());
                        musicPlayFragment.setMusicImgBitmap(song1.getAlbumBmp());
                        musicName.setText(song1.getName());
                        musicSinger.setText(song1.getSinger());
                        isClosed = false;
                        seekBar.setMax(song1.getDuration());
                    }
                    musicPlayFragment.resumeObjectAnimator();
                    /*playAnimation.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            objectAnimator.resume();
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });*/
                    break;
                case NotificationUtils.ACTION_PAUSE_MUSIC:
                    //needleImg.startAnimation(pauseAnimation);
                    musicPlayFragment.startNeedleImgPauseAnim();
                    playMusic.setVisibility(View.VISIBLE);
                    pauseMusic.setVisibility(View.GONE);
                    //objectAnimator.pause();
                    musicPlayFragment.pauseObjectAnimator();
                    break;
                case NotificationUtils.ACTION_CLOSE:
                    //timer.cancel();
                    if (musicService.isPlaying()){
                        //needleImg.startAnimation(pauseAnimation);
                        musicPlayFragment.startNeedleImgPauseAnim();
                    }
                    playMusic.setVisibility(View.VISIBLE);
                    pauseMusic.setVisibility(View.GONE);
                    //objectAnimator.pause();
                    musicPlayFragment.pauseObjectAnimator();
                    currTime.setText(R.string.default_time);
                    totalTime.setText(R.string.default_time);
                    //musicImg.setImageResource(R.drawable.ic_default_music);
                    musicPlayFragment.setDefaultMusicImg();
                    musicName.setText(R.string.bottom_music_default_text);
                    musicSinger.setText("");
                    isClosed = true;
                    seekBar.setProgress(0);
                    break;
            }
        }
    }
}
