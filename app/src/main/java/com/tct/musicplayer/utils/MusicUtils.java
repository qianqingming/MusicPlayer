package com.tct.musicplayer.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import com.tct.musicplayer.R;
import com.tct.musicplayer.entity.Album;
import com.tct.musicplayer.entity.Artist;
import com.tct.musicplayer.entity.Song;

import org.litepal.LitePal;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MusicUtils {

    public static final int PLAY_MODE_IN_ORDER = 0;//顺序播放
    public static final int PLAY_MODE_SINGLE_CYCLE = 1;//单曲循环
    public static final int PLAY_MODE_RANDOM = 2;//随机播放

    /**
     * 播放模式
     */
    public static int playMode;

    /**
     * 歌曲列表
     */
    private static List<Song> list;

    /**
     * 收藏列表
     */
    private static List<Song> favoriteList;

    /**
     * 艺术家列表
     */
    private static List<Artist> artistList;

    /**
     * 专辑列表
     */
    private static List<Album> albumList;


    /**
     * 加载歌曲列表
     */
    public static List<Song> loadMusicList(Context context) {
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
                song.setSongId(""+id);
                song.setAlbumId(albumId);
                song.setAlbumName(albumName);
                //去掉歌曲名字后缀.mp3
                if (name.contains(".")) {
                    int lastIndex = name.lastIndexOf(".");
                    name = name.substring(0,lastIndex);
                    song.setName(name);
                }
                //设置专辑图片
                //song.setAlbumBmp(getAlbumArtBmp(context,albumId));
                //设置专辑路径
                song.setAlbumPath(getAlbumArtPath(context,albumId));
                if (size > 1000 * 800) {
                    if (name.contains("-")) {
                        //把歌曲名字和歌手切割开
                        String[] str = name.split("-");
                        //song.setSinger(str[0].trim());
                        song.setName(str[1].trim());
                    }
                    list.add(song);
                    song.save();
                }
            }
            cursor.close();
        }
        return list;
    }

    /**
     * 获取歌曲列表
     */
    public static List<Song> getMusicList() {
        return list;
    }

    public static void setMusicList(List<Song> songList) {
        list = songList;
    }


    /**
     * 转换歌曲时间的格式
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


    private static String getAlbumArtPath(Context context, long album_id) {
        String mUriAlbums = "content://media/external/audio/albums";
        String[] projection = new String[]{"album_art"};
        Cursor cur = context.getContentResolver().query(Uri.parse(mUriAlbums + "/" + album_id), projection, null, null, null);
        String album_art = null;
        if (cur.getCount() > 0 && cur.getColumnCount() > 0) {
            cur.moveToNext();
            album_art = cur.getString(0);
        }
        cur.close();
        return album_art;
    }

    /**
     * 获取专辑封面
     */
    private static Bitmap getAlbumArtBmp(Context context, String album_art) {
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
            //在中文字符前加上---中文的每个字的拼音和“&”
            String singer;
            String s;
            Artist artist;
            String pinyin;
            for (int i = 0; i < sortArtistList.size(); i++) {
                artist = sortArtistList.get(i);
                singer = artist.getSinger();
                s = singer.substring(0,1);
                if (s.matches("[\\u4E00-\\u9FA5]+")) {
                    //中文
                    pinyin = CharacterUtils.getPingYin(singer);
                    artist.setFirstLetter(pinyin.substring(0,1).toUpperCase());
                    s = pinyin + "&" + singer;
                }else if (s.matches("[a-zA-Z]")){
                    //字母
                    artist.setFirstLetter(s.toUpperCase());
                    s = singer;
                }else {
                    //其他 < 等
                    artist.setFirstLetter("#");
                    s = "zzzzzzz" + "&" + singer;
                }
                artist.setSinger(s);
            }
            Collections.sort(sortArtistList, new Comparator<Artist>() {
                @Override
                public int compare(Artist artist, Artist t1) {
                    return artist.getSinger().compareToIgnoreCase(t1.getSinger());
                }
            });
            //排完序去掉拼音首字母和“&”
            for (int i = 0; i < artistList.size(); i++) {
                s = artistList.get(i).getSinger();
                //Log.d("qianqingming","first---"+artistList.get(i).getFirstLetter());
                if (s.contains("&")){
                    s = s.split("&")[1];
                    artistList.get(i).setSinger(s);
                }
            }
        }
        return sortArtistList;
    }


    /**
     * 获取专辑列表
     * @return
     */
    public static List<Album> getAlbumList() {
        if (list != null) {
            if (albumList == null) {
                albumList = new ArrayList<>();
                Song song;
                String albumName;
                String singer;
                Album album;
                for (int i = 0; i < list.size(); i++) {
                    song = list.get(i);
                    albumName = song.getAlbumName();
                    singer = song.getSinger();
                    album = new Album(albumName,singer);
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
            String albumName;
            String s;
            Album album;
            for (int i = 0; i < sortAlbumList.size(); i++) {
                album = sortAlbumList.get(i);
                albumName = album.getAlbumName();
                s = albumName.substring(0,1);
                String pinyin;
                if (s.matches("[\\u4E00-\\u9FA5]+")) {
                    //中文
                    pinyin = CharacterUtils.getPingYin(albumName);
                    album.setFirstLetter(pinyin.substring(0,1).toUpperCase());
                    s = pinyin + "&" + albumName;
                }else if (s.matches("[0-9]")) {
                    //数字
                    album.setFirstLetter("#");
                    s = "zzz" + "&" + albumName;
                }else if (s.matches("[a-zA-Z]")){
                    //字母
                    album.setFirstLetter(s.toUpperCase());
                    s = albumName;
                }else {
                    //其他 【 《 等
                    album.setFirstLetter("#");
                    s = "zzzzzzz" + "&" + albumName;
                }
                album.setAlbumName(s);
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
                //Log.d("qianqingming","first---"+sortAlbumList.get(i).getFirstLetter());
                s = sortAlbumList.get(i).getAlbumName();
                if (s.contains("&")){
                    s = s.split("&")[1];
                    sortAlbumList.get(i).setAlbumName(s);
                }
            }
        }
        return sortAlbumList;
    }

    /**
     * 获取收藏列表
     * @return
     */
    public static List<Song> getFavoriteList() {
        //if (favoriteList == null) {
            favoriteList = new ArrayList<>();
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).getFavorite() == 1) {
                    favoriteList.add(list.get(i));
                }
            }
        //}
        return favoriteList;
    }

    /**
     * 初始化播放模式
     * @param context
     */
    public static void initPlayMode(Context context) {
        //获取播放模式
        SharedPreferences preferences = context.getSharedPreferences("playMode", Context.MODE_PRIVATE);
        playMode = preferences.getInt("play_mode",MusicUtils.PLAY_MODE_IN_ORDER);
    }

    public static void setPlayMode(int mode) {
        playMode = mode;
    }

    /**
     * 是否第一次加载数据
     * @param context
     * @return
     */
    public static boolean isFirst(Context context) {
        SharedPreferences preferences = context.getSharedPreferences("first", Context.MODE_PRIVATE);
        int first = preferences.getInt("isfirst",0);
        if (first == 0) {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putInt("isfirst",1);
            editor.apply();
            return true;
        }
        return false;
    }

    public static List<String> getMusicDirList() {
        if (list != null) {
            List<String> dirList = new ArrayList<>();
            for (int i = 0; i < list.size(); i++) {
                //  /storage/emulated/0/netease/cloudmusic/Music/张杰 张碧晨 - 只要平凡.mp3
                String path = list.get(i).getPath();
                // /storage/emulated/0/netease/cloudmusic/Music
                path =  path.substring(0,path.lastIndexOf("/"));
                if (!dirList.contains(path)) {
                    dirList.add(path);
                }
            }
            return dirList;
        }
        return null;
    }
}
