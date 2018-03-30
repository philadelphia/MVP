package com.meiliwu.installer.ui.packgeList.mvp;

import com.meiliwu.installer.api.ApiService;
import com.meiliwu.installer.base.BaseModel;
import com.meiliwu.installer.entity.APKEntity;
import com.meiliwu.installer.entity.PackageEntity;
import com.meiliwu.installer.entity.Result;

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
        return getApiService().getPackageList();
    }

    @Override
    public Observable<Result<APKEntity>> getSpecifiedAPKVersionList(String system_name, String application_id, String version_type, int pageIndex) {
        return getApiService().getSpecifiedAPKVersionList(system_name,application_id, version_type, pageIndex);
    }
}
