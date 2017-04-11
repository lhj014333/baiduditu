package com.feicuiedu.treasure_20170327.commons.user.register;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 2017/3/31.
 */

public class RegisterResult {


    /**
     * 注册的响应结果实体
     */


        @SerializedName("tokenid")
        private int tokenId;

        @SerializedName("errcode")
        private int code;

        @SerializedName("errmsg")
        private String msg;

        public int getTokenId() {
            return tokenId;
        }

        public int getCode() {
            return code;
        }

        public String getMsg() {
            return msg;
        }
}
