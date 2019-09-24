package com.tct.musicplayer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.tct.musicplayer.adapter.SongsAdapter;
import com.tct.musicplayer.domain.Artist;
import com.tct.musicplayer.utils.MusicUtils;

public class ArtistActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView title;
    private ImageView backImg,moreImg;
    private RecyclerView musicListRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artist);

        title = findViewById(R.id.title_text_view);
        backImg = findViewById(R.id.back_image_view);
        moreImg = findViewById(R.id.more_image_view);
        musicListRecyclerView = findViewById(R.id.recycler_view_music_list);

        backImg.setOnClickListener(this);
        moreImg.setOnClickListener(this);

        Intent intent = getIntent();
        int position = intent.getIntExtra("position", 0);
        Artist artist = MusicUtils.getSortedSingerList().get(position);

        title.setText(artist.getSinger());

        musicListRecyclerView.setLayoutManager(new LinearLayoutManager(ArtistActivity.this));
        musicListRecyclerView.setAdapter(new SongsAdapter(ArtistActivity.this,artist.getSongList()));
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.back_image_view:
                finish();
                break;
            case R.id.more_image_view:
                break;
            default:
                break;
        }
    }
}
