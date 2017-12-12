package com.qinjunyuan.smarthome.feature.modbus;

import com.qinjunyuan.smarthome.feature.FeatureUtil;
import com.serotonin.modbus4j.ModbusMaster;
import com.serotonin.modbus4j.base.ModbusUtils;
import com.serotonin.modbus4j.exception.ModbusTransportException;
import com.serotonin.modbus4j.msg.ReadHoldingRegistersRequest;
import com.serotonin.modbus4j.msg.ReadHoldingRegistersResponse;
import com.serotonin.modbus4j.msg.ReadInputRegistersRequest;
import com.serotonin.modbus4j.msg.ReadInputRegistersResponse;


public class Parameter {
    private final int slaveId;
    private final int registerType;
    private final int startOffset;
    private final int numberOfRegisters;
    private short[] values;

    public Parameter(int slaveId, int registerType, int startOffset, int numberOfRegisters) {
        this.slaveId = slaveId;
        this.registerType = registerType;
        this.startOffset = startOffset;
        this.numberOfRegisters = numberOfRegisters;
    }

    public int getSlaveId() {
        return slaveId;
    }

    short getValue(int positionOfValues) {
        if (values != null && positionOfValues < values.length) {
            return values[positionOfValues];
        }
        return -1;
    }

    public void read(ModbusMaster master) throws ModbusTransportException {
        if (master != null && master.isInitialized()) {
            switch (registerType) {
                case FeatureUtil.HOLDING:
                    values = ((ReadHoldingRegistersResponse) master.send(new ReadHoldingRegistersRequest(slaveId, startOffset, numberOfRegisters))).getShortData();
                    break;
                case FeatureUtil.INPUT:
                    values = ((ReadInputRegistersResponse) master.send(new ReadInputRegistersRequest(slaveId, startOffset, numberOfRegisters))).getShortData();
                    break;
            }
        }
    }
}
