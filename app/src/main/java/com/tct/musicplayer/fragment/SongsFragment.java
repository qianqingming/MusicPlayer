package com.tct.musicplayer.fragment;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tct.musicplayer.MainActivity;
import com.tct.musicplayer.R;
import com.tct.musicplayer.adapter.SongsAdapter;
import com.tct.musicplayer.entity.Song;
import com.tct.musicplayer.utils.MusicUtils;

import java.util.List;


/**
 * 歌曲
 */
public class SongsFragment extends Fragment {

    private ImageView loadImg;
    private TextView loadText;
    private SongsAdapter songsAdapter;
    private RecyclerView recyclerView;
    private TextView textView;

    private boolean isFirst = true;

    private List<Song> list;

    public SongsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_songs, container, false);
        loadImg = view.findViewById(R.id.loading_img);
        loadText = view.findViewById(R.id.loading_text);
        recyclerView = view.findViewById(R.id.recycler_view_songs);

        loadImg.setVisibility(View.GONE);
        loadText.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        songsAdapter = new SongsAdapter(getActivity(), MusicUtils.getMusicList());
        recyclerView.setAdapter(songsAdapter);
//        Log.d("qianqingming","onCreateView");
//        isFirst = true;
        return view;
    }


    @Override
    public void onResume() {
        super.onResume();
        /*if (isFirst) {
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            songsAdapter = new SongsAdapter(getActivity(), MusicUtils.getMusicList(getActivity()));
            recyclerView.setAdapter(songsAdapter);
            isFirst = false;
        }*/
    }


    public void scrollToPosition(int position) {
        if (recyclerView != null) {
            recyclerView.scrollToPosition(position);
            songsAdapter.notifyDataSetChanged();
        }
    }

    public void notifyData() {
        if (songsAdapter != null) {
            //songsAdapter.notifyDataSetChanged();
            list = MusicUtils.getMusicList();
            if (list != null) {
                if (list.size() > 0) {
                    loadImg.setVisibility(View.GONE);
                    loadText.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                    songsAdapter.setList(list);
                    songsAdapter.notifyDataSetChanged();
                }else {
                    loadImg.setVisibility(View.VISIBLE);
                    loadText.setVisibility(View.VISIBLE);
                    loadText.setText(getResources().getText(R.string.no_songs));
                    recyclerView.setVisibility(View.GONE);
                }
            }
        }
    }


}
