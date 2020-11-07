package com.shuai.musicplayer2.control;

import android.animation.ObjectAnimator;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
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
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.shuai.musicplayer2.R;
import com.shuai.musicplayer2.domain.MusicInfo;
import com.shuai.musicplayer2.domain.MusicList;
import com.shuai.musicplayer2.interfaces.IPlayerController;
import com.shuai.musicplayer2.interfaces.IPlayerViewController;
import com.shuai.musicplayer2.service.PlayService;
import com.shuai.musicplayer2.utils.GetMenuList;
import com.shuai.musicplayer2.utils.LikeUpdate;


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
    private static String mMusicId;;
    private com.shuai.musicplayer2.domain.MusicListInfo mMusicInfo;
    private boolean isLike = false;
    private Button mLike;

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
        String newMusicId = GetMenuList.sMusicListInfo.get(newPosition).getId();
        if (mMusicId!=null&&mMusicId.equals(newMusicId)){
            isLike = true;
        }
        mPosition = newPosition;
        mMusicId = newMusicId;
        mMusicInfo = GetMenuList.sMusicListInfo.get(mPosition);
        ((TextView)findViewById(R.id.player_title)).setText(mMusicInfo.getName());
        ((TextView)findViewById(R.id.player_alis)).setText(mMusicInfo.getAlia());
        ((TextView)findViewById(R.id.player_artists)).setText(mMusicInfo.getArtistsName());
        mLike = findViewById(R.id.player_like);
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
                    mController.pauseOrResume();
                }
            }
        });

        mLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG,"我点击了喜欢");
                new LikeUpdate().likeAdd(getApplicationContext(),mPosition);
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
        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        public void onPlayStateChange(int state) {
            switch (state){
                case PLAY_STATE_START:
                    mRotation.resume();
                    mSp.setBackgroundResource(R.drawable.pause);
                    break;
                case PLAY_STATE_PAUSE:
                    mRotation.pause();
                    mSp.setBackgroundResource(R.drawable.start);
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
