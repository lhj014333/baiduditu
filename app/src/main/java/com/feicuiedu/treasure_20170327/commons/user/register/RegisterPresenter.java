package com.feicuiedu.treasure_20170327.commons.user.register;

import com.feicuiedu.treasure_20170327.commons.user.UserPrefs;
import com.feicuiedu.treasure_20170327.net.NetClient;
import com.feicuiedu.treasure_20170327.commons.user.User;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Administrator on 2017/3/28.
 */

public class RegisterPresenter {
    private  RegisterView registerView;


    public RegisterPresenter(RegisterView registerView) {
        this.registerView = registerView;
    }


    public  void register( User user){
        NetClient.getInstance().getTreasureApi().register(user).enqueue(new Callback<RegisterResult>() {
            @Override
            public void onResponse(Call<RegisterResult> call, Response<RegisterResult> response) {
                registerView.hideProgress();
                if(response.isSuccessful()){
                    RegisterResult registerResult = response.body();
                    if(registerResult==null){
                        registerView.showMessage("未知的错误");
                        return;
                    }
                    if(registerResult.getCode()==1){
                        //真正的注册成功了
                        //保存Tolenid
                        UserPrefs.getInstance().setTokenid(registerResult.getTokenId());
                        registerView.navigateToHome();
                    }
                    registerView.showMessage(registerResult.getMsg());
                }

            }

            @Override
            public void onFailure(Call<RegisterResult> call, Throwable t) {
                registerView.hideProgress();
                registerView.showMessage("请求失败"+t.getMessage());

            }
        });



    }
}
