package com.meiliwu.installer;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.BottomSheetDialog;
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
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.meiliwu.installer.adapter.CommonViewHolder;
import com.meiliwu.installer.adapter.CustomRecyclerAdapter;
import com.meiliwu.installer.adapter.OnRecyclerViewItemClickListener;
import com.meiliwu.installer.entity.APKEntity;
import com.meiliwu.installer.mvp.MvpContract;
import com.meiliwu.installer.mvp.Presenter;
import com.meiliwu.installer.service.DownloadService;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity implements MvpContract.IView, SwipeRefreshLayout.OnRefreshListener, View.OnClickListener {
    private static final String TAG = "MainActivity";
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.tv_buildType)
    TextView tvBuildType;
    @BindView(R.id.tv_packageName)
    TextView tvPackageName;

    private RecyclerView bottomRecyclerView;
    private Button btnCancel;
    private CustomRecyclerAdapter<APKEntity> adapter;
    private CustomRecyclerAdapter<APKEntity> bottomSheetAdapter;
    private List<APKEntity> apkList = new ArrayList<>();
    private List<APKEntity> pkgList = new ArrayList<>();
    private Presenter presenter;
    private BottomSheetDialog bottomSheetDialog;
    private String selectedVersionType;
    private String selectedApplicationID;
    private String defaultSystemType = "android";
    private MyReceiver receiver;
    private IntentFilter intentFilter;
    private ArrayList<String> buildTypes = new ArrayList<>();
    private int pageIndex = 0;
    private int currentPageIndex;
    public static final int perPageCount = 20;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        presenter = new Presenter(this);
        swipeRefreshLayout.setOnRefreshListener(this);
        initData();
        adapter = new CustomRecyclerAdapter<APKEntity>(apkList) {
            @Override
            public void convert(CommonViewHolder holder, final APKEntity packageEntity, int position) {
                Glide.with(holder.itemView.getContext())
                        .load(packageEntity.getIcon_url())
                        .into(((ImageView) holder.getView(R.id.img_icon)));
                holder.setText(R.id.tv_packageName, packageEntity.getApplication_name() + "(" + packageEntity.getVersion_name() + ")");
                holder.setText(R.id.tv_timeStamp, packageEntity.getCreate_time());
                if ((!TextUtils.isEmpty(packageEntity.getVersion_type())) && packageEntity.getVersion_type().equals("测试")) {
                    holder.setVisible(R.id.tv_isDebugVersion, View.VISIBLE);
                    holder.setText(R.id.tv_isDebugVersion, packageEntity.getVersion_type());
                } else {
                    holder.setVisible(R.id.tv_isDebugVersion, View.INVISIBLE);
                }
                holder.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String[] split = packageEntity.getDownload_url().split("/");
                        List<String> strings = Arrays.asList(split);
                        strings.get(strings.size() - 1);
                        installAPK(packageEntity.getDownload_url(), strings.get(strings.size() - 1));
                    }
                }, R.id.btn_downLoad);
            }


            @Override
            public int getItemLayoutID() {
                return R.layout.layout_recyclerview_package_item;
            }
        };

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });
    }

    private void initData() {
        buildTypes.add("全部");
        buildTypes.add("正式");
        buildTypes.add("测试");
    }


    @Override
    protected void onResume() {
        super.onResume();
        disableFilter(true);
        registerReceiver();
        presenter.getPackageList();
        presenter.getSpecifiedAPKVersionList(defaultSystemType, selectedApplicationID, selectedVersionType);
    }


    @Override
    public void onLoadPackageListSuccess(List<APKEntity> dataList) {
        Log.i(TAG, "onLoadPackageListSuccess: " + dataList.size());
        disableFilter(false);
        swipeRefreshLayout.setRefreshing(false);
        pkgList.clear();
        APKEntity apkEntity = new APKEntity();
        apkEntity.setApplication_name("全部");
        pkgList.add(0, apkEntity);
        pkgList.addAll(dataList);
        bottomSheetAdapter.notifyDataSetChanged();
    }

    @Override
    public void onLoadAPKListSuccess(List<APKEntity> dataSource) {
        Log.i(TAG, "onLoadAPKListSuccess:dataSource.size() ==  " + dataSource.size());
        apkList.clear();
        apkList.addAll(dataSource);
        for (int i = 0; i < apkList.size(); i++) {
            APKEntity apkEntity = apkList.get(i);
            Log.i(TAG, "onLoadAPKListSuccess: versionName = " + apkEntity.getVersion_name());
            Log.i(TAG, "onLoadAPKListSuccess: VersionType = " + apkEntity.getVersion_type());
        }
        adapter.notifyDataSetChanged();
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
        apkList.clear();
        adapter.notifyDataSetChanged();

    }


    private void disableFilter(boolean flag) {
        tvBuildType.setClickable(!flag);
        tvPackageName.setClickable(!flag);
    }

    @Override
    public void onRefresh() {
        Log.i(TAG, "onRefresh: ");
        presenter.getPackageList();
    }

    @OnClick({R.id.tv_buildType, R.id.tv_packageName})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_buildType:
                showBottomSheetDialog(buildTypes);
                break;
            case R.id.tv_packageName:
                showBottomSheetDialog(pkgList);
                break;
            default:
                break;
        }
    }

    public void showBottomSheetDialog(final ArrayList<String> buildTypes) {
        initBottomSheetDialog();
        CustomRecyclerAdapter<String> bottomSheetAdapter = new CustomRecyclerAdapter<String>(buildTypes) {
            @Override
            public void convert(CommonViewHolder holder, String item, int position) {
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
                if (position == 0){
                    selectedVersionType = null;
                }else {
                    selectedVersionType = buildTypes.get(position);
                }
                tvBuildType.setText(buildTypes.get(position));
                ((TextView) view.findViewById(R.id.tv_packageName)).setTextColor(view.getContext().getResources().getColor(R.color.textSelectedColor));
                doFilter(defaultSystemType, selectedApplicationID, selectedVersionType);
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });
        bottomRecyclerView.setAdapter(bottomSheetAdapter);
    }

    public void showBottomSheetDialog(final List<APKEntity> dataSource) {
        initBottomSheetDialog();
        bottomSheetAdapter = new CustomRecyclerAdapter<APKEntity>(dataSource) {
            @Override
            public void convert(CommonViewHolder holder, APKEntity item, int position) {
                holder.setText(R.id.tv_packageName, item.getApplication_name());
            }

            @Override
            public int getItemLayoutID() {
                return R.layout.layout_bottom_sheet_dialog_item;
            }
        };
        bottomRecyclerView.setAdapter(bottomSheetAdapter);
        bottomRecyclerView.addOnItemTouchListener(new OnRecyclerViewItemClickListener(bottomRecyclerView) {
            @Override
            public void onItemClick(View view, int position) {
                if (position == 0){
                    selectedApplicationID = null;
                }else {
                    selectedApplicationID = dataSource.get(position).getId();
                }
                tvPackageName.setText(dataSource.get(position).getApplication_name());
                ((TextView) view.findViewById(R.id.tv_packageName)).setTextColor(view.getContext().getResources().getColor(R.color.textSelectedColor));
                doFilter(defaultSystemType, selectedApplicationID, selectedVersionType);
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
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_downLoad:

                break;
            case R.id.btn_cancel:
                if (bottomSheetDialog != null) {
                    bottomSheetDialog.dismiss();
                }
                break;
            default:
                break;

        }
    }

    private void doFilter(String system_name, String application_id, String version_type) {
        if (bottomSheetDialog != null && bottomSheetDialog.isShowing()) {
            bottomSheetDialog.dismiss();
        }
        Log.i(TAG, "doFilter: system_name -- " + system_name);
        Log.i(TAG, "doFilter: application_id -- " + application_id);
        Log.i(TAG, "doFilter: version_type -- " + version_type);
        presenter.getSpecifiedAPKVersionList(system_name, application_id, version_type);
    }

    private void installAPK(String download_url, String packageName) {
        Intent serviceIntent = new Intent(MainActivity.this, DownloadService.class);
        //将下载地址url放入intent中
        serviceIntent.setData(Uri.parse(download_url));
        serviceIntent.putExtra(DownloadService.FILE_NAME, packageName);
        startService(serviceIntent);
    }


    private void registerReceiver() {
        receiver = new MyReceiver();
        intentFilter = new IntentFilter(DownloadService.BROADCAST_ACTION);
        intentFilter.addCategory(Intent.CATEGORY_DEFAULT);
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, intentFilter);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }

    private class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            long data = intent.getLongExtra(DownloadService.EXTENDED_DATA_STATUS, 0L);
            Log.i(TAG, "requestID == " + String.valueOf(data));
            String fileName = intent.getStringExtra(DownloadService.FILE_NAME);
            Log.i(TAG, "onReceive:fileName == " + fileName);
            long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            intent = new Intent(Intent.ACTION_VIEW);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            Uri uri = Uri.fromFile(new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/" + fileName));
            Log.i(TAG, "onReceive: uri ==  " + uri);
            intent.setDataAndType(uri, "application/vnd.android.package-archive");
            startActivity(intent);

        }
    }
}
