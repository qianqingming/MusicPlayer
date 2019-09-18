package com.tct.musicplayer.views;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

import com.tct.musicplayer.R;

public class CircleImageViewWithDisk extends androidx.appcompat.widget.AppCompatImageView {

    private static final int DEFAULT_BORDER_COLOR = Color.TRANSPARENT;
    private static final int DEFAULT_BORDER_WIDTH = 0;

    private float width;
    private float height;
    private float radius;
    private Paint paint;
    private Matrix matrix;

    private Paint mBorderPaint;
    private Bitmap diskBitmap;



    public CircleImageViewWithDisk(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context,attrs);
    }

    public CircleImageViewWithDisk(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context,attrs);
    }

    private void init(Context context,AttributeSet attrs) {
        paint = new Paint();
        paint.setAntiAlias(true);
        matrix = new Matrix();

        diskBitmap = BitmapFactory.decodeResource(getResources(),R.drawable.ic_disk);

        mBorderPaint = new Paint();
        mBorderPaint.setAntiAlias(true);
        mBorderPaint.setStyle(Paint.Style.STROKE);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        width = getMeasuredWidth();
        height = getMeasuredHeight();
        radius = Math.min(width, height) / 2;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Drawable drawable = getDrawable();
        if (drawable == null) {
            super.onDraw(canvas);
            return;
        }
        if (drawable instanceof BitmapDrawable) {
            paint.setShader(initBitmapShader((BitmapDrawable) drawable));//将着色器设置给画笔
            canvas.drawBitmap(diskBitmap,0,0,mBorderPaint);
            canvas.drawCircle(width / 2, height / 2, radius-150, paint);//使用画笔在画布上画圆
            return;
        }
        super.onDraw(canvas);
    }

    private BitmapShader initBitmapShader(BitmapDrawable drawable) {
        Bitmap bitmap = drawable.getBitmap();
        BitmapShader bitmapShader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        float scale = Math.max(width / bitmap.getWidth(), height / bitmap.getHeight());
        matrix.setScale(scale, scale);
        bitmapShader.setLocalMatrix(matrix);
        return bitmapShader;
    }


}
