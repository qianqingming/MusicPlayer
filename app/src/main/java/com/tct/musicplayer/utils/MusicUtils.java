package com.tct.musicplayer.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;

import com.tct.musicplayer.R;
import com.tct.musicplayer.domain.Album;
import com.tct.musicplayer.domain.Artist;
import com.tct.musicplayer.domain.Song;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MusicUtils {

    public static final int PLAY_MODE_IN_ORDER = 0;//顺序播放
    public static final int PLAY_MODE_SINGLE_CYCLE = 1;//单曲循环
    public static final int PLAY_MODE_RANDOM = 2;//随机播放

    public static int playMode;


    private static List<Song> list;
    private static List<String> singerList;
    private static List<Album> albumList;
    private static List<Artist> artistList;
    private static List<Song> favoriteList;

    private static boolean isFirst = true;


    public static List<Song> getTenMuscList(Context context) {
        List<Song> list = new ArrayList<>();
        Cursor cursor = context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null,
                null, MediaStore.Audio.AudioColumns.IS_MUSIC);
        if (cursor != null) {
            String name;
            String singer;
            String path;
            int duration;
            long size;
            long albumId;
            long id;
            String albumName;
            int count = 0;
            while (cursor.moveToNext()) {
                if (count == 8) {
                    break;
                }
                Song song = new Song();
                name = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME));
                id = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID));
                singer = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST));
                path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));
                duration = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION));
                size = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE));
                albumId = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID));
                albumName = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM));//专辑名称
                //String year = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.YEAR));//歌曲的发行时间
                //Log.d("qianqingming","year--"+year);
                song.setName(name);
                song.setSinger(singer);
                song.setPath(path);
                song.setDuration(duration);
                song.setSize(size);
                song.setId(id);
                song.setAlbumId(albumId);
                song.setAlbumName(albumName);
                //去掉歌曲名字后缀.mp3
                if (name.contains(".")) {
                    int lastIndex = name.lastIndexOf(".");
                    name = name.substring(0,lastIndex);
                    song.setName(name);
                }
                //设置专辑图片
                song.setAlbumBmp(getAlbumArt(context,albumId));
                //是否被收藏
                SharedPreferences preferences = context.getSharedPreferences("favorite", Context.MODE_PRIVATE);
                int favorite = preferences.getInt("" + id, -1);
                if (favorite != -1) {
                    song.setFavorite(true);
                }
                if (size > 1000 * 800) {
                    if (name.contains("-")) {
                        //把歌曲名字和歌手切割开
                        String[] str = name.split("-");
                        //song.setSinger(str[0].trim());
                        song.setName(str[1].trim());
                    }
                    list.add(song);
                    count++;
                }
            }
            cursor.close();
        }
        return list;
    }


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
                String name;
                String singer;
                String path;
                int duration;
                long size;
                long albumId;
                long id;
                String albumName;
                while (cursor.moveToNext()) {
                    Song song = new Song();
                    name = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME));
                    id = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID));
                    singer = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST));
                    path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));
                    duration = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION));
                    size = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE));
                    albumId = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID));
                    albumName = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM));//专辑名称
                    //String year = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.YEAR));//歌曲的发行时间
                    //Log.d("qianqingming","year--"+year);
                    song.setName(name);
                    song.setSinger(singer);
                    song.setPath(path);
                    song.setDuration(duration);
                    song.setSize(size);
                    song.setId(id);
                    song.setAlbumId(albumId);
                    song.setAlbumName(albumName);
                    //去掉歌曲名字后缀.mp3
                    if (name.contains(".")) {
                        int lastIndex = name.lastIndexOf(".");
                        name = name.substring(0,lastIndex);
                        song.setName(name);
                    }
                    //设置专辑图片
                    song.setAlbumBmp(getAlbumArt(context,albumId));
                    //是否被收藏
                    SharedPreferences preferences = context.getSharedPreferences("favorite", Context.MODE_PRIVATE);
                    int favorite = preferences.getInt("" + id, -1);
                    if (favorite != -1) {
                        song.setFavorite(true);
                    }
                    if (size > 1000 * 800) {
                        if (name.contains("-")) {
                            //把歌曲名字和歌手切割开
                            String[] str = name.split("-");
                            //song.setSinger(str[0].trim());
                            song.setName(str[1].trim());
                        }
                        list.add(song);
                    }
                }
                cursor.close();
            }
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
            /*BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            options.inSampleSize = 2;
            options.inJustDecodeBounds = false;*/
            if (file.exists()){
                bm = BitmapFactory.decodeFile(album_art);
                //bm = BitmapFactory.decodeFile(album_art,options);
            }else {
                bm = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_default_music);
                //bm = BitmapFactory.decodeResource(context.getResources(),R.drawable.ic_default_music,options);
            }
        } else {
            bm = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_default_music);
        }
        return bm;
    }

    /**
     * 获取艺术家列表
     * @return
     */
    public static List<Artist> getSingerList() {
        if (list != null) {
            if (artistList == null) {
                artistList = new ArrayList<>();
                for (int i = 0; i < list.size(); i++) {
                    Song song = list.get(i);
                    Artist artist = new Artist();
                    artist.setSinger(song.getSinger());
                    if (!artistList.contains(artist)) {
                        artist.getSongList().add(song);
                        artistList.add(artist);
                    }else {
                        int index = artistList.indexOf(artist);
                        artistList.get(index).getSongList().add(song);
                    }
                }
            }
            return artistList;
        }else {
            return null;
        }
    }

    /**
     * 获取排好序的艺术家列表
     * @return
     */
    public static List<Artist> getSortedSingerList() {
        artistList = getSingerList();
        List<Artist> sortArtistList = artistList;
        if (sortArtistList != null) {
            //排序
            //在中文字符前加上---中文的每个字的拼音的首字母和“&”
            for (int i = 0; i < sortArtistList.size(); i++) {
                String s = sortArtistList.get(i).getSinger().substring(0,1);
                if (s.matches("[\\u4E00-\\u9FA5]+")) {
                    //中文
                    s = CharacterUtils.getEachFirstSpell(sortArtistList.get(i).getSinger()) + "&" + sortArtistList.get(i).getSinger();
                }else if (s.charAt(0) >= 'a' && s.charAt(0) <= 'z' || s.charAt(0) >= 'A' && s.charAt(0) <= 'Z'){
                    //字母
                    s = sortArtistList.get(i).getSinger();
                }else {
                    //其他 < 等
                    s = "zzzzzzz" + "&" + sortArtistList.get(i).getSinger();
                }
                sortArtistList.get(i).setSinger(s);
            }
            Collections.sort(sortArtistList, new Comparator<Artist>() {
                @Override
                public int compare(Artist artist, Artist t1) {
                    return artist.getSinger().compareToIgnoreCase(t1.getSinger());
                }
            });
            //排完序去掉拼音首字母和“&”
            for (int i = 0; i < artistList.size(); i++) {
                String s = artistList.get(i).getSinger();
                if (s.contains("&")){
                    s = s.split("&")[1];
                    artistList.get(i).setSinger(s);
                }
            }
        }
        return sortArtistList;
    }


    /**
     * 获取歌手列表
     * @return
     */
    /*public static List<String> getSingerList() {
        if (list != null) {
            if (singerList == null) {
                singerList = new ArrayList<>();
                for (int i = 0; i < list.size(); i++) {
                    if (!singerList.contains(list.get(i).getSinger())){
                        singerList.add(list.get(i).getSinger());
                    }
                }
            }
            return singerList;
        }else {
            return null;
        }
    }*/

    /**
     * 获取排好序的歌手列表
     */
    /*public static List<String> getSortedSingerList() {
        singerList = getSingerList();
        List<String> sortSingerList = singerList;
        if (sortSingerList != null) {
            //排序
            //在中文字符前加上---中文的每个字的拼音的首字母和“&”
            for (int i = 0; i < sortSingerList.size(); i++) {
                String s = sortSingerList.get(i).substring(0,1);
                if (s.matches("[\\u4E00-\\u9FA5]+")) {
                    s = CharacterUtils.getEachFirstSpell(sortSingerList.get(i)) + "&" + sortSingerList.get(i);
                    sortSingerList.set(i,s);
                }else if (s.equals("<")){
                    //如果是<unknown>
                    s = "zzzzzzz" + "&" + sortSingerList.get(i);
                    sortSingerList.set(i,s);
                }
            }
            Comparator<Object> com = Collator.getInstance(java.util.Locale.CHINA);
            sortSingerList.sort(com);
            //排完序去掉拼音首字母和“&”
            for (int i = 0; i < sortSingerList.size(); i++) {
                String s = sortSingerList.get(i);
                if (s.contains("&")){
                    s = s.split("&")[1];
                    sortSingerList.set(i,s);
                }
            }
        }
        return sortSingerList;
    }*/

    /**
     * 获取专辑列表
     * @return
     */
    public static List<Album> getAlbumList() {
        if (list != null) {
            if (albumList == null) {
                albumList = new ArrayList<>();
                for (int i = 0; i < list.size(); i++) {
                    Song song = list.get(i);
                    String albumName = song.getAlbumName();
                    String singer = song.getSinger();
                    Album album = new Album(albumName,singer);
                    if (!albumList.contains(album)) {
                        //如果专辑列表中没有存在
                        album.getSongList().add(song);
                        albumList.add(album);
                    }else {
                        //如果专辑列表中存在，将歌曲添加到专辑中
                        int index = albumList.indexOf(album);
                        albumList.get(index).getSongList().add(song);
                    }
                }
            }
            return albumList;
        }else {
            return null;
        }
    }

    /**
     * 获取排好序的专辑列表
     * @return
     */
    public static List<Album> getSortedAlbumList() {
        albumList = getAlbumList();
        List<Album> sortAlbumList = albumList;
        if (sortAlbumList != null) {
            for (int i = 0; i < sortAlbumList.size(); i++) {
                String s = sortAlbumList.get(i).getAlbumName().substring(0,1);
                if (s.matches("[\\u4E00-\\u9FA5]+")) {
                    //中文
                    s = CharacterUtils.getEachFirstSpell(sortAlbumList.get(i).getAlbumName()) + "&" + sortAlbumList.get(i).getAlbumName();
                }else if (s.matches("^[-\\+]?[\\d]*$")) {
                    //数字
                    s = "zzz" + "&" + sortAlbumList.get(i).getAlbumName();
                }else if (s.charAt(0) >= 'a' && s.charAt(0) <= 'z' || s.charAt(0) >= 'A' && s.charAt(0) <= 'Z'){
                    //字母
                    s = sortAlbumList.get(i).getAlbumName();
                }else {
                    //其他 【 《 等
                    s = "zzzzzzz" + "&" + sortAlbumList.get(i).getAlbumName();
                }
                sortAlbumList.get(i).setAlbumName(s);
            }
            //Comparator<Object> com = Collator.getInstance(java.util.Locale.CHINA);
            //albumList.sort(com);
            Collections.sort(sortAlbumList, new Comparator<Album>() {
                @Override
                public int compare(Album album, Album t1) {
                    return album.getAlbumName().compareToIgnoreCase(t1.getAlbumName());
                }
            });

            //排完序去掉拼音首字母和“&”
            for (int i = 0; i < sortAlbumList.size(); i++) {
                String s = sortAlbumList.get(i).getAlbumName();
                if (s.contains("&")){
                    s = s.split("&")[1];
                    sortAlbumList.get(i).setAlbumName(s);
                }
            }
        }
        return sortAlbumList;
    }

    public static List<Song> getFavoriteList() {
        if (favoriteList == null) {
            favoriteList = new ArrayList<>();
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).isFavorite()) {
                    favoriteList.add(list.get(i));
                }
            }
        }
        return favoriteList;
    }

    public static void initPlayMode(Context context) {
        //获取播放模式
        SharedPreferences preferences = context.getSharedPreferences("playMode", Context.MODE_PRIVATE);
        playMode = preferences.getInt("play_mode",MusicUtils.PLAY_MODE_IN_ORDER);
    }

    public static void setPlayMode(int mode) {
        playMode = mode;
    }
}
