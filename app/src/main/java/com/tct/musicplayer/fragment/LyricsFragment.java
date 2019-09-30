package com.tct.musicplayer.fragment;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tct.musicplayer.MainActivity;
import com.tct.musicplayer.R;
import com.tct.musicplayer.entity.Song;

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
        final TextView textView = view.findViewById(R.id.text_view);
        final HashMap<Long, String> map = parseLyrics("Beyond.lrc");
        StringBuilder sb = new StringBuilder();
        Set<Long> keySet = map.keySet();
        for (Long l : keySet) {
            sb.append(map.get(l) + "\n");
        }
        textView.setText(sb.toString());

        final List<Long> timeList = new ArrayList<>(keySet);
        Collections.sort(timeList);

        /*Timer timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                if (MainActivity.musicService.isPlaying()) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            int a = MainActivity.musicService.getCurrPosition();
                            int index = 0;
                            for (int i = 0; i < timeList.size(); i++) {
                                if (timeList.get(i) >= a) {
                                    index = i;
                                    break;
                                }
                            }
                            String s = map.get(timeList.get(index));
                            textView.setText(s == null ? "" : s);
                        }
                    });
                }
            }
        };
        timer.schedule(timerTask,0,1000);*/


        return view;
    }


    private HashMap<Long,String> parseLyrics(String file) {
        //[01:20.26]今天我寒夜里看雪飘过
        //[01:20.26][01:26.95]今天我寒夜里看雪飘过
        //[01:20.26][01:26.95][01:26.95]今天我寒夜里看雪飘过
        HashMap<Long,String> map = new HashMap<>();
        //解析歌词
        try {
            InputStreamReader inputStreamReader = new InputStreamReader(getResources().getAssets().open(file));
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String line;
            int min;
            int sec;
            int ms;
            String content;
            long time = 0;
            String regex = "\\[(\\d{1,2}):(\\d{1,2}).(\\d{1,2})\\]"; // 正则表达式
            Pattern pattern = Pattern.compile(regex); // 创建 Pattern 对象
            while((line = bufferedReader.readLine()) != null){
                line = line.trim();
                if(line.equals(""))
                    continue;
                Matcher matcher = pattern.matcher(line);
                while (matcher.find()) {
                    min = Integer.parseInt(matcher.group(1));
                    sec = Integer.parseInt(matcher.group(2));
                    ms = Integer.parseInt(matcher.group(3));
                    time = min * 60 * 1000 + sec * 1000 + ms;

                    content = line.substring(matcher.end());
                    map.put(time,content);
                    Log.d("qianqingming",time + "-" + content);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return map;
    }


    /*private String parseLyrics(String file) {
        //解析歌词
        try {
            InputStreamReader inputStreamReader = new InputStreamReader(getResources().getAssets().open(file));
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String line;
            StringBuilder result = new StringBuilder();
            while((line = bufferedReader.readLine()) != null){
                if(line.trim().equals(""))
                    continue;
                result.append(line + "\r\n");
            }
            return result.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }*/
}
