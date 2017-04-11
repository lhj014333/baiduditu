package com.feicuiedu.treasure_20170327.commons.user.account;

/**
 * Created by Administrator on 2017/4/10.
 */

public interface AccountView {
    void showProgress();

    void hideProgress();

    void showMessage(String msg);

    void updatePhoto(String photoUrl);
}
