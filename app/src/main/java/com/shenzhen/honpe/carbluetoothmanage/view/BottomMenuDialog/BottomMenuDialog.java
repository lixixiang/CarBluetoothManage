package com.shenzhen.honpe.carbluetoothmanage.view.BottomMenuDialog;

import android.media.MediaPlayer;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.shenzhen.honpe.carbluetoothmanage.R;
import com.shenzhen.honpe.carbluetoothmanage.adapter.CenterMainAdapter;
import com.shenzhen.honpe.carbluetoothmanage.config.Music;
import com.shenzhen.honpe.carbluetoothmanage.interfaces.CommodityPresenterInf;
import com.shenzhen.honpe.carbluetoothmanage.util.DBUtils;
import com.shenzhen.honpe.carbluetoothmanage.util.ScreenAdapterUtils;
import com.shenzhen.honpe.carbluetoothmanage.view.MusicDialog;

import java.util.List;

import static com.shenzhen.honpe.carbluetoothmanage.config.DBData.KEY_SAVE_SONG;
import static com.shenzhen.honpe.carbluetoothmanage.config.DBData.TABLE_SONG;

/**
 * FileName: BottomMenuDialog
 * Author: asus
 * Date: 2021/6/22 13:52
 * Description:
 */
public class BottomMenuDialog implements CommodityPresenterInf {
    private AppCompatActivity activity;
    private MusicDialog mMusicDialog;
    private View contentView;
    private RecyclerView recyclerView;
    private CenterMainAdapter mAdapter;
    List<Music> songList;
    public BottomMenuDialog(AppCompatActivity activity, List<Music> songList) {
        this.activity = activity;
        this.songList = songList;
    }

    /**
     * 显示窗口
     */
    @Override
    public void showDialog() {
        mMusicDialog = new MusicDialog(activity, R.style.GoodDialog);
        mMusicDialog.outDuration(200);
        mMusicDialog.inDuration(200);
       int height = ScreenAdapterUtils.getScreenHeight(activity);
        //设置铺满
        mMusicDialog.heightParam(height/2);
        //解析视图
        contentView = LayoutInflater.from(activity).inflate(R.layout.css_menu, null);
        mMusicDialog.setContentView(contentView);
        intId();
        mMusicDialog.show();
    }

    private void intId() {
        recyclerView = contentView.findViewById(R.id.recycler_song);
        recyclerView.setLayoutManager(new LinearLayoutManager(activity));
        mAdapter = new CenterMainAdapter(songList);
        recyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int pos) {

                if (preAndPlaying != null) {
                    preAndPlaying.next(pos,mAdapter);
                }
                mMusicDialog.dismiss();
            }
        });
    }

    PreAndPlaying preAndPlaying;

    public void setOnClickNextPlaying(PreAndPlaying onClickNextPlaying){
        this.preAndPlaying = onClickNextPlaying;
    }

    public interface PreAndPlaying{
        void next(int pos,CenterMainAdapter adapter);
    }

}




























