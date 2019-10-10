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
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.tct.musicplayer.adapter.SongsAdapter;
import com.tct.musicplayer.entity.Album;
import com.tct.musicplayer.entity.Song;
import com.tct.musicplayer.service.MusicService;
import com.tct.musicplayer.utils.BroadcastUtils;
import com.tct.musicplayer.utils.GlideUtils;
import com.tct.musicplayer.utils.MusicUtils;
import com.tct.musicplayer.utils.NotificationUtils;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


public class AlbumActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView title;
    private ImageView albumBmp;
    private RecyclerView musicListRecyclerView;

    private ProgressBar progressBar;
    private ImageView bottomMusicBg;//底部图片
    private TextView bottomDefaultText,bottomMusicName,bottomMusicSinger;//底部默认文字、歌曲名字、歌手
    private LinearLayout bottomTextLayout;
    private ImageView playMusicImg;
    private ImageView pauseMusicImg;

    private ObjectAnimator objectAnimator;
    private Timer timer;
    private MusicService musicService;
    private boolean hasPlayedMusic = false;
    private MusicStateReceiver musicStateReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album);

        //修改状态栏字体颜色
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        initViews();
        initAnimation();

        Intent intent = getIntent();
        int position = intent.getIntExtra("position", 0);
        Album album = MusicUtils.getSortedAlbumList().get(position);
        title.setText(album.getSinger());
        GlideUtils.setImg(this,album.getSongList().get(0).getAlbumPath(),albumBmp);
        musicListRecyclerView.setLayoutManager(new LinearLayoutManager(AlbumActivity.this));
        musicListRecyclerView.setAdapter(new SongsAdapter(AlbumActivity.this,album.getSongList()));

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

        if (musicService.getMusicIndex() != -1) {
            Song song = musicService.getMusicList().get(musicService.getMusicIndex());
            GlideUtils.setImg(this,song.getAlbumPath(),bottomMusicBg);
            bottomDefaultText.setVisibility(View.GONE);
            //bottomMusicName.setVisibility(View.VISIBLE);
            //bottomMusicSinger.setVisibility(View.VISIBLE);
            bottomTextLayout.setVisibility(View.VISIBLE);
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
        ImageView backImg = findViewById(R.id.back_image_view);
        albumBmp = findViewById(R.id.album_bmp);
        musicListRecyclerView = findViewById(R.id.recycler_view_music_list);

        progressBar = findViewById(R.id.progress_bar_music);
        bottomMusicBg = findViewById(R.id.music_bg_image_view);
        bottomDefaultText = findViewById(R.id.default_bottom_music_text);
        bottomMusicName = findViewById(R.id.bottom_music_name);
        bottomMusicSinger = findViewById(R.id.bottom_music_singer);
        ImageView lastMusicImg = findViewById(R.id.last_music_image_view);
        playMusicImg = findViewById(R.id.play_music_image_view);
        pauseMusicImg = findViewById(R.id.pause_music_image_view);
        ImageView nextMusicImg = findViewById(R.id.next_music_image_view);
        bottomTextLayout = findViewById(R.id.bottom_music_singer_layout);

        backImg.setOnClickListener(this);

        bottomMusicBg.setOnClickListener(this);
        bottomDefaultText.setOnClickListener(this);
        bottomMusicName.setOnClickListener(this);
        bottomMusicSinger.setOnClickListener(this);
        lastMusicImg.setOnClickListener(this);
        playMusicImg.setOnClickListener(this);
        pauseMusicImg.setOnClickListener(this);
        nextMusicImg.setOnClickListener(this);
        bottomTextLayout.setOnClickListener(this);
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
        //bottomMusicName.setVisibility(View.VISIBLE);
        //bottomMusicSinger.setVisibility(View.VISIBLE);
        bottomTextLayout.setVisibility(View.VISIBLE);
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
        //bottomMusicName.setVisibility(View.VISIBLE);
        //bottomMusicSinger.setVisibility(View.VISIBLE);
        bottomTextLayout.setVisibility(View.VISIBLE);
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
        //bottomMusicName.setVisibility(View.GONE);
        //bottomMusicSinger.setVisibility(View.GONE);
        bottomTextLayout.setVisibility(View.GONE);
        pauseMusicImg.setVisibility(View.GONE);
        playMusicImg.setVisibility(View.VISIBLE);
        objectAnimator.pause();
        progressBar.setProgress(0);
        hasPlayedMusic = false;
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
            case R.id.bottom_music_singer_layout:
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


    public class MusicStateReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.d("qianqingming","AlbumPlayActivity:"+action);
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
