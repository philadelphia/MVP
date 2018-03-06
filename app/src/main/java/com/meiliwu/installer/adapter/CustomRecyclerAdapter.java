package com.meiliwu.installer.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.meiliwu.installer.R;
import com.meiliwu.installer.entity.PackageEntity;

import java.util.List;

import butterknife.ButterKnife;

/**
 * Author:  ZhangTao
 * Date: 2018/3/5.
 */

public abstract class CustomRecyclerAdapter<T> extends RecyclerView.Adapter<CommonViewHolder> {
    private List<T> dataSource;

    public CustomRecyclerAdapter(List<T> dataSource){
        this.dataSource = dataSource;
    }
    @Override
    public CommonViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(getItemLayoutID(), parent, false);
        return new CommonViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(CommonViewHolder holder, int position) {
        convert(holder,dataSource.get(position), position);
    }

    public abstract void convert(CommonViewHolder holder, T item ,int position);
    public abstract int getItemLayoutID();
    @Override
    public int getItemCount() {
        return dataSource == null ? 0 : dataSource.size();
    }

//    public static class MyViewHolder extends RecyclerView.ViewHolder{
//        private ImageView img_icon;
//        private TextView tv_packageName, tv_timeStamp;
//        private Button btn_installer;
//
//        public MyViewHolder(View itemView) {
//            super(itemView);
////            ButterKnife.bind(itemView);
//            img_icon = itemView.findViewById(R.id.img_icon);
//            tv_packageName = itemView.findViewById(R.id.tv_packageName);
//            tv_timeStamp = itemView.findViewById(R.id.tv_timeStamp);
//            btn_installer = itemView.findViewById(R.id.btn_downLoad);
//        }
//    }

//    public void setOnItemClickListener(CustomRecyclerAdapter.OnItemClickListener onItemClickListener) {
//        this.onItemClickListener = onItemClickListener;
//    }
//
//    public  interface OnItemClickListener{
//        void onItemClick(View view);
//    }
}
