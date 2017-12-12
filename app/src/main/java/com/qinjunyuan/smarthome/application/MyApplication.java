package com.qinjunyuan.smarthome.application;

import android.app.Application;

import com.qinjunyuan.smarthome.util.SQLite;


public class MyApplication extends Application {
    private AppComponent appComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        appComponent = DaggerAppComponent
                .builder()
                .appModule(new AppModule(this))
                .build();
        appComponent.getSQLite().delete(SQLite.TABLE_PAGE, null, null);
        appComponent.getSQLite().delete(SQLite.TABLE_DEVICE, null, null);
    }

    public AppComponent getAppComponent() {
        return appComponent;
    }
}
