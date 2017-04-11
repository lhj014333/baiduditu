package com.feicuiedu.treasure_20170327.net;

import com.feicuiedu.treasure_20170327.commons.treasure.detail.TreasureDetail;
import com.feicuiedu.treasure_20170327.commons.treasure.detail.TreasureDetailResult;
import com.feicuiedu.treasure_20170327.commons.treasure.hide.HideTreasure;
import com.feicuiedu.treasure_20170327.commons.treasure.hide.HideTreasureResult;
import com.feicuiedu.treasure_20170327.commons.treasure.map.Area;
import com.feicuiedu.treasure_20170327.commons.treasure.map.Treasure;
import com.feicuiedu.treasure_20170327.commons.user.MultUser;
import com.feicuiedu.treasure_20170327.commons.user.User;
import com.feicuiedu.treasure_20170327.commons.user.account.UpLoadResult;
import com.feicuiedu.treasure_20170327.commons.user.account.Update;
import com.feicuiedu.treasure_20170327.commons.user.account.UpdateResult;
import com.feicuiedu.treasure_20170327.commons.user.login.LoginResult;
import com.feicuiedu.treasure_20170327.commons.user.register.RegisterResult;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

/**
 * Created by Administrator on 2017/3/31.
 */

public interface TreasureApi {




    //登录
    @POST("/Handler/UserHandler.ashx?action=login")
    Call<LoginResult> login(@Body User user);
//注册
    @POST("/Handler/UserHandler.ashx?action=register")
    Call<RegisterResult>register(@Body User user);

   //获取区域类的宝藏数据
    @POST("/Handler/TreasureHandler.ashx?action=show")
    Call<List<Treasure>>getTreasureInArea(@Body Area area);
    //宝藏详情的数据获取
    @POST("/Handler/TreasureHandler.ashx?action=tdetails")
    Call<List<TreasureDetailResult>> getTreasureDetail(@Body TreasureDetail treasureDetail);


    //埋藏宝藏的请求
    @POST("/Handler/TreasureHandler.ashx?action=hide")
    Call<HideTreasureResult>hideTreasure(@Body HideTreasure hideTreasure);


    //头像的上传
    @Multipart
    @POST("/Handler/UserLoadPicHandler1.ashx")
    Call<UpLoadResult>upload(@Part MultipartBody.Part part);
    //用户头像的更新
    @POST("/Handler/UserHandler.ashx?action=update")
    Call<UpdateResult>update(@Body Update update);
/*
//表单
    @POST("http://wx.feicuiedu.com:9094/yitao/UserWeb?method=register")
    @FormUrlEncoded
    Call<ResponseBody>getFromeData(@Field("username")String name,
                                   @Field("password")String Password);




    // 多部分：
    @POST("http://wx.feicuiedu.com:9094/yitao/UserWeb?method=update")
    @Multipart
    Call<ResponseBody> getMultData(@Part("user")MultUser user);*/

}

