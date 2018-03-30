package com.meiliwu.installer.ui.packageList.di;

import com.meiliwu.installer.di.AppComponent;
import com.meiliwu.installer.ui.packageList.PackageListActivity;

import dagger.Component;

/**
 * Author:  ZhangTao
 * Date: 2018/3/30.
 */

@ActivityScope
@Component(modules = PackageListModule.class, dependencies = AppComponent.class)
public interface PackageListComponent {
    void inject(PackageListActivity packageListActivity);
}
