package com.tct.musicplayer.fragment;


import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.tct.musicplayer.R;
import com.tct.musicplayer.adapter.SongsAdapter;
import com.tct.musicplayer.entity.Song;
import com.tct.musicplayer.utils.MusicUtils;

import java.util.List;

/**
 * 收藏
 */
public class FavoriteFragment extends Fragment {

    private RecyclerView recyclerView;
    private ImageView loadImg;
    private TextView loadText;

    private List<Song> list;
    private SongsAdapter adapter;


    public FavoriteFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favorite, container, false);
        recyclerView = view.findViewById(R.id.recycler_view_songs);
        loadImg = view.findViewById(R.id.loading_img);
        loadText = view.findViewById(R.id.loading_text);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new SongsAdapter(getActivity(),list);
        recyclerView.setAdapter(adapter);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        notifyData();
    }

    public void notifyData() {
        list = MusicUtils.getFavoriteList();
        if (list != null && loadImg != null && loadText != null && recyclerView != null && adapter!= null) {
            if (list.size() > 0) {
                loadImg.setVisibility(View.GONE);
                loadText.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
                adapter.setList(list);
                adapter.notifyDataSetChanged();
            }else {
                loadImg.setVisibility(View.VISIBLE);
                loadText.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
            }
        }
    }

    public void notifyDataSetChanged() {
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }
}
