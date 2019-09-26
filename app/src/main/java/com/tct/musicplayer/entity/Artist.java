package com.tct.musicplayer.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Artist {

    private String singer;//歌手
    private List<Song> songList;

    private String firstLetter;//歌手名首字母

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

    public void setSongList(List<Song> songList) {
        this.songList = songList;
    }

    public String getFirstLetter() {
        return firstLetter;
    }

    public void setFirstLetter(String firstLetter) {
        this.firstLetter = firstLetter;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Artist artist = (Artist) o;
        return Objects.equals(singer, artist.singer);
    }

    @Override
    public int hashCode() {
        return Objects.hash(singer);
    }
}
