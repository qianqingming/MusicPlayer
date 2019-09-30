package com.tct.musicplayer.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.tct.musicplayer.MainActivity;
import com.tct.musicplayer.MusicPlayActivity;
import com.tct.musicplayer.R;
import com.tct.musicplayer.entity.Song;
import com.tct.musicplayer.utils.BroadcastUtils;
import com.tct.musicplayer.utils.GlideUtils;
import com.tct.musicplayer.utils.MusicUtils;
import com.tct.musicplayer.utils.NotificationUtils;

import java.util.List;

public class PlayListAdapter extends RecyclerView.Adapter<PlayListAdapter.ViewHolder> {

    private List<Song> list;
    private Context context;

    private int selectedPos;
    private int lastSelectedPos;
    private boolean isFirst = true;


    public PlayListAdapter(final Context context, List<Song> list){
        this.context = context;
        this.list = list;



    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_view_item_play_list, parent, false);
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
                Intent intent = new Intent(BroadcastUtils.ACTION_PLAY_SELECTED_MUSIC);
                intent.putExtra("position",holder.getAdapterPosition());
                context.sendOrderedBroadcast(intent,null);
                notifyDataSetChanged();
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
            GlideUtils.setImg(context,song.getAlbumPath(),holder.songImg);

            if (MainActivity.musicService != null && MainActivity.musicService.getMusicList() != null) {
                if (MainActivity.musicService.getMusicIndex() >= 0) {
                    if (song.getId() == MainActivity.musicService.getMusicList().get(MainActivity.musicService.getMusicIndex()).getId()){
                        holder.songName.setTextColor(context.getColor(R.color.colorSelected));
                        holder.songSinger.setTextColor(context.getColor(R.color.colorSelected));
                        holder.songTime.setTextColor(context.getColor(R.color.colorSelected));
                    }else {
                        holder.songName.setTextColor(context.getColor(R.color.white));
                        holder.songSinger.setTextColor(context.getColor(R.color.white));
                        holder.songTime.setTextColor(context.getColor(R.color.white));
                    }
                }
            }

            /*if (!isFirst) {
                if (position == selectedPos) {
                    //holder.musicLayout.setBackgroundColor(context.getColor(R.color.colorSelected));
                    holder.songName.setTextColor(context.getColor(R.color.colorSelected));
                    holder.songSinger.setTextColor(context.getColor(R.color.colorSelected));
                    holder.songTime.setTextColor(context.getColor(R.color.colorSelected));
                }else {
                    //holder.musicLayout.setBackgroundColor(Color.parseColor("#ffffff"));
                    holder.songName.setTextColor(context.getColor(R.color.white));
                    holder.songSinger.setTextColor(context.getColor(R.color.white));
                    holder.songTime.setTextColor(context.getColor(R.color.white));
                }
            }*/
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

}
