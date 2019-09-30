package com.tct.musicplayer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.tct.musicplayer.adapter.SongsAdapter;
import com.tct.musicplayer.entity.Artist;
import com.tct.musicplayer.entity.Song;
import com.tct.musicplayer.receiver.BaseReceiver;
import com.tct.musicplayer.service.MusicService;
import com.tct.musicplayer.utils.GlideUtils;
import com.tct.musicplayer.utils.MusicUtils;
import com.tct.musicplayer.utils.NotificationUtils;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class ArtistActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView title;
    private ImageView backImg,moreImg;
    private RecyclerView musicListRecyclerView;
    private ImageView lastMusicImg,playMusicImg,pauseMusicImg,nextMusicImg;//底部上一曲、播放、暂停、下一曲
    private ProgressBar progressBar;
    private ImageView bottomMusicBg;//底部图片
    private TextView bottomDefaultText,bottomMusicName,bottomMusicSinger;//底部默认文字、歌曲名字、歌手

    private ObjectAnimator objectAnimator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artist);

        title = findViewById(R.id.title_text_view);
        backImg = findViewById(R.id.back_image_view);
        moreImg = findViewById(R.id.more_image_view);
        musicListRecyclerView = findViewById(R.id.recycler_view_music_list);
        progressBar = findViewById(R.id.progress_bar_music);
        bottomMusicBg = findViewById(R.id.music_bg_image_view);
        bottomDefaultText = findViewById(R.id.default_bottom_music_text);
        bottomMusicName = findViewById(R.id.bottom_music_name);
        bottomMusicSinger = findViewById(R.id.bottom_music_singer);
        lastMusicImg = findViewById(R.id.last_music_image_view);
        playMusicImg = findViewById(R.id.play_music_image_view);
        pauseMusicImg = findViewById(R.id.pause_music_image_view);
        nextMusicImg = findViewById(R.id.next_music_image_view);

        backImg.setOnClickListener(this);
        moreImg.setOnClickListener(this);
        bottomMusicBg.setOnClickListener(this);
        bottomDefaultText.setOnClickListener(this);
        bottomMusicName.setOnClickListener(this);
        bottomMusicSinger.setOnClickListener(this);
        lastMusicImg.setOnClickListener(this);
        playMusicImg.setOnClickListener(this);
        pauseMusicImg.setOnClickListener(this);
        nextMusicImg.setOnClickListener(this);

        //-------------
        Intent intent = getIntent();
        int position = intent.getIntExtra("position", 0);
        Artist artist = MusicUtils.getSortedSingerList().get(position);

        title.setText(artist.getSinger());

        musicListRecyclerView.setLayoutManager(new LinearLayoutManager(ArtistActivity.this));
        musicListRecyclerView.setAdapter(new SongsAdapter(ArtistActivity.this,artist.getSongList()));

        //-----------
        initAnimation();

        //--------------------
        if (MainActivity.musicService != null && MainActivity.musicService.getMusicList() != null && MainActivity.musicService.getMusicIndex() >= 0) {

            MusicService musicService = MainActivity.musicService;

            Timer timer = new Timer();
            TimerTask timerTask = new TimerTask() {
                @Override
                public void run() {
                    //if (MainActivity.musicService.isPlaying()) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                progressBar.setMax(MainActivity.musicService.getDuration());
                                progressBar.setProgress(MainActivity.musicService.getCurrPosition());
                            }
                        });
                    //}
                }
            };
            timer.schedule(timerTask,0,1000);

            Song song = musicService.getMusicList().get(musicService.getMusicIndex());
            GlideUtils.setImg(this,song.getAlbumPath(),bottomMusicBg);
            bottomDefaultText.setVisibility(View.GONE);
            bottomMusicName.setVisibility(View.VISIBLE);
            bottomMusicSinger.setVisibility(View.VISIBLE);
            bottomMusicName.setText(song.getName());
            bottomMusicSinger.setText(song.getSinger());
            if (MainActivity.musicService.isPlaying()) {
                playMusicImg.setVisibility(View.GONE);
                pauseMusicImg.setVisibility(View.VISIBLE);
                objectAnimator.start();
            }else {
                pauseMusicImg.setVisibility(View.GONE);
                playMusicImg.setVisibility(View.VISIBLE);
            }
        }

    }

    private void initAnimation() {
        objectAnimator = ObjectAnimator.ofFloat(bottomMusicBg,"rotation",0f,360f);
        objectAnimator.setInterpolator(new LinearInterpolator());
        objectAnimator.setDuration(20000);
        objectAnimator.setRepeatCount(ValueAnimator.INFINITE);
        objectAnimator.setRepeatMode(ValueAnimator.RESTART);
        //objectAnimator.start();
    }



    @Override
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()){
            case R.id.back_image_view:
                finish();
                break;
            case R.id.more_image_view:
                break;
            case R.id.music_bg_image_view:
            case R.id.default_bottom_music_text:
            case R.id.bottom_music_name:
            case R.id.bottom_music_singer:
                intent = new Intent(this, MusicPlayActivity.class);
                startActivity(intent);
                break;
            case R.id.play_music_image_view:
                objectAnimator.start();
                intent = new Intent(NotificationUtils.ACTION_PLAY_MUSIC);
                sendBroadcast(intent);
                playMusicImg.setVisibility(View.GONE);
                pauseMusicImg.setVisibility(View.VISIBLE);
                break;
            case R.id.pause_music_image_view:
                objectAnimator.pause();
                intent = new Intent(NotificationUtils.ACTION_PAUSE_MUSIC);
                sendBroadcast(intent);
                pauseMusicImg.setVisibility(View.GONE);
                playMusicImg.setVisibility(View.VISIBLE);
                break;
            case R.id.last_music_image_view:
                intent = new Intent(NotificationUtils.ACTION_LAST_MUSIC);
                sendBroadcast(intent);
                GlideUtils.setImg(this,MainActivity.musicService.getMusicList().get(MainActivity.musicService.getMusicIndex()).getAlbumPath(),bottomMusicBg);
                bottomMusicName.setText(MainActivity.musicService.getMusicList().get(MainActivity.musicService.getMusicIndex()).getName());
                bottomMusicSinger.setText(MainActivity.musicService.getMusicList().get(MainActivity.musicService.getMusicIndex()).getSinger());
                if (MainActivity.musicService.isPlaying()) {
                    playMusicImg.setVisibility(View.GONE);
                    pauseMusicImg.setVisibility(View.VISIBLE);
                }else {
                    pauseMusicImg.setVisibility(View.GONE);
                    playMusicImg.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.next_music_image_view:
                intent = new Intent(NotificationUtils.ACTION_NEXT_MUSIC);
                sendBroadcast(intent);
                GlideUtils.setImg(this,MainActivity.musicService.getMusicList().get(MainActivity.musicService.getMusicIndex()).getAlbumPath(),bottomMusicBg);
                bottomMusicName.setText(MainActivity.musicService.getMusicList().get(MainActivity.musicService.getMusicIndex()).getName());
                bottomMusicSinger.setText(MainActivity.musicService.getMusicList().get(MainActivity.musicService.getMusicIndex()).getSinger());
                if (MainActivity.musicService.isPlaying()) {
                    playMusicImg.setVisibility(View.GONE);
                    pauseMusicImg.setVisibility(View.VISIBLE);
                }else {
                    pauseMusicImg.setVisibility(View.GONE);
                    playMusicImg.setVisibility(View.VISIBLE);
                }
                break;
            default:
                break;
        }
    }

}
