package com.meiliwu.installer.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.meiliwu.installer.MainActivity;
import com.meiliwu.installer.R;
import com.meiliwu.installer.entity.APKEntity;
import com.meiliwu.installer.entity.PackageEntity;
import com.meiliwu.installer.service.DownloadService;

import org.w3c.dom.Text;

import java.util.Arrays;
import java.util.List;

/**
 * Author:  ZhangTao
 * Date: 2018/3/5.
 */

public abstract class CustomRecyclerAdapter<T> extends RecyclerView.Adapter<CommonViewHolder> {
    private Context context;
    private List<T> dataSource;

    // 普通布局
    private final int TYPE_ITEM = 1;
    // 脚布局
    private final int TYPE_FOOTER = 2;
    // 当前加载状态，默认为加载完成
    private int loadState = 2;
    // 正在加载
    public final int LOADING = 1;
    // 加载完成
    public final int LOADING_COMPLETE = 2;
    // 加载到底
    public final int LOADING_END = 3;

    public CustomRecyclerAdapter(List<T> dataSource) {
//        this.context = context;
        this.dataSource = dataSource;
    }

    @Override
    public CommonViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case TYPE_ITEM:
                View itemView = LayoutInflater.from(parent.getContext()).inflate(getItemLayoutID(), parent, false);
                return new CommonViewHolder(itemView);
            case TYPE_FOOTER:
                View footerView = LayoutInflater.from(parent.getContext()).inflate(getFootViewLayoutID(), parent, false);
                return new CommonViewHolder(footerView);
            default:
                return null;
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == getItemCount() - 1) {
            return TYPE_FOOTER;
        } else {
            return TYPE_ITEM;
        }
    }

    @Override
    public void onBindViewHolder(CommonViewHolder holder, int position) {
        switch (getItemViewType(position)) {
            case TYPE_ITEM:
                convert(holder, dataSource.get(position), position);
                break;
            case TYPE_FOOTER:
                switch (loadState) {
                    case LOADING: // 正在加载
                        holder.setVisible(R.id.progressBar, View.VISIBLE);
                        holder.setVisible(R.id.tv_loadMore, View.VISIBLE);
                        holder.setVisible(R.id.tv_loadEnd, View.GONE);

                        break;

                    case LOADING_COMPLETE: // 加载完成,footerview 隐藏
                        holder.setVisible(R.id.progressBar, View.INVISIBLE);
                        holder.setVisible(R.id.tv_loadMore, View.INVISIBLE);
                        holder.setVisible(R.id.tv_loadEnd, View.GONE);
                        break;

                    case LOADING_END: // 加载到底
                        holder.setVisible(R.id.progressBar, View.GONE);
                        holder.setVisible(R.id.tv_loadMore, View.GONE);
                        holder.setVisible(R.id.tv_loadEnd, View.VISIBLE);
                        break;

                    default:
                        break;
                }


        }
    }


    public abstract void convert(CommonViewHolder holder, T item, int position);

    public abstract int getItemLayoutID();

    public abstract int getFootViewLayoutID();

    @Override
    public int getItemCount() {
        return dataSource == null ? 0 : dataSource.size() + 1;
    }


//    public static class MyViewHolder extends RecyclerView.ViewHolder {
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
//
//
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

    /**
     * 设置上拉加载状态
     *
     * @param loadState 0.正在加载 1.加载完成 2.加载到底
     */
    public void setLoadState(int loadState) {
        this.loadState = loadState;
        notifyDataSetChanged();
    }

}
