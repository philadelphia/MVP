package com.meiliwu.installer.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.meiliwu.installer.R;
import com.meiliwu.installer.service.DownloadService;

import java.util.List;

/**
 * Author:  ZhangTao
 * Date: 2018/3/5.
 */

public abstract class BottomSheetRecyclerAdapter<T> extends RecyclerView.Adapter<CommonViewHolder> {
    private List<T> dataSource;

    public BottomSheetRecyclerAdapter(List<T> dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public CommonViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(getItemLayoutID(), parent, false);
        return new CommonViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(CommonViewHolder holder, int position) {
        convert(holder, dataSource.get(position), position);
    }


    public abstract void convert(CommonViewHolder holder, T item, int position);

    public abstract int getItemLayoutID();

    @Override
    public int getItemCount() {
        return dataSource == null ? 0 : dataSource.size();
    }


//    public static class MyViewHolder extends RecyclerView.ViewHolder{
//        private ImageView img_icon;
//        private TextView tv_packageName, tv_timeStamp;
//        private TextView tv_isDebugVersion;
//        private Button btn_installer;
//
//        public MyViewHolder(View itemView) {
//            super(itemView);
//            img_icon = itemView.findViewById(R.id.img_icon);
//            tv_packageName = itemView.findViewById(R.id.tv_packageName);
//            tv_timeStamp = itemView.findViewById(R.id.tv_timeStamp);
//            tv_isDebugVersion = itemView.findViewById(R.id.tv_isDebugVersion);
//            btn_installer = itemView.findViewById(R.id.btn_downLoad);
//        }
//    }


//    private static class FootViewHolder extends RecyclerView.ViewHolder {
//        ProgressBar pbLoading;
//        TextView tvLoading;
//        LinearLayout llEnd;
//
//        FootViewHolder(View itemView) {
//            super(itemView);
//            pbLoading = (ProgressBar) itemView.findViewById(R.id.progressBar);
//            tvLoading = (TextView) itemView.findViewById(R.id.tv_loadMore);
////            llEnd = (LinearLayout) itemView.findViewById(R.id.ll_end);
//        }
//    }
//
//    /**
//     * 设置上拉加载状态
//     *
//     * @param loadState 0.正在加载 1.加载完成 2.加载到底
//     */
//    public void setLoadState(int loadState) {
//        this.loadState = loadState;
//        notifyDataSetChanged();
//    }

}
