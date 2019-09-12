package com.tct.musicplayer.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationChannelGroup;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.tct.musicplayer.MainActivity;
import com.tct.musicplayer.NotificationControlReceiver;
import com.tct.musicplayer.R;
import com.tct.musicplayer.domain.Song;

import java.io.IOException;
import java.util.List;

public class MusicService extends Service {

    private MediaPlayer mediaPlayer;
    private List<Song> musicList;
    private int musicIndex;

    public static final String ACTION_CLOSE = "ACTION_CLOSE";
    public static final String ACTION_PLAY_MUSIC = "ACTION_PLAY_MUSIC";
    public static final String ACTION_PAUSE_MUSIC = "ACTION_PAUSE_MUSIC";
    public static final String ACTION_LAST_MUSIC = "ACTION_LAST_MUSIC";
    public static final String ACTION_NEXT_MUSIC = "ACTION_NEXT_MUSIC";

    private NotificationControlReceiver notificationControlReceiver;

    private RemoteViews remoteViews;
    private NotificationManager manager;
    private Notification notification;


    public MusicService() {
        mediaPlayer = new MediaPlayer();
        musicList = MainActivity.musicList;
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

        startForeground(1,createNotification());

        //注册广播
        IntentFilter notificationFilter = new IntentFilter();
        notificationFilter.addAction(ACTION_CLOSE);
        notificationFilter.addAction(ACTION_PLAY_MUSIC);
        notificationFilter.addAction(ACTION_PAUSE_MUSIC);
        notificationFilter.addAction(ACTION_LAST_MUSIC);
        notificationFilter.addAction(ACTION_NEXT_MUSIC);
        notificationControlReceiver = new NotificationControlReceiver();
        registerReceiver(notificationControlReceiver,notificationFilter);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new MusicBinder();
    }

    public void playMusic() {
        if (!isPlaying()) {
            mediaPlayer.start();
        }
    }

    public void pauseMusic() {
        if (isPlaying()){
            mediaPlayer.pause();
        }
    }

    public boolean isPlaying() {
        return mediaPlayer.isPlaying();
    }


    public void playNextMusic() {
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


    private Notification createNotification() {
        manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        String groupId = "musicPlayerGroup";
        String groupName = "musicPlayer";
        NotificationChannelGroup group = new NotificationChannelGroup(groupId,groupName);
        manager.createNotificationChannelGroup(group);

        String channelId = "musicPlayerChannelId";
        String channelName = "musicPlayerChannelName";
        NotificationChannel channel = new NotificationChannel(channelId,channelName,NotificationManager.IMPORTANCE_HIGH);
        channel.setGroup(groupId);
        manager.createNotificationChannel(channel);

        //PendingIntent pi = PendingIntent.getActivity(this,0,new Intent(this,MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);

        remoteViews = new RemoteViews(getPackageName(), R.layout.notification_remote_view);
        Song song = musicList.get(musicIndex);
        remoteViews.setImageViewBitmap(R.id.notification_music_img,song.getAlbumBmp());
        remoteViews.setTextViewText(R.id.notification_music_name,song.getName());
        remoteViews.setTextViewText(R.id.notification_music_singer,song.getSinger());
        //remoteViews.setOnClickPendingIntent(R.id.notification_music_img,pi);

        //为通知栏设置点击事件
        Intent closeIntent = new Intent(ACTION_CLOSE);
        PendingIntent closePi = PendingIntent.getBroadcast(this,0,closeIntent,PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.notification_close,closePi);

        Intent playIntent = new Intent(ACTION_PLAY_MUSIC);
        PendingIntent playPi = PendingIntent.getBroadcast(this,0,playIntent,PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.notification_play_music,playPi);

        Intent pauseIntent = new Intent(ACTION_PAUSE_MUSIC);
        PendingIntent pausePi = PendingIntent.getBroadcast(this,0,pauseIntent,PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.notification_pause_music,pausePi);

        Intent lastIntent = new Intent(ACTION_LAST_MUSIC);
        PendingIntent lastPi = PendingIntent.getBroadcast(this,0,lastIntent,PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.notification_last_music,lastPi);

        Intent nextIntent = new Intent(ACTION_NEXT_MUSIC);
        PendingIntent nextPi = PendingIntent.getBroadcast(this,0,nextIntent,PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.notification_next_music,nextPi);

        notification = new NotificationCompat.Builder(this.getApplicationContext(), channelId)
                .setSmallIcon(R.drawable.ic_launcher)
                .setCustomContentView(remoteViews)
                .build();
        return notification;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }
        stopForeground(true);
        unregisterReceiver(notificationControlReceiver);
    }

    public int getMusicIndex() {
        return musicIndex;
    }

    /**
     * 关闭前台服务
     */
    public void stopForeground(){
        stopForeground(true);
    }

    /**
     * 获取NotificationManager
     * @return
     */
    public NotificationManager getManager(){
        return manager;
    }

    /**
     * 获取Notification
     * @return
     */
    public Notification getNotification(){
        return notification;
    }

    /**
     * 获取前台服务的RemoteViews
     * @return
     */
    public RemoteViews getRemoteViews() {
        return remoteViews;
    }

    public NotificationControlReceiver getNotificationControlReceiver() {
        return notificationControlReceiver;
    }





    public class NotificationControlUtils {
        public void updateRemoteViews() {
            Song song = musicList.get(musicIndex);
            remoteViews.setImageViewBitmap(R.id.notification_music_img,song.getAlbumBmp());
            remoteViews.setTextViewText(R.id.notification_music_name,song.getName());
            remoteViews.setTextViewText(R.id.notification_music_singer,song.getSinger());
            if (isPlaying()) {
                remoteViews.setViewVisibility(R.id.notification_play_music, View.GONE);
                remoteViews.setViewVisibility(R.id.notification_pause_music, View.VISIBLE);
            }else {
                remoteViews.setViewVisibility(R.id.notification_pause_music, View.GONE);
                remoteViews.setViewVisibility(R.id.notification_play_music, View.VISIBLE);
            }
            manager.notify(1,notification);
        }
    }


    public class MusicBinder extends Binder {
        public MusicService getMusicService() {
            return MusicService.this;
        }
    }
}
