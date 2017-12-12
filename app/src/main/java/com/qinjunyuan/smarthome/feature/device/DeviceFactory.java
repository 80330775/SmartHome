package com.qinjunyuan.smarthome.feature.device;

import android.support.annotation.Nullable;

import com.qinjunyuan.smarthome.feature.FeatureUtil;


public class DeviceFactory {
    @Nullable
    public static Device createDevice(String type, int slaveId) {
        Device device = null;
        switch (type) {
            case FeatureUtil.RDF302:
                device = new RDF302(slaveId);
                break;
            case FeatureUtil.C5:
                device = new C5(slaveId);
                break;
        }
        return device;
    }
}
