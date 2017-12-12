package com.qinjunyuan.smarthome.navigation.main;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.qinjunyuan.smarthome.R;
import com.qinjunyuan.smarthome.application.MyApplication;
import com.qinjunyuan.smarthome.feature.modbus.Parameter;
import com.qinjunyuan.smarthome.util.BaseFragment;
import com.qinjunyuan.smarthome.feature.modbus.MyArcProgressStackView;
import com.qinjunyuan.smarthome.util.SQLite;

import java.util.List;

import javax.inject.Inject;


public class MainFragment extends BaseFragment implements MainContract.View {
    private int pageId = -1;
    private MyArcProgressStackView arcView;
    private List<Parameter> parameters;

    @Inject
    MainContract.Presenter presenter;

    public static MainFragment newInstance(int pageId) {
        Bundle args = new Bundle();
        args.putInt(SQLite.PAGE_ID, pageId);
        MainFragment fragment = new MainFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        Bundle bundle = getArguments();
        if (bundle != null) {
            pageId = bundle.getInt(SQLite.PAGE_ID, -1);
        }
        DaggerMainComponent
                .builder()
                .appComponent(((MyApplication) getActivity().getApplication()).getAppComponent())
                .mainPresenterModule(new MainPresenterModule(this, pageId))
                .build()
                .inject(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.main_fragment, container, false);
        arcView = (MyArcProgressStackView) view.findViewById(R.id.arcView);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!isViewCreate && getUserVisibleHint()) {
            lazyLoading();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        arcView = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        presenter.onDestroy();
    }

    @Override
    public void setupView(List<? super MyArcProgressStackView.Model> models, List<Parameter> parameters) {
        if (models != null && parameters != null) {
//            int startColor = ContextCompat.getColor(getContext(), R.color.holo_green_light);
            int endColor = ContextCompat.getColor(getContext(), R.color.holo_red_light);
            int[] colors = {Color.GREEN, endColor};
            for (int i = 0; i < models.size(); i++) {
                MyArcProgressStackView.Model model = (MyArcProgressStackView.Model) models.get(i);
                model.setColors(colors);
//                model.setColor(startColor);
            }
            arcView.setModels(models);
            arcView.invalidate();
            this.parameters = parameters;
            isViewCreate = true;
            if (isVisible()) {
                onVisible();
            }
        }
    }

    @Override
    public void lazyLoading() {
        presenter.start();
    }

    @Override
    public void onVisible() {
        super.onVisible();
        if (getActivity() instanceof OnSetUserVisibleHintListener) {
            OnSetUserVisibleHintListener listener = (OnSetUserVisibleHintListener) getActivity();
            listener.startRead(parameters, pageId);
        }
    }

    @Override
    public void onInvisible() {
        super.onInvisible();
        if (getActivity() instanceof OnSetUserVisibleHintListener) {
            OnSetUserVisibleHintListener listener = (OnSetUserVisibleHintListener) getActivity();
            listener.stopRead(pageId);
        }
    }

    @Override
    public void updateUI() {
        super.updateUI();
        if (isVisible() && arcView != null) {
            arcView.update();
        }
    }
}
