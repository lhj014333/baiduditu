package com.feicuiedu.treasure_20170327.commons.treasure.map;

import com.feicuiedu.treasure_20170327.net.NetClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Administrator on 2017/4/5.
 */
// 获取宝藏数据的业务类
public class MapPresenter {

    private MapMvpView mapMvpView;
    private Area areas;

    public MapPresenter(MapMvpView mapMvpView) {
        this.mapMvpView = mapMvpView;
    }
     //获取宝藏数据
    public void getTreasure(Area area){


        // 当前区域已经缓存过，就不再去请求
        if (TreasureRepo.getInstance().isCached(area)){
            return;
        }
        areas = area;
      Call<List<Treasure>> listCall = NetClient.getInstance().getTreasureApi().getTreasureInArea(area);
        listCall.enqueue(listCallback);

  }
    private Callback<List<Treasure>> listCallback=new Callback<List<Treasure>>() {
        //请求成功
        @Override
        public void onResponse(Call<List<Treasure>> call, Response<List<Treasure>> response) {
            if(response.isSuccessful()){
                //拿到响应体数据
                List<Treasure> body = response.body();
                if(body==null){
                    mapMvpView.showmessage("未知错误");
                    return;
                }
                // 做一个缓存:缓存请求的数据和区域
                TreasureRepo.getInstance().addTreasure(body);
                TreasureRepo.getInstance().cache(areas);
                //拿到数据给MapFragment上，在地图上显示
                mapMvpView.setTreasureData(body);

            }
        }
        //请求失败
        @Override
        public void onFailure(Call<List<Treasure>> call, Throwable t) {
             mapMvpView.showmessage("请求失败"+t.getMessage());
        }
    };


}
