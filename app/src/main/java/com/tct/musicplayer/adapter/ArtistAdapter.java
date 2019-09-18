package com.tct.musicplayer.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tct.musicplayer.R;
import com.tct.musicplayer.domain.Song;
import com.tct.musicplayer.utils.CharacterUtils;

import java.util.List;

public class ArtistAdapter extends RecyclerView.Adapter<ArtistAdapter.ViewHolder> {

    private Context context;
    private List<String> singerList;

    public ArtistAdapter(Context context, List<String> singerList) {
        this.context = context;
        this.singerList = singerList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.artist_list_item, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (singerList != null) {
            holder.singerTextView.setText(singerList.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return singerList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView singerTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            singerTextView = itemView.findViewById(R.id.tv_singer);
        }
    }

    public int getSelectPosition(String s) {
        if (s.equals("#")){
            //如果是<unknown>
            return singerList.size() - 1;
        }
        for (int i = 0; i < singerList.size(); i++) {
            //Log.d("qianqingming","1--"+CharacterUtils.getPingYin(singerList.get(i)).substring(0,1));
            if (s.equals(CharacterUtils.getPingYin(singerList.get(i)).substring(0,1).toUpperCase())){
                return i;
            }
        }
        return -1;
    }
}
