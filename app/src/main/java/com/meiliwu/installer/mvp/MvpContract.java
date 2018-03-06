package com.meiliwu.installer.mvp;


import com.meiliwu.installer.entity.PackageEntity;
import com.meiliwu.installer.entity.Result;

import java.util.List;

import rx.Observable;


/**
 * Author Tao.ZT.Zhang
 * Date   2017/10/30
 */

public class MvpContract {
    public  interface IView{
       void  onLoadDataSuccess(List<PackageEntity> dataSource);
       void onLoadDataFailed();

    }
    public  interface IModel{
         Observable<Result<PackageEntity>> getPackageList() ;
    }
}
