package com.feicuiedu.treasure_20170327.commons.treasure.detail;

import android.util.Log;

import com.feicuiedu.treasure_20170327.net.NetClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Administrator on 2017/4/6.
 */

public class TreasureDetailPresenter {
    private TreasureDetailView treasureDetailView;

    public TreasureDetailPresenter(TreasureDetailView treasureDetailView) {
        this.treasureDetailView = treasureDetailView;
    }

    public void getTreasureDetail(TreasureDetail treasureDetail){

    Call<List<TreasureDetailResult>> listCall = NetClient.getInstance().getTreasureApi().getTreasureDetail(treasureDetail);
        Log.e("-------------------",""+listCall);
    listCall.enqueue(callback);
}
    private Callback<List<TreasureDetailResult>> callback=new Callback<List<TreasureDetailResult>>() {
        @Override
        public void onResponse(Call<List<TreasureDetailResult>> call, Response<List<TreasureDetailResult>> response) {
            if(response.isSuccessful()){
                List<TreasureDetailResult> results = response.body();
                if(results==null){
                    treasureDetailView.showMessage("未知错误");
                    return;
                }
                // 数据获取到了，将数据设置给视图(TextView展示)
                treasureDetailView.setDetailDate(results);
            }
        }

        @Override
        public void onFailure(Call<List<TreasureDetailResult>> call, Throwable t) {
               treasureDetailView.showMessage("请求失败："+t.getMessage());
        }
    };




}
