package com.qinjunyuan.smarthome.navigation;

import com.qinjunyuan.smarthome.feature.PageInfo;
import com.qinjunyuan.smarthome.util.BasePresenter;
import com.qinjunyuan.smarthome.util.BaseView;

import java.util.ArrayList;


interface MainActivityContract {
    interface View extends BaseView {
        void init(ArrayList<PageInfo> pageInfoList);
    }

    interface Presenter extends BasePresenter {

    }
}
