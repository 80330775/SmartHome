package com.qinjunyuan.smarthome.data.local;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import com.qinjunyuan.smarthome.Util;
import com.qinjunyuan.smarthome.data.DataSource;
import com.qinjunyuan.smarthome.feature.PageInfo;
import com.qinjunyuan.smarthome.feature.device.Device;
import com.qinjunyuan.smarthome.feature.device.DeviceFactory;
import com.qinjunyuan.smarthome.util.SQLite;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.schedulers.Schedulers;


public class LocalDataSource implements DataSource {
    private SQLiteDatabase db;

    public LocalDataSource(SQLiteDatabase db) {
        this.db = db;
    }

    @Override
    public Observable<PageInfo> getPageInfo() {
        return Observable.create(new ObservableOnSubscribe<PageInfo>() {
            @Override
            public void subscribe(ObservableEmitter<PageInfo> e) throws Exception {
                Cursor cursor = db.rawQuery("select * from " + SQLite.TABLE_PAGE, null);
                if (cursor.moveToFirst()) {
                    do {
                        int pageId = cursor.getInt(cursor.getColumnIndex(SQLite.PAGE_ID));
                        String tag = cursor.getString(cursor.getColumnIndex(SQLite.TAG));
                        PageInfo info = null;
                        switch (tag) {
                            case Util.PAGE_MAIN:
                                info = new PageInfo(pageId, tag);
                                break;
                            case Util.PAGE_ROOM:
                                String name = cursor.getString(cursor.getColumnIndex(SQLite.NAME));
                                String image = cursor.getString(cursor.getColumnIndex(SQLite.IMAGE));
                                info = new PageInfo(pageId, tag, name, image);
                                break;
                        }
                        if (info != null) {
                            e.onNext(info);
                        }
                    } while (cursor.moveToNext());
                }
                cursor.close();
                e.onComplete();
            }
        }).subscribeOn(Schedulers.io());
    }

    @Override
    public Observable<Device> getDevices(final int pageId) {
        return Observable.create(new ObservableOnSubscribe<Device>() {
            @Override
            public void subscribe(ObservableEmitter<Device> e) throws Exception {
                Cursor cursor = db.rawQuery("select * from " + SQLite.TABLE_DEVICE + " where " + SQLite.PAGE_ID + " = " + pageId, null);
                if (cursor.moveToFirst()) {
                    do {
                        String type = cursor.getString(cursor.getColumnIndex(SQLite.TYPE));
                        int slaveId = cursor.getInt(cursor.getColumnIndex(SQLite.SLAVE_ID));
                        Device device = DeviceFactory.createDevice(type, slaveId);
                        if (device != null) {
                            e.onNext(device);
                        }
                    } while (cursor.moveToNext());
                }
                cursor.close();
                e.onComplete();
            }
        }).subscribeOn(Schedulers.io());
    }

    @Override
    public void savePic(Uri uri, int pageId) {
        ContentValues values = new ContentValues();
        values.put(SQLite.IMAGE, uri.toString());
        Log.d("233", "savePic: " + db.update(SQLite.TABLE_PAGE, values, SQLite.PAGE_ID + " = " + pageId, null));
    }
}
