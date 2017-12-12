package com.qinjunyuan.smarthome.navigation;

import com.qinjunyuan.smarthome.data.Repository;
import com.qinjunyuan.smarthome.feature.PageInfo;

import java.util.ArrayList;
import java.util.concurrent.Callable;

import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;


class MainActivityPresenter implements MainActivityContract.Presenter {
    private final MainActivityContract.View view;
    private final Repository repository;
    private Disposable disposable;

    MainActivityPresenter(MainActivityContract.View view, Repository repository) {
        this.view = view;
        this.repository = repository;
    }

    @Override
    public void start() {
        disposable = repository.getPageInfo()
                .toList(new Callable<ArrayList<PageInfo>>() {
                    @Override
                    public ArrayList<PageInfo> call() throws Exception {
                        return new ArrayList<>();
                    }
                })
                .subscribe(new Consumer<ArrayList<PageInfo>>() {
                    @Override
                    public void accept(@NonNull ArrayList<PageInfo> pageInfo) throws Exception {
                        view.init(pageInfo);
                    }
                });
    }

    @Override
    public void onDestroy() {
        if (disposable != null && !disposable.isDisposed())
            disposable.dispose();
    }
}
