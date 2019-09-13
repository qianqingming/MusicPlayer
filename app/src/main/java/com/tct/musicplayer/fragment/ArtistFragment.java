package com.tct.musicplayer.fragment;


import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tct.musicplayer.R;
import com.tct.musicplayer.adapter.ArtistAdapter;
import com.tct.musicplayer.adapter.ItemLineDecoration;
import com.tct.musicplayer.utils.MusicUtils;

import java.util.List;

/**
 * 艺术家
 */
public class ArtistFragment extends Fragment {

    private RecyclerView recyclerView;
    private ArtistAdapter artistAdapter;

    public ArtistFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_artist, container, false);
        recyclerView = view.findViewById(R.id.recycler_view_singer);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        artistAdapter = new ArtistAdapter(getActivity(), MusicUtils.getMusicList(getActivity()));
        recyclerView.setAdapter(artistAdapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(),DividerItemDecoration.VERTICAL));
        //recyclerView.addItemDecoration(new ItemLineDecoration());
        return view;
    }

}
