package com.meiliwu.installer.ui.packageList.di;

import com.meiliwu.installer.api.ApiService;
import com.meiliwu.installer.ui.packageList.mvp.PackageListContract;
import com.meiliwu.installer.ui.packageList.mvp.PackageListModel;

import dagger.Module;
import dagger.Provides;

/**
 * Author:  ZhangTao
 * Date: 2018/3/30.
 */
@Module
public class PackageListModule {
    private PackageListContract.View view;

    public PackageListModule(PackageListContract.View view) {
        this.view = view;
    }

    @ActivityScope
    @Provides
    PackageListContract.View provideView() {
        return view;
    }

    @ActivityScope
    @Provides
    PackageListContract.Model provideModel(ApiService apiService) {
        return new PackageListModel(apiService);
    }
}
