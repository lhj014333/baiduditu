package com.feicuiedu.treasure_20170327.commons.treasure.map;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Space;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMapOptions;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.feicuiedu.treasure_20170327.R;
import com.feicuiedu.treasure_20170327.commons.ActivityUtils;
import com.feicuiedu.treasure_20170327.commons.treasure.detail.TreasureDetailActivity;
import com.feicuiedu.treasure_20170327.commons.treasure.hide.HideTreasureActivity;
import com.feicuiedu.treasure_20170327.custom.TreasureView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by Administrator on 2017/4/1.
 */

public class MapFragment extends Fragment implements MapMvpView {

    @BindView(R.id.center)
    Space center;
    @BindView(R.id.iv_located)
    ImageView ivLocated;
    @BindView(R.id.btn_HideHere)
    Button btnHideHere;
    @BindView(R.id.centerLayout)
    RelativeLayout centerLayout;
    @BindView(R.id.iv_scaleUp)
    ImageView ivScaleUp;
    @BindView(R.id.iv_scaleDown)
    ImageView ivScaleDown;
    @BindView(R.id.tv_located)
    TextView tvLocated;
    @BindView(R.id.tv_satellite)
    TextView tvSatellite;
    @BindView(R.id.tv_compass)
    TextView tvCompass;
    @BindView(R.id.ll_locationBar)
    LinearLayout llLocationBar;
    @BindView(R.id.layout_bottom)
    FrameLayout layoutBottom;
    @BindView(R.id.map_frame)
    FrameLayout mapFrame;
    @BindView(R.id.treasureView)
    TreasureView treasureView;
    @BindView(R.id.hide_treasure)
    RelativeLayout hidetreasure;
    Unbinder unbinder;
    @BindView(R.id.tv_currentLocation)
    TextView tvCurrentLocation;
    Unbinder unbinder1;
    @BindView(R.id.iv_toTreasureInfo)
    ImageView ivToTreasureInfo;
    @BindView(R.id.et_treasureTitle)
    EditText etTreasureTitle;
    @BindView(R.id.cardView)
    CardView cardView;
    private MapView mapView;
    private BaiduMap mapViewMap;
    private LocationClient locationClient;
    private boolean isFist = true;
    private static LatLng currentLocation;
    private static final int LOCATION_REQUEST_CODE = 100;
    private LatLng currentStatus;
    private MapPresenter mapPresenter;
    private ActivityUtils activityUtils;
    private Marker currentMarker;
    private static String currentAddr;
    private GeoCoder geoCoder;
    private String geoCoderAddr;


