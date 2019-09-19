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
import com.tct.musicplayer.adapter.TitleDecoration;
import com.tct.musicplayer.domain.Album;
import com.tct.musicplayer.utils.CharacterUtils;
import com.tct.musicplayer.utils.MusicUtils;
import com.tct.musicplayer.views.RightNavigationBar;

import java.text.Collator;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * 专辑
 */
public class AlbumFragment extends Fragment {

    private RecyclerView recyclerView;
    private TextView textView;
    private RightNavigationBar rightNavigationBar;

    private AlbumAdapter albumAdapter;

    private List<Album> albumList;

    private boolean isFirst = true;

    public AlbumFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //albumList = MusicUtils.getAlbumList();
        //Log.d("qianqingming","size:"+albumList.size());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_album, container, false);
        recyclerView = view.findViewById(R.id.recycler_view_album);
        textView = view.findViewById(R.id.tv_letter);
        rightNavigationBar = view.findViewById(R.id.right_navigation_bar);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 2);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                Log.d("qianqingming","pos:"+position);
                /*if (position == 0) {
                    if (albumList.size() >= 2 &&
                            CharacterUtils.getPingYin(albumList.get(0).getAlbumName()).substring(0,1).toUpperCase().equals(
                                    CharacterUtils.getPingYin(albumList.get(1).getAlbumName()).substring(0,1).toUpperCase()
                            )){
                        return 1;
                    }else {
                        return 2;
                    }
                }

                int count = 1;
                for (int i = 1; i < position; i++) {
                    if (CharacterUtils.getPingYin(albumList.get(i).getAlbumName()).substring(0,1).toUpperCase().equals(
                                    CharacterUtils.getPingYin(albumList.get(i-1).getAlbumName()).substring(0,1).toUpperCase()
                            )){
                        return 1;
                    }
                }*/
                return 1;
            }
        });
        recyclerView.setLayoutManager(gridLayoutManager);
        albumAdapter = new AlbumAdapter(getActivity(),albumList);
        recyclerView.setAdapter(albumAdapter);

        /*recyclerView.addItemDecoration(new TitleDecoration(getActivity(), new TitleDecoration.TitleDecorationCallBack() {
            @Override
            public String getSingerName(int position) {
                return albumList.get(position).getAlbumName();
            }

            @Override
            public int getSingerListSize() {
                return albumList.size();
            }
        }));*/

        rightNavigationBar.setTextView(textView);
        rightNavigationBar.setListener(new RightNavigationBar.OnTouchLetterListener() {
            @Override
            public void touchLetter(String s) {

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
            //albumList = MusicUtils.getAlbumList();
            sortList();
            albumAdapter.setAlbumList(albumList);
            albumAdapter.notifyDataSetChanged();
        }
    }

    private void sortList() {
        albumList = MusicUtils.getAlbumList();
        for (int i = 0; i < albumList.size(); i++) {
            String s = albumList.get(i).getAlbumName().substring(0,1);
            if (s.matches("[\\u4E00-\\u9FA5]+")) {
                //中文
                s = CharacterUtils.getFirstSpell(albumList.get(i).getAlbumName()) + "&" + albumList.get(i).getAlbumName();
            }else if (s.matches("^[-\\+]?[\\d]*$")) {
                //数字
                s = "zzz" + "&" + albumList.get(i).getAlbumName();
            }else if (s.charAt(0) >= 'a' && s.charAt(0) <= 'z' || s.charAt(0) >= 'A' && s.charAt(0) <= 'Z'){
                s = albumList.get(i).getAlbumName();
            }else {
                //其他 【 《 等
                s = "zzzzzzz" + "&" + albumList.get(i).getAlbumName();
            }
            albumList.get(i).setAlbumName(s);
        }
        //Comparator<Object> com = Collator.getInstance(java.util.Locale.CHINA);
        //albumList.sort(com);
        Collections.sort(albumList, new Comparator<Album>() {
            @Override
            public int compare(Album album, Album t1) {
                return album.getAlbumName().compareToIgnoreCase(t1.getAlbumName());
            }
        });

        //排完序去掉拼音首字母和“&”
        for (int i = 0; i < albumList.size(); i++) {
            String s = albumList.get(i).getAlbumName();
            if (s.contains("&")){
                s = s.split("&")[1];
                albumList.get(i).setAlbumName(s);
            }
        }
    }
}
