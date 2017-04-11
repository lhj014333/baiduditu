package com.feicuiedu.treasure_20170327.commons.treasure;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.feicuiedu.treasure_20170327.MainActivity;
import com.feicuiedu.treasure_20170327.R;
import com.feicuiedu.treasure_20170327.commons.ActivityUtils;
import com.feicuiedu.treasure_20170327.commons.treasure.list.TreasureListFragment;
import com.feicuiedu.treasure_20170327.commons.treasure.map.MapFragment;
import com.feicuiedu.treasure_20170327.commons.user.UserPrefs;
import com.feicuiedu.treasure_20170327.commons.user.account.AccountActivity;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;




public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.navigation)
    NavigationView navigation;
    @BindView(R.id.drawerLayout)
    DrawerLayout drawerLayout;
    private ImageView icon;
    private MapFragment mapFragment;
    private TreasureListFragment treasureListfragment;
    private FragmentManager fragmentManager;
    private ActivityUtils activityUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);
    activityUtils=new ActivityUtils(this);
        fragmentManager=getSupportFragmentManager();
    mapFragment= (MapFragment) fragmentManager.findFragmentById(R.id.mapFragment);

        setSupportActionBar(toolbar);
        if(getSupportActionBar()!=null){
            // 不显示默认的标题，而是显示布局中自己加的TextView
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        //侧滑的监听
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        //同步状态
              toggle.syncState();
        drawerLayout.addDrawerListener(toggle);

        //侧滑菜单item的选择监听
        navigation.setNavigationItemSelectedListener(this);
        // 找到侧滑的头布局里面的头像


        icon = (ImageView) navigation.getHeaderView(0).findViewById(R.id.iv_usericon);
        icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
          activityUtils.startActivity(AccountActivity.class);
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        String photo=UserPrefs.getInstance().getPhoto();
        if(photo==null){
            Picasso
                    .with(this)
                    .load(photo)
                    .into(icon);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_hide:
                //埋藏宝藏的时候
             mapFragment.changeUIMode(2);//切换到埋藏宝藏的视图
                break;
            case R.id.menu_logout:
                //退出的时候
                //清空用户信息数据
              UserPrefs.getInstance().clearUser();
                //返回到main界面
                activityUtils.startActivity(MainActivity.class);
                finish();
                break;
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
    // 准备工作：完成选项菜单的图标的切换等

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        //item图标的变化处理
        MenuItem item = menu.findItem(R.id.action_toggle);
        // 根据显示的视图不一样，设置不一样的图标
        if(treasureListfragment!=null&&treasureListfragment.isAdded()){
            item.setIcon(R.drawable.ic_map);
        }else {
            item.setIcon(R.drawable.ic_view_list);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    // 创建：选项菜单的布局填充

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //菜单的填充
        getMenuInflater().inflate(R.menu.menu_home,menu);
        return true;
    }
    // 某一个选项菜单被选择的时候(点击)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_toggle:

                // 切换视图：地图的视图和列表的视图进行切换
                showListFragment();

                // 更新选项菜单的视图：触发onPrepareOptionsMenu
                invalidateOptionsMenu();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showListFragment() {
        if(treasureListfragment!=null&&treasureListfragment.isAdded()){
            //讲listFrament弹出回收栈
           fragmentManager.popBackStack();
            //移除listFragment
            fragmentManager.beginTransaction().remove(treasureListfragment).commit();
            return;
        }
        treasureListfragment= new TreasureListFragment();

        // 在布局中展示(FrameLayout作为占位)
        fragmentManager.beginTransaction()
                .replace(R.id.fragment_container,treasureListfragment)
                // 添加回退栈
                .addToBackStack(null)
                .commit();

    }
    //处理返回键


    @Override
    public void onBackPressed() {

        //侧滑打开时先关闭
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
             drawerLayout.closeDrawer(GravityCompat.START);
        }else {
          //如果mapFrament是普通视图的时候就可以直接退出了
            if(mapFragment.clickBackPressed()){
                super.onBackPressed();
            }

        }

    }
}
