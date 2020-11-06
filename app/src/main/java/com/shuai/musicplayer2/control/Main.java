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
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.shuai.musicplayer2.R;
import com.shuai.musicplayer2.adapter.MusicListAdapter;
import com.shuai.musicplayer2.adapter.TopListAdapter;
import com.shuai.musicplayer2.domain.TopList;
import com.shuai.musicplayer2.utils.GetMenuList;
import com.shuai.musicplayer2.utils.GetTopList;


public class Main extends AppCompatActivity {

    private EditText mEditText;
    private static final String TAG = "Main";
    private TopListAdapter mAdapter;
    public static Handler mHandler;
    private String mKeyWord;
    private RecyclerView mTopList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        new GetTopList();
    }

    private void initView() {
        mEditText = findViewById(R.id.keywords);
        mTopList = findViewById(R.id.rv_toplist);
        mAdapter = new TopListAdapter();
        mHandler = new Handler(){
            @Override
            public void handleMessage(@NonNull Message msg) {
                if (msg.what==100){
                    /**
                     * Result数据加载完毕，页面跳转到Result返回搜索结果
                     */
                    goResult();
                }else if(msg.what==200){
                    initTopList();
                }
            }
        };
    }


    public void search(View view) {
        mKeyWord = mEditText.getText().toString();
        if(mKeyWord ==null|| mKeyWord.equals("")){
            Toast.makeText(this, "请输入搜索关键词", Toast.LENGTH_SHORT).show();
        }
        new GetMenuList(mKeyWord);
    }

    private void goResult() {
        Intent intent = new Intent(Main.this,Result.class);
        intent.putExtra("Tag","搜索：");
        intent.putExtra("keyword",mKeyWord);
        startActivity(intent);
    }

    //实现点击的接口
    public void initListener(){
        mAdapter.setOnTopListClick(new TopListAdapter.OnTopListClickListener() {
            @Override
            public void onTopListClick(String id) {
                Toast.makeText(Main.this, "你点击了："+id, Toast.LENGTH_SHORT).show();
            }
        });
    }

    //更新榜单信息
    private void initTopList() {
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2,RecyclerView.VERTICAL);
        mAdapter.setData();
        mTopList.setAdapter(mAdapter);
        mTopList.setLayoutManager(layoutManager);
        initListener();
    }

}
