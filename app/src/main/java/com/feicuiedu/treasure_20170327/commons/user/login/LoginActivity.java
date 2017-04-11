package com.feicuiedu.treasure_20170327.commons.user.login;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.feicuiedu.treasure_20170327.MainActivity;
import com.feicuiedu.treasure_20170327.R;
import com.feicuiedu.treasure_20170327.commons.ActivityUtils;
import com.feicuiedu.treasure_20170327.commons.RegexUtils;
import com.feicuiedu.treasure_20170327.commons.treasure.HomeActivity;
import com.feicuiedu.treasure_20170327.custom.AlertDialogFragment;
import com.feicuiedu.treasure_20170327.commons.user.User;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class LoginActivity extends AppCompatActivity implements LoginView{

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.et_Username)
    EditText etUsername;
    @BindView(R.id.et_Password)
    EditText etPassword;
    @BindView(R.id.tv_forgetPassword)
    TextView tvForgetPassword;
    @BindView(R.id.btn_Login)
    Button btnLogin;
    private Unbinder bind;
    private String username;
    private String password;
    private ActivityUtils activityutils;
    private ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);



    }

    @Override
    public void onContentChanged() {
        super.onContentChanged();
        activityutils=new ActivityUtils(this);
        bind = ButterKnife.bind(this);

        // toolbar
        // Toolbar作为ActionBar展示
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            // 设置返回的箭头,内部是选项菜单来处理的，而且Android内部已经给他设置好了id
            // android.R.id.home
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            //设置标题
            getSupportActionBar().setTitle(R.string.login);
        }
        etUsername.addTextChangedListener(textWatcher);
        etPassword.addTextChangedListener(textWatcher);


    }

    private TextWatcher textWatcher = new TextWatcher() {
        //文本变化前
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        //  文本变化时
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        //文本变化后
        @Override
        public void afterTextChanged(Editable s) {
            username = etUsername.getText().toString();
            password = etPassword.getText().toString();
            //判断用户名和密码不为空 ，登录按钮才可以点击
            boolean canLogin = !(TextUtils.isEmpty(username) || TextUtils.isEmpty(password));
                 btnLogin.setEnabled(canLogin);

        }
    };
    // 重写选项菜单的选中监听


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            //处理AcationBar上面的返回箭头的事件
            case android.R.id.home:
                finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.btn_Login)
    public void onViewClicked() {
        //账号不符合规范
      if(RegexUtils.verifyUsername(username)!=RegexUtils.VERIFY_SUCCESS){
          AlertDialogFragment.getInastance(getString(R.string.username_error),getString(R.string.username_rules))
                  .show(getSupportFragmentManager(),"usernameError");

        return;
     }
        //密码不符合规范
        if(RegexUtils.verifyPassword(password)!=RegexUtils.VERIFY_SUCCESS){
            AlertDialogFragment.getInastance(getString(R.string.password_error),getString(R.string.password_rules))
                    .show(getSupportFragmentManager(),"passwordError");
            return;
        }

   new LoginPresenter(this).login(new User(username,password));

    }

    public void navigateToHome() {
        activityutils.startActivity(HomeActivity.class);
        finish();
         Intent intent=new Intent(MainActivity.MATN_ACTION);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);

    }

    public void showMessage(String message) {
        activityutils.showToast(message);
    }

    public void hideProgress() {
          if(progress!=null){
              progress.dismiss();
          }
    }

    public void showProgress() {
        progress = ProgressDialog.show(this, "登录", "正在登录中，请稍后");

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        bind.unbind();
    }
}
