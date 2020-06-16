package com.mogul.lib_audio.core;

import android.media.MediaPlayer;

import java.io.IOException;

/**
 * 带状态的MediaPlayer
 */
public class CustomMediaPlayer extends MediaPlayer implements MediaPlayer.OnCompletionListener {

    public enum Status {
        IDL, INITIALIZED, STARTED, PAUSED, STOPPED, COMPLETED
    }

    private Status status = Status.IDL;
    private OnCompletionListener mCompletionListener;

    public CustomMediaPlayer() {
        super();
        this.status = Status.IDL;
        super.setOnCompletionListener(this);
    }


    @Override
    public void reset() {
        super.reset();
        this.status = Status.IDL;
    }

    @Override
    public void setDataSource(String path) throws IOException, IllegalArgumentException, IllegalStateException, SecurityException {
        super.setDataSource(path);
        this.status = Status.INITIALIZED;
    }

    @Override
    public void start() throws IllegalStateException {
        super.start();
        this.status = Status.STARTED;
    }

    @Override
    public void pause() throws IllegalStateException {
        super.pause();
        this.status = Status.PAUSED;
    }

    @Override
    public void stop() throws IllegalStateException {
        super.stop();
        this.status = Status.STOPPED;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        this.status = Status.COMPLETED;

    }

    public Status getStatus() {
        return status;
    }


    /**
     * 判断是否完成状态
     *
     * @return
     */
    public boolean isCompleted() {
        return status == Status.COMPLETED;
    }


    public void setCompletionListener(OnCompletionListener completionListener) {
        this.mCompletionListener = completionListener;
    }

}
