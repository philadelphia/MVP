package com.meiliwu.installer.view;

import android.content.Context;
import android.view.View;

import com.meiliwu.installer.entity.ISelectable;

import java.util.List;

/**
 * Author:  ZhangTao
 * Date: 2018/3/14.
 */

public class TabItemView {
    private Context context;
    private FilterTabItemView filterTabItemView;
    private MyBottomSheetDialog customBottomSheetDialog;
    private List<ISelectable> dataSource;

    public TabItemView(Context context, FilterTabItemView filterTabItemView, MyBottomSheetDialog customBottomSheetDialog) {
       this.context = context;
        this.filterTabItemView = filterTabItemView;
        this.customBottomSheetDialog = customBottomSheetDialog;

    }

    public TabItemView initBottomSheetDialog(List<ISelectable> dataSource){
        customBottomSheetDialog = new MyBottomSheetDialog(context);
        customBottomSheetDialog.setDataList(dataSource);
        customBottomSheetDialog.setOnItemClickListener(new MyBottomSheetDialog.OnItemClickListener() {
            @Override
            public void ontItemClick(View view, ISelectable packageEntity, int position) {

            }
        });
        return this;
    }

    public void show(boolean flag){
        if (flag){
            customBottomSheetDialog.show();
        }else {
            customBottomSheetDialog.dismiss();
        }
    }
}
