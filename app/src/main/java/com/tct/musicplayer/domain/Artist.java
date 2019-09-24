package com.tct.musicplayer.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Artist {

    private String singer;//歌手
    private List<Song> songList;

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
