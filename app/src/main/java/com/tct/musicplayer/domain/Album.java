package com.tct.musicplayer.domain;


import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Album {
    private String albumName;//专辑名称
    private String singer;//歌手

    private List<Song> songList;

    public Album() {
    }

    public Album(String albumName, String singer) {
        this.albumName = albumName;
        this.singer = singer;
    }

    public String getAlbumName() {
        return albumName;
    }

    public void setAlbumName(String albumName) {
        this.albumName = albumName;
    }

    public String getSinger() {
        return singer;
    }

    public void setSinger(String singer) {
        this.singer = singer;
    }

    public List<Song> getSongList() {
        if (songList == null) {
            songList = new ArrayList<>();
        }
        return songList;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Album album = (Album) o;
        return Objects.equals(albumName, album.albumName) &&
                Objects.equals(singer, album.singer);
    }

    @Override
    public int hashCode() {
        return Objects.hash(albumName, singer);
    }
}
