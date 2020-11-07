package com.shuai.musicplayer2.control;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.shuai.musicplayer2.R;
import com.shuai.musicplayer2.adapter.MusicListAdapter;

public class Result extends AppCompatActivity {


    private RecyclerView mRvResult;
    private MusicListAdapter mAdapter;
    private TextView mInfo;
    private ProgressBar mProgressBar;
    public static Handler mHandler;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        InitView();
    }
    private void InitView() {
        mInfo = findViewById(R.id.tv_info);
        mRvResult = findViewById(R.id.rv_result);
        mAdapter = new MusicListAdapter();
        mProgressBar = findViewById(R.id.pb_result);
        mProgressBar.setVisibility(View.VISIBLE);
        Intent intent = getIntent();
        String info = intent.getStringExtra("Tag")+intent.getStringExtra("keyword");
        mInfo.setText(info);
        mHandler = new Handler(){
            @Override
            public void handleMessage(@NonNull Message msg) {
                if (msg.what==100){
                    /**
                     * Result数据加载完毕，开始更新UI
                     */
                    mProgressBar.setVisibility(View.GONE);
                    //UI更新搜索结果
                    update();
                }
            }
        };
    }

    //实现点击的接口
    public void initListener(){
        mAdapter.setOnMusicClickListener(new MusicListAdapter.OnMusicClickListener() {
            @Override
            public void onMusicClick(int position) {
                Intent intent = new Intent(Result.this,Player.class);
                intent.putExtra("position",position);
                startActivity(intent);
            }
        });
    }

    //更新UI
    private void update() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mAdapter.setData();
        mRvResult.setAdapter(mAdapter);
        mRvResult.setLayoutManager(layoutManager);
        initListener();
    }
}
