package com.tct.musicplayer.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tct.musicplayer.MusicPlayActivity;
import com.tct.musicplayer.R;
import com.tct.musicplayer.domain.Song;
import com.tct.musicplayer.utils.MusicUtils;
import com.tct.musicplayer.utils.NotificationUtils;

import java.util.ArrayList;
import java.util.List;

public class SongsAdapter extends RecyclerView.Adapter<SongsAdapter.ViewHolder> {

    private List<Song> list;
    private Context context;

    private MediaPlayer mMediaPlayer;

    private List<Boolean> isClicked;

    public SongsAdapter(Context context, List<Song> list){
        this.context = context;
        this.list = list;

        isClicked = new ArrayList<>();
        for (int i=0;i<list.size();i++){
            isClicked.add(i,false);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.songs_list_item, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        holder.musicLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //发送广播 播放音乐
                Intent intent = new Intent(NotificationUtils.ACTION_PLAY_SELECTED_MUSIC);
                intent.putExtra("position",holder.getAdapterPosition());
                context.sendBroadcast(intent);

                Intent intent1 = new Intent(context, MusicPlayActivity.class);
                context.startActivity(intent1);

                for (int i = 0; i < list.size(); i++) {
                    isClicked.set(i,false);
                }
                isClicked.set(holder.getAdapterPosition(),true);
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
            //holder.songImg.setImageDrawable(new BitmapDrawable(song.getAlbumBmp()));
            holder.songImg.setImageBitmap(song.getAlbumBmp());

            if (isClicked.get(position)) {
                holder.musicLayout.setBackgroundColor(context.getColor(R.color.colorSelected));
            }else {
                holder.musicLayout.setBackgroundColor(Color.parseColor("#ffffff"));
            }

        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void setIsClicked(int position) {
        for (int i = 0; i < list.size(); i++) {
            isClicked.set(i,false);
        }
        isClicked.set(position,true);
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        RelativeLayout musicLayout;
        ImageView songImg;
        TextView songName;
        TextView songSinger;
        TextView songTime;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            musicLayout = itemView.findViewById(R.id.music_layout);
            songImg = itemView.findViewById(R.id.iv_song_pic);
            songName = itemView.findViewById(R.id.tv_song_name);
            songSinger = itemView.findViewById(R.id.tv_song_singer);
            songTime = itemView.findViewById(R.id.tv_song_time);
        }
    }
}
