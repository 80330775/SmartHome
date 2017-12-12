package com.qinjunyuan.smarthome.feature.modbus;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;


public class Title extends AbsView {
    private final String text;

    public Title(String text) {
        this.text = text;
    }

    @Override
    public void update(View v, Context context) {
        if (v instanceof TextView) {
            TextView textView = (TextView) v;
            if (TextUtils.isEmpty(textView.getText())) {
                textView.setText(text);
            }
        }
    }
}
