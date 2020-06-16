package com.mogul.lib_network.okhttp.response;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.NonNull;

import com.mogul.lib_network.okhttp.exception.OKHttpException;
import com.mogul.lib_network.okhttp.listener.DisposeDataHandler;
import com.mogul.lib_network.okhttp.listener.DisposeDownloadListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;


public class CommonFileCallback implements Callback {

    protected final int NETWORK_ERROR = -1;
    protected final int IO_ERROR = -2;
    protected final String EMPTY_MSG = "";

    private static final int PROGRESS_MESSAGE = 0x01;
    private Handler mHandler;
    private DisposeDownloadListener mListener;

    private int mProgress; //文件下载进度
    private String mFilePath; //文件保存路径

    public CommonFileCallback(DisposeDataHandler handler) {
        this.mListener = (DisposeDownloadListener) handler.mListener;
        this.mFilePath = handler.mSource;
        this.mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                if (msg.what == PROGRESS_MESSAGE) {
                    mListener.onProgress((Integer) msg.obj);
                }
            }
        };
    }

    @Override
    public void onFailure(Call call, final IOException e) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mListener.onFailure(new OKHttpException(NETWORK_ERROR, e));
            }
        });
    }

    @Override
    public void onResponse(Call call, Response response) throws IOException {
        final File file = handleResponse(response);
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (file != null) {
                    mListener.onSuccess(file);
                } else {
                    mListener.onFailure(new OKHttpException(IO_ERROR, EMPTY_MSG));
                }
            }
        });
    }

    /**
     * 通过IO流获取文件
     *
     * @param response
     * @return
     */
    private File handleResponse(final Response response) {
        if (response == null) {
            return null;
        }
        InputStream inputStream = null;
        File file;
        FileOutputStream fos = null;
        byte[] buffer = new byte[2048];
        int length;
        double currentLength = 0;
        double sumLength;
        try {
            checkLocalFilePath(mFilePath);
            file = new File(mFilePath);
            fos = new FileOutputStream(file);
            inputStream = response.body().byteStream();
            sumLength = response.body().contentLength();
            while ((length = inputStream.read(buffer)) != -1) {
                fos.write(buffer, 0, buffer.length);
                currentLength += length;
                mProgress = (int) (currentLength / sumLength * 100);
                mHandler.obtainMessage(PROGRESS_MESSAGE, mProgress).sendToTarget();
            }
            fos.flush();
        } catch (Exception e) {
            file = null;
        } finally {
            try {
                if (fos != null) {
                    fos.close();
                }
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (Exception e) {
                file = null;
            }
        }
        return file;
    }

    private void checkLocalFilePath(String localFilePath) {
        File path = new File(localFilePath.substring(0, localFilePath.lastIndexOf("/") + 1));
        File file = new File(localFilePath);
        if (!path.exists()) {
            path.mkdirs();
        }
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}