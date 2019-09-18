package com.tct.musicplayer.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tct.musicplayer.R;
import com.tct.musicplayer.domain.Album;
import com.tct.musicplayer.utils.CharacterUtils;

import java.util.List;

public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.ViewHolder> {

    private Context context;
    private List<Album> albumList;

    public AlbumAdapter(Context context, List<Album> albumList) {
        this.context = context;
        this.albumList = albumList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.album_list_item, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (albumList != null) {
            Album album = albumList.get(position);
            Log.d("qianqingming","songlist:"+album.getSongList());
            holder.albumImg.setImageBitmap(album.getSongList().get(0).getAlbumBmp());
            holder.albumName.setText(album.getAlbumName());
            holder.songCount.setText(""+album.getSongList().size());
            holder.singerName.setText(album.getSinger());
        }
    }

    @Override
    public int getItemCount() {
        return albumList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        ImageView albumImg;
        TextView albumName;
        TextView songCount;
        TextView singerName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            albumImg = itemView.findViewById(R.id.img_album);
            albumName = itemView.findViewById(R.id.tv_album_name);
            songCount = itemView.findViewById(R.id.tv_song_count);
            singerName = itemView.findViewById(R.id.tv_singer);
        }
    }

}
