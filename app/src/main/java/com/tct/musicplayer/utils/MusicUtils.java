package com.tct.musicplayer.utils;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import com.tct.musicplayer.R;
import com.tct.musicplayer.domain.Song;

import java.io.InputStream;
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
                    song.setAlbumBmp(getAlbumArt(context,albumId));
                /*if (size > 1000 * 800) {
                    if (name.contains("-")) {
                        //把歌曲名字和歌手切割开
                        String[] str = name.split("-");
                        singer = str[0];
                        song.setSinger(singer);
                        name = str[1];
                        song.setName(name);
                    } else {
                        song.setName(name);
                    }
                    //去掉歌曲名字后缀.mp3
                    name =name.split("\\.")[0];
                    song.setName(name);
                    list.add(song);
                }*/
                    list.add(song);
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
        if (time / 1000 % 60 < 10) {
            String tt = time / 1000 / 60 + ":0" + time / 1000 % 60;
            return tt;
        } else {
            String tt = time / 1000 / 60 + ":" + time / 1000 % 60;
            return tt;
        }
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
        Cursor cur = context.getContentResolver().query(Uri.parse(mUriAlbums + "/" + String.valueOf(album_id)), projection, null, null, null);
        String album_art = null;
        if (cur.getCount() > 0 && cur.getColumnCount() > 0) {
            cur.moveToNext();
            album_art = cur.getString(0);
        }
        cur.close();
        Bitmap bm = null;
        if (album_art != null) {
            bm = BitmapFactory.decodeFile(album_art);
        } else {
            bm = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher);
        }
        return bm;
    }

}
