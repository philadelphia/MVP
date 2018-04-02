package com.meiliwu.installer.di;

import com.meiliwu.installer.api.ApiService;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit2.Retrofit;

/**
 * Author:  ZhangTao
 * Date: 2018/3/30.
 */
@Module
public class ServiceModule {
    @Singleton
    @Provides
    ApiService provideAPIService(Retrofit retrofit) {
        return retrofit.create(ApiService.class);
    }
}
