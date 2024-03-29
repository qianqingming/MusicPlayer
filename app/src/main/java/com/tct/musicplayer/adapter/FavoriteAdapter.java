package com.tct.musicplayer.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.tct.musicplayer.MainActivity;
import com.tct.musicplayer.R;
import com.tct.musicplayer.entity.Album;
import com.tct.musicplayer.entity.Artist;
import com.tct.musicplayer.entity.Song;
import com.tct.musicplayer.utils.BroadcastUtils;
import com.tct.musicplayer.utils.MusicUtils;
import com.tct.musicplayer.utils.ToastUtils;

import org.litepal.LitePal;

import java.io.File;
import java.util.List;

public class FavoriteAdapter extends RecyclerView.Adapter<FavoriteAdapter.ViewHolder> {

    private static final String TAG = "qianqingming";

    private List<Song> list;
    private Context context;


    private PopupWindow removeFavoritePopupWindow;
    private int longClickPos;


    public FavoriteAdapter(final Context context, List<Song> list){
        this.context = context;
        this.list = list;

        initRemovePopupWindow();

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
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_view_item_favorite, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        holder.musicLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //更新播放的列表
                MainActivity.musicService.setMusicIndex(holder.getAdapterPosition());
                MainActivity.musicService.setMusicList(list);
                //MainActivity.musicList = list;
                //跳转Activity
                //Intent intent1 = new Intent(context, MusicPlayActivity.class);
                //context.startActivity(intent1);
                //发送广播，播放音乐
                Intent intent = new Intent(BroadcastUtils.ACTION_PLAY_SELECTED_MUSIC);
                intent.putExtra("position",holder.getAdapterPosition());
                context.sendOrderedBroadcast(intent,null);
                notifyDataSetChanged();
            }
        });

        holder.musicLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                //显示弹窗
                removeFavoritePopupWindow.showAtLocation(view, Gravity.BOTTOM,0,0);
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
            //holder.songImg.setImageBitmap(song.getAlbumBmp());
            //GlideUtils.setImg(context,song.getAlbumPath(),holder.songImg);
            Glide.with(context).load(song.getAlbumPath())
                    .error(R.drawable.ic_default_music)
                    .placeholder(R.drawable.ic_default_music)
                    .into(holder.songImg);

            if (MainActivity.musicService != null && MainActivity.musicService.getMusicList() != null) {
                if (MainActivity.musicService.getMusicIndex() >= 0) {
                    if (song.getId() == MainActivity.musicService.getMusicList().get(MainActivity.musicService.getMusicIndex()).getId()){
                        holder.songName.setTextColor(context.getColor(R.color.colorSelected));
                        holder.songSinger.setTextColor(context.getColor(R.color.colorSelected));
                        holder.songTime.setTextColor(context.getColor(R.color.colorSelected));
                    }else {
                        holder.songName.setTextColor(context.getColor(R.color.black));
                        holder.songSinger.setTextColor(context.getColor(R.color.gray_default));
                        holder.songTime.setTextColor(context.getColor(R.color.gray_default));
                    }
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }



    class ViewHolder extends RecyclerView.ViewHolder {

        RelativeLayout musicLayout;
        ImageView songImg;
        TextView songName;
        TextView songSinger;
        TextView songTime;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            musicLayout = itemView.findViewById(R.id.music_layout);
            songImg = itemView.findViewById(R.id.iv_song_pic);
            songName = itemView.findViewById(R.id.tv_song_name);
            songSinger = itemView.findViewById(R.id.tv_song_singer);
            songTime = itemView.findViewById(R.id.tv_song_time);
        }
    }

    public void setList(List<Song> list) {
        this.list = list;
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
                song.setFavorite(0);
                //notifyItemChanged(longClickPos);
                removeFavoritePopupWindow.dismiss();

                ContentValues contentValues = new ContentValues();
                contentValues.put("favorite",0);
                LitePal.update(Song.class,contentValues,song.getId());

                List<Song> favoriteList = MusicUtils.getFavoriteList();

                if (MainActivity.musicService.getMusicIndex() == favoriteList.size() - 1) {
                    MainActivity.musicService.setMusicIndex(favoriteList.size() - 2 > 0 ? favoriteList.size() - 2 : 0);
                }

                for (int i = 0; i < favoriteList.size(); i++) {
                    if (favoriteList.get(i).getSongId().equals(song.getSongId())) {
                        favoriteList.remove(i);
                        break;
                    }
                }

                Intent intent = new Intent(BroadcastUtils.ACTION_NOTIFY_DATA);
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
        final CheckBox checkBox = view.findViewById(R.id.delete_from_device);
        TextView delText = view.findViewById(R.id.delete_from_device_text);

        delText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkBox.isChecked()) {
                    checkBox.setChecked(false);
                }else {
                    checkBox.setChecked(true);
                }
            }
        });

        view.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        view.findViewById(R.id.delete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Song song = list.get(longClickPos);
                if (checkBox.isChecked()) {
                    //从设备中删除
                    File file = new File(song.getPath());
                    if (file.exists()) {
                        file.delete();
                    }
                }

                List<Song> favoriteList = MusicUtils.getFavoriteList();
                for (int i = 0; i < favoriteList.size(); i++) {
                    if (favoriteList.get(i).getSongId().equals(song.getSongId())) {
                        favoriteList.remove(i);
                        break;
                    }
                }

                List<Song> musicList = MusicUtils.getMusicList();
                for (int i = 0; i < musicList.size(); i++) {
                    if (musicList.get(i).getSongId().equals(song.getSongId())) {
                        musicList.remove(i);
                        break;
                    }
                }

                List<Artist> artistList = MusicUtils.getArtistList();
                for (int i = 0; i < artistList.size(); i++) {
                    if (artistList.get(i).getSinger().equals(song.getSinger())) {
                        if (artistList.get(i).getSongList().size() == 1) {
                            artistList.remove(i);
                        }else {
                            for (int j = 0; j < artistList.get(i).getSongList().size(); j++) {
                                if (artistList.get(i).getSongList().get(j).getSongId().equals(song.getSongId())) {
                                    artistList.get(i).getSongList().remove(j);
                                    break;
                                }
                            }
                        }
                        break;
                    }
                }

                List<Album> albumList = MusicUtils.getAlbumList();
                for (int i = 0; i < albumList.size(); i++) {
                    if (albumList.get(i).getSinger().equals(song.getSinger())) {
                        if (albumList.get(i).getSongList().size() == 1) {
                            albumList.remove(i);
                        }else {
                            for (int j = 0; j < albumList.get(j).getSongList().size(); j++) {
                                if (albumList.get(i).getSongList().get(j).getSongId().equals(song.getSongId())) {
                                    albumList.get(i).getSongList().remove(j);
                                    break;
                                }
                            }
                        }
                        break;
                    }
                }

                LitePal.delete(Song.class,song.getId());
                //MusicUtils.setMusicList(LitePal.findAll(Song.class));

                Intent intent = new Intent(BroadcastUtils.ACTION_NOTIFY_DATA);
                intent.putExtra("songId",song.getSongId());
                context.sendBroadcast(intent);
                dialog.dismiss();
                ToastUtils.showToast(context,context.getResources().getString(R.string.delete_success));
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
