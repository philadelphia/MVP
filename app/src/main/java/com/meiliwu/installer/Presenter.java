package com.meiliwu.installer;

/**
 * Author:  ZhangTao
 * Date: 2018/3/30.
 */
import com.meiliwu.installer.entity.APKEntity;
import com.meiliwu.installer.entity.PackageEntity;
import com.meiliwu.installer.entity.Result;
import com.meiliwu.installer.mvp.Model;
import com.meiliwu.installer.mvp.MvpContract;
import com.meiliwu.installer.rx.RxErrorHandler;
import com.meiliwu.installer.rx.RxErrorHandlerSubscriber;

import rx.Observable;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

/**
 * Author Tao.ZT.Zhang
 * Date   2017/10/30
 */

public class Presenter {
    private static final String TAG = "Presenter";
    private MvpContract.IView view;
    private MvpContract.IModel model;
    private CompositeSubscription compositeSubscription;
    private RxErrorHandler rxErrorHandler;

    public Presenter(MvpContract.IView iView, RxErrorHandler rxErrorHandler) {
        view = iView;
        model = new Model();
        this.rxErrorHandler = rxErrorHandler;
        compositeSubscription = new CompositeSubscription();
    }

    public void getPackageList() {
        Observable<Result<PackageEntity>> observable = model.getPackageList();
        Subscription subscribe = observable.subscribe(new RxErrorHandlerSubscriber<Result<PackageEntity>>(rxErrorHandler) {
            @Override
            public void onStart() {
                super.onStart();
            }

            @Override
            public void onNext(Result<PackageEntity> pkgEntity) {
                if (pkgEntity.getCode() == 0) {
                    if (pkgEntity.getData().getData().size() > 0) {
                        view.onLoadPackageListSuccess(pkgEntity.getData().getData());
                    }
                } else {
                    view.onLoadPackageListFailed();
                }
            }
        });

        compositeSubscription.add(subscribe);
    }

    public void getSpecifiedAPKVersionList(String system_name, String application_id, String version_type, int pageIndex) {
        Observable<Result<APKEntity>> specifiedAPKVersionList = model.getSpecifiedAPKVersionList(system_name, application_id, version_type, pageIndex);
        Subscription subscribe = specifiedAPKVersionList.subscribe(new RxErrorHandlerSubscriber<Result<APKEntity>>(rxErrorHandler) {
            @Override
            public void onNext(Result<APKEntity> apkEntityResult) {
                if (apkEntityResult.getCode() == 0) {
                    if (apkEntityResult.getData().getStatistic().getCount() > 0) {
                        view.showContentView();
                        view.notifyDataSize(apkEntityResult.getData().getStatistic().getCount());
                        view.onLoadAPKListSuccess(apkEntityResult.getData().getData());
                    } else {
                        view.showEmptyView();
                    }
                } else {
                    view.showErrorView();
                }
            }
        });

        compositeSubscription.add(compositeSubscription);

    }

    public void onDestroy() {
        if (compositeSubscription != null) {
            compositeSubscription.unsubscribe();
        }
    }
}
