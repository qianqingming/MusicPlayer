package com.tct.musicplayer.fragment;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.tct.musicplayer.MainActivity;
import com.tct.musicplayer.R;
import com.tct.musicplayer.adapter.FavoriteAdapter;
import com.tct.musicplayer.adapter.SongsAdapter;
import com.tct.musicplayer.entity.Song;
import com.tct.musicplayer.utils.MusicUtils;
import com.tct.musicplayer.utils.ToastUtils;

import java.util.List;

/**
 * 收藏
 */
public class FavoriteFragment extends Fragment {

    private RecyclerView recyclerView;
    private ImageView loadImg;
    private TextView loadText;

    private List<Song> list;
    private FavoriteAdapter adapter;
    LinearLayoutManager layoutManager;
    private FloatingActionButton floatingActionButton;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                floatingActionButton.setVisibility(View.GONE);
            }
        }
    };


    public FavoriteFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favorite, container, false);
        recyclerView = view.findViewById(R.id.recycler_view_songs);

        floatingActionButton = view.findViewById(R.id.floating_button);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int pos = MainActivity.musicService.getMusicIndex();
                pos = pos >= 0 ? pos : 0;
                scrollToTop(pos);
                ToastUtils.showToast(getActivity(),getResources().getString(R.string.scroll_to_curr_pos));
            }
        });

        loadImg = view.findViewById(R.id.loading_img);
        loadText = view.findViewById(R.id.loading_text);
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        adapter = new FavoriteAdapter(getActivity(),list);
        recyclerView.setAdapter(adapter);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    //floatingActionButton.setVisibility(View.GONE);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Thread.sleep(5000);
                                mHandler.sendEmptyMessage(1);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                }else {
                    floatingActionButton.setVisibility(View.VISIBLE);
                }
            }
        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        notifyData();
    }

    public void scrollToTop(int position) {
        if (layoutManager != null) {
            layoutManager.scrollToPositionWithOffset(position,0);
        }
    }

    public void smoothScrollToTop() {
        if (recyclerView != null) {
            recyclerView.smoothScrollToPosition(0);
        }
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

    public void scrollToPosition(int position) {
        if (recyclerView != null) {
            recyclerView.scrollToPosition(position);
            //adapter.notifyDataSetChanged();
        }
    }
}
