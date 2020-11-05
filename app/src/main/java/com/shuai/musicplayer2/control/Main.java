package com.shuai.musicplayer2.control;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.shuai.musicplayer2.R;
import com.shuai.musicplayer2.adapter.MusicListAdapter;
import com.shuai.musicplayer2.utils.GetMusicListInfo;


public class Main extends AppCompatActivity {

    private EditText mEditText;
    private RecyclerView mRv;
    private static final String TAG = "Main";
    private MusicListAdapter mAdapter;
    public static Handler mHandler;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        InitView();
    }

    private void InitView() {
        mEditText = findViewById(R.id.keywords);
        mRv = findViewById(R.id.rv);
        mAdapter = new MusicListAdapter();

    }

    public void initListener(){
        mAdapter.setOnMusicClickListener(new MusicListAdapter.OnMusicClickListener() {
            @Override
            public void onMusicClick(int position) {
                Intent intent = new Intent(Main.this,Player.class);
                intent.putExtra("position",position);
                startActivity(intent);
            }
        });
    }



    public void search(View view) {
        String mKeyWord = mEditText.getText().toString();
        if(mKeyWord==null||mKeyWord.equals("")){
            Toast.makeText(this, "请输入搜索关键词", Toast.LENGTH_SHORT).show();
        }
        new  GetMusicListInfo(mKeyWord);
        mHandler = new Handler(){
            @Override
            public void handleMessage(@NonNull Message msg) {
                if (msg.what==100){
                    /**
                     * 数据加载完毕开始更新UI
                     */
                    Log.i(TAG,GetMusicListInfo.sMusicListInfo.toString());
                    update();
                }
            }
        };
    }

    private void update() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mAdapter.setData();
        mRv.setAdapter(mAdapter);
        mRv.setLayoutManager(layoutManager);
        initListener();
    }
}
