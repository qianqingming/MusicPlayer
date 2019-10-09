package com.tct.musicplayer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class ScanActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView backImg;
    private RelativeLayout scanDir;
    private Button scanBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        //修改状态栏字体颜色
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        backImg = findViewById(R.id.back);
        scanDir = findViewById(R.id.layout_scan_dir);
        scanBtn = findViewById(R.id.btn_scan);

        backImg.setOnClickListener(this);
        scanDir.setOnClickListener(this);
        scanBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.layout_scan_dir:
                Intent intent = new Intent(this,ScanDirActivity.class);
                startActivity(intent);
                break;
            case R.id.btn_scan:
                break;
        }
    }
}
