package com.feicuiedu.treasure_20170327.commons.treasure.hide;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;

import com.baidu.mapapi.model.LatLng;
import com.feicuiedu.treasure_20170327.R;
import com.feicuiedu.treasure_20170327.commons.ActivityUtils;
import com.feicuiedu.treasure_20170327.commons.treasure.map.TreasureRepo;
import com.feicuiedu.treasure_20170327.commons.user.UserPrefs;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class HideTreasureActivity extends AppCompatActivity implements HideTreasureView{


    @BindView(R.id.hide_send)
    ImageView hideSend;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.et_description)
    EditText etDescription;
    private static final String KEY_TITLE="key_title";
    private static final String KEY_ADDRESS="key_address";
    private static final String KEY_LATLNG="key_latlng";
    private static final String KEY_ALTITUDE="key_altitude";
    private ActivityUtils activityUtils;
    private ProgressDialog progressDialog;

    //对外提供一个跳转的方法
    public static void open(Context context, String title, String address, LatLng latLng,double altitude){
        Intent intent = new Intent(context,HideTreasureActivity.class);
        intent.putExtra(KEY_TITLE,title);
        intent.putExtra(KEY_ADDRESS,address);
        intent.putExtra(KEY_LATLNG,latLng);
       intent.putExtra(KEY_ALTITUDE,altitude);
        context.startActivity(intent);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hide_treasure);
        ButterKnife.bind(this);
        activityUtils=new ActivityUtils(this);

        //toolbar
        setSupportActionBar(toolbar);
        if(getSupportActionBar()!= null){
            getSupportActionBar().setTitle(getIntent().getStringExtra(KEY_TITLE));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    //宝藏的上传
    @OnClick(R.id.hide_send)
    public void sendTreasure(){
        //取出传替的数据
        Intent intent = getIntent();
        String title = intent.getStringExtra(KEY_TITLE);
        String address= intent.getStringExtra(KEY_ADDRESS);
        LatLng latlng= intent.getParcelableExtra(KEY_LATLNG);
        double altitude = intent.getDoubleExtra(KEY_ALTITUDE,0);
         // 拿到用户的TokenId
        int tokenid = UserPrefs.getInstance().getTokenid();
        //拿到描述信息
        String desc = etDescription.getText().toString();
        //需要上传的请求体数据
        HideTreasure hideTreasure=new HideTreasure();
        hideTreasure.setTitle(title);//标题
        hideTreasure.setAltitude(altitude);//海拔
        hideTreasure.setDescription(desc);//描述信息
        hideTreasure.setLatitude(latlng.latitude);//纬度
        hideTreasure.setLongitude(latlng.longitude);//经度
        hideTreasure.setLocation(address);//地址
        hideTreasure.setTokenId(tokenid);//用户令牌

        //埋藏宝藏的网络请求
        new HideTreasurePresenter(this).hideTreasure(hideTreasure);
    }

    @Override
    public void showMessage(String msg) {
        activityUtils.showToast(msg);
    }

    @Override
    public void showProgress() {
        progressDialog = ProgressDialog.show(this, "宝藏上传", "宝藏正在上传中，请稍后~~~");
    }

    @Override
    public void hideProgress() {
        if(progressDialog!=null){
            progressDialog.dismiss();
        }

    }

    @Override
    public void navigateToHome() {
      finish();
        //清除缓存：为了返回到之前的页面重新加载数据
        TreasureRepo.getInstance().clear();
    }
}
