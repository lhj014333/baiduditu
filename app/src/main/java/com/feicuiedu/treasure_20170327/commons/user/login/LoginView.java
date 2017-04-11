package com.feicuiedu.treasure_20170327.commons.user.login;

/**
 * Created by Administrator on 2017/3/28.
 */

public interface LoginView {


    void navigateToHome();

    void showMessage(String msg);

    void hideProgress();

    void showProgress();
}
