package com.tct.musicplayer.entity;

import java.util.Objects;

public class LyricsRow implements Comparable<LyricsRow> {
    private long time;
    private String content;

    public LyricsRow() {
    }

    public LyricsRow(long time, String content) {
        this.time = time;
        this.content = content;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LyricsRow lyricsRow = (LyricsRow) o;
        return time == lyricsRow.time;
    }

    @Override
    public int hashCode() {
        return Objects.hash(time);
    }

    @Override
    public int compareTo(LyricsRow lyricsRow) {
        return (int)(this.time - lyricsRow.getTime());
    }
}
