package com.tct.musicplayer.adapter;


import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.tct.musicplayer.AlbumActivity;
import com.tct.musicplayer.ArtistActivity;
import com.tct.musicplayer.MainActivity;
import com.tct.musicplayer.MusicPlayActivity;
import com.tct.musicplayer.R;
import com.tct.musicplayer.entity.Album;
import com.tct.musicplayer.entity.Artist;
import com.tct.musicplayer.entity.Song;
import com.tct.musicplayer.utils.BroadcastUtils;
import com.tct.musicplayer.utils.MusicUtils;

import java.util.ArrayList;
import java.util.List;

public class SearchAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_TITLE = 1;
    private static final int TYPE_SONG = 2;
    private static final int TYPE_ARTIST = 3;
    private static final int TYPE_ALBUM = 4;

    private List<Object> list;
    private Context context;

    public SearchAdapter(Context context, List<Object> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == TYPE_TITLE) {
            view = LayoutInflater.from(context).inflate(R.layout.search_view_item_title, parent, false);
            return new TitleHolder(view);
        }else if (viewType == TYPE_SONG) {
            view = LayoutInflater.from(context).inflate(R.layout.search_view_item_songs, parent, false);
            final SongHolder songHolder = new SongHolder(view);
            songHolder.musicLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Song song = (Song) list.get(songHolder.getAdapterPosition());
                    List<Song> songList = new ArrayList<>();
                    songList.add(song);
                    //更新播放的列表
                    MainActivity.musicService.setMusicIndex(0);
                    MainActivity.musicService.setMusicList(songList);
                    //跳转Activity
                    Intent intent1 = new Intent(context, MusicPlayActivity.class);
                    context.startActivity(intent1);
                    //发送广播，播放音乐
                    Intent intent = new Intent(BroadcastUtils.ACTION_PLAY_MUSIC);
                    context.sendOrderedBroadcast(intent,null);
                    notifyDataSetChanged();
                }
            });
            return songHolder;
        }else if (viewType == TYPE_ARTIST) {
            view = LayoutInflater.from(context).inflate(R.layout.search_view_item_artist_album, parent, false);
            final ArtistHolder artistHolder = new ArtistHolder(view);
            artistHolder.text.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Artist artist = (Artist) list.get(artistHolder.getAdapterPosition());
                    int pos = 0;
                    for (int i = 0; i < MusicUtils.getArtistList().size(); i++) {
                        Artist artist1 = MusicUtils.getArtistList().get(i);
                        if (artist1.equals(artist)) {
                            pos = i;
                            break;
                        }
                    }
                    Intent intent = new Intent(context, ArtistActivity.class);
                    intent.putExtra("position",pos);
                    context.startActivity(intent);
                }
            });
            return artistHolder;
        }else{
            view = LayoutInflater.from(context).inflate(R.layout.search_view_item_artist_album, parent, false);
            final AlbumHolder albumHolder = new AlbumHolder(view);
            albumHolder.text.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Album album = (Album) list.get(albumHolder.getAdapterPosition());
                    int pos = 0;
                    for (int i = 0; i < MusicUtils.getAlbumList().size(); i++) {
                        Album album1 = MusicUtils.getAlbumList().get(i);
                        if (album1.equals(album)) {
                            pos = i;
                            break;
                        }
                    }
                    Intent intent = new Intent(context, AlbumActivity.class);
                    intent.putExtra("position",pos);
                    context.startActivity(intent);
                }
            });
            return albumHolder;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (list != null && list.size() > 0) {
            if (holder instanceof TitleHolder) {
                TitleHolder titleHolder = (TitleHolder)holder;
                titleHolder.titleText.setText((String)list.get(position));
            }else if (holder instanceof SongHolder) {
                SongHolder songHolder = (SongHolder)holder;
                Song song = (Song) list.get(position);
                songHolder.songName.setText(song.getName());
                songHolder.songSinger.setText(song.getSinger());
                songHolder.songTime.setText(MusicUtils.formatTime(song.getDuration()));
                Glide.with(context).load(song.getAlbumPath())
                        .error(R.drawable.ic_default_music)
                        .placeholder(R.drawable.ic_default_music)
                        .into(songHolder.songImg);
            }else if (holder instanceof ArtistHolder) {
                ArtistHolder artistHolder = (ArtistHolder)holder;
                artistHolder.text.setText(((Artist) list.get(position)).getSinger());
            }else if (holder instanceof AlbumHolder) {
                AlbumHolder albumHolder = (AlbumHolder)holder;
                albumHolder.text.setText(((Album) list.get(position)).getAlbumName());
            }
        }
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 :list.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (list.get(position) instanceof String) {
            return TYPE_TITLE;
        }else if (list.get(position) instanceof Song) {
            return TYPE_SONG;
        }else if (list.get(position) instanceof Artist) {
            return TYPE_ARTIST;
        }else if (list.get(position) instanceof Album) {
            return TYPE_ALBUM;
        }
        return super.getItemViewType(position);
    }


    class TitleHolder extends RecyclerView.ViewHolder {
        TextView titleText;

        public TitleHolder(@NonNull View itemView) {
            super(itemView);
            titleText = itemView.findViewById(R.id.title);
        }
    }

    class SongHolder extends RecyclerView.ViewHolder {
        RelativeLayout musicLayout;
        ImageView songImg;
        TextView songName;
        TextView songSinger;
        TextView songTime;

        public SongHolder(@NonNull View itemView) {
            super(itemView);
            musicLayout = itemView.findViewById(R.id.music_layout);
            songImg = itemView.findViewById(R.id.iv_song_pic);
            songName = itemView.findViewById(R.id.tv_song_name);
            songSinger = itemView.findViewById(R.id.tv_song_singer);
            songTime = itemView.findViewById(R.id.tv_song_time);
        }
    }

    class ArtistHolder extends RecyclerView.ViewHolder {
        TextView text;

        public ArtistHolder(@NonNull View itemView) {
            super(itemView);
            text = itemView.findViewById(R.id.text);
        }
    }

    class AlbumHolder extends RecyclerView.ViewHolder {
        TextView text;

        public AlbumHolder(@NonNull View itemView) {
            super(itemView);
            text = itemView.findViewById(R.id.text);
        }
    }

}
