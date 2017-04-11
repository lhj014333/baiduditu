package com.feicuiedu.treasure_20170327.net;

import com.feicuiedu.treasure_20170327.commons.user.User;
import com.google.gson.Gson;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Administrator on 2017/3/29.
 */

public class NetClient {
   private static  NetClient netClient;
    private final OkHttpClient okHttpClient;
    public  static String BASE_URL="http://admin.syfeicuiedu.com";
    private final Gson gson;
    private  TreasureApi treasureApi;
    Retrofit retrofit;


    private NetClient(){
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        okHttpClient = new OkHttpClient().newBuilder()
        .addInterceptor(interceptor)
        .build();

        gson = new Gson();

         retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();


    }
    public static NetClient getInstance(){

        if(netClient==null){
            netClient=new NetClient();
        }
        return netClient;

    }
    public Call getData(){

        Request request = new Request.Builder()
                .get()
                .url("http://www.baidu.com")
                .build();

        return okHttpClient.newCall(request);


    }
    public Call login(User user){
        RequestBody requestBody=RequestBody.create(null,gson.toJson(user));
        Request request= new Request.Builder()
                .post(requestBody)
                .url(BASE_URL+"/Handler/UserHandler.ashx?action=login")
                .build();



        return okHttpClient.newCall(request);
    }
    public TreasureApi getTreasureApi(){

        if (treasureApi==null){
            // 对请求接口的具体实现
            treasureApi = retrofit.create(TreasureApi.class);
        }
        return treasureApi;
    }

}
