package com.qinjunyuan.smarthome.util;

import android.support.v4.app.Fragment;
import android.util.Log;

import com.qinjunyuan.smarthome.feature.modbus.AbsView;
import com.qinjunyuan.smarthome.feature.modbus.Parameter;

import java.util.List;


public class BaseFragment extends Fragment {
    protected boolean isViewCreate;

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (getView() != null) {
            if (isVisibleToUser) {
                if (!isViewCreate) {
                    lazyLoading();
                    Log.d("233", "lazyLoading(): ");
                } else {
                    onVisible();
                }
            } else {
                onInvisible();
            }
        }
    }

    public void lazyLoading() {
        Log.d("233", "lazyLoading: ");
        isViewCreate = true;
    }

    public void onVisible() {
        Log.d("233", "onVisible: ");
    }

    public void onInvisible() {
        Log.d("233", "onInvisible: ");
    }

    public void updateUI() {
        Log.d("233", "updateUI: ");
    }

    public interface OnSetUserVisibleHintListener {
        void startRead(List<Parameter> parameters, int roomId);

        void stopRead(int roomId);

        void write(AbsView view);
    }
}
