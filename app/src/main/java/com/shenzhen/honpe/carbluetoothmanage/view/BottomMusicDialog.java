package com.shenzhen.honpe.carbluetoothmanage.view;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.shenzhen.honpe.carbluetoothmanage.R;
import com.shenzhen.honpe.carbluetoothmanage.adapter.CenterMainAdapter;
import com.shenzhen.honpe.carbluetoothmanage.config.Music;
import com.shenzhen.honpe.carbluetoothmanage.interfaces.CommodityPresenterInf;
import com.shenzhen.honpe.carbluetoothmanage.util.DBUtils;
import com.shenzhen.honpe.carbluetoothmanage.util.music.BlurUtil;
import com.shenzhen.honpe.carbluetoothmanage.util.music.MergeImage;
import com.shenzhen.honpe.carbluetoothmanage.util.music.MusicUtil;
import com.shenzhen.honpe.carbluetoothmanage.view.BottomMenuDialog.BottomMenuDialog;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static android.content.Context.AUDIO_SERVICE;
import static com.shenzhen.honpe.carbluetoothmanage.config.DBData.KEY_SAVE_SONG;
import static com.shenzhen.honpe.carbluetoothmanage.config.DBData.KEY_isPlaying;
import static com.shenzhen.honpe.carbluetoothmanage.config.DBData.TABLE_SONG;

/**
 * FileName: BottomMusicDialog
 * Author: asus
 * Date: 2021/6/22 12:00
 * Description:
 */
