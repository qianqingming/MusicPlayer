package com.tct.musicplayer.fragment;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.tct.musicplayer.MainActivity;
import com.tct.musicplayer.R;
import com.tct.musicplayer.adapter.SongsAdapter;
import com.tct.musicplayer.entity.Song;
import com.tct.musicplayer.utils.MusicUtils;
import com.tct.musicplayer.utils.ToastUtils;

import java.util.List;


/**
 * 歌曲
 */
public class SongsFragment extends Fragment {

    private ImageView loadImg;
    private TextView loadText;
    private SongsAdapter songsAdapter;
    private RecyclerView recyclerView;
    private TextView textView;
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


    private boolean isFirst = true;

    private List<Song> list;

    public SongsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_songs, container, false);
        loadImg = view.findViewById(R.id.loading_img);
        loadText = view.findViewById(R.id.loading_text);
        recyclerView = view.findViewById(R.id.recycler_view_songs);

        floatingActionButton = view.findViewById(R.id.floating_button);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int pos = MainActivity.musicService.getMusicIndex();
                pos = pos >= 0 ? pos : 0;

                if (list != null && list.size() > 0) {
                    Song song = MainActivity.musicService.getMusicList().get(pos);
                    for (int i = 0; i < list.size(); i++) {
                        if (list.get(i).getSongId().equals(song.getSongId())) {
                            pos = i;
                            break;
                        }
                    }
                }

                //scrollToPosition(pos);
                scrollToTop(pos);
                ToastUtils.showToast(getActivity(),getResources().getString(R.string.scroll_to_curr_pos));
            }
        });

        loadImg.setVisibility(View.GONE);
        loadText.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);

        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        songsAdapter = new SongsAdapter(getActivity(), MusicUtils.getMusicList());
        recyclerView.setAdapter(songsAdapter);

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
    }

    public void scrollToTop(int position) {
        if (layoutManager != null) {
            layoutManager.scrollToPositionWithOffset(position,0);
        }
    }

    public void scrollToPosition(int position) {
        if (recyclerView != null) {
            recyclerView.scrollToPosition(position);
            songsAdapter.notifyDataSetChanged();
        }
    }

    public void smoothScrollToTop() {
        if (recyclerView != null) {
            recyclerView.smoothScrollToPosition(0);
        }
    }

    public void notifyData() {
        if (songsAdapter != null) {
            //songsAdapter.notifyDataSetChanged();
            list = MusicUtils.getMusicList();
            if (list != null) {
                if (list.size() > 0) {
                    loadImg.setVisibility(View.GONE);
                    loadText.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                    songsAdapter.setList(list);
                    songsAdapter.notifyDataSetChanged();
                }else {
                    loadImg.setVisibility(View.VISIBLE);
                    loadText.setVisibility(View.VISIBLE);
                    loadText.setText(getResources().getText(R.string.no_songs));
                    recyclerView.setVisibility(View.GONE);
                }
            }
        }
    }


}
