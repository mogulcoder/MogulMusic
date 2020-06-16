package com.mogul.lib_network.okhttp.response;

import android.os.Handler;
import android.os.Looper;

import com.mogul.lib_network.okhttp.listener.DisposeDataHandler;
import com.mogul.lib_network.okhttp.exception.OKHttpException;
import com.mogul.lib_network.okhttp.listener.DisposeDataListener;
import com.mogul.lib_network.okhttp.utils.ResponseEntityToModule;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;


public class CommonJsonCallback implements Callback {

    protected final String EMPTY_MSG = "";

    protected final int NETWORK_ERROR = -1;
    protected final int JSON_ERROR = -2;
    protected final int OTHER_ERROR = -3;

    private Class<?> mClass;
    private DisposeDataListener mListener;
    private Handler mHandler;

    public CommonJsonCallback(DisposeDataHandler handler) {
        this.mListener = handler.mListener;
        this.mClass = handler.mClass;
        this.mHandler = new Handler(Looper.getMainLooper());
    }

    @Override
    public void onFailure(Call call, final IOException e) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mListener.failure(new OKHttpException(NETWORK_ERROR, e));
            }
        });
    }

    @Override
    public void onResponse(Call call, Response response) throws IOException {
        final String result = response.body().string();
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                handleResponse(result);
            }
        });
    }

    /**
     * 处理响应结果
     */
    private void handleResponse(String result) {
        if (result == null || result.equals("")) {
            mListener.failure(new OKHttpException(NETWORK_ERROR, EMPTY_MSG));
            return;
        }
        try {
            if (mClass == null) {
                mListener.success(result);
            } else {
                Object object = ResponseEntityToModule.parseJsonToModule(result, mClass);
                if (object != null) {
                    mListener.success(object);
                } else {
                    mListener.failure(new OKHttpException(JSON_ERROR, EMPTY_MSG));
                }
            }
        } catch (Exception e) {
            mListener.failure(new OKHttpException(JSON_ERROR, EMPTY_MSG));
        }
    }
}