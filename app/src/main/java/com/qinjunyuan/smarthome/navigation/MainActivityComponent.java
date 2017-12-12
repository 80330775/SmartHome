package com.qinjunyuan.smarthome.navigation;

import com.qinjunyuan.smarthome.application.AppComponent;
import com.qinjunyuan.smarthome.util.FragmentScope;

import dagger.Component;


@FragmentScope
@Component(dependencies = AppComponent.class, modules = MainActivityPresenterModule.class)
interface MainActivityComponent {
    void inject(MainActivity activity);
}
