package com.tct.musicplayer.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tct.musicplayer.R;

import java.util.List;

public class ScanDirAdapter extends RecyclerView.Adapter<ScanDirAdapter.ViewHolder> {

    private Context context;
    private List<String> dirList;

    public ScanDirAdapter(Context context, List<String> dirList) {
        this.context = context;
        this.dirList = dirList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_view_item_scan_dir, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String s = dirList.get(position);
        holder.dirPath.setText(s + "/");
        holder.dirName.setText(s.substring(s.lastIndexOf("/") + 1));
    }

    @Override
    public int getItemCount() {
        return dirList == null ? 0 : dirList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView dirName;
        TextView dirPath;
        CheckBox checkBox;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            dirName = itemView.findViewById(R.id.tv_dir_name);
            dirPath = itemView.findViewById(R.id.tv_dir_path);
            checkBox = itemView.findViewById(R.id.check_box);
        }
    }
}
