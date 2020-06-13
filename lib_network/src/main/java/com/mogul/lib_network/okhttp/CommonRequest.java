package com.mogul.lib_network.okhttp;

import java.util.Map;

import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.Request;


public class CommonRequest {

    /**
     * Post请求
     *
     * @param url
     * @param params
     * @param headers
     * @return
     */
    public static Request createPostRequest(String url, RequestParams params, RequestParams headers) {
        FormBody.Builder formBody = new FormBody.Builder();
        if (params != null) {
            //参数遍历
            for (Map.Entry<String, String> entry : params.urlParams.entrySet()) {
                formBody.add(entry.getKey(), entry.getValue());
            }
        }

        //添加请求头
        Headers.Builder header = new Headers.Builder();
        if (headers != null) {
            for (Map.Entry<String, String> entry : headers.urlParams.entrySet()) {
                formBody.add(entry.getKey(), entry.getValue());
            }
        }
        return new Request.Builder().url(url).headers(header.build()).post(formBody.build()).build();
    }

    /**
     * Get请求
     *
     * @param url
     * @param params
     * @param headers
     * @return
     */
    public static Request createGetRequest(String url, RequestParams params, RequestParams headers) {
        StringBuilder urlBuilder = new StringBuilder(url).append("?");
        if (params != null) {
            for (Map.Entry<String, String> entry : params.urlParams.entrySet()) {
                urlBuilder.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
            }
        }
        //添加请求头
        Headers.Builder header = new Headers.Builder();
        if (headers != null) {
            for (Map.Entry<String, String> entry : headers.urlParams.entrySet()) {
                header.add(entry.getKey(), entry.getValue());
            }
        }
        return new Request.Builder().url(url).get().headers(header.build()).build();
    }

    /**
     * 文件上传请求
     *
     * @return
     */
    private static final MediaType FILE_TYPE = MediaType.parse("application/octet-stream");

    public static Request createMultiFilePostRequest(String url, RequestParams params) {
        return null;
    }
}
