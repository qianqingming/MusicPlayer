package com.tct.musicplayer.utils;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;

import com.tct.musicplayer.R;
import com.tct.musicplayer.domain.Song;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MusicUtils {

    private static String name;
    private static String singer;
    private static String path;
    private static int duration;
    private static long size;
    private static long albumId;
    private static long id;

    private static List<Song> list;

    /**
     * 获取歌曲列表
     * @param context
     * @return
     */
    public static List<Song> getMusicList(Context context) {
        if (list == null) {
            list = new ArrayList<>();
            Cursor cursor = context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null,
                    null, MediaStore.Audio.AudioColumns.IS_MUSIC);
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    Song song = new Song();
                    name = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME));
                    id = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID));
                    singer = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST));
                    path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));
                    duration = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION));
                    size = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE));
                    albumId = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID));
                    song.setName(name);
                    song.setSinger(singer);
                    song.setPath(path);
                    song.setDuration(duration);
                    song.setSize(size);
                    song.setId(id);
                    song.setAlbumId(albumId);
                    //去掉歌曲名字后缀.mp3
                    if (name.contains(".")) {
                        int lastIndex = name.lastIndexOf(".");
                        name = name.substring(0,lastIndex);
                        song.setName(name);
                    }
                    //设置专辑图片
                    song.setAlbumBmp(getAlbumArt(context,albumId));
                    if (size > 1000 * 800) {
                        if (name.contains("-")) {
                            //把歌曲名字和歌手切割开
                            String[] str = name.split("-");
                            singer = str[0].replaceAll(" ","");//去掉空格
                            song.setSinger(singer);
                            name = str[1].replaceAll(" ","");//去掉空格
                            song.setName(name);
                        }
                        list.add(song);
                    }
                }
            }
            cursor.close();
        }
        return list;
    }

    /**
     * 转换歌曲时间的格式
     * @param time
     * @return
     */
    public static String formatTime(int time) {
        String formatTime;
        if (time / 1000 / 60 < 10) {
            //分钟数小于10
            if (time / 1000 % 60 < 10) {
                //秒数小于10
                formatTime = "0" + time / 1000 / 60 + ":0" + time / 1000 % 60;
            } else {
                formatTime = "0" + time / 1000 / 60 + ":" + time / 1000 % 60;
            }
        } else {
            if (time / 1000 % 60 < 10) {
                formatTime = time / 1000 / 60 + ":0" + time / 1000 % 60;
            } else {
                formatTime = time / 1000 / 60 + ":" + time / 1000 % 60;
            }
        }
        return formatTime;

        /*if (time / 1000 % 60 < 10) {
            String tt = time / 1000 / 60 + ":0" + time / 1000 % 60;
            return tt;
        } else {
            String tt = time / 1000 / 60 + ":" + time / 1000 % 60;
            return tt;
        }*/
    }


    /**
     * 获取专辑封面
     * @param context
     * @param album_id
     * @return
     */
    private static Bitmap getAlbumArt(Context context, long album_id) {
        String mUriAlbums = "content://media/external/audio/albums";
        String[] projection = new String[]{"album_art"};
        Cursor cur = context.getContentResolver().query(Uri.parse(mUriAlbums + "/" + album_id), projection, null, null, null);
        String album_art = null;
        if (cur.getCount() > 0 && cur.getColumnCount() > 0) {
            cur.moveToNext();
            album_art = cur.getString(0);
            //Log.d("qianqingming---",albumId + "***"+album_art);
        }
        cur.close();
        Bitmap bm;
        if (album_art != null) {
            //还需要判断该路径下的文件是否存在
            File file = new File(album_art);
            if (file.exists()){
                bm = BitmapFactory.decodeFile(album_art);
            }else {
                bm = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_default_music);
            }
        } else {
            bm = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_default_music);
        }
        return bm;
    }

}
