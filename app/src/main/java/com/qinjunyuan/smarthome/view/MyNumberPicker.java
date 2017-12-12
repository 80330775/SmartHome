package com.qinjunyuan.smarthome.view;

import android.content.Context;
import android.util.AttributeSet;

import com.shawnlin.numberpicker.NumberPicker;


public class MyNumberPicker extends NumberPicker {
    private boolean initialized = false;

    public MyNumberPicker(Context context) {
        super(context);
    }

    public MyNumberPicker(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyNumberPicker(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void init(int min, int max, final int divisorFormatter, final String unit) {
        setMinValue(min);
        setMaxValue(max);
        setWrapSelectorWheel(false);
        setFormatter(new NumberPicker.Formatter() {
            @Override
            public String format(int value) {
                float f = (float) value / divisorFormatter;
                return f + unit;
            }
        });
        initialized = true;
    }

    public boolean isInitialized() {
        return initialized;
    }
}