    // 地图和宝藏的展示
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, null);
        //检测权限有没有授权成功
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // 没有成功，需要向用户申请
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}
                    , LOCATION_REQUEST_CODE);


        }
        unbinder1 = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TreasureRepo.getInstance().clear();
        unbinder = ButterKnife.bind(this, view);
        activityUtils = new ActivityUtils(this);
        mapPresenter = new MapPresenter(this);
        initmapview();
        initLocation();
        //初始化地理编码相关
        initGeoCoder();
    }

    private void initGeoCoder() {
        //1.创建地理编码检索实例
        geoCoder = GeoCoder.newInstance();
        //2.设置地理编码检索实例的监听
        geoCoder.setOnGetGeoCodeResultListener(onGetGeoCoderResultListener);


    }

    //地理编码的监听者
    private OnGetGeoCoderResultListener onGetGeoCoderResultListener = new OnGetGeoCoderResultListener() {
        //获取地理编码结果
        @Override
        public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {

        }

        //获取地理反向编码结果
        @Override
        public void onGetReverseGeoCodeResult(ReverseGeoCodeResult reverseGeoCodeResult) {
            //判断结果是否取到
            if (reverseGeoCodeResult == null || reverseGeoCodeResult.error != SearchResult.ERRORNO.NO_ERROR) {
                geoCoderAddr = "未知的地址";
                tvCurrentLocation.setText(geoCoderAddr);
                return;
            }
            //拿到地址信息
            geoCoderAddr = reverseGeoCodeResult.getAddress();
            //将地址信息设置给TextView
            tvCurrentLocation.setText(geoCoderAddr);
        }
    };

    private void initLocation() {
        // 前置：激活定位图层
        mapViewMap.setMyLocationEnabled(true);
        // 1. 第一步，初始化LocationClient类
        locationClient = new LocationClient(getContext().getApplicationContext());
        // 2. 第二步，配置定位SDK参数
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true);// 打开GPS
        option.setCoorType("bd09ll");// 设置坐标类型，默认gcj02，会有偏差，设置返回的定位结果坐标系
        // 设置参数给LocationClient
        locationClient.setLocOption(option);

        // 3. 第三步，实现BDLocationListener接口
        locationClient.registerLocationListener(mBDLocationListener);

        // 4. 第四步，开始定位
        locationClient.start();

    }

    // 定位监听
    private BDLocationListener mBDLocationListener = new BDLocationListener() {
        // 当获取到定位数据的时候会触发
        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            if (bdLocation == null) {
                // 没有拿到数据，可以重新进行请求
                locationClient.requestLocation();
                return;
            }

            // 拿到定位的经纬度
            double latitude = bdLocation.getLatitude();
            double longitude = bdLocation.getLongitude();

            // 定位的位置和地址
            currentLocation = new LatLng(latitude, longitude);
            currentAddr = bdLocation.getAddrStr();

            Log.i("TAG", "定位的位置：" + currentAddr + "经纬度：" + latitude + "," + longitude);

            // 地图上设置定位数据
            MyLocationData locationData = new MyLocationData.Builder()
                    // 设置定位的经纬度
                    .latitude(latitude)
                    .longitude(longitude)
                    .accuracy(100f)// 定位精度的大小
                    .build();

            mapViewMap.setMyLocationData(locationData);
            //第一次进入将地图自动移动到定位的位置
            if (isFist) {
                moveToLocation();
                isFist = false;
            }
        }


    };

    @OnClick(R.id.tv_located)
    public void moveToLocation() {
        //更新的是地图的状态
        MapStatus mapStatus = new MapStatus.Builder()
                .target(currentLocation)//定位的位置
                .rotate(0)
                .overlook(0)
                .zoom(19)
                .build();
        //更新的状态
        MapStatusUpdate update = MapStatusUpdateFactory.newMapStatus(mapStatus);
        //利用地图操作类更新地图的状态
        mapViewMap.animateMapStatus(update);


    }

    //宝藏信息卡片的点击事件
    @OnClick(R.id.treasureView)
    public void clickTreasureView() {
        //跳转到详情页，展示宝藏信息，将宝藏数据传替过去
        int id = currentMarker.getExtraInfo().getInt("id");
        Treasure treasure = TreasureRepo.getInstance().getTreasure(id);
        TreasureDetailActivity.open(getContext(), treasure);
    }

    @OnClick(R.id.hide_treasure)
    public void hideTreasure() {
        //拿到录入的标题
        String title = etTreasureTitle.getText().toString();
          if(TextUtils.isEmpty(title)){
            activityUtils.showToast("请输入宝藏标题");
              return;
          }
        //输入了标题：跳转到埋藏宝藏详细界面
        LatLng latlng=mapViewMap.getMapStatus().target;
        HideTreasureActivity.open(getContext(),title,geoCoderAddr,latlng,0);
    }


    // 初始化百度地图的操作
    private void initmapview() {
        // 地图的状态
        MapStatus mapStatus = new MapStatus.Builder()
                .rotate(0)// 旋转的角度
                .zoom(15)// 默认是12，范围3-21
                .overlook(0)// 俯仰的角度
                .build();
        // 设置地图的信息
        BaiduMapOptions options = new BaiduMapOptions()
                .mapStatus(mapStatus)
                .compassEnabled(true)// 是否显示指南针，默认显示
                .zoomGesturesEnabled(true)// 是否允许缩放手势
                .scaleControlEnabled(false)// 不显示比例尺
                .zoomControlsEnabled(false)// 不显示缩放的控件
                ;
        // 创建地图控件
        mapView = new MapView(getContext(), options);
        // 在布局中添加地图的控件：0，放置在第一位
        mapFrame.addView(mapView, 0);
        // 拿到地图的操作类(设置地图的视图、地图状态变化、添加覆盖物等)
        mapViewMap = mapView.getMap();

        //设置地图状态的监听
        mapViewMap.setOnMapStatusChangeListener(onMapStatusChangeListener);
        // 设置地图上的覆盖物的点击监听
        mapViewMap.setOnMarkerClickListener(onMarkerClickListener);


    }

    //覆盖物的点击监听
    private BaiduMap.OnMarkerClickListener onMarkerClickListener = new BaiduMap.OnMarkerClickListener() {
        // 点击Marker会触发：marker当前点击的
        @Override
        public boolean onMarkerClick(Marker marker) {
            currentMarker = marker;
            if (currentMarker != null) {
                if (currentMarker != marker) {
                    currentMarker.setVisible(true);// 点击了其他的，把之前的显示出来
                }
            }
            // 点击展示InfoWindow，当前的覆盖物不可见
            currentMarker.setVisible(false);
            // 1. 创建InfoWindow
            InfoWindow infoWindow = new InfoWindow(dot_expand, marker.getPosition(), 0, new InfoWindow.OnInfoWindowClickListener() {
                @Override
                public void onInfoWindowClick() {
                    changeUIMode(UI_MODE_NORMAL);//切换到普通视图
                }
            });
            //地图上显示
            mapViewMap.showInfoWindow(infoWindow);
            //宝藏信息的读取
            int id = marker.getExtraInfo().getInt("id");
            Treasure treasure = TreasureRepo.getInstance().getTreasure(id);
            treasureView.bindTreasure(treasure);

            changeUIMode(UI_MODE_SELECT);
            return false;
        }
    };
    private BaiduMap.OnMapStatusChangeListener onMapStatusChangeListener = new BaiduMap.OnMapStatusChangeListener() {
        //变化时
        @Override
        public void onMapStatusChangeStart(MapStatus mapStatus) {

        }

        //变化中
        @Override
        public void onMapStatusChange(MapStatus mapStatus) {

        }

        //变化后
        @Override
        public void onMapStatusChangeFinish(MapStatus mapStatus) {
            // 拿到当前移动后的地图状态所在的位置
            LatLng target = mapStatus.target;
            // 地图状态确实发生变化了
            if (target != MapFragment.this.currentStatus) {

                // 根据当前的地图的状态来获取当前的区域内的宝藏数据
                updateMapArea();
                //在埋宝藏的时候
                if (mUIMode == UI_MODE_HIDE) {
                    //设置反地理编码的参数：位置（当前的经纬度）
                    ReverseGeoCodeOption option = new ReverseGeoCodeOption();
                    option.location(target);
                    //发起反地理编码：经纬度----》地址
                    geoCoder.reverseGeoCode(option);
                }

                // 当前地图的位置
                MapFragment.this.currentStatus = target;
            }


        }
    };

    // 区域的确定和宝藏数据的获取
    private void updateMapArea() {

        // 拿到当前的地图状态
        MapStatus mapStatus = mapViewMap.getMapStatus();

        // 从中拿到当前地图的经纬度
        double longitude = mapStatus.target.longitude;
        double latitude = mapStatus.target.latitude;

        // 根据当前的经纬度来确定区域
        Area area = new Area();

        // 根据当前经纬度向上和向下取整得到的区域
        area.setMaxLat(Math.ceil(latitude));
        area.setMaxLng(Math.ceil(longitude));
        area.setMinLng(Math.floor(longitude));
        area.setMinLat(Math.floor(latitude));

        // 要根据当前的区域来获取了：进行网络请求
        mapPresenter.getTreasure(area);
    }


    @OnClick(R.id.tv_satellite)
    public void switchType() {
        int mapType = mapViewMap.getMapType();
        mapType = (mapType == BaiduMap.MAP_TYPE_NORMAL) ? BaiduMap.MAP_TYPE_SATELLITE : BaiduMap.MAP_TYPE_NORMAL;

        String msg = (mapType == BaiduMap.MAP_TYPE_NORMAL) ? "卫星" : "普通";
        mapViewMap.setMapType(mapType);
        tvSatellite.setText(msg);
    }

    @OnClick(R.id.tv_compass)
    public void switchCompass() {
        boolean enabled = mapViewMap.getUiSettings().isCompassEnabled();
        mapViewMap.getUiSettings().setCompassEnabled(!enabled);
    }

    @OnClick({R.id.iv_scaleDown, R.id.iv_located})
    public void scaleMap(View view) {
        switch (view.getId()) {
            case R.id.iv_located:
                mapViewMap.setMapStatus(MapStatusUpdateFactory.zoomIn());
                break;
            case R.id.iv_scaleDown:
                mapViewMap.setMapStatus(MapStatusUpdateFactory.zoomOut());
                break;
        }


    }

    /**
     * 视图的切换方法：根据各个控件的显示和隐藏来实现视图的切换
     * 普通的视图
     * 宝藏选中的视图
     * 埋藏宝藏的视图
     */
    private static final int UI_MODE_NORMAL = 0;// 普通视图
    private static final int UI_MODE_SELECT = 1;// 宝藏选中视图
    private static final int UI_MODE_HIDE = 2;// 埋藏宝藏视图

    private static int mUIMode = UI_MODE_NORMAL;// 当前的视图

    public void changeUIMode(int uiMode) {
        if (uiMode == mUIMode) return;
        mUIMode = uiMode;
        switch (uiMode) {
            //切换为普通视图
            case UI_MODE_NORMAL:
                if (currentMarker != null) {
                    currentMarker.setVisible(true);
                }
                mapViewMap.hideInfoWindow();
                layoutBottom.setVisibility(View.GONE);
                centerLayout.setVisibility(View.GONE);
                break;
            //切换为选中视图
            case UI_MODE_SELECT:
                layoutBottom.setVisibility(View.VISIBLE);
                treasureView.setVisibility(View.VISIBLE);
                centerLayout.setVisibility(View.GONE);
                hidetreasure.setVisibility(View.GONE);
                break;
            //切换为埋藏宝藏视图
            case UI_MODE_HIDE:
                if (currentMarker != null) {
                    currentMarker.setVisible(true);
                }
                mapViewMap.hideInfoWindow();
                centerLayout.setVisibility(View.VISIBLE);
                layoutBottom.setVisibility(View.GONE);
                btnHideHere.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        layoutBottom.setVisibility(View.VISIBLE);
                        treasureView.setVisibility(View.GONE);
                        hidetreasure.setVisibility(View.VISIBLE);
                    }
                });

                break;
        }


    }


    //处理权限的回调

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case LOCATION_REQUEST_CODE:

                // 用户授权成功了
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // 定位了
                    locationClient.requestLocation();
                } else {
                    // 显示个吐司、提示框
                }
                break;
        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void showmessage(String msg) {
        activityUtils.showToast(msg);
    }

    @Override
    public void setTreasureData(List<Treasure> treasures) {

        for (Treasure treasure : treasures) {

            //拿到每一个宝藏数据、将宝藏信息以飞高物的形式添加到地图上
            LatLng latLng = new LatLng(treasure.getLatitude(), treasure.getLongitude());
            addMarker(latLng, treasure.getId());
        }
    }

    // 将定位的位置返回出去，供其它调用
    public static LatLng getMyLocation() {
        return currentLocation;
    }

    public static String getLocationAddr() {
        return currentAddr;
    }

    //添加覆盖物的图标
    private BitmapDescriptor dot = BitmapDescriptorFactory.fromResource(R.mipmap.treasure_dot);
    private BitmapDescriptor dot_expand = BitmapDescriptorFactory.fromResource(R.mipmap.treasure_expanded);

    //覆盖物的方法
    private void addMarker(LatLng latLng, int treasureId) {
        // 根据宝藏的经纬度、id 添加覆盖物
        MarkerOptions options = new MarkerOptions()
                .position(latLng)// 覆盖物添加的位置
                .icon(dot)// 覆盖物的图标
                .anchor(0.5f, 0.5f);// 锚点位置：居中
        // 将宝藏的id信息也一并存到覆盖物里面
        Bundle bundle = new Bundle();
        bundle.putInt("id", treasureId);
        options.extraInfo(bundle);
        mapViewMap.addOverlay(options);

    }

    //对外提供一个方法，什么时候可以退出
    public boolean clickBackPressed() {
        if (mUIMode != UI_MODE_NORMAL) {
            changeUIMode(UI_MODE_NORMAL);
            return false;
        }
        return true;
    }
}
