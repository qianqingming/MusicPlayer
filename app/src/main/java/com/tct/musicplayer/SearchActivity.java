package com.tct.musicplayer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tct.musicplayer.adapter.SearchAdapter;
import com.tct.musicplayer.entity.Album;
import com.tct.musicplayer.entity.Artist;
import com.tct.musicplayer.entity.Song;
import com.tct.musicplayer.utils.MusicUtils;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {

    private static final String TAG = "qianqingming";

    private ImageView backImg;
    private SearchView searchView;
    private RecyclerView recyclerView;
    private RelativeLayout clearHistoryLayout;
    private LinearLayout historyLayout;
    private FrameLayout noMatchLayout;
    private TextView clearHistoryText;


    private List<Object> list = new ArrayList<>();
    private SearchAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        //修改状态栏字体颜色
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        backImg = findViewById(R.id.back_image_view);
        searchView = findViewById(R.id.search_view);
        recyclerView = findViewById(R.id.recycler_view_search);
        clearHistoryLayout = findViewById(R.id.clear_all_layout);
        historyLayout = findViewById(R.id.history_layout);
        noMatchLayout = findViewById(R.id.no_file_layout);
        clearHistoryText = findViewById(R.id.clear_all);

        backImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        clearHistoryText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearHistoryLayout.setVisibility(View.VISIBLE);
                historyLayout.setVisibility(View.VISIBLE);
            }
        });

        //----------RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new SearchAdapter(this,list);
        recyclerView.setAdapter(adapter);

        //-----------SearchView
        searchView.setIconifiedByDefault(false);
        searchView.setFocusable(true);
        searchView.setFocusableInTouchMode(true);
        searchView.requestFocus();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                //点击输入法的提交时回调
                Log.d(TAG, "onQueryTextSubmit: " + query);
                //保存搜索历史
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //文字改变时回调
                initList(newText);
                Log.d(TAG, "onQueryTextChange: " + newText + "--size:" +  list.size());

                /*if (TextUtils.isEmpty(newText)) {
                    clearHistoryLayout.setVisibility(View.VISIBLE);
                    historyLayout.setVisibility(View.VISIBLE);
                }else {
                    clearHistoryLayout.setVisibility(View.GONE);
                    historyLayout.setVisibility(View.GONE);
                }*/

                if (list.size() == 0 && !TextUtils.isEmpty(newText)) {
                    //没有匹配结果
                    recyclerView.setVisibility(View.GONE);
                    noMatchLayout.setVisibility(View.VISIBLE);
                }else {
                    noMatchLayout.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                }
                adapter.notifyDataSetChanged();
                return true;
            }
        });
    }

    private void initList(String text) {
        list.clear();
        if (!TextUtils.isEmpty(text)) {
            List<Song> musicList = MusicUtils.getMusicList();
            list.add(getResources().getString(R.string.songs));
            for (int i = 0; i < musicList.size(); i++) {
                Song song = musicList.get(i);
                if (song.getName().contains(text)) {
                    list.add(song);
                }
            }
            if (list.size() == 1) {
                list.remove(0);
            }

            int count = list.size();
            list.add(getResources().getString(R.string.artist));
            List<Artist> artistList = MusicUtils.getArtistList();
            for (int i = 0; i < artistList.size(); i++) {
                Artist artist = artistList.get(i);
                if (artist.getSinger().contains(text)) {
                    list.add(artist);
                }
            }
            if (count + 1 == list.size()) {
                list.remove(count);
            }

            count = list.size();
            list.add(getResources().getString(R.string.album));
            List<Album> albumList = MusicUtils.getAlbumList();
            for (int i = 0; i < albumList.size(); i++) {
                Album album = albumList.get(i);
                if (album.getAlbumName().contains(text)) {
                    list.add(album);
                }
            }
            if (count + 1 == list.size()) {
                list.remove(count);
            }
        }
    }
}
