package com.mogul.lib_audio.core;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import android.util.Log;

import androidx.annotation.NonNull;

import com.mogul.lib_audio.app.AudioHelper;
import com.mogul.lib_audio.events.AudioCompleteEvent;
import com.mogul.lib_audio.events.AudioErrorEvent;
import com.mogul.lib_audio.events.AudioLoadEvent;
import com.mogul.lib_audio.events.AudioPauseEvent;
import com.mogul.lib_audio.events.AudioReleaseEvent;
import com.mogul.lib_audio.events.AudioStartEvent;
import com.mogul.lib_audio.model.AudioBean;

import org.greenrobot.eventbus.EventBus;

/**
 * 播放音频，对外发送事件
 */
public class AudioPlayer implements MediaPlayer.OnCompletionListener, MediaPlayer.OnBufferingUpdateListener, MediaPlayer.OnPreparedListener,
        MediaPlayer.OnErrorListener, AudioFocusManager.AudioFocusListener {
    private static final String TAG = "AudioPlayer";
    private static final int TIME_MSG = 0x01;
    private static final int TIME_INVAL = 100;

    private CustomMediaPlayer mMediaPlayer;
    private WifiManager.WifiLock mWifiLock;
    private AudioFocusManager mAudioFocusManager;
    private boolean isPauseByFocusLossTransient;


    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            if (msg.what == TIME_MSG) {

            }
        }
    };

    public AudioPlayer() {
        init();
    }

    /**
     * 初始化
     */
    private void init() {
        mMediaPlayer = new CustomMediaPlayer();
        mMediaPlayer.setWakeMode(null, PowerManager.PARTIAL_WAKE_LOCK);
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mMediaPlayer.setOnCompletionListener(this);
        mMediaPlayer.setOnPreparedListener(this);
        mMediaPlayer.setOnBufferingUpdateListener(this);
        mMediaPlayer.setOnErrorListener(this);

        // 初始化wifi锁
        mWifiLock = ((WifiManager) AudioHelper.getContext()
                .getApplicationContext()
                .getSystemService(Context.WIFI_SERVICE)).createWifiLock(WifiManager.WIFI_MODE_FULL, TAG);

        // 初始化音频焦点管理器
        mAudioFocusManager = new AudioFocusManager(AudioHelper.getContext(), this);
    }

    /**
     * 设置音量
     *
     * @param leftVol  左声道
     * @param rightVol 右声道
     */
    private void setVolume(float leftVol, float rightVol) {
        if (mMediaPlayer != null) {
            mMediaPlayer.setVolume(leftVol, rightVol);
        }
    }

    /**
     * 开始播放
     */
    private void start() {
        if (!mAudioFocusManager.requestAudioFocus()) {
            Log.e(TAG, "获取音频焦点失败");
        }
        mMediaPlayer.start();
        mWifiLock.acquire();
        //对外发送start事件
        EventBus.getDefault().post(new AudioStartEvent());
    }

    /**
     * 对外提供加载音频的方法
     *
     * @param audioBean
     */
    public void load(AudioBean audioBean) {
        try {
            //正常逻辑
            mMediaPlayer.reset();
            mMediaPlayer.setDataSource(audioBean.mUrl);
            mMediaPlayer.prepareAsync();
            //发送load事件
            EventBus.getDefault().post(new AudioLoadEvent(audioBean));
        } catch (Exception e) {
            //发送error事件
            EventBus.getDefault().post(new AudioErrorEvent());
        }
    }


    /**
     * 获取播放器当前状态
     *
     * @return
     */
    public CustomMediaPlayer.Status getStatus() {
        if (mMediaPlayer != null) {
            return mMediaPlayer.getStatus();
        }
        return CustomMediaPlayer.Status.STOPPED;
    }

    /**
     * 对外提供暂停功能
     */
    public void pause() {
        if (getStatus() == CustomMediaPlayer.Status.STARTED) {
            mMediaPlayer.pause();
            //释放音频焦点WiFiLock
            if (mWifiLock.isHeld()) {
                mWifiLock.release();
            }
            //释放音频焦点
            if (mAudioFocusManager != null) {
                mAudioFocusManager.abandonAudioFocus();
            }
            //发送暂停事件通知更新UI
            EventBus.getDefault().post(new AudioPauseEvent());
        }
    }

    /**
     * 对外提供恢复功能
     */
    public void resume() {
        if (getStatus() == CustomMediaPlayer.Status.PAUSED) {
            start();
        }
    }

    /**
     * 释放播放器资源
     */
    public void release() {
        if (mMediaPlayer == null) {
            return;
        }
        mMediaPlayer.release();
        mMediaPlayer = null;
        if (mAudioFocusManager != null) {
            mAudioFocusManager.abandonAudioFocus();
            mAudioFocusManager = null;
        }
        if (mWifiLock.isHeld()) {
            mWifiLock.release();
        }
        mWifiLock = null;
        mAudioFocusManager = null;

        //对外发送release销毁事件
        EventBus.getDefault().post(new AudioReleaseEvent());
    }


    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        //缓存进度回调
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        //播放完成回调
        EventBus.getDefault().post(new AudioCompleteEvent());
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        //播放出错回调
        EventBus.getDefault().post(new AudioErrorEvent());
        return true;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        //资源准备完毕
        start();
    }

    @Override
    public void audioFocusGrant() {
        //再次获得音频焦点
        setVolume(1.0f, 1.0f);
        if (isPauseByFocusLossTransient) {
            resume();
        }
        isPauseByFocusLossTransient = false;
    }

    @Override
    public void audioFocusLoss() {
        //永久失去焦点
        pause();
    }

    @Override
    public void audioFocusLossTransient() {
        //短暂性失去焦点
        pause();
        isPauseByFocusLossTransient = true;
    }

    @Override
    public void audioFocusLossDuck() {
        //瞬间失去焦点
        setVolume(0.5f, 0.5f);
    }
}