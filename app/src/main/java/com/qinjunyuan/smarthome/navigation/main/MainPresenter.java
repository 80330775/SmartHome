package com.qinjunyuan.smarthome.navigation.main;

import android.util.Log;

import com.qinjunyuan.smarthome.data.Repository;
import com.qinjunyuan.smarthome.feature.device.Device;

import java.util.Arrays;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;


class MainPresenter implements MainContract.Presenter {
    private final MainContract.View view;
    private final Repository repository;
    private final int pageId;
    private Disposable disposable;

    MainPresenter(MainContract.View view, Repository repository, int pageId) {
        this.view = view;
        this.repository = repository;
        this.pageId = pageId;
    }

    @Override
    public void start() {
        disposable = repository.getDevices(pageId)
                .singleOrError()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Device>() {
                    @Override
                    public void accept(@NonNull Device device) throws Exception {
                        view.setupView(Arrays.asList(device.getViews()), Arrays.asList(device.getParameters()));
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {
                        Log.d("233", "MainPresenter onFailure ");
                    }
                });
    }

    @Override
    public void onDestroy() {
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
    }
}
