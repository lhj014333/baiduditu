package com.feicuiedu.treasure_20170327.commons.user.register;

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

public class RegisterActivity extends AppCompatActivity implements RegisterView {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.et_Username)
    EditText etUsername;
    @BindView(R.id.et_Password)
    EditText etPassword;
    @BindView(R.id.et_Confirm)
    EditText etConfirm;
    @BindView(R.id.btn_Register)
    Button btnRegister;
    private Unbinder bind;
    private ActivityUtils activityUtils;
    private String username;
    private String password;
    private ProgressDialog progress;
    private String confirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

    }

    @Override
    public void onContentChanged() {
        super.onContentChanged();
         activityUtils=new ActivityUtils(this);
        bind = ButterKnife.bind(this);


        setSupportActionBar(toolbar);
        if(getSupportActionBar()!=null){

            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("注册");



        }
        etUsername.addTextChangedListener(textWatcher);
        etPassword.addTextChangedListener(textWatcher);
        etConfirm.addTextChangedListener(textWatcher);
    }
         private TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                username = etUsername.getText().toString();
                password = etPassword.getText().toString();
                confirm = etConfirm.getText().toString();

                boolean canRegister= !(TextUtils.isEmpty(username)
                        ||TextUtils.isEmpty(password)
                        ||TextUtils.isEmpty(confirm))
                        && password.equals(confirm);
                   btnRegister.setEnabled(canRegister);

            }
        };

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case  android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.btn_Register)
    public void onViewClicked() {
        if(RegexUtils.verifyUsername(username)!=RegexUtils.VERIFY_SUCCESS){
            AlertDialogFragment.getInastance(getString(R.string.username_error),getString(R.string.username_rules))
                    .show(getSupportFragmentManager(),"usernameError");
            return;
        }
        if(RegexUtils.verifyPassword(password)!=RegexUtils.VERIFY_SUCCESS){
            AlertDialogFragment.getInastance(getString(R.string.password_error),getString(R.string.password_rules))
                    .show(getSupportFragmentManager(),"passwordError");
            return;
        }

      new RegisterPresenter(this).register(new User(username,password));

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        bind.unbind();
    }

    @Override
    public void navigateToHome() {
        activityUtils.startActivity(HomeActivity.class);
        finish();
        Intent intent = new Intent(MainActivity.MATN_ACTION);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);

    }

    @Override
    public void showMessage(String msg) {
        activityUtils.showToast(msg);

    }

    @Override
    public void hideProgress() {
        progress.dismiss();

    }

    @Override
    public void showProgress() {
        progress = ProgressDialog.show(this, "注册", "正在注册中，请稍后");

    }
}
