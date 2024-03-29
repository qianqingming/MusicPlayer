package com.tct.musicplayer.fragment;


import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import com.tct.musicplayer.entity.Album;
import com.tct.musicplayer.utils.MusicUtils;
import com.tct.musicplayer.views.RightNavigationBar;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 专辑
 */
public class AlbumFragment extends Fragment {

    private static final String TAG = "qianqingming";

    private RecyclerView recyclerView;
    private ImageView loadImg;
    private TextView loadText;

    private AlbumAdapter albumAdapter;

    private List<Album> albumList;

    private Map<String,Integer> map;


    public AlbumFragment() {
        // Required empty public constructor
        Log.d(TAG, "AlbumFragment: construct");
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: ");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: ");

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
                String key = albumList.get(position).getFirstLetter();
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
            public String getAlbumFirstLetter(int position) {
                return albumList.get(position).getFirstLetter();
            }

            @Override
            public boolean isSecond(int position) {
                char ch = albumList.get(position).getFirstLetter().charAt(0);
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
                return albumList.size() - map.get("#");
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

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: ");
        notifyData();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        Log.d(TAG, "onAttach: ");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: ");
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: ");
    }


    public void notifyData() {
        if (albumAdapter != null) {
            albumList = MusicUtils.getAlbumList();
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
        String key;
        for (int i = 0; i < albumList.size(); i++) {
            key = albumList.get(i).getFirstLetter();
            if (map.containsKey(key)){
                map.put(key,map.get(key)+1);
            }else {
                map.put(key,1);
            }
        }
    }

    public void scrollToPosition(int position) {
        if (recyclerView != null) {
            recyclerView.scrollToPosition(position);
            //adapter.notifyDataSetChanged();
        }
    }

    public void smoothScrollToTop() {
        if (recyclerView != null) {
            recyclerView.smoothScrollToPosition(0);
        }
    }

}
