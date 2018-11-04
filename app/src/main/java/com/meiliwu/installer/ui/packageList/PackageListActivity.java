package com.meiliwu.installer.ui.packageList;

import android.Manifest;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.meiliwu.installer.BuildConfig;
import com.meiliwu.installer.R;
import com.meiliwu.installer.adapter.CommonViewHolder;
import com.meiliwu.installer.adapter.CustomRecyclerAdapter;
import com.meiliwu.installer.base.BaseActivity;
import com.meiliwu.installer.entity.APKEntity;
import com.meiliwu.installer.entity.BuildType;
import com.meiliwu.installer.entity.ISelectable;
import com.meiliwu.installer.entity.PackageEntity;
import com.meiliwu.installer.rx.ResponseErrorListener;
import com.meiliwu.installer.rx.RxErrorHandler;
import com.meiliwu.installer.service.DownloadService;
import com.meiliwu.installer.ui.packageList.mvp.PackageListContract;
import com.meiliwu.installer.ui.packageList.mvp.PackageListPresenter;
import com.meiliwu.installer.utils.EndlessRecyclerOnScrollListener;
import com.meiliwu.installer.view.CustomBottomSheetDialog;
import com.meiliwu.installer.view.FilterTabItemView;
import com.meiliwu.installer.view.StatusLayout;
import com.tbruyelle.rxpermissions.RxPermissions;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.functions.Action1;

