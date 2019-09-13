package com.tct.musicplayer.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.tct.musicplayer.domain.Song;
import com.tct.musicplayer.utils.MusicUtils;
import com.tct.musicplayer.utils.NotificationUtils;

import java.io.IOException;
import java.util.List;

public class MusicService extends Service {

    private MediaPlayer mediaPlayer;
    private List<Song> musicList;
    private int musicIndex;

    private boolean isStartForeground = false;

    public MusicService() {
        mediaPlayer = new MediaPlayer();
        musicList = MusicUtils.getMusicList(this);
        musicIndex = 0;
        try {
            mediaPlayer.setDataSource(musicList.get(musicIndex).getPath());
            mediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new MusicBinder();
    }

    public void playMusic() {
        if (!isStartForeground) {
            startForeground();
        }
        if (!isPlaying()) {
            mediaPlayer.start();
        }
    }

    public void pauseMusic() {
        if (isPlaying()){
            mediaPlayer.pause();
        }
    }

    public void playNextMusic() {
        if (!isStartForeground) {
            startForeground();
        }
        if (mediaPlayer != null && musicIndex < musicList.size()) {
            mediaPlayer.stop();
            try {
                mediaPlayer.reset();
                if (musicIndex == musicList.size() - 1) {
                    //如果已经是最后一首，则跳转到第一首播放
                    musicIndex = 0;
                    mediaPlayer.setDataSource(musicList.get(musicIndex).getPath());
                }else {
                    mediaPlayer.setDataSource(musicList.get(++musicIndex).getPath());
                }
                mediaPlayer.prepare();
                mediaPlayer.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void playLastMusic() {
        if (!isStartForeground) {
            startForeground();
        }
        if (mediaPlayer != null && musicIndex > 0) {
            mediaPlayer.stop();
            try {
                mediaPlayer.reset();
                mediaPlayer.setDataSource(musicList.get(--musicIndex).getPath());
                mediaPlayer.prepare();
                mediaPlayer.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void stopMusic() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.reset();
        }
    }

    public void playSelectedMusic(int index) {
        if (!isStartForeground) {
            startForeground();
        }
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            try {
                mediaPlayer.reset();
                mediaPlayer.setDataSource(musicList.get(index).getPath());
                musicIndex = index;
                mediaPlayer.prepare();
                mediaPlayer.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public boolean isPlaying() {
        if (mediaPlayer == null){
            return false;
        }
        return mediaPlayer.isPlaying();
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }
        stopForeground(true);
    }

    public int getMusicIndex() {
        return musicIndex;
    }

    public void startForeground() {
        Notification notification = NotificationUtils.getNotification((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE),getPackageName(),this);
        startForeground(1,notification);
        isStartForeground = true;
    }

    public void stopForeground() {
        stopForeground(true);
        stopMusic();
        isStartForeground = false;
    }




    public class MusicBinder extends Binder {
        public MusicService getMusicService() {
            return MusicService.this;
        }
    }

}
