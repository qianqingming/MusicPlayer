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

import com.tct.musicplayer.MainActivity;
import com.tct.musicplayer.R;
import com.tct.musicplayer.entity.Song;

import java.io.File;
import java.util.List;

public class NotificationUtils {

    private static RemoteViews bigRemoteViews;
    private static RemoteViews normalRemoteViews;
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

        NotificationUtils.generateBigRemoteViews(packageName,context);
        NotificationUtils.generateNormalRemoteViews(packageName,context);

        notification = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.drawable.ic_default_music)
                .setCustomContentView(normalRemoteViews)
                .setCustomBigContentView(bigRemoteViews)
                .build();
        return notification;
    }

    /**
     * 大视图
     * @param packageName
     * @param context
     */
    private static void generateBigRemoteViews(String packageName, Context context) {
        bigRemoteViews = new RemoteViews(packageName, R.layout.notification_remote_view_big);

        //为通知栏设置点击事件
        Intent activityIntent = new Intent(context, MainActivity.class);
        PendingIntent activityPi = PendingIntent.getActivity(context,0,activityIntent,PendingIntent.FLAG_UPDATE_CURRENT);
        bigRemoteViews.setOnClickPendingIntent(R.id.notification_layout,activityPi);

        Intent closeIntent = new Intent(BroadcastUtils.ACTION_CLOSE);
        PendingIntent closePi = PendingIntent.getBroadcast(context,1,closeIntent,PendingIntent.FLAG_UPDATE_CURRENT);
        bigRemoteViews.setOnClickPendingIntent(R.id.notification_close,closePi);

        Intent playIntent = new Intent(BroadcastUtils.ACTION_PLAY_MUSIC);
        PendingIntent playPi = PendingIntent.getBroadcast(context,2,playIntent,PendingIntent.FLAG_UPDATE_CURRENT);
        bigRemoteViews.setOnClickPendingIntent(R.id.notification_play_music,playPi);

        Intent pauseIntent = new Intent(BroadcastUtils.ACTION_PAUSE_MUSIC);
        PendingIntent pausePi = PendingIntent.getBroadcast(context,3,pauseIntent,PendingIntent.FLAG_UPDATE_CURRENT);
        bigRemoteViews.setOnClickPendingIntent(R.id.notification_pause_music,pausePi);

        Intent lastIntent = new Intent(BroadcastUtils.ACTION_LAST_MUSIC);
        PendingIntent lastPi = PendingIntent.getBroadcast(context,4,lastIntent,PendingIntent.FLAG_UPDATE_CURRENT);
        bigRemoteViews.setOnClickPendingIntent(R.id.notification_last_music,lastPi);

        Intent nextIntent = new Intent(BroadcastUtils.ACTION_NEXT_MUSIC);
        PendingIntent nextPi = PendingIntent.getBroadcast(context,5,nextIntent,PendingIntent.FLAG_UPDATE_CURRENT);
        bigRemoteViews.setOnClickPendingIntent(R.id.notification_next_music,nextPi);
    }

    /**
     * 普通视图
     * @param packageName
     * @param context
     */
    private static void generateNormalRemoteViews(String packageName,Context context) {
        normalRemoteViews = new RemoteViews(packageName, R.layout.notification_remote_view_normal);

        //为通知栏设置点击事件
        Intent activityIntent = new Intent(context, MainActivity.class);
        PendingIntent activityPi = PendingIntent.getActivity(context,0,activityIntent,PendingIntent.FLAG_UPDATE_CURRENT);
        normalRemoteViews.setOnClickPendingIntent(R.id.notification_layout,activityPi);

        Intent closeIntent = new Intent(BroadcastUtils.ACTION_CLOSE);
        PendingIntent closePi = PendingIntent.getBroadcast(context,1,closeIntent,PendingIntent.FLAG_UPDATE_CURRENT);
        normalRemoteViews.setOnClickPendingIntent(R.id.notification_close,closePi);

        Intent playIntent = new Intent(BroadcastUtils.ACTION_PLAY_MUSIC);
        PendingIntent playPi = PendingIntent.getBroadcast(context,2,playIntent,PendingIntent.FLAG_UPDATE_CURRENT);
        normalRemoteViews.setOnClickPendingIntent(R.id.notification_play_music,playPi);

        Intent pauseIntent = new Intent(BroadcastUtils.ACTION_PAUSE_MUSIC);
        PendingIntent pausePi = PendingIntent.getBroadcast(context,3,pauseIntent,PendingIntent.FLAG_UPDATE_CURRENT);
        normalRemoteViews.setOnClickPendingIntent(R.id.notification_pause_music,pausePi);

        Intent lastIntent = new Intent(BroadcastUtils.ACTION_LAST_MUSIC);
        PendingIntent lastPi = PendingIntent.getBroadcast(context,4,lastIntent,PendingIntent.FLAG_UPDATE_CURRENT);
        normalRemoteViews.setOnClickPendingIntent(R.id.notification_last_music,lastPi);

        Intent nextIntent = new Intent(BroadcastUtils.ACTION_NEXT_MUSIC);
        PendingIntent nextPi = PendingIntent.getBroadcast(context,5,nextIntent,PendingIntent.FLAG_UPDATE_CURRENT);
        normalRemoteViews.setOnClickPendingIntent(R.id.notification_next_music,nextPi);
    }

    public static void updateRemoteViews(List<Song> musicList,int musicIndex,boolean isPlaying) {
        Song song = musicList.get(musicIndex);
        if (song.getAlbumPath() != null) {
            File file = new File(song.getAlbumPath());
            if (file.exists()) {
                bigRemoteViews.setImageViewBitmap(R.id.notification_music_img, BitmapFactory.decodeFile(song.getAlbumPath()));
                normalRemoteViews.setImageViewBitmap(R.id.notification_music_img, BitmapFactory.decodeFile(song.getAlbumPath()));
            }else{
                bigRemoteViews.setImageViewResource(R.id.notification_music_img, R.drawable.ic_default_music);
                normalRemoteViews.setImageViewResource(R.id.notification_music_img, R.drawable.ic_default_music);
            }
        }else {
            bigRemoteViews.setImageViewResource(R.id.notification_music_img, R.drawable.ic_default_music);
            normalRemoteViews.setImageViewResource(R.id.notification_music_img, R.drawable.ic_default_music);
        }
        //bigRemoteViews.setImageViewBitmap(R.id.notification_music_img,song.getAlbumBmp());
        bigRemoteViews.setTextViewText(R.id.notification_music_name,song.getName());
        normalRemoteViews.setTextViewText(R.id.notification_music_name,song.getName());
        bigRemoteViews.setTextViewText(R.id.notification_music_singer,song.getSinger());
        normalRemoteViews.setTextViewText(R.id.notification_music_singer,song.getSinger());
        if (isPlaying) {
            bigRemoteViews.setViewVisibility(R.id.notification_play_music, View.GONE);
            normalRemoteViews.setViewVisibility(R.id.notification_play_music, View.GONE);
            bigRemoteViews.setViewVisibility(R.id.notification_pause_music, View.VISIBLE);
            normalRemoteViews.setViewVisibility(R.id.notification_pause_music, View.VISIBLE);
        }else {
            bigRemoteViews.setViewVisibility(R.id.notification_pause_music, View.GONE);
            normalRemoteViews.setViewVisibility(R.id.notification_pause_music, View.GONE);
            bigRemoteViews.setViewVisibility(R.id.notification_play_music, View.VISIBLE);
            normalRemoteViews.setViewVisibility(R.id.notification_play_music, View.VISIBLE);
        }
        manager.notify(1,notification);
    }

}
