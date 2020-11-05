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
    private String mKeyWord;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mEditText = findViewById(R.id.keywords);
    }

    public void search(View view) {
        mKeyWord = mEditText.getText().toString();
        if(mKeyWord ==null|| mKeyWord.equals("")){
            Toast.makeText(this, "请输入搜索关键词", Toast.LENGTH_SHORT).show();
        }
        new  GetMusicListInfo(mKeyWord);
        mHandler = new Handler(){
            @Override
            public void handleMessage(@NonNull Message msg) {
                if (msg.what==100){
                    /**
                     * 数据加载完毕，页面跳转到Result返回搜索结果
                     */
                    goResult();
                }
            }
        };
    }

    private void goResult() {
        Intent intent = new Intent(Main.this,Result.class);
        intent.putExtra("Tag","搜索：");
        intent.putExtra("keyword",mKeyWord);
        startActivity(intent);
    }
}
