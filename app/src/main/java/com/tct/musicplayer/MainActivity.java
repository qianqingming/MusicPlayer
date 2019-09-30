package com.tct.musicplayer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.tct.musicplayer.adapter.MyFragmentPagerAdapter;
import com.tct.musicplayer.entity.Song;
import com.tct.musicplayer.fragment.AlbumFragment;
import com.tct.musicplayer.fragment.ArtistFragment;
import com.tct.musicplayer.fragment.FavoriteFragment;
import com.tct.musicplayer.fragment.SongsFragment;
import com.tct.musicplayer.receiver.BaseReceiver;
import com.tct.musicplayer.service.MusicService;
import com.tct.musicplayer.utils.GlideUtils;
import com.tct.musicplayer.utils.MusicUtils;
import com.tct.musicplayer.utils.NotificationUtils;

import org.litepal.LitePal;
import org.litepal.crud.callback.FindMultiCallback;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "qianqingming";

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

    public static List<Song> musicList;

    private MusicStateReceiver musicStateReceiver;
    private ObjectAnimator objectAnimator;
    private boolean hasPlayedMusic = false;

    private SongsFragment songsFragment;
    private FavoriteFragment favoriteFragment;
    private ArtistFragment artistFragment;
    private AlbumFragment albumFragment;


    public static MusicService musicService;
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            MusicService.MusicBinder musicBinder = (MusicService.MusicBinder) iBinder;
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

        initViews();

        //musicList = MusicUtils.getMusicList(this);
        //musicList = MusicUtils.getTenMuscList(this);

        //MyTask myTask = new MyTask();
        //myTask.execute();

        boolean first = MusicUtils.isFirst(this);
        if (first) {
            //MusicUtils.loadMusicList(this);
            MyTask myTask = new MyTask();
            myTask.execute();
        }else {
            //musicList = LitePal.findAll(Song.class);
            //MusicUtils.setMusicList(musicList);

            LitePal.findAllAsync(Song.class).listen(new FindMultiCallback<Song>() {
                @Override
                public void onFinish(List<Song> list) {
                    MusicUtils.setMusicList(list);
                    musicList = list;
                    Log.d(TAG,"size:"+list.size());
                    notifyData();
                }
            });
        }

        //绑定TabLayout与ViewPager
        init();

        bindService(new Intent(this,MusicService.class),serviceConnection,BIND_AUTO_CREATE);

        registerBroadcast();

        initNavigationView();

        initAnimation();
    }


    private void initViews() {
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

        bottomMusicBg = findViewById(R.id.music_bg_image_view);
        bottomDefaultText = findViewById(R.id.default_bottom_music_text);
        bottomMusicName = findViewById(R.id.bottom_music_name);
        bottomMusicSinger = findViewById(R.id.bottom_music_singer);


        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);

        settingsImg.setOnClickListener(this);
        searchImg.setOnClickListener(this);
        moreImg.setOnClickListener(this);
        lastMusicImg.setOnClickListener(this);
        playMusicImg.setOnClickListener(this);
        pauseMusicImg.setOnClickListener(this);
        nextMusicImg.setOnClickListener(this);
        bottomMusicBg.setOnClickListener(this);
        bottomDefaultText.setOnClickListener(this);
        bottomMusicName.setOnClickListener(this);
        bottomMusicSinger.setOnClickListener(this);
    }

    private void registerBroadcast() {
        //注册广播
        IntentFilter notificationFilter = new IntentFilter();
        notificationFilter.addAction(NotificationUtils.ACTION_CLOSE);
        notificationFilter.addAction(NotificationUtils.ACTION_PLAY_MUSIC);
        notificationFilter.addAction(NotificationUtils.ACTION_PAUSE_MUSIC);
        notificationFilter.addAction(NotificationUtils.ACTION_LAST_MUSIC);
        notificationFilter.addAction(NotificationUtils.ACTION_NEXT_MUSIC);
        notificationFilter.addAction(NotificationUtils.ACTION_PLAY_SELECTED_MUSIC);
        notificationFilter.addAction("ACTION_NOTIFY_DATA");
        notificationFilter.addAction("ACTION_PLAY_COMPLETED");
        musicStateReceiver = new MusicStateReceiver();
        registerReceiver(musicStateReceiver,notificationFilter);
    }

    private void initAnimation() {
        objectAnimator = ObjectAnimator.ofFloat(bottomMusicBg,"rotation",0f,360f);
        objectAnimator.setInterpolator(new LinearInterpolator());
        objectAnimator.setDuration(20000);
        objectAnimator.setRepeatCount(ValueAnimator.INFINITE);
        objectAnimator.setRepeatMode(ValueAnimator.RESTART);
        //objectAnimator.start();
    }

    private void initNavigationView() {
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.scan_music:
                        Toast.makeText(MainActivity.this,"扫描",Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.clock_stop_music:
                        drawerLayout.closeDrawers();
                        initTimerDialog();
                        break;
                    case R.id.change_background:
                        Toast.makeText(MainActivity.this,"换肤",Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.exit:
                        //Toast.makeText(MainActivity.this,"退出",Toast.LENGTH_SHORT).show();
                        finish();
                        break;
                }
                //drawerLayout.closeDrawers();
                return true;
            }
        });
    }

    private void initTimerDialog() {
        View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.dialog_timer,null);
        final android.app.AlertDialog dialog = new AlertDialog.Builder(MainActivity.this).setView(view).create();
        //dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        Window window = dialog.getWindow();
        if (window != null){
            window.setGravity(Gravity.BOTTOM);
            window.setBackgroundDrawable(null);
        }

        final String[] timerList = new String[] {
                "不开启", "10分钟后", "20分钟后",
                "30分钟后", "45分钟后", "60分钟后",
                "自定义"
        };
        final ListView listView = view.findViewById(R.id.timer_single_choice);
        listView.setAdapter(new ArrayAdapter<String>(this,R.layout.item_single_choice,timerList));
        listView.setItemsCanFocus(false);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(MainActivity.this,"pos:"+i,Toast.LENGTH_SHORT).show();
                //如果选择自定义
                if (i == timerList.length - 1) {
                    View customer = LayoutInflater.from(MainActivity.this).inflate(R.layout.dialog_timer_customer,null);
                    final android.app.AlertDialog customerDialog = new AlertDialog.Builder(MainActivity.this).setView(customer).create();
                    customerDialog.setCanceledOnTouchOutside(false);
                    customerDialog.show();
                    Window window = customerDialog.getWindow();
                    if (window != null){
                        window.setGravity(Gravity.BOTTOM);
                        window.setBackgroundDrawable(null);
                    }
                    //获取数字选择器
                    final NumberPicker hourPicker = customer.findViewById(R.id.customer_hour);
                    final NumberPicker minPicker = customer.findViewById(R.id.customer_min);
                    //设置不可编辑
                    hourPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
                    minPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
                    //设置最大值
                    hourPicker.setMaxValue(12);
                    minPicker.setMaxValue(59);

                    customer.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            customerDialog.dismiss();
                        }
                    });
                    customer.findViewById(R.id.sure).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Log.d("qianqingming","hour:"+hourPicker.getValue());
                            Log.d("qianqingming","min:"+minPicker.getValue());
                        }
                    });
                }
            }
        });
        //获取switch
        final Switch switchUntilEnd = view.findViewById(R.id.switch_until_play_end);

        view.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        view.findViewById(R.id.sure).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(MainActivity.this,"pos:"+listView.getCheckedItemPosition(),Toast.LENGTH_SHORT).show();
                if (switchUntilEnd.isChecked()) {

                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(musicStateReceiver);
        unbindService(serviceConnection);
    }

    @Override
    public void onBackPressed() {
        /*if (drawerLayout.isDrawerOpen(navigationView)) {
            drawerLayout.closeDrawers();
        }else {
            super.onBackPressed();
        }*/
        if (drawerLayout.isDrawerOpen(navigationView)) {
            drawerLayout.closeDrawers();
        }else {
            moveTaskToBack(true);
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

        favoriteFragment = new FavoriteFragment();
        songsFragment = new SongsFragment();
        artistFragment = new ArtistFragment();
        albumFragment = new AlbumFragment();

        fragment_list = new ArrayList<>();
        fragment_list.add(favoriteFragment);
        fragment_list.add(songsFragment);
        fragment_list.add(artistFragment);
        fragment_list.add(albumFragment);

        tabLayout.addTab(tabLayout.newTab().setText(tab_title_list.get(0)));
        tabLayout.addTab(tabLayout.newTab().setText(tab_title_list.get(1)));
        tabLayout.addTab(tabLayout.newTab().setText(tab_title_list.get(2)));
        tabLayout.addTab(tabLayout.newTab().setText(tab_title_list.get(3)));

        //预加载
        viewPager.setOffscreenPageLimit(3);

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
            case R.id.music_bg_image_view:
            case R.id.default_bottom_music_text:
            case R.id.bottom_music_name:
            case R.id.bottom_music_singer:
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
        songsFragment.setSelectedPos(musicService.getMusicIndex());
        songsFragment.scrollToPosition(musicService.getMusicIndex());
        favoriteFragment.notifyDataSetChanged();
        objectAnimator.start();
        hasPlayedMusic = true;
    }

    public void playNextMusic() {
        musicService.playNextMusic();
        changeMusicImageAndText();
        songsFragment.setSelectedPos(musicService.getMusicIndex());
        songsFragment.scrollToPosition(musicService.getMusicIndex());
        favoriteFragment.notifyDataSetChanged();
        objectAnimator.start();
        hasPlayedMusic = true;
    }

    public void playMusic() {
        musicService.playMusic();
        changeMusicImageAndText();
        songsFragment.setSelectedPos(musicService.getMusicIndex());
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
        //songsFragment.setSelectedPos(musicService.getMusicIndex());
        objectAnimator.pause();
    }

    public void playSelectedMusic(int index) {
        musicService.playSelectedMusic(index);
        changeMusicImageAndText();
        objectAnimator.start();
        favoriteFragment.notifyDataSetChanged();
        songsFragment.scrollToPosition(index);
        hasPlayedMusic = true;
    }

    public void playCompleted() {
        changeMusicImageAndText();
        songsFragment.setSelectedPos(musicService.getMusicIndex());
        songsFragment.scrollToPosition(musicService.getMusicIndex());
        objectAnimator.start();
        hasPlayedMusic = true;
    }

    private void changeMusicImageAndText() {
        Song song = musicList.get(musicService.getMusicIndex());
        //bottomMusicBg.setImageBitmap(song.getAlbumBmp());
        GlideUtils.setImg(this,song.getAlbumPath(),bottomMusicBg);
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
                case "ACTION_NOTIFY_DATA":
                    notifyData();
                    break;
                case "ACTION_PLAY_COMPLETED":
                    playCompleted();
                    break;
            }
        }
    }

    private void notifyData() {
        if (songsFragment != null){
            songsFragment.notifyData();
        }
        if (artistFragment != null) {
            artistFragment.notifyData();
        }
        if (albumFragment != null) {
            albumFragment.notifyData();
        }
        if (favoriteFragment != null) {
            favoriteFragment.notifyData();
        }
    }

    class MyTask extends AsyncTask<Void,Void,Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            //musicList = MusicUtils.getMusicList(MainActivity.this);
            musicList = MusicUtils.loadMusicList(MainActivity.this);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            notifyData();
        }
    }
}
