package com.tct.musicplayer.entity;

import android.graphics.Bitmap;

import java.io.Serializable;

public class Song implements Serializable {
    private String name;//歌曲名
    private String singer;//歌手
    private long size;//歌曲所占空间大小
    private int duration;//歌曲时间长度
    private String path;//歌曲地址
    private long  albumId;//图片id
    private long id;//歌曲id
    private String albumName;//专辑名称

    private boolean favorite = false;

    private Bitmap albumBmp;//专辑图片

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSinger() {
        return singer;
    }

    public void setSinger(String singer) {
        this.singer = singer;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public long getAlbumId() {
        return albumId;
    }

    public void setAlbumId(long albumId) {
        this.albumId = albumId;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Bitmap getAlbumBmp() {
        return albumBmp;
    }

    public void setAlbumBmp(Bitmap albumBmp) {
        this.albumBmp = albumBmp;
    }

    public String getAlbumName() {
        return albumName;
    }

    public void setAlbumName(String albumName) {
        this.albumName = albumName;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    @Override
    public String toString() {
        return "Song{" +
                "name='" + name + '\'' +
                ", singer='" + singer + '\'' +
                ", size=" + size +
                ", duration=" + duration +
                ", path='" + path + '\'' +
                ", albumId=" + albumId +
                ", id=" + id +
                ", albumName='" + albumName + '\'' +
                '}';
    }
}
