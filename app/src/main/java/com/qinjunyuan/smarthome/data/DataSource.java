package com.qinjunyuan.smarthome.data;

import android.net.Uri;

import com.qinjunyuan.smarthome.feature.PageInfo;
import com.qinjunyuan.smarthome.feature.device.Device;

import io.reactivex.Observable;


public interface DataSource {
    Observable<PageInfo> getPageInfo();

    Observable<Device> getDevices(int pageId);

    void savePic(Uri uri, int pageId);
}
