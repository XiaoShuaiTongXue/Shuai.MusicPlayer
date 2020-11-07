package com.shuai.musicplayer2.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;

import com.shuai.musicplayer2.domain.MusicListInfo;

public class LikeUpdate {
    public void likeAdd(Context context, int position){
        String like_id = GetMenuList.sMusicListInfo.get(position).getId();
        SqlHelper helper = new SqlHelper(context);
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("like_id",like_id);
        long l =db.insert("like_info",null,contentValues);
        if(l == -1){
            Toast.makeText(context, "该歌曲已经添加", Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(context, "加入喜欢成功", Toast.LENGTH_SHORT).show();
        }
        db.close();
    }
    public void likeDelete(Context context, int position){
        String like_id = GetMenuList.sMusicListInfo.get(position).getId();
        SqlHelper helper = new SqlHelper(context);
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("like_id",like_id);
        int i = db.delete("like_id","_id = ?",new String[]{like_id});
        if(i == 0){
            Toast.makeText(context,"删除失败",Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(context,"该音乐已从你的喜欢中删除"+i,Toast.LENGTH_SHORT).show();
        }
        db.close();
    }
}
