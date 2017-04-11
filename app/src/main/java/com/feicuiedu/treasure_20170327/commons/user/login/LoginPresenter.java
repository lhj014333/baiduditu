package com.feicuiedu.treasure_20170327.commons.user.login;



import com.feicuiedu.treasure_20170327.commons.user.UserPrefs;
import com.feicuiedu.treasure_20170327.net.NetClient;
import com.feicuiedu.treasure_20170327.commons.user.User;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * Created by Administrator on 2017/3/28.
 */

public class LoginPresenter {
    private LoginView loginView;


    public LoginPresenter(LoginView loginView) {
        this.loginView = loginView;
    }

    public void login(User user){
      NetClient.getInstance().getTreasureApi().login(user).enqueue(new Callback<LoginResult>() {
          @Override
          public void onResponse(Call<LoginResult> call, Response<LoginResult> response) {
             loginView.hideProgress();
              if(response.isSuccessful()){
                  LoginResult loginResult = response.body();
                  if(loginResult==null){
                      loginView.showMessage("未知的错误");
                      return;
                  }
                  if(loginResult.getCode()==1){
                      UserPrefs.getInstance().setPhoto(NetClient.BASE_URL+loginResult.getHeadpic());
                      UserPrefs.getInstance().setTokenid(loginResult.getTokenid());
                      loginView.navigateToHome();
                  }
                  loginView.showMessage(loginResult.getMsg());
              }

          }

          @Override
          public void onFailure(Call<LoginResult> call, Throwable t) {
          loginView.hideProgress();
              loginView.showMessage("请求失败"+t.getMessage());
          }
      });











    }
}
