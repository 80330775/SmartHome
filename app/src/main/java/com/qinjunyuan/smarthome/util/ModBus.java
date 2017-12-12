package com.qinjunyuan.smarthome.util;

import android.support.annotation.NonNull;

import com.serotonin.modbus4j.BatchRead;
import com.serotonin.modbus4j.ModbusFactory;
import com.serotonin.modbus4j.ModbusMaster;
import com.serotonin.modbus4j.code.DataType;
import com.serotonin.modbus4j.exception.ModbusInitException;
import com.serotonin.modbus4j.ip.IpParameters;
import com.serotonin.modbus4j.locator.BaseLocator;


public class ModBus {
    public static ModbusMaster init(@NonNull IpParameters params) throws ModbusInitException {
        ModbusFactory factory = new ModbusFactory();
        ModbusMaster master = factory.createTcpMaster(params, true);
        master.init();
        return master;
    }
}
