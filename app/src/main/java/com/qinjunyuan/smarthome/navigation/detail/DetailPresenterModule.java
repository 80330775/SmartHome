package com.qinjunyuan.smarthome.navigation.detail;

import com.qinjunyuan.smarthome.data.Repository;
import com.qinjunyuan.smarthome.util.FragmentScope;

import dagger.Module;
import dagger.Provides;


@Module
class DetailPresenterModule {
    private final DetailContract.View view;
    private final int roomId;

    DetailPresenterModule(DetailContract.View view, int roomId) {
        this.view = view;
        this.roomId = roomId;
    }

    @FragmentScope
    @Provides
    DetailContract.Presenter providePresenter(Repository repository) {
        return new DetailPresenter(view, repository, roomId);
    }
}
