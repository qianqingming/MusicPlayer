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
import com.tct.musicplayer.utils.MusicUtils;


/**
 * 歌曲
 */
public class SongsFragment extends Fragment {

    private SongsAdapter songsAdapter;
    private RecyclerView recyclerView;
    private TextView textView;

    private boolean isFirst = true;

    public SongsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_songs, container, false);
        recyclerView = view.findViewById(R.id.recycler_view_songs);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        songsAdapter = new SongsAdapter(getActivity(), MusicUtils.getMusicList(getActivity()));
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

    public void setSelectedPos(int pos) {
        if (songsAdapter != null) {
            songsAdapter.setSelectedPos(pos);
        }
    }

    public void scrollToPosition(int position) {
        if (recyclerView != null) {
            recyclerView.scrollToPosition(position);
        }
    }

    public void notifyData() {
        if (songsAdapter != null) {
            songsAdapter.notifyDataSetChanged();
        }
    }


}
