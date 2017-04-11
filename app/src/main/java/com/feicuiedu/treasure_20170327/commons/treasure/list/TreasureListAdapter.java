package com.feicuiedu.treasure_20170327.commons.treasure.list;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.feicuiedu.treasure_20170327.commons.treasure.detail.TreasureDetailActivity;
import com.feicuiedu.treasure_20170327.commons.treasure.map.Treasure;
import com.feicuiedu.treasure_20170327.custom.TreasureView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/4/6.
 */

public class TreasureListAdapter extends RecyclerView.Adapter<TreasureListAdapter.MyViewHolder>{

    List<Treasure> data=new ArrayList<>();


    private OnItemClickListener onItemClickListener;
    public  void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
    // 添加数据的方法
    public void addItemData(List<Treasure> list){
        data.clear();
        data.addAll(list);
        notifyDataSetChanged();
    }


    // 创建ViewHolder
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        TreasureView treasureView= new TreasureView(parent.getContext());
        return new MyViewHolder(treasureView);
    }
    // 数据的绑定
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

         //数据的绑定
        final Treasure treasure = data.get(position);
        holder.treasureview.bindTreasure(treasure);
        //点击事件
        holder.treasureview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 点击卡片的时候直接跳转到宝藏详情页
                TreasureDetailActivity.open(v.getContext(), treasure);
            }
        });
    }



    @Override
    public int getItemCount() {
        return data.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{

        private TreasureView treasureview;
        public MyViewHolder(TreasureView treasureView) {

            super(treasureView);
            this.treasureview=treasureView;
        }
    }
    public interface OnItemClickListener {

        void onItemClick();

        void onItemLongClick();
    }
}
