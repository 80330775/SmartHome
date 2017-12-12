package com.qinjunyuan.smarthome.navigation.main;

import com.qinjunyuan.smarthome.application.AppComponent;
import com.qinjunyuan.smarthome.util.FragmentScope;

import dagger.Component;


@FragmentScope
@Component(dependencies = AppComponent.class, modules = MainPresenterModule.class)
interface MainComponent {
    void inject(MainFragment fragment);
}
