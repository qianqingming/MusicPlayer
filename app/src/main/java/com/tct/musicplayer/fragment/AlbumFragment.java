package com.tct.musicplayer.fragment;


import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.tct.musicplayer.R;
import com.tct.musicplayer.adapter.AlbumAdapter;
import com.tct.musicplayer.adapter.AlbumTitleDecoration;
import com.tct.musicplayer.domain.Album;
import com.tct.musicplayer.utils.CharacterUtils;
import com.tct.musicplayer.utils.MusicUtils;
import com.tct.musicplayer.views.RightNavigationBar;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 专辑
 */
public class AlbumFragment extends Fragment {

    private RecyclerView recyclerView;
    private ImageView loadImg;
    private TextView loadText;

    private AlbumAdapter albumAdapter;

    private List<Album> albumList;

    private Map<String,Integer> map;


    public AlbumFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_album, container, false);
        recyclerView = view.findViewById(R.id.recycler_view_album);
        TextView textView = view.findViewById(R.id.tv_letter);
        loadImg = view.findViewById(R.id.loading_img);
        loadText = view.findViewById(R.id.loading_text);
        RightNavigationBar rightNavigationBar = view.findViewById(R.id.right_navigation_bar);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 2);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                String key = CharacterUtils.getPingYin(albumList.get(position).getAlbumName()).substring(0, 1).toUpperCase();
                char ch = key.charAt(0);
                if (!(ch >= 'A' && ch <= 'Z')) {
                    key = "#";
                }
                int size = map.get(key);
                if (size % 2 == 0){
                    return 1;
                }else {
                    int preSize = 0;
                    ch = (char) (ch - 1);
                    while (ch >= 'A'){
                        if (map.containsKey(String.valueOf(ch))) {
                            preSize += map.get(String.valueOf(ch));
                        }
                        ch = (char) (ch - 1);
                    }
                    if ((preSize + size) != position +1) {
                        return 1;
                    }else {
                        return 2;
                    }
                }
            }
        });
        recyclerView.setLayoutManager(gridLayoutManager);
        albumAdapter = new AlbumAdapter(getActivity(),albumList);
        recyclerView.setAdapter(albumAdapter);

        recyclerView.addItemDecoration(new AlbumTitleDecoration(getActivity(), new AlbumTitleDecoration.TitleDecorationCallBack() {
            @Override
            public String getSingerName(int position) {
                return albumList.get(position).getAlbumName();
            }

            @Override
            public int getSingerListSize() {
                return albumList.size();
            }

            @Override
            public boolean isSecond(int position) {
                String key = CharacterUtils.getPingYin(albumList.get(position).getAlbumName()).substring(0, 1).toUpperCase();
                char ch = key.charAt(0);
                if (!(ch >= 'A' && ch <= 'Z')) {
                    key = "#";
                }
                int preSize = 0;
                ch = (char) (ch - 1);
                while (ch >= 'A'){
                    if (map.containsKey(String.valueOf(ch))) {
                        preSize += map.get(String.valueOf(ch));
                    }
                    ch = (char) (ch - 1);
                }
                if ((preSize + 2) == position + 1) {
                    return true;
                }else {
                    return false;
                }
            }

            @Override
            public int getPreMapSize() {
                int lastSize = map.get("#");
                return albumList.size() - lastSize;
            }
        }));

        rightNavigationBar.setTextView(textView);
        rightNavigationBar.setListener(new RightNavigationBar.OnTouchLetterListener() {
            @Override
            public void touchLetter(String s) {
                int preSize = 0;
                if (s.equals("#")){
                    preSize = albumList.size() - map.get("#");
                }else {
                    char ch = s.charAt(0);
                    ch = (char) (ch - 1);
                    while (ch >= 'A'){
                        if (map.containsKey(String.valueOf(ch))) {
                            preSize += map.get(String.valueOf(ch));
                        }
                        ch = (char) (ch - 1);
                    }
                }
                recyclerView.scrollToPosition(preSize);
                GridLayoutManager layoutManager = (GridLayoutManager) recyclerView.getLayoutManager();
                layoutManager.scrollToPositionWithOffset(preSize,0);
            }
        });
        return view;
    }

    /*@Override
    public void onResume() {
        super.onResume();
        if (isFirst) {
            recyclerView.setLayoutManager(new GridLayoutManager(getActivity(),2));
            albumAdapter = new AlbumAdapter(getActivity(),albumList);
            recyclerView.setAdapter(albumAdapter);
            isFirst = false;
        }
    }*/

    public void notifyData() {
        if (albumAdapter != null) {
            albumList = MusicUtils.getSortedAlbumList();
            if (albumList == null || albumList.size() == 0) {
                loadText.setText("没有歌曲文件");
            }else {
                initLetterMap();
                loadImg.setVisibility(View.GONE);
                loadText.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
                albumAdapter.setAlbumList(albumList);
                albumAdapter.notifyDataSetChanged();
            }
        }
    }

    private void initLetterMap() {
        map = new HashMap<>();
        for (int i = 0; i < albumList.size(); i++) {
            String key = CharacterUtils.getPingYin(albumList.get(i).getAlbumName()).substring(0, 1).toUpperCase();
            char ch = key.charAt(0);
            if (!(ch >= 'A' && ch <= 'Z')) {
                key = "#";
            }
            if (map.containsKey(key)){
                map.put(key,map.get(key)+1);
            }else {
                map.put(key,1);
            }
        }
    }

}
