package com.meiliwu.installer.ui.packageList.mvp;

import com.meiliwu.installer.base.BasePresenter;
import com.meiliwu.installer.entity.APKEntity;
import com.meiliwu.installer.entity.PackageEntity;
import com.meiliwu.installer.entity.Result;
import com.meiliwu.installer.rx.RxErrorHandler;
import com.meiliwu.installer.rx.RxErrorHandlerSubscriber;
import com.meiliwu.installer.ui.packageList.di.ActivityScope;
import com.meiliwu.installer.utils.ApiServiceUtil;

import javax.inject.Inject;

import rx.Observable;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

/**
 * Author Tao.ZT.Zhang
 * Date   2017/10/30
 */

@ActivityScope
public class PackageListPresenter extends BasePresenter<PackageListContract.View, PackageListContract.Model> {
    private static final String TAG = "PackageListPresenter";


    private RxErrorHandler rxErrorHandler;

    @Inject
    public PackageListPresenter(PackageListContract.View view, PackageListContract.Model model, RxErrorHandler rxErrorHandler) {
        super(view, model);
        this.rxErrorHandler = rxErrorHandler;

    }

    public void getPackageList() {
        Observable<Result<PackageEntity>> observable = getModel().getPackageList();
        Subscription subscribe = observable.subscribe(new RxErrorHandlerSubscriber<Result<PackageEntity>>(rxErrorHandler) {
            @Override
            public void onStart() {
                super.onStart();
            }

            @Override
            public void onNext(Result<PackageEntity> pkgEntity) {
                if (pkgEntity.getCode() == 0) {
                    if (pkgEntity.getData().getData().size() > 0) {
                        getView().onLoadPackageListSuccess(pkgEntity.getData().getData());
                    }
                } else {
                    getView().onLoadPackageListFailed();
                }
            }


            @Override
            public void onError(Throwable e) {
                super.onError(e);
            }
        });

        addSubscription(subscribe);
    }

    public void getSpecifiedAPKVersionList(String system_name, String application_id, String version_type, int pageIndex) {
        Observable<Result<APKEntity>> specifiedAPKVersionList = getModel().getSpecifiedAPKVersionList(system_name, application_id, version_type, pageIndex);
        Subscription subscription = specifiedAPKVersionList.subscribe(new RxErrorHandlerSubscriber<Result<APKEntity>>(rxErrorHandler) {
            @Override
            public void onNext(Result<APKEntity> apkEntityResult) {
                if (apkEntityResult.getCode() == 0) {
                    if (apkEntityResult.getData().getStatistic().getCount() > 0) {
                        getView().showContentView();
                        getView().notifyDataSize(apkEntityResult.getData().getStatistic().getCount());
                        getView().onLoadAPKListSuccess(apkEntityResult.getData().getData());
                    } else {
                        getView().showEmptyView();
                    }
                } else {
                    getView().showErrorView();
                }
            }
        });

        addSubscription(subscription);
    }

    public void onDestroy() {

    }
}
