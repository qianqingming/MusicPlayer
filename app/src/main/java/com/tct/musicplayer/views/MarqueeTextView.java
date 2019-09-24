package com.tct.musicplayer.views;


import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;

import androidx.annotation.Nullable;

public class MarqueeTextView extends androidx.appcompat.widget.AppCompatTextView {

    public MarqueeTextView(Context context) {
        super(context);
        init();
    }

    public MarqueeTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MarqueeTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setSingleLine();
        setEllipsize(TextUtils.TruncateAt.MARQUEE);
        setMarqueeRepeatLimit(-1);
    }

    @Override
    public boolean isSelected() {
        return true;
    }

    /*@Override
    public boolean isFocused() {
        return true;
    }*/
}
