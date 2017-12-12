package com.qinjunyuan.smarthome.feature.device;

import com.qinjunyuan.smarthome.feature.FeatureUtil;
import com.qinjunyuan.smarthome.feature.modbus.AbsView;
import com.qinjunyuan.smarthome.feature.modbus.Button;
import com.qinjunyuan.smarthome.feature.modbus.Title;
import com.qinjunyuan.smarthome.feature.modbus.Parameter;
import com.qinjunyuan.smarthome.feature.modbus.Picker;
import com.qinjunyuan.smarthome.util.SelectorEnum;


class RDF302 extends Device {
    RDF302(int slaveId) {
        super(slaveId);
        Parameter parameter1 = new Parameter(slaveId, FeatureUtil.INPUT, 1000, 10);
        Parameter parameter2 = new Parameter(slaveId, FeatureUtil.HOLDING, 0, 1);
        views = new AbsView[]{
                new Picker(parameter1, 3, 102, 40, 60, "℃", 25, 2),
                new Title("设备功能"),
                new Button.Image(parameter2, 0, 0, 0, SelectorEnum.HOT),
                new Button.Image(parameter2, 0, 0, 1, SelectorEnum.COLD),
                new Title("风力"),
                new Button.Image(parameter1, 9, 101, 0, SelectorEnum.AUTO),
                new Button.Image(parameter1, 1, 101, 33, SelectorEnum.LOW),
                new Button.Image(parameter1, 1, 101, 66, SelectorEnum.MID),
                new Button.Image(parameter1, 1, 101, 100, SelectorEnum.HIGH),
                new Title("工作模式"),
                new Button.Text(parameter1, 0, 100, 1, "开机"),
                new Button.Text(parameter1, 0, 100, 3, "关机"),
                new Button.Text(parameter1, 0, 100, 4, "锁机")
        };
        parameters = new Parameter[]{parameter1, parameter2};
    }
}
