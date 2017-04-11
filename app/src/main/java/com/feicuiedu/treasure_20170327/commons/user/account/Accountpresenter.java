package com.feicuiedu.treasure_20170327.commons.user.account;

import com.feicuiedu.treasure_20170327.commons.user.UserPrefs;
import com.feicuiedu.treasure_20170327.net.NetClient;

import java.io.File;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.Multipart;

/**
 * Created by Administrator on 2017/4/10.
 */
//个人信息的业务类
public class Accountpresenter {
    private AccountView accountView;

    public Accountpresenter(AccountView accountView) {
        this.accountView = accountView;
    }

    //上传及更新头像
    public void uploadPhoto(File file){
        accountView.showProgress();
       MultipartBody.Part part= MultipartBody.Part.createFormData("file","photo.png", RequestBody.create(null,file));

        Call<UpLoadResult> uploadCall = NetClient.getInstance().getTreasureApi().upload(part);

        uploadCall.enqueue(resultCall);
    }
    private Callback<UpLoadResult> resultCall=new Callback<UpLoadResult>() {
        @Override
        public void onResponse(Call<UpLoadResult> call, Response<UpLoadResult> response) {
           accountView.hideProgress();
            if(response.isSuccessful()){
                UpLoadResult upLoadResult = response.body();
                if(upLoadResult==null){
                    accountView.showMessage("未知的错误");
                    return;
                }
                //显示信息
                accountView.showMessage(upLoadResult.getMsg());
                if (upLoadResult.getCount()!=1){
                    return;
                }
                // 可以拿到头像地址数据
                String photoUrl = upLoadResult.getUrl();
                // 可以存储到用户仓库里面、展示出来
                UserPrefs.getInstance().setPhoto(NetClient.BASE_URL+photoUrl);
                // 将上传的头像在页面上展示出来
                accountView.updatePhoto(NetClient.BASE_URL+photoUrl);

                // 更新数据
                // 需要截取一下
                String substring = photoUrl.substring(photoUrl.lastIndexOf("/") + 1, photoUrl.length());
                Update update = new Update(UserPrefs.getInstance().getTokenid(),substring);
                Call<UpdateResult> resultCall = NetClient.getInstance().getTreasureApi().update(update);
                resultCall.enqueue(mUpdateCallback);
            }
        }

        @Override
        public void onFailure(Call<UpLoadResult> call, Throwable t) {

            //隐藏进度条
            accountView.hideProgress();
            //提示信息
            accountView.showMessage("上传失败："+t.getMessage());
        }
    };
    private Callback<UpdateResult> mUpdateCallback=new Callback<UpdateResult>() {
        @Override
        public void onResponse(Call<UpdateResult> call, Response<UpdateResult> response) {
            accountView.hideProgress();
            if(response.isSuccessful()){
                UpdateResult updateResult = response.body();
                if (updateResult==null){
                    accountView.showMessage("未知的错误");
                    return;
                }
                accountView.showMessage(updateResult.getMsg());
                if (updateResult.getCode()!=1){
                    return;
                }
            }
        }

        @Override
        public void onFailure(Call<UpdateResult> call, Throwable throwable) {
                 accountView.hideProgress();
            accountView.showMessage("更新失败："+throwable.getMessage());
        }
    };
}
