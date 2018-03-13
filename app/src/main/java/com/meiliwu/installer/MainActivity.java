package com.meiliwu.installer;

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
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.meiliwu.installer.adapter.BottomSheetRecyclerAdapter;
import com.meiliwu.installer.adapter.CommonViewHolder;
import com.meiliwu.installer.adapter.CustomItemDecoration;
import com.meiliwu.installer.adapter.CustomRecyclerAdapter;
import com.meiliwu.installer.adapter.OnRecyclerViewItemClickListener;
import com.meiliwu.installer.adapter.WrapContentLinearLayoutManager;
import com.meiliwu.installer.entity.APKEntity;
import com.meiliwu.installer.entity.PackageEntity;
import com.meiliwu.installer.mvp.MvpContract;
import com.meiliwu.installer.mvp.Presenter;
import com.meiliwu.installer.rx.ResponseErrorListener;
import com.meiliwu.installer.rx.RxErrorHandler;
import com.meiliwu.installer.service.DownloadService;
import com.meiliwu.installer.utils.EndlessRecyclerOnScrollListener;
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

public class MainActivity extends AppCompatActivity implements MvpContract.IView, SwipeRefreshLayout.OnRefreshListener, View.OnClickListener, ResponseErrorListener {
    private static final String TAG = "MainActivity";
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

    private RecyclerView bottomRecyclerView;
    private Button btnCancel;
    private CustomRecyclerAdapter<APKEntity> adapter;
    private BottomSheetRecyclerAdapter<PackageEntity> bottomSheetAdapter;
    private final List<APKEntity> apkList = new ArrayList<>();
    private final List<PackageEntity> pkgList = new ArrayList<>();
    private Presenter presenter;
    private BottomSheetDialog bottomSheetDialog;
    private String selectedVersionType;
    private int selectedVersionTypeIndex;
    private String selectedApplicationID;
    private String selectedApplicationName = "全部";
    private static final String defaultSystemType = "android";
    private MyReceiver receiver;
    private final ArrayList<String> buildTypes = new ArrayList<>();
    private int pageIndex = 1;
    private static int dataListSize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        presenter = new Presenter(this, new RxErrorHandler.Builder().with(this).responseErrorListener(this).build());
        swipeRefreshLayout.setOnRefreshListener(this);
        initData();
        requestPermissions();
        initAdapter();

