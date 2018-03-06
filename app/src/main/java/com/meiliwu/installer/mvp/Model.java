package com.meiliwu.installer.mvp;

import com.meiliwu.installer.api.ApiService;
import com.meiliwu.installer.entity.PackageEntity;
import com.meiliwu.installer.entity.Result;
import com.meiliwu.installer.utils.RetrofitUtil;
import com.meiliwu.installer.utils.RxsRxSchedulers;

import rx.Observable;


/**
 * Author Tao.ZT.Zhang
 * Date   2017/10/30
 */

public class Model implements MvpContract.IModel {
    @Override
    public Observable<Result<PackageEntity>> getPackageList() {
        return RetrofitUtil.getInstance().getRetrofit().create(ApiService.class).getPackageList().compose(RxsRxSchedulers.<Result<PackageEntity>>io_main());
    }


}
