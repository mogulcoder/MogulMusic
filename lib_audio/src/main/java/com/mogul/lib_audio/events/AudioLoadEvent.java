package com.mogul.lib_audio.events;

import com.mogul.lib_audio.model.AudioBean;

public class AudioLoadEvent {

    public AudioBean mAudioBean;

    public AudioLoadEvent(AudioBean audioBean) {
        mAudioBean = audioBean;
    }
}
