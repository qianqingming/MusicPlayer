package com.tct.musicplayer.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.tct.musicplayer.AlbumActivity;
import com.tct.musicplayer.R;
import com.tct.musicplayer.entity.Album;
import com.tct.musicplayer.utils.GlideUtils;

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
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_view_item_album, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, AlbumActivity.class);
                intent.putExtra("position",holder.getAdapterPosition());
                context.startActivity(intent);
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (albumList != null) {
            Album album = albumList.get(position);
            //holder.albumImg.setImageBitmap(album.getSongList().get(0).getAlbumBmp());
            GlideUtils.setImg(context,album.getSongList().get(0).getAlbumPath(),holder.albumImg);
            holder.albumName.setText(album.getAlbumName());
            holder.songCount.setText(""+album.getSongList().size());
            holder.singerName.setText(album.getSinger());
        }
    }

    @Override
    public int getItemCount() {
        return albumList == null ? 0 : albumList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        ImageView albumImg;
        TextView albumName;
        TextView songCount;
        TextView singerName;
        LinearLayout layout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            albumImg = itemView.findViewById(R.id.img_album);
            albumName = itemView.findViewById(R.id.tv_album_name);
            songCount = itemView.findViewById(R.id.tv_song_count);
            singerName = itemView.findViewById(R.id.tv_singer);
            layout = itemView.findViewById(R.id.layout);
        }
    }

    public void setAlbumList(List<Album> albumList) {
        this.albumList = albumList;
    }

}
