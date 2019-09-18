package com.tct.musicplayer.adapter;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.tct.musicplayer.R;

public class ItemLineDecoration extends RecyclerView.ItemDecoration {

    private Paint paint;

    public ItemLineDecoration(Context context) {
        paint = new Paint();
        paint.setColor(ContextCompat.getColor(context,R.color.gray));
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
        if (parent != null){
            int itemCount = parent.getAdapter().getItemCount();
            //Log.d("qianqingming","count:"+itemCount);
            for (int i = 0; i < itemCount; i++) {
                View view = parent.getChildAt(i);
                if (view != null) {
                    c.drawRect(10,view.getBottom(),view.getWidth(),view.getBottom()+5,paint);
                }
            }
        }

    }

}
