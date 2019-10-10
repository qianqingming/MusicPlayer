package com.tct.musicplayer.fragment;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tct.musicplayer.MainActivity;
import com.tct.musicplayer.R;
import com.tct.musicplayer.entity.LyricsRow;
import com.tct.musicplayer.entity.Song;
import com.tct.musicplayer.utils.LyricsUtils;
import com.tct.musicplayer.views.LyricsView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 歌词
 */
public class LyricsFragment extends Fragment {

    private static final String TAG = "qianqingming";

    public LyricsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_lyrics, container, false);
        //final TextView textView = view.findViewById(R.id.text_view);
        //HashMap<Long, String> map = LyricsUtils.parseLyrics(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + "海阔天空 - Beyond.lrc");
        //textView.setText(LyricsUtils.getSortedLyrics(map));

        List<LyricsRow> lyricsRowList = LyricsUtils.parseLyrics(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + "海阔天空 - Beyond.lrc");

        LyricsView lyricsView = view.findViewById(R.id.lyrics_view);
        lyricsView.hasLyrics(true);
        lyricsView.setLyricsRowList(lyricsRowList);
        return view;
    }

}
