package com.tct.musicplayer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.tct.musicplayer.adapter.ItemLineDecoration;
import com.tct.musicplayer.adapter.ScanDirAdapter;
import com.tct.musicplayer.utils.MusicUtils;

import java.util.ArrayList;
import java.util.List;

public class ScanDirActivity extends AppCompatActivity implements View.OnClickListener {

    private RecyclerView dirListRecyclerView;
    private ImageView backImg;
    private TextView sureText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_dir);

        //修改状态栏字体颜色
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        backImg = findViewById(R.id.back);
        sureText = findViewById(R.id.tv_sure);
        dirListRecyclerView = findViewById(R.id.lv_dir);

        backImg.setOnClickListener(this);
        sureText.setOnClickListener(this);

        List<String> dirList = MusicUtils.getMusicDirList();

        dirListRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        dirListRecyclerView.setAdapter(new ScanDirAdapter(this,dirList));
        dirListRecyclerView.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.tv_sure:
                finish();
                break;
        }
    }
}
