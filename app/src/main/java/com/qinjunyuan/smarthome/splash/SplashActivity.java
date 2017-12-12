package com.qinjunyuan.smarthome.splash;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.qinjunyuan.smarthome.MyService;
import com.qinjunyuan.smarthome.Util;
import com.qinjunyuan.smarthome.navigation.MainActivity;


public class SplashActivity extends AppCompatActivity {
    static final String FIRST_CREATE = "first_create";
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null) {
                final String action = intent.getAction();
                Log.d("233", "onReceive: " + action);
                if (Util.ACTION_BROADCAST_OK.equals(action) || Util.ACTION_BROADCAST_CANCELED.equals(action)) {
                    SharedPreferences sp = getSharedPreferences(FIRST_CREATE, 0);
                    final boolean isFirstCreate = sp.getBoolean(FIRST_CREATE, true);
                    startActivity(isFirstCreate ? WelcomeActivity.class : MainActivity.class);
                }
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Util.ACTION_BROADCAST_OK);
        intentFilter.addAction(Util.ACTION_BROADCAST_CANCELED);
        registerReceiver(receiver, intentFilter);
        startService(new Intent(this, MyService.class));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }

    @Override
    public void onBackPressed() {
        stopService(new Intent(this, MyService.class));
        finish();
    }

    private void startActivity(Class c) {
        Intent intent = new Intent(this, c);
        startActivity(intent);
        finish();
    }
}
