package com.meiliwu.installer.mvp;

import android.util.Log;

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
        Observable<Result<PackageEntity>> gankDailyDataEntityObservable = model.getPackageList();
        gankDailyDataEntityObservable.subscribe(new Action1<Result<PackageEntity>>() {
            @Override
            public void call(Result<PackageEntity> result) {
                if (result.getCode() == 0) {
                    if (result.getData().getData().size() > 0) {
                        view.onLoadDataSuccess(result.getData().getData());
                    } else {
//                        view.onLoadDataFailed();
                    }

                } else {
//                    view.onLoadDataFailed();
                }
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                view.onLoadDataFailed();
            }
        });
    }
}
