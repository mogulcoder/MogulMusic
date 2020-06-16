package com.mogul.lib_network.okhttp.listener;

public interface DisposeDownloadListener {

    void onProgress(int progress);

    void onFailure(Object object);

    void onSuccess(Object object);
}
