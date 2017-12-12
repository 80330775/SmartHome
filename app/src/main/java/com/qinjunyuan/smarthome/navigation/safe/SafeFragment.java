package com.qinjunyuan.smarthome.navigation.safe;

import android.os.Bundle;

import com.qinjunyuan.smarthome.util.BaseFragment;


public class SafeFragment extends BaseFragment {
    public static SafeFragment newInstance() {
        Bundle args = new Bundle();

        SafeFragment fragment = new SafeFragment();
        fragment.setArguments(args);
        return fragment;
    }
}
