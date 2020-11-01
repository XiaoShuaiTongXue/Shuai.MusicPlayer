package com.shuai.musicplayer2.control;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.shuai.musicplayer2.R;
import com.shuai.musicplayer2.adapter.MusicListAdapter;
import com.shuai.musicplayer2.api.Api;
import com.shuai.musicplayer2.domain.MusicList;
import com.shuai.musicplayer2.utils.RetrofitManager;

import javax.net.ssl.HttpsURLConnection;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;


public class Main extends AppCompatActivity {

    private EditText mEditText;
    private RecyclerView mRv;
    private Retrofit mRetrofit;
    private Api mApi;
    private static final String TAG = "Main";
    private MusicListAdapter mAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        InitView();
    }

    private void InitView() {
        mEditText = findViewById(R.id.keywords);
        mRv = findViewById(R.id.rv);
        mRetrofit = RetrofitManager.getRetrofit();
        mApi = mRetrofit.create(Api.class);
        mAdapter = new MusicListAdapter();

    }

    public void initListener(){
        mAdapter.setOnMusicClickListener(new MusicListAdapter.OnMusicClickListener() {
            @Override
            public void onMusicClick(String musicId) {
                Toast.makeText(Main.this, "你选择的音乐id："+musicId, Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void search(View view) {
        String mKeyWord = mEditText.getText().toString();
        if (mKeyWord!=null){
            Call<MusicList> task =mApi.getMusicList(mKeyWord);
            task.enqueue(new Callback<MusicList>() {
                @Override
                public void onResponse(Call<MusicList> call, Response<MusicList> response) {
                    int code = response.code();
                    Log.i(TAG,"code:"+code);
                    if (code == HttpsURLConnection.HTTP_OK){
                        update(response.body());
                    }
                }

                @Override
                public void onFailure(Call<MusicList> call, Throwable t) {
                    Log.i(TAG,t.toString());
                }
            });
        }
    }

    private void update(MusicList body) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mAdapter.setData(body);
        mRv.setAdapter(mAdapter);
        mRv.setLayoutManager(layoutManager);
        initListener();
    }
}
