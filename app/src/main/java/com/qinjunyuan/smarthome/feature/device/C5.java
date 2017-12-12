package com.qinjunyuan.smarthome.feature.device;

import com.qinjunyuan.smarthome.feature.FeatureUtil;
import com.qinjunyuan.smarthome.feature.modbus.AbsView;
import com.qinjunyuan.smarthome.feature.modbus.MyArcProgressStackView;
import com.qinjunyuan.smarthome.feature.modbus.Parameter;


class C5 extends Device {
    C5(int slaveId) {
        super(slaveId);
        Parameter parameter = new Parameter(slaveId, FeatureUtil.HOLDING, 0, 5);
        // 放在控件中间的必须是最后一个
        views = new AbsView[]{
                new MyArcProgressStackView.Model(parameter, 4, 1000, "甲醛：", " mg/m³", 0, 500, false),
                new MyArcProgressStackView.Model(parameter, 0, 1, "PM2.5：", " ug/m³", 0, 300, false),
                new MyArcProgressStackView.Model(parameter, 3, 1, "二氧化碳：", " ppm", 0, 3000, false),
                new MyArcProgressStackView.Model(parameter, 2, 1, "环境湿度：", " %", 0, 99, false),
                new MyArcProgressStackView.Model(parameter, 1, 10, "", "℃", -100, 500, true)
        };
        parameters = new Parameter[]{parameter};
    }
}
