package com.shenzhen.honpe.carbluetoothmanage.adapter;

import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.shenzhen.honpe.carbluetoothmanage.R;
import com.shenzhen.honpe.carbluetoothmanage.config.Music;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * FileName: CenterMainAdapter
 * Author: asus
 * Date: 2021/6/22 14:11
 * Description:
 */
public class CenterMainAdapter extends BaseQuickAdapter<Music, BaseViewHolder> {
    List<Music> data;
    ImageView imageView;
    public CenterMainAdapter(@Nullable List<Music> data) {
        super(R.layout.item_play_list_song, data);
        this.data = data;
    }

    public void updateData(List<Music> data){
        this.data = data;
        notifyDataSetChanged();
    }

    @Override
    protected void convert(@NotNull BaseViewHolder holder, Music bean) {
        holder.setText(R.id.tv_id, holder.getAdapterPosition() + 1 + "");
        holder.setText(R.id.tv_song_name,bean.title +"\n"+bean.album);
        holder.setText(R.id.tv_author, bean.artist);
        holder.setText(R.id.tv_play_time_long, "00:00"+"/"+bean.length);
        imageView = holder.getView(R.id.iv_play_pause);
        imageView.setColorFilter(getContext().getResources().getColor(R.color.white));

        if (bean.isPlaying) {
            holder.setGone(R.id.tv_play_time_long, true);
            holder.setGone(R.id.iv_play_pause, false);
            holder.setBackgroundResource(R.id.re_list_bg, R.color.color_select_blue);
        }else {
            holder.setGone(R.id.tv_play_time_long, false);
            holder.setGone(R.id.iv_play_pause, true);
            holder.setBackgroundResource(R.id.re_list_bg, R.color.alpha_black_50);
        }
    }
}
