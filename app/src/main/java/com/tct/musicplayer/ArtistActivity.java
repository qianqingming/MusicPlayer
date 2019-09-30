package com.tct.musicplayer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.BroadcastReceiver;
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
import com.tct.musicplayer.service.MusicService;
import com.tct.musicplayer.utils.BroadcastUtils;
import com.tct.musicplayer.utils.GlideUtils;
import com.tct.musicplayer.utils.MusicUtils;
import com.tct.musicplayer.utils.NotificationUtils;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class ArtistActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "qianqingming";

    private TextView title;
    private ImageView backImg,moreImg;
    private RecyclerView musicListRecyclerView;
    private ImageView lastMusicImg,playMusicImg,pauseMusicImg,nextMusicImg;//底部上一曲、播放、暂停、下一曲
    private ProgressBar progressBar;
    private ImageView bottomMusicBg;//底部图片
    private TextView bottomDefaultText,bottomMusicName,bottomMusicSinger;//底部默认文字、歌曲名字、歌手

    private ObjectAnimator objectAnimator;
    private Timer timer;
    private MusicService musicService;
    private boolean hasPlayedMusic = false;
    private MusicStateReceiver musicStateReceiver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artist);

        initViews();

        //-----------
        initAnimation();


        //-------------
        Intent intent = getIntent();
        int position = intent.getIntExtra("position", 0);
        Artist artist = MusicUtils.getSortedSingerList().get(position);
        title.setText(artist.getSinger());
        musicListRecyclerView.setLayoutManager(new LinearLayoutManager(ArtistActivity.this));
        musicListRecyclerView.setAdapter(new SongsAdapter(ArtistActivity.this,artist.getSongList()));

        //-------------
        musicService = MainActivity.musicService;
        progressBar.setMax(musicService.getDuration());
        progressBar.setProgress(musicService.getCurrPosition());

        timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                if (MainActivity.musicService.isPlaying()) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressBar.setMax(MainActivity.musicService.getDuration());
                            progressBar.setProgress(MainActivity.musicService.getCurrPosition());
                        }
                    });
                }
            }
        };
        timer.schedule(timerTask,0,1000);

        //---------------
        if (musicService.getMusicIndex() != -1) {
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
                hasPlayedMusic = true;
            }else {
                pauseMusicImg.setVisibility(View.GONE);
                playMusicImg.setVisibility(View.VISIBLE);
            }
        }

        //------------注册广播----------
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BroadcastUtils.ACTION_PLAY_MUSIC);
        intentFilter.addAction(BroadcastUtils.ACTION_PAUSE_MUSIC);
        intentFilter.addAction(BroadcastUtils.ACTION_LAST_MUSIC);
        intentFilter.addAction(BroadcastUtils.ACTION_NEXT_MUSIC);
        intentFilter.addAction(BroadcastUtils.ACTION_PLAY_SELECTED_MUSIC);
        intentFilter.addAction(BroadcastUtils.ACTION_CLOSE);
        intentFilter.addAction(BroadcastUtils.ACTION_PLAY_COMPLETED);
        intentFilter.setPriority(BroadcastUtils.Priority_4);
        musicStateReceiver = new MusicStateReceiver();
        registerReceiver(musicStateReceiver,intentFilter);
    }

    private void initViews() {
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
    protected void onDestroy() {
        super.onDestroy();
        timer.cancel();
        unregisterReceiver(musicStateReceiver);
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
                intent = new Intent(BroadcastUtils.ACTION_PLAY_MUSIC);
                sendOrderedBroadcast(intent,null);
                break;
            case R.id.pause_music_image_view:
                intent = new Intent(BroadcastUtils.ACTION_PAUSE_MUSIC);
                sendOrderedBroadcast(intent,null);
                break;
            case R.id.last_music_image_view:
                intent = new Intent(BroadcastUtils.ACTION_LAST_MUSIC);
                sendOrderedBroadcast(intent,null);
                break;
            case R.id.next_music_image_view:
                intent = new Intent(BroadcastUtils.ACTION_NEXT_MUSIC);
                sendOrderedBroadcast(intent,null);
                break;
            default:
                break;
        }
    }


    private void playMusic() {
        if (hasPlayedMusic){
            objectAnimator.resume();
        }else {
            objectAnimator.start();
            hasPlayedMusic = true;
        }
        Song song = musicService.getMusicList().get(musicService.getMusicIndex());
        GlideUtils.setImg(this,song.getAlbumPath(),bottomMusicBg);
        bottomDefaultText.setVisibility(View.GONE);
        bottomMusicName.setVisibility(View.VISIBLE);
        bottomMusicSinger.setVisibility(View.VISIBLE);
        bottomMusicName.setText(song.getName());
        bottomMusicSinger.setText(song.getSinger());
        playMusicImg.setVisibility(View.GONE);
        pauseMusicImg.setVisibility(View.VISIBLE);
    }

    private void pauseMusic() {
        objectAnimator.pause();
        pauseMusicImg.setVisibility(View.GONE);
        playMusicImg.setVisibility(View.VISIBLE);
        hasPlayedMusic = true;
    }

    private void playLastMusic() {
        Song song = MainActivity.musicService.getMusicList().get(MainActivity.musicService.getMusicIndex());
        GlideUtils.setImg(this,song.getAlbumPath(),bottomMusicBg);
        bottomMusicName.setText(song.getName());
        bottomMusicSinger.setText(song.getSinger());
        if (musicService.isPlaying()) {
            playMusicImg.setVisibility(View.GONE);
            pauseMusicImg.setVisibility(View.VISIBLE);
        }else {
            pauseMusicImg.setVisibility(View.GONE);
            playMusicImg.setVisibility(View.VISIBLE);
        }
        hasPlayedMusic = true;
    }

    private void playCompleted() {
        Song song = MainActivity.musicService.getMusicList().get(MainActivity.musicService.getMusicIndex());
        GlideUtils.setImg(this,song.getAlbumPath(),bottomMusicBg);
        bottomMusicName.setText(song.getName());
        bottomMusicSinger.setText(song.getSinger());
        objectAnimator.start();
        hasPlayedMusic = true;
    }

    private void playSelectedMusic(int index) {
        Song song = musicService.getMusicList().get(musicService.getMusicIndex());
        GlideUtils.setImg(this,song.getAlbumPath(),bottomMusicBg);
        bottomDefaultText.setVisibility(View.GONE);
        bottomMusicName.setVisibility(View.VISIBLE);
        bottomMusicSinger.setVisibility(View.VISIBLE);
        bottomMusicName.setText(song.getName());
        bottomMusicSinger.setText(song.getSinger());
        if (musicService.isPlaying()) {
            playMusicImg.setVisibility(View.GONE);
            pauseMusicImg.setVisibility(View.VISIBLE);
        }else {
            pauseMusicImg.setVisibility(View.GONE);
            playMusicImg.setVisibility(View.VISIBLE);
        }
        objectAnimator.start();
        hasPlayedMusic = true;
    }

    private void stopMusic() {
        bottomMusicBg.setImageResource(R.drawable.ic_default_music);
        bottomDefaultText.setVisibility(View.VISIBLE);
        bottomMusicName.setVisibility(View.GONE);
        bottomMusicSinger.setVisibility(View.GONE);
        pauseMusicImg.setVisibility(View.GONE);
        playMusicImg.setVisibility(View.VISIBLE);
        objectAnimator.pause();
        progressBar.setProgress(0);
        hasPlayedMusic = false;
    }


    public class MusicStateReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.d("qianqingming","ArtistActivity:"+action);
            switch (action){
                case BroadcastUtils.ACTION_PLAY_MUSIC:
                    playMusic();
                    break;
                case BroadcastUtils.ACTION_PAUSE_MUSIC:
                    pauseMusic();
                    break;
                case BroadcastUtils.ACTION_LAST_MUSIC:
                case BroadcastUtils.ACTION_NEXT_MUSIC:
                    playLastMusic();
                    break;
                case BroadcastUtils.ACTION_PLAY_SELECTED_MUSIC:
                    int index = intent.getIntExtra("position",0);
                    playSelectedMusic(index);
                    break;
                case BroadcastUtils.ACTION_PLAY_COMPLETED:
                    playCompleted();
                    break;
                case BroadcastUtils.ACTION_CLOSE:
                    stopMusic();
                    break;
            }
        }
    }

}
