package com.qinjunyuan.smarthome.feature;

import android.os.Parcel;
import android.os.Parcelable;


public class PageInfo implements Parcelable {
    private final int pageId;
    private final String tag;
    private final String name;
    private final String image;

    public PageInfo(int pageId, String tag) {
        this(pageId, tag, null, null);
    }

    public PageInfo(int pageId, String tag, String name, String image) {
        this.pageId = pageId;
        this.tag = tag;
        this.name = name;
        this.image = image;
    }

    public int getPageId() {
        return pageId;
    }

    public String getTag() {
        return tag;
    }

    public String getName() {
        return name;
    }

    public String getImage() {
        return image;
    }

    protected PageInfo(Parcel in) {
        pageId = in.readInt();
        tag = in.readString();
        name = in.readString();
        image = in.readString();
    }

    public static final Creator<PageInfo> CREATOR = new Creator<PageInfo>() {
        @Override
        public PageInfo createFromParcel(Parcel in) {
            return new PageInfo(in);
        }

        @Override
        public PageInfo[] newArray(int size) {
            return new PageInfo[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(pageId);
        dest.writeString(tag);
        dest.writeString(name);
        dest.writeString(image);
    }
}
