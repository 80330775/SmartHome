package com.qinjunyuan.smarthome.feature.modbus;

import android.content.Context;
import android.view.View;

import com.qinjunyuan.smarthome.view.MyNumberPicker;
import com.serotonin.modbus4j.ModbusMaster;
import com.serotonin.modbus4j.exception.ModbusTransportException;
import com.serotonin.modbus4j.msg.WriteRegisterRequest;


public class Picker extends AbsView {
    private final int min;
    private final int max;
    private final String unit;
    private final int divisor;
    private final int divisorFormatter;
    private final int writeOffset;
    private int writeValue;

    public Picker(Parameter parameter, int positionOfValues, int writeOffset, int min, int max, String unit) {
        this(parameter, positionOfValues, writeOffset, min, max, unit, 1, 1);
    }

    public Picker(Parameter parameter, int positionOfValues, int writeOffset, int min, int max, String unit, int divisor, int divisorFormatter) {
        super(parameter, positionOfValues);
        this.writeOffset = writeOffset;
        this.min = min;
        this.max = max;
        this.unit = unit;
        this.divisor = divisor;
        this.divisorFormatter = divisorFormatter;
    }

    public void setWriteValue(int writeValue) {
        this.writeValue = writeValue;
    }

    @Override
    public void write(ModbusMaster master) throws ModbusTransportException {
        if (master != null && master.isInitialized()) {
            master.send(new WriteRegisterRequest(parameter.getSlaveId(), writeOffset, writeValue * divisor));
        }
    }

    @Override
    public void update(View v, Context context) {
        if (v instanceof MyNumberPicker) {
            MyNumberPicker picker = (MyNumberPicker) v;
            if (!picker.isInitialized()) {
                picker.init(min, max, divisorFormatter, unit);
            }
            if (parameter.getValue(positionOfValues) != -1) {
                picker.setValue(parameter.getValue(positionOfValues) / divisor);
            }
        }
    }
}
