package com.tct.musicplayer.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.tct.musicplayer.entity.Song;
import com.tct.musicplayer.utils.BroadcastUtils;
import com.tct.musicplayer.utils.MusicUtils;
import com.tct.musicplayer.utils.NotificationUtils;

import java.io.IOException;
import java.util.List;
import java.util.Random;

public class MusicService extends Service {

    private MediaPlayer mediaPlayer;
    private List<Song> musicList;
    private int musicIndex = -1;

    private boolean isStartForeground = false;
    private boolean isSetDataSource = false;

    private MusicStateReceiver musicStateReceiver;

    private MusicBinder musicBinder;

    public MusicService() {

    }

    @Override
    public void onCreate() {
        super.onCreate();
        mediaPlayer = new MediaPlayer();
        musicList = MusicUtils.getMusicList();

        //注册广播
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BroadcastUtils.ACTION_PLAY_MUSIC);
        intentFilter.addAction(BroadcastUtils.ACTION_PAUSE_MUSIC);
        intentFilter.addAction(BroadcastUtils.ACTION_LAST_MUSIC);
        intentFilter.addAction(BroadcastUtils.ACTION_NEXT_MUSIC);
        intentFilter.addAction(BroadcastUtils.ACTION_PLAY_SELECTED_MUSIC);
        intentFilter.addAction(BroadcastUtils.ACTION_CLOSE);
        intentFilter.setPriority(BroadcastUtils.Priority_1);
        musicStateReceiver = new MusicStateReceiver();
        registerReceiver(musicStateReceiver,intentFilter);

