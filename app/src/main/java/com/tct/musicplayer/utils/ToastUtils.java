package com.tct.musicplayer.utils;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.tct.musicplayer.R;

public class ToastUtils {

    public static void showToast(Context context, String message) {
        View view = LayoutInflater.from(context).inflate(R.layout.toast_image_text,null);
        TextView textView = view.findViewById(R.id.message);
        textView.setText(message);
        Toast toast = new Toast(context);
        toast.setGravity(Gravity.CENTER,0,0);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(view);
        toast.show();
    }
}
