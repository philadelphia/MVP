package com.meiliwu.installer.utils;

import android.content.Context;
import android.os.Environment;

import com.meiliwu.installer.constant.Constant;

import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Author:  ZhangTao
 * Date: 2018/3/5.
 */

public class RetrofitUtil {
    private static volatile RetrofitUtil instance;

    private final Retrofit retrofit;

    /*缓存最大容量为10M*/
    public static final int MAX_CACHE_SIZE = 10 * 1024 * 1024;

    public Retrofit getRetrofit() {
        return retrofit;
    }

    private OkHttpClient getOkHttpClient() {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .readTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .connectTimeout(10, TimeUnit.SECONDS)
                .addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BASIC))
                .cache(new Cache(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),MAX_CACHE_SIZE ))
                .build();
        return okHttpClient;
    }

    private RetrofitUtil() {
        retrofit = new Retrofit.Builder().baseUrl(Constant.BASE_URL)
                .client(getOkHttpClient())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();
    }

    public static RetrofitUtil getInstance() {
        if (instance == null) {
            synchronized (RetrofitUtil.class) {
                if (instance == null) {
                    return new RetrofitUtil();
                }
            }
        }
        return instance;
    }
}
