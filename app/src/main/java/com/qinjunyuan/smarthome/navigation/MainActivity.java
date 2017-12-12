package com.qinjunyuan.smarthome.navigation;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import com.qinjunyuan.smarthome.MyService;
import com.qinjunyuan.smarthome.R;
import com.qinjunyuan.smarthome.Util;
import com.qinjunyuan.smarthome.application.MyApplication;
import com.qinjunyuan.smarthome.feature.PageInfo;
import com.qinjunyuan.smarthome.feature.modbus.AbsView;
import com.qinjunyuan.smarthome.feature.modbus.Parameter;
import com.qinjunyuan.smarthome.navigation.detail.DetailFragment;
import com.qinjunyuan.smarthome.navigation.main.MainFragment;
import com.qinjunyuan.smarthome.navigation.safe.SafeFragment;
import com.qinjunyuan.smarthome.util.BaseFragment;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;


public class MainActivity extends AppCompatActivity implements MainActivityContract.View, TabLayout.OnTabSelectedListener, BaseFragment.OnSetUserVisibleHintListener {
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null) {
                final String action = intent.getAction();
                if (Util.ACTION_BROADCAST_UPDATE.equals(action)) {
                    if (currentFragment != null) {
                        currentFragment.updateUI();
                    }
                }
            }
        }
    };
    private MyService.MyBinder binder;
    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d("233", "onServiceConnected: ");
            binder = (MyService.MyBinder) service;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d("233", "onServiceDisconnected: ");
        }
    };

    private TabLayout tabLayout;
    private FrameLayout viewContainer;
    private View roomContainer;
    private ViewPager roomPager;
    private RoomPagerAdapter adapter;

    private int index;
    private static final String INDEX = "index";
    private ArrayList<PageInfo> pageInfoList;
    private static final String PAGE_INFO = "page_info";

    private BaseFragment currentFragment;

    @Inject
    MainActivityContract.Presenter presenter;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(INDEX, index);
        if (pageInfoList != null && !pageInfoList.isEmpty()) {
            outState.putParcelableArrayList(PAGE_INFO, pageInfoList);
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DaggerMainActivityComponent
                .builder()
                .appComponent(((MyApplication) getApplication()).getAppComponent())
                .mainActivityPresenterModule(new MainActivityPresenterModule(this))
                .build()
                .inject(this);
        setContentView(R.layout.navigation);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Util.ACTION_BROADCAST_UPDATE);
        registerReceiver(receiver, intentFilter);
        bindService(new Intent(this, MyService.class), connection, BIND_AUTO_CREATE);

        initView();

        if (savedInstanceState != null) {
            index = savedInstanceState.getInt(INDEX);
            pageInfoList = savedInstanceState.getParcelableArrayList(PAGE_INFO);
            TabLayout.Tab tab = tabLayout.getTabAt(index);
            if (tab != null) {
                tab.select();
            }
            return;
        }
        presenter.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
        unbindService(connection);
        presenter.onDestroy();
        viewContainer = null;
        roomContainer = null;
        roomPager = null;
        adapter = null;
        currentFragment = null;
        tabLayout = null;
    }

    @Override
    public void onBackPressed() {
        stopService(new Intent(this, MyService.class));
        finish();
    }

    @Override
    public void init(ArrayList<PageInfo> pageInfoList) {
        this.pageInfoList = pageInfoList;
        TabLayout.Tab tab = tabLayout.getTabAt(index);
        if (tab != null) {
            tab.select();
        }
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        if (tab != null) {
            String tag = (String) tab.getTag();
            if (!TextUtils.isEmpty(tag)) {
                Log.d("233", "onTabSelected: " + tag);
                if (!Util.PAGE_ROOM.equals(tag)) {
                    FragmentManager fm = getSupportFragmentManager();
                    Fragment fragment = fm.findFragmentByTag(tag);
                    if (fragment == null) {
                        PageInfo mPageInfo = null;
                        for (int i = 0; i < pageInfoList.size(); i++) {
                            final PageInfo pageInfo = pageInfoList.get(i);
                            if (tag.equals(pageInfo.getTag())) {
                                mPageInfo = pageInfo;
                                break;
                            }
                        }
                        switch (tag) {
                            case Util.PAGE_MAIN:
                                fragment = mPageInfo != null ? MainFragment.newInstance(mPageInfo.getPageId()) : new MainFragment();
                                break;
//                            case Util.PAGE_SAFE:
//                                fragment = mPageInfo != null ? SafeFragment.newInstance() : new SafeFragment();
//                                break;
//                            case Util.PAGE_KEEP:
//
//                                break;
                        }
                    }
                    if (fragment != null) {
                        if (!fragment.isAdded()) {
                            fm.beginTransaction().add(R.id.fragment, fragment, tag).commit();
                        } else {
                            fm.beginTransaction().show(fragment).commit();
                        }
                        currentFragment = (BaseFragment) fragment;
                    }
                } else {
                    if (roomContainer == null) {
                        List<PageInfo> list = new ArrayList<>();
                        for (int i = 0; i < pageInfoList.size(); i++) {
                            final PageInfo pageInfo = pageInfoList.get(i);
                            if (tag.equals(pageInfo.getTag())) {
                                list.add(pageInfo);
                            }
                        }
                        if (!list.isEmpty()) {
                            roomContainer = getLayoutInflater().inflate(R.layout.room_container, viewContainer, false);
                            adapter = new RoomPagerAdapter(getSupportFragmentManager(), list);
                            roomPager = (ViewPager) roomContainer.findViewById(R.id.viewPager);
                            roomPager.setOffscreenPageLimit(list.size());
                            roomPager.setAdapter(adapter);
                            TabLayout tabLayout = (TabLayout) roomContainer.findViewById(R.id.tabLayout);
                            tabLayout.setupWithViewPager(roomPager);
                        } else {
                            roomContainer = getLayoutInflater().inflate(R.layout.nothing, viewContainer, false);
                        }
                    }
                    if (viewContainer.indexOfChild(roomContainer) == -1) {
                        viewContainer.addView(roomContainer);
                    } else {
                        if (roomContainer.getVisibility() != View.VISIBLE) {
                            roomContainer.setVisibility(View.VISIBLE);
                        }
                    }
                    if (roomPager != null) {
                        currentFragment = (BaseFragment) adapter.instantiateItem(roomPager, roomPager.getCurrentItem());
                    }
                }
                index = tabLayout.getSelectedTabPosition();
                if (currentFragment != null) {
                    currentFragment.setUserVisibleHint(true);
                }
            }
        }
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {
        String tag = (String) tab.getTag();
        if (!TextUtils.isEmpty(tag)) {
            Log.d("233", "onTabUnselected: " + tag);
            if (!Util.PAGE_ROOM.equals(tag)) {
                FragmentManager fm = getSupportFragmentManager();
                Fragment fragment = fm.findFragmentByTag(tag);
                fm.beginTransaction().hide(fragment).commit();
            } else {
                roomContainer.setVisibility(View.INVISIBLE);
            }
            if (currentFragment != null) {
                currentFragment.setUserVisibleHint(false);
            }
        }
    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {
        Log.d("233", "onTabReselected: ");
    }

    @Override
    public void startRead(List<Parameter> parameter, int pageId) {
        if (binder != null) {
            binder.startRead(parameter, pageId);
        }
    }

    @Override
    public void stopRead(int pageId) {
        if (binder != null) {
            binder.stopRead(pageId);
        }
    }

    @Override
    public void write(AbsView view) {
        if (binder != null) {
            binder.write(view);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.nav_toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.takePhoto:
                if (currentFragment instanceof DetailFragment && roomContainer.getVisibility() == View.VISIBLE) {
                    DetailFragment fragment = (DetailFragment) currentFragment;
                    fragment.openMatisseActivity();
                }
                break;
            case R.id.scan:
//                FragmentManager fm = getSupportFragmentManager();
//                Dialog_Scan scan = (Dialog_Scan) fm.findFragmentByTag(DIALOG_SCAN);
//                if (scan == null) {
//                    scan = new Dialog_Scan();
//                }
//                scan.show(fm, DIALOG_SCAN);
                break;
        }
        return true;
    }

    private void initView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
            ab.setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);
        }
        viewContainer = (FrameLayout) findViewById(R.id.fragment);
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabLayout.addTab(tabLayout.newTab().setText("首页").setTag(Util.PAGE_MAIN), false);
        tabLayout.addTab(tabLayout.newTab().setText("空调").setTag(Util.PAGE_ROOM), false);
        tabLayout.addTab(tabLayout.newTab().setText("安防"), false);//.setTag(Util.PAGE_SAFE)
        tabLayout.addTab(tabLayout.newTab().setText("保留"), false);//.setTag(Util.PAGE_KEEP)
        tabLayout.addOnTabSelectedListener(this);
    }
}
