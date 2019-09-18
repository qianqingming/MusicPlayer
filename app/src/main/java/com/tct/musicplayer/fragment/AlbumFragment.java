package com.tct.musicplayer.fragment;


import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tct.musicplayer.R;
import com.tct.musicplayer.adapter.AlbumAdapter;
import com.tct.musicplayer.domain.Album;
import com.tct.musicplayer.utils.MusicUtils;
import com.tct.musicplayer.views.RightNavigationBar;

import java.util.List;

/**
 * 专辑
 */
public class AlbumFragment extends Fragment {

    private RecyclerView recyclerView;
    private TextView textView;
    private RightNavigationBar rightNavigationBar;

    private List<Album> albumList;

    private boolean isFirst = true;

    public AlbumFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        albumList = MusicUtils.getAlbumList();
        Log.d("qianqingming","size:"+albumList.size());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_album, container, false);
        recyclerView = view.findViewById(R.id.recycler_view_album);
        textView = view.findViewById(R.id.tv_letter);
        rightNavigationBar = view.findViewById(R.id.right_navigation_bar);
        isFirst = true;
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isFirst) {
            recyclerView.setLayoutManager(new GridLayoutManager(getActivity(),2));
            AlbumAdapter adapter = new AlbumAdapter(getActivity(),albumList);
            recyclerView.setAdapter(adapter);
            isFirst = false;
        }
    }
}
