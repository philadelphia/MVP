package com.meiliwu.installer.di;

import android.app.Application;
import android.text.TextUtils;

import com.meiliwu.installer.App;
import com.meiliwu.installer.http.GlobalHttpHandler;
import com.meiliwu.installer.rx.ResponseErrorListener;
import com.meiliwu.installer.rx.RxErrorHandler;

import java.io.File;
import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.Cache;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Author:  ZhangTao
 * Date: 2018/3/30.
 */
@Module
public class NetModule {
    private static final int TIME_OUT = 50;
    public static final int HTTP_RESPONSE_DISK_CACHE_MAX_SIZE = 10 * 1024 * 1024;//缓存文件最大值为10Mb
    private HttpUrl mApiUrl;
    private GlobalHttpHandler mHandler;
    private Interceptor[] mInterceptors;
    private ResponseErrorListener mErrorListener;

    private NetModule(NetModule.Builder builder){
        this.mApiUrl = builder.mApiUrl;
        this.mInterceptors = builder.mInterceptors;
        this.mHandler = builder.mHandler;
        this.mErrorListener = builder.mErrorListener;
    }

    @Singleton
    @Provides
    HttpUrl ProvideUrl() {
        return mApiUrl;
    }

    /**
     * 提供Retrofit
     * */
    @Singleton
    @Provides
     Retrofit provideRetrofit(OkHttpClient okHttpClient, HttpUrl url) {
        Retrofit.Builder builder = new Retrofit.Builder();
        return configureRetrofit(builder, okHttpClient, url);
    }

    /**
     * 配置Retrofit
     * */
    private Retrofit configureRetrofit(Retrofit.Builder builder, OkHttpClient okHttpClient, HttpUrl url) {
        return builder.baseUrl(url).client(okHttpClient)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())//使用rxjava
                .addConverterFactory(GsonConverterFactory.create())//使用Gson
                .build();
    }

    /**
     * 提供OkHttpClient
     * */
    @Singleton
    @Provides
     OkHttpClient provideOkHttpClient(Cache cache) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        return configureOkHttpClient(builder, cache);
    }

    /**
     * 配置OkHttpClient
     * */
    private OkHttpClient configureOkHttpClient(OkHttpClient.Builder builder, Cache cache) {
        return builder
                .readTimeout(TIME_OUT, TimeUnit.SECONDS)
                .writeTimeout(TIME_OUT, TimeUnit.SECONDS)
                .connectTimeout(TIME_OUT, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true)
                .cache(cache).build();
    }

    /**
     * 提供HTTPClint 缓存
     * */
    @Singleton
    @Provides
    Cache provideCache(File cacheFile){
        return new Cache(cacheFile, HTTP_RESPONSE_DISK_CACHE_MAX_SIZE);
    }

    /*
    * 提供缓存文件
    * */
    @Singleton
    @Provides
    File provideCacheFile(Application application){
        return application.getCacheDir();
    }

    /**
     * 提供处理Rxjava错误的管理器
     *
     * @return
     */
    @Singleton
    @Provides
    RxErrorHandler proRxErrorHandler(Application application) {
        return RxErrorHandler
                .builder()
                .with(application)
                .responseErrorListener(mErrorListener)
                .build();
    }


    public static final class Builder{
        private static final int TIME_OUT = 50;
        public static final int HTTP_RESPONSE_DISK_CACHE_MAX_SIZE = 10 * 1024 * 1024;//缓存文件最大值为10Mb
        private HttpUrl mApiUrl ;
        private GlobalHttpHandler mHandler;
        private Interceptor[] mInterceptors;
        private ResponseErrorListener mErrorListener;

        public Builder(){

        }

        public Builder setApiUrl(String baseurl) {
            if (TextUtils.isEmpty(baseurl)) {
                throw new IllegalArgumentException("baseurl can not be empty");
            }
            this.mApiUrl = HttpUrl.parse(baseurl);
            return this;
        }

        public Builder setHandler(GlobalHttpHandler handler){
            this.mHandler = handler;
            return this;
        }

        public Builder setInterceptors(Interceptor[] mInterceptors) {
            this.mInterceptors = mInterceptors;
            return this;
        }

        public Builder setErrorListener(ResponseErrorListener mErrorListener) {
            this.mErrorListener = mErrorListener;
            return this;
        }

        public NetModule build(){
            if (mApiUrl == null) {
                throw new IllegalStateException("baseurl is required");
            }
            return new NetModule(this);
        }
    }
}
