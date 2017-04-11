package com.feicuiedu.treasure_20170327.commons.treasure.hide;

import com.feicuiedu.treasure_20170327.net.NetClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Administrator on 2017/4/7.
 */
//埋藏宝藏的业务
public class HideTreasurePresenter {
   private HideTreasureView hideTreasureview;

    public HideTreasurePresenter(HideTreasureView hideTreasureview) {
        this.hideTreasureview = hideTreasureview;
    }

    public void hideTreasure(HideTreasure hideTreasure){
        //显示进度条
        hideTreasureview.showProgress();
        Call<HideTreasureResult> resultCall = NetClient.getInstance().getTreasureApi().hideTreasure(hideTreasure);
        resultCall.enqueue(ResultCallback);

    }
    private Callback<HideTreasureResult> ResultCallback=new Callback<HideTreasureResult>() {
        @Override
        public void onResponse(Call<HideTreasureResult> call, Response<HideTreasureResult> response) {
            //隐藏进度条
            hideTreasureview.hideProgress();
            if(response.isSuccessful()){
                HideTreasureResult treasureResult = response.body();
                if(treasureResult==null){
                    hideTreasureview.showMessage("未知的错误");
                    return;
                }
                if(treasureResult.getCode()==1){
                    //跳转到Home页面
                    hideTreasureview.navigateToHome();
                }
                //提示信息
                hideTreasureview.showMessage(treasureResult.getMsg());
            }
        }

        @Override
        public void onFailure(Call<HideTreasureResult> call, Throwable t) {
                hideTreasureview.hideProgress();
            hideTreasureview.showMessage("请求失败："+t.getMessage());
        }
    };

}
