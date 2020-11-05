package com.shuai.musicplayer2.control;

import android.animation.ObjectAnimator;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.shuai.musicplayer2.R;
import com.shuai.musicplayer2.api.Api;
import com.shuai.musicplayer2.domain.MusicInfo;
import com.shuai.musicplayer2.domain.MusicList;
import com.shuai.musicplayer2.domain.MusicListInfo;
import com.shuai.musicplayer2.domain.MusicUrl;
import com.shuai.musicplayer2.interfaces.IPlayerController;
import com.shuai.musicplayer2.interfaces.IPlayerViewController;
import com.shuai.musicplayer2.service.PlayService;
import com.shuai.musicplayer2.utils.GetMusicListInfo;


import retrofit2.Call;
import retrofit2.Retrofit;

import static com.shuai.musicplayer2.interfaces.IPlayerController.PLAY_STATE_PAUSE;
import static com.shuai.musicplayer2.interfaces.IPlayerController.PLAY_STATE_START;
import static com.shuai.musicplayer2.interfaces.IPlayerController.PLAY_STATE_STOP;

public class Player extends AppCompatActivity {

    private static final String TAG = "Player";
    private ImageView mPic;
    private ObjectAnimator mRotation;
    private IPlayerController mController;
    private PlayerConnection mPlayerConnection;
    private boolean isTouch = false;
    private SeekBar mSeek;
    private Button mSp;
    private static int mPosition;
    private static int mMusicId;;
    private MusicListInfo mMusicInfo;
    private boolean isLike = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acitivity_player);
        //初始化界面
        initView();
        initEvent();
        initService();
        initBindService();
    }

    /**
     * 根据数据设置初始界面
     */
    private void initView() {
        /**
         * 判断与上一首歌是否相同
         */
        Intent intent = getIntent();
        int newPosition = intent.getIntExtra("position",-1);
        int newMusicId = GetMusicListInfo.sMusicListInfo.get(newPosition).getId();
        if (mMusicId==newMusicId){
            isLike = true;
        }
        mPosition = newPosition;
        mMusicId = newMusicId;
        mMusicInfo = GetMusicListInfo.sMusicListInfo.get(mPosition);
        TextView title= findViewById(R.id.player_title);
        title.setText(mMusicInfo.getName());
        TextView artists= findViewById(R.id.player_artists);
        String mArtists = "";
        //处理多个作者
        for (MusicList.ResultBean.SongsBean.ArtistsBean artist: mMusicInfo.getArtistsName()) {
            mArtists += artist.getName();
        }
        artists.setText(mArtists);
        //处理多个标注
        TextView alis= findViewById(R.id.player_alis);
        String alisStr = "";
        for (String ali : mMusicInfo.getAlia()){
            alisStr += ali;
        }
        if(!alisStr.equals("")){
            alisStr = "("+alisStr+")";
        }
        alis.setText(alisStr);
        Button mv = findViewById(R.id.player_mv);
        if (mMusicInfo.getMvid()==0){
            mv.setVisibility(View.GONE);
        }
        mPic = findViewById(R.id.player_pic);
        //设置背景图片
        Glide.with(mPic.getContext()).load(mMusicInfo.getPicUrl()).into(mPic);
        //设置旋转动画
        mRotation = ObjectAnimator
                .ofFloat(mPic, "rotation", 0,360)
                .setDuration(15000);
        mRotation.setRepeatCount(Animation.INFINITE);
        mRotation.setInterpolator(new LinearInterpolator());
        mRotation.start();
    }


    /**
     * 初始化音乐播放事件，包括进度条更新
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
     * 初始化音乐服务
     */
    private void initService() {
        Log.i(TAG,"->initService");
        startService(new Intent(this, PlayService.class));
    }

    /**
     * 绑定音乐服务
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
        if (mRotation!=null){
            mRotation.start();
        }
        if (mController != null&&isLike==false) {
            mController.start(mMusicInfo.getUrl());
        }
    }

    /**
     * 销毁时取消绑定服务
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPlayerConnection != null) {
            unbindService(mPlayerConnection);
            mPlayerViewController = null;
        }
    }
}
