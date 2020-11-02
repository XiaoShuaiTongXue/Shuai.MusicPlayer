package com.shuai.musicplayer2.service;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

public class PlayerService extends Service {
    private static final String TAG = "player";
    public MediaPlayer mediaPlayer;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new MyBinder() ;
    }

    class MyBinder extends Binder {
        public void plays(String path){
            play(path);
        }
        public void pauses(){
            pause();
        }
        public void replays(String path){
            replay(path);
        }
        public void stops(){
            stop();
        }
        public int getCurrentPosition(){
            return getCurrentProgress();
        }
        public int getMusicWith(){
            return getMusicLength();
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    //播放音乐
    public void play(String path){
        try{
            if (mediaPlayer == null){
                //创建一个播放器
                mediaPlayer = new MediaPlayer();
                //指定播放器为音频文件
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                //指定播放路径
                mediaPlayer.setDataSource(path);
                //准备播放
                mediaPlayer.prepare();
                mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        //开始播放
                        mediaPlayer.start();
                    }
                });
            }
            else {
                //获取当前进度
                int position = getCurrentProgress();
                //从指定位置播放音频
                mediaPlayer.seekTo(position);
                mediaPlayer.prepare();
                mediaPlayer.start();
            }
        }catch (Exception e){
            Log.i(TAG,e.toString());
        }
    }

    //暂停音乐
    public void pause(){
        if (mediaPlayer!=null && mediaPlayer.isPlaying()){
            Toast.makeText(this, "音乐已暂停", Toast.LENGTH_SHORT).show();
            mediaPlayer.pause();
        }else if(mediaPlayer != null && (!mediaPlayer.isPlaying())){
            mediaPlayer.start();
        }
    }

    //重新播放音乐
    public void replay(String path){
        if(mediaPlayer != null){
            Toast.makeText(this, "重新播放音乐", Toast.LENGTH_SHORT).show();
            mediaPlayer.seekTo(0);
            try {
                mediaPlayer.prepare();
            }catch (Exception e){
                Log.i(TAG,e.toString());
            }
            mediaPlayer.start();
        }
    }

    //停止音乐
    public void stop(){
        if(mediaPlayer != null){
            Toast.makeText(this, "停止播放", Toast.LENGTH_SHORT).show();
            mediaPlayer.stop();
            //释放资源
            mediaPlayer.release();
            mediaPlayer = null;
        }else {
            Toast.makeText(this, "已经停止音乐", Toast.LENGTH_SHORT).show();
        }
    }

    //获取资源文件长度
    public int getMusicLength(){
        if(mediaPlayer!=null){
            return mediaPlayer.getDuration();
        }
        return 0;
    }

    //获取当前进度
    public int getCurrentProgress(){
        if(mediaPlayer != null ){
            return mediaPlayer.getCurrentPosition();
        }
        return 0;
    }

    @Override
    public void onDestroy() {
        stop();
        super.onDestroy();
    }
}
