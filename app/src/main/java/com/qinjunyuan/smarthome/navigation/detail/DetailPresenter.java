package com.qinjunyuan.smarthome.navigation.detail;

import android.net.Uri;

import com.qinjunyuan.smarthome.data.Repository;
import com.qinjunyuan.smarthome.feature.device.Device;
import com.qinjunyuan.smarthome.feature.modbus.AbsView;
import com.qinjunyuan.smarthome.feature.modbus.Parameter;
import com.qinjunyuan.smarthome.view.RecyclerAdapter_Radio;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;


class DetailPresenter implements DetailContract.Presenter {
    private final DetailContract.View view;
    private final Repository repository;
    private final int pageId;
    private Disposable disposable;
    private List<AbsView> absViews = new ArrayList<>();
    private List<Parameter> parameters = new ArrayList<>();

    DetailPresenter(DetailContract.View view, Repository repository, int pageId) {
        this.view = view;
        this.repository = repository;
        this.pageId = pageId;
    }

    @Override
    public void start() {
        disposable = repository.getDevices(pageId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Device>() {
                    @Override
                    public void accept(@NonNull Device device) throws Exception {
                        Collections.addAll(absViews, device.getViews());
                        Collections.addAll(parameters, device.getParameters());
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {

                    }
                }, new Action() {
                    @Override
                    public void run() throws Exception {
                        absViews.add(0, new AbsView());
                        view.notifyDataSetChanged();
                    }
                });
    }

    @Override
    public RecyclerAdapter_Radio initAdapter() {
        return new RecyclerAdapter_Radio(absViews);
    }

    @Override
    public List<Parameter> getParameter() {
        return parameters;
    }

    @Override
    public void savePic(Uri uri) {
        repository.savePic(uri, pageId);
    }

    @Override
    public void onDestroy() {
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
    }
}
