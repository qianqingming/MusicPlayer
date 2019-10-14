package com.tct.musicplayer;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.Toast;

import com.tct.musicplayer.entity.Song;
import com.tct.musicplayer.utils.BroadcastUtils;
import com.tct.musicplayer.utils.MusicUtils;
import com.tct.musicplayer.utils.ToastUtils;

import java.util.List;

public class ScanActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int SKIP_60_SEC = 1;
    private static final int NOT_SKIP_60_SEC = 0;
    private static final int SKIP_500K = 1;
    private static final int NOT_SKIP_500K = 0;


    private ImageView backImg;
    private RelativeLayout scanDir;
    private Button scanBtn;

    private Switch switch60,switch500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        //修改状态栏字体颜色
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        backImg = findViewById(R.id.back);
        scanDir = findViewById(R.id.layout_scan_dir);
        scanBtn = findViewById(R.id.btn_scan);
        switch60 = findViewById(R.id.switch_60);
        switch500 = findViewById(R.id.switch_500);

        backImg.setOnClickListener(this);
        scanDir.setOnClickListener(this);
        scanBtn.setOnClickListener(this);


        SharedPreferences preferences = getSharedPreferences("skip", Context.MODE_PRIVATE);
        int skip60 = preferences.getInt("skip60",-1);
        int skip500 = preferences.getInt("skip500",-1);
        if (skip60 == NOT_SKIP_60_SEC) {
            switch60.setChecked(false);
        }
        if (skip500 == NOT_SKIP_500K) {
            switch500.setChecked(false);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.layout_scan_dir:
                Intent intent = new Intent(this,ScanDirActivity.class);
                startActivity(intent);
                break;
            case R.id.btn_scan:

                View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_loading,null);
                final android.app.AlertDialog dialog = new AlertDialog.Builder(this).setView(dialogView).create();
                dialog.setCanceledOnTouchOutside(false);
                dialog.show();
                Window window = dialog.getWindow();
                if (window != null){
                    window.setGravity(Gravity.CENTER);
                    window.setBackgroundDrawable(null);

                    WindowManager windowManager = getWindowManager();
                    Display display = windowManager.getDefaultDisplay();
                    WindowManager.LayoutParams lp = window.getAttributes();
                    lp.width = (int) getResources().getDimension(R.dimen.dp_200); //设置宽度
                    window.setAttributes(lp);
                }

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        SharedPreferences.Editor editor = getSharedPreferences("skip", Context.MODE_PRIVATE).edit();
                        if (switch60.isChecked()) {
                            editor.putInt("skip60",SKIP_60_SEC);
                        }else {
                            editor.putInt("skip60",NOT_SKIP_60_SEC);
                        }
                        if (switch500.isChecked()) {
                            editor.putInt("skip500",SKIP_500K);
                        }else {
                            editor.putInt("skip500",NOT_SKIP_500K);
                        }
                        editor.apply();


                        List<Song> musicList = MusicUtils.getMusicList();
                        List<Song> songs = MusicUtils.scanMusicList(ScanActivity.this, switch60.isChecked(), switch500.isChecked());
                        for (int i = 0; i < songs.size(); i++) {
                            if (!musicList.contains(songs.get(i))) {
                                Song song = songs.get(i);
                                musicList.add(song);
                                song.save();
                            }
                        }

                        MusicUtils.loadFavoriteList();
                        MusicUtils.loadArtistList();
                        MusicUtils.loadAlbumList();

                        Intent intent1 = new Intent(BroadcastUtils.ACTION_NOTIFY_DATA);
                        sendBroadcast(intent1);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(ScanActivity.this,getResources().getString(R.string.scan_success),Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                                finish();
                            }
                        });
                    }
                }).start();


                break;
        }
    }
}
