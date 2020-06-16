package com.mogul.lib_audio.app;

import android.content.Context;

public class AudioHelper {
    private static Context mContext;

    public static void init(Context context) {
        mContext = context;
    }

    public static Context getContext() {
        return mContext;
    }
}