        filterBuildType.setTitle("全部");
        filterPackageName.setTitle("全部");
        recyclerView.setLayoutManager(new WrapContentLinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapter);
        recyclerView.addOnScrollListener(new EndlessRecyclerOnScrollListener() {
            @Override
            public void onLoadMore() {
                Log.i(TAG, "onLoadMore: ");
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
                            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this)
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

    @Override
    protected void onResume() {
        super.onResume();
        disableFilter(true);
        registerReceiver();
        presenter.getPackageList();
        presenter.getSpecifiedAPKVersionList(defaultSystemType, selectedApplicationID, selectedVersionType, pageIndex);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
        presenter.onDestroy();
    }

    private void initData() {
        buildTypes.add("全部");
        buildTypes.add("正式");
        buildTypes.add("测试");
    }

    private void initAdapter() {
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
                        installAPK(apkEntity.getDownload_url(), strings.get(strings.size() - 1));
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

        bottomSheetAdapter = new BottomSheetRecyclerAdapter<PackageEntity>(pkgList) {
            @Override
            public void convert(CommonViewHolder holder, PackageEntity item, int position) {
                if (item.getApplication_name().equals(selectedApplicationName)) {
                    holder.setTextColor(R.id.tv_packageName, ContextCompat.getColor(MainActivity.this, R.color.textSelectedColor));
                }

                holder.setText(R.id.tv_packageName, item.getApplication_name());
            }

            @Override
            public int getItemLayoutID() {
                return R.layout.layout_bottom_sheet_dialog_item;
            }

        };
    }

    @Override
    public void onLoadPackageListSuccess(List<PackageEntity> dataList) {
        Log.i(TAG, "onLoadPackageListSuccess: " + dataList.size());
        disableFilter(false);
        swipeRefreshLayout.setRefreshing(false);
        pkgList.clear();
        PackageEntity apkEntity = new PackageEntity();
        apkEntity.setApplication_name("全部");
        pkgList.add(0, apkEntity);
        pkgList.addAll(dataList);
        bottomSheetAdapter.notifyDataSetChanged();
    }

    @Override
    public void onLoadAPKListSuccess(List<APKEntity> dataSource) {
        Log.i(TAG, "onLoadAPKListSuccess:dataSource.size() ==  " + dataSource.size());
        adapter.setLoadState(adapter.LOADING_COMPLETE);
        apkList.addAll(dataSource);
        adapter.notifyItemRangeInserted(apkList.size(), dataSource.size());
        if (apkList.size() < dataListSize) {
            pageIndex++;
        } else {
            adapter.setLoadState(adapter.LOADING_END);
        }
        Log.i(TAG, "onLoadAPKListSuccess: 已加载数据长度 ==  " + apkList.size());
    }

    @Override
    public void onLoadPackageListFailed() {
        Log.i(TAG, "onLoadPackageListFailed: ");
//        disableFilter(true);
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onLoadAPKListFailed() {
        Log.i(TAG, "onLoadAPKListFailed: ");
        adapter.setLoadState(adapter.LOADING_COMPLETE);
        statusLayout.setEmptyClick(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.getSpecifiedAPKVersionList(defaultSystemType, selectedApplicationID, selectedVersionType, pageIndex);
            }
        });
//        apkList.clear();
//        adapter.notifyDataSetChanged();

    }

    @Override
    public void notifyDataSize(int count) {
        dataListSize = count;
        Log.i(TAG, "notifyDataSize:dataListSize =  " + dataListSize);
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
        Log.i(TAG, "onFailure: " + string);

    }


    private void disableFilter(boolean flag) {
        filterBuildType.setClickable(!flag);
        filterPackageName.setClickable(!flag);
    }

    @Override
    public void onRefresh() {
        Log.i(TAG, "onRefresh: ");
        /*清空已加载的apk数据*/
        apkList.clear();
        pageIndex = 1;
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
    private void showBottomSheetDialog(final ArrayList<String> buildTypes) {
        initBottomSheetDialog();
        BottomSheetRecyclerAdapter<String> bottomSheetAdapter = new BottomSheetRecyclerAdapter<String>(buildTypes) {
            @Override
            public void convert(CommonViewHolder holder, String item, int position) {
                if (position == selectedVersionTypeIndex) {
                    holder.setTextColor(R.id.tv_packageName, ContextCompat.getColor(MainActivity.this, R.color.textSelectedColor));
                }
                holder.setText(R.id.tv_packageName, item);

            }

            @Override
            public int getItemLayoutID() {
                return R.layout.layout_bottom_sheet_dialog_item;
            }

        };
        bottomRecyclerView.addOnItemTouchListener(new OnRecyclerViewItemClickListener(bottomRecyclerView) {
            @Override
            public void onItemClick(View view, int position) {
                selectedVersionTypeIndex = position;
                apkList.clear();
                adapter.notifyItemMoved(0, apkList.size());
                if (position == 0) {
                    selectedVersionType = null;
                    filterBuildType.setHighlight(false);

                } else {
                    selectedVersionType = buildTypes.get(position);
                    filterBuildType.setHighlight(true);
                }
                filterBuildType.setTitle(buildTypes.get(position));
                doFilter(selectedApplicationID, selectedVersionType);
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });
        bottomRecyclerView.setAdapter(bottomSheetAdapter);
    }

    /*APK筛选*/
    private void showBottomSheetDialog(final List<PackageEntity> dataSource) {
        initBottomSheetDialog();

        bottomRecyclerView.setAdapter(bottomSheetAdapter);
        bottomRecyclerView.addOnItemTouchListener(new OnRecyclerViewItemClickListener(bottomRecyclerView) {
            @Override
            public void onItemClick(View view, int position) {
                apkList.clear();
                if (position == 0) {
                    selectedApplicationID = null;
                    filterPackageName.setHighlight(false);
                } else {
                    selectedApplicationID = dataSource.get(position).getId();
                    filterPackageName.setHighlight(true);
                }
                selectedApplicationName = dataSource.get(position).getApplication_name();
                filterPackageName.setTitle(dataSource.get(position).getApplication_name());
                doFilter(selectedApplicationID, selectedVersionType);
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });
    }

    private void initBottomSheetDialog() {
        bottomSheetDialog = new BottomSheetDialog(this);
        View view = LayoutInflater.from(this).inflate(R.layout.laytou_bottom_sheet_dialog, null);
        bottomSheetDialog.setContentView(view);
        bottomSheetDialog.show();
        bottomRecyclerView = view.findViewById(R.id.bottom_recyclerView);
        btnCancel = view.findViewById(R.id.btn_cancel);
        btnCancel.setOnClickListener(this);
        bottomRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        bottomRecyclerView.addItemDecoration(new CustomItemDecoration(15, 0, 0, 10));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_cancel:
                if (bottomSheetDialog != null) {
                    bottomSheetDialog.dismiss();
                }
                break;
            default:
                break;

        }
    }

    private void doFilter(String application_id, String version_type) {
        if (bottomSheetDialog != null && bottomSheetDialog.isShowing()) {
            bottomSheetDialog.dismiss();
        }
        pageIndex = 1;
        presenter.getSpecifiedAPKVersionList(MainActivity.defaultSystemType, application_id, version_type, pageIndex);
    }

    private void registerReceiver() {
        receiver = new MyReceiver();
        IntentFilter intentFilter = new IntentFilter(DownloadService.BROADCAST_ACTION);
        intentFilter.addCategory(Intent.CATEGORY_DEFAULT);
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, intentFilter);

    }

    private void installAPK(String url, String fileName) {
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
        //在Android7.0(Android N)及以上版本
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            Uri uri = FileProvider.getUriForFile(MainActivity.this, "com.meiliwu.installer.fileprovider", file);//通过FileProvider创建一个content类型的Uri
            intent.setDataAndType(uri, "application/vnd.android.package-archive");
        } else {
            Uri uri = Uri.fromFile(file);
            intent.setDataAndType(uri, "application/vnd.android.package-archive");
        }
        startActivity(intent);
    }
}
