package com.tct.musicplayer.fragment;


import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tct.musicplayer.R;
import com.tct.musicplayer.adapter.ArtistAdapter;
import com.tct.musicplayer.adapter.ItemLineDecoration;
import com.tct.musicplayer.adapter.TitleDecoration;
import com.tct.musicplayer.utils.CharacterUtils;
import com.tct.musicplayer.utils.MusicUtils;
import com.tct.musicplayer.views.RightNavigationBar;

import org.w3c.dom.Text;

import java.text.Collator;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * 艺术家
 */
public class ArtistFragment extends Fragment {

    private View view;
    private RecyclerView recyclerView;
    private TextView textView;
    private ArtistAdapter artistAdapter;
    private List<String> singerList;

    private boolean isFirst = true;

    public ArtistFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //排序
        MusicUtils.getMusicList(getActivity());
        singerList = MusicUtils.getSingerList();
        //在中文字符前加上---中文的每个字的拼音的首字母和“&”
        for (int i = 0; i < singerList.size(); i++) {
            String s = singerList.get(i).substring(0,1);
            if (s.matches("[\\u4E00-\\u9FA5]+")) {
                s = CharacterUtils.getFirstSpell(singerList.get(i)) + "&" + singerList.get(i);
                singerList.set(i,s);
            }
            if (s.equals("<")){
                //如果是<unknown>
                s = "zzzzzzz" + "&" + singerList.get(i);
                singerList.set(i,s);
            }
        }
        Comparator<Object> com = Collator.getInstance(java.util.Locale.CHINA);
        singerList.sort(com);
        //排完序去掉拼音首字母和“&”
        for (int i = 0; i < singerList.size(); i++) {
            String s = singerList.get(i);
            if (s.contains("&")){
                s = s.split("&")[1];
                singerList.set(i,s);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (view == null){
            view = inflater.inflate(R.layout.fragment_artist, container, false);
            recyclerView = view.findViewById(R.id.recycler_view_singer);
            textView = view.findViewById(R.id.tv_letter);
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        }
        isFirst = true;
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isFirst) {
            //数据初始化
            artistAdapter = new ArtistAdapter(getActivity(),singerList);
            recyclerView.setAdapter(artistAdapter);
            recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(),DividerItemDecoration.VERTICAL));
            //recyclerView.addItemDecoration(new ItemLineDecoration(getActivity()));
            recyclerView.addItemDecoration(new TitleDecoration(getActivity(), new TitleDecoration.TitleDecorationCallBack() {
                @Override
                public String getSingerName(int position) {
                    return singerList.get(position);
                }

                @Override
                public int getSingerListSize() {
                    return singerList.size();
                }
            }));
            //为字母导航栏设置touch事件
            RightNavigationBar rightNavigationBar = view.findViewById(R.id.right_navigation_bar);
            rightNavigationBar.setTextView(textView);
            rightNavigationBar.setListener(new RightNavigationBar.OnTouchLetterListener() {
                @Override
                public void touchLetter(String s) {
                    int selectPosition = artistAdapter.getSelectPosition(s);
                    if (selectPosition != -1){
                        recyclerView.scrollToPosition(selectPosition);
                        LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                        layoutManager.scrollToPositionWithOffset(selectPosition,0);
                    }
                }
            });
            isFirst = false;
        }
    }
}
