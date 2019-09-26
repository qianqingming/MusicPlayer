package com.tct.musicplayer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.tct.musicplayer.adapter.SongsAdapter;
import com.tct.musicplayer.entity.Album;
import com.tct.musicplayer.entity.Song;
import com.tct.musicplayer.service.MusicService;
import com.tct.musicplayer.utils.MusicUtils;
import com.tct.musicplayer.utils.NotificationUtils;

import java.util.List;


public class AlbumActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView title;
    private ImageView backImg,moreImg;
    private ImageView albumBmp;
    private RecyclerView musicListRecyclerView;

    private ProgressBar progressBar;
    private ImageView bottomMusicBg;//底部图片
    private TextView bottomDefaultText,bottomMusicName,bottomMusicSinger;//底部默认文字、歌曲名字、歌手
    private ImageView lastMusicImg,playMusicImg,pauseMusicImg,nextMusicImg;//底部上一曲、播放、暂停、下一曲

    private List<Song> musicList;
    private MusicService musicService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album);

        title = findViewById(R.id.title_text_view);
        backImg = findViewById(R.id.back_image_view);
        moreImg = findViewById(R.id.more_image_view);
        albumBmp = findViewById(R.id.album_bmp);
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

        musicService = MainActivity.musicService;
        musicList = musicService.getMusicList();


        Intent intent = getIntent();
        int position = intent.getIntExtra("position", 0);
        Album album = MusicUtils.getSortedAlbumList().get(position);

        title.setText(album.getSinger());
        albumBmp.setImageBitmap(album.getSongList().get(0).getAlbumBmp());

        musicListRecyclerView.setLayoutManager(new LinearLayoutManager(AlbumActivity.this));
        musicListRecyclerView.setAdapter(new SongsAdapter(AlbumActivity.this,album.getSongList()));
    }


    private void changeMusicImageAndText() {
        Song song = musicList.get(musicService.getMusicIndex());
        bottomMusicBg.setImageBitmap(song.getAlbumBmp());
        bottomDefaultText.setVisibility(View.GONE);
        bottomMusicName.setVisibility(View.VISIBLE);
        bottomMusicSinger.setVisibility(View.VISIBLE);
        bottomMusicName.setText(song.getName());
        bottomMusicSinger.setText(song.getSinger());
        //NotificationUtils.updateRemoteViews(musicList,musicService.getMusicIndex(),musicService.isPlaying());
        if (musicService.isPlaying()) {
            playMusicImg.setVisibility(View.GONE);
            pauseMusicImg.setVisibility(View.VISIBLE);
        }else {
            pauseMusicImg.setVisibility(View.GONE);
            playMusicImg.setVisibility(View.VISIBLE);
        }
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
            case R.id.last_music_image_view:
                intent = new Intent(NotificationUtils.ACTION_LAST_MUSIC);
                sendBroadcast(intent);
                changeMusicImageAndText();
                break;
            case R.id.play_music_image_view:
                intent = new Intent(NotificationUtils.ACTION_PLAY_MUSIC);
                sendBroadcast(intent);
                changeMusicImageAndText();
                break;
            case R.id.pause_music_image_view:
                intent = new Intent(NotificationUtils.ACTION_PAUSE_MUSIC);
                sendBroadcast(intent);
                changeMusicImageAndText();
                break;
            case R.id.next_music_image_view:
                intent = new Intent(NotificationUtils.ACTION_NEXT_MUSIC);
                sendBroadcast(intent);
                changeMusicImageAndText();
                break;
            default:
                break;
        }
    }
}
