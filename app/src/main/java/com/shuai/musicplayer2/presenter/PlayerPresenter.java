package com.shuai.musicplayer2.presenter;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.util.Log;

import com.shuai.musicplayer2.interfaces.IPlayerController;
import com.shuai.musicplayer2.interfaces.IPlayerViewController;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class PlayerPresenter extends Binder implements IPlayerController {

    private IPlayerViewController mViewController;
    private static final String TAG = "PlayerPresenter";
    private static int mCurrentState = PLAY_STATE_STOP;
    private MediaPlayer mPlayer;
    private Timer mTimer;
    private SeekTimeTask mTimeTask;
    private static String mUrl;


    @Override
    public void registerIPlayViewController(IPlayerViewController iPlayerViewController) {
        mViewController = iPlayerViewController;
    }

    @Override
    public void unregisterIPlayViewController() {
        mViewController = null;
    }

    @Override
    public void start(String url) {
        Log.i(TAG,"->start");
        if (mUrl!=null&&mUrl.equals(url)){
            Log.i(TAG,"两个相同");
            return;
        }
        mUrl = url;
        if (mCurrentState != PLAY_STATE_STOP){
            mPlayer.stop();
        }
        initMediaPlayer();
        try {
            mPlayer.setDataSource(mUrl);
            mPlayer.prepare();
            mPlayer.start();
            mCurrentState = PLAY_STATE_START;
            startTimeTask();
        } catch (IOException e) {
            Log.i(TAG,"error:"+e.toString());
            e.printStackTrace();
        }
    }

    @Override
    public void pauseOrResume() {
        if(mCurrentState == PLAY_STATE_START){
            if (mPlayer != null) {
                mPlayer.pause();
                mCurrentState = PLAY_STATE_PAUSE;
                stopTimeTask();
            }
        }else if(mCurrentState == PLAY_STATE_PAUSE){
            if (mPlayer != null) {
                mPlayer.start();
                mCurrentState = PLAY_STATE_START;
                startTimeTask();
            }
        }
    }

    /**
     * 初始化播放器
     */
    private void initMediaPlayer() {
        if (mPlayer == null) {
            mPlayer = new MediaPlayer();
            mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        }
        mViewController.onSeekChange(0);
    }


    @Override
    public void stop() {
        Log.i(TAG,"停止播放");
        if (mPlayer != null&&mPlayer.isPlaying()) {
            mPlayer.stop();
            mCurrentState = PLAY_STATE_STOP;
            if (mViewController != null) {
                mViewController.onPlayStateChange(mCurrentState);
            }
            stopTimeTask();
            mPlayer.release();
            mPlayer = null;
        }
    }

    /**
     * 开启一个TimeTask
     */
    public void startTimeTask(){
        if (mTimer == null) {
            mTimer = new Timer();
        }
        if (mTimeTask == null) {
            mTimeTask = new SeekTimeTask();
        }
        mTimer.schedule(mTimeTask,0,50);
    }

    /**
     * 关闭TimeTask
     */
    public void stopTimeTask(){
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
        if (mTimeTask != null) {
            mTimeTask.cancel();
            mTimeTask = null;
        }
    }

    /**
     * 定义TImeTask，实时获取进度
     */
    class SeekTimeTask extends TimerTask{
        @Override
        public void run() {
            int currentPosition = (int) (100.1*mPlayer.getCurrentPosition()/mPlayer.getDuration());
            mViewController.onSeekChange(currentPosition);
        }
    }

    @Override
    public void seekTo(int seek) {
        Log.i(TAG,seek+"");
        if (mPlayer != null) {
            int tagSeek = (int) (seek*1.0/100*mPlayer.getDuration());
            mPlayer.seekTo(tagSeek);
        }
    }
}
