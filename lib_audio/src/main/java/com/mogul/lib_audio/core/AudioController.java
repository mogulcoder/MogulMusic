package com.mogul.lib_audio.core;

import com.mogul.lib_audio.events.AudioPlayModeEvent;
import com.mogul.lib_audio.model.AudioBean;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Random;

/**
 * 播放器核心控制类
 */
public class AudioController {

    public enum PlayMode {
        LOOP, //列表循环
        RANDOM, //随机播放
        REPEAT //单曲循环
    }

    private static volatile AudioController instance;
    private AudioPlayer mAudioPlayer; //播放器
    private ArrayList<AudioBean> mQueue; //播放队列
    private PlayMode mPlayMode; //播放模式
    private int mQueueIndex; //当前播放索引

    private AudioController() {
        mAudioPlayer = new AudioPlayer();
        mQueue = new ArrayList<>();
        mPlayMode = PlayMode.LOOP;
        mQueueIndex = 0;
    }

    public static AudioController getInstance() {
        if (instance == null) {
            synchronized (AudioController.class) {
                if (instance == null) {
                    instance = new AudioController();
                }
            }
        }
        return instance;
    }

    /**
     * 加入指定位置的一首歌曲
     *
     * @param index
     * @param bean
     */
    public void addAudio(int index, AudioBean bean) {
        if (mQueue == null) {
            throw new RuntimeException("当前播放队列为空");
        }
        int query = queryAudio(bean);
        if (query <= -1) {
            //没有添加过，就往集合中添加
            addCustomAudio(index, bean);
            //然后设置这首歌播放
            setPLayIndex(index);
        } else { //添加过
            AudioBean currentBean = getNowPlaying();
            //且不在播放中
            if (!bean.id.equals(currentBean.id)) {
                //设置这首歌播放
                setPLayIndex(query);
            }
        }
    }

    public void addAudio(AudioBean bean) {
        this.addAudio(0, bean);
    }




    /**
     * 设置播放队列
     *
     * @return
     */
    public ArrayList<AudioBean> getQueue() {
        return mQueue == null ? new ArrayList<AudioBean>() : mQueue;
    }

    //默认播放第一首歌曲
    public void setQueue(ArrayList<AudioBean> queue) {
        this.setQueue(queue, 0);
    }

    public void setQueue(ArrayList<AudioBean> queue, int queueIndex) {
        mQueue.addAll(queue);
        this.mQueueIndex = queueIndex;
    }

    /**
     * 设置播放模式
     *
     * @return
     */
    public PlayMode getPlayMode() {
        return mPlayMode;
    }

    public void setPlayMode(PlayMode playMode) {
        this.mPlayMode = playMode;
        //对外发送播放模式事件
        EventBus.getDefault().post(new AudioPlayModeEvent(mPlayMode));
    }

    /**
     * 指定某首歌曲播放
     *
     * @param index
     */
    public void setPLayIndex(int index) {
        if (mQueue == null) {
            throw new RuntimeException("当前播放队列为空");
        }
        this.mQueueIndex = index;
        play();
    }


    /**
     * 播放下一首歌曲
     */
    public void next() {
        AudioBean bean = getNextPlaying();
        mAudioPlayer.load(bean);
    }

    /**
     * 播放上一首歌曲
     */
    public void previous() {
        AudioBean bean = getPreviousPlaying();
        mAudioPlayer.load(bean);
    }

    /**
     * 切换播放/暂停
     */
    public void playOrPause() {
        if (isStartState()) {
            pause();
        } else if (isPauseState()) {
            resume();
        }
    }

    /**
     * 获取当前播放歌曲的索引
     *
     * @return
     */
    public int getQueueIndex() {
        return mQueueIndex;
    }

    /**
     * 对外提供是否播放状态
     *
     * @return
     */
    public boolean isStartState() {
        return CustomMediaPlayer.Status.STARTED == getStatus();
    }

    /**
     * 对外提供是否暂停状态
     *
     * @return
     */
    public boolean isPauseState() {
        return CustomMediaPlayer.Status.PAUSED == getStatus();
    }


    /**
     * 获取播放状态
     *
     * @return
     */
    private CustomMediaPlayer.Status getStatus() {
        return mAudioPlayer.getStatus();
    }


    /**
     * 获取当前播放的歌曲对象
     *
     * @return
     */
    private AudioBean getNowPlaying() {
        return getPlaying();
    }

    private AudioBean getPlaying() {
        if (mQueue != null && !mQueue.isEmpty() && mQueueIndex >= 0 && mQueueIndex < mQueue.size()) {
            return mQueue.get(mQueueIndex);
        }
        throw new RuntimeException("当前播放队列为空,请先设置播放队列");
    }


    /**
     * 获取下一首歌曲对象
     *
     * @return
     */
    private AudioBean getNextPlaying() {
        switch (mPlayMode) {
            case LOOP:
                mQueueIndex = (mQueueIndex + 1) % mQueue.size();
                break;
            case RANDOM:
                mQueueIndex = new Random().nextInt(mQueue.size()) % mQueue.size();
                break;
            case REPEAT:
                break;
        }
        return getPlaying();
    }


    /**
     * 获取上一首歌曲对象
     *
     * @return
     */
    private AudioBean getPreviousPlaying() {
        switch (mPlayMode) {
            case LOOP:
                mQueueIndex = (mQueueIndex - 1) % mQueue.size();
                break;
            case RANDOM:
                mQueueIndex = new Random().nextInt(mQueue.size()) % mQueue.size();
                break;
            case REPEAT:
                break;
        }
        return getPlaying();
    }

    /**
     * 对外提供播放方法
     */
    private void play() {
        AudioBean bean = getNowPlaying();
        mAudioPlayer.load(bean);
    }

    /**
     * 对外提供暂停方法
     */
    private void pause() {
        mAudioPlayer.pause();
    }

    /**
     * 对外提供恢复播放方法
     */
    private void resume() {
        mAudioPlayer.resume();
    }

    /**
     * 对外提供释放资源方法
     */
    private void release() {
        mAudioPlayer.release();
        EventBus.getDefault().register(this);
    }

    /**
     * 添加到歌曲集合
     * @param index
     * @param bean
     */
    private void addCustomAudio(int index, AudioBean bean) {
        if (mQueue == null) {
            throw new RuntimeException("当前播放队列为空,请先设置播放队列.");
        }
        mQueue.add(index, bean);
    }

    /**
     * 查询歌曲是否在队列中
     *
     * @return
     */
    private int queryAudio(AudioBean bean) {
        return mQueue.indexOf(bean);
    }
}