@SuppressLint("ClickableViewAccessibility")
public class BottomMusicDialog implements CommodityPresenterInf, View.OnClickListener {
    /*** 引用上下文*/
    private AppCompatActivity activity;
    /*** 弹窗*/
    private MusicDialog mMusicDialog;
    /*** 弹窗布局*/
    private View contentView;
    private RelativeLayout reBg;
    private ImageView bgImgv;
    private TextView titleTv;
    private TextView artistTv;
    private TextView playCurrentProgress;
    private TextView totalTv;
    private ImageView prevImgv;
    private ImageView nextImgv;
    private int position;
    private ImageView discImagv;
    private ImageView needleImagv;
    private MediaPlayer mediaPlayer;
    private ImageView ivPlayStart;
    private ImageView downImg;
    private ImageView styleImg;
    private SeekBar playSeek;
    private ImageView icMenu;
    private AudioManager am;
    private LinearLayout llVolume;
    private TextView tvVolume;
    private SeekBar seekVolume;
    private ImageView ivVolume;
    //设置音乐播放模式
    private int i = 0;
    private int playMode = 0;
    private int buttonWitch = 0;
    private boolean isStop = false;
    private boolean isPlaying;
    private boolean isShowVolume;
    private int FACTOR = 100;
    int currentVolume,maxVolume;
    private CenterMainAdapter mAdapter;
    private ObjectAnimator objectAnimator = null;
    private RotateAnimation rotateAnimation = null;
    private RotateAnimation rotateAnimation2 = null;
    List<Music> songList;
    private static int[] playOrders = {R.mipmap.ic_play_btn_loop,R.mipmap.ic_play_btn_one,R.mipmap.ic_play_btn_shuffle};
    //Handler实现向主线程进行传值
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            super.handleMessage(msg);
            if (playSeek != null) {
                playSeek.setProgress((int) (msg.what));
            }
            if (playCurrentProgress != null) {
                playCurrentProgress.setText(formatTime(msg.what));
            }
        }
    };


    //创建一个类MusicThread实现Runnable接口，实现多线程
    class MusicThread implements Runnable {

        @Override
        public void run() {
            while (!isStop && songList.get(position) != null) {
                try {
                    //让线程睡眠1000毫秒
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                //放送给Handler现在的运行到的时间，进行ui更新
                handler.sendEmptyMessage(mediaPlayer.getCurrentPosition());

            }
        }
    }

    public BottomMusicDialog(AppCompatActivity mActivity) {
        this.activity = mActivity;
    }

    /**
     * 显示窗口
     */
    @Override
    public void showDialog() {
        mMusicDialog = new MusicDialog(activity, R.style.GoodDialog);
        mMusicDialog.outDuration(200);
        mMusicDialog.inDuration(200);
        //设置铺满
        mMusicDialog.heightParam(ViewGroup.LayoutParams.WRAP_CONTENT);
        //解析视图
        contentView = LayoutInflater.from(activity).inflate(R.layout.css_music, null);
        mMusicDialog.setContentView(contentView);
        initId(contentView);
        songList = MusicUtil.getAllMediaList(activity);
        mediaPlayer = new MediaPlayer();
        mMusicDialog.setCanceledOnTouchOutside(false);
        prevAndNextPlaying();
        initVoice();
        initDB();
        mMusicDialog.show();
    }

    private void initDB() {
        isPlaying = (boolean) DBUtils.get(activity, TABLE_SONG, KEY_isPlaying, false);
        position = (int) DBUtils.get(activity, TABLE_SONG, KEY_SAVE_SONG, position);
    }

    private void initVoice() {
        am = (AudioManager) activity.getSystemService(AUDIO_SERVICE);
         maxVolume = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        seekVolume.setMax(maxVolume);
        currentVolume = am.getStreamVolume(AudioManager.STREAM_MUSIC);
        seekVolume.setProgress(currentVolume);
        tvVolume.setText(currentVolume * 100 / maxVolume + "");
        if (currentVolume == 0) {
            ivVolume.setImageResource(R.drawable.ic_baseline_volume_off_24);
        }else {
            ivVolume.setImageResource(R.drawable.ic_baseline_volume_up_24);
        }
        seekVolume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                am.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
                int currentVolume = am.getStreamVolume(AudioManager.STREAM_MUSIC);
                seekBar.setProgress(currentVolume);
                tvVolume.setText(currentVolume * 100 / maxVolume + "");
                if (currentVolume == 0) {
                    ivVolume.setImageResource(R.drawable.ic_baseline_volume_off_24);
                }else {
                    ivVolume.setImageResource(R.drawable.ic_baseline_volume_up_24);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        playSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    mediaPlayer.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private void initId(View v) {
        bgImgv = v.findViewById(R.id.music_bg_imgv);
        titleTv = v.findViewById(R.id.music_title_tv);
        artistTv = v.findViewById(R.id.music_artist_tv);
        bgImgv = v.findViewById(R.id.music_bg_imgv);
        playCurrentProgress = v.findViewById(R.id.music_current_tv);
        totalTv = v.findViewById(R.id.music_total_tv);
        prevImgv = v.findViewById(R.id.music_prev_imgv);
        nextImgv = v.findViewById(R.id.music_next_imgv);
        discImagv = v.findViewById(R.id.music_disc_imagv);
        needleImagv = v.findViewById(R.id.music_needle_imag);
        ivPlayStart = v.findViewById(R.id.music_pause_imgv);
        downImg = v.findViewById(R.id.music_down_imgv);
        playSeek = v.findViewById(R.id.music_seekbar);
        styleImg = v.findViewById(R.id.music_play_btn_loop_img);
        icMenu = v.findViewById(R.id.ic_menu);
        reBg = v.findViewById(R.id.re_bg);
        llVolume = v.findViewById(R.id.ll_volume);
        tvVolume = v.findViewById(R.id.tv_volume);
        seekVolume = v.findViewById(R.id.video_volume);
        ivVolume = v.findViewById(R.id.iv_volume);
        ivPlayStart.setOnClickListener(this);
        prevImgv.setOnClickListener(this);
        nextImgv.setOnClickListener(this);
        downImg.setOnClickListener(this);
        styleImg.setOnClickListener(this);
        icMenu.setOnClickListener(this);
        reBg.setOnClickListener(this);
        ivVolume.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.music_play_btn_loop_img:
                playMode++;
                if (playMode == 3) {
                    playMode = 0;
                }
                styleImg.setImageResource(playOrders[playMode]);

                break;
            case R.id.music_prev_imgv:
                buttonWitch = 1;
                setBtnMode();
                break;
            case R.id.music_pause_imgv:
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                    ivPlayStart.setImageResource(R.mipmap.ic_play_btn_play);
                } else {
                    mediaPlayer.start();
                    ivPlayStart.setImageResource(R.mipmap.ic_play_btn_pause);
                }
                DBUtils.put(activity,TABLE_SONG,KEY_SAVE_SONG,position);
                break;
            case R.id.music_next_imgv:
                buttonWitch = 2;
                setBtnMode();
                break;
            case R.id.ic_menu:
                BottomMenuDialog dialog = new BottomMenuDialog(activity, songList);
                dialog.showDialog();
                dialog.setOnClickNextPlaying(new BottomMenuDialog.PreAndPlaying() {
                    @Override
                    public void next(int pos,CenterMainAdapter adapter) {
                        mediaPlayer.reset();
                        position = pos;
                        mAdapter = adapter;
                        adapter.updateData(songList);
                        prevAndNextPlaying();
                        DBUtils.put(activity,TABLE_SONG,KEY_SAVE_SONG,position);
                    }
                });
                break;
            case R.id.re_bg:
                if (isShowVolume) {
                    llVolume.setVisibility(View.VISIBLE);
                }else {
                    llVolume.setVisibility(View.GONE);
                }
                isShowVolume = !isShowVolume;
                break;
            case R.id.music_down_imgv:
                mMusicDialog.dismiss();
                break;
        }
    }

    //prevAndnext() 实现页面的展现
    private void prevAndNextPlaying() {
        isStop = false;
        mediaPlayer.reset();
        setDataPlaying();
        titleTv.setText(songList.get(position).title);
        artistTv.setText(songList.get(position).artist + "--" + songList.get(position).album);
        ivPlayStart.setImageResource(R.mipmap.ic_play_btn_pause);

        if (songList.get(position).albumBip != null) {
            Bitmap bgbm = BlurUtil.doBlur(songList.get(position).albumBip, 10, 5);//将专辑虚化
            bgImgv.setImageBitmap(bgbm);                                    //设置虚化后的专辑图片为背景
            //   Bitmap bitmap1 = BitmapFactory.decodeResource(mMusicDialog.getContext().getResources(), R.mipmap.play_page_disc);//BitmapFactory.decodeResource用于根据给定的资源ID从指定的资源文件中解析、创建Bitmap对象。
            Bitmap bm = MergeImage.mergeThumbnailBitmap(songList.get(position).albumBip, songList.get(position).albumBip);//将专辑图片放到圆盘中
            discImagv.setImageBitmap(bm);
        } else {
            Bitmap bitmap = BitmapFactory.decodeResource(mMusicDialog.getContext().getResources(), R.mipmap.bei);
            bgImgv.setImageBitmap(bitmap);
            //    Bitmap bitmap1 = BitmapFactory.decodeResource(mMusicDialog.getContext().getResources(), R.mipmap.play_page_disc);
            Bitmap bm = MergeImage.mergeThumbnailBitmap(songList.get(position).albumBip, songList.get(position).albumBip);//将专辑图片放到圆盘中
            discImagv.setImageBitmap(bm);
        }
        try {
            mediaPlayer.setDataSource(songList.get(position).path);
            mediaPlayer.prepare();                   // 准备
            mediaPlayer.start();                        // 启动
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                @Override
                public void onCompletion(MediaPlayer mp) {
                    if (!mediaPlayer.isPlaying()) {
                        setPlayMode();
                    }
                }
            });
        } catch (IllegalArgumentException | SecurityException | IllegalStateException
                | IOException e) {
            e.printStackTrace();
        }
        totalTv.setText(formatTime(songList.get(position).length));
        playSeek.setMax(songList.get(position).length);

        MusicThread musicThread = new MusicThread();                                         //启动线程
        new Thread(musicThread).start();

    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void setPlayMode() {
        if (playMode == 0)//全部循环
        {
            if (position == songList.size() - 1)//默认循环播放
            {
                position = 0;// 第一首
                mediaPlayer.reset();
                prevAndNextPlaying();

            } else {
                position++;
                mediaPlayer.reset();
                prevAndNextPlaying();
            }
        } else if (playMode == 1)//单曲循环
        {
            //position不需要更改
            mediaPlayer.reset();
            prevAndNextPlaying();
        } else if (playMode == 2)//随机
        {
            position = (int) (Math.random() * songList.size());//随机播放
            mediaPlayer.reset();
            prevAndNextPlaying();
        }
    }

    //格式化数字
    private String formatTime(int length) {
        Date date = new Date(length);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("mm:ss");    //规定固定的格式
        String totaltime = simpleDateFormat.format(date);
        return totaltime;
    }


    private void setBtnMode() {
        if (playMode == 0) { //列表循环
            if (position == songList.size() - 1) {//默认循环播放
                if (buttonWitch == 1) { //上一首
                    position--;

                } else if (buttonWitch == 2) { //下一首
                    position = 0;
                }
            } else if (position == 0) {
                if (buttonWitch == 1) {
                    position = songList.size() - 1;
                } else if (buttonWitch == 2) {
                    position++;
                }
            } else {
                if (buttonWitch == 1) {
                    position--;
                } else if (buttonWitch == 2) {
                    position++;
                }
            }
            mediaPlayer.reset();
            prevAndNextPlaying();
        } else if (playMode == 1) { //单曲循环
            mediaPlayer.reset();  //position不需要更改
            prevAndNextPlaying();
        } else if (playMode == 2) {
            position = (int) (Math.random() * songList.size());//随机播放
            mediaPlayer.reset();
            prevAndNextPlaying();
        }
    }

    private void setDataPlaying() {
        for (Music song : songList) {
            song.isPlaying=false;
        }
        songList.get(position).isPlaying=true;

    }
}







