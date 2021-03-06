package com.example.myapplication;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.icu.util.TimeZone;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;
import java.math.BigDecimal;

import com.bumptech.glide.Glide;
import com.sackcentury.shinebuttonlib.ShineButton;

import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ViewPagerAdapter extends RecyclerView.Adapter<ViewPagerAdapter.ViewPagerViewHolder> {
    private Context mContext;
    private List<VedioInfo> list;
    private int list_size;
//    private  DownloadManager downloadManager;
//    private  long reference;
    public static int ItemCount=30;

    public ViewPagerAdapter(Context mContext, ViewPager2 viewPager2,List list,int list_size){
            this.mContext=mContext;
            this.list=list;
            this.list_size=list_size;
    }
    @Override
    public void onViewAttachedToWindow(@NonNull ViewPagerViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        Uri path=Uri.parse( list.get(holder.mPosition%list_size).getFeedurl());
        holder.mVideoview.setVideoURI(path);
        holder.iv_cover.setVisibility(View.VISIBLE);
    }

    @Override
    public void onViewDetachedFromWindow(@NonNull ViewPagerViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        holder.mVideoview.stopPlayback();
    }

    @NonNull
    @Override
    public ViewPagerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ViewPagerViewHolder holder = new ViewPagerViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_page, parent, false));
        return holder;
    }
    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(@NonNull final ViewPagerViewHolder holder, int position) {
//        Uri path=Uri.parse( list.get(position%list_size).getFeedurl());
//        holder.mVideoview.setVideoURI(path);

        // 创建手势检测器实例 & 传入OnGestureListener接口（需要复写对应方法）
        GestureDetector mGestureDetector = new GestureDetector(mContext,holder.onGestureListenernew );
        // 创建 & 设置OnDoubleTapListener接口实现类
        mGestureDetector.setOnDoubleTapListener(holder.onDoubleTapListenern);

        int likecount=list.get(position%list_size).getLikecount();
        //点赞数格式
        if(likecount<=999){
            holder.tv_likecount.setText(String.valueOf(likecount));
        }
        else if(likecount>999&&likecount<10000) {
            holder.tv_likecount.setText("999+");
        }
        else{
            float temp=(float) likecount/10000;
            BigDecimal bg = new BigDecimal(temp);
            holder.tv_likecount.setText(String.valueOf(bg.setScale(1,BigDecimal.ROUND_HALF_UP).doubleValue())+"w");
        }
        Glide.with(mContext).load(Uri.parse(list.get(position%list_size).getAvatar())).into(holder.iv_cover);
        holder.tv_description.setText(String.valueOf(list.get(position%list_size).getDescription())+"\n");
        holder.tv_nickname.setText("@"+String.valueOf(list.get(position%list_size).getNickname()));
        holder.mPosition = position;
        //绑定监听，必须在onBindViewHolder里绑定
        holder.mVideoview.setOnErrorListener(holder.mp_error_Listener);//监听器：不弹出播放失败对话框
        holder.mVideoview.setOnCompletionListener(holder.play_loop_Listen);//设置循环播放
        holder.mVideoview.setOnPreparedListener(holder.vedio_pre_listen);
        holder.mSeekBar.setOnSeekBarChangeListener(holder.seekBar_listen);//滑动过程处理，滑动条与播放进度textview 的交互
        String serviceString = Context.DOWNLOAD_SERVICE;
//        downloadManager = (DownloadManager)mContext.getSystemService(serviceString);

        //直接使用系统的下载管理器。是不是非常方便

        holder.bt_heart.setChecked(false);
        holder.mVideoview.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mGestureDetector.onTouchEvent(event);
                return true;
            }
        });//监听器：点击屏幕暂停播放  暂停图片展示Listen,双击点赞播放动画

    }
    @Override
    public int getItemCount() {
        return ItemCount;
    }

    @Override
    public void onViewRecycled(@NonNull ViewPagerViewHolder holder) {
        super.onViewRecycled(holder);
        holder.mVideoview.stopPlayback();
        holder.mVideoview.setOnPreparedListener(null);
        holder.mVideoview.setOnCompletionListener(null);
        holder.mVideoview.setOnErrorListener(null);
    }

    public static class ViewPagerViewHolder extends RecyclerView.ViewHolder {
        public MediaPlayer.OnErrorListener mp_error_Listener=new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                return false;
            }
        };//监听器：不弹出播放失败对话框
        public MediaPlayer.OnCompletionListener play_loop_Listen=new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mVideoview.start();
            }
        };//设置循环播放
        public SeekBar.OnSeekBarChangeListener seekBar_listen=new SeekBar.OnSeekBarChangeListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                //当进度条变化（手动滑动，或者通过handle改变）时，设置进度textview 的值
                    SimpleDateFormat dateFormat = new SimpleDateFormat("mm:ss", Locale.CHINA);
                    dateFormat.setTimeZone(TimeZone.getTimeZone("GMT+00:00"));
                    float seekbar_cur=(float)(seekBar.getProgress()*mVideoview.getDuration())/100;
                    mTv_progress.setText(dateFormat.format(new Date((int)seekbar_cur))+"/"+dateFormat.format(new Date(mVideoview.getDuration())));

            }
            //滑动开始时处理
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                //当手动开始滑动时，移除视频与进度条交互的handle
                handler.removeCallbacks(runnable);
                mTv_progress.setVisibility(View.VISIBLE);
            }
            //滑动条拖动结束时处理
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //当手动滑动结束时，恢复视频与进度条交互的handle，同时设置进度条为滑动后的位置
                mTv_progress.setVisibility(View.INVISIBLE);
                mVideoview.seekTo(seekBar.getProgress()*(int)(mVideoview.getDuration()/100f));
                handler.postDelayed(runnable,0);
            }
        };//滑动过程处理，滑动条与播放进度textview 的交互
        public MediaPlayer.OnSeekCompleteListener vedio_seek_comp_listen=new MediaPlayer.OnSeekCompleteListener(){

            @Override
            public void onSeekComplete(MediaPlayer mp) {
                mp.start();
                mImage_pause.setVisibility(View.INVISIBLE);
            }
        };




        public MediaPlayer.OnPreparedListener vedio_pre_listen=new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setOnSeekCompleteListener(vedio_seek_comp_listen);
            }
        };
        GestureDetector.OnGestureListener onGestureListenernew =new GestureDetector.OnGestureListener() {
            @Override
            public boolean onDown(MotionEvent e) {
                return false;
            }

            @Override
            public void onShowPress(MotionEvent e) {

            }

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return false;
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                return false;
            }

            @Override
            public void onLongPress(MotionEvent e) {

            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                return false;
            }

        };//onGestureListenernew初始化需要
        GestureDetector.OnDoubleTapListener onDoubleTapListenern= new GestureDetector.OnDoubleTapListener() {
            // 1. 单击事件
            // onSingleTapConfirmed：再次点击（即双击），则不会执行
            // onSingleTapUp：手抬起就会执行
            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                if(mVideoview.isPlaying()){
                    mVideoview.pause();
                    mImage_pause.setVisibility(View.VISIBLE);
                }
                else{
                    mVideoview.start();
                    mImage_pause.setVisibility(View.INVISIBLE);
                }
                return false;
            }
            // 2. 双击事件
            @Override
            public boolean onDoubleTap(MotionEvent e) {
                bt_heart.showAnim();
                bt_heart.setChecked(true);
                return false;
            }
            // 3. 双击间隔中发生的动作
            @Override
            public boolean onDoubleTapEvent(MotionEvent e) {
                return false;
            }
        };//单双击事件监听

        public int mPosition;
        public Button download;
        public VideoView mVideoview;
        public TextView mTv_progress;
        public SeekBar mSeekBar;
        public ImageView mImage_pause;
        public TextView tv_likecount;
        public TextView tv_nickname;
        private TextView tv_description;
        public RelativeLayout re_vedio;
        private ShineButton bt_heart;
        public ImageView iv_cover;
        //处理视频进度与进度条 进度文字的交互逻辑
        public Handler handler=new Handler();
        public  Runnable runnable=new Runnable() {
        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public void run() {
            if(mVideoview.isPlaying()){
                int current=mVideoview.getCurrentPosition();//获取当前播放进度
                int total=mVideoview.getDuration();         //获取总时间
                float progress=(float) current/total*100;   //计算当前播放百分比
                mSeekBar.setProgress((int)progress);      //设置进度条
                //格式转化，进度textview设置
                SimpleDateFormat dateFormat = new SimpleDateFormat("mm:ss", Locale.CHINA);
                dateFormat.setTimeZone(TimeZone.getTimeZone("GMT+00:00"));
                mTv_progress.setText(dateFormat.format(new Date(current))+"/"+dateFormat.format(new Date(total)));
            }
            handler.postDelayed(runnable,1000);//监听间隔1s
        }
    };
        public ViewPagerViewHolder(@NonNull View itemView) {
            super(itemView);
            download=itemView.findViewById(R.id.download);
            mVideoview = itemView.findViewById(R.id.videoView);
            mSeekBar=itemView.findViewById(R.id.seekBar);
            mTv_progress=itemView.findViewById(R.id.tv_progress);
            mImage_pause=itemView.findViewById(R.id.image_pause);
            tv_likecount=itemView.findViewById(R.id.tv_likecount);
            tv_description=itemView.findViewById(R.id.tv_description);
            tv_nickname=itemView.findViewById(R.id.tv_nickname);
            re_vedio=itemView.findViewById(R.id.re_video);
            bt_heart=itemView.findViewById(R.id.bt_heart);
            iv_cover=itemView.findViewById(R.id.IV_cover);
            itemView.setTag(this);//  在Mainactivitu可以用gettag找到当前item
        }
    }
}