package com.meiliwu.installer.di;

import android.app.Application;
import android.content.Context;

import com.meiliwu.installer.App;
import com.meiliwu.installer.api.ApiService;
import com.meiliwu.installer.rx.RxErrorHandler;

import javax.inject.Singleton;

import dagger.Component;
import dagger.Module;

/**
 * Author:  ZhangTao
 * Date: 2018/3/30.
 */
@Singleton
@Component(modules = {AppModule.class, NetModule.class, ServiceModule.class})
public interface AppComponent {
    void inject(App app);

    Application Application();

    Context context();

    ApiService apiService();  // 所有Api请求的管理类

    //RxJava错误处理管理类
    RxErrorHandler rxErrorHandler();
}