        //音乐播放完的监听事件
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                if (isSetDataSource){
                    Intent intent = new Intent(BroadcastUtils.ACTION_PLAY_COMPLETED);
                    sendBroadcast(intent);
                    switch (MusicUtils.playMode) {
                        case MusicUtils.PLAY_MODE_SINGLE_CYCLE:
                            mediaPlayer.start();
                            break;
                        case MusicUtils.PLAY_MODE_IN_ORDER:
                            mediaPlayer.stop();
                            mediaPlayer.reset();
                            if (musicIndex == musicList.size() - 1) {
                                //如果已经是最后一首，则跳转到第一首播放
                                musicIndex = 0;
                            }else {
                                musicIndex += 1;
                            }
                            try {
                                mediaPlayer.setDataSource(musicList.get(musicIndex).getPath());
                                mediaPlayer.prepare();
                                mediaPlayer.start();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    NotificationUtils.updateRemoteViews(MusicService.this,musicList.get(musicIndex),isPlaying());
                                }
                            }).start();
                            break;
                        case MusicUtils.PLAY_MODE_RANDOM:
                            mediaPlayer.stop();
                            mediaPlayer.reset();
                            Random random = new Random();
                            musicIndex = random.nextInt(musicList.size());
                            Log.d("qianqingming","random:"+musicIndex);
                            try {
                                mediaPlayer.setDataSource(musicList.get(musicIndex).getPath());
                                mediaPlayer.prepare();
                                mediaPlayer.start();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    NotificationUtils.updateRemoteViews(MusicService.this,musicList.get(musicIndex),isPlaying());
                                }
                            }).start();
                            break;
                    }
                    isSetDataSource = true;
                }
            }
        });

        //初始化播放模式
        MusicUtils.initPlayMode(this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        musicBinder = new MusicBinder();
        return musicBinder;
    }

    public void playMusic() {
        if (!isStartForeground) {
            startForeground();
        }
        if (musicIndex == -1) {
            musicIndex = 0;
        }
        if (!isSetDataSource && musicList != null) {
            try {
                mediaPlayer.setDataSource(musicList.get(musicIndex).getPath());
                mediaPlayer.prepare();
            } catch (IOException e) {
                e.printStackTrace();
            }
            isSetDataSource = true;
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

    public void playLastMusic() {
        if (!isStartForeground) {
            startForeground();
        }
        if (musicIndex == -1) {
            musicIndex = 0;
        }
        if (mediaPlayer != null && musicIndex > 0) {
            //mediaPlayer.stop();
            try {
                mediaPlayer.reset();
                mediaPlayer.setDataSource(musicList.get(--musicIndex).getPath());
                mediaPlayer.prepare();
                mediaPlayer.start();
                isSetDataSource = true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void playNextMusic() {
        if (!isStartForeground) {
            startForeground();
        }
        if (mediaPlayer != null && musicIndex < musicList.size()) {
            //mediaPlayer.stop();
            try {
                mediaPlayer.reset();
                if (MusicUtils.playMode == MusicUtils.PLAY_MODE_RANDOM) {
                    Random random = new Random();
                    musicIndex = random.nextInt(musicList.size());
                }else {
                    if (musicIndex == musicList.size() - 1) {
                        //如果已经是最后一首，则跳转到第一首播放
                        musicIndex = 0;
                    }else {
                        musicIndex += 1;
                    }
                }
                mediaPlayer.setDataSource(musicList.get(musicIndex).getPath());
                mediaPlayer.prepare();
                mediaPlayer.start();
                isSetDataSource = true;
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

    public void seekToPosition(int position) {
        if (mediaPlayer != null) {
            mediaPlayer.seekTo(position);
        }
    }

    public void playSelectedMusic(int index) {
        if (index == -1) {
            return;
        }
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
                isSetDataSource = true;
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

    public int getDuration() {
        if (mediaPlayer != null && isSetDataSource) {
            return mediaPlayer.getDuration();
        }
        return 0;
    }

    public int getCurrPosition() {
        if (mediaPlayer != null && isSetDataSource) {
            return mediaPlayer.getCurrentPosition();
        }
        return 0;
    }

    public boolean getIsSetDataSource() {
        return isSetDataSource;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }
        stopForeground(true);
        unregisterReceiver(musicStateReceiver);
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
        isSetDataSource = false;
    }

    public void setMusicIndex(int musicIndex) {
        if (musicIndex == -1) {
            return;
        }
        this.musicIndex = musicIndex;
    }

    public void setMusicList(List<Song> list) {
        if (list != null) {
            musicList = list;
        }
    }

    public List<Song> getMusicList() {
        return musicList;
    }

    public class MusicBinder extends Binder {
        public MusicService getMusicService() {
            return MusicService.this;
        }
    }

    class MusicStateReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(final Context context, Intent intent) {
            String action = intent.getAction();
            Log.d("qianqingming", "MusicService-----:" + action);
            switch (action) {
                case BroadcastUtils.ACTION_PLAY_MUSIC:
                    playMusic();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            NotificationUtils.updateRemoteViews(context,musicList.get(musicIndex),isPlaying());
                        }
                    }).start();
                    //NotificationUtils.updateRemoteViews(context,musicList.get(musicIndex),isPlaying());
                    break;
                case BroadcastUtils.ACTION_PAUSE_MUSIC:
                    pauseMusic();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            NotificationUtils.updateRemoteViews(context,musicList.get(musicIndex),isPlaying());
                        }
                    }).start();
                    //NotificationUtils.updateRemoteViews(context,musicList.get(musicIndex),isPlaying());
                    break;
                case BroadcastUtils.ACTION_LAST_MUSIC:
                    playLastMusic();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            NotificationUtils.updateRemoteViews(context,musicList.get(musicIndex),isPlaying());
                        }
                    }).start();
                    //NotificationUtils.updateRemoteViews(context,musicList.get(musicIndex),isPlaying());
                    break;
                case BroadcastUtils.ACTION_NEXT_MUSIC:
                    playNextMusic();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            NotificationUtils.updateRemoteViews(context,musicList.get(musicIndex),isPlaying());
                        }
                    }).start();
                    //NotificationUtils.updateRemoteViews(context,musicList.get(musicIndex),isPlaying());
                    break;
                case BroadcastUtils.ACTION_PLAY_SELECTED_MUSIC:
                    int index = intent.getIntExtra("position",0);
                    playSelectedMusic(index);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            NotificationUtils.updateRemoteViews(context,musicList.get(musicIndex),isPlaying());
                        }
                    }).start();
                    //NotificationUtils.updateRemoteViews(context,musicList.get(musicIndex),isPlaying());
                    break;
                case BroadcastUtils.ACTION_CLOSE:
                    stopForeground();
                    break;
            }
        }
    }
}
