package com.qinjunyuan.smarthome.data;

import android.net.Uri;

import com.qinjunyuan.smarthome.feature.PageInfo;
import com.qinjunyuan.smarthome.feature.device.Device;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Observable;


@Singleton
public class Repository implements DataSource {
    private DataSource local;

    @Inject
    Repository(DataSource local) {
        this.local = local;
    }

    @Override
    public Observable<PageInfo> getPageInfo() {
        return local.getPageInfo();
    }

    @Override
    public Observable<Device> getDevices(int pageId) {
        return local.getDevices(pageId);
    }

    @Override
    public void savePic(Uri uri, int pageId) {
        local.savePic(uri, pageId);
    }
}
