package com.qinjunyuan.smarthome.feature.device;

import com.qinjunyuan.smarthome.feature.modbus.AbsView;
import com.qinjunyuan.smarthome.feature.modbus.Parameter;


public class Device {
    private final int slaveId;
    Parameter[] parameters;
    AbsView[] views;

    Device(int slaveId) {
        this.slaveId = slaveId;
    }

    public int getSlaveId() {
        return slaveId;
    }

    public Parameter[] getParameters() {
        return parameters;
    }

    public AbsView[] getViews() {
        return views;
    }
}
