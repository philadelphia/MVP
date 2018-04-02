package com.meiliwu.installer.di;

import com.meiliwu.installer.api.ApiService;
import com.meiliwu.installer.entity.APKEntity;
import com.meiliwu.installer.entity.PackageEntity;
import com.meiliwu.installer.entity.Result;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit2.Retrofit;
import rx.Observable;

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
