package com.tct.musicplayer.utils;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.util.Log;

import com.tct.musicplayer.R;
import com.tct.musicplayer.entity.Album;
import com.tct.musicplayer.entity.Artist;
import com.tct.musicplayer.entity.Song;

import org.litepal.LitePal;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
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
    private static List<Song> favoriteList;
    private static List<Artist> artistList;
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

    public static List<Song> scanMusicList(Context context,boolean skip60,boolean skip500) {
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
                if (name.contains("-")) {
                    //把歌曲名字和歌手切割开
                    String[] str = name.split("-");
                    //song.setSinger(str[0].trim());
                    song.setName(str[1].trim());
                }
                song.setAlbumPath(getAlbumArtPath(context,albumId));
                if (skip60 && skip500) {
                    if (duration > 60 * 1000 && size > 500 * 1024) {
                        list.add(song);
                    }
                }
                if (skip60 && !skip500) {
                    if (duration > 60 * 1000) {
                        list.add(song);
                    }
                }
                if (!skip60 && skip500) {
                    if (size > 500 * 1024) {
                        list.add(song);
                    }
                }
                if (!skip60 && !skip500) {
                    list.add(song);
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


    public static List<Artist> loadArtistList() {
        if (list != null && list.size() > 0) {
            artistList = new ArrayList<>();
            String singer;
            String s;
            String pinyin;
            for (int i = 0; i < list.size(); i++) {
                Song song = list.get(i);
                Artist artist = new Artist();
                //排序
                singer = song.getSinger();
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
                if (!artistList.contains(artist)) {
                    artist.getSongList().add(song);
                    artistList.add(artist);
                }else {
                    int index = artistList.indexOf(artist);
                    artistList.get(index).getSongList().add(song);
                }
            }
            Collections.sort(artistList, new Comparator<Artist>() {
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
        return artistList;
    }
    public static List<Artist> getArtistList() {
        return artistList;
    }
    public static void setArtistList(List<Artist> newList) {
        artistList = newList;
    }

    public static List<Album> loadAlbumList() {
        if (list != null && list.size() > 0) {
            albumList = new ArrayList<>();
            Song song;
            String albumName;
            String singer;
            Album album;
            String s;
            for (int i = 0; i < list.size(); i++) {
                song = list.get(i);
                albumName = song.getAlbumName();
                singer = song.getSinger();
                album = new Album();
                album.setSinger(singer);
                //排序-------------------------------
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
                //-------------------------------------
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
            Collections.sort(albumList, new Comparator<Album>() {
                @Override
                public int compare(Album album, Album t1) {
                    return album.getAlbumName().compareToIgnoreCase(t1.getAlbumName());
                }
            });

            //排完序去掉拼音首字母和“&”
            for (int i = 0; i < albumList.size(); i++) {
                //Log.d("qianqingming","first---"+sortAlbumList.get(i).getFirstLetter());
                s = albumList.get(i).getAlbumName();
                if (s.contains("&")){
                    s = s.split("&")[1];
                    albumList.get(i).setAlbumName(s);
                }
            }
        }
        return albumList;
    }
    public static List<Album> getAlbumList() {
        return albumList;
    }
    public static void setAlbumList(List<Album> newList) {
        albumList = newList;
    }



    public static List<Song> loadFavoriteList() {
        if (favoriteList == null && list.size() > 0) {
            favoriteList = new ArrayList<>();
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).getFavorite() == 1) {
                    favoriteList.add(list.get(i));
                }
            }
        }
        return favoriteList;
    }
    public static List<Song> getFavoriteList() {
        return favoriteList;
    }
    public static void setFavoriteList(List<Song> newList) {
        favoriteList = newList;
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







    //----------------------

    private static final Uri sArtworkUri = Uri
            .parse("content://media/external/audio/albumart");

    private static final BitmapFactory.Options sBitmapOptions = new BitmapFactory.Options();


    public static Bitmap getArtwork(Context context, long song_id, long album_id) {
        return getArtwork(context, song_id, album_id, true);
    }

    public static Bitmap getArtwork(Context context, long song_id,
                                    long album_id, boolean allowdefault) {
        sBitmapOptions.inPreferredConfig = Bitmap.Config.RGB_565;
        sBitmapOptions.inDither = false;

        if (context == null) {
            return null;
        }

        if (album_id < 0) {
            // This is something that is not in the database, so get the album
            // art directly from the file.
            if (song_id >= 0) {
                Bitmap bm = getArtworkFromFile(context, song_id, -1);
                if (bm != null) {
                    return bm;
                }
            }
            if (allowdefault) {
                return getDefaultArtwork(context);
            }
            return null;
        }

        ContentResolver res = context.getContentResolver();
        Uri uri = ContentUris.withAppendedId(sArtworkUri, album_id);
        if (uri != null) {
            InputStream in = null;
            try {
                if (!isUriExisted(context, uri)) {
                    throw new FileNotFoundException();
                }
                in = res.openInputStream(uri);
                return BitmapFactory.decodeStream(in, null, sBitmapOptions);
            } catch (FileNotFoundException ex) {
                // The album art thumbnail does not actually exist. Maybe the
                // user deleted it, or
                // maybe it never existed to begin with.
                Bitmap bm = getArtworkFromFile(context, song_id, album_id);
                if (bm != null) {
                    if (bm.getConfig() == null) {
                        bm = bm.copy(Bitmap.Config.RGB_565, false);
                        if (bm == null && allowdefault) {
                            return getDefaultArtwork(context);
                        }
                    }
                } else if (allowdefault) {
                    bm = getDefaultArtwork(context);
                }
                return bm;
            } catch (OutOfMemoryError ex) {
                return null;
            } finally {
                try {
                    if (in != null) {
                        in.close();
                    }
                } catch (IOException ex) {
                }
            }
        }

        return null;
    }
    private static boolean isUriExisted(Context context, Uri uri) {
        if (uri != null) {
            Cursor result = context.getContentResolver().query(uri, null, null, null, null);
            if (result != null) {
                if (result.getCount() == 0) {
                    result.close();
                    return false;
                }
                return true;
            }
        }
        return false;
    }

    // get album art for specified file
    private static final String sExternalMediaUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI.toString();
    private static Bitmap getArtworkFromFile(Context context, long songid, long albumid) {
        Bitmap bm = null;
        byte [] art = null;
        String path = null;

        if (albumid < 0 && songid < 0) {
            throw new IllegalArgumentException("Must specify an album or a song id");
        }
        ParcelFileDescriptor pfd = null;
        try {
            if (albumid < 0) {
                Uri uri = Uri.parse("content://media/external/audio/media/" + songid + "/albumart");
                if (isUriExisted(context, uri)) {
                    throw new FileNotFoundException();
                }
                pfd = context.getContentResolver().openFileDescriptor(uri, "r");
                if (pfd != null) {
                    FileDescriptor fd = pfd.getFileDescriptor();
                    bm = BitmapFactory.decodeFileDescriptor(fd);
                }
            } else {
                Uri uri = ContentUris.withAppendedId(sArtworkUri, albumid);
                if (isUriExisted(context, uri)) {
                    throw new FileNotFoundException();
                }
                pfd = context.getContentResolver().openFileDescriptor(uri, "r");
                if (pfd != null) {
                    FileDescriptor fd = pfd.getFileDescriptor();
                    bm = BitmapFactory.decodeFileDescriptor(fd);
                }
            }
        } catch (IllegalStateException ex) {
        } catch (FileNotFoundException ex) {
        } finally {
            try {
                if (pfd != null) {
                    pfd.close();
                }
            } catch (IOException e) {
            }
        }
        return bm;
    }

    @SuppressLint("ResourceType")
    public static Bitmap getDefaultArtwork(Context context) {
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inPreferredConfig = Bitmap.Config.ARGB_8888;
        return BitmapFactory.decodeStream(
                context.getResources().openRawResource(R.drawable.ic_default_music),
                null, opts);
    }

}
