package com.qinjunyuan.smarthome.application;

import android.database.sqlite.SQLiteDatabase;

import com.qinjunyuan.smarthome.data.Repository;
import com.qinjunyuan.smarthome.data.search.SearchHost;

import javax.inject.Singleton;

import dagger.Component;


@Singleton
@Component(modules = AppModule.class)
public interface AppComponent {
    Repository getRepository();

    SearchHost getSearchHost();

    SQLiteDatabase getSQLite();
}
