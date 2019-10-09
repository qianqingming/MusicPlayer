package com.tct.musicplayer.fragment;


import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tct.musicplayer.MainActivity;
import com.tct.musicplayer.R;
import com.tct.musicplayer.adapter.PlayListAdapter;
import com.tct.musicplayer.adapter.SongsAdapter;

/**
 * 播放列表
 */
public class PlayListFragment extends Fragment {

    private RecyclerView recyclerView;
    private TextView listSizeTextView;

    private PlayListAdapter adapter;

    public PlayListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_play_list, container, false);
        recyclerView = view.findViewById(R.id.recycler_view_play_list);
        listSizeTextView = view.findViewById(R.id.song_list_size);

        listSizeTextView.setText("（" + MainActivity.musicService.getMusicList().size() + "首）");

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new PlayListAdapter(getActivity(), MainActivity.musicService.getMusicList());
        recyclerView.setAdapter(adapter);

        return view;
    }

    public void notifyData() {
        if (recyclerView != null && adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    public void scrollToPosition(int position) {
        if (recyclerView != null) {
            recyclerView.scrollToPosition(position);
            adapter.notifyDataSetChanged();
        }
    }
}
