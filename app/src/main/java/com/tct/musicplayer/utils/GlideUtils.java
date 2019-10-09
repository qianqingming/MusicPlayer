package com.tct.musicplayer.utils;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.tct.musicplayer.R;

import java.io.File;

public class GlideUtils {

    public static void setImg(Context context, String path, ImageView view) {
        if (path == null) {
            Glide.with(context).load(R.drawable.ic_default_music).into(view);
        }else{
            File file = new File(path);
            if (file.exists()) {
                Glide.with(context).load(path).into(view);
            }else {
                Glide.with(context).load(R.drawable.ic_default_music).into(view);
            }
        }
        //Glide.with(context).load(path).error(R.drawable.ic_default_music).into(view);
    }
}
