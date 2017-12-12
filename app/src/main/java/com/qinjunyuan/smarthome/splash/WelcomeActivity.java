package com.qinjunyuan.smarthome.splash;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.qinjunyuan.smarthome.MyService;
import com.qinjunyuan.smarthome.R;
import com.qinjunyuan.smarthome.application.MyApplication;
import com.qinjunyuan.smarthome.navigation.MainActivity;
import com.qinjunyuan.smarthome.view.ConfigUtil;
import com.qinjunyuan.smarthome.view.RecyclerAdapter_Config;

import java.util.ArrayList;
import java.util.List;

import me.relex.circleindicator.CircleIndicator;


public class WelcomeActivity extends AppCompatActivity implements RecyclerAdapter_Config.OnStartActivityListener {
    private SQLiteDatabase db;
    private TextView button;
    private RecyclerAdapter_Config adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = ((MyApplication) getApplication()).getAppComponent().getSQLite();
        setContentView(R.layout.welcome);

        List<View> viewList = new ArrayList<>();
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewPager);
        ViewGroup.LayoutParams params = viewPager.getLayoutParams();
        for (int i = 1; i <= 3; i++) {
            TextView textView = new TextView(this);
            textView.setGravity(Gravity.CENTER);
            textView.setText(String.valueOf(i));
            textView.setLayoutParams(params);
            viewList.add(textView);
        }

        View view = LayoutInflater.from(this).inflate(R.layout.welcome_selector, viewPager, false);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);
        adapter = new RecyclerAdapter_Config(recyclerView);
        adapter.setListener(this);
        recyclerView.setAdapter(adapter);
        button = (TextView) view.findViewById(R.id.button);
        viewList.add(view);

        WelcomePagerAdapter pagerAdapter = new WelcomePagerAdapter(viewList);
        viewPager.setAdapter(pagerAdapter);
        CircleIndicator indicator = (CircleIndicator) findViewById(R.id.circleIndicator);
        indicator.setViewPager(viewPager);
    }

    @Override
    public void setButtonEnabled() {
        if (!button.isEnabled()) {
            button.setTextColor(ContextCompat.getColor(this, R.color.white));
            button.setEnabled(true);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ConfigUtil.saveConfig(db, adapter.getHouseType());
                    onStartActivity();
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        stopService(new Intent(this, MyService.class));
        finish();
    }

    private void onStartActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        SharedPreferences.Editor editor = getSharedPreferences(SplashActivity.FIRST_CREATE, 0).edit();
        editor.putBoolean(SplashActivity.FIRST_CREATE, false);
        editor.apply();
        finish();
    }

    private static class WelcomePagerAdapter extends PagerAdapter {
        private List<View> viewList;

        private WelcomePagerAdapter(List<View> viewList) {
            this.viewList = viewList;
        }

        @Override
        public int getCount() {
            return viewList.size();
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(viewList.get(position));
            return viewList.get(position);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(viewList.get(position));
        }
    }
}
