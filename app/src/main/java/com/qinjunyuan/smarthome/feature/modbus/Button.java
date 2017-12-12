package com.qinjunyuan.smarthome.feature.modbus;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.qinjunyuan.smarthome.util.SelectorEnum;
import com.serotonin.modbus4j.ModbusMaster;
import com.serotonin.modbus4j.exception.ModbusTransportException;
import com.serotonin.modbus4j.msg.WriteRegisterRequest;


public class Button extends AbsView {
    private final int writeOffset;
    private final int value;

    private Button(Parameter parameter, int positionOfValues, int writeOffset, int value) {
        super(parameter, positionOfValues);
        this.writeOffset = writeOffset;
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    @Override
    public void write(ModbusMaster master) throws ModbusTransportException {
        master.send(new WriteRegisterRequest(parameter.getSlaveId(), writeOffset, value));
    }

    public static class Image extends Button {
        private final SelectorEnum selectorEnum;

        public Image(Parameter parameter, int positionOfValues, int writeOffset, int value, SelectorEnum selectorEnum) {
            super(parameter, positionOfValues, writeOffset, value);
            this.selectorEnum = selectorEnum;
        }

        @Override
        public void update(View v, Context context) {
            if (v instanceof ImageView) {
                ImageView imageView = (ImageView) v;
                imageView.setImageDrawable(ContextCompat.getDrawable(context, selectorEnum.getSrc(parameter.getValue(positionOfValues) == getValue())));
            }
        }
    }

    public static class Text extends Button {
        private final SelectorEnum textColorEnum = SelectorEnum.YELLOW;
        private final String text;

        public Text(Parameter parameter, int positionOfValues, int writeOffset, int value, String text) {
            super(parameter, positionOfValues, writeOffset, value);
            this.text = text;
        }

        @Override
        public void update(View v, Context context) {
            if (v instanceof TextView) {
                TextView textView = (TextView) v;
                if (TextUtils.isEmpty(textView.getText())) {
                    textView.setText(text);
                }
                textView.setTextColor(ContextCompat.getColor(context, textColorEnum.getSrc(parameter.getValue(positionOfValues) == getValue())));
            }
        }
    }
}
