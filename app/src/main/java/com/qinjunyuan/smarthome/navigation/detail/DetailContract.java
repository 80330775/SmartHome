package com.qinjunyuan.smarthome.navigation.detail;

import android.net.Uri;

import com.qinjunyuan.smarthome.feature.modbus.Parameter;
import com.qinjunyuan.smarthome.util.BasePresenter;
import com.qinjunyuan.smarthome.view.RecyclerAdapter_Radio;

import java.util.List;


interface DetailContract {
    interface View {
        void notifyDataSetChanged();
    }

    interface Presenter extends BasePresenter {
        RecyclerAdapter_Radio initAdapter();

        List<Parameter> getParameter();

        void savePic(Uri uri);
    }
}
