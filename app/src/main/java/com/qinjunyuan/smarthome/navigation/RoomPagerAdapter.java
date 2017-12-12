package com.qinjunyuan.smarthome.navigation;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;

import com.qinjunyuan.smarthome.feature.PageInfo;
import com.qinjunyuan.smarthome.navigation.detail.DetailFragment;

import java.util.List;


class RoomPagerAdapter extends FragmentStatePagerAdapter {
    private List<PageInfo> dataList;
    private final RecyclerView.RecycledViewPool pool = new RecyclerView.RecycledViewPool();

    RoomPagerAdapter(FragmentManager fm, List<PageInfo> dataList) {
        super(fm);
        this.dataList = dataList;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        final PageInfo info = dataList.get(position);
        String name = "房间";
        if (!TextUtils.isEmpty(info.getName())) {
            name = info.getName();
        }
        return name;
    }

    @Override
    public Fragment getItem(int position) {
        final PageInfo pageInfo = dataList.get(position);
        final int pageId = pageInfo.getPageId();
        final String image = pageInfo.getImage();
        DetailFragment fragment = DetailFragment.newInstance(pageId, image);
        fragment.setPool(pool);
        return fragment;
    }

    @Override
    public int getCount() {
        return dataList.size();
    }
}
