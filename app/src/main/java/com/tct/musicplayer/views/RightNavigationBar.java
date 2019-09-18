package com.tct.musicplayer.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;

import java.util.Collections;

public class RightNavigationBar extends View {

    private Paint textPaint = new Paint();

    private TextView textView;

    private OnTouchLetterListener listener;

    private String[] letter = {
            "A","B","C","D","E","F","G","H","I","J","K","L","M","N",
            "O","P","Q","R","S","T","U","V","W","X","Y","Z","#"};

    private int choose = -1;

    public RightNavigationBar(Context context) {
        super(context);
    }

    public RightNavigationBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public RightNavigationBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void initPaint() {
        textPaint.setTextSize(30);
        textPaint.setAntiAlias(true);
        textPaint.setColor(Color.BLACK);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawText(canvas);
    }

    private void drawText(Canvas canvas) {
        //获取View的宽高
        int width = getWidth();
        int height = getHeight();
        //获取每个字母的高度
        int singleHeight = height / letter.length;
        //画字母
        for (int i = 0; i < letter.length; i++) {
            //画笔默认颜色
            initPaint();
            //高亮字母颜色
            if (choose == i) {
                textPaint.setColor(Color.RED);
            }
            //计算每个字母的坐标
            float x = (width - textPaint.measureText(letter[i])) / 2;
            float y = (i + 1) * singleHeight;
            canvas.drawText(letter[i], x, y, textPaint);
            //重置颜色
            textPaint.reset();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int index = (int) (event.getY() / getHeight() * letter.length);
        if (index >= letter.length) {
            index = letter.length - 1;
        }else if (index < 0){
            index = 0;
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                setBackgroundColor(Color.GRAY);
                choose = index;
                if (textView != null) {
                    textView.setVisibility(VISIBLE);
                    textView.setText(letter[index]);
                }
                if (listener != null){
                    listener.touchLetter(letter[index]);
                }
                invalidate();
                break;
            default:
                setBackgroundColor(Color.TRANSPARENT);
                if (textView != null) {
                    textView.setVisibility(GONE);
                }
                invalidate();
                break;
        }
        return true;
    }

    public void setListener(OnTouchLetterListener onTouchLetterListener) {
        this.listener = onTouchLetterListener;
    }

    public interface OnTouchLetterListener {
        void touchLetter(String s);
    }

    public void setTextView(TextView textView) {
        this.textView = textView;
    }
}