public class PackageListActivity extends BaseActivity<PackageListPresenter> implements PackageListContract.View, SwipeRefreshLayout.OnRefreshListener, ResponseErrorListener, CustomBottomSheetDialog.OnItemClickListener {
    private static final String TAG = "PackageListActivity";
    @BindView(R.id.statusLayout)
    StatusLayout statusLayout;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.filter_buildType)
    FilterTabItemView filterBuildType;
    @BindView(R.id.filter_packageName)
    FilterTabItemView filterPackageName;
    private CustomRecyclerAdapter<APKEntity> adapter;
    private final List<APKEntity> apkList = new ArrayList<>();
    private final List<PackageEntity> pkgList = new ArrayList<>();
    private String selectedVersionType;
    private String selectedApplicationID;
    private static final String defaultSystemType = "android";
    private MyReceiver receiver;
    private final ArrayList<BuildType> buildTypes = new ArrayList<>();
    private int pageIndex = 1;
    private static int dataListSize;
    private CustomBottomSheetDialog packageBottomSheetDialog;
    private CustomBottomSheetDialog buildTypeBottomSheetDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate: ");
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        swipeRefreshLayout.setOnRefreshListener(this);
        initData();
        requestPermissions();
        registerReceiver();
        initAdapter();
        disableFilter(true);

        //请求数据
        if (presenter == null) {
            Log.i(TAG, "onCreate: getPresenter() == null");
        }else {
            Log.i(TAG, "onCreate: SimpleName " + presenter.getClass().getSimpleName());
            Log.i(TAG, "onCreate: Presenter not null" );
        }
        presenter.getPackageList();
        presenter.getSpecifiedAPKVersionList(defaultSystemType, selectedApplicationID, selectedVersionType, pageIndex);

        filterBuildType.setTitle("全部");
        filterPackageName.setTitle("全部");
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapter);
        recyclerView.addOnScrollListener(new EndlessRecyclerOnScrollListener() {
            @Override
            public void onLoadMore() {
                if (apkList.size() < dataListSize) {
                    adapter.setLoadState(adapter.LOADING);
                    presenter.getSpecifiedAPKVersionList(defaultSystemType, selectedApplicationID, selectedVersionType, pageIndex);
                }

            }

            @Override
            public void setFlag(boolean flag) {
                if (apkList.size() < dataListSize) {
                    EndlessRecyclerOnScrollListener.flag = false;
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        Log.i(TAG, "onDestroy: ");
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
    }

    @Override
    public int getLayoutID() {
        Log.i(TAG, "getLayoutID: ");
        return R.layout.activity_main;
    }

    @Override
    public PackageListPresenter createPresenter() {
        return new PackageListPresenter(new RxErrorHandler.Builder().with(this).responseErrorListener(this).build());
    }

    private void requestPermissions() {
        final RxPermissions rxPermissions = new RxPermissions(this);
        rxPermissions.request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean granted) {
                        if (granted) {
                            Log.i(TAG, "call: true");
                        } else {
                            Log.i(TAG, "call: false");
                            AlertDialog.Builder builder = new AlertDialog.Builder(PackageListActivity.this)
                                    .setTitle("提示")
                                    .setMessage("请务必给与存储权限，以便您的使用")
                                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            requestPermissions();
                                        }
                                    });
                            builder.create().show();
                        }
                    }
                });
    }

    private void initData() {
        buildTypes.add(new BuildType("全部"));
        buildTypes.add(new BuildType("正式"));
        buildTypes.add(new BuildType("测试"));
    }

    private void initAdapter() {
        Log.i(TAG, "initAdapter: ");
        adapter = new CustomRecyclerAdapter<APKEntity>(apkList) {
            @Override
            public void convert(CommonViewHolder holder, final APKEntity apkEntity, int position) {
                Glide.with(holder.itemView.getContext())
                        .load(apkEntity.getIcon_url())
                        .into(((ImageView) holder.getView(R.id.img_icon)));
                holder.setText(R.id.tv_packageName, apkEntity.getApplication_name() + "(" + apkEntity.getVersion_name() + ")");
                holder.setText(R.id.tv_timeStamp, apkEntity.getCreate_time());
                if ((!TextUtils.isEmpty(apkEntity.getVersion_type())) && apkEntity.getVersion_type().equals("测试")) {
                    holder.setVisible(R.id.tv_isDebugVersion, View.VISIBLE);
                    holder.setText(R.id.tv_isDebugVersion, apkEntity.getVersion_type());
                } else {
                    holder.setVisible(R.id.tv_isDebugVersion, View.INVISIBLE);
                }
                holder.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String[] split = apkEntity.getDownload_url().split("/");
                        List<String> strings = Arrays.asList(split);
                        strings.get(strings.size() - 1);
                        downLoadAPK(apkEntity.getDownload_url(), strings.get(strings.size() - 1));
                    }
                }, R.id.btn_downLoad);
            }

            @Override
            public int getItemLayoutID() {
                return R.layout.layout_recyclerview_package_item;
            }

            @Override
            public int getFootViewLayoutID() {
                return R.layout.layout_footer_view;
            }
        };
    }

    @Override
    public void onLoadPackageListSuccess(List<PackageEntity> dataList) {
        Log.i(TAG, "onLoadPackageListSuccess:size ==  " + dataList.size());
        disableFilter(false);
        swipeRefreshLayout.setRefreshing(false);
        pkgList.clear();
        PackageEntity packageEntity = new PackageEntity();
        packageEntity.setApplication_name("全部");
        pkgList.add(0, packageEntity);
        pkgList.addAll(dataList);
        packageBottomSheetDialog.notifyDataSetChanged(dataList);
    }

    @Override
    public void onLoadAPKListSuccess(List<APKEntity> dataSource) {
        swipeRefreshLayout.setRefreshing(false);
        adapter.setLoadState(adapter.LOADING_COMPLETE);
        apkList.addAll(dataSource);
        adapter.notifyItemRangeInserted(apkList.size(), dataSource.size());
        if (apkList.size() < dataListSize) {
            pageIndex++;
        } else {
            adapter.setLoadState(adapter.LOADING_END);
        }
    }

    @Override
    public void onLoadPackageListFailed() {
        disableFilter(true);
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onLoadAPKListFailed() {
        adapter.setLoadState(adapter.LOADING_COMPLETE);
        statusLayout.setEmptyClick(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.getSpecifiedAPKVersionList(defaultSystemType, selectedApplicationID, selectedVersionType, pageIndex);
            }
        });
    }

    @Override
    public void notifyDataSize(int count) {
        dataListSize = count;
    }

    @Override
    public void showContentView() {
        statusLayout.showContentView();
    }

    @Override
    public void showErrorView() {
        statusLayout.showErrorView();
    }

    @Override
    public void showEmptyView() {
        statusLayout.showEmptyView();
    }

    @Override
    public void onFailure(String string) {

    }


    private void disableFilter(boolean flag) {
        filterBuildType.setClickable(!flag);
        filterPackageName.setClickable(!flag);
    }

    @Override
    public void onRefresh() {
        /*清空已加载的apk数据*/
        apkList.clear();
        pageIndex = 1;
        presenter.getPackageList();
        presenter.getSpecifiedAPKVersionList(defaultSystemType, selectedApplicationID, selectedVersionType, pageIndex);
    }

    @OnClick({R.id.filter_buildType, R.id.filter_packageName})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.filter_buildType:
                showBottomSheetDialog(buildTypes);
                break;
            case R.id.filter_packageName:
                showBottomSheetDialog(pkgList);
                break;
            default:
                break;
        }
    }

    /*正式/测试筛选*/
    private void showBottomSheetDialog(final ArrayList<BuildType> buildTypes) {
        if (buildTypeBottomSheetDialog == null){
            buildTypeBottomSheetDialog = new CustomBottomSheetDialog(this);
        }
        buildTypeBottomSheetDialog.setDataList(buildTypes);
        buildTypeBottomSheetDialog.show();
        buildTypeBottomSheetDialog.setOnItemClickListener(new CustomBottomSheetDialog.OnItemClickListener() {
            @Override
            public void ontItemClick(View view, ISelectable packageEntity, int position) {
                apkList.clear();
                if (position == 0){
                    selectedVersionType = null;
                    filterBuildType.setHighlight(false);
                }else {
                    selectedVersionType = packageEntity.getName();
                    filterBuildType.setHighlight(true);
                }
                filterBuildType.setTitle(packageEntity.getName());
                doFilter(selectedApplicationID, selectedVersionType);
                buildTypeBottomSheetDialog.dismiss();
            }
        });
    }

    /*APK筛选*/
    private void showBottomSheetDialog(final List<PackageEntity> dataSource) {
        if (packageBottomSheetDialog == null){
            packageBottomSheetDialog = new CustomBottomSheetDialog(this);
        }
        packageBottomSheetDialog.setDataList(dataSource);
        packageBottomSheetDialog.show();
        packageBottomSheetDialog.setOnItemClickListener(this);
    }

    private void doFilter(String application_id, String version_type) {
        pageIndex = 1;
        presenter.getSpecifiedAPKVersionList(PackageListActivity.defaultSystemType, application_id, version_type, pageIndex);
    }

    private void registerReceiver() {
        receiver = new MyReceiver();
        IntentFilter intentFilter = new IntentFilter(DownloadService.BROADCAST_ACTION);
        intentFilter.addCategory(Intent.CATEGORY_DEFAULT);
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, intentFilter);

    }

    private void downLoadAPK(String url, String fileName) {
        Intent serviceIntent = new Intent(this, DownloadService.class);
        serviceIntent.setData(Uri.parse(url));
        serviceIntent.putExtra(DownloadService.FILE_NAME, fileName);
        startService(serviceIntent);
    }

    @Override
    public void handlerResponseError(Context context, Exception e) {
        Log.i(TAG, "handlerResponseError:getCause " + e.getCause());
        Log.i(TAG, "handlerResponseError:getMessage " + e.getMessage());
    }

    @Override
    public void ontItemClick(View view, ISelectable entity, int position) {
        apkList.clear();
        if (entity instanceof PackageEntity){
            if (position == 0) {
                selectedApplicationID = null;
                filterPackageName.setHighlight(false);
            } else {
                selectedApplicationID = entity.getID();
                filterPackageName.setHighlight(true);
            }
            filterPackageName.setTitle(entity.getName());
            doFilter(selectedApplicationID, selectedVersionType);
        }
        packageBottomSheetDialog.dismiss();
    }

    private class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String fileName = intent.getStringExtra(DownloadService.FILE_NAME);
            installAPK(fileName);
        }

    }

    private void installAPK(String fileName) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/myapp", fileName);
        Uri uri ;
        //在Android7.0(Android N)及以上版本
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            uri = FileProvider.getUriForFile(PackageListActivity.this, BuildConfig.APPLICATION_ID +".fileprovider", file);//通过FileProvider创建一个content类型的Uri
        } else {
            uri = Uri.fromFile(file);
        }
        intent.setDataAndType(uri, "application/vnd.android.package-archive");
        startActivity(intent);
    }
}
