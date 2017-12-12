package com.qinjunyuan.smarthome.navigation;

import com.qinjunyuan.smarthome.data.Repository;
import com.qinjunyuan.smarthome.util.FragmentScope;

import dagger.Module;
import dagger.Provides;


@Module
class MainActivityPresenterModule {
    private MainActivityContract.View view;

    MainActivityPresenterModule(MainActivityContract.View view) {
        this.view = view;
    }

    @FragmentScope
    @Provides
    MainActivityContract.Presenter providePresenter(Repository repository) {
        return new MainActivityPresenter(view, repository);
    }
}
