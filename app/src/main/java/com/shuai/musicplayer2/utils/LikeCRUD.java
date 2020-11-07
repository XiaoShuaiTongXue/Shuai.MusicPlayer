package com.shuai.musicplayer2.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class LikeCRUD {

    private static final String TAG = "LikeCRUD";

    public void likeAdd(Context context, int position){
        String like_id = MenuList.sMusicListInfo.get(position).getId();
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
        String like_id = MenuList.sMusicListInfo.get(position).getId();
        SqlHelper helper = new SqlHelper(context);
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("like_id",like_id);
        int i = db.delete("like_info","_id = ?",new String[]{like_id});
        if(i == 0){
            Toast.makeText(context,"删除失败",Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(context,"该音乐已从你的喜欢中删除"+i,Toast.LENGTH_SHORT).show();
        }
        db.close();
    }

    public void likeSelete(Context context,int count){
        SqlHelper helper = new SqlHelper(context);
        SQLiteDatabase db=helper.getReadableDatabase();
        List<String> likeIds = new ArrayList<String>();
        Cursor cs = db.query("like_info",null,null,null,null,null,null);
        while(cs.moveToNext()){
            String value = cs.getString(cs.getColumnIndex("like_id"));
            Log.i(TAG,"v"+value);
            likeIds.add(value);
        }
        cs.close();
        db.close();
        if(likeIds.size()<count){
            count = likeIds.size();
        }
        //new MenuList(likeIds,count);
    }
}
