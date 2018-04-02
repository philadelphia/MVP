package com.meiliwu.installer.ui.packageList.mvp;

import android.util.Log;

import com.meiliwu.installer.api.ApiService;
import com.meiliwu.installer.base.BaseModel;
import com.meiliwu.installer.entity.APKEntity;
import com.meiliwu.installer.entity.PackageEntity;
import com.meiliwu.installer.entity.Result;
import com.meiliwu.installer.utils.RxsRxSchedulers;

import rx.Observable;

/**
 * Author:  ZhangTao
 * Date: 2018/3/29.
 */
public class PackageListModel extends BaseModel<ApiService> implements PackageListContract.Model {
    public PackageListModel(ApiService apiService) {
        super(apiService);
    }

    @Override
    public Observable<Result<PackageEntity>> getPackageList() {
            return getService().getPackageList().compose(RxsRxSchedulers.<Result<PackageEntity>>io_main());
    }

    @Override
    public Observable<Result<APKEntity>> getSpecifiedAPKVersionList(String system_name, String application_id, String version_type, int pageIndex) {
        return getService().getSpecifiedAPKVersionList(system_name,application_id, version_type, pageIndex).compose(RxsRxSchedulers.<Result<APKEntity>>io_main());
    }
}
