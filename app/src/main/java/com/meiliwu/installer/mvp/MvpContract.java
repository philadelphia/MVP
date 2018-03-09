package com.meiliwu.installer.mvp;


import com.meiliwu.installer.entity.APKEntity;
import com.meiliwu.installer.entity.PackageEntity;
import com.meiliwu.installer.entity.Result;

import java.util.List;

import retrofit2.http.Query;
import rx.Observable;


/**
 * Author Tao.ZT.Zhang
 * Date   2017/10/30
 */

public class MvpContract {
    public interface IView {
        void onLoadPackageListSuccess(List<APKEntity> dataSource);

        void onLoadPackageListFailed();

        void onLoadAPKListSuccess(List<APKEntity> dataSource);

        void onLoadAPKListFailed();

        void notifyDataSize(int count);

        void showContentView();

        void showErrorView();

        void showEmptyView();

        void onFailure(String string);
    }

    public interface IModel {
        Observable<Result<APKEntity>> getPackageList();

        Observable<Result<APKEntity>> getSpecifiedAPKVersionList(String system_name, String application_id, String version_type, int pageIndex);
    }
}
