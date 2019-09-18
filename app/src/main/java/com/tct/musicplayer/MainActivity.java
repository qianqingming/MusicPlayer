package com.tct.musicplayer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.tct.musicplayer.adapter.MyFragmentPagerAdapter;
import com.tct.musicplayer.domain.Album;
import com.tct.musicplayer.domain.Song;
import com.tct.musicplayer.fragment.AlbumFragment;
import com.tct.musicplayer.fragment.ArtistFragment;
import com.tct.musicplayer.fragment.FavoriteFragment;
import com.tct.musicplayer.fragment.SongsFragment;
import com.tct.musicplayer.receiver.BaseReceiver;
import com.tct.musicplayer.service.MusicService;
import com.tct.musicplayer.utils.MusicUtils;
import com.tct.musicplayer.utils.NotificationUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private List<String> tab_title_list;
    private List<Fragment> fragment_list;

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ImageView searchImg,settingsImg,moreImg;//顶部搜索、设置、更多
    private ImageView lastMusicImg,playMusicImg,pauseMusicImg,nextMusicImg;//底部上一曲、播放、暂停、下一曲
    private ProgressBar progressBar;

    private ImageView bottomMusicBg;//底部图片
    private TextView bottomDefaultText,bottomMusicName,bottomMusicSinger;//底部默认文字、歌曲名字、歌手

    private RelativeLayout bottomLayout;

    public static List<Song> musicList;

    private MusicStateReceiver musicStateReceiver;
    private ObjectAnimator objectAnimator;
    private boolean hasPlayedMusic = false;

    private SongsFragment songsFragment;

    private MusicService.MusicBinder musicBinder;
    public static MusicService musicService;
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            musicBinder = (MusicService.MusicBinder) iBinder;
            musicService = musicBinder.getMusicService();

            Timer timer = new Timer();
            TimerTask timerTask = new TimerTask() {
                @Override
                public void run() {
                    if (musicService.isPlaying()) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                progressBar.setMax(musicService.getDuration());
                                progressBar.setProgress(musicService.getCurrPosition());
                            }
                        });
                    }
                }
            };
            timer.schedule(timerTask,0,1000);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);

        tabLayout = findViewById(R.id.tabs);
        viewPager = findViewById(R.id.view_pager);

        settingsImg = findViewById(R.id.menu_image_view);
        searchImg = findViewById(R.id.search_image_view);
        moreImg = findViewById(R.id.more_image_view);
        lastMusicImg = findViewById(R.id.last_music_image_view);
        playMusicImg = findViewById(R.id.play_music_image_view);
        pauseMusicImg = findViewById(R.id.pause_music_image_view);
        nextMusicImg = findViewById(R.id.next_music_image_view);
        progressBar = findViewById(R.id.progress_bar_music);
        bottomLayout = findViewById(R.id.bottom_layout);

        bottomMusicBg = findViewById(R.id.music_bg_image_view);
        bottomDefaultText = findViewById(R.id.default_bottom_music_text);
        bottomMusicName = findViewById(R.id.bottom_music_name);
        bottomMusicSinger = findViewById(R.id.bottom_music_singer);

        settingsImg.setOnClickListener(this);
        searchImg.setOnClickListener(this);
        moreImg.setOnClickListener(this);
        lastMusicImg.setOnClickListener(this);
        playMusicImg.setOnClickListener(this);
        pauseMusicImg.setOnClickListener(this);
        nextMusicImg.setOnClickListener(this);
        bottomLayout.setOnClickListener(this);

        musicList = MusicUtils.getMusicList(this);

