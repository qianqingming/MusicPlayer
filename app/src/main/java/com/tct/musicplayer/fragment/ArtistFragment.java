package com.tct.musicplayer.fragment;


import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.tct.musicplayer.R;
import com.tct.musicplayer.adapter.ArtistAdapter;
import com.tct.musicplayer.adapter.TitleDecoration;
import com.tct.musicplayer.entity.Artist;
import com.tct.musicplayer.utils.MusicUtils;
import com.tct.musicplayer.views.RightNavigationBar;

import java.util.List;

/**
 * 艺术家
 */
public class ArtistFragment extends Fragment {

    private View view;
    private RecyclerView recyclerView;
    private ImageView loadImg;
    private TextView loadText;
    private ArtistAdapter artistAdapter;
    private List<Artist> singerList;


    public ArtistFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (view == null){
            view = inflater.inflate(R.layout.fragment_artist, container, false);
            recyclerView = view.findViewById(R.id.recycler_view_singer);
            TextView textView = view.findViewById(R.id.tv_letter);
            loadImg = view.findViewById(R.id.loading_img);
            loadText = view.findViewById(R.id.loading_text);
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

            //数据初始化
            artistAdapter = new ArtistAdapter(getActivity(),singerList);
            recyclerView.setAdapter(artistAdapter);
            recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(),DividerItemDecoration.VERTICAL));
            //recyclerView.addItemDecoration(new ItemLineDecoration(getActivity()));
            recyclerView.addItemDecoration(new TitleDecoration(getActivity(), new TitleDecoration.TitleDecorationCallBack() {
                @Override
                public String getSingerFirstLetter(int position) {
                    return singerList.get(position).getFirstLetter();
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
                    if (artistAdapter != null) {
                        int selectPosition = artistAdapter.getSelectPosition(s);
                        if (selectPosition != -1){
                            recyclerView.scrollToPosition(selectPosition);
                            LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                            layoutManager.scrollToPositionWithOffset(selectPosition,0);
                        }
                    }
                }
            });
        }
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        notifyData();
    }

    public void notifyData() {
        if (artistAdapter != null) {
            singerList = MusicUtils.getArtistList();
            if (singerList == null || singerList.size() == 0) {
                loadText.setText("没有歌曲文件");
            } else {
                loadImg.setVisibility(View.GONE);
                loadText.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
                artistAdapter.setSingerList(singerList);
                artistAdapter.notifyDataSetChanged();
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
