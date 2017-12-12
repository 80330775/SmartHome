package com.qinjunyuan.smarthome.feature.modbus;

import android.content.Context;
import android.view.View;

import com.serotonin.modbus4j.ModbusMaster;
import com.serotonin.modbus4j.exception.ModbusTransportException;


public class AbsView {
    Parameter parameter;
    int positionOfValues;

    public AbsView() {
    }

    public AbsView(Parameter parameter, int positionOfValues) {
        this.parameter = parameter;
        this.positionOfValues = positionOfValues;
    }

    public Parameter getParameter() {
        return parameter;
    }

    public void write(ModbusMaster master) throws ModbusTransportException {

    }

    public void update(View v, Context context) {

    }
}
