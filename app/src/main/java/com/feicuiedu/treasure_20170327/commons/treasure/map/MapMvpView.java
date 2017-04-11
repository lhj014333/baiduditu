package com.feicuiedu.treasure_20170327.commons.treasure.map;

import java.util.List;

/**
 * Created by Administrator on 2017/4/5.
 */

public interface MapMvpView {



    void showmessage(String msg);
    void setTreasureData(List<Treasure> treasures);
}
