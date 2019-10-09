package com.tct.musicplayer.utils;

import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LyricsUtils {

    public static String getSortedLyrics(HashMap<Long,String> map) {
        StringBuilder sb = new StringBuilder();
        List<Long> timeList = new ArrayList<>(map.keySet());
        Collections.sort(timeList);
        for (int i = 0; i < timeList.size(); i++) {
            //Log.d(TAG, timeList.get(i) + "-" + map.get(timeList.get(i)) + "+++" + map.get(timeList.get(i)).equals(""));
            sb.append(map.get(timeList.get(i)) + "\n");
        }
        return sb.toString();
    }

    public static HashMap<Long,String> parseLyrics(String filePath) {
        HashMap<Long,String> map = new HashMap<>();
        try {
            File file = new File(filePath);
            FileInputStream fileInputStream = new FileInputStream(file);
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String line;
            int min;
            int sec;
            int ms;
            String content;
            long time = 0;
            String regex = "\\[(\\d{1,2}):(\\d{1,2}).(\\d{1,2})\\]";
            Pattern pattern = Pattern.compile(regex);
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
                    //Log.d("qianqingming",time + "-" + content);
                    //Log.d("qianqingming",min + ":" + sec + "." + ms + "-" + content);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return map;
    }
}
