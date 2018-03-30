package com.meiliwu.installer.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.meiliwu.installer.App;
import com.meiliwu.installer.di.AppComponent;

import javax.inject.Inject;

/**
 * Author:  ZhangTao
 * Date: 2018/3/29.
 */
public abstract class BaseActivity<P extends BasePresenter> extends AppCompatActivity {
    private static final String TAG = "BaseActivity";
    @Inject
    P presenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Log.i(TAG, "onCreate: ");
        super.onCreate(savedInstanceState);
        setContentView(getLayoutID());
        componentInject(((App) getApplication()).getAppComponent());//依赖注入
    }

    public P getPresenter() {
        return presenter;

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (presenter != null) {
            presenter.onDetach();
        }
    }

    protected abstract void componentInject(AppComponent appComponent);

    public abstract int getLayoutID();
}

