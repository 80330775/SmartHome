package com.qinjunyuan.smarthome.navigation.main;

import com.qinjunyuan.smarthome.feature.modbus.MyArcProgressStackView;
import com.qinjunyuan.smarthome.feature.modbus.Parameter;
import com.qinjunyuan.smarthome.util.BasePresenter;
import com.qinjunyuan.smarthome.util.BaseView;

import java.util.List;


interface MainContract {
    interface View extends BaseView {
        void setupView(List<? super MyArcProgressStackView.Model> viewList, List<Parameter> parameters);
    }

    interface Presenter extends BasePresenter {

    }
}
