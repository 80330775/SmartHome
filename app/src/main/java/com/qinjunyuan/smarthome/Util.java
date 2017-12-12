package com.qinjunyuan.smarthome;


import com.serotonin.modbus4j.code.DataType;

public final class Util {
    public static final String ACTION_BROADCAST_OK = "com.qinjunyuan.smarthome.action.broadcast.ok";
    public static final String ACTION_BROADCAST_CANCELED = "com.qinjunyuan.smarthome.action.broadcast.canceled";
    public static final String ACTION_BROADCAST_UPDATE = "com.qinjunyuan.smarthome.action.broadcast.update";

    public static final String PAGE_MAIN = "main";
    public static final String PAGE_ROOM = "room";
    public static final String PAGE_SAFE = "safe";
    public static final String PAGE_KEEP = "keep";

    private Util() {

    }

/*    public Number bytesToValueRealOffset(byte[] data, int offset, int dataType) {
        offset *= 2;
        //UNSIGNED和SIGNED区别在于返回的类型是int还是short，UNSIGNED代表没有负数，short类型装不下两个字节的无符号数
        //SWAPPED表示的是高位在前还是低位在前
        if (dataType == DataType.TWO_BYTE_INT_UNSIGNED) {
            return ((data[offset] & 0xff) << 8) | (data[offset + 1] & 0xff);
        }
        if (dataType == DataType.TWO_BYTE_INT_SIGNED) {
            return (short) (((data[offset] & 0xff) << 8) | (data[offset + 1] & 0xff));
        }
        if (dataType == DataType.TWO_BYTE_INT_UNSIGNED_SWAPPED) {
            return ((data[offset + 1] & 0xff) << 8) | (data[offset] & 0xff);
        }
        if (dataType == DataType.TWO_BYTE_INT_SIGNED_SWAPPED) {
            return (short) (((data[offset + 1] & 0xff) << 8) | (data[offset] & 0xff));
        }
        return null;
    }*/
}
