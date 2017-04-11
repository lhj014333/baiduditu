package com.feicuiedu.treasure_20170327.commons.user.account;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageView;

import com.feicuiedu.treasure_20170327.R;
import com.feicuiedu.treasure_20170327.commons.ActivityUtils;
import com.feicuiedu.treasure_20170327.commons.user.UserPrefs;
import com.feicuiedu.treasure_20170327.custom.IconSelectWindow;
import com.pkmmte.view.CircularImageView;
import com.squareup.picasso.Picasso;

import org.hybridsquad.android.library.CropHandler;
import org.hybridsquad.android.library.CropHelper;
import org.hybridsquad.android.library.CropParams;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

//个人信息页面
public class AccountActivity extends AppCompatActivity implements AccountView {

    @BindView(R.id.account_toolbar)
    Toolbar toolbar;
    @BindView(R.id.iv_usericon)
    ImageView ivicon;

    private IconSelectWindow iconSelectWindow;
    private ActivityUtils activityUtils;
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
       ButterKnife.bind(this);
          activityUtils=new ActivityUtils(this);
        // toolbar
        setSupportActionBar(toolbar);
        if (getSupportActionBar()!=null){
            getSupportActionBar().setTitle(R.string.account_msg);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        String photo = UserPrefs.getInstance().getPhoto();
        if (photo!=null){
            // 加载头像
            Picasso.with(this)
                    .load(photo)
                    .into(ivicon);
        }
    }


    // 点击头像：弹出一个弹窗(自定义一个PopupWindow)
    @OnClick(R.id.iv_usericon)
    public void showPhotoWindow(){

        if(iconSelectWindow==null){
            iconSelectWindow=new IconSelectWindow(this,listener);
        }
        if(iconSelectWindow.isShowing()){
            iconSelectWindow.dismiss();
        }
        iconSelectWindow.show();
    }
    //跳转的监听
private IconSelectWindow.Listener listener=new IconSelectWindow.Listener() {
       // 到相册
    @Override
    public void toGallery() {
        //清除缓存
        CropHelper.clearCachedCropFile(cropHandler.getCropParams().uri);

        Intent intent =CropHelper.buildCropFromGalleryIntent(cropHandler.getCropParams());
        startActivityForResult(intent,CropHelper.REQUEST_CAMERA);
    }
       //到相机
    @Override
    public void toCamera() {
        //清除之前剪切的图片的缓存
        CropHelper.clearCachedCropFile(cropHandler.getCropParams().uri);
        //跳转
      Intent intent =CropHelper.buildCaptureIntent(cropHandler.getCropParams().uri);
        startActivityForResult(intent,CropHelper.REQUEST_CAMERA);
    }
};
//处理图片剪切的结果
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        CropHelper.handleResult(cropHandler,requestCode,resultCode,data);
    }
    //图片处理
    private CropHandler cropHandler=new CropHandler() {
        //图片剪切后：参数Uri代表剪切后的图片
        @Override
        public void onPhotoCropped(Uri uri) {
         //拿到剪切之后的图片
            File file=new File(uri.getPath());
        }
       //取消
        @Override
        public void onCropCancel() {
       activityUtils.showToast("剪切取消");
        }
        //剪切失败
        @Override
        public void onCropFailed(String message) {
     activityUtils.showToast(message);
        }
      //剪切的参数设置
        @Override
        public CropParams getCropParams() {
            //默认的剪切设置
            CropParams cropParams= new CropParams();
            return cropParams;
        }
      //上下文
        @Override
        public Activity getContext() {
            return AccountActivity.this;
        }
    };

    @Override
    protected void onDestroy() {
        if(cropHandler.getCropParams()!=null){
            CropHelper.clearCachedCropFile(cropHandler.getCropParams().uri);
        }
        super.onDestroy();
    }

    //处理返回箭头的监听
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void showProgress() {
        progressDialog = ProgressDialog.show(this, "头像上传", "正在上传中");
    }

    @Override
    public void hideProgress() {
        if(progressDialog!=null){
            progressDialog.dismiss();
        }

    }

    @Override
    public void showMessage(String msg) {
           activityUtils.showToast(msg);
    }

    @Override
    public void updatePhoto(String photoUrl) {

        if (photoUrl!=null){
            Picasso.with(this)
                    .load(photoUrl)
                    .error(R.mipmap.user_icon)// 加载错误显示的视图
                    .placeholder(R.mipmap.user_icon)// 占位视图
                    .into(ivicon);
        }
    }
}
