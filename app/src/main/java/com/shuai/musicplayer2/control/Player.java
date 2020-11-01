package com.shuai.musicplayer2.control;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.shuai.musicplayer2.R;
import com.shuai.musicplayer2.api.Api;
import com.shuai.musicplayer2.domain.MusicInfo;
import com.shuai.musicplayer2.utils.RetrofitManager;

import java.io.IOException;
import java.net.HttpURLConnection;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class Player extends AppCompatActivity {

    private static final String TAG = "Player";
    private Retrofit mRetrofit;
    private Api mApi;
    private String mMusicId;
    private Call<MusicInfo> mTask;
    private MusicInfo mMusicInfo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acitivity_player);
        Intent intent = getIntent();
        mMusicId = intent.getStringExtra("musicId");
        Toast.makeText(this, mMusicId, Toast.LENGTH_SHORT).show();
        initView();
    }

    private void setUI() {
        MusicInfo.SongsBean songsBean = mMusicInfo.getSongs().get(0);
        TextView title= findViewById(R.id.player_title);
        title.setText(songsBean.getName());
        TextView artists= findViewById(R.id.player_artists);
        artists.setText(songsBean.getAr().get(0).getName());
        TextView alis= findViewById(R.id.player_alis);
        String alisStr = "";
        for (String ali : songsBean.getAlia()){
            alisStr += ali;
        }
        if(!alisStr.equals("")){
            alisStr = "("+alisStr+")";
        }
        alis.setText(alisStr);
        Button mv = findViewById(R.id.player_mv);
        if (songsBean.getMv()==0){
            mv.setVisibility(View.GONE);
        }
        ImageView pic= findViewById(R.id.player_pic);
        Glide.with(pic.getContext()).load(songsBean.getAl().getPicUrl()).into(pic);
    }

    private void initView() {
        mRetrofit = RetrofitManager.getRetrofit();
        mApi = mRetrofit.create(Api.class);
        mTask = mApi.getMusicInfo(mMusicId);
        mTask.enqueue(new Callback<MusicInfo>() {
            @Override
            public void onResponse(Call<MusicInfo> call, Response<MusicInfo> response) {
                if(response.code() == HttpURLConnection.HTTP_OK){
                    mMusicInfo = response.body();
                    setUI();
                }
            }

            @Override
            public void onFailure(Call<MusicInfo> call, Throwable t) {
                Log.i(TAG,t.toString());
            }
        });
    }
}
