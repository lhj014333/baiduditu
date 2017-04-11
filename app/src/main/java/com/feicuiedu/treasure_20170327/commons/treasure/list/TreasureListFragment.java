package com.feicuiedu.treasure_20170327.commons.treasure.list;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.feicuiedu.treasure_20170327.R;
import com.feicuiedu.treasure_20170327.commons.treasure.map.TreasureRepo;

/**
 * Created by Administrator on 2017/4/1.
 */

public class TreasureListFragment extends Fragment {

    private RecyclerView recyclerView;

    // 宝藏的列表视图
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        // 使用RecyclerView完成列表视图的展示
        /**
         * 1. 创建RecyclerView
         * 2. 设置展示的样式：设置布局管理器
         * 3. 设置背景、动画、分割线等
         * 4. 设置适配器、设置数据
         */
        recyclerView = new RecyclerView(container.getContext());
        //设置布局管理器:GridLayoutManager,LinearLayoutManager,StaggeredGridLayoutManager
        recyclerView.setLayoutManager(new LinearLayoutManager(container.getContext()));
        //设置动画
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        //设置背景
        recyclerView.setBackgroundResource(R.mipmap.screen_bg);
        return recyclerView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //设置适配器和数据
        TreasureListAdapter adapter = new TreasureListAdapter();
        recyclerView.setAdapter(adapter);
        //数据从缓存里边拿到
        adapter.addItemData(TreasureRepo.getInstance().getTreasure());
    }
}
