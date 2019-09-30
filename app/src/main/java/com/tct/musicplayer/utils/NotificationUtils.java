package com.tct.musicplayer.utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationChannelGroup;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.view.View;
import android.widget.RemoteViews;

import androidx.core.app.NotificationCompat;

import com.bumptech.glide.Glide;
import com.tct.musicplayer.MainActivity;
import com.tct.musicplayer.R;
import com.tct.musicplayer.entity.Song;

import java.io.File;
import java.util.List;

public class NotificationUtils {

    private static RemoteViews remoteViews;
    private static NotificationManager manager;
    private static Notification notification;


    public static Notification getNotification(NotificationManager notificationManager,String packageName,Context context) {
        manager = notificationManager;
        String groupId = "musicPlayerGroup";
        String groupName = "musicPlayer";
        NotificationChannelGroup group = new NotificationChannelGroup(groupId,groupName);
        manager.createNotificationChannelGroup(group);

        String channelId = "musicPlayerChannelId";
        String channelName = "musicPlayerChannelName";
        NotificationChannel channel = new NotificationChannel(channelId,channelName,NotificationManager.IMPORTANCE_HIGH);
        channel.setGroup(groupId);
        manager.createNotificationChannel(channel);

        NotificationUtils.generateRemoteViews(packageName,context);

        notification = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.drawable.ic_default_music)
                .setCustomContentView(remoteViews)
                .build();
        return notification;
    }

    private static void generateRemoteViews(String packageName,Context context) {
        remoteViews = new RemoteViews(packageName, R.layout.notification_remote_view);

        //为通知栏设置点击事件
        Intent activityIntent = new Intent(context, MainActivity.class);
        PendingIntent activityPi = PendingIntent.getActivity(context,0,activityIntent,PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.notification_layout,activityPi);

        Intent closeIntent = new Intent(BroadcastUtils.ACTION_CLOSE);
        PendingIntent closePi = PendingIntent.getBroadcast(context,1,closeIntent,PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.notification_close,closePi);

        Intent playIntent = new Intent(BroadcastUtils.ACTION_PLAY_MUSIC);
        PendingIntent playPi = PendingIntent.getBroadcast(context,2,playIntent,PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.notification_play_music,playPi);

        Intent pauseIntent = new Intent(BroadcastUtils.ACTION_PAUSE_MUSIC);
        PendingIntent pausePi = PendingIntent.getBroadcast(context,3,pauseIntent,PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.notification_pause_music,pausePi);

        Intent lastIntent = new Intent(BroadcastUtils.ACTION_LAST_MUSIC);
        PendingIntent lastPi = PendingIntent.getBroadcast(context,4,lastIntent,PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.notification_last_music,lastPi);

        Intent nextIntent = new Intent(BroadcastUtils.ACTION_NEXT_MUSIC);
        PendingIntent nextPi = PendingIntent.getBroadcast(context,5,nextIntent,PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.notification_next_music,nextPi);
    }

    public static void updateRemoteViews(List<Song> musicList,int musicIndex,boolean isPlaying) {
        Song song = musicList.get(musicIndex);
        if (song.getAlbumPath() != null) {
            File file = new File(song.getAlbumPath());
            if (file.exists()) {
                remoteViews.setImageViewBitmap(R.id.notification_music_img, BitmapFactory.decodeFile(song.getAlbumPath()));
            }else{
                remoteViews.setImageViewResource(R.id.notification_music_img, R.drawable.ic_default_music);
            }
        }else {
            remoteViews.setImageViewResource(R.id.notification_music_img, R.drawable.ic_default_music);
        }
        //remoteViews.setImageViewBitmap(R.id.notification_music_img,song.getAlbumBmp());
        remoteViews.setTextViewText(R.id.notification_music_name,song.getName());
        remoteViews.setTextViewText(R.id.notification_music_singer,song.getSinger());
        if (isPlaying) {
            remoteViews.setViewVisibility(R.id.notification_play_music, View.GONE);
            remoteViews.setViewVisibility(R.id.notification_pause_music, View.VISIBLE);
        }else {
            remoteViews.setViewVisibility(R.id.notification_pause_music, View.GONE);
            remoteViews.setViewVisibility(R.id.notification_play_music, View.VISIBLE);
        }
        manager.notify(1,notification);
    }

}
