package com.qinjunyuan.smarthome.util;

import android.content.ContentResolver;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.qinjunyuan.smarthome.R;


public final class SQLite extends SQLiteOpenHelper {
    private final String DEFAULT_URI;
    public static final String NAME = "name", IMAGE = "image", TAG = "tag";
    public static final String SLAVE_ID = "slave_id", TYPE = "type";
    public static final String PAGE_ID = "page_id";
    public static final String TABLE_PAGE = "table_page";
    public static final String TABLE_DEVICE = "table_device";

//  设置外键的格式： foreign key(外键表的参考字段) references 主键表名(主键表的被参考字段) 主键表的被参考字段应为主键
//  on delete cascade和on update cascade 是用来设置级联操作，当主键表的被参考字段发生变化时，外键表的参考字段也会发生改变（更新和删除）。

//  外键和触发器可以说是一样的，区别微乎其微，区别在http://blog.csdn.net/nokiaxjw/article/details/26161657文章底部（看不懂）。
//  外键支持在SDK版本为8以上才有

//  1. INSERT或UPDATE的on conflict子句会覆盖CREATE TABLE所定义的on conflict子句。

    public SQLite(Context context) {
        super(context, "SQL.db", null, 1);
        DEFAULT_URI = "'" + ContentResolver.SCHEME_ANDROID_RESOURCE + "://"
                + context.getResources().getResourcePackageName(R.drawable.default_room_image) + "/"
                + context.getResources().getResourceTypeName(R.drawable.default_room_image) + "/"
                + context.getResources().getResourceEntryName(R.drawable.default_room_image) + "'";
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "create table " + TABLE_PAGE
                        + "("
                        + PAGE_ID + " integer primary key autoincrement,"
                        + TAG + " text,"
                        + NAME + " text,"
                        + IMAGE + " text default " + DEFAULT_URI
                        + ")"
        );
        db.execSQL(
                "create table " + TABLE_DEVICE
                        + "("
                        + "device_id integer primary key autoincrement,"
                        + SLAVE_ID + " integer unique not null,"
                        + TYPE + " text not null,"
                        + PAGE_ID + " integer references " + TABLE_PAGE + "(" + PAGE_ID + ") on delete cascade on update cascade"
                        + ")"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists " + TABLE_PAGE);
        db.execSQL("drop table if exists " + TABLE_DEVICE);
        onCreate(db);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
//      开启外键支持
        db.execSQL("PRAGMA FOREIGN_KEYS=ON;");
    }
}
