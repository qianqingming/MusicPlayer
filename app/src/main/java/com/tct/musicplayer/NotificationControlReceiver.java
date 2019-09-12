package com.tct.musicplayer;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;

import com.tct.musicplayer.service.MusicService;

public class NotificationControlReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        /*switch (action){
            case MusicService.ACTION_CLOSE:
                remoteInterface.remoteStopForeground();
                break;
            case MusicService.ACTION_LAST_MUSIC:
                remoteInterface.remotePlayLastMusic();
                break;
            case MusicService.ACTION_PLAY_MUSIC:
                Log.d("qianqingming","recevier:"+remoteInterface);
                //remoteInterface.remotePlayMusic();
                break;
            case MusicService.ACTION_PAUSE_MUSIC:
                remoteInterface.remotePauseMusic();
                break;
            case MusicService.ACTION_NEXT_MUSIC:
                remoteInterface.remotePlayNextMusic();
                break;
        }*/
    }

    /*
    private RemoteInterface remoteInterface;

    public interface RemoteInterface {
        void remoteStopForeground();
        void remotePlayMusic();
        void remotePauseMusic();
        void remotePlayLastMusic();
        void remotePlayNextMusic();
    }

    public void setRemoteInterfaceListener(RemoteInterface remoteInterface) {
        this.remoteInterface = remoteInterface;
    }*/

}
