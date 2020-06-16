package com.mogul.lib_audio.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mogul.lib_audio.R;
import com.mogul.lib_audio.model.AudioBean;

import org.greenrobot.eventbus.EventBus;

public class BottomMusicView extends RelativeLayout {

    /**
     * view
     */
    private ImageView mLeftView;
    private TextView mTitleView;
    private TextView mAlbumView;
    private ImageView mRightView;
    private ImageView mPlayerView;

    //歌曲
    private AudioBean mAudioBean;

    private Context mContext;


    public BottomMusicView(Context context) {
        this(context, null);
    }

    public BottomMusicView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BottomMusicView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        EventBus.getDefault().register(this);
        initView();
    }

    private void initView() {
        View rootView = LayoutInflater.from(mContext).inflate(R.layout.bottom_view, this);

    }
}
