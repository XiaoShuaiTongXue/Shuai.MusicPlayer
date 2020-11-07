package com.shuai.musicplayer2.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.shuai.musicplayer2.R;
import com.shuai.musicplayer2.utils.GetMenuList;

import java.util.ArrayList;
import java.util.List;

public class MusicListAdapter extends RecyclerView.Adapter<MusicListAdapter.InnerHolder>{

    List<com.shuai.musicplayer2.domain.MusicListInfo> mMusicListInfo = new ArrayList<com.shuai.musicplayer2.domain.MusicListInfo>();
    private  View mItemView;
    private OnMusicClickListener mMusicClickListener;
    private Context mContext;


    @NonNull
    @Override
    public InnerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (mContext == null) {
            mContext = parent.getContext().getApplicationContext();
        }
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_muiclist, null);
        return new InnerHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InnerHolder holder, int position) {
        com.shuai.musicplayer2.domain.MusicListInfo musicListInfo = mMusicListInfo.get(position);
        TextView tv_title = mItemView.findViewById(R.id.music_title);
        TextView tv_alias = mItemView.findViewById(R.id.music_alias);
        TextView tv_album = mItemView.findViewById(R.id.music_album);
        TextView tv_artists = mItemView.findViewById(R.id.music_artists);
        Button btn_mv = mItemView.findViewById(R.id.music_mv);
        ImageView iv_pic = mItemView.findViewById(R.id.music_pic);
        tv_title.setText(musicListInfo.getName());
        tv_alias.setText(musicListInfo.getAlia());
        tv_album.setText(musicListInfo.getAlbumName());
        tv_artists.setText(musicListInfo.getArtistsName());
        if (musicListInfo.getMvid()==0){
            btn_mv.setVisibility(View.GONE);
        }
        Glide.with(mContext)
                .load(musicListInfo.getPicUrl())
                .thumbnail(0.1f)
                .into(iv_pic);
        holder.setPosition(position);
    }


    //设置点击事件

    public void setOnMusicClickListener(OnMusicClickListener listener){
        mMusicClickListener = listener;
    }

    public interface OnMusicClickListener{
        void onMusicClick(int position);
    }
    @Override
    public int getItemCount() {
        return GetMenuList.sCount;
    }

    /**
     * 设置数据
     */
    public void setData() {
        mMusicListInfo.clear();
        mMusicListInfo.addAll(GetMenuList.sMusicListInfo);
        notifyDataSetChanged();
    }


    public class InnerHolder extends RecyclerView.ViewHolder {
        private int mPosition;

        public InnerHolder(@NonNull View itemView) {
            super(itemView);
            mItemView = itemView;
            mItemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mMusicClickListener != null){
                        mMusicClickListener.onMusicClick(mPosition);
                    }
                }
            });
        }
        public void setPosition(int position){
            mPosition = position;
        }
    }
}
