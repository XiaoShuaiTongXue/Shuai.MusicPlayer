package com.shuai.musicplayer2.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.shuai.musicplayer2.R;
import com.shuai.musicplayer2.domain.MusicList;

import java.util.ArrayList;
import java.util.List;

public class MusicListAdapter extends RecyclerView.Adapter<MusicListAdapter.InnerHolder>{

    List<MusicList.ResultBean.SongsBean> mSongsBeans = new ArrayList<>();
    private  View mItemView;
    private OnMusicClickListener mMusicClickListener;

    @NonNull
    @Override
    public InnerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_muiclist, null);
        return new InnerHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InnerHolder holder, int position) {
        TextView tv_title = mItemView.findViewById(R.id.music_title);
        TextView tv_alias = mItemView.findViewById(R.id.music_alias);
        TextView tv_album = mItemView.findViewById(R.id.music_album);
        TextView tv_artists = mItemView.findViewById(R.id.music_artists);
        Button btn_mv = mItemView.findViewById(R.id.music_mv);
        MusicList.ResultBean.SongsBean songsBean = mSongsBeans.get(position);
        tv_title.setText(songsBean.getName());
        String mArtist = "";
        //处理多个作者
        for (MusicList.ResultBean.SongsBean.ArtistsBean artist: songsBean.getArtists()) {
            mArtist += artist.getName();
        }
        tv_artists.setText(mArtist);
        tv_album.setText("-"+songsBean.getAlbum().getName());
        //处理多个备注
        String mAlias = "";
        for (String alias: songsBean.getAlias()) {
            mAlias += alias;
        }
        tv_alias.setText(mAlias);
        if (songsBean.getMvid()==0){
            btn_mv.setVisibility(View.GONE);
        }
        holder.setMusicId(songsBean.getId()+"");
    }

    //设置点击事件

    public void setOnMusicClickListener(OnMusicClickListener listener){
        mMusicClickListener = listener;
    }

    public interface OnMusicClickListener{
        void onMusicClick(String musicId);
    }
    @Override
    public int getItemCount() {
        return 10;
    }

    public void setData(MusicList body) {
        mSongsBeans.clear();
        mSongsBeans.addAll(body.getResult().getSongs());
        notifyDataSetChanged();
    }


    public class InnerHolder extends RecyclerView.ViewHolder {
        private String mMusicId;
        public InnerHolder(@NonNull View itemView) {
            super(itemView);
            mItemView = itemView;
            mItemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mMusicClickListener != null){
                        mMusicClickListener.onMusicClick(mMusicId);
                    }
                }
            });
        }
        public void setMusicId(String musicId){
            mMusicId = musicId;

        }
    }
}
