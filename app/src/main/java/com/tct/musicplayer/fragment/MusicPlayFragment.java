package com.tct.musicplayer.fragment;


import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.tct.musicplayer.MainActivity;
import com.tct.musicplayer.R;
import com.tct.musicplayer.utils.GlideUtils;

import java.io.File;

/**
 * 唱片
 */
public class MusicPlayFragment extends Fragment {

    private ImageView musicImg;
    private ImageView needleImg;

    private int needleLeft, needleTop;

    private RotateAnimation playAnimation,pauseAnimation;
    private ObjectAnimator objectAnimator;

    public MusicPlayFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_music_play, container, false);
        musicImg = view.findViewById(R.id.music_img);
        needleImg = view.findViewById(R.id.needle_image_view);

        //------------初始化Needle距离父控件的left和top----------
        needleLeft = needleImg.getLeft();
        needleTop = needleImg.getTop();

        //------------初始化Needle的播放和暂停动画----------
        playAnimation = new RotateAnimation(-15f,0f,needleLeft, needleTop);
        playAnimation.setInterpolator(new LinearInterpolator());
        playAnimation.setDuration(500);
        playAnimation.setFillAfter(true);

        pauseAnimation = new RotateAnimation(0,-15f,needleLeft, needleTop);
        pauseAnimation.setInterpolator(new LinearInterpolator());
        pauseAnimation.setDuration(500);
        pauseAnimation.setFillAfter(true);

        if (MainActivity.musicService.getMusicIndex() == -1) {
            setDefaultMusicImg();
        }else {
            //setMusicImgBitmap(MainActivity.musicService.getMusicList().get(MainActivity.musicService.getMusicIndex()).getAlbumBmp());
            setMusicImgBitmap(MainActivity.musicService.getMusicList().get(MainActivity.musicService.getMusicIndex()).getAlbumPath());
        }

        objectAnimator = ObjectAnimator.ofFloat(musicImg,"rotation",0f,360f);
        objectAnimator.setInterpolator(new LinearInterpolator());
        objectAnimator.setDuration(60000);//1min
        objectAnimator.setRepeatCount(ValueAnimator.INFINITE);
        objectAnimator.setRepeatMode(ValueAnimator.RESTART);
        objectAnimator.start();
        if (!MainActivity.musicService.isPlaying()){
            objectAnimator.pause();
        }

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    public void setDefaultMusicImg() {
        if (musicImg != null)
            musicImg.setImageResource(R.drawable.ic_default_music);
    }

    public void setMusicImgBitmap(String path) {
        if (musicImg != null) {
            GlideUtils.setImg(getActivity(),path,musicImg);
        }
        //musicImg.setImageBitmap(bitmap);
    }

    public void pauseObjectAnimator() {
        objectAnimator.pause();
    }

    public void startObjectAnimator() {
        objectAnimator.start();
    }

    public void resumeObjectAnimator() {
        objectAnimator.resume();
    }

    public void startNeedleImgPlayAnim() {
        needleImg.startAnimation(playAnimation);
    }

    public void startNeedleImgPauseAnim() {
        needleImg.startAnimation(pauseAnimation);
    }
}
