package com.shuai.musicplayer2.control;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.shuai.musicplayer2.R;
import com.shuai.musicplayer2.api.Api;
import com.shuai.musicplayer2.domain.MusicInfo;
import com.shuai.musicplayer2.utils.FastBlur;
import com.shuai.musicplayer2.utils.RetrofitManager;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Time;
import java.util.Timer;
import java.util.TimerTask;

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
    private ImageView mPic;
    private ObjectAnimator mRotation;

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
        mPic = findViewById(R.id.player_pic);
        //设置背景图片
        Glide.with(mPic.getContext())
                .asBitmap()
                .load(songsBean.getAl().getPicUrl())
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        mPic.setImageBitmap(resource);
                    }
                });
        //设置旋转动画
        mRotation = ObjectAnimator
                .ofFloat(mPic, "rotation", 0,360)
                .setDuration(15000);
        mRotation.setRepeatCount(Animation.INFINITE);
        mRotation.setInterpolator(new LinearInterpolator());
        mRotation.start();
    }


    private void initView() {
        //根据json的内容设置页面内容
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
