package com.qinjunyuan.smarthome.navigation.main;

import com.qinjunyuan.smarthome.data.Repository;
import com.qinjunyuan.smarthome.util.FragmentScope;

import dagger.Module;
import dagger.Provides;


@Module
class MainPresenterModule {
    private final MainContract.View view;
    private final int pageId;

    MainPresenterModule(MainContract.View view, int pageId) {
        this.view = view;
        this.pageId = pageId;
    }

    @FragmentScope
    @Provides
    MainContract.Presenter providePresenter(Repository repository) {
        return new MainPresenter(view, repository, pageId);
    }
}
