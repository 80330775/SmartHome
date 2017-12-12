package com.qinjunyuan.smarthome.application;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.qinjunyuan.smarthome.data.DataSource;
import com.qinjunyuan.smarthome.data.search.SearchHost;
import com.qinjunyuan.smarthome.data.local.LocalDataSource;
import com.qinjunyuan.smarthome.util.SQLite;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;


@Module
class AppModule {
    private MyApplication app;

    AppModule(MyApplication app) {
        this.app = app;
    }

    @Singleton
    @Provides
    Context getContext() {
        return app.getApplicationContext();
    }

    @Singleton
    @Provides
    SQLiteDatabase getSQLite(Context context) {
        return new SQLite(context).getReadableDatabase();
    }

    @Singleton
    @Provides
    DataSource getLocalDataSource(SQLiteDatabase db) {
        return new LocalDataSource(db);
    }

    @Singleton
    @Provides
    SearchHost getSearchHost() {
        return new SearchHost();
    }
}
