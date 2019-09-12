package com.tct.musicplayer.adapter;

import android.content.Context;
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

import com.tct.musicplayer.R;
import com.tct.musicplayer.domain.Song;
import com.tct.musicplayer.utils.MusicUtils;

import java.io.IOException;
import java.util.List;

public class SongsAdapter extends RecyclerView.Adapter<SongsAdapter.ViewHolder> {

    private List<Song> list;
    private Context context;

    private MediaPlayer mMediaPlayer;

    public SongsAdapter(Context context, List<Song> list){
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.songs_list_item, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        holder.musicLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    /*holder.songName.setTextColor(getResources().getColor(R.color.colorSelected));
                    holder.songSinger.setTextColor(getResources().getColor(R.color.colorSelected));
                    holder.songTime.setTextColor(getResources().getColor(R.color.colorSelected));*/

                /*try {
                    mMediaPlayer = new MediaPlayer();
                    mMediaPlayer.setDataSource(list.get(holder.getAdapterPosition()).getPath());
                    mMediaPlayer.prepare();
                    mMediaPlayer.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }*/
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (list != null){
            Song song = list.get(position);
            holder.songName.setText(song.getName());
            holder.songSinger.setText(song.getSinger());
            holder.songTime.setText(MusicUtils.formatTime(song.getDuration()));
            //holder.songImg.setImageDrawable(new BitmapDrawable(song.getAlbumBmp()));
            holder.songImg.setImageBitmap(song.getAlbumBmp());
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
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
