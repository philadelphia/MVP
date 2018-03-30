package com.meiliwu.installer.di;

import android.app.Application;

import com.meiliwu.installer.App;

import dagger.Module;
import dagger.Provides;

/**
 * Author:  ZhangTao
 * Date: 2018/3/30.
 * 全局提供APP 访问
 */
@Module
public class AppModule {
    private Application app;

    public AppModule(Application app) {
        this.app = app;
    }

    @Provides
    public Application providesApp(){
        return app;
    }

//    @Provides
//    public Context provideContext(){
//        return this.app;
//    }
}
