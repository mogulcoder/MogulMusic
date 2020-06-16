package com.mogul.lib_network.okhttp.listener;

public class DisposeDataHandler {

    public DisposeDataListener mListener;
    public Class<?> mClass;
    public String mSource;

    public DisposeDataHandler(DisposeDataListener listener) {
        mListener = listener;
    }

    public DisposeDataHandler(DisposeDataListener listener, Class<?> aClass) {
        mListener = listener;
        mClass = aClass;
    }

    public DisposeDataHandler(DisposeDataListener listener, String source) {
        mListener = listener;
        mSource = source;
    }
}