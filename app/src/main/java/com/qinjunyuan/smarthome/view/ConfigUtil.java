package com.qinjunyuan.smarthome.view;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.qinjunyuan.smarthome.Util;
import com.qinjunyuan.smarthome.feature.FeatureUtil;
import com.qinjunyuan.smarthome.util.SQLite;

import java.util.ArrayList;
import java.util.List;


public class ConfigUtil {
    static final String FOUR_TWO = "四房两厅";
    static final String THREE_TWO = "三房两厅";
    static final String THREE_ONE = "三房一厅";
    static final String TWO_ONE = "两房一厅";

    public static void saveConfig(SQLiteDatabase db, String houseType) {
        List<Page> pageList = new ArrayList<>();
        pageList.add(new Page(Util.PAGE_ROOM, "主卧", new DeviceObject(FeatureUtil.RDF302, 11)));
        pageList.add(new Page(Util.PAGE_MAIN, new DeviceObject(FeatureUtil.C5, 21)));
        pageList.add(new Page(Util.PAGE_ROOM, "次卧", new DeviceObject(FeatureUtil.RDF302, 12)));
        pageList.add(new Page(Util.PAGE_ROOM, "次卧", new DeviceObject(FeatureUtil.RDF302, 13)));
        pageList.add(new Page(Util.PAGE_ROOM, "次卧", new DeviceObject(FeatureUtil.RDF302, 14)));
//        pageList.add(new Page(Util.PAGE_SAFE));
//        pageList.add(new Page(Util.PAGE_KEEP));
        ContentValues values = new ContentValues();
        for (int i = 0; i < pageList.size(); i++) {
            Page page = pageList.get(i);
            values.put(SQLite.TAG, page.tag);
            if (!TextUtils.isEmpty(page.name)) {
                values.put(SQLite.NAME, page.name);
            }
            int rowId = (int) db.insert(SQLite.TABLE_PAGE, null, values);
            Log.d("233", "insert_room: PAGE_ID = " + rowId);
            values.clear();
            if (rowId != -1) {
                for (DeviceObject deviceObject : page.deviceObjects) {
                    values.put(SQLite.TYPE, deviceObject.type);
                    values.put(SQLite.SLAVE_ID, deviceObject.slaveId);
                    values.put(SQLite.PAGE_ID, rowId);
                    int insert_device = (int) db.insert(SQLite.TABLE_DEVICE, null, values);
                    Log.d("233", "insert_device:  DEVICE_ID = " + insert_device);
                    values.clear();
                }
            }
        }
    }

    private static final class Page {
        private final String tag;
        private final String name;
        private final DeviceObject[] deviceObjects;

        private Page(String tag, @NonNull DeviceObject... deviceObjects) {
            this(tag, null, deviceObjects);
        }

        private Page(String tag, String name, @NonNull DeviceObject... deviceObjects) {
            this.tag = tag;
            this.name = name;
            this.deviceObjects = deviceObjects;
        }
    }

    private static final class DeviceObject {
        private final String type;
        private final int slaveId;

        private DeviceObject(String type, int slaveId) {
            this.type = type;
            this.slaveId = slaveId;
        }
    }
}
