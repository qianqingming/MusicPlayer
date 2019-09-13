package com.tct.musicplayer.adapter;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ItemLineDecoration extends RecyclerView.ItemDecoration {

    private Paint paint;

    public ItemLineDecoration() {
        paint = new Paint();
        paint.setColor(Color.RED);
    }

    /**
     * outRect可以设置子项的四个方向的偏移
     */
    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        //outRect.set(0,0,0,10);
    }


    /**
     * 绘制背景
     */
    @Override
    public void onDraw(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.onDraw(c, parent, state);
        //c.drawRect(5,100,400,200,paint);
        //c.drawLine();
        if (parent != null){
            int itemCount = parent.getAdapter().getItemCount();
            for (int i = 0; i < itemCount; i++) {
                View view = parent.getChildAt(i);
                c.drawRect(10,view.getTop(),parent.getWidth()-10,view.getTop()+5,paint);
            }
        }

    }

    /**
     * 绘制前景
     */
    @Override
    public void onDrawOver(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.onDrawOver(c, parent, state);
    }
}
