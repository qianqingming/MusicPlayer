package com.tct.musicplayer.utils;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.widget.Toast;

import androidx.core.content.FileProvider;

import com.tct.musicplayer.MainActivity;
import com.tct.musicplayer.MusicPlayActivity;
import com.tct.musicplayer.entity.Song;

import java.io.File;

public class ShareUtils {


    public static void shareTo(Context context, String packageName, String className) {
        if (checkInstall(context,packageName)) {
            Intent share_intent = new Intent();
            share_intent.setAction(Intent.ACTION_SEND);//设置分享行为
            share_intent.setType("audio/*");//设置分享内容的类型
            share_intent.setComponent(new ComponentName(packageName, className));
            Song song = MainActivity.musicService.getMusicList().get(MainActivity.musicService.getMusicIndex());
            File file = new File(song.getPath());

            Uri uri = FileProvider.getUriForFile(context,"com.tct.musicplayer.fileProvider",file);
            share_intent.putExtra(Intent.EXTRA_STREAM,uri);

            //创建分享的Dialog
            share_intent = Intent.createChooser(share_intent, "分享到");
            context.startActivity(share_intent);
        }
    }

    /**
     * 使用系统的分享
     * @param context
     */
    public static void sharedBySys(Context context) {
        Intent share_intent = new Intent();
        share_intent.setAction(Intent.ACTION_SEND);//设置分享行为
        share_intent.setType("audio/*");//设置分享内容的类型
        Song song = MainActivity.musicService.getMusicList().get(MainActivity.musicService.getMusicIndex());
        File file = new File(song.getPath());

        Uri uri = FileProvider.getUriForFile(context,"com.tct.musicplayer.fileProvider",file);
        share_intent.putExtra(Intent.EXTRA_STREAM,uri);

        //创建分享的Dialog
        share_intent = Intent.createChooser(share_intent, "分享到");
        context.startActivity(share_intent);
    }

    /**
     * 是否安装分享app
     * @param packageName
     */
    public static boolean checkInstall(Context context, String packageName){
        try {
            context.getPackageManager().getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(context, "请先安装应用app", Toast.LENGTH_SHORT).show();
            return false;
        }
    }
}
