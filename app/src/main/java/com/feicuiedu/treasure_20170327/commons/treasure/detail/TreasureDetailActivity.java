package com.feicuiedu.treasure_20170327.commons.treasure.detail;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMapOptions;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.navi.BaiduMapNavigation;
import com.baidu.mapapi.navi.NaviParaOption;
import com.feicuiedu.treasure_20170327.R;
import com.feicuiedu.treasure_20170327.commons.ActivityUtils;
import com.feicuiedu.treasure_20170327.commons.treasure.map.MapFragment;
import com.feicuiedu.treasure_20170327.commons.treasure.map.Treasure;
import com.feicuiedu.treasure_20170327.custom.TreasureView;

import java.io.Serializable;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TreasureDetailActivity extends AppCompatActivity implements TreasureDetailView{

    private static String KEY_TREASURE = "key_treasure";
    @BindView(R.id.iv_navigation)
    ImageView ivNavigation;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.frameLayout)
    FrameLayout frameLayout;
    @BindView(R.id.detail_treasure)
    TreasureView detailTreasure;
    @BindView(R.id.tv_detail_description)
    TextView tvDetailDescription;
    private Treasure treasure;
    private ActivityUtils activityUtils;
    private TreasureDetailPresenter treasureDetailPresenter;

    /**
     * 对外提供一个方法，跳转到本页面
     * 规范一下传递的数据：需要什么参数就必须要传入
     */
    public static void open(Context context, Treasure treasure) {
        Intent intent = new Intent(context, TreasureDetailActivity.class);
        intent.putExtra(KEY_TREASURE, treasure);
        context.startActivity(intent);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_treasure_detail);
        activityUtils=new ActivityUtils(this);
        treasureDetailPresenter=new TreasureDetailPresenter(this);
        ButterKnife.bind(this);
        treasure = (Treasure) getIntent().getSerializableExtra(KEY_TREASURE);

        setSupportActionBar(toolbar);
        if(getSupportActionBar()!=null){
            getSupportActionBar().setTitle(treasure.getTitle());
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        //地图的展示
          initMapView();
        //宝藏信息卡片的展示
        detailTreasure.bindTreasure(treasure);
        //网络获取宝藏的详情数据
        TreasureDetail treasureDetail = new TreasureDetail(treasure.getId());
        treasureDetailPresenter.getTreasureDetail(treasureDetail);
    }

    private void initMapView() {
        //宝藏的位置
        LatLng latLng=new LatLng(treasure.getLatitude(),treasure.getAltitude());
        MapStatus mapStatus=new MapStatus.Builder()
                .target(latLng)
                .zoom(18)
                .rotate(0)
                .overlook(-20)
                .build();

        // 地图只是用于展示，没有任何操作
        BaiduMapOptions options = new BaiduMapOptions()
                .mapStatus(mapStatus)
                .compassEnabled(false)
                .zoomControlsEnabled(false)
                .zoomGesturesEnabled(false)
                .scaleControlEnabled(false)
                .rotateGesturesEnabled(false)
                ;
        //创建的地图控件
        MapView mapView = new MapView(this,options);
        //放到布局中
       frameLayout.addView(mapView);
        // 拿到地图的操作类
        BaiduMap map = mapView.getMap();
        //添加一个覆盖物
        BitmapDescriptor descriptor = BitmapDescriptorFactory.fromResource(R.mipmap.treasure_expanded);
        MarkerOptions option = new MarkerOptions()
                .position(latLng)
                .icon(descriptor)
                .anchor(0.5f,0.5f)
                ;
        map.addOverlay(option);
    }

    //返回箭头的事件
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.iv_navigation)
    public void showPopupMenu(View view) {
        // 展示出来一个PopupMenu
        /**
         * 1. 创建一个弹出式菜单
         * 2. 菜单项的填充(布局)
         * 3. 设置菜单项的点击监听
         * 4. 显示
         */
        PopupMenu popupMenu = new PopupMenu(this,view);
        popupMenu.inflate(R.menu.menu_navigation);
        popupMenu.setOnMenuItemClickListener(onMenuItemClickListener);
        popupMenu.show();
    }
    private PopupMenu.OnMenuItemClickListener onMenuItemClickListener=new PopupMenu.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem item) {

            // 不管进行骑行还是步行，都需要起点和终点：坐标和地址
            // 起点：我们定位的位置和地址
            LatLng startPoint= MapFragment.getMyLocation();
            String startAddr=MapFragment.getLocationAddr();
            // 终点：宝藏的位置和地址
            LatLng endPoint=new LatLng(treasure.getLatitude(),treasure.getLongitude());
            String endAddr=treasure.getLocation();

            switch (item.getItemId()){
                //开始步行导航
                case R.id.walking_navi:
                    startWalkingNavi(startPoint,startAddr, endPoint, endAddr);
                    break;
                //开始骑行导航
                case  R.id.biking_navi:
                    startBikingNavi(startPoint, startAddr, endPoint, endAddr);
                    break;
            }
            return false;
        }
    };
    //骑行导航
    private void startBikingNavi(LatLng startPoint,String startAddr,LatLng endPoint,String endAddr) {
        NaviParaOption option=new NaviParaOption()
                .startName(startAddr)
                .startPoint(startPoint)
                .endName(endAddr)
                .endPoint(endPoint)
                ;
        boolean bikeNavi = BaiduMapNavigation.openBaiduMapBikeNavi(option, this);
        if(!bikeNavi){
            startWebNavi(startPoint, startAddr, endPoint, endAddr);
        }
    }


    //步行导航
    private void startWalkingNavi(LatLng startPoint,String startAddr,LatLng endPoint,String endAddr) {

        NaviParaOption option=new NaviParaOption()
                .startName(startAddr)
                .startPoint(startPoint)
                .endName(endAddr)
                .endPoint(endPoint)
                ;
        boolean walkNavi = BaiduMapNavigation.openBaiduMapWalkNavi(option, this);
        if(!walkNavi){
            startWebNavi(startPoint, startAddr, endPoint, endAddr);
        }
    }
    //打开网页导航
    private void startWebNavi(LatLng startPoint, String startAddr, LatLng endPoint, String endAddr) {
        // 起点和终点的设置
        NaviParaOption option = new NaviParaOption()
                .startName(startAddr)
                .startPoint(startPoint)
                .endName(endAddr)
                .endPoint(endPoint);
        BaiduMapNavigation.openWebBaiduMapNavi(option,this);
    }

    @Override
    public void showMessage(String msg) {
        activityUtils.showToast(msg);
    }

    @Override
    public void setDetailDate(List<TreasureDetailResult> list) {
        if(list.size()>=1){
            TreasureDetailResult result = list.get(0);
            tvDetailDescription.setText(result.description);
            return;
        }
        tvDetailDescription.setText("当前宝藏没有信息");

    }
}
