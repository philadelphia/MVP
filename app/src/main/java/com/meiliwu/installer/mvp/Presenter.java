package com.meiliwu.installer.mvp;

import android.util.Log;

import com.meiliwu.installer.entity.APKEntity;
import com.meiliwu.installer.entity.PackageEntity;
import com.meiliwu.installer.entity.Result;

import rx.Observable;
import rx.functions.Action1;


/**
 * Author Tao.ZT.Zhang
 * Date   2017/10/30
 */

public class Presenter {
    private static final String TAG = "Presenter";
    private MvpContract.IView view;
    private MvpContract.IModel model;

    public Presenter(MvpContract.IView iView) {
        view = iView;
        model = new Model();
    }

    public void getPackageList() {
        Log.i(TAG, "getPackageList: ");
        Observable<Result<APKEntity>> observable = model.getPackageList();
        observable.subscribe(new Action1<Result<APKEntity>>() {
            @Override
            public void call(Result<APKEntity> result) {
                Log.i(TAG, "call: " + result.toString());
                if (result.getCode() == 0) {
                    if (result.getData().getData().size() > 0) {
                        view.onLoadPackageListSuccess(result.getData().getData());
                    }
                }

            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                view.onLoadPackageListFailed();
            }
        });
    }

    public void getSpecifiedAPKVersionList(String system_name, String application_id, String version_type) {
        Log.i(TAG, "getSpecifiedAPKVersionList: ");
        Observable<Result<APKEntity>> specifiedAPKVersionList = model.getSpecifiedAPKVersionList(system_name, application_id, version_type);
        specifiedAPKVersionList.subscribe(new Action1<Result<APKEntity>>() {
            @Override
            public void call(Result<APKEntity> apkEntityResult) {
                if (apkEntityResult.getCode() == 0) {
                    if (apkEntityResult.getData().getStatistic().getCount() > 0) {
                        view.onLoadAPKListSuccess(apkEntityResult.getData().getData());
                    } else {
                        view.onLoadAPKListFailed();
                    }
                }
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                view.onLoadAPKListFailed();
            }
        });

    }
}