//        MyTask myTask = new MyTask();
//        myTask.execute();

        //绑定TabLayout与ViewPager
        init();


        bindService(new Intent(this,MusicService.class),serviceConnection,BIND_AUTO_CREATE);

        //注册广播
        IntentFilter notificationFilter = new IntentFilter();
        notificationFilter.addAction(NotificationUtils.ACTION_CLOSE);
        notificationFilter.addAction(NotificationUtils.ACTION_PLAY_MUSIC);
        notificationFilter.addAction(NotificationUtils.ACTION_PAUSE_MUSIC);
        notificationFilter.addAction(NotificationUtils.ACTION_LAST_MUSIC);
        notificationFilter.addAction(NotificationUtils.ACTION_NEXT_MUSIC);
        notificationFilter.addAction(NotificationUtils.ACTION_PLAY_SELECTED_MUSIC);
        musicStateReceiver = new MusicStateReceiver();
        registerReceiver(musicStateReceiver,notificationFilter);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.scan_music:
                        Toast.makeText(MainActivity.this,"扫描",Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.clock_stop_music:
                        Toast.makeText(MainActivity.this,"定时",Toast.LENGTH_SHORT).show();
                        break;
                }
                //drawerLayout.closeDrawers();
                return true;
            }
        });

        objectAnimator = ObjectAnimator.ofFloat(bottomMusicBg,"rotation",0f,360f);
        objectAnimator.setInterpolator(new LinearInterpolator());
        objectAnimator.setDuration(20000);
        objectAnimator.setRepeatCount(ValueAnimator.INFINITE);
        objectAnimator.setRepeatMode(ValueAnimator.RESTART);
        //objectAnimator.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(musicStateReceiver);
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(navigationView)) {
            drawerLayout.closeDrawers();
        }else {
            super.onBackPressed();
        }
    }

    /**
     * 初始化TabLayout、ViewPager，并绑定TabLayout与ViewPager
     */
    private void init() {
        tab_title_list = new ArrayList<>();
        tab_title_list.add("收藏");
        tab_title_list.add("歌曲");
        tab_title_list.add("艺术家");
        tab_title_list.add("专辑");

        songsFragment = new SongsFragment();

        fragment_list = new ArrayList<>();
        fragment_list.add(new FavoriteFragment());
        fragment_list.add(songsFragment);
        fragment_list.add(new ArtistFragment());
        fragment_list.add(new AlbumFragment());

        tabLayout.addTab(tabLayout.newTab().setText(tab_title_list.get(0)));
        tabLayout.addTab(tabLayout.newTab().setText(tab_title_list.get(1)));
        tabLayout.addTab(tabLayout.newTab().setText(tab_title_list.get(2)));
        tabLayout.addTab(tabLayout.newTab().setText(tab_title_list.get(3)));

        //预加载
        viewPager.setOffscreenPageLimit(2);

        //给ViewPager添加适配器
        viewPager.setAdapter(new MyFragmentPagerAdapter(getSupportFragmentManager(),tab_title_list,fragment_list));

        //将TabLayout与ViewPager绑定
        tabLayout.setupWithViewPager(viewPager);

        //设置默认选中项
        //tabLayout.getTabAt(1).select();
        viewPager.setCurrentItem(1);

        //监听切换
        tabChangeListener();
    }

    /**
     * TabLayout切换监听
     */
    private void tabChangeListener() {
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getText().equals(tab_title_list.get(2)) || tab.getText().equals(tab_title_list.get(3))){
                    moreImg.setVisibility(View.GONE);
                }else {
                    moreImg.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.menu_image_view:
                drawerLayout.openDrawer(GravityCompat.START);
                break;
            case R.id.search_image_view:
                break;
            case R.id.more_image_view:
                initPopMenu();
                break;
            case R.id.bottom_layout:
                Intent intent = new Intent(this, MusicPlayActivity.class);
                startActivity(intent);
                break;
            case R.id.last_music_image_view:
                playLastMusic();
                break;
            case R.id.play_music_image_view:
                playMusic();
                break;
            case R.id.pause_music_image_view:
                pauseMusic();
                break;
            case R.id.next_music_image_view:
                playNextMusic();
                break;
            default:
                break;
        }
    }


    public void playLastMusic() {
        musicService.playLastMusic();
        changeMusicImageAndText();
        songsFragment.setIsClicked(musicService.getMusicIndex());
        songsFragment.scrollToPosition(musicService.getMusicIndex());
        objectAnimator.start();
        hasPlayedMusic = true;
    }

    public void playNextMusic() {
        musicService.playNextMusic();
        changeMusicImageAndText();
        songsFragment.setIsClicked(musicService.getMusicIndex());
        songsFragment.scrollToPosition(musicService.getMusicIndex());
        objectAnimator.start();
        hasPlayedMusic = true;
    }

    public void playMusic() {
        musicService.playMusic();
        changeMusicImageAndText();
        songsFragment.setIsClicked(musicService.getMusicIndex());
        if (hasPlayedMusic){
            objectAnimator.resume();
        }else {
            objectAnimator.start();
            hasPlayedMusic = true;
        }
    }

    public void pauseMusic() {
        musicService.pauseMusic();
        changeMusicImageAndText();
        songsFragment.setIsClicked(musicService.getMusicIndex());
        objectAnimator.pause();
    }

    public void playSelectedMusic(int index) {
        musicService.playSelectedMusic(index);
        changeMusicImageAndText();
        objectAnimator.start();
        hasPlayedMusic = true;
    }

    private void changeMusicImageAndText() {
        Song song = musicList.get(musicService.getMusicIndex());
        bottomMusicBg.setImageBitmap(song.getAlbumBmp());
        bottomDefaultText.setVisibility(View.GONE);
        bottomMusicName.setVisibility(View.VISIBLE);
        bottomMusicSinger.setVisibility(View.VISIBLE);
        bottomMusicName.setText(song.getName());
        bottomMusicSinger.setText(song.getSinger());
        NotificationUtils.updateRemoteViews(musicList,musicService.getMusicIndex(),musicService.isPlaying());
        if (musicService.isPlaying()) {
            playMusicImg.setVisibility(View.GONE);
            pauseMusicImg.setVisibility(View.VISIBLE);
        }else {
            pauseMusicImg.setVisibility(View.GONE);
            playMusicImg.setVisibility(View.VISIBLE);
        }
    }

    private void initPopMenu() {
        PopupMenu popupMenu = new PopupMenu(MainActivity.this,moreImg);
        popupMenu.getMenuInflater().inflate(R.menu.pop_menu,popupMenu.getMenu());
        popupMenu.show();
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                Toast.makeText(MainActivity.this,menuItem.getTitle(),Toast.LENGTH_SHORT).show();
                switch (menuItem.getItemId()){
                    case R.id.select:
                        Log.d("qianqingming","select");
                        break;
                    case R.id.sort_by_date:
                        Log.d("qianqingming","sort_by_date");
                        break;
                    case R.id.sort_by_name:
                        Log.d("qianqingming","sort_by_name");
                        break;
                }
                return false;
            }
        });
    }


    public class MusicStateReceiver extends BaseReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            super.onReceive(context, intent);
            String action = intent.getAction();
            Log.d("qianqingming","MainActivity-----:"+action);
            switch (action){
                case NotificationUtils.ACTION_CLOSE:
                    musicService.stopForeground();
                    bottomMusicBg.setImageResource(R.drawable.ic_default_music);
                    bottomDefaultText.setVisibility(View.VISIBLE);
                    bottomMusicName.setVisibility(View.GONE);
                    bottomMusicSinger.setVisibility(View.GONE);
                    pauseMusicImg.setVisibility(View.GONE);
                    playMusicImg.setVisibility(View.VISIBLE);
                    objectAnimator.pause();
                    hasPlayedMusic = false;
                    break;
                case NotificationUtils.ACTION_LAST_MUSIC:
                    playLastMusic();
                    break;
                case NotificationUtils.ACTION_NEXT_MUSIC:
                    playNextMusic();
                    break;
                case NotificationUtils.ACTION_PLAY_MUSIC:
                    playMusic();
                    break;
                case NotificationUtils.ACTION_PAUSE_MUSIC:
                    pauseMusic();
                    break;
                case NotificationUtils.ACTION_PLAY_SELECTED_MUSIC:
                    int index = intent.getIntExtra("position",0);
                    playSelectedMusic(index);
                    break;
            }
        }
    }

    class MyTask extends AsyncTask<Void,Void,Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            musicList = MusicUtils.getMusicList(MainActivity.this);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            //songsFragment.notifyData();
        }
    }
}
