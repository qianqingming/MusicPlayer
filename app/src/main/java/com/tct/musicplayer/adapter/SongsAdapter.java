package com.tct.musicplayer.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.tct.musicplayer.MainActivity;
import com.tct.musicplayer.MusicPlayActivity;
import com.tct.musicplayer.R;
import com.tct.musicplayer.domain.Song;
import com.tct.musicplayer.utils.MusicUtils;
import com.tct.musicplayer.utils.NotificationUtils;
import com.tct.musicplayer.utils.ToastUtils;

import java.io.File;
import java.util.List;

public class SongsAdapter extends RecyclerView.Adapter<SongsAdapter.ViewHolder> {

    private List<Song> list;
    private Context context;

    private int selectedPos;
    private int lastSelectedPos;
    private boolean isFirst = true;

    private PopupWindow addFavoritePopupWindow;
    private PopupWindow removeFavoritePopupWindow;
    private int longClickPos;

    public SongsAdapter(final Context context, List<Song> list){
        this.context = context;
        this.list = list;

        initAddPopupWindow();
        initRemovePopupWindow();

        addFavoritePopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                WindowManager.LayoutParams layoutParams = ((Activity)context).getWindow().getAttributes();
                layoutParams.alpha = 1f;
                ((Activity) context).getWindow().setAttributes(layoutParams);
            }
        });
        removeFavoritePopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                WindowManager.LayoutParams layoutParams = ((Activity)context).getWindow().getAttributes();
                layoutParams.alpha = 1f;
                ((Activity) context).getWindow().setAttributes(layoutParams);
            }
        });
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_view_item_songs, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        holder.musicLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //更新播放的列表
                MainActivity.musicService.setMusicIndex(holder.getAdapterPosition());
                MainActivity.musicService.setMusicList(list);
                MainActivity.musicList = list;
                //跳转Activity
                Intent intent1 = new Intent(context, MusicPlayActivity.class);
                context.startActivity(intent1);
                //设置选中项并更新
                setSelectedPos(holder.getAdapterPosition());
                //发送广播，播放音乐
                Intent intent = new Intent(NotificationUtils.ACTION_PLAY_SELECTED_MUSIC);
                intent.putExtra("position",holder.getAdapterPosition());
                context.sendBroadcast(intent);
            }
        });

        holder.musicLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (list.get(holder.getAdapterPosition()).isFavorite()) {
                    removeFavoritePopupWindow.showAtLocation(view, Gravity.BOTTOM,0,0);
                }else {
                    addFavoritePopupWindow.showAtLocation(view, Gravity.BOTTOM,0,0);
                }
                longClickPos = holder.getAdapterPosition();
                //设置窗口透明度
                WindowManager.LayoutParams layoutParams = ((Activity) context).getWindow().getAttributes();
                layoutParams.alpha = 0.4f;
                ((Activity) context).getWindow().setAttributes(layoutParams);
                return true;
            }
        });

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        if (list != null){
            Song song = list.get(position);
            holder.songName.setText(song.getName());
            holder.songSinger.setText(song.getSinger());
            holder.songTime.setText(MusicUtils.formatTime(song.getDuration()));
            //holder.songImg.setImageDrawable(new BitmapDrawable(context.getResources(),song.getAlbumBmp()));
            holder.songImg.setImageBitmap(song.getAlbumBmp());

            if (song.isFavorite()){
                holder.favorite.setVisibility(View.VISIBLE);
            }else {
                holder.favorite.setVisibility(View.GONE);
            }
            
            if (!isFirst) {
                if (position == selectedPos) {
                    //holder.musicLayout.setBackgroundColor(context.getColor(R.color.colorSelected));
                    holder.songName.setTextColor(context.getColor(R.color.colorSelected));
                    holder.songSinger.setTextColor(context.getColor(R.color.colorSelected));
                    holder.songTime.setTextColor(context.getColor(R.color.colorSelected));
                }else {
                    //holder.musicLayout.setBackgroundColor(Color.parseColor("#ffffff"));
                    holder.songName.setTextColor(context.getColor(R.color.black));
                    holder.songSinger.setTextColor(context.getColor(R.color.gray_default));
                    holder.songTime.setTextColor(context.getColor(R.color.gray_default));
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    public void setSelectedPos(int pos){
        if (isFirst) {
            selectedPos = pos;
            notifyItemChanged(selectedPos);
            lastSelectedPos = selectedPos;
            isFirst = false;
        } else {
            notifyItemChanged(lastSelectedPos);
            selectedPos = pos;
            notifyItemChanged(selectedPos);
            lastSelectedPos = selectedPos;
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        RelativeLayout musicLayout;
        ImageView songImg;
        TextView songName;
        TextView songSinger;
        TextView songTime;
        ImageView favorite;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            musicLayout = itemView.findViewById(R.id.music_layout);
            songImg = itemView.findViewById(R.id.iv_song_pic);
            songName = itemView.findViewById(R.id.tv_song_name);
            songSinger = itemView.findViewById(R.id.tv_song_singer);
            songTime = itemView.findViewById(R.id.tv_song_time);
            favorite = itemView.findViewById(R.id.iv_favorite);
        }
    }

    public void setList(List<Song> list) {
        this.list = list;
    }

    /**
     * 添加到收藏--Window初始化
     */
    private void initAddPopupWindow() {
        View view = LayoutInflater.from(context).inflate(R.layout.popup_window_add_favorite,null);
        addFavoritePopupWindow = new PopupWindow(view,ViewGroup.LayoutParams.MATCH_PARENT,300);
        addFavoritePopupWindow.setOutsideTouchable(true);//点击外部消失
        //popupWindow.setTouchable(true);
        addFavoritePopupWindow.setFocusable(true);

        //监听Back键
        view.findViewById(R.id.popup_window_layout).setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (keyEvent.getAction() == KeyEvent.ACTION_DOWN && i == KeyEvent.KEYCODE_BACK) {
                    addFavoritePopupWindow.dismiss();
                }
                return false;
            }
        });


        //添加到收藏
        view.findViewById(R.id.add_favorite).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Song song = list.get(longClickPos);
                song.setFavorite(true);
                //notifyItemChanged(longClickPos);
                addFavoritePopupWindow.dismiss();

                SharedPreferences.Editor editor = context.getSharedPreferences("favorite",Context.MODE_PRIVATE).edit();
                editor.putInt(""+song.getId(),1);
                editor.apply();

                List<Song> favoriteList = MusicUtils.getFavoriteList();
                favoriteList.add(song);

                Intent intent = new Intent("ACTION_ADD_FAVORITE");
                context.sendBroadcast(intent);

                ToastUtils.showToast(context,context.getResources().getString(R.string.add_favorite_success));
            }
        });

        //从库中移除
        view.findViewById(R.id.remove_from_list).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addFavoritePopupWindow.dismiss();
                Song song = list.get(longClickPos);
                showDeleteDialog(song.getName());
            }
        });

        //addFavoritePopupWindow.showAtLocation(view, Gravity.BOTTOM,0,0);
    }

    /**
     * 从收藏中移除--Window初始化
     */
    private void initRemovePopupWindow() {
        View view = LayoutInflater.from(context).inflate(R.layout.popup_window_remove_favorite,null);
        removeFavoritePopupWindow = new PopupWindow(view,ViewGroup.LayoutParams.MATCH_PARENT,300);
        removeFavoritePopupWindow.setOutsideTouchable(true);//点击外部消失
        //popupWindow.setTouchable(true);
        removeFavoritePopupWindow.setFocusable(true);

        //监听Back键
        view.findViewById(R.id.popup_window_layout).setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (keyEvent.getAction() == KeyEvent.ACTION_DOWN && i == KeyEvent.KEYCODE_BACK) {
                    removeFavoritePopupWindow.dismiss();
                }
                return false;
            }
        });


        //从收藏中移除
        view.findViewById(R.id.remove_favorite).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Song song = list.get(longClickPos);
                song.setFavorite(false);
                //notifyItemChanged(longClickPos);
                removeFavoritePopupWindow.dismiss();

                SharedPreferences.Editor editor = context.getSharedPreferences("favorite",Context.MODE_PRIVATE).edit();
                editor.remove(""+song.getId());
                editor.apply();

                List<Song> favoriteList = MusicUtils.getFavoriteList();
                for (int i = 0; i < favoriteList.size(); i++) {
                    if (favoriteList.get(i).getId() == song.getId()) {
                        favoriteList.remove(i);
                        break;
                    }
                }

                Intent intent = new Intent("ACTION_REMOVE_FAVORITE");
                context.sendBroadcast(intent);

                ToastUtils.showToast(context,context.getResources().getString(R.string.remove_favorite_success));
            }
        });

        //从库中移除
        view.findViewById(R.id.remove_from_list).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeFavoritePopupWindow.dismiss();
                Song song = list.get(longClickPos);
                showDeleteDialog(song.getSinger() + " - " + song.getName());
            }
        });

        //removeFavoritePopupWindow.showAtLocation(view, Gravity.BOTTOM,0,0);
    }

    private void showDeleteDialog(String title) {
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_delete,null);
        final AlertDialog dialog = new AlertDialog.Builder(context).setView(view).create();
        dialog.setCanceledOnTouchOutside(false);//点击外部不消失，按返回键消失
        ((TextView)view.findViewById(R.id.title)).setText("删除\""+title+"\"吗？");
        view.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        view.findViewById(R.id.delete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        dialog.show();
        Window window = dialog.getWindow();
        if (window != null){
            window.setGravity(Gravity.BOTTOM);
            window.setBackgroundDrawable(null);//圆角设置无效，将背景设置为null
            //window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
    }
}
