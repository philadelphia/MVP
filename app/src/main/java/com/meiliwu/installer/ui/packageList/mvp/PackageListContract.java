package com.meiliwu.installer.ui.packageList.mvp;

import com.meiliwu.installer.base.IModel;
import com.meiliwu.installer.base.IView;
import com.meiliwu.installer.entity.APKEntity;
import com.meiliwu.installer.entity.PackageEntity;
import com.meiliwu.installer.entity.Result;

import java.util.List;

import rx.Observable;

/**
 * Author:  ZhangTao
 * Date: 2018/3/29.
 */
public interface PackageListContract {
    interface View extends IView {
        void onLoadPackageListSuccess(List<PackageEntity> dataSource);

        void onLoadPackageListFailed();

        void onLoadAPKListSuccess(List<APKEntity> dataSource);

        void onLoadAPKListFailed();

        void notifyDataSize(int count);

        void showContentView();

        void showErrorView();

        void showEmptyView();

        void onFailure(String string);
    }

    interface Model extends IModel {
        Observable<Result<PackageEntity>> getPackageList();

        Observable<Result<APKEntity>> getSpecifiedAPKVersionList(String system_name, String application_id, String version_type, int pageIndex);
    }
}
