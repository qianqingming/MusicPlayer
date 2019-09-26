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
import com.tct.musicplayer.utils.CharacterUtils;

import java.util.Collections;


public class TitleDecoration extends RecyclerView.ItemDecoration {

    private Paint textPaint;
    private Paint bgPaint;
    private TitleDecorationCallBack callBack;
    private int titleHeight;

    public TitleDecoration(Context context, TitleDecorationCallBack callBack) {
        this.callBack = callBack;

        textPaint = new Paint();
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(50);
        titleHeight = 100;

        bgPaint = new Paint();
        bgPaint.setColor(ContextCompat.getColor(context, R.color.gray));
    }

    /**
     * outRect可以设置子项的四个方向的偏移
     * 上下左右被撑开的距离
     */
    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        //outRect.set(300,60,10,100);
        int position = parent.getChildAdapterPosition(view);
        //Log.d("qianqingming","position:"+position+"--"+CharacterUtils.getPingYin(callBack.getSingerName(position)).substring(0,1).toUpperCase());
        if (position == 0) {
            outRect.set(0,titleHeight,0,0);
        }else{
            String str1 = callBack.getSingerFirstLetter(position);
            String str2 = callBack.getSingerFirstLetter(position - 1);
            if (!str1.equals(str2)) {
                outRect.set(0,titleHeight,0,0);
            }
        }
    }


    @Override
    public void onDraw(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.onDraw(c, parent, state);
        int left = parent.getPaddingLeft();
        int right = parent.getWidth() - parent.getPaddingRight();
        int top = parent.getPaddingTop();
        //Log.d("qianqingming","left"+left);
        //Log.d("qianqingming","right"+right);
        int childCount = parent.getChildCount();
        //Log.d("qianqingming","childCount:"+childCount);//8
        for (int i = 0; i < childCount; i++) {
            View child = parent.getChildAt(i);
            int position = parent.getChildAdapterPosition(child);
            if (position == 0) {
                c.drawRect(0,child.getTop()-titleHeight,right,child.getTop(),bgPaint);
                c.drawText(callBack.getSingerFirstLetter(position),left,child.getTop()-36, textPaint);
            }else if (position == callBack.getSingerListSize() - 1){
                c.drawRect(0,child.getTop()-titleHeight,right,child.getTop(),bgPaint);
                c.drawText("#",left,child.getTop()-36, textPaint);
            }else {
                String str1 = callBack.getSingerFirstLetter(position);
                String str2 = callBack.getSingerFirstLetter(position - 1);
                if (!str1.equals(str2)) {
                    c.drawRect(0,child.getTop()-titleHeight,right,child.getTop(),bgPaint);
                    c.drawText(str1,left,child.getTop()-36, textPaint);
                }
            }
        }
    }

    /**
     * 绘制前景
     */
    @Override
    public void onDrawOver(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.onDrawOver(c, parent, state);
        /*c.drawRect(0,0,parent.getWidth() - parent.getPaddingRight(),titleHeight,bgPaint);
        int left = parent.getPaddingLeft();
        int right = parent.getWidth() - parent.getPaddingRight();
        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = parent.getChildAt(i);
            int position = parent.getChildAdapterPosition(child);
            if (position == 0) {
                c.drawText(CharacterUtils.getPingYin(callBack.getSingerName(position)).substring(0,1).toUpperCase(),left,titleHeight-36, textPaint);
            }else {
                if (child.getTop() < 0) {
                    String str1 = CharacterUtils.getPingYin(callBack.getSingerName(position)).substring(0,1).toUpperCase();
                    String str2 = CharacterUtils.getPingYin(callBack.getSingerName(position - 1)).substring(0,1).toUpperCase();
                    if (str1.equals(str2)) {
                        c.drawText(CharacterUtils.getPingYin(callBack.getSingerName(position - 1)).substring(0,1).toUpperCase(),left,titleHeight-36, textPaint);
                    }else {
                        c.drawText(CharacterUtils.getPingYin(callBack.getSingerName(position)).substring(0,1).toUpperCase(),left,titleHeight-36, textPaint);
                    }
                }
            }
        }*/
    }

    public interface TitleDecorationCallBack {
        String getSingerFirstLetter(int position);
        int getSingerListSize();
    }
}
