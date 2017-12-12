package com.qinjunyuan.smarthome.util;

import com.qinjunyuan.smarthome.R;


public enum SelectorEnum {
    HOT(R.drawable.ic_wb_sunny_grey_600_36dp, R.drawable.ic_wb_sunny_blue_36dp),
    COLD(R.drawable.ic_ac_unit_grey_600_36dp, R.drawable.ic_ac_unit_blue_36dp),
    WET(R.drawable.ic_opacity_grey_600_36dp, R.drawable.ic_opacity_blue_36dp),
    WIND(R.drawable.ic_toys_grey_600_36dp, R.drawable.ic_toys_blue_36dp),
    SWITCH(R.drawable.ic_power_settings_new_grey_600_36dp, R.drawable.ic_power_settings_new_blue_36dp),
    ALARM(R.drawable.ic_alarm_grey_600_36dp, R.drawable.ic_alarm_blue_36dp),
    AUTO(R.drawable.ic_brightness_auto_grey_600_36dp, R.drawable.ic_brightness_auto_blue_36dp),
    LOW(R.drawable.ic_signal_wifi_1_bar_grey_600_36dp, R.drawable.ic_signal_wifi_1_bar_blue_36dp),
    MID(R.drawable.ic_signal_wifi_3_bar_grey_600_36dp, R.drawable.ic_signal_wifi_3_bar_blue_36dp),
    HIGH(R.drawable.ic_signal_wifi_4_bar_grey_600_36dp, R.drawable.ic_signal_wifi_4_bar_blue_36dp),
    YELLOW(R.color.defaultTextColor, R.color.colorPrimary);

    private final int unCheckedSrc;
    private final int checkedSrc;

    SelectorEnum(int unCheckedSrc, int checkedSrc) {
        this.unCheckedSrc = unCheckedSrc;
        this.checkedSrc = checkedSrc;
    }

    public int getSrc(boolean checked) {
        return checked ? checkedSrc : unCheckedSrc;
    }
}
