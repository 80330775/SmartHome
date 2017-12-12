package com.qinjunyuan.smarthome.navigation.detail;

import com.qinjunyuan.smarthome.application.AppComponent;
import com.qinjunyuan.smarthome.util.FragmentScope;

import dagger.Component;


@FragmentScope
@Component(dependencies = AppComponent.class, modules = DetailPresenterModule.class)
interface DetailComponent {
    void inject(DetailFragment fragment);
}
