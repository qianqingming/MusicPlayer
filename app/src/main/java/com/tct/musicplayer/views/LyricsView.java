package com.tct.musicplayer.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.tct.musicplayer.entity.LyricsRow;

import java.util.List;

public class LyricsView extends View {

    private static final String DEFAULT_TEXT = "没有歌词文件";


    private Paint highlightPaint;
    private Paint normalTextPaint;
    private Paint timelinePaint;
    private Paint progressPaint;
    private List<LyricsRow> lyricsRowList;

    private boolean hasLyrics = false;

    public LyricsView(Context context) {
        super(context);
        init();
    }

    public LyricsView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public LyricsView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        highlightPaint = new Paint();
        highlightPaint.setColor(0xffffffff);
        highlightPaint.setTextSize(32);
        highlightPaint.setAntiAlias(true);

        normalTextPaint = new Paint();
        normalTextPaint.setColor(0xffffffff);
        normalTextPaint.setTextSize(40);
        normalTextPaint.setAntiAlias(true);

        timelinePaint = new Paint();
        timelinePaint.setColor(0xff5a5a5a);
        timelinePaint.setTextSize(5);

        progressPaint = new Paint();
        progressPaint.setTextSize(16);
        progressPaint.setAntiAlias(true);
        progressPaint.setColor(0x55ffffff);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (!hasLyrics) {
            drawHintText(canvas, DEFAULT_TEXT);
            return;
        }

        //float y = getHeight() / 2 + 10;
        float y = 10;
        if (lyricsRowList != null && !lyricsRowList.isEmpty()) {
            for (int i = 0; i < lyricsRowList.size(); i++) {
                drawNormalText(canvas,lyricsRowList.get(i).getContent(),y);
                y = y + 80;
            }
        }
    }

    /**
     * 当正在加载或者暂无歌词时，绘制提示词
     */
    private void drawHintText(Canvas canvas, String text) {
        float textWidth = normalTextPaint.measureText(text);
        float textX = (getWidth() - textWidth) / 2;
        canvas.drawText(text, textX, getHeight() / 2, normalTextPaint);
    }

    /*private void drawNormalText(Canvas canvas, String text, float y) {
        if (text.isEmpty()) {
            return;
        }
        float textWidth = normalTextPaint.measureText(text);
        float x = (getWidth() - textWidth) / 2;
        canvas.drawText(text, x, y, normalTextPaint);
    }*/

    private void drawNormalText(Canvas canvas, String text, float y) {
        if (text.isEmpty()) {
            return;
        }
        canvas.save();
        float textWidth = normalTextPaint.measureText(text);
        float x = (getWidth() - textWidth) / 2;
        canvas.drawText(text, x, y, normalTextPaint);
        canvas.restore();
    }


    public void hasLyrics(boolean hasLyrics){
        this.hasLyrics = hasLyrics;
    }

    public void setLyricsRowList(List<LyricsRow> lyricsRowList) {
        this.lyricsRowList = lyricsRowList;
    }

}
