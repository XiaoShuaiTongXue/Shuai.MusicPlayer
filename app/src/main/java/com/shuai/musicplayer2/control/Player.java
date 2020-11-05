package com.shuai.musicplayer2.control;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.UiThread;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.shuai.musicplayer2.R;
import com.shuai.musicplayer2.api.Api;
import com.shuai.musicplayer2.domain.MusicInfo;
import com.shuai.musicplayer2.domain.MusicUrl;
import com.shuai.musicplayer2.interfaces.IPlayerController;
import com.shuai.musicplayer2.interfaces.IPlayerViewController;
import com.shuai.musicplayer2.service.PlayService;
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

import static com.shuai.musicplayer2.interfaces.IPlayerController.PLAY_STATE_PAUSE;
import static com.shuai.musicplayer2.interfaces.IPlayerController.PLAY_STATE_START;
import static com.shuai.musicplayer2.interfaces.IPlayerController.PLAY_STATE_STOP;

public class Player extends AppCompatActivity {

    private static final String TAG = "Player";
    private Retrofit mRetrofit;
    private Api mApi;
    private String mMusicId;
    private Call<MusicInfo> mTask;
    private MusicInfo mMusicInfo;
    private ImageView mPic;
    private ObjectAnimator mRotation;
    private IPlayerController mController;
    private PlayerConnection mPlayerConnection;
    private boolean isTouch = false;
    private SeekBar mSeek;
    private Button mSp;
    private String mUrl;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acitivity_player);
        Intent intent = getIntent();
        mMusicId = intent.getStringExtra("musicId");
        mUrl = intent.getStringExtra("url");
        Toast.makeText(this, mMusicId, Toast.LENGTH_SHORT).show();
        //根据MusicID准备本页面需要的数据
        initData();
        initEvent();
        initService();
        initBindService();
    }

    private void initData() {
        //根据json的内容设置页面内容
        mRetrofit = RetrofitManager.getRetrofit();
        mApi = mRetrofit.create(Api.class);
        mTask = mApi.getMusicInfo(mMusicId);
        //初始化音乐信息
        mTask.enqueue(new Callback<MusicInfo>() {
            @Override
            public void onResponse(Call<MusicInfo> call, Response<MusicInfo> response) {
                if(response.code() == HttpURLConnection.HTTP_OK){
                    mMusicInfo = response.body();
                    //得到信息后开始初始化界面
                    initView();
                }
            }

            @Override
            public void onFailure(Call<MusicInfo> call, Throwable t) {
                Log.i(TAG,t.toString());
            }
        });
    }

    /**
     * 根据数据设置初始界面
     */
    private void initView() {
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


    /**
     * 初始化事件
     */

    private void initEvent() {
        mSeek = findViewById(R.id.player_seek);
        mSp = findViewById(R.id.player_sp);
        mSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                //当进度条进度发生改变
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                //当开始触摸进度条
                isTouch = true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //停止触摸进度条
                if (mController != null) {
                    mController.seekTo(seekBar.getProgress());
                    isTouch=false;
                }
            }
        });

        //开始或暂停按钮播放按钮的点击事件
        mSp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mController != null) {
                }
            }
        });
    }

    /**
     * 初始化服务
     */
    private void initService() {
        Log.i(TAG,"->initService");
        startService(new Intent(this, PlayService.class));
    }

    /**
     * 绑定服务
     */
    private void initBindService() {
        Log.i(TAG,"->initBindService");
        Intent intent = new Intent(this, PlayService.class);
        if (mPlayerConnection == null) {
            Log.i(TAG,"->mPlayerConnection");
            mPlayerConnection = new PlayerConnection();
        }
        bindService(intent,mPlayerConnection,BIND_AUTO_CREATE);
    }

    private class PlayerConnection implements ServiceConnection {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.i(TAG,"->onServiceConnected");
            mController = (IPlayerController)service;
            //服务完成绑定后将UI控制器传到逻辑层
            mController.registerIPlayViewController(mPlayerViewController);
            startPlay();

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.i(TAG,"->onServiceDisconnected");
            mController = null;
        }
    }

    private IPlayerViewController mPlayerViewController = new IPlayerViewController() {
        @Override
        public void onPlayStateChange(int state) {
            switch (state){
                case PLAY_STATE_START:
                    //设置播放
                    break;
                case PLAY_STATE_PAUSE:
                    //设置暂停
                case PLAY_STATE_STOP:
                    break;
            }
        }

        @Override
        public void onSeekChange(int seek) {
            /**
             * 设置进度条的进度
             */
            if (mSeek != null&&isTouch==false) {
                mSeek.setProgress(seek);
            }
        }
    };

    /**
     * 开始播放
     */
    private void startPlay() {
        Log.i(TAG,"->startPlay");
        if (mController != null) {
            mController.start(mUrl);
        }
    }

//    /**
//     * 根据Id获取音乐播放Url
//     * @return 音乐播放的Url
//     */
//    private String getUrl() {
//        mUrlTask = mApi.getMusicUrl(mMusicId);
//        String url = "";
//        try {
//            mMusicUrl = mUrlTask.execute().body();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        if (mMusicUrl!=null){
//            url = mMusicUrl.getData().get(0).getUrl().toString();
//        }
//        Log.i(TAG,"url获取："+url);
//        return url;
//    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        /**
         * 销毁时取消绑定服务
         */
        if (mPlayerConnection != null) {
            unbindService(mPlayerConnection);
            mPlayerViewController = null;
        }
    }
}
