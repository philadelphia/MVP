package com.meiliwu.installer.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

/**
 * Author:  ZhangTao
 * Date: 2018/3/29.
 */
public abstract class BaseActivity<P extends BasePresenter> extends AppCompatActivity implements IView {
    private static final String TAG = "BaseActivity";

    protected P presenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Log.i(TAG, "onCreate: ");
        super.onCreate(savedInstanceState);
        setContentView(getLayoutID());
        presenter = createPresenter();
        if (presenter != null) {
            presenter.onAttach( this);
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (presenter != null) {
            presenter.onDetach();
        }
    }


    public abstract int getLayoutID();

    public abstract P createPresenter();
}

