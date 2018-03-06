package com.meiliwu.installer;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.meiliwu.installer.adapter.CommonViewHolder;
import com.meiliwu.installer.adapter.CustomRecyclerAdapter;
import com.meiliwu.installer.entity.PackageEntity;
import com.meiliwu.installer.mvp.MvpContract;
import com.meiliwu.installer.mvp.Presenter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements MvpContract.IView,SwipeRefreshLayout.OnRefreshListener{
    private static final String TAG = "MainActivity";
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;
    private CustomRecyclerAdapter<PackageEntity> adapter;
    private List<PackageEntity> dataSource = new ArrayList<>();
    private Presenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        presenter = new Presenter(this);
        swipeRefreshLayout.setOnRefreshListener(this);
        adapter = new CustomRecyclerAdapter<PackageEntity>(dataSource) {
            @Override
            public void convert(CommonViewHolder holder, PackageEntity packageEntity, int position) {
//                PackageEntity packageEntity = dataSource.get(position);
                Glide.with(holder.itemView.getContext())
                        .load(packageEntity.getIcon_url())
                        .into(((ImageView) holder.getView(R.id.img_icon)));
                holder.setText(R.id.tv_packageName, packageEntity.getApplication_name());
                holder.setText(R.id.tv_timeStamp, packageEntity.getCreate_time());
                holder.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        
                    }
                },R.id.btn_downLoad);
            }

            @Override
            public int getItemLayoutID() {
                return R.layout.layout_recyclerview_package_item;
            }
        };
        
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        presenter.getPackageList();
    }


    @Override
    public void onLoadDataSuccess(List<PackageEntity> dataList) {
        Log.i(TAG, "onLoadDataSuccess: " + dataList.size());
//        Log.i(TAG, "onLoadDataSuccess: " + dataList.get(0).toString());

        swipeRefreshLayout.setRefreshing(false);
        dataSource.clear();
        dataSource.addAll(dataList);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onLoadDataFailed() {
        Log.i(TAG, "onLoadDataFailed: ");
        swipeRefreshLayout.setRefreshing(false);

    }

    @Override
    public void onRefresh() {
        Log.i(TAG, "onRefresh: ");
        presenter.getPackageList();
    }
}
