package com.qinjunyuan.smarthome.feature.modbus;

import android.content.Context;
import android.view.View;
import android.widget.TextView;


public class Text extends AbsView {
    private final String unit;
    private final int decimal;
    private StringBuilder builder;

    public Text(Parameter parameter, int positionOfValues, String unit, int decimal) {
        super(parameter, positionOfValues);
        this.unit = unit;
        this.decimal = decimal;
        builder = new StringBuilder();
        builder.append(unit);
    }

    @Override
    public void update(View v, Context context) {
        if (v instanceof TextView) {
            TextView textView = (TextView) v;
            if (parameter.getValue(positionOfValues) == -1) {
                builder.replace(0, builder.length() - unit.length(), "??? ");
            } else {
                if (decimal > 1) {
                    builder.replace(0, builder.length() - unit.length(), String.valueOf((float) parameter.getValue(positionOfValues) / decimal));
                } else {
                    builder.replace(0, builder.length() - unit.length(), String.valueOf(parameter.getValue(positionOfValues) / decimal));
                }
            }
            textView.setText(builder);
        }
    }
}
