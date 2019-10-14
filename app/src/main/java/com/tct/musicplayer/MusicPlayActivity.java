package com.tct.musicplayer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.tct.musicplayer.entity.Song;
import com.tct.musicplayer.fragment.LyricsFragment;
import com.tct.musicplayer.fragment.MusicPlayFragment;
import com.tct.musicplayer.fragment.PlayListFragment;
import com.tct.musicplayer.service.MusicService;
import com.tct.musicplayer.utils.BroadcastUtils;
import com.tct.musicplayer.utils.MusicUtils;
import com.tct.musicplayer.utils.ShareUtils;
import com.tct.musicplayer.utils.ToastUtils;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MusicPlayActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "qianqingming";

    private View circleView1,circleView2,circleView3;
    private ViewPager viewPager;
    private List<Fragment> fragment_list;
    private PlayListFragment playListFragment;
    private MusicPlayFragment musicPlayFragment;
    private LyricsFragment lyricsFragment;

    private ImageView backImg,shareImg;
    private TextView musicName,musicSinger;
    private TextView currTime,totalTime;
    private ImageView playMusic,pauseMusic,lastMusic,nextMusic;
    private ImageView addFavorite,removeFavorite;
    private SeekBar seekBar;
    private ImageView playInOrder,playSingleCycle,playRandom;

    private MusicService musicService;

    private MusicStateReceiver musicStateReceiver;

    private boolean isClosed = false;
    private boolean isFirst = false;

    private Timer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_play);

        musicService = MainActivity.musicService;

        //通知栏透明
        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }*/

        initViews();

        initViewPager();

        if (musicService != null) {
            if (musicService.getMusicIndex() == -1) {
                //没有播放
                musicName.setText(R.string.bottom_music_default_text);
                musicSinger.setText("");
                currTime.setText(R.string.default_time);
                totalTime.setText(R.string.default_time);
            }else if (musicService.getMusicList() != null && musicService.getMusicIndex() >= 0) {
                if (!musicService.isPlaying()) {
                    playMusic.setVisibility(View.VISIBLE);
                    pauseMusic.setVisibility(View.GONE);
                }else {
                    playMusic.setVisibility(View.GONE);
                    pauseMusic.setVisibility(View.VISIBLE);
                }
                Song song = musicService.getMusicList().get(musicService.getMusicIndex());
                musicName.setText(song.getName());
                musicSinger.setText(song.getSinger());
                totalTime.setText(MusicUtils.formatTime(song.getDuration()));
                seekBar.setMax(song.getDuration());//设置进度条的最大值
                if (song.getFavorite() == 1) {
                    addFavorite.setVisibility(View.GONE);
                    removeFavorite.setVisibility(View.VISIBLE);
                }
            }
        }


        if (musicService != null) {
            if (!musicService.isPlaying()) {
                playMusic.setVisibility(View.VISIBLE);
                pauseMusic.setVisibility(View.GONE);
            }else {
                playMusic.setVisibility(View.GONE);
                pauseMusic.setVisibility(View.VISIBLE);
            }
            if (musicService.getMusicIndex() == -1) {
                currTime.setText(R.string.default_time);
                totalTime.setText(R.string.default_time);
                musicName.setText(R.string.bottom_music_default_text);
                musicSinger.setText("");
                isFirst = true;
            }else {
                Song song = musicService.getMusicList().get(musicService.getMusicIndex());
                //musicImg.setImageBitmap(song.getAlbumBmp());
                //musicPlayFragment.setMusicImgBitmap(song.getAlbumBmp());
                musicName.setText(song.getName());
                musicSinger.setText(song.getSinger());
                totalTime.setText(MusicUtils.formatTime(song.getDuration()));
                seekBar.setMax(song.getDuration());//设置进度条的最大值
                if (song.getFavorite() == 1) {
                    addFavorite.setVisibility(View.GONE);
                    removeFavorite.setVisibility(View.VISIBLE);
                }
                //高斯模糊
                //Bitmap bitmap = BlurUtil.doBlur(song.getAlbumBmp(),300,200);
                //layout.setBackground(new BitmapDrawable(getResources(),bitmap));
                isFirst = false;
            }

            timer = new Timer();
            TimerTask timerTask = new TimerTask() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (musicService.getIsSetDataSource()) {
                                //Log.d("qianqingming","time---"+musicService.getCurrPosition());
                                currTime.setText(MusicUtils.formatTime(musicService.getCurrPosition()));
                                seekBar.setProgress(musicService.getCurrPosition());//设置进度条位置
                            }
                        }
                    });
                }
            };
            timer.schedule(timerTask,0,1000);
        }

        //------------注册广播----------
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BroadcastUtils.ACTION_PLAY_MUSIC);
        intentFilter.addAction(BroadcastUtils.ACTION_PAUSE_MUSIC);
        intentFilter.addAction(BroadcastUtils.ACTION_LAST_MUSIC);
        intentFilter.addAction(BroadcastUtils.ACTION_NEXT_MUSIC);
        intentFilter.addAction(BroadcastUtils.ACTION_PLAY_SELECTED_MUSIC);
        intentFilter.addAction(BroadcastUtils.ACTION_CLOSE);
        intentFilter.addAction(BroadcastUtils.ACTION_PLAY_COMPLETED);
        intentFilter.setPriority(BroadcastUtils.Priority_3);
        musicStateReceiver = new MusicStateReceiver();
        registerReceiver(musicStateReceiver,intentFilter);
    }

    private void initViews() {
        //初始化小圆点选中状态
        circleView1 = findViewById(R.id.circle1);
        circleView1.setEnabled(false);
        circleView2 = findViewById(R.id.circle2);
        circleView2.setEnabled(true);
        circleView3 = findViewById(R.id.circle3);
        circleView3.setEnabled(false);

        viewPager = findViewById(R.id.view_pager);
        backImg = findViewById(R.id.back_image_view);
        shareImg = findViewById(R.id.share_image_view);
        musicName = findViewById(R.id.music_name);
        musicSinger = findViewById(R.id.music_singer);
        currTime = findViewById(R.id.music_curr_time);
        totalTime = findViewById(R.id.music_total_time);
        playMusic = findViewById(R.id.play_music);
        pauseMusic = findViewById(R.id.pause_music);
        lastMusic = findViewById(R.id.last_music);
        nextMusic = findViewById(R.id.next_music);
        seekBar = findViewById(R.id.seek_bar);
        addFavorite = findViewById(R.id.add_favorite);
        removeFavorite = findViewById(R.id.remove_favorite);
        playInOrder = findViewById(R.id.play_in_order);
        playSingleCycle = findViewById(R.id.play_single_cycle);
        playRandom = findViewById(R.id.play_random);

        backImg.setOnClickListener(this);
        shareImg.setOnClickListener(this);
        playMusic.setOnClickListener(this);
        pauseMusic.setOnClickListener(this);
        lastMusic.setOnClickListener(this);
        nextMusic.setOnClickListener(this);
        addFavorite.setOnClickListener(this);
        removeFavorite.setOnClickListener(this);
        playInOrder.setOnClickListener(this);
        playSingleCycle.setOnClickListener(this);
        playRandom.setOnClickListener(this);

        //初始化播放模式
        MusicUtils.initPlayMode(this);
        switch (MusicUtils.playMode) {
            case MusicUtils.PLAY_MODE_IN_ORDER:
                break;
            case MusicUtils.PLAY_MODE_SINGLE_CYCLE:
                playInOrder.setVisibility(View.GONE);
                playSingleCycle.setVisibility(View.VISIBLE);
                break;
            case MusicUtils.PLAY_MODE_RANDOM:
                playInOrder.setVisibility(View.GONE);
                playRandom.setVisibility(View.VISIBLE);
                break;
        }

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (b) {
                    //如果是用户拖动导致的进度改变
                    if (musicService.getIsSetDataSource()) {
                        //currTime.setText(MusicUtils.formatTime(musicService.getCurrPosition()));
                        musicService.seekToPosition(i);
                    }
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private void initViewPager() {
        fragment_list = new ArrayList<>();

        playListFragment = new PlayListFragment();
        musicPlayFragment = new MusicPlayFragment();
        lyricsFragment = new LyricsFragment();

        fragment_list.add(playListFragment);
        fragment_list.add(musicPlayFragment);
        fragment_list.add(lyricsFragment);

        viewPager.setOffscreenPageLimit(2);

        viewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager(),FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
            @NonNull
            @Override
            public Fragment getItem(int position) {
                return fragment_list.get(position);
            }

            @Override
            public int getCount() {
                return fragment_list.size();
            }
        });

        viewPager.setCurrentItem(1);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    circleView1.setEnabled(true);
                    circleView2.setEnabled(false);
                    circleView3.setEnabled(false);
                }else if (position == 1) {
                    circleView1.setEnabled(false);
                    circleView2.setEnabled(true);
                    circleView3.setEnabled(false);
                }else if (position == 2) {
                    circleView1.setEnabled(false);
                    circleView2.setEnabled(false);
                    circleView3.setEnabled(true);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }


    @Override
    public void onClick(View view) {
        Intent intent;
        SharedPreferences.Editor editor = getSharedPreferences("playMode",Context.MODE_PRIVATE).edit();
        switch (view.getId()){
            case R.id.back_image_view:
                finish();
                break;
            case R.id.share_image_view:
                share();
                break;
            case R.id.play_music:
                intent = new Intent(BroadcastUtils.ACTION_PLAY_MUSIC);
                sendOrderedBroadcast(intent,null);
                break;
            case R.id.pause_music:
                intent = new Intent(BroadcastUtils.ACTION_PAUSE_MUSIC);
                sendOrderedBroadcast(intent,null);
                break;
            case R.id.last_music:
                intent = new Intent(BroadcastUtils.ACTION_LAST_MUSIC);
                sendOrderedBroadcast(intent,null);
                break;
            case R.id.next_music:
                intent = new Intent(BroadcastUtils.ACTION_NEXT_MUSIC);
                sendOrderedBroadcast(intent,null);
                break;
            case R.id.add_favorite:
                if (musicService.getMusicIndex() != -1) {
                    addFavorite.setVisibility(View.GONE);
                    removeFavorite.setVisibility(View.VISIBLE);

                    Song song = musicService.getMusicList().get(musicService.getMusicIndex());
                    List<Song> favoriteList = MusicUtils.getFavoriteList();

                    song.setFavorite(1);

                    ContentValues contentValues = new ContentValues();
                    contentValues.put("favorite",1);
                    LitePal.update(Song.class,contentValues,song.getId());

                    favoriteList.add(song);

                    intent = new Intent(BroadcastUtils.ACTION_NOTIFY_DATA);
                    sendBroadcast(intent);

                    ToastUtils.showToast(this,this.getResources().getString(R.string.add_favorite_success));
                }
                break;
            case R.id.remove_favorite:
                removeFavorite.setVisibility(View.GONE);
                addFavorite.setVisibility(View.VISIBLE);

                Song song1 = musicService.getMusicList().get(musicService.getMusicIndex());
                List<Song> favoriteList1 = MusicUtils.getFavoriteList();
                song1.setFavorite(0);

                ContentValues contentValues1 = new ContentValues();
                contentValues1.put("favorite",0);
                LitePal.update(Song.class,contentValues1,song1.getId());

                if (MainActivity.musicService.getMusicIndex() == favoriteList1.size() - 1) {
                    MainActivity.musicService.setMusicIndex(favoriteList1.size() - 2 > 0 ? favoriteList1.size() - 2 : 0);
                }

                for (int i = 0; i < favoriteList1.size(); i++) {
                    if (favoriteList1.get(i).getSongId().equals(song1.getSongId())) {
                        favoriteList1.remove(i);
                        break;
                    }
                }

                intent = new Intent(BroadcastUtils.ACTION_NOTIFY_DATA);
                sendBroadcast(intent);

                ToastUtils.showToast(this,this.getResources().getString(R.string.remove_favorite_success));
                break;
            case R.id.play_in_order:
                playInOrder.setVisibility(View.GONE);
                playSingleCycle.setVisibility(View.VISIBLE);
                ToastUtils.showToast(MusicPlayActivity.this,getResources().getString(R.string.play_single_cycle));

                editor.putInt("play_mode",MusicUtils.PLAY_MODE_SINGLE_CYCLE);
                editor.apply();
                MusicUtils.setPlayMode(MusicUtils.PLAY_MODE_SINGLE_CYCLE);
                break;
            case R.id.play_single_cycle:
                playSingleCycle.setVisibility(View.GONE);
                playRandom.setVisibility(View.VISIBLE);
                ToastUtils.showToast(MusicPlayActivity.this,getResources().getString(R.string.play_random));

                editor.putInt("play_mode",MusicUtils.PLAY_MODE_RANDOM);
                editor.apply();
                MusicUtils.setPlayMode(MusicUtils.PLAY_MODE_RANDOM);
                break;
            case R.id.play_random:
                playRandom.setVisibility(View.GONE);
                playInOrder.setVisibility(View.VISIBLE);
                ToastUtils.showToast(MusicPlayActivity.this,getResources().getString(R.string.play_in_order));

                editor.putInt("play_mode",MusicUtils.PLAY_MODE_IN_ORDER);
                editor.apply();
                MusicUtils.setPlayMode(MusicUtils.PLAY_MODE_IN_ORDER);
                break;
            default:
                break;
        }
    }

    private void share() {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_share,null);
        final android.app.AlertDialog dialog = new AlertDialog.Builder(this).setView(view).create();
        //dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        Window window = dialog.getWindow();
        if (window != null){
            window.setGravity(Gravity.BOTTOM);
            window.setBackgroundDrawable(null);

            WindowManager windowManager = getWindowManager();
            Display display = windowManager.getDefaultDisplay();
            WindowManager.LayoutParams lp = window.getAttributes();
            lp.width = display.getWidth(); //设置宽度
            window.setAttributes(lp);
        }

        view.findViewById(R.id.weixin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                ShareUtils.shareTo(MusicPlayActivity.this,"com.tencent.mm","com.tencent.mm.ui.tools.ShareImgUI");
            }
        });
        view.findViewById(R.id.weixin_pengyouquan).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShareUtils.shareTo(MusicPlayActivity.this,"com.tencent.mm","com.tencent.mm.ui.tools.ShareToTimeLineUI");
            }
        });
        view.findViewById(R.id.qq).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShareUtils.shareTo(MusicPlayActivity.this,"com.tencent.mobileqq","com.tencent.mobileqq.activity.JumpActivity");
            }
        });
        view.findViewById(R.id.qq_zone).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShareUtils.shareTo(MusicPlayActivity.this,"com.tencent.mobileqq","com.tencent.mobileqq.activity.JumpActivity");
            }
        });
        view.findViewById(R.id.more).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                ShareUtils.sharedBySys(MusicPlayActivity.this);
            }
        });
    }


    private void playMusic() {
        if (musicService != null && musicService.getMusicList() != null && musicService.getMusicIndex() >= 0) {
            Song song = musicService.getMusicList().get(musicService.getMusicIndex());
            musicPlayFragment.setMusicImgBitmap(song.getAlbumPath());
            musicName.setText(song.getName());
            musicSinger.setText(song.getSinger());
            totalTime.setText(MusicUtils.formatTime(song.getDuration()));
            seekBar.setMax(song.getDuration());
            playMusic.setVisibility(View.GONE);
            pauseMusic.setVisibility(View.VISIBLE);
            musicPlayFragment.startNeedleImgPlayAnim();
            musicPlayFragment.resumeObjectAnimator();
            if (song.getFavorite() == 1) {
                addFavorite.setVisibility(View.GONE);
                removeFavorite.setVisibility(View.VISIBLE);
            }

            playListFragment.scrollToPosition(MainActivity.musicService.getMusicIndex());
            playListFragment.notifyData();
        }
    }

    private void pauseMusic() {
        musicPlayFragment.startNeedleImgPauseAnim();
        playMusic.setVisibility(View.VISIBLE);
        pauseMusic.setVisibility(View.GONE);
        musicPlayFragment.pauseObjectAnimator();
    }

    private void playLastMusic() {
        //musicPlayFragment.startNeedleImgPauseAnim();
        if (musicService.isPlaying()) {
            playMusic.setVisibility(View.GONE);
            pauseMusic.setVisibility(View.VISIBLE);
        }else {
            pauseMusic.setVisibility(View.GONE);
            playMusic.setVisibility(View.VISIBLE);
        }
        Song song = musicService.getMusicList().get(musicService.getMusicIndex());
        if (song.getFavorite() == 1) {
            addFavorite.setVisibility(View.GONE);
            removeFavorite.setVisibility(View.VISIBLE);
        }else {
            removeFavorite.setVisibility(View.GONE);
            addFavorite.setVisibility(View.VISIBLE);
        }
        musicPlayFragment.setMusicImgBitmap(song.getAlbumPath());
        musicName.setText(song.getName());
        musicSinger.setText(song.getSinger());
        totalTime.setText(MusicUtils.formatTime(song.getDuration()));
        seekBar.setMax(song.getDuration());
        musicPlayFragment.startObjectAnimator();
        musicPlayFragment.startNeedleImgPlayAnim();
        playListFragment.scrollToPosition(MainActivity.musicService.getMusicIndex());
        playListFragment.notifyData();
    }

    private void playCompleted() {
        Song song = musicService.getMusicList().get(musicService.getMusicIndex());
        if (song.getFavorite() == 1) {
            addFavorite.setVisibility(View.GONE);
            removeFavorite.setVisibility(View.VISIBLE);
        }else {
            removeFavorite.setVisibility(View.GONE);
            addFavorite.setVisibility(View.VISIBLE);
        }
        musicPlayFragment.setMusicImgBitmap(song.getAlbumPath());
        musicName.setText(song.getName());
        musicSinger.setText(song.getSinger());
        totalTime.setText(MusicUtils.formatTime(song.getDuration()));
        seekBar.setMax(song.getDuration());
        musicPlayFragment.startObjectAnimator();

        playListFragment.scrollToPosition(MainActivity.musicService.getMusicIndex());
        playListFragment.notifyData();
    }

    private void playSelectedMusic(int index) {
        if (musicService.isPlaying()) {
            playMusic.setVisibility(View.GONE);
            pauseMusic.setVisibility(View.VISIBLE);
        }else {
            pauseMusic.setVisibility(View.GONE);
            playMusic.setVisibility(View.VISIBLE);
        }
        Song song = musicService.getMusicList().get(musicService.getMusicIndex());
        if (song.getFavorite() == 1) {
            addFavorite.setVisibility(View.GONE);
            removeFavorite.setVisibility(View.VISIBLE);
        }else {
            removeFavorite.setVisibility(View.GONE);
            addFavorite.setVisibility(View.VISIBLE);
        }
        musicPlayFragment.setMusicImgBitmap(song.getAlbumPath());
        musicName.setText(song.getName());
        musicSinger.setText(song.getSinger());
        totalTime.setText(MusicUtils.formatTime(song.getDuration()));
        seekBar.setMax(song.getDuration());
        musicPlayFragment.startObjectAnimator();
        musicPlayFragment.startNeedleImgPlayAnim();
        playListFragment.scrollToPosition(index);
    }

    private void stopMusic() {
        if (musicService.isPlaying()){
            musicPlayFragment.startNeedleImgPauseAnim();
        }
        playMusic.setVisibility(View.VISIBLE);
        pauseMusic.setVisibility(View.GONE);
        musicPlayFragment.pauseObjectAnimator();
        musicPlayFragment.startNeedleImgPauseAnim();
        currTime.setText(R.string.default_time);
        totalTime.setText(R.string.default_time);
        musicPlayFragment.setDefaultMusicImg();
        musicName.setText(R.string.bottom_music_default_text);
        musicSinger.setText("");
        isClosed = true;
        seekBar.setProgress(0);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(musicStateReceiver);
        timer.cancel();
    }

    public class MusicStateReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.d("qianqingming","MusicPlayActivity:"+action);
            switch (action){
                case BroadcastUtils.ACTION_PLAY_MUSIC:
                    playMusic();
                    break;
                case BroadcastUtils.ACTION_PAUSE_MUSIC:
                    pauseMusic();
                    break;
                case BroadcastUtils.ACTION_LAST_MUSIC:
                case BroadcastUtils.ACTION_NEXT_MUSIC:
                    playLastMusic();
                    break;
                case BroadcastUtils.ACTION_PLAY_COMPLETED:
                    playCompleted();
                    break;
                case BroadcastUtils.ACTION_PLAY_SELECTED_MUSIC:
                    int index = intent.getIntExtra("position",0);
                    playSelectedMusic(index);
                    break;
                case BroadcastUtils.ACTION_CLOSE:
                    stopMusic();
                    break;
            }
        }
    }
}
