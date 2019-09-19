package com.tct.musicplayer;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.tct.musicplayer.domain.Song;
import com.tct.musicplayer.receiver.BaseReceiver;
import com.tct.musicplayer.service.MusicService;
import com.tct.musicplayer.utils.MusicUtils;
import com.tct.musicplayer.utils.NotificationUtils;

import java.util.Timer;
import java.util.TimerTask;

public class MusicPlayActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView backImg,moreImg;
    private TextView musicName,musicSinger;
    private ImageView musicImg;
    private TextView currTime,totalTime;
    private ImageView playMusic,pauseMusic,lastMusic,nextMusic;
    private SeekBar seekBar;
    private ImageView needleImg;

    private MusicService musicService = MainActivity.musicService;
    private RotateAnimation animation;//图片旋转动画
    private ObjectAnimator objectAnimator;
    private MusicStateReceiver musicStateReceiver;

    private Timer timer;
    private TimerTask timerTask;

    private boolean isClosed = false;
    private boolean isFirst = false;
    private int needleLeft, needleTop;

    private RotateAnimation playAnimation,pauseAnimation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_play);

        //------------初始化控件，设置点击监听----------
        backImg = findViewById(R.id.back_image_view);
        moreImg = findViewById(R.id.more_image_view);
        musicImg = findViewById(R.id.music_img);
        musicName = findViewById(R.id.music_name);
        musicSinger = findViewById(R.id.music_singer);
        currTime = findViewById(R.id.music_curr_time);
        totalTime = findViewById(R.id.music_total_time);
        playMusic = findViewById(R.id.play_music);
        pauseMusic = findViewById(R.id.pause_music);
        lastMusic = findViewById(R.id.last_music);
        nextMusic = findViewById(R.id.next_music);
        seekBar = findViewById(R.id.seek_bar);
        needleImg = findViewById(R.id.needle_image_view);

        backImg.setOnClickListener(this);
        moreImg.setOnClickListener(this);
        playMusic.setOnClickListener(this);
        pauseMusic.setOnClickListener(this);
        lastMusic.setOnClickListener(this);
        nextMusic.setOnClickListener(this);

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

        //------------注册广播----------
        IntentFilter notificationFilter = new IntentFilter();
        notificationFilter.addAction(NotificationUtils.ACTION_CLOSE);
        notificationFilter.addAction(NotificationUtils.ACTION_PLAY_MUSIC);
        notificationFilter.addAction(NotificationUtils.ACTION_PAUSE_MUSIC);
        notificationFilter.addAction(NotificationUtils.ACTION_LAST_MUSIC);
        notificationFilter.addAction(NotificationUtils.ACTION_NEXT_MUSIC);
        musicStateReceiver = new MusicStateReceiver();
        registerReceiver(musicStateReceiver,notificationFilter);

        //------------初始化Needle距离父控件的left和top----------
        needleLeft = needleImg.getLeft();
        needleTop = needleImg.getTop();

        //------------初始化Needle的播放和暂停动画----------
        playAnimation = new RotateAnimation(-15f,0f,needleLeft, needleTop);
        playAnimation.setInterpolator(new LinearInterpolator());
        playAnimation.setDuration(500);
        playAnimation.setFillAfter(true);

        pauseAnimation = new RotateAnimation(0,-15f,needleLeft, needleTop);
        pauseAnimation.setInterpolator(new LinearInterpolator());
        pauseAnimation.setDuration(500);
        pauseAnimation.setFillAfter(true);

        if (musicService != null) {
            if (!musicService.isPlaying()) {
                playMusic.setVisibility(View.VISIBLE);
                pauseMusic.setVisibility(View.GONE);
                RotateAnimation animation = new RotateAnimation(0,-15f,needleLeft, needleTop);
                animation.setInterpolator(new LinearInterpolator());
                animation.setDuration(100);
                animation.setFillAfter(true);
                needleImg.startAnimation(animation);
            }else {
                playMusic.setVisibility(View.GONE);
                pauseMusic.setVisibility(View.VISIBLE);
            }


            if (musicService.getMusicIndex() == -1) {
                currTime.setText(R.string.default_time);
                totalTime.setText(R.string.default_time);
                musicImg.setImageResource(R.drawable.ic_default_music);
                musicName.setText(R.string.bottom_music_default_text);
                musicSinger.setText("");
                isFirst = true;
            }else {
                Song song = MusicUtils.getMusicList(this).get(musicService.getMusicIndex());
                musicImg.setImageBitmap(song.getAlbumBmp());
                musicName.setText(song.getName());
                musicSinger.setText(song.getSinger());
                totalTime.setText(MusicUtils.formatTime(song.getDuration()));
                seekBar.setMax(song.getDuration());//设置进度条的最大值
                isFirst = false;
            }

            objectAnimator = ObjectAnimator.ofFloat(musicImg,"rotation",0f,360f);
            objectAnimator.setInterpolator(new LinearInterpolator());
            objectAnimator.setDuration(60000);//1min
            objectAnimator.setRepeatCount(ValueAnimator.INFINITE);
            objectAnimator.setRepeatMode(ValueAnimator.RESTART);
            objectAnimator.start();

            if (!musicService.isPlaying()){
                objectAnimator.pause();
            }

            timer = new Timer();
            timerTask = new TimerTask() {
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
    }


    @Override
    public void onClick(View view) {
        Intent intent = null;
        switch (view.getId()){
            case R.id.back_image_view:
                finish();
                break;
            case R.id.more_image_view:
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
                    objectAnimator.pause();
                    if (playMusic.getVisibility() == View.VISIBLE) {
                        needleImg.startAnimation(playAnimation);
                    }
                    Song song = MusicUtils.getMusicList(MusicPlayActivity.this).get(musicService.getMusicIndex());
                    musicImg.setImageBitmap(song.getAlbumBmp());
                    musicName.setText(song.getName());
                    musicSinger.setText(song.getSinger());
                    totalTime.setText(MusicUtils.formatTime(song.getDuration()));
                    if (musicService.isPlaying()) {
                        playMusic.setVisibility(View.GONE);
                        pauseMusic.setVisibility(View.VISIBLE);
                    }else {
                        pauseMusic.setVisibility(View.GONE);
                        playMusic.setVisibility(View.VISIBLE);
                    }
                    seekBar.setMax(song.getDuration());
                    objectAnimator.start();
                    break;
                case NotificationUtils.ACTION_PLAY_MUSIC:
                    //timer.schedule(timerTask,0,1000);
                    if (isFirst) {
                        Song song2 = MusicUtils.getMusicList(MusicPlayActivity.this).get(musicService.getMusicIndex());
                        musicImg.setImageBitmap(song2.getAlbumBmp());
                        musicName.setText(song2.getName());
                        musicSinger.setText(song2.getSinger());
                        totalTime.setText(MusicUtils.formatTime(song2.getDuration()));
                        seekBar.setMax(song2.getDuration());
                    }
                    needleImg.startAnimation(playAnimation);
                    playMusic.setVisibility(View.GONE);
                    pauseMusic.setVisibility(View.VISIBLE);
                    if (isClosed){
                        //通知栏点击关闭后的处理
                        Song song1 = MusicUtils.getMusicList(MusicPlayActivity.this).get(musicService.getMusicIndex());
                        totalTime.setText(MusicUtils.formatTime(song1.getDuration()));
                        musicImg.setImageBitmap(song1.getAlbumBmp());
                        musicName.setText(song1.getName());
                        musicSinger.setText(song1.getSinger());
                        isClosed = false;
                        seekBar.setMax(song1.getDuration());
                    }
                    playAnimation.setAnimationListener(new Animation.AnimationListener() {
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
                    });
                    break;
                case NotificationUtils.ACTION_PAUSE_MUSIC:
                    needleImg.startAnimation(pauseAnimation);
                    playMusic.setVisibility(View.VISIBLE);
                    pauseMusic.setVisibility(View.GONE);
                    objectAnimator.pause();
                    break;
                case NotificationUtils.ACTION_CLOSE:
                    //timer.cancel();
                    if (musicService.isPlaying()){
                        needleImg.startAnimation(pauseAnimation);
                    }
                    playMusic.setVisibility(View.VISIBLE);
                    pauseMusic.setVisibility(View.GONE);
                    objectAnimator.pause();
                    currTime.setText(R.string.default_time);
                    totalTime.setText(R.string.default_time);
                    musicImg.setImageResource(R.drawable.ic_default_music);
                    musicName.setText(R.string.bottom_music_default_text);
                    musicSinger.setText("");
                    isClosed = true;
                    seekBar.setProgress(0);
                    break;
            }
        }
    }
}